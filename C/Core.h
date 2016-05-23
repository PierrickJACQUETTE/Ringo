#ifndef __CORE_H__
#define __CORE_H__

#include "Include.h"

#define SIZEMSSG 512

typedef struct{
	char* idm;
	bool isTest;
	long my_time;
}Mssg;

typedef struct s_List List;
struct s_List{
	List* next;
	Mssg* data;
};

typedef struct{
	char* idm;
	char* portUDPIn;
	char* portUDPOut[2];
	char* adresseOut[2];
	char* portTCPIn;
	char* portTCPOut;
	char* addrMultiDiff[2];
	char* portMultiDiff[2];
	bool isDuplicateur;
	bool isEYBE;
	List* mssgTransmisAnneau1;
	List* mssgTransmisAnneau2;
}Entite;

#endif
