
LOCAL_PATH := $(call my-dir)

$(LOCAL_PATH)src/parser.cpp: $(LOCAL_PATH)src/parser.y
	bison -d -o $@ $^

$(LOCAL_PATH)src/parser.hpp: $(LOCAL_PATH)src/parser.cpp

$(LOCAL_PATH)src/tokens.cpp: $(LOCAL_PATH)src/tokens.l $(LOCAL_PATH)src/parser.hpp
	lex -o $@ $^


include $(DEFINE_MODULE)
CC := gcc
CXX := g++
MODULE := stp
LOCAL_CXXFLAGS := -std=c++11 $(shell llvm-config --cxxflags)
LOCAL_SRCS := \
	src/tokens.cpp \
	src/parser.cpp \
	src/sempass.cpp \
	src/compilercontext.cpp \
	src/types/stapletype.cpp \
	src/codegen/LLVMCodeGenerator.cpp \
	src/codegen/LLVMStapleObject.cpp \
	src/main.cpp 

LOCAL_CLEAN := \
	src/parser.cpp \
	src/parser.hpp \
	src/tokens.cpp
	
LOCAL_LIBS := $(shell llvm-config --ldflags --libs) -ltinfo -ldl
include $(BUILD_EXE)

$(LOCAL_CPP_SOURCES): $(LOCAL_PATH)src/tokens.cpp $(LOCAL_PATH)src/parser.hpp $(LOCAL_PATH)src/parser.cpp