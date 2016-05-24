#ifndef __ENTITE_H__
#define __ENTITE_H__

#include "Core.h"
#include "List.h"

Entite* entite_create();
void entite_destroy(Entite* entite);

char* entite_print_simple(Entite* entite);
void entite_print_complex(Entite* entite);
void entite_print_new(Entite* entite);

char * getIdentifiant(Entite* entite);
Entite* setIdentifiant(Entite* entite, char * identifiant);

char* getPortInUDP(Entite* entite);
Entite* setPortInUDP(Entite* entite, char* portInUDP);

char* getPortOutUDP(Entite* entite, int i);
Entite* setPortOutUDP(Entite* entite, char* portOutUDP, int i);

char* getPortTCPIn(Entite* entite);
Entite* setPortTCPIn(Entite* entite, char* portTCP);

char* getPortTCPOut(Entite* entite);
Entite* setPortTCPOut(Entite* entite, char* portTCP);

char * getAddrNext(Entite* entite, int i);
Entite* setAddrNext(Entite* entite, char * addrNext, int i);

char * getAddrMultiDiff(Entite* entite, int i);
Entite* setAddrMultiDiff(Entite* entite, char * addrMultiDiff, int i);

char* getPortMultiDiff(Entite* entite, int i);
Entite* setPortMultiDiff(Entite* entite, char* portMultiDiff, int i);

bool getIsDuplicateur(Entite* entite);
Entite* setIsDuplicateur(Entite* entite, bool isDuplicateur);

bool getAlreadyReceivedEYBG(Entite* entite);
Entite* setAlreadyReceivedEYBG(Entite* entite, bool alreadyReceivedEYBG);

List* getMssgTransmisAnneau1(Entite* entite);
Entite* setMssgTransmisAnneau1(Entite* entite, List* mssgTransmis);

List* getMssgTransmisAnneau2(Entite* entite);
Entite* setMssgTransmisAnneau2(Entite* entite, List* mssgTransmis);

#endif
