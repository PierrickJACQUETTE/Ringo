#ifndef __MSSGMULTIDIFF_H__
#define __MSSGMULTIDIFF_H__

#include "Core.h"
#include "Entite.h"
#include "Annexe.h"
#include "Exception.h"

Entite* receiveMultiDiff(Entite* entite, int sock, int anneau);
void* run(void* ent);

#endif
