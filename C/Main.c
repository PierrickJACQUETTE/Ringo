#include "Main.h"

int main(int argc, char **argv) {
	Entite* entite = entite_create();
	entite = choixEntite(entite);
	anneau_insert(entite, OPTVALMULTIDIFF);
	entite_destroy(entite);
	return 0;
}
