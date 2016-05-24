#include "MssgTCP.h"

void sendTCP(char* mess, int sock){
  write(sock, mess, strlen(mess));
  char tmp[SIZEMSSG+11];
  sprintf(tmp, "%s %s","Envoi de :",mess);
  INFO(tmp);
}

char* receiveTCP(int sock){
  char* buff= malloc(SIZEMSSG * sizeof(char));
  if(buff == NULL ){
    fprintf(stderr,"Allocation impossible : %s\n","fonction receiveTCP : MssgTCP.c");
    return NULL;
  }
  int recu=read(sock, buff, (SIZEMSSG-1) * sizeof(char));
  if(recu >= 0){
    buff[recu] = '\0';
    char tmp[SIZEMSSG+14];
    sprintf(tmp, "%s %s","Message recu :",buff);
    INFO(tmp);
  }
  return buff;
}

bool suiteAnalyseMssg(int longeur, char* mssg, char** parts, int sizeParts){
  bool res = false;
  if (sizeParts != longeur) {
    lengthException(longeur, sizeParts, "TCP", mssg);
    res = true;
  }
  int sizeMssg = strlen(parts[sizeParts-1]);
  char lastMssg = parts[sizeParts-1][sizeMssg-1];
  if (lastMssg != '\n') {
    notSDLException(mssg, "TCP");
    res = true;
  }
  return res;
}

bool analyseMssg(char * mssg, char** parts, int nombreDePartie ) {
  bool res = false;
  int sizeMssg = strlen(mssg);
  if (sizeMssg > SIZEMSSG) {
    lengthExceptionMax(sizeMssg, mssg, "TCP");
    res = true;
  }
  if (strcmp(parts[0],"WELC")==0 || strcmp(parts[0],"DUPL")==0) {
    res = suiteAnalyseMssg(5, mssg, parts,nombreDePartie);
  } else if (strcmp(parts[0],"ACKC\n")==0 || strcmp(parts[0],"NOTC\n")==0) {
    res = suiteAnalyseMssg(1, mssg, parts,nombreDePartie);
  } else if (strcmp(parts[0],"ACKD")==0) {
    res = suiteAnalyseMssg(2, mssg, parts,nombreDePartie);
  } else if (strcmp(parts[0],"NEWC")==0) {
    res = suiteAnalyseMssg(3, mssg, parts,nombreDePartie);
  } else {
    mssgSpellCheck(parts[0], "TCP");
    res = true;
  }
  return res;
}

void print(bool erreur, Entite* entite) {
  if (erreur == true) {
    INFO("Erreur lors de l'insertion donc pas d'insertion -- entite inchange\n");
  } else {
    INFO(entite_print_simple(entite));
  }
}

Entite* insertNouveauTCP(Entite* entite, bool joindre) {
  bool res = false;
  struct sockaddr_in *adress_sock;
  struct addrinfo *first_info;
  struct addrinfo hints;
  bzero(&hints,sizeof(struct addrinfo));
  hints.ai_family = AF_INET;
  hints.ai_socktype = SOCK_STREAM;
  int r2 = getaddrinfo(convertIPV4Imcomplete(getAddrNext(entite,1)),getPortTCPOut(entite),&hints,&first_info);
  if(r2 == -1){
    fprintf(stderr,"pb InsertNOUVEAU TCP\n");
  }
  int sock = socket(PF_INET, SOCK_STREAM, 0);
  if(first_info != NULL){
    adress_sock = (struct sockaddr_in *)first_info->ai_addr;
    int r = connect(sock,(struct sockaddr *)adress_sock, (socklen_t)sizeof(struct sockaddr_in));
    if(r != 1){
      char * message = receiveTCP(sock);
      char* copyMessage = copyStr(message);
      char* copyMessage2 = copyStr(message);
      int nombreDePartie = sizePart(copyMessage, " ");
      char** parts = split(message, ' ');
      res = analyseMssg(copyMessage2, parts, nombreDePartie);
      parts = substringLast(parts, nombreDePartie);
      if(res == true){
        close(sock);
        exit(EXIT_FAILURE);
      }
      if (strcmp(parts[0], "WELC")==0) {
        if (joindre == true) {
          entite = setAddrNext(entite, parts[1], 1);
          entite = setPortOutUDP(entite, parts[2], 1);
          entite = setAddrMultiDiff(entite, parts[3], 1);
          entite = setPortMultiDiff(entite, parts[4], 1);
        }
        char* send = malloc(SIZEMSSG*sizeof(char));
        if(send == NULL ){
          fprintf(stderr,"Allocation impossible : %s\n","fonction insertNouveauTCP : MssgTCP.c");
          return NULL;
        }
        if (joindre == true) {
          sprintf(send, "%s%s %s\n", "NEWC ", trouveAdress(true), getPortInUDP(entite));
        } else {
          sprintf(send, "%s%s %s %s %s\n", "DUPL " , trouveAdress(true) , getPortInUDP(entite) , getAddrMultiDiff(entite,1) , getPortMultiDiff(entite, 1));
        }
        copyMessage = copyStr(send);
        copyMessage2 = copyStr(send);
        char* copyMessage3 = copyStr(send);
        nombreDePartie = sizePart(copyMessage, " ");
        parts = split(copyMessage3, ' ');
        analyseMssg(copyMessage2, parts, nombreDePartie);
        sendTCP(send, sock);
        free(send);

        // ACKC ou ACKD
        message = receiveTCP(sock);
        copyMessage = copyStr(message);
        copyMessage2 = copyStr(message);
        nombreDePartie = sizePart(copyMessage, " ");
        parts = split(message, ' ');
        res = analyseMssg(copyMessage2, parts, nombreDePartie);
        parts = substringLast(parts, nombreDePartie);

        if (joindre == false) {
          entite = setPortOutUDP(entite, parts[1], 1);
        }

        close(sock);
        INFO("Fin de connection TCP");
        print(false, entite);
        free(copyMessage3);
      } else {
        printf("Entite ou se connecter est deja un doubleur ");
        close(sock);
        INFO("Fin de connection TCP");
        print(true, entite);
        exit(EXIT_FAILURE);
      }
      free(parts);
      free(message);
      free(copyMessage);
      free(copyMessage2);
    }
  }
  return entite;
}

Entite* insertAnneauTCP(Entite* entite, int sock) {
  INFO("Evenement sur TCP\n");
  int sock2;
  struct sockaddr_in caller;
  socklen_t size = sizeof(caller);
  if ((sock2 = accept(sock,(struct sockaddr *)&caller,&size))==-1) {
    fprintf(stderr,"accept problem");
    close(sock);
    exit(EXIT_FAILURE);
  }
  if(sock2 >= 0){
    INFO("Acceptation TCP\n");
    char* send = malloc(SIZEMSSG*(sizeof(char)));
    if(send == NULL ){
      fprintf(stderr,"Allocation impossible : %s\n","fonction insertAnneauTCP : send : MssgTCP.c");
      return NULL;
    }
    if (getIsDuplicateur(entite) == false) {
      // WELC
      sprintf(send,"%s%s %s %s %s\n","WELC ",trouveAdress(), getPortOutUDP(entite,1), getAddrMultiDiff(entite,1), getPortMultiDiff(entite,1));
    } else {
      // NOTC
      sprintf(send, "%s\n", "NOTC");
    }
    char* copyMessage = copyStr(send);
    char* copyMessage2 = copyStr(send);
    char* copyMessage3 = copyStr(send);
    int nombreDePartie = sizePart(copyMessage, " ");
    char** parts = split(copyMessage3, ' ');
    analyseMssg(copyMessage2, parts, nombreDePartie);
    sendTCP(send, sock2);
    free(send);
    if (getIsDuplicateur(entite) == false) {
      // NEWC ou DUPL
      char* message = receiveTCP(sock2);
      copyMessage = copyStr(message);
      copyMessage2 = copyStr(message);
      nombreDePartie = sizePart(copyMessage, " ");
      parts = split(message, ' ');
      analyseMssg(copyMessage2, parts, nombreDePartie);
      parts = substringLast(parts, nombreDePartie);
      char* futurAddrUDPOut = parts[1];
      char* futurPortUDPOut = parts[2];
      int anneau = 1;
      char* futurMultiDiffAddr = malloc(16*sizeof(char));
      if(futurMultiDiffAddr == NULL ){
        fprintf(stderr,"Allocation impossible : %s\n","fonction insertAnneauTCP : futurMultiDiffAddr : MssgTCP.c");
        return NULL;
      }
      char* futurMultiDiffPort = malloc(5*sizeof(char));
      if(futurMultiDiffPort == NULL ){
        fprintf(stderr,"Allocation impossible : %s\n","fonction insertAnneauTCP : futurMultiDiffPort : MssgTCP.c");
        return NULL;
      }
      char* envoi = malloc(SIZEMSSG*sizeof(char));
      if(envoi == NULL ){
        fprintf(stderr,"Allocation impossible : %s\n","fonction insertAnneauTCP : envoi : MssgTCP.c");
        return NULL;
      }
      bool demandeDupplication = false;
      if (strcmp(parts[0], "NEWC")==0) {
        envoi = "ACKC\n";
      } else if (strcmp(parts[0], "DUPL")==0) {
        anneau = 2;
        futurMultiDiffAddr = parts[3];
        futurMultiDiffPort = parts[4];
        sprintf(envoi, "%s%s\n", "ACKD ", getPortInUDP(entite));
        demandeDupplication = true;
      }
      copyMessage = copyStr(envoi);
      copyMessage2 = copyStr(envoi);
      copyMessage3 = copyStr(envoi);
      nombreDePartie = sizePart(copyMessage, " ");
      parts = split(copyMessage3, ' ');
      analyseMssg(copyMessage2, parts, nombreDePartie);
      sendTCP(envoi, sock2);
      entite = setAddrNext(entite, futurAddrUDPOut, anneau);
      entite = setPortOutUDP(entite, futurPortUDPOut, anneau);
      if (demandeDupplication == true) {
        entite = setAddrMultiDiff(entite, futurMultiDiffAddr, anneau);
        entite = setPortMultiDiff(entite, futurMultiDiffPort, anneau);
      }

      close(sock2);
      INFO("Fin de connection TCP");
      entite = setIsDuplicateur(entite, demandeDupplication);
      print(false, entite);
      free(send);
      free(futurAddrUDPOut);
    } else {
      close(sock2);
      INFO("Fin de connection TCP");
      print(true, entite);
    }
    free(parts);
    free(copyMessage);
    free(copyMessage2);
    free(copyMessage3);
  }
  return entite;
}
