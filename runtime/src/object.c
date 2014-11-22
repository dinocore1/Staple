
#include <staple/object.h>

stp_objectClass stp_objectClassObj = {
  "object",
  NULL,
  stp_object_init,
  stp_object_dest
};

void stp_object_init(void* self)
{
  stp_object* thiz = (stp_object*)self;
  thiz->classType = &stp_objectClassObj;
  thiz->refCount = 1;
}

void stp_object_dest(void* self)
{
}

void stp_object_addref(void* obj)
{
  __sync_add_and_fetch(&((stp_object*) obj)->refCount, 1);
}