
LOCAL_OUTPUT_DIR := $(OUT)/$(LOCAL_MODULE)
local_exefile := $(LOCAL_OUTPUT_DIR)/$(LOCAL_MODULE)

LCC_CFLAGS := 

OBJS := 

include $(call my-dir)/module.mk

$(eval $(call create-exe))

EXES += $(local_exefile)


