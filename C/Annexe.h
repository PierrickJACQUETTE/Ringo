#ifndef __ANNEXE_H__
#define __ANNEXE_H__

#include "Include.h"

char* saisir_chaine();
char** substringLast(char** parts, int sizeParts);
bool verifNombre(char * str, bool isUDP);
bool verifAddress(char * str);
int sizePart(char* str, char * separateur);
char** split(char* a_str, const char a_delim);
char* copyStr(char *lpBuffer);
int entier(char* str);
char* convertIPV4Complete(char * textAddr);
char* trouveAdress();
char* identifiantEntite(char* port);
void waitAMssg();
long timeReel();
char* newIdentifiant();
char* remplirZero(int size, int sizeVoulu);
#endif
