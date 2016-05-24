#ifndef __MSSG_H__
#define __MSSG_H__

#include "Core.h"

Mssg* mssg_createT(char* idm, bool test, long t);
Mssg* mssg_create(char* idm);
void mssg_destroy(Mssg* mssg);
bool getIsTest(Mssg* message);
Mssg* setIsTest(Mssg* message, bool test);

#endif
