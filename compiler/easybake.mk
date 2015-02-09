

src/parser.cpp: src/parser.y
	bison -d -o $@ $^

src/parser.hpp: src/parser.cpp

src/tokens.cpp: src/tokens.l src/parser.hpp
	lex -o $@ $^

LOCAL_PATH := $(call my-dir)

include $(DEFINE_MODULE)
CC := gcc-4.8
CXX := g++-4.8
MODULE := stp
LOCAL_CXXFLAGS := -std=c++11 $(shell llvm-config-3.5 --cxxflags --system-libs --libs all)
LOCAL_SRCS := \
    src/compilercontext.cpp \
	src/tokens.cpp \
	src/parser.cpp \
	src/codegen.cpp \
	src/sempass.cpp \
	src/type.cpp \
	src/main.cpp 

LOCAL_CLEAN := \
	src/parser.cpp \
	src/parser.hpp \
	src/tokens.cpp
	
LOCAL_LIBS := $(shell llvm-config-3.5 --ldflags --system-libs --libs all)
include $(BUILD_EXE)
