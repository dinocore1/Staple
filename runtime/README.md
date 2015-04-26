Staple Runtime ABI
===================

Staple has a very simple representation of it's object instances in runtime. 

## Object Instance Struct ##


```
stp_obj = type {
  stp_obj_header = header,
  uint32 = header offset, <-- heightest derived class
  stp_class* = class,
  ... Foo fields
  uint32 = header offset, <-- derived class
  stp_class* = class,
  ...  more fields
}


stp_obj_header = type {
  uint32 = reference counter
}

```

## Class Struct ##

```
stp_class = type {
  i8* = className,
  stp_class* = parent class | null if no parent
  stp_obj_vtable = virtual function table
}

```

## Virtual Function Table Struct ##

Each class type has its own vtable that hold its virtual functions. The first entry in the vtable is always
the destructor function. 

```
stp_obj_vtable = type {
  void(stp_obj*)* = object destructor function,
  ...
}
```