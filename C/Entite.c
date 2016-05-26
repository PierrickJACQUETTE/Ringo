#include "Entite.h"

Entite* entite_create(){
	Entite *entite = (Entite*)malloc(sizeof(Entite));
	if (entite == NULL){
		fprintf(stderr,"Allocation impossible : %s\n","fonction entite_create : Entite.c");
		exit(EXIT_FAILURE);
	}
	entite->idm = "-1";
	entite->portUDPIn = "-1";
	entite->portUDPOut[0] = NULL;
	entite->portUDPOut[1] = NULL;
	entite->adresseOut[0] = NULL;
	entite->adresseOut[1] = NULL;
	entite->portTCPIn = "-1";
	entite->portTCPOut = "-1";
	entite->addrMultiDiff[0] = NULL;
	entite->addrMultiDiff[1] = NULL;
	entite->portMultiDiff[0] = NULL;
	entite->portMultiDiff[1] = NULL;
	entite->isDuplicateur = false;
	entite->isEYBE = false;
	entite->mssgTransmisAnneau1 = list_create(NULL);
	entite->mssgTransmisAnneau2 = list_create(NULL);
	return entite;
}

void entite_destroy(Entite* entite){
	free(entite->idm);
	free(entite->portTCPIn);
	free(entite->portTCPOut);
	free(entite->adresseOut);
	free(entite->portTCPIn);
	free(entite->portTCPOut);
	free(entite->addrMultiDiff);
	free(entite->portMultiDiff);
	list_destroy(entite->mssgTransmisAnneau1);
	list_destroy(entite->mssgTransmisAnneau2);
	free(entite);
}

void entite_print_new(Entite* entite){
	printf("\nVous venez de créer une entité avec ses attributs :");
	INFO(entite_print_simple(entite));
}

char* entite_print_simple(Entite* entite){
	printf("\nIdentifiant : %s\n", entite->idm);
	printf("PortTCPIn : %s\n", entite->portTCPIn);
	printf("Port In UPD : %s \n", entite->portUDPIn);
	printf("PortTCPOut : %s\n", entite->portTCPOut);
	int i;
	for(i = 0; i < 2 ; i++){
		printf("Addresse next : %s\n", entite->adresseOut[i]);
		printf("Port Out UDP : %s\n", entite->portUDPOut[i]);
	}
	printf("L'entite est un duplicateur ? %s\n", entite->isDuplicateur?"true":"false");
	for(i = 0; i < 2; i++){
		printf("Addresse Multi Diff : %s\n", entite->addrMultiDiff[i]);
		printf("Port Multi Diff : %s\n", entite->portMultiDiff[i]);
	}
	printf("Si cest un duppl et deja recu EYBE ? %s\n", entite->isEYBE?"true":"false");
	return "";
}

void entite_print_complex(Entite* entite){
	entite_print_simple(entite);
	printf("Les messages transmis par cette entite sont : \n");
	printf("Sur l'anneau 1 : \n");
	list_print(entite->mssgTransmisAnneau1);
	printf("Sur l'anneau 2 : \n");
	list_print(entite->mssgTransmisAnneau2);
	printf("\n");
}

char * getIdentifiant(Entite* entite) {
	return  entite->idm;
}

Entite* setIdentifiant(Entite* entite, char * identifiant) {
	entite->idm = identifiant;
	return entite;
}

char* getPortInUDP(Entite* entite) {
	return entite->portUDPIn;
}

Entite* setPortInUDP(Entite* entite, char* portInUDP) {
	entite->portUDPIn = portInUDP;
	return entite;
}

char* getPortOutUDP(Entite* entite, int i) {
	char* res = "";
	if (i == 1) {
		res =  entite->portUDPOut[0];
	} else if (i == 2) {
		res =  entite->portUDPOut[1];
	} else {
		fprintf(stderr,"Erreur dans getAddrMultiDiff anneau non reconnue : %d \n", i);
	}
	return res;
}

Entite* setPortOutUDP(Entite* entite, char* portOutUDP, int i) {
	if (i == 1) {
		entite->portUDPOut[0] = portOutUDP;
	} else if (i == 2) {
		entite->portUDPOut[1] = portOutUDP;
	} else {
		fprintf(stderr,"Erreur dans setAddrNext anneau non reconnue : %d \n", i);
	}
	return entite;
}

char* getPortTCPIn(Entite* entite) {
	return entite->portTCPIn;
}

Entite* setPortTCPIn(Entite* entite, char* portTCP) {
	entite->portTCPIn = portTCP;
	return entite;
}

char* getPortTCPOut(Entite* entite) {
	return  entite->portTCPOut;
}

Entite* setPortTCPOut(Entite* entite, char* portTCP) {
	entite->portTCPOut = portTCP;
	return entite;
}

char * getAddrNext(Entite* entite, int i) {
	char * res = "";
	if (i == 1) {
		res =  entite->adresseOut[0];
	} else if (i == 2) {
		res =  entite->adresseOut[1];
	} else {
		fprintf(stderr,"Erreur dans getAddrNext anneau non reconnue : %d \n", i);
	}
	return res;
}

Entite* setAddrNext(Entite* entite, char * addrNext, int i) {
	if (i == 1) {
		entite->adresseOut[0] = addrNext;
	} else if (i == 2){
		entite->adresseOut[1] = addrNext;
	} else {
		fprintf(stderr,"Erreur dans setAddrNext anneau non reconnue : %d \n", i);
	}
	return entite;
}

char * getAddrMultiDiff(Entite* entite, int i) {
	char * res = "";
	if (i == 1) {
		res =  entite->addrMultiDiff[0];
	} else if (i == 2) {
		res =  entite->addrMultiDiff[1];
	} else {
		fprintf(stderr,"Erreur dans getAddrMultiDiff anneau non reconnue : %d \n", i);
	}
	return res;
}

Entite* setAddrMultiDiff(Entite* entite, char * addrMultiDiff, int i) {
	if (i == 1) {
		entite->addrMultiDiff[0] = addrMultiDiff;
	} else if (i == 2) {
		entite->addrMultiDiff[1] = addrMultiDiff;
	} else {
		fprintf(stderr,"Erreur dans setAddrMultiDiff anneau non reconnue : %d \n", i);
	}
	return entite;
}

char* getPortMultiDiff(Entite* entite, int i) {
	char* res = "";
	if (i == 1) {
		res = entite->portMultiDiff[0];
	} else if (i == 2) {
		res = entite->portMultiDiff[1];
	} else {
		fprintf(stderr,"Erreur dans portMultiDiff anneau non reconnue : %d \n", i);
	}
	return res;
}

Entite* setPortMultiDiff(Entite* entite, char* portMultiDiff, int i) {
	if (i == 1) {
		entite->portMultiDiff[0] = portMultiDiff;
	} else if (i == 2) {
		entite->portMultiDiff[1] = portMultiDiff;
	} else {
		fprintf(stderr,"Erreur dans portMultiDiff anneau non reconnue : %d \n", i);
	}
	return entite;
}

bool getIsDuplicateur(Entite* entite) {
	return  entite->isDuplicateur;
}

Entite* setIsDuplicateur(Entite* entite, bool isDuplicateur) {
	entite->isDuplicateur = isDuplicateur;
	return entite;
}

bool getAlreadyReceivedEYBG(Entite* entite) {
	return  entite->isEYBE;
}

Entite* setAlreadyReceivedEYBG(Entite* entite, bool alreadyReceivedEYBG) {
	entite->isEYBE = alreadyReceivedEYBG;
	return entite;
}

List* getMssgTransmisAnneau1(Entite* entite) {
	return  entite->mssgTransmisAnneau1;
}

Entite* setMssgTransmisAnneau1(Entite* entite, List* mssgTransmis) {
	entite->mssgTransmisAnneau1 = mssgTransmis;
	return entite;
}

List* getMssgTransmisAnneau2(Entite* entite) {
	return  entite->mssgTransmisAnneau2;
}

Entite* setMssgTransmisAnneau2(Entite* entite, List* mssgTransmis) {
	entite->mssgTransmisAnneau2 = mssgTransmis;
	return entite;
}
