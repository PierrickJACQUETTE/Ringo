#include "MssgUDP.h"

bool suiteAnalyseMssgDiff(int longeur, char* mssg, char **parts , int nombreDePartie){
  if(nombreDePartie != longeur){
    lengthException(longeur, nombreDePartie, "UDP", mssg);
    return true;
  }
  return false;
}

bool suiteAnalyseMssgInf(int longeur, char* mssg, char** parts, int nombreDePartie){
  if(nombreDePartie < longeur){
    lengthException(longeur, nombreDePartie, "UDP", mssg);
    return true;
  }
  return false;
}

bool analyseMssgUDP(char* mssg, char** parts, int nombreDePartie, int size, bool isPrivate){
  bool res = false;
  if(strlen(mssg)> SIZEMSSG){
    lengthExceptionMax(strlen(mssg), mssg, "UDP");
    res = true;
  }
  if(strcmp(parts[0], "WHOS")==0 || (strcmp(parts[0], "EYBG")==0 && isPrivate == true)){
    res = suiteAnalyseMssgDiff(2, mssg, parts, nombreDePartie);
  }
  else if(strcmp(parts[0],"MEMB")==0 && isPrivate == true){
    res = suiteAnalyseMssgDiff(5, mssg, parts, nombreDePartie);
  }
  else if(strcmp(parts[0],"GBYE")==0){
    res = suiteAnalyseMssgDiff(6, mssg, parts, nombreDePartie);
  }
  else if(strcmp(parts[0],"TEST")==0){
    res = suiteAnalyseMssgDiff(4, mssg, parts, nombreDePartie);
  }
  else if(strcmp(parts[0],"APPL")==0){
    if(strcmp(parts[2], "DIFF####")!=0){
      mssgSpellCheck(mssg, "UDP");
      res = true;
    }
    else{
      res = suiteAnalyseMssgInf(5, mssg, parts, nombreDePartie);
    }
  }
  else if(strcmp(parts[0],"SUPP")==0){
    res = suiteAnalyseMssgDiff(3, mssg, parts, nombreDePartie);
  }
  else{
    mssgSpellCheck(parts[0], "UDP");
    res = true;
  }
  return res;
}

void sendUDP(char* message, Entite* entite, int anneau){
  int sock = socket(PF_INET, SOCK_DGRAM, 0);
  struct addrinfo *first_info;
  struct addrinfo hints;
  memset(&hints, 0, sizeof(struct addrinfo));
  hints.ai_family = AF_INET;
  hints.ai_socktype = SOCK_DGRAM;
  int r = getaddrinfo(convertIPV4Imcomplete(getAddrNext(entite,anneau)), getPortOutUDP(entite, anneau), &hints, &first_info);
  if(r == 0){
    if(first_info != NULL){
      struct sockaddr *saddr = first_info->ai_addr;
      sendto(sock, message, strlen(message), 0, saddr, (socklen_t)sizeof(struct sockaddr_in));
      INFO("Message envoyé : ");
      INFO(message);
      INFO("");
    }
  }
  close(sock);
}

void sendSUPP(Entite* entite, char* idm) {
  char* newIden = newIdentifiant();
  char* message = malloc(sizeof(char)*SIZEMSSG);
  sprintf(message, "%s %s %s","SUPP", newIden, idm);
  sendUDP(message, entite, 1);
}

void removeMssg(char* idm, Entite* entite, int anneau) {
  if (anneau == 1 && getIsDuplicateur(entite) == false) {
    list_removeOne(getMssgTransmisAnneau1(entite), mssg_create(idm));
    char tmp [65];
    sprintf(tmp, "%s%s%s%s", "Remove mssg with this idm : ", idm ," from this entity : ", getIdentifiant(entite));
    INFO(tmp);
  }
}

void sendAnneau(char* message, Entite* entite, char* idm) {
  sendUDP(message, entite, 1);
  List * tmp = list_add(getMssgTransmisAnneau1(entite),mssg_create(idm));
  entite = setMssgTransmisAnneau1(entite,tmp);
  if (getIsDuplicateur(entite) == true) {
    sendUDP(message, entite, 2);
    List * tmp = list_add(getMssgTransmisAnneau2(entite),mssg_create(idm));
    entite = setMssgTransmisAnneau2(entite,tmp);
  }
}

void membPrint(char** parts) {
  printf("\nDans l'anneau est present : \n");
  printf("Une entite avec cette identifiant : %s\n", parts[2]);
  printf("Qui a comme port : %s et comme addresse : %s\n\n", parts[4], parts[3]);
}

Entite* mssgWHOS(char* message, char** parts, Entite* entite){
  char* idmM = newIdentifiant();
  char* idm = parts[1];
  char* send = malloc(SIZEMSSG*sizeof(char));
  if(send == NULL ){
    fprintf(stderr,"Allocation impossible : %s\n","fonction mssgWHO : MssgUDP.c");
    return NULL;
  }
  sprintf(send, "%s %s %s %s %s", "MEMB", idmM, getIdentifiant(entite), trouveAdress(), getPortInUDP(entite));
  Mssg* m = mssg_create(idm);
  if(list_search(getMssgTransmisAnneau1(entite), m) == true){
    if(getIsDuplicateur(entite) == false){
      removeMssg(idm, entite, 1);
      sendSUPP(entite, idm);
    }
  }
  else{
    sendUDP(message, entite, 1);
    List * tmp = list_add(getMssgTransmisAnneau1(entite), mssg_create(idm));
    entite = setMssgTransmisAnneau1(entite, tmp);
    sendUDP(send, entite, 1);
    tmp = list_add(getMssgTransmisAnneau1(entite), mssg_create(idmM));
    entite = setMssgTransmisAnneau1(entite, tmp);
    char* copyMessage2 = copyStr(send);
    membPrint(split(copyMessage2, ' '));
  }
  if(getIsDuplicateur(entite) == true){
    if(list_search(getMssgTransmisAnneau2(entite), m) == true){
    }
    else{
      sendUDP(message, entite, 2);
      List * tmp = list_add(getMssgTransmisAnneau2(entite),mssg_create(idm));
      entite = setMssgTransmisAnneau2(entite,tmp);
      sendUDP(send, entite, 2);
      tmp = list_add(getMssgTransmisAnneau2(entite),mssg_create(idmM));
      entite = setMssgTransmisAnneau2(entite,tmp);
    }
  }
  return entite;
}

Entite* mssgMEMB(char* message, char** parts, Entite* entite){
  char* idm = parts[1];
  Mssg* m = mssg_create(idm);
  if(list_search(getMssgTransmisAnneau1(entite), m) == true){
    if(getIsDuplicateur(entite) == false){
      removeMssg(idm, entite, 1);
      sendSUPP(entite, idm);
    }
  }
  else{
    membPrint(parts);
    sendUDP(message, entite, 1);
    List * tmp = list_add(getMssgTransmisAnneau1(entite), mssg_create(idm));
    entite = setMssgTransmisAnneau1(entite, tmp);
  }
  if(getIsDuplicateur(entite) == true){
    if(list_search(getMssgTransmisAnneau2(entite), m) == true){
    }
    else{
      sendUDP(message, entite, 2);
      List * tmp = list_add(getMssgTransmisAnneau2(entite), mssg_create(idm));
      entite = setMssgTransmisAnneau2(entite, tmp);
    }
  }
  return entite;
}

Entite* mssgGBYEsuite(Entite* entite, char ** parts, int anneau){
  Entite* tmp = entite_create();
  tmp = setAddrNext(tmp, getAddrNext(entite, anneau), anneau);
  tmp = setPortOutUDP(tmp, getPortOutUDP(entite, anneau), anneau);
  entite = setAddrNext(entite, parts[4], anneau);
  entite = setPortOutUDP(entite, parts[5], anneau);
  char* idmNew = newIdentifiant();
  char * message = malloc(sizeof(char)*14);
  sprintf(message, "%s %s", "EYBG", idmNew);
  sendUDP(message, tmp, anneau);
  if(anneau == 1){
    List* tmp = list_add(getMssgTransmisAnneau1(entite), mssg_create(idmNew));
    entite = setMssgTransmisAnneau1(entite, tmp);
  }
  else if (anneau == 2){
    List * tmp = list_add(getMssgTransmisAnneau2(entite), mssg_create(idmNew));
    entite = setMssgTransmisAnneau2(entite, tmp);
  }
  return entite;
}

Entite* mssgGBYE(char* message, char** parts, Entite* entite){
  char* idm = parts[1];
  int anneau = 1;
  Mssg* m = mssg_create(idm);
  if(list_search(getMssgTransmisAnneau1(entite), m) == true){
    if(getIsDuplicateur(entite) == false){
      removeMssg(idm, entite, anneau);
      sendSUPP(entite, idm);
    }
  }
  else if((strcmp(trouveAdress(), parts[2]) == 0) && (strcmp(getPortOutUDP(entite, anneau), parts[3]) == 0 ) ){
    entite = mssgGBYEsuite(entite, parts, anneau);
  }
  else{
    sendUDP(message, entite, anneau);
    List * tmp = list_add(getMssgTransmisAnneau1(entite), mssg_create(idm));
    entite = setMssgTransmisAnneau1(entite, tmp);
  }
  if(getIsDuplicateur(entite) ==  true){
    anneau =2;
    if(list_search(getMssgTransmisAnneau2(entite), m) == true){
    }
    else if( (strcmp(trouveAdress(), parts[2]) == 0) && (strcmp(getPortOutUDP(entite, anneau), parts[3]) == 0) ){
      entite = mssgGBYEsuite(entite, parts, anneau);
    }
    else{
      sendUDP(message, entite, anneau);
      List * tmp = list_add(getMssgTransmisAnneau2(entite), mssg_create(idm));
      entite = setMssgTransmisAnneau2(entite, tmp);
    }
  }
  return entite;
}

Entite* mssgEYBG(char* message, char** parts, Entite* entite){
  if(getIsDuplicateur(entite) == false){
    exit(0);
  }
  if( getIsDuplicateur(entite) == true){
    if( getAlreadyReceivedEYBG(entite) == true){
      exit(0);
    }
    else{
      entite = setAlreadyReceivedEYBG(entite, true);
    }
  }
  return entite;
}

Entite* mssgTEST(char* message, char** parts, Entite* entite){
  char* idm = parts[1];
  Mssg* m = mssg_create(idm);
  bool one = false;
  int anneau = 1;
  if(list_search(getMssgTransmisAnneau1(entite), m) == true){
    List* tmp = list_changeTest(getMssgTransmisAnneau1(entite), m);
    entite = setMssgTransmisAnneau1(entite, tmp);
    if( getIsDuplicateur(entite) == false){
      removeMssg(idm, entite, anneau);
      sendSUPP(entite, idm);
    }
    one = true;
    printf("\nL anneau est en parfait etat\n\n");
  }
  else if( (strcmp(parts[2], getAddrMultiDiff(entite, anneau)) != 0) && (strcmp(parts[3], getPortMultiDiff(entite, anneau))!=0) ){
    printf("J'ai recu un message TEST mais ils n'appartient pas a cet anneau\n");
  }
  else{
    sendUDP(message, entite, anneau);
    List * tmp = list_add(getMssgTransmisAnneau1(entite), mssg_create(idm));
    entite = setMssgTransmisAnneau1(entite, tmp);
  }
  if(getIsDuplicateur(entite) == true){
    anneau = 2;
    if( (list_search(getMssgTransmisAnneau2(entite), m) == true) && (one == false)){
      List* tmp = list_changeTest(getMssgTransmisAnneau2(entite), m);
      entite = setMssgTransmisAnneau2(entite, tmp);
      printf("\nL anneau est en parfait etat\n\n");
    }
    else if( (strcmp(parts[2], getAddrMultiDiff(entite, anneau)) != 0) && (strcmp(parts[3], getPortMultiDiff(entite, anneau))!=0) ){
      printf("J'ai recu un message TEST mais ils n'appartient pas a cet anneau\n");
    }
    else{
      sendUDP(message, entite, anneau);
      List * tmp = list_add(getMssgTransmisAnneau2(entite), mssg_create(idm));
      entite = setMssgTransmisAnneau2(entite, tmp);
    }
  }
  return entite;
}

Entite* mssgSUPP(char* message, char** parts, Entite* entite){
  char* idm = parts[2];
  Mssg* m = mssg_create(idm);
  if(list_search(getMssgTransmisAnneau1(entite), m) == true){
    if(getIsDuplicateur(entite) == false){
      removeMssg(idm, entite, 1);
    }
    sendUDP(message, entite, 1);
  }
  if(getIsDuplicateur(entite) == true){
    if(list_search(getMssgTransmisAnneau2(entite), m) == true){
      sendUDP(message, entite, 2);
    }
  }
  return entite;
}

char* corpsDuMssgAPPL(char* message, int debut){
  char* copyMessage = copyStr(message);
  char** parts = split(copyMessage, ' ');
  int debutReel = 0;
  int i;
  for(i = 0; i < debut; i++){
    debutReel += strlen(parts[i])+1;
  }
  int size = strlen(message)-debutReel;
  char* res = malloc(sizeof(char)*size);
  for(i = 0; i < size; i++){
    res[i] = message[i + debutReel];
  }
  return res;
}


Entite* mssgAPPL(char* message, char** parts, Entite* entite){
  char* idm = parts[1];
  if(strcmp(parts[2], "DIFF####") == 0){
    char* messageDIFF = corpsDuMssgAPPL(message, 4);
    printf("\n J'ai recu le mssgAPPL est le message est : \n");
    printf("%s\n", messageDIFF);

    Mssg* m = mssg_create(idm);
    if(list_search(getMssgTransmisAnneau1(entite), m) == true){
      if(getIsDuplicateur(entite) == false){
        removeMssg(idm, entite, 1);
        sendSUPP(entite, idm);
      }
    }
    else{
      sendUDP(message, entite, 1);
      List * tmp = list_add(getMssgTransmisAnneau1(entite), mssg_create(idm));
      entite = setMssgTransmisAnneau1(entite, tmp);
    }
    if(getIsDuplicateur(entite) == true){
      if(list_search(getMssgTransmisAnneau2(entite), m) == true){
      }
      else{
        sendUDP(message, entite, 2);
        List * tmp = list_add(getMssgTransmisAnneau2(entite), mssg_create(idm));
        entite = setMssgTransmisAnneau2(entite, tmp);
      }
    }
  }
  else{
    fprintf(stderr,"Erreur dans appl UDP recu non reconnue\n");
  }
  return entite;
}

Entite* receiveUDP(Entite* entite, int sock) {
  char *tampon = malloc(sizeof(char)*SIZEMSSG);
  int rec=recv(sock,tampon,SIZEMSSG,0);
  tampon[rec]='\0';
  INFO("Message UDP recu : ");
  INFO(tampon);
  INFO("");
  int size = strlen(tampon);
  char* copyMessage = copyStr(tampon);
  char* copyMessage2 = copyStr(tampon);
  char* copyMessage3 = copyStr(tampon);
  int nombreDePartie = sizePart(copyMessage, " ");
  char** parts = split(copyMessage3, ' ');
  bool erreur = analyseMssgUDP(copyMessage2, parts, nombreDePartie, size, true);
  if(erreur != true){
    if (strcmp(parts[0], "WHOS")==0) {
      mssgWHOS(tampon, parts, entite);
    } else if (strcmp(parts[0], "MEMB")==0) {
      mssgMEMB(tampon, parts, entite);
    } else if (strcmp(parts[0], "GBYE")==0) {
      mssgGBYE(tampon, parts, entite);
    } else if (strcmp(parts[0], "EYBG")==0) {
      mssgEYBG(tampon, parts, entite);
    } else if (strcmp(parts[0], "TEST")==0) {
      mssgTEST(tampon, parts, entite);
    } else if (strcmp(parts[0], "SUPP")==0) {
      mssgSUPP(tampon, parts, entite);
    } else if (strcmp(parts[0], "APPL")==0) {
      mssgAPPL(tampon, parts, entite);
    }
  }
  return entite;
}
