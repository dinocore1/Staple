
CC := gcc
CXX := g++
LD := ld

EASYBAKEDIR := $(dir $(lastword $(MAKEFILE_LIST)))
TOP := $(abspath $(EASYBAKEDIR)..)
BUILDDIR := build


SILENT := @

DEFINE_MODULE := $(EASYBAKEDIR)module.mk
BUILD_LIBRARY := $(EASYBAKEDIR)library.mk
BUILD_EXE := $(EASYBAKEDIR)executable.mk
COMPILE := $(EASYBAKEDIR)compile.mk

my-dir = $(dir $(lastword $(MAKEFILE_LIST)))

define all-makefiles-under
$(wildcard $(1)/*/easybake.mk)
endef


############ C/C++ definitions ################

define compile
$(OBJ): $(SRC)
	$(SILENT) mkdir -p $(intermediateDir)
	$(COMPILER) -c -o $(OBJ) $(cflags) $(SRC)

-include $(DEPEND)

endef

define c_template
$(eval intermediateDir := $(BUILDDIR)/$(MODULE)/$(dir $(1)))
$(eval COMPILER := $(CC))
$(eval SRC := $(LOCAL_PATH)$(1))
$(eval OBJ := $(BUILDDIR)/$(MODULE)/$(1:.c=.o))
$(eval DEPEND := $(BUILDDIR)/$(MODULE)/$(1:.c=.d))
$(eval LOCAL_OBJS += $(OBJ))

$(eval $(call compile))
endef

define cpp_template
$(eval intermediateDir := $(BUILDDIR)/$(MODULE)/$(dir $(1)))
$(eval COMPILER := $(CXX))
$(eval SRC := $(LOCAL_PATH)$(1))
$(eval OBJ := $(BUILDDIR)/$(MODULE)/$(1:.cpp=.o))
$(eval DEPEND := $(BUILDDIR)/$(MODULE)/$(1:.cpp=.d))
$(eval LOCAL_OBJS += $(OBJ))

$(eval $(call make-intermediate-dir))
$(eval $(call compile))
endef

################################################

.PHONY: all clean

all: 

clean:
	rm -rf $(BUILDDIR)

include easybake.mk

all: $(ALL_MODULES)

