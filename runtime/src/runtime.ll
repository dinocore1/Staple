
%stp_obj_vtable = type { void (%stp_obj*)*, void (%stp_obj*)* }
%stp_class = type { i8*, %stp_class*, %stp_obj_vtable }
%stp_obj = type { %stp_class*, i32 }

define void @stp_release(%stp_obj* %value) {
  %1 = getelementptr %stp_obj* %value, i32 0, i32 1
  %old = atomicrmw sub i32* %1, i32 1 acq_rel
  %cond = icmp eq i32 1, %old
  br i1 %cond, label %destroy, label %finish
destroy:
  %2 = getelementptr %stp_obj* %value, i32 0, i32 0
  %3 = load %stp_class** %2
  %4 = getelementptr %stp_class* %3, i32 0, i32 2, i32 1
  %5 = load void (%stp_obj*)** %4
  call void %5(%stp_obj* %value)
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
  %1 = getelementptr %stp_obj** %dest, i32 0
  %2 = load %stp_obj** %1
  call void @stp_release(%stp_obj* %2)
  store %stp_obj* %value, %stp_obj** %1
  ret void
}