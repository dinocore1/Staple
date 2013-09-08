
#.PRECIOUS: %.s

LOCAL_OUTPUT_DIR := $(OUT)/$(LOCAL_MODULE)
local_shared_libfile := $(LOCAL_OUTPUT_DIR)/lib$(LOCAL_MODULE).so
local_static_libfile := $(LOCAL_OUTPUT_DIR)/lib$(LOCAL_MODULE).a

LCC_CFLAGS := -relocation-model=pic

OBJS :=

include $(call my-dir)/module.mk

$(eval $(call objs-to-a))

$(eval $(call a-to-so))

LIBS += $(local_shared_libfile) $(local_static_libfile)


