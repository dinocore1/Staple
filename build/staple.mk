
SILENT?=@




define my-dir
$(subst $(ROOT)/,,$(strip \
  $(patsubst %/,%,$(dir $(lastword $(MAKEFILE_LIST))))
))
endef

include $(call my-dir)/definitions.mk

current-makefiles = $(CURDIR)/$(word $(words $(MAKEFILE_LIST)),$(MAKEFILE_LIST))

MAKEFILES := $(wildcard $(ROOT)/*/Staple.mk)

CLEAR_VARS := $(call my-dir)/clear_vars.mk
BUILD_LIB := $(call my-dir)/build_lib.mk
BUILD_EXE :=  $(call my-dir)/build_exe.mk

LIBS := 
EXES :=

include $(MAKEFILES)

all: $(LIBS) $(EXES)
