#include "Mssg.h"

Mssg * mssg_createT(char* idm, bool test, long t){
	Mssg *mssg =(Mssg*) malloc(sizeof(Mssg));
	if (mssg == NULL){
		fprintf(stderr,"Allocation impossible : %s\n","fonction mssg_create : Mssg.c");
		exit(EXIT_FAILURE);
	}
	if(strlen(idm) == 8){
		mssg->idm = idm;
		mssg->isTest = test;
		mssg->my_time = t;
	}
	else{
		int len = strlen(idm);
		fprintf(stderr,"La taille de l'identifiant ne vaut pas 8 mais : %d %s \n",len ,idm);
	}
	return mssg;
}

void mssg_destroy(Mssg* mssg){
	free(mssg->idm);
	free(mssg);
}

bool getIsTest(Mssg* message){
	return message->isTest;
}

Mssg* setIsTest(Mssg* message, bool test){
	message->isTest = test;
	return message;
}

Mssg * mssg_create(char* idm){
	return mssg_createT(idm, false, 0);
}
