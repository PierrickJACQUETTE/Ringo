#include "MssgMultiDiff.h"

Entite* modifEntite(Entite* entite, int anneau){
  char* addrNext = malloc(sizeof(char)*16);
  char* addrMulti = malloc(sizeof(char)*16);
  char* portOutUDP = malloc(sizeof(char)*4);
  char* portMulti = malloc(sizeof(char)*4);
  if(addrNext == NULL ){
    fprintf(stderr,"Allocation impossible : %s\n","fonction modifEntite : addrNext : MssgMultiDiff.c");
    return NULL;
  }
  if(addrMulti == NULL ){
    fprintf(stderr,"Allocation impossible : %s\n","fonction modifEntite : addrMulti : MssgMultiDiff.c");
    return NULL;
  }
  if(portOutUDP == NULL ){
    fprintf(stderr,"Allocation impossible : %s\n","fonction modifEntite : portOutUDP : MssgMultiDiff.c");
    return NULL;
  }
  if(portMulti == NULL ){
    fprintf(stderr,"Allocation impossible : %s\n","fonction modifEntite : portMulti : MssgMultiDiff.c");
    return NULL;
  }
  if(anneau == 1){
    addrNext = getAddrNext(entite, 2);
    addrMulti = getAddrMultiDiff(entite, 2);
    portOutUDP = getPortOutUDP(entite, 2);
    portMulti = getPortMultiDiff(entite, 2);
    entite = setMssgTransmisAnneau1(entite, getMssgTransmisAnneau2(entite));
  }
  else{
    addrNext = NULL;
    addrMulti = NULL;
    portOutUDP = NULL;
    portMulti = NULL;
  }
  entite = setAddrNext(entite, addrNext, anneau);
  entite = setPortOutUDP(entite, portOutUDP, anneau);
  entite = setAddrMultiDiff(entite, addrMulti, anneau);
  entite = setPortMultiDiff(entite, portMulti, anneau);
  entite = setMssgTransmisAnneau2(entite, list_create(NULL));
  return entite;

}

Entite* sendMutiDiff(Entite* entite, int anneau){
  int sock = socket(PF_INET,SOCK_DGRAM,0);
  struct addrinfo *first_info;
  struct addrinfo hints;
  memset(&hints, 0, sizeof(struct addrinfo));
  hints.ai_family = AF_INET;
  hints.ai_socktype = SOCK_DGRAM;
  int r = getaddrinfo(getAddrMultiDiff(entite, anneau),getPortMultiDiff(entite, anneau), &hints, &first_info);
  if(r == 0){
    if(first_info != NULL){
      struct sockaddr *saddr = first_info->ai_addr;
      char tampon[5];
      strcpy(tampon,"DOWN");
      sendto(sock, tampon, strlen(tampon), 0, saddr, (socklen_t)sizeof(struct sockaddr_in));
      INFO("Message envoyé : ");
      INFO(tampon);
      INFO("");
    }
  }
  return entite;
}

bool IsDeclenche(Entite* entite, int anneau){
  List* tmp = NULL;
  if(anneau == 1){
    tmp = getMssgTransmisAnneau1(entite);
  }
  else if(anneau == 2){
    tmp = getMssgTransmisAnneau2(entite);
  }
  while (tmp){
    if(tmp->data !=NULL){
      if( (((timeReel()) - tmp->data->my_time) > TIMEMAX) && (tmp->data->isTest == true) ){
        return true;
      }
    }
    tmp = tmp->next;
  }
  return false;
}

Entite* declencheMultiDiff(Entite* entite, int anneau){
  if(IsDeclenche(entite, anneau) == true){
    sendMutiDiff(entite, anneau);
  }
  return entite;
}

Entite* receiveMultiDiff(Entite* entite, int sock, int anneau){
  char* tampon = malloc(sizeof(char)*SIZEMSSG);
  int rec = recv(sock,tampon,SIZEMSSG,0);
  tampon[rec] = '\0';
  INFO("Message MULTIDIFF recu : ");
  INFO(tampon);
  INFO("");
  char* copyMessage3 = copyStr(tampon);
  char** parts = split(copyMessage3, ' ');
  if(rec > SIZEMSSG || rec == -1){
    lengthExceptionMax(rec, tampon, "MULTIDIFF");
  }
  else if(strcmp(parts[0],"DOWN")==0){
    if(getIsDuplicateur(entite) == false){
      exit(0);
    }
    else{
      if( anneau == 1){
        entite = setIsDuplicateur(entite, false);
        entite = modifEntite(entite, 1);
        entite = modifEntite(entite, 2);
      }
      else if( anneau == 2){
        entite = setIsDuplicateur(entite, false);
        entite = modifEntite(entite, 2);
      }
      else{
        fprintf(stderr,"L'addresse et/ou le port de multidiff est inconnu a cette entite\n");
      }
    }
  }
  else{
    mssgSpellCheck(tampon, "multidiff");
  }
  return entite;
}

void* run(void* ent) {
  Entite* entite = (Entite*) ent;
  sleep(TIMEMAX);
  entite = declencheMultiDiff(entite, 1);
  if(getIsDuplicateur(entite) == true){
    entite = declencheMultiDiff(entite, 2);
  }
  pthread_exit(entite);
}
