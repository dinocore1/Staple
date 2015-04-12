
LOCAL_PATH := $(call my-dir)

$(LOCAL_PATH)src/parser.cpp: $(LOCAL_PATH)src/parser.y
	bison -d -o $@ $^

$(LOCAL_PATH)src/parser.hpp: $(LOCAL_PATH)src/parser.cpp

$(LOCAL_PATH)src/tokens.cpp: $(LOCAL_PATH)src/tokens.l $(LOCAL_PATH)src/parser.hpp
	lex -o $@ $^


include $(DEFINE_MODULE)
CC := gcc-4.8
CXX := g++-4.8
MODULE := stp
LOCAL_CXXFLAGS := -std=c++11 $(shell llvm-config-3.5 --cxxflags --system-libs --libs all)
LOCAL_SRCS := \
	src/tokens.cpp \
	src/parser.cpp \
	src/sempass.cpp \
	src/codegen.cpp \
	src/compilercontext.cpp \
	src/types/stapletype.cpp \
	src/main.cpp 

LOCAL_CLEAN := \
	src/parser.cpp \
	src/parser.hpp \
	src/tokens.cpp
	
LOCAL_LIBS := $(shell llvm-config-3.5 --ldflags --system-libs --libs all)
include $(BUILD_EXE)
