$(eval ALL_MODULES += $(MODULE))

LOCAL_C_SOURCES := $(filter %.c,$(LOCAL_SRCS))
LOCAL_CPP_SOURCES := $(filter %.cpp,$(LOCAL_SRCS))

cflags := $(LOCAL_CFLAGS) $(CFLAGS) -MMD -fPIC
$(foreach file,$(LOCAL_C_SOURCES),$(eval $(call c_template,$(file))))

cflags := $(LOCAL_CXXFLAGS) -MMD -fPIC
$(foreach file,$(LOCAL_CPP_SOURCES),$(eval $(call cpp_template,$(file))))

SHARED_LIB := $(BUILDDIR)/$(MODULE)/lib$(MODULE).so
STATIC_LIB := $(BUILDDIR)/$(MODULE)/$(MODULE).a

define shared_lib_template
$(SHARED_LIB): $(intermediateDirObj) $(LOCAL_OBJS)
	$$(LD) -shared -soname lib$(MODULE).so -o $(SHARED_LIB) $(LOCAL_OBJS)
endef

$(eval $(call shared_lib_template))

define static_lib_template
$(STATIC_LIB): $(intermediateDirObj) $(LOCAL_OBJS)
	$$(AR) rcs $(STATIC_LIB) $(LOCAL_OBJS) 
endef

$(eval $(call static_lib_template))

$(MODULE): $(SHARED_LIB) $(STATIC_LIB)
