LOCAL_PATH := $(call my-dir)

include $(DEFINE_MODULE)
MODULE := stp_runtime
LLC := llc-3.5
LOCAL_SRCS := \
    src/runtime.ll

include $(BUILD_LIBRARY)
