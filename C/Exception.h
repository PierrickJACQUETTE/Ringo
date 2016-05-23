#ifndef __EXCEPTION_H__
#define __EXCEPTION_H__

#include "Core.h"

void lengthException(int lengthAttendu, int lengthReel, char* ou, char* message);
void lengthExceptionMax(int lengthReel, char* message, char* ou);
void mssgSpellCheck(char* message, char* ou);
void notSDLException(char* message, char* ou);

#endif
