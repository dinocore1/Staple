
.PHONY: all clean

all: test

clean:
	rm -f test libstp_runtime.a object.o

test: test.c libstp_runtime.a
	gcc -g -o test test.c libstp_runtime.a -I include

libstp_runtime.a: object.o src/object.c
	ar rcs libstp_runtime.a object.o

object.o: src/object.c
	gcc -c -g -o object.o -I include src/object.c


