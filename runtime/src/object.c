
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
  thiz->classType = &objectClass;
  thiz->refCount = 1;
}

void stp_object_dest(void* self)
{
}