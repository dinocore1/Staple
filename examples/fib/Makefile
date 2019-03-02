

STPC := ../../build/compiler/stp

.PHONY: clean

fib: fib.o
	gcc $< -o $@

fib.ll: fib.stp
	$(STPC) -o fib.ll fib.stp

fib.o: fib.ll
	llc -filetype=obj -relocation-model=pic $<

clean:
	rm -rf fib fib.o fib.ll