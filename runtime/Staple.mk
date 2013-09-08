
LOCAL_PATH := $(call my-dir)

LOCAL_MODULE := stapleruntime
LOCAL_INCLUDE := $(LOCAL_PATH)/include
LOCAL_SRC := alloc.stp

include $(BUILD_LIB)
