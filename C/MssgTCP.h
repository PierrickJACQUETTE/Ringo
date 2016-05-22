#ifndef __MSSGTCP_H__
#define __MSSGTCP_H__

#include "Core.h"
#include "Exception.h"
#include "Entite.h"
#include "Annexe.h"

Entite* insertNouveauTCP(Entite* entite, bool joindre);
Entite* insertAnneauTCP(Entite* entite, int sock);

#endif
