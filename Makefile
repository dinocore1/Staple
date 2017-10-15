
include easybake/main.mk


test.ll: test.stp
	./stp test.stp > test.ll

test.o: test.ll
	llc -filetype=obj $<

test1: test.o
	gcc $< -o $@