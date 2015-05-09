
%obj_vtable = type { void (%obj*)* }
%obj_class = type { i8*, %obj_class*, %obj_vtable }
%obj = type { %obj_class*, i32 }


@obj_className = private constant [4 x i8] c"obj\00"
@obj_class_def = constant %obj_class {
  i8* getelementptr inbounds ([4 x i8]* @obj_className, i32 0, i32 0),
  %obj_class* null,
  %obj_vtable { void (%obj*)* @obj_kill }
}

define void @obj_init(%obj* %o) {
  %refcountptr = getelementptr %obj* %o, i32 0, i32 1
  store i32 0, i32* %refcountptr
  ret void
}

define void @obj_kill(%obj* %obj) {
  ret void
}

define void @stp_release(%obj* %value) {
  %intval = ptrtoint %obj* %value to i32
  %1 = icmp eq i32 0, %intval
  br i1 %1, label %finish, label %begin
begin:
  %2 = getelementptr %obj* %value, i32 0, i32 1
  %old = atomicrmw sub i32* %2, i32 1 acq_rel
  %cond = icmp eq i32 1, %old
  br i1 %cond, label %destroy, label %finish
destroy:
  %3 = getelementptr %obj* %value, i32 0, i32 0
  %4 = load %obj_class** %3
  %5 = getelementptr %obj_class* %4, i32 0, i32 2, i32 0
  %6 = load void (%obj*)** %5
  call void %6(%obj* %value)
  br label %finish
finish:
  ret void
}

define void @stp_retain(%obj* %value) {
  %1 = getelementptr %obj* %value, i32 0, i32 1
  %old = atomicrmw add i32* %1, i32 1 acq_rel
  ret void
}

define void @stp_storeStrong(%obj** %dest, %obj* %value) {
  call void @stp_retain(%obj* %value)
  %1 = load %obj** %dest
  call void @stp_release(%obj* %1)
  store %obj* %value, %obj** %dest
  ret void
}