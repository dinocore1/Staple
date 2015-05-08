
%stp_obj_vtable = type { void (%stp_obj*)* }
%stp_class = type { i8*, %stp_class*, %stp_obj_vtable }
%stp_obj = type { %stp_class*, i32 }


@stp_obj_className = private constant [8 x i8] c"stp_obj\00"
@stp_class_def = constant %stp_class {
  i8* getelementptr inbounds ([8 x i8]* @stp_obj_className, i32 0, i32 0),
  %stp_class* null,
  %stp_obj_vtable { void (%stp_obj*)* @stp_obj_kill }
}

define void @stp_obj_init(%stp_obj* %obj) {
  %intval = ptrtoint %stp_obj* %obj to i32
  %1 = icmp eq i32 0, %intval
  br i1 %1, label %finish, label %begin
begin:
  %2 = getelementptr %stp_obj* %obj, i32 0, i32 1
  store i32 0, i32* %2
  br label %finish
finish:
  ret void
}

define void @stp_obj_kill(%stp_obj* %obj) {
  ret void
}

define void @stp_release(%stp_obj* %value) {
  %intval = ptrtoint %stp_obj* %value to i32
  %1 = icmp eq i32 0, %intval
  br i1 %1, label %finish, label %begin
begin:
  %2 = getelementptr %stp_obj* %value, i32 0, i32 1
  %old = atomicrmw sub i32* %2, i32 1 acq_rel
  %cond = icmp eq i32 1, %old
  br i1 %cond, label %destroy, label %finish
destroy:
  %3 = getelementptr %stp_obj* %value, i32 0, i32 0
  %4 = load %stp_class** %3
  %5 = getelementptr %stp_class* %4, i32 0, i32 2, i32 0
  %6 = load void (%stp_obj*)** %5
  call void %6(%stp_obj* %value)
  br label %finish
finish:
  ret void
}

define void @stp_retain(%stp_obj* %value) {
  %1 = getelementptr %stp_obj* %value, i32 0, i32 1
  %old = atomicrmw add i32* %1, i32 1 acq_rel
  ret void
}

define void @stp_storeStrong(%stp_obj** %dest, %stp_obj* %value) {
  call void @stp_retain(%stp_obj* %value)
  %1 = load %stp_obj** %dest
  call void @stp_release(%stp_obj* %1)
  store %stp_obj* %value, %stp_obj** %dest
  ret void
}