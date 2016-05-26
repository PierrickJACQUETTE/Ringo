#ifndef __ANNEAU_H__
#define __ANNEAU_H__

#include "Core.h"
#include "Annexe.h"
#include "Entite.h"
#include "MssgTCP.h"
#include "MssgUDP.h"
#include "ThreadScanner.h"
#include "MssgMultiDiff.h"

void anneau_insert(Entite* entite, int optvalMultiDiff);

#endif
