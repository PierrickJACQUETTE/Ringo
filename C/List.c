#include "List.h"

List * list_create (Mssg* data){
  List *list = (List*)malloc(sizeof(List));
  if (list ==NULL){
    fprintf(stderr,"Allocation impossible : %s\n","fonction list_create : List.h");
    exit(EXIT_FAILURE);
  }
  list->data = data;
  list->next = NULL;

  return list;
}

List* list_add(List* list, Mssg* data){
  List** plist = &list;
  while (*plist){
    plist = &(*plist)->next;
  }
  *plist = list_create(data);
  if (*plist){
    return list;
  }
  else{
    return NULL;
  }
}

bool list_search(List* list,Mssg* data){
  List* tmp = list;
  while (tmp){
    tmp = tmp->next;
  }
  return false;
}

void list_print(List* list){
  List* tmp = list;
  int i = 0;
  while (tmp){
    printf("%d : %s\n", i, tmp->data->idm);
    tmp = tmp->next;
    i++;
  }
}

List* list_remove_first(List *list){
  List* first = list;
  list = list->next;
  free(first);
  return list;
}

void list_destroy(List* list){
  while (list){
    list = list_remove_first(list);
  }
  free(list);
}
