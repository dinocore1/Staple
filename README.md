Staple Programming Language 
============================

Staple is a general-purpose object-oriented programing language. Staple's focuses on being *simple*, __powerfull__, and fast.
Staple is designed to be a better alternative to C, C++, or Obj-C. Built using [LLVM technology](http://llvm.org/), Staple
code compiles directly to machine code for a growing number of architectures including x86/x86-64, ARM, MIPS, PowerPC, 
SPARC, and more. Unlike some languages, Staple has a very light-weight runtime with a small dependency set. As a 
result, Staple code can easily run on just about any operating system, or embedded environment. 

Staple is designed to "play nice" with libraries written in C. Calling a function written in C is as easy as declaring
its prototype with `extern` keyword and then calling it like a regular function.

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
    

Staple's syntax feels similar to Java or C++. If you are already comfortable with Java, you will feel right at home
with Staple.
    
    class LinkedList {
      uint size;
      Node* head;
      Node* tail;
      
      void add(obj data) {
        Node* newNode = new Node();
        newNode.data = data;
        head.next = newNode;
        head = newNode;
      }
      
    }
    
    class Node {
      Node* next;
      Node* prev;
      obj data;
    }
      
### Refernece Counting and ARC ###

Staple walks a fine balance between simplicity to program and minimal runtime requirements. The use of object reference
counting is one example of such a trade-off. Staple's compiler uses a technique called Automatic Reference Counting (ARC)
simular to the one available in Obj-C. This relieves the programmer from worrying about managing memory explicitly like they
would with a language like C or C++. 



### Compile ###

you need LLVM 3.6 to build Staple. On Ubuntu:

$ sudo apt-get install llvm-dev


### Test C Code ###

$ clang helloworld.c -S -emit-llvm -O0