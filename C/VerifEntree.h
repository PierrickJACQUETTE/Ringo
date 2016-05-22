#ifndef __VERIFENTREE_H__
#define __VERIFENTREE_H__

#include "Core.h"
#include "Entite.h"
#include "Annexe.h"
#include "MssgTCP.h"

void modeAffichage(int argc, char **argv);
Entite* choixEntite(Entite* entite);
Entite* rejoindreAnneau(Entite* entite, bool joindre);

#endif
