#include "VerifEntree.h"

bool testPortInUDP(int port){
  int sock=socket(PF_INET, SOCK_DGRAM, 0);
  struct sockaddr_in address_sock;
  address_sock.sin_family = AF_INET;
  address_sock.sin_port = htons(port);
  address_sock.sin_addr.s_addr = htonl(INADDR_ANY);
  int r = bind(sock, (struct sockaddr *)&address_sock, sizeof(struct sockaddr_in));
  close(sock);
  return (r == 0)? true : false;
}

bool testPortInTCP(int port) {
  int sock=socket(PF_INET, SOCK_STREAM, 0);
  struct sockaddr_in address_sock;
  address_sock.sin_family = AF_INET;
  address_sock.sin_port = htons(port);
  address_sock.sin_addr.s_addr = htonl(INADDR_ANY);
  int r = bind(sock,(struct sockaddr *)&address_sock, sizeof(struct sockaddr_in));
  if(r == 0){
    r = listen(sock, 0);
    if(r == 0){
      close(sock);
      return true;
    }
  }
  close(sock);
  return false;
}

Entite* initMultiDiff(Entite* entite,int anneau){

  // char* reponse = malloc(SIZEMSSG*sizeof(char));
  // if(reponse == NULL ){
  //   fprintf(stderr,"Allocation impossible : %s\n","fonction initMultiDiff : VerifEntree.c");
  //   return NULL;
  // }
  // bool correct = false;
  // while( !correct){
  //   printf("Veuillez entrer le numero du port de multi-diff:\n");
  //   reponse = saisir_chaine();
  //   correct = verifNombre(reponse, true);
  // }
  // entite = setPortMultiDiff(entite, reponse, anneau);
  // correct = false;
  // while ( !correct) {
  //   printf("Veuillez entrer l'adresse multi-diff : \n");
  //   reponse = saisir_chaine();
  //   correct = verifAddress(reponse);
  // }
  // entite = setAddrMultiDiff(entite, convertIPV4Complete(reponse), anneau);
  // free(reponse);

  return entite;
}

Entite* initEntite(Entite* entite) {

  // bool correct = false;
  // char* reponse = malloc(SIZEMSSG*sizeof(char));
  // if(reponse == NULL ){
  //   fprintf(stderr,"Allocation impossible : %s\n","fonction initEntite : VerifEntree.c");
  //   return NULL;
  // }
  // while ( !correct) {
  //   printf("Veuillez entrer son numero du port UDP : \n");
  //   reponse = saisir_chaine();
  //   correct = verifNombre(reponse, true);
  //   if (correct == true) {
  //     correct = testPortInUDP(entier(reponse));
  //   }
  // }
  // entite = setPortInUDP(entite, reponse);
  // entite = setPortOutUDP(entite, reponse, 1);
  // correct = false;
  // while ( !correct) {
  //   printf("Veuillez entrer son numero du port TCP : \n");
  //   reponse = saisir_chaine();
  //   correct = verifNombre(reponse, false);
  //   if (correct == true) {
  //     correct = testPortInTCP(entier(reponse));
  //   }
  // }
  // entite = setPortTCPIn(entite, reponse);

  entite = setIdentifiant(entite, identifiantEntite(getPortTCPIn(entite)));
  return entite;
}

Entite* nouveauAnneau(Entite* entite){
  entite = initEntite(entite);
  entite = setAddrNext(entite, trouveAdress(), 1);
  entite = initMultiDiff(entite, 1);
  entite_print_new(entite);
  return entite;
}

Entite* rejoindreAnneau(Entite* entite, bool joindre) {
  entite = initEntite(entite);

  // char* reponse = malloc(SIZEMSSG*sizeof(char));
  // if(reponse == NULL ){
  //   fprintf(stderr,"Allocation impossible : %s\n","fonction rejoindreAnneau : VerifEntree.c");
  //   return NULL;
  // }
  // bool correct = false;
  // while ( !correct) {
  //   printf("Veuillez entrer l'adresse Ip de l'entité ou se connecter: \n");
  //   reponse = saisir_chaine();
  //   correct = verifAddress(reponse);
  // }
  // entite = setAddrNext(entite, convertIPV4Complete(reponse), 1);
  // correct = false;
  // while ( !correct) {
  //   printf("Veuillez entrer le port TCP de l'entité ou se connecter: \n");
  //   reponse = saisir_chaine(reponse);
  //   correct = verifNombre(reponse, false);
  // }
  // entite = setPortTCPOut(entite, reponse);

  if (joindre == false) {
    entite = initMultiDiff(entite, 2);
  }
  entite_print_new(entite);
  return entite;
}

Entite* choixEntite(Entite* entite){
  char* reponse = malloc(512*sizeof(char));
  if(reponse == NULL ){
    fprintf(stderr,"Allocation impossible : %s\n","fonction choixEntite : VerifEntree.c");
    return NULL;
  }
  bool correctAction = false;
  while( !correctAction){
    printf("Voulez un Nouveau anneau ou Joindre ou Duppliquer un anneau existant ?\n");
    printf("Taper N ou J ou D\n");
    reponse = saisir_chaine();
    if(strcmp(reponse, "N")==0){
      correctAction = true;

      entite = setPortMultiDiff(entite,"7003", 1);
      entite = setAddrMultiDiff(entite,"238.255.000.003", 1);
      entite = setPortInUDP(entite,"7001");
      entite = setPortOutUDP(entite,"7001", 1);
      entite = setPortTCPIn(entite,"7000");

      entite = nouveauAnneau(entite);
    }
    else if (strcmp(reponse,"J")==0 || strcmp(reponse,"D")==0) {
      correctAction = true;
      bool joindre = false;
      if (strcmp(reponse,"J")==0) {
        joindre = true;
      }

      printf("Voulez un A ou Z ou E?\n");
      reponse = saisir_chaine();
      if (strcmp(reponse,"A")==0) {
        entite = setAddrNext(entite, "192.168.001.041", 1);
        entite = setPortTCPOut(entite, "7000");
        entite = setPortTCPIn(entite, "6999");
        entite = setPortInUDP(entite, "7002");
        entite = setPortOutUDP(entite, "7005", 1);
        if (joindre == false) {
          entite = setPortMultiDiff(entite, "7007", 1);
          entite = setAddrMultiDiff(entite, "238.255.000.005", 1);
        }
      } else if (strcmp(reponse,"Z")==0) {
        entite = setAddrNext(entite, "127.000.000.006", 1);
        entite = setPortTCPOut(entite, "7000");
        entite = setPortTCPIn(entite, "7008");
        entite = setPortInUDP(entite, "6001");
        entite = setPortOutUDP(entite, "6002", 1);
        if (joindre == false) {
          entite = setPortMultiDiff(entite, "7007", 1);
          entite = setAddrMultiDiff(entite, "238.255.000.004", 1);
        }

      } else {
        entite = setAddrNext(entite, "127.000.000.006", 1);
        entite = setPortTCPOut(entite, "6999");
        entite = setPortTCPIn(entite, "7009");
        entite = setPortInUDP(entite, "6005");
        entite = setPortOutUDP(entite, "6009", 1);
      }

      entite = rejoindreAnneau(entite, joindre);
      entite = insertNouveauTCP(entite, joindre);
    } else {
      printf("Erreur de frappe, recommencez\n");
    }
  }
  free(reponse);
  return entite;
}
