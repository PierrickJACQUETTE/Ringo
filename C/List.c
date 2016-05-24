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

bool list_search(List* list, Mssg* data){
  List* tmp = list;
  while (tmp){
    if(tmp->data !=NULL){
      if(strcmp(tmp->data->idm, data->idm) == 0){
        return true;
      }
    }
    tmp = tmp->next;
  }
  return false;
}

List* list_changeTest(List* list, Mssg* data){
  List* tmp = list;
  while (tmp){
    if(tmp->data !=NULL){
      if(strcmp(tmp->data->idm, data->idm) == 0){
        tmp->data->isTest = false;
        break;
      }
    }
    tmp = tmp->next;
  }
  return list;
}

void list_print(List* list){
  List* tmp = list;
  int i = 0;
  while (tmp){
    if(tmp->data !=NULL){
      printf("%d : %s\n", i, tmp->data->idm);
      i++;
    }
    tmp = tmp->next;
  }
}

void list_removeOne(List* list, Mssg* m){
  List* current = list->next;
  List* previous = list;
  while (current != NULL && previous != NULL) {
    if (strcmp(m->idm, current->data->idm) == 0) {
      List * temp = current;
      previous->next = current->next;
      free(temp);
      return;
    }
    previous = current;
    current = current->next;
  }
  return;
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
