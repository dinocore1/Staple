[![Build Status](https://travis-ci.org/dinocore1/Staple.svg?branch=master)](https://travis-ci.org/dinocore1/Staple)

Staple Programming Language
============================

Staple is a general-purpose object-oriented programming language with a focus on runtime speed and simple to compile and debug. Built using [LLVM technology](http://llvm.org/), Staple code compiles directly to machine code for many computer architectures including x86/x86-64, ARM, MIPS, PowerPC, and SPARC. Staple has a very light-weight runtime and can be easily ported to any operating system, or embedded environment. 

Staple's syntax feels similar to Java or C++. If you are already comfortable with Java, you will feel right at home with Staple.

```
void printf(i8* fmt, ...);
int atoi(i8* str);

int fib(int x) {
  if(x == 0) {
    return 0;
  } else if(x == 1) {
    return 1;
  } else if(x == 2) {
    return 1;
  } else {
    return fib(x-2) + fib(x-1);
  }
}

int main(int argc, i8** argv) {
  int x;
  if(argc < 2) {
    printf("first parameter missing\n");
    return 1;
  } else {
    x = atoi(argv[1]);
    printf("fib(%d) = %d\n", x, fib(x));
    return 0;
  }
}
```

Staple is designed to "play nice" with libraries written in C. All you have to do is forward-delcare it in your code by adding: 
```
void printf(i8* fmt, ...);
```

### Compile a Staple Program ###

```
$ ./stp -o output.ll test.stp
$ llc -O0 -filetype=obj -o output.o output.ll
$ gcc -o test output.o
$ ./test
```

### Build the Staple Compiler ###

Build Dependencies:
* g++ or clang (support for C++ 11/14 or greater)
* LLVM 8.0+ (apt-get install llvm-dev)
* Bison (apt-get install bison)
* Flex (apt-get install flex)

install dependencies on Ubuntu:
```
$ sudo apt-get install cmake g++ llvm-dev bison flex libz-dev
$ mkdir build
$ cd build
$ cmake -DLLVM_DIR=`llvm-config --cmakedir` ..
$ make
```


### Test C Code ###
```
$ llc -march=cpp helloworld.c
$ clang helloworld.c -S -emit-llvm -O0
```