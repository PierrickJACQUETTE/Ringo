#include "MssgMultiDiff.h"

Entite* sendMutiDiff(Entite* entite, int anneau){
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
      if( ((timeReel() - tmp->data->my_time) > TIMEMAX) && (tmp->data->isTest == true) ){
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

Entite* receiveMultiDiff(Entite* entite, int socket){
  return entite;
}

Entite* modifEntite(Entite* entite, int anneau){
  return entite;
}
