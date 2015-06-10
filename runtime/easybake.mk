LOCAL_PATH := $(call my-dir)

include $(DEFINE_MODULE)
MODULE := stp_runtime
LLC := llc
STPC := ./stp
stpflags := -I $(LOCAL_PATH)src/stp
LOCAL_SRCS := \
    src/runtime.ll \
    src/cstdlib.stp \
    src/stp/org/stp/String.stp

include $(BUILD_LIBRARY)
