Staple Programming Language 
============================

Staple is a fast, *simple*, and powerful Object-oriented programming language. Staple is designed as replacement 
for C or C++. Staple compiles to machine language and produces programs that run nearly as fast as assembly, 
just like C. Staple is also object-oriented, and unlike C++ or Obj-C, Staple has a user friendly syntax.


Staple can do simple stuff that you might use C for:

    int main() {
      int x = fib(12);
      printf("fib(12) = %d", x);
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
    

Staple has a multi-pass compiler, and there is no need to forward-declare your functions before you call them. Also, 
Staple is designed to "play nice" with libraries written in C. Calling a function written in C is as easy as declaring
its prototype with `extern` keyword and then calling it like a regular function.

    
    class LinkedList {
      uint size;
      Node head;
      Node tail;
      
      void add(obj data) {
        Node newNode = new Node();
        newNode.data = data;
        head.next = newNode;
        head = newNode;
      }
      
    }
    
    class Node {
      Node next;
      Node prev;
      obj data;
    }
      

### Compile ###

you need LLVM 3.6 to build Staple. On Ubuntu:

$ sudo apt-get install llvm-dev


### Test C Code ###

$ clang helloworld.c -S -emit-llvm -O0