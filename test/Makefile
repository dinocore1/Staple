.PHONY: all clean

STPC := ../build/compiler/stp

all: test

clean:
	rm -f *.o *.ll test

fib.ll: fib.stp
	$(STPC) -I .. -o $@ $<

fib.o: fib.ll
	llc -O0 -filetype=obj -o $@ $<

fib: fib.o
	gcc -o $@ $<

test1.ll: devsmart/test1.stp
	$(STPC) -I .. -o $@ $<

test1.o: test1.ll
	llc -O0 -filetype=obj -o $@ $<

test: test1.o
	gcc -o $@ $<