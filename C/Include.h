#ifndef __INCLUDE_H__
#define __INCLUDE_H__

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <sys/time.h>
#include <pthread.h>

#include <arpa/inet.h>
#include <fcntl.h>
#include <ifaddrs.h>
#include <net/if.h>
#include <netdb.h>
#include <unistd.h>

#ifdef DETAILED
#define INFO(s) printf("%s\n", s)
#define INFO2(s,t) INFO(s)
#else
#define INFO(s)
#define INFO2(s,t) INFO(s)
#endif

#endif
