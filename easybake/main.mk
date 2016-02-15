
CC := gcc
CXX := g++
LLC := llc
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
	$(SILENT) echo "$(MODULE) <== $(SRC)"
	$(SILENT) mkdir -p $(intermediateDir)
	$(SILENT) $(COMPILER) -c -o $(OBJ) $(cflags) $(SRC)
	$(SILENT) $(COMPILER) -MM -MT $(OBJ) -o $(DEPEND) $(cflags) $(SRC) &> /dev/null

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

######## LLVM COMPILE #######

define llvm_compile
$(OBJ): $(SRC)
	$(SILENT) echo "$(MODULE) <== $(SRC)"
	$(SILENT) mkdir -p $(intermediateDir)
	$(SILENT) $(COMPILER) $(cflags) -o $(OBJ) $(SRC)

endef

define llvm_template
$(eval intermediateDir := $(BUILDDIR)/$(MODULE)/$(dir $(1)))
$(eval COMPILER := $(LLC))
$(eval SRC := $(LOCAL_PATH)$(1))
$(eval OBJ := $(BUILDDIR)/$(MODULE)/$(1:.ll=.o))
$(eval LOCAL_OBJS += $(OBJ))

$(eval $(call make-intermediate-dir))
$(eval $(call llvm_compile))
endef

######### STAPLE ##########

define stp_compile
$(LLVM_SRC): $(SRC)
	$(SILENT) mkdir -p $(intermediateDir)
	$(COMPILER) $(stpflags) -o $(LLVM_SRC) $(SRC)

$(OBJ): $(LLVM_SRC)
	$(SILENT) mkdir -p $(intermediateDir)
	$(LLC) -filetype=obj -o $(OBJ) $(LLVM_SRC)

endef

define stp_template
$(eval intermediateDir := $(BUILDDIR)/$(MODULE)/$(dir $(1)))
$(eval COMPILER := $(STPC))
$(eval SRC := $(LOCAL_PATH)$(1))
$(eval LLVM_SRC := $(BUILDDIR)/$(MODULE)/$(1:.stp=.ll))
$(eval OBJ := $(BUILDDIR)/$(MODULE)/$(1:.stp=.o))
$(eval LOCAL_OBJS += $(OBJ))

$(eval $(call make-intermediate-dir))
$(eval $(call stp_compile))

endef

ALL_CLEAN :=

################################################
.PHONY: all clean

all:
	echo "nothing"

include easybake.mk

all: $(ALL_MODULES)

clean: $(ALL_CLEAN)
	rm -rf $(BUILDDIR)
