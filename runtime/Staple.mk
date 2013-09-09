
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := stapleruntime
LOCAL_INCLUDE := $(LOCAL_PATH)/include
LOCAL_SRC := dispatch.stp alloc.stp
include $(BUILD_LIB)


include $(CLEAR_VARS)
LOCAL_MODULE := stapleruntimetest
LOCAL_INCLUDE := $(LOCAL_PATH)/include
LOCAL_STATIC_LIBS := stapleruntime
LOCAL_SRC := test.stp
include $(BUILD_EXE)