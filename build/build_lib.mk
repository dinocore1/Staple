
#.PRECIOUS: %.s

LOCAL_OUTPUT_DIR := $(OUT)/$(LOCAL_MODULE)
LOCAL_LIBFILE := $(LOCAL_OUTPUT_DIR)/lib$(LOCAL_MODULE).so

LCC_CFLAGS := -relocation-model=pic

OBJS :=

include $(call my-dir)/module.mk

$(LOCAL_LIBFILE): $(OBJS)
	$(CC) -shared -Wl,-soname,$(notdir $(LOCAL_LIBFILE)) -o $(LOCAL_LIBFILE) $(OBJS)

LIBS += $(LOCAL_LIBFILE)


