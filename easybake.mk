

src/parser.cpp: src/parser.y
	bison -d -o $@ $^

src/parser.hpp: src/parser.cpp

src/tokens.cpp: src/tokens.l src/parser.hpp
	lex -o $@ $^

LOCAL_PATH := $(call my-dir)

include $(DEFINE_MODULE)
CC := gcc-4.9
CXX := g++-4.9
MODULE := compiler
LOCAL_CXXFLAGS := $(shell llvm-config-3.6 --cxxflags --system-libs --libs core)
LOCAL_SRCS := \
	src/tokens.cpp \
	src/parser.cpp \
	src/codegen.cpp \
	src/main.cpp 

LOCAL_CLEAN := \
	src/parser.cpp \
	src/parser.hpp \
	src/tokens.cpp
	
LOCAL_LIBS := $(shell llvm-config-3.6 --ldflags --system-libs --libs core)
include $(BUILD_EXE)
