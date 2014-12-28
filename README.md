

### Compile ###

you need LLVM 3.6 to build Staple. On Ubuntu:

$ sudo apt-get install llvm-dev


### Test C Code ###

$ clang helloworld.c -S -emit-llvm -O0