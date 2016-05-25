#ifndef __MSSGUDP_H__
#define __MSSGUDP_H__

#include "Core.h"
#include "Annexe.h"
#include "List.h"
#include "Mssg.h"
#include "Entite.h"
#include "Exception.h"

bool suiteAnalyseMssgInf(int longeur, char* mssg, char** parts, int nombreDePartie);
bool analyseMssgUDP(char* mssg, char** parts, int nombreDePartie, int size, bool isPrivate);
void membPrint(char** parts);
void sendUDP(char* message, Entite* entite, int anneau);
Entite* receiveUDP(Entite* entite, int socket);

#endif
