Staple Programming Language
============================

Staple is a general-purpose object-oriented programming language. Staple's focuses on being *simple*, __powerful__, and fast.
Staple is designed to be a better alternative to C, C++, or Obj-C. Built using [LLVM technology](http://llvm.org/), Staple
code compiles directly to machine code for many computer architectures including x86/x86-64, ARM, MIPS, PowerPC,
and SPARC. Staple has a very light-weight runtime and can be easily ported to any operating system, or embedded environment.

Staple's syntax feels similar to Java or C++. If you are already comfortable with Java, you will feel right at home
with Staple.

    class LinkedList {
      int size;
      Node* head;
      Node* tail;

      void add(obj* data) {
        Node* newNode = new Node;
        newNode.data = data;
        tail.next = newNode;
        tail = newNode;
        size = size + 1;
      }

    }

    class Node {
      Node* next;
      Node* prev;
      obj* data;
    }


Staple is designed to "play nice" with libraries written in C. Calling a function written in C is as easy as declaring
its prototype with `extern` keyword and then calling it like a regular function.

    int main(int argc, uint8** argv) {
      int x = atoi(argv@1);
      int y = fib(x);
      printf("fib(%d) = %d", x, y);
      return 0;
    }

    int fib(int x) {
      if(x == 0) {
        return 0;
      } else if(x == 1) {
        return 1;
      } else {
        return fib(x-1) + fib(x-2);
      }
    }

    extern int printf(uint8*, ...)
    extern int atoi(uint8*)


### Reference Counting and ARC ###

Staple uses reference counting for tracking object allocated on the heap. Staple's compiler automatically inserts
retain and release calls similar to the Automatic Reference Counting (ARC) feature available in Obj-C. This relieves
the programmer from worrying about managing memory explicitly.


### Compile a Staple Program ###


    $ ./stp -g test.stp
    $ llc -O0 -filetype=obj -o output.o output.ll
    $ gcc -o test output.o build/stp_runtime/stp_runtime.a
    $ ./test


### Build the Staple Compiler ###

Build Dependencies:
* g++ or clang (support for C++ 11/14 or greater)
* LLVM 3.5 (apt-get install llvm-dev)
* Bison (apt-get install bison)
* Flex (apt-get install flex)



### Test C Code ###

$ clang helloworld.c -S -emit-llvm -O0
