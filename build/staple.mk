
%.ll : %.stp
	java -jar $(STP_JAR) $< > $@

%.s : %.ll
	llc $(LCC_CFLAGS) $< -o $@

%.o : %.s
	gcc -c $< -o $@


define my-dir
$(strip \
  $(patsubst %/,%,$(dir $(lastword $(MAKEFILE_LIST))))
)
endef

current-makefiles = $(CURDIR)/$(word $(words $(MAKEFILE_LIST)),$(MAKEFILE_LIST))

MAKEFILES := $(wildcard $(ROOT)/*/Staple.mk)

BUILD_LIB := $(call my-dir)/build_lib.mk

LIBS := 

include $(MAKEFILES)

all: $(LIBS)
