#include "Annexe.h"

char* saisir_chaine(){
  char*  lpBuffer = malloc(512*sizeof(char));
  if(lpBuffer == NULL ){
    fprintf(stderr,"Allocation Impossible: %s\n","fonction saisir_chaine : Annexe.c");
    return NULL;
  }
  size_t nbCar = 0;
  int c;
  c = getchar();
  while (c != EOF && c != '\n'){
    lpBuffer[nbCar] = (char)c;
    nbCar++;
    c = getchar();
  }
  lpBuffer[nbCar] = '\0';
  return lpBuffer;
}

char** substringLast(char ** parts, int sizeParts){
  int sizeMssg = strlen(parts[sizeParts-1]);
  char lastMssg = parts[sizeParts-1][sizeMssg-1];
  if (lastMssg == '\n') {
    char *subbuff = malloc(sizeof(char)*sizeMssg);
    strncpy( subbuff, parts[sizeParts-1], sizeMssg-1 );
    subbuff[sizeMssg-1] = '\0';
    parts[sizeParts-1] = subbuff;
  }
  return parts;
}

bool inf9999(int i) {
  return (i < 9999) ? true : false;
}

bool sup0(int i) {
  return (i > 0) ? true : false;
}

bool sup1023(int i) {
  return (i > 1023) ? true : false;
}

bool inf65636(int i) {
  return (i < 65636) ? true : false;
}

bool verifNombre(char * str,bool isUDP){
  int res = entier(str);
  if( (res == 0) || (strlen(str) > 4) ){
    return false;
  }
  if (sup0(res) == false) {
    fprintf(stderr,"Le nombre doit etre superieur a 0.");
    return false;
  }
  if (sup1023(res) == false) {
    fprintf(stderr,"Le nombre doit etre superieur a 1023.");
    return false;
  }
  if (isUDP) {
    if (inf9999(res) == false) {
      fprintf(stderr,"Le nombre doit etre inferieur a 9999.");
      return false;
    }
  } else {
    if (inf65636(res) == false) {
      fprintf(stderr,"Le nombre doit etre inferieur a 65636.");
      return false;
    }
  }
  return true;
}

bool verifAddress(char* str, bool multi){
  struct in_addr address;
  int i = inet_aton(str,&address);
  struct addrinfo *first_info;
  struct addrinfo hints;
  bzero(&hints,sizeof(struct addrinfo));
  hints.ai_family = AF_INET;
  hints.ai_socktype = SOCK_STREAM;
  int ii = getaddrinfo(str, "7778", &hints, &first_info);
  bool res = true;
  if(multi == true){
      char* copyMessage3 = copyStr(str);
      char** parts = split(copyMessage3, '.');
      res = (entier(parts[0]) > 239 || entier(parts[0]) < 224)? false : true;
  }
  return ((i == 0 || ii == 00) && res == true);
}

char* addZero(char* str, int nbrZero) {
  char* res = malloc(sizeof(char)*nbrZero);
  int i;
  for (i = 0; i < nbrZero; i++) {
    strcat(res,"0");
  }
  return strcat(res,str);
}

int sizePart(char* str,char * separateur){
  const char* s = separateur;
  char *token;
  int nb = 0;
  token = strtok(str, s);
  nb++;
  while( token != NULL ) {
    token = strtok(NULL, s);
    nb++;
  }
  return nb-1;
}

char** split(char* a_str, const char a_delim){
  char** result = 0;
  size_t count = 0;
  char* tmp = a_str;
  char* last_comma = 0;
  char delim[2];
  delim[0] = a_delim;
  delim[1] = 0;
  while (*tmp){
    if (a_delim == *tmp){
      count++;
      last_comma = tmp;
    }
    tmp++;
  }
  count += last_comma < (a_str + strlen(a_str) - 1);
  count++;
  result = malloc(sizeof(char*) * count);
  if(result == NULL ){
    fprintf(stderr,"Allocation Impossible: %s\n","fonction split : Annexe.c");
    return NULL;
  }
  if (result){
    size_t idx  = 0;
    char* token = strtok(a_str, delim);
    while (token){
      *(result + idx++) = strdup(token);
      token = strtok(0, delim);
    }
    *(result + idx) = 0;
  }
  return result;
}

char* copyStr(char *lpBuffer){
  char* mssg2 = malloc(strlen(lpBuffer)*(sizeof(char)));
  if(mssg2 == NULL ){
    fprintf(stderr,"Allocation Impossible: %s\n","fonction copyStr : Annexe.c");
    return NULL;
  }
  sprintf(mssg2, "%s", lpBuffer);
  return mssg2;
}

int entier(char* str){
  return atoi(str);
}

char* convertIPV4Imcomplete(char* str){
  char* copyTextAddr = copyStr(str);
  char* copyTextAddr2 = copyStr(str);
  int nombreDePartie = sizePart(copyTextAddr2, ".");
  char** parts = split(copyTextAddr, '.');
  char* addrComplete = malloc(15*sizeof(char));
  if(addrComplete == NULL ){
    fprintf(stderr,"Allocation Impossible: %s\n","fonction convertIPV4Complete : Annexe.c");
    return NULL;
  }
  int j;
  for(j=0;j<strlen(addrComplete);j++){
    addrComplete[j]='0';
  }
  for (j = 0; j < nombreDePartie; j++) {
    if(j == 0){
      sprintf(addrComplete, "%d", entier(parts[j]));
    }
    else{
      char buff[2];
      sprintf(buff, "%d", entier(parts[j]));
      strcat(addrComplete, buff);
    }
    if(j+1 < nombreDePartie){
      char buff2[2];
      sprintf(buff2, "%c", '.');
      strcat(addrComplete, buff2);
    }
  }
  return addrComplete;
}

char* convertIPV4Complete(char * textAddr) {
  char* copyTextAddr = copyStr(textAddr);
  int nombreDePartie = sizePart(textAddr, ".");
  char** parts = split(copyTextAddr, '.');
  char* addrComplete = malloc(15*sizeof(char));
  if(addrComplete == NULL ){
    fprintf(stderr,"Allocation Impossible: %s\n","fonction convertIPV4Complete : Annexe.c");
    return NULL;
  }
  int j2;
  for(j2 = 0; j2 < 15; j2++){
    addrComplete[j2] = 0;
  }
  int j;
  for (j = 0; j < nombreDePartie; j++) {
    int i = entier(parts[j]);
    if ((i >= 0) && (i < 10)) {
      sprintf(addrComplete, "%s%s%d", addrComplete, "00", i);
    } else if (i >= 10 && i < 99) {
      sprintf(addrComplete, "%s%s%d", addrComplete, "0", i);
    } else {
      sprintf(addrComplete, "%s%d", addrComplete, i);
    }
    if(j+1 < nombreDePartie){
      sprintf(addrComplete, "%s%s", addrComplete, ".");
    }
  }
  free(parts);
  free(copyTextAddr);
  return addrComplete;
}

char* trouveAdress(){
  struct ifaddrs *myaddrs, *ifa;
  struct sockaddr_in *s4;
  int status;
  char *ip = (char *)malloc(64*sizeof(char));
  if(ip == NULL ){
    fprintf(stderr,"Allocation Impossible: %s\n","fonction trouveAdress : Annexe.c");
    return NULL;
  }
  status = getifaddrs(&myaddrs);
  if (status != 0){
    perror("Probleme de recuperation d'adresse IP");
    exit(1);
  }
  for (ifa = myaddrs; ifa != NULL; ifa = ifa->ifa_next){
    if (ifa->ifa_addr == NULL) continue;
    if ((ifa->ifa_flags & IFF_UP) == 0) continue;
    if ((ifa->ifa_flags & IFF_LOOPBACK) != 0) continue;
    if (ifa->ifa_addr->sa_family == AF_INET){
      s4 = (struct sockaddr_in *)(ifa->ifa_addr);
      if (inet_ntop(ifa->ifa_addr->sa_family, (void *)&(s4->sin_addr), ip, 64*sizeof(char)) != NULL){
      }
    }
  }
  freeifaddrs(myaddrs);
  return convertIPV4Complete(ip);
}

char* base(char* str){
  long nbr;
  int base = 97;
  long reste,quotient,diviseur ;
  char* affichage =  malloc(20*sizeof(char));
  if(affichage == NULL ){
    fprintf(stderr,"Allocation Impossible: %s\n","fonction base : affichage : Annexe.c");
    return NULL;
  }
  char* convert = malloc(2*sizeof(char));
  if(convert == NULL ){
    fprintf(stderr,"Allocation Impossible: %s\n","fonction base : convert : Annexe.c");
    return NULL;
  }
  char* temp = malloc(20*sizeof(char));
  if(temp == NULL ){
    fprintf(stderr,"Allocation Impossible: %s\n","fonction base : temp : Annexe.c");
    return NULL;
  }
  nbr =  atol(str);
  diviseur = base ;
  quotient = 1 ;
  convert[1] = '\0' ;
  affichage[0] ='\0' ;
  while (quotient != 0){
    quotient = nbr / diviseur ;
    reste = nbr % diviseur ;
    nbr = quotient ;
    if ((reste >= 0) && (reste <= 9)){
      convert[0] = reste+48 ;
    }
    else{
      convert[0] = reste+55 ;
    }
    if(convert[0] < 33 || convert[0] > 126){
      convert[0] = 48;
    }
    strcpy(temp,convert) ;
    strcat(temp,affichage) ;
    strcpy(affichage,temp) ;
  }
  free(temp);
  free(convert);
  return affichage;
}

char* length8(char* tmp) {
  int coupure = 8;
  tmp = base(tmp);
  char* res = malloc(coupure*sizeof(char));
  if(res == NULL ){
    fprintf(stderr,"Allocation Impossible: %s\n","fonction length8 : Annexe.c");
    return NULL;
  }
  int sizeTmp = strlen(tmp);
  if (sizeTmp > coupure) {
    int c;
    for (c =sizeTmp-coupure; c < sizeTmp; c++) {
      res[c] = tmp[c];
    }
    res[c]='\0';
  }
  else if(sizeTmp < coupure){
    int c;
    for (c = 0; c < sizeTmp; c++) {
      res[c] = tmp[c];
    }
    for (c = sizeTmp; c < coupure; c++) {
      res[c] = '0';
    }
    res[c]='\0';
  }
  else{
    res = tmp;
  }
  return res;
}

long timeReel(){
  struct timespec start_time;
  clock_gettime(CLOCK_REALTIME, &start_time);
  return start_time.tv_nsec;
}

char* newIdentifiant(){
  char* identifiant = malloc(sizeof(char)*10);
  sprintf(identifiant, "%ld", timeReel());
  return length8(identifiant);
}

char* suppPoint(char* str){
  char* res = malloc(sizeof(str));
  if(res == NULL ){
    fprintf(stderr,"Allocation Impossible: %s\n","fonction suppPoint : Annexe.c");
    return NULL;
  }
  int i;
  int iRes = 0;
  for(i = 0; i < strlen(str); i++){
    if(str[i] != '.'){
      res[iRes] = str[i];
      iRes++;
    }
  }
  return res;
}

char* identifiantEntite(char* port){
  char* adresse = trouveAdress();
  char* idm = malloc(16*sizeof(char));
  if(idm == NULL ){
    fprintf(stderr,"Allocation Impossible: %s\n","fonction identifiantEntite : Annexe.c");
    return NULL;
  }
  sprintf(idm, "%s%s", adresse, port);
  return length8(suppPoint(idm));
}

char* remplirZero(int size, int sizeVoulu){
  char* taille = malloc(sizeof(char)*(sizeVoulu));
  sprintf(taille,"%d",size);
  taille = addZero(taille, sizeVoulu-strlen(taille));
  return taille;
}

void waitAMssg() {
  printf("\nWaiting for messages : WHOS, GBYE, TEST, INFO [SIMPLE|COMPLEX], APPL DIFF mess\n");
}
