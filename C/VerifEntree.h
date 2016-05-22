#ifndef __VERIFENTREE_H__
#define __VERIFENTREE_H__

#include "Core.h"
#include "Entite.h"
#include "Annexe.h"
#include "MssgTCP.h"

Entite* choixEntite(Entite* entite);
Entite* rejoindreAnneau(Entite* entite, bool joindre);

#endif
