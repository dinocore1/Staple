Staple Runtime ABI
===================

Staple has a very simple representation of it's object instances in runtime. 

## Object Instance Struct ##


```
stp_obj = type {
  stp_class* = class struct,
  uint32 = reference count 
  ...  other members
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