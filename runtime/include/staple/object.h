#ifndef STP_OBJECT_H_
#define STP_OBJECT_H_

#include <staple/type.h>

typedef struct _stp_objectClass stp_objectClass;
struct _stp_objectClass {
  uint8* name;
  stp_objectClass* parent;
  void (*init)(void* self);
  void (*dest)(void* self);
};

void stp_object_init(void* self);
void stp_object_dest(void* self);

typedef struct _stp_object stp_object;
struct _stp_object {
  stp_objectClass* classType;
  uint32 refCount;
};

#endif /* STP_OBJECT_H_ */