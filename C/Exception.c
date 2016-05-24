#include "Exception.h"

void lengthException(int lengthAttendu, int lengthReel, char* ou, char* message) {
  fprintf(stderr,"Erreur dans le nombre d'arguments en %s nombre d'arguments trouve : %d et %d sont attendus pour le message %s\n", ou, lengthReel, lengthAttendu, message);
  INFO("Je ne transfere pas ce message");
}

void lengthExceptionMax(int lengthReel, char* message, char* ou) {
  fprintf(stderr,"Erreur dans la longueur du message en %s vaut : %d et %d sont attendus pour le message %s \n", ou, lengthReel, SIZEMSSG, message);
  INFO("Je ne transfere pas ce message\n");
}

void mssgSpellCheck(char* message, char* ou) {
  fprintf(stderr,"Erreur type de message inconnue en %s : %s \n", ou, message);
  INFO("Je ne transfere pas ce message\n");
}

void notSDLException(char* message, char* ou) {
  fprintf(stderr,"Erreur dans la synthaxe du message en %s il manque le \\n : dans le message %s\n", ou, message);
}
