#include "Anneau.h"

void closeSock(char* message, int sock){
  fprintf(stderr, "Problem %s\n", message);
  close(sock);
  exit(EXIT_FAILURE);
}

int max2(int un, int deux){
  return (un>deux)? un : deux;
}

int max(int un, int deux,int trois){
  return max2(max2(un,deux),trois);
}

void anneau_insert(Entite* entite, int optvalMultiDiff){

  // --------------- TCP NON BLOQUANT ------------
  int sock_tcp = socket(PF_INET, SOCK_STREAM, 0);
  if (sock_tcp ==-1) {
    fprintf(stderr, "socket problem TCP NON BLOQUANT\n");
    exit(EXIT_FAILURE);
  }
  struct sockaddr_in address_sock_tcp;
  bzero(&address_sock_tcp, sizeof(address_sock_tcp));
  address_sock_tcp.sin_family = AF_INET;
  address_sock_tcp.sin_port = htons(entier(getPortTCPIn(entite)));
  address_sock_tcp.sin_addr.s_addr = htonl(INADDR_ANY);
  if (bind(sock_tcp, (struct sockaddr *)&address_sock_tcp, sizeof(address_sock_tcp)) ==-1) {
    closeSock("bind TCP NON BLOQUANT", sock_tcp);
  }
  if (listen(sock_tcp,0)==-1) {
    closeSock("listen TCP NON BLOQUANT", sock_tcp);
  }
  if (fcntl(sock_tcp,F_SETFL,O_NONBLOCK)<0) {
    fprintf(stderr,"Problem non bloquant tcp\n");
  }
  // -----------------------------------------------

  // ------------------ UDP NON BLOQUANT ------------
  int sock_udp = socket(PF_INET,SOCK_DGRAM,0);
  if (sock_udp==-1) {
    fprintf(stderr,"socket problem UDP NON BLOQUANT\n");
    exit(EXIT_FAILURE);
  }
  struct sockaddr_in address_sock_udp;
  bzero(&address_sock_udp,sizeof(address_sock_udp));
  address_sock_udp.sin_family=AF_INET;
  address_sock_udp.sin_port=htons(entier(getPortInUDP(entite)));
  address_sock_udp.sin_addr.s_addr=htonl(INADDR_ANY);
  if(bind(sock_udp,(struct sockaddr *)&address_sock_udp,sizeof(struct sockaddr_in))==-1){
    closeSock("Probleme bind UDP NON BLOQUANT",sock_udp);
  }
  if (fcntl(sock_udp,F_SETFL,O_NONBLOCK)<0) {
    fprintf(stderr,"problem non bloquant udp\n");
  }
  // ------------------------------------------------

  // ------------ MULTI DIFF NON BLOQUANT -----------
  int sock_multi_diff = socket(PF_INET,SOCK_DGRAM,0);
  if (sock_multi_diff==-1) {
    fprintf(stderr,"socket problem MULTI DIFF NON BLOQUANT\n");
    exit(EXIT_FAILURE);
  }
  int ok = 1;
  if(setsockopt(sock_multi_diff,SOL_SOCKET,optvalMultiDiff,&ok,sizeof(ok))==-1){
    fprintf(stderr,"setsockopt problem MULTI DIFF NON BLOQUANT\n");
    exit(EXIT_FAILURE);
  }
  struct sockaddr_in address_sock_multi_diff;
  bzero(&address_sock_multi_diff,sizeof(address_sock_multi_diff));
  address_sock_multi_diff.sin_family=AF_INET;
  address_sock_multi_diff.sin_port=htons(entier(getPortMultiDiff(entite,1)));
  address_sock_multi_diff.sin_addr.s_addr=htonl(INADDR_ANY);
  if(bind(sock_multi_diff,(struct sockaddr *)&address_sock_multi_diff,sizeof(struct sockaddr_in))==-1){
    closeSock("Probleme bind MULTI DIFF NON BLOQUANT",sock_multi_diff);
  }
  struct ip_mreq mreq;
  mreq.imr_multiaddr.s_addr=inet_addr("225.1.2.4");
  mreq.imr_interface.s_addr=htonl(INADDR_ANY);
  if(setsockopt(sock_multi_diff,IPPROTO_IP,IP_ADD_MEMBERSHIP,&mreq,sizeof(mreq))==-1){
    closeSock("Probleme setsockopt MULTI DIFF NON BLOQUANT",sock_multi_diff);
  }
  if (fcntl(sock_multi_diff,F_SETFL,O_NONBLOCK)<0) {
    fprintf(stderr,"problem non bloquant MULTI DIFF\n");
  }
  // ------------------------------------------------

  pthread_t scanner;
  pthread_create(&scanner, NULL, (void*(*)(void*)) &threadScanner,entite);
  pthread_join(scanner, NULL);

  fd_set initial;
  FD_ZERO(&initial);
  FD_SET(sock_udp,&initial);
  FD_SET(sock_tcp, &initial);
  FD_SET(sock_multi_diff, &initial);
  while(1){
    waitAMssg();
    fd_set rdfs;
    int fd_max = max(sock_tcp,sock_udp,sock_multi_diff);
    memcpy(&rdfs, &initial, sizeof(fd_set));
    int ret=select(fd_max+1, &rdfs, NULL, NULL, NULL);
    if(ret == -1){
      break;
    }
    while(ret > 0){
      if(FD_ISSET(sock_tcp, &rdfs)){
        entite = insertAnneauTCP(entite, sock_tcp);
        ret--;
        FD_CLR(sock_tcp, &rdfs);
      }
      if(FD_ISSET(sock_udp,&rdfs)){
        entite = receiveUDP(entite, sock_udp);
        ret--;
        FD_CLR(sock_udp, &rdfs);
      }
      if(FD_ISSET(sock_multi_diff,&rdfs)){
        printf("Quelque chose en multidiff");
        ret--;
        FD_CLR(sock_multi_diff, &rdfs);
      }
    }
  }
}
