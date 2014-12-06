#include <staple/staple.h>

typedef struct _org_staple_Node org_staple_Node;
//********* Header ***********
typedef struct _org_staple_LinkedListClass org_staple_LinkedListClass;
struct _org_staple_LinkedListClass {
  stp_objectClass parent;
  void (*add)(void* self, stp_object* obj);
  void (*deleteFirst)(void* self);
};
#define CAST_org_staple_LinkedListClass(x) ((org_staple_LinkedListClass*)x)

typedef struct _org_staple_LinkedList org_staple_LinkedList;
struct _org_staple_LinkedList {
  stp_object parent;
  uint32 size;
  org_staple_Node* head;
  org_staple_Node* tail;
};

#define CAST_org_staple_LinkedList(x) ((org_staple_LinkedList*)x)
void org_staple_LinkedList_add(void* self, stp_object* obj);
void org_staple_LinkedList_deleteFirst(void* self);
void org_staple_LinkedList_init(void* self);
void org_staple_LinkedList_dest(void* self);
extern org_staple_LinkedListClass LinkedListClassObj;

//********* Code *************
typedef struct _org_staple_NodeClass org_staple_NodeClass;
struct _org_staple_NodeClass {
  stp_objectClass parent;
};

typedef struct _org_staple_Node org_staple_Node;
struct _org_staple_Node {
  stp_object parent;
  org_staple_Node* next;
  org_staple_Node* previous;
  stp_object* data;
};

#define CAST_org_staple_Node(x) ((org_staple_Node*)x)
void org_staple_Node_init(void* self);
void org_staple_Node_dest(void* self);
org_staple_LinkedListClass org_staple_LinkedListClassObj = {
  "org_staple_LinkedList",
  &stp_objectClassObj,
  org_staple_LinkedList_init,
  org_staple_LinkedList_dest,
  org_staple_LinkedList_add,
  org_staple_LinkedList_deleteFirst
};
org_staple_NodeClass org_staple_NodeClassObj = {
  "org_staple_Node",
  &stp_objectClassObj,
  org_staple_Node_init,
  org_staple_Node_dest
};
void org_staple_Node_init(void* self)
{
  {
    stp_object_init(CAST_org_staple_Node(self));
    OBJ_ASSIGN_S(CAST_org_staple_Node(self)->next, NULL);
    OBJ_ASSIGN_S(CAST_org_staple_Node(self)->previous, NULL);
    OBJ_ASSIGN_S(CAST_org_staple_Node(self)->data, NULL);
  }
}

void org_staple_Node_dest(void* self)
{
  {
    OBJ_ASSIGN_S(CAST_org_staple_Node(self)->next, NULL);
    OBJ_ASSIGN_S(CAST_org_staple_Node(self)->previous, NULL);
    OBJ_ASSIGN_S(CAST_org_staple_Node(self)->data, NULL);
    stp_object_dest(CAST_org_staple_Node(self));
  }
}

void org_staple_LinkedList_add(void* self, stp_object* obj)
{
  {
    org_staple_Node* newNode = NULL;
    OBJ_ASSIGN_S(newNode, CREATE_OBJ(org_staple_Node));
    OBJ_ASSIGN_S(newNode->data, obj);
    OBJ_ASSIGN_S(newNode->previous, CAST_org_staple_LinkedList(self)->head);
    OBJ_ASSIGN_S(CAST_org_staple_LinkedList(self)->head->next, newNode);
    OBJ_ASSIGN_S(CAST_org_staple_LinkedList(self)->head, newNode);
    CAST_org_staple_LinkedList(self)->size++;
  }
}

void org_staple_LinkedList_deleteFirst(void* self)
{
  {
    if((CAST_org_staple_LinkedList(self)->head!=NULL)){
      OBJ_ASSIGN_S(CAST_org_staple_LinkedList(self)->head->previous, NULL);
      OBJ_ASSIGN_S(CAST_org_staple_LinkedList(self)->head, NULL);
      CAST_org_staple_LinkedList(self)->size--;
    }
  }
}

void org_staple_LinkedList_init(void* self)
{
  {
    stp_object_init(CAST_org_staple_LinkedList(self));
    OBJ_ASSIGN_S(CAST_org_staple_LinkedList(self)->head, NULL);
    OBJ_ASSIGN_S(CAST_org_staple_LinkedList(self)->tail, NULL);
  }
}

void org_staple_LinkedList_dest(void* self)
{
  {
    OBJ_ASSIGN_S(CAST_org_staple_LinkedList(self)->head, NULL);
    OBJ_ASSIGN_S(CAST_org_staple_LinkedList(self)->tail, NULL);
    stp_object_dest(CAST_org_staple_LinkedList(self));
  }
}

int main(int argv, char** argc)
{
  org_staple_LinkedList* list = CREATE_OBJ(org_staple_LinkedList);

  stp_object* obj1 = CREATE_OBJ(stp_object);
  CAST_org_staple_LinkedListClass(list->parent.classType)->add(list, obj1);
}