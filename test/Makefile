.PHONY: all clean

STPC := ../build/compiler/stp

all: test

clean:
	rm -f output.o output.ll test

output.ll: devsmart/test1.stp
	$(STPC) -I .. -o $@ $<

output.o: output.ll
	llc -O0 -filetype=obj -o $@ $<

test: output.o
	gcc -o $@ $<