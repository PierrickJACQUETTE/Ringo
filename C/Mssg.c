#include "Mssg.h"

Mssg * mssg_create (char* idm){
	Mssg *mssg =(Mssg*) malloc(sizeof(Mssg));
	if (mssg == NULL){
		fprintf(stderr,"Allocation impossible : %s\n","fonction mssg_create : Mssg.c");
		exit(EXIT_FAILURE);
	}
	if(strlen(idm) == 8){
		mssg->idm = idm;
	}
	else{
		int len = strlen(idm);
		fprintf(stderr,"La taille de l'identifiant ne vaut pas 8 mais : %d\n",len);
	}
	return mssg;
}

void mssg_destroy(Mssg* mssg){
	free(mssg->idm);
	free(mssg);
}
