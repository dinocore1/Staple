
.PRECIOUS: %.s

LIBFILE := lib$(LOCAL_LIB).so
OBJS := $(addprefix $(LOCAL_PATH)/,$(LOCAL_OBJS))

LCC_CFLAGS := -relocation-model=pic

$(LIBFILE): $(OBJS)
	gcc -shared -Wl,-soname,$@ -o $@ $<

LIBS += $(LIBFILE)

