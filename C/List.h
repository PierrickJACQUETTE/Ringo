#ifndef __LIST_H__
#define __LIST_H__

#include "Core.h"

List* list_create (Mssg *data);
List* list_add(List *list, Mssg *data);
bool list_search(List *list,Mssg *data);
void list_removeOne(List* list, Mssg* m);
void list_print(List *list);
void list_destroy(List *list);

#endif
