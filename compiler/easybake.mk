
LOCAL_PATH := $(call my-dir)

$(LOCAL_PATH)src/staple_parser.cpp: $(LOCAL_PATH)src/staple_parser.y
	bison -d -o $@ $<

$(LOCAL_PATH)src/staple_parser.hpp: $(LOCAL_PATH)src/staple_parser.cpp

$(LOCAL_PATH)src/staple_lex.cpp: $(LOCAL_PATH)src/staple_lex.l $(LOCAL_PATH)src/staple_parser.hpp
	flex -o $@ $<

.PHONY: parser

define parserrule
parser:
	flex -o $(LOCAL_PATH)src/staple_lex.cpp $(LOCAL_PATH)src/staple_lex.l
	bison -d -o $(LOCAL_PATH)src/staple_parser.cpp $(LOCAL_PATH)src/staple_parser.y
endef

$(eval $(parserrule))

include $(DEFINE_MODULE)
MODULE := stp
LOCAL_CXXFLAGS := -std=c++11 -I$(LOCAL_PATH)src/ $(shell llvm-config-3.5 --cxxflags)
LOCAL_SRCS := \
	src/staple_lex.cpp \
	src/staple_parser.cpp \
	src/main.cpp

LOCAL_CLEAN := \
	src/staple_lex.cpp \
	src/staple_parser.hpp \
	src/staple_parser.cpp
	
LOCAL_LIBS := $(shell llvm-config-3.5 --ldflags --libs) -ltinfo -ldl -pthread
include $(BUILD_EXE)

