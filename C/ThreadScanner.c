#include "ThreadScanner.h"

Mssg* trans(char* idm, char* message, char ** parts, int anneau, int nombreDePartie) {
  Mssg* m = mssg_create(idm);
  if (strcmp(parts[0], "TEST")==0) {
    m = mssg_createT(idm, true, timeReel());
  }
  return m;

}

Mssg* sendAnneauT(Entite* entite, char* tmp, char* tmpAPPL, int i, char** suite, bool isPossible, int ndp) {
  char* idm = newIdentifiant();
  char* message = malloc(sizeof(char)*SIZEMSSG);
  sprintf(message, "%s %s", tmp ,idm);
  if (strcmp(tmp ,"MEMB")==0) {
    sprintf(message, "%s %s %s %s %s", "MEMB", idm, getIdentifiant(entite), trouveAdress(), getPortInUDP(entite));
  } else if (strcmp(tmp, "GBYE")==0) {
    char message2 [SIZEMSSG];
    snprintf(message2, sizeof(message2), "%s %s %s %s %s", message, trouveAdress(), getPortInUDP(entite), getAddrNext(entite, i), getPortOutUDP(entite, i));
    sprintf(message, "%s", message2);
  } else if (strcmp(tmp, "TEST")==0) {
    char message2 [SIZEMSSG];
    snprintf(message2, sizeof(message2), "%s %s %s", message, getAddrMultiDiff(entite, i), getPortMultiDiff(entite, i));
    sprintf(message, "%s", message2);
  } else if (strcmp(tmp, "APPL")==0 && strcmp(suite[1], "DIFF")==0) {
    char message2 [SIZEMSSG];
    snprintf(message2, sizeof(message2), "%s %s%s ", message, suite[1], "####");
    sprintf(message, "%s", message2);
    int debut = 2;
    int debutReel = 0;
    int i;
    for(i = 0; i < debut; i++){
      debutReel += strlen(suite[i] + 1);
    }
    char* res = malloc(sizeof(char)*(strlen(message)-debutReel));
    debutReel = 0;
    for(i = debut; i < ndp; i++){
      strcat(res, " ");
      strcat(res, suite[i]);
      debutReel+= strlen(suite[i])+1;
    }
    char *aze = remplirZero(debutReel-1,3);
    strcat(message, aze);
    strcat(message, res);
  }
  char* copyMessage2 = copyStr(message);
  char* copyMessage3 = copyStr(message);
  int nombreDePartie = sizePart(copyMessage3, " ");
  char** parts = split(copyMessage2, ' ');
  bool res = false;
  if (isPossible == true) {
    membPrint(parts);
    res = analyseMssgUDP(message, parts, nombreDePartie, strlen(message), true);
  } else {
    res = analyseMssgUDP(message, parts, nombreDePartie, strlen(message), false);
  }
  if(res == false){
    sendUDP(message, entite, i);
  }
  return trans(idm, message, parts, i, nombreDePartie);
}

void envoi(Entite* entite, char* tmp, char* tmp2, char** suite, bool isPossible, int ndp) {
  Mssg* m = sendAnneauT(entite, tmp, tmp2, 1, suite, isPossible, ndp);
  List* listEntite = list_add(getMssgTransmisAnneau1(entite),m);
  entite = setMssgTransmisAnneau1(entite,listEntite);
  if (getIsDuplicateur(entite) == true) {
    m = sendAnneauT(entite, tmp, tmp2, 2, suite, isPossible, ndp);
    List* listEntite = list_add(getMssgTransmisAnneau2(entite),m);
    entite = setMssgTransmisAnneau2(entite,listEntite);
  }
}

void threadScanner(Entite* entite) {
  char* reponse = malloc(SIZEMSSG*sizeof(char));
  char* copyReponse = copyStr(reponse);
  bool res = false;
  if(reponse == NULL ){
    fprintf(stderr,"Allocation impossible : %s\n","fonction threadScanner : threadScanner.c");
  }
  reponse = saisir_chaine();
  char** suite = NULL;
  bool info = false;
  char* copyMessage = copyStr(reponse);
  char* copyMessage2 = copyStr(reponse);
  int nombreDePartie = sizePart(copyMessage, " ");
  suite = split(copyMessage2, ' ');
  if (strcmp(reponse, "INFO SIMPLE")==0) {
    entite_print_simple(entite);
    info = true;
  } else if (strcmp(reponse, "INFO COMPLEX")==0) {
    entite_print_complex(entite);
    info = true;
  } else if (strcmp(reponse, "WHOS")==0) {
 	envoi(entite, reponse, copyReponse, suite, res, nombreDePartie);
    envoi(entite, "MEMB", "", suite, true, nombreDePartie);
	info = true;
  } else if (strcmp(suite[0], "APPL")==0) {
    res = suiteAnalyseMssgInf(3, reponse, suite, nombreDePartie);
    reponse = suite[0];
  }
  if (info == false && res == false) {
    envoi(entite, reponse, copyReponse, suite, res, nombreDePartie);
  }


}
