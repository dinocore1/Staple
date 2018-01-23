
include easybake/main.mk


test.ll: test.stp
	./stp -o test.ll test.stp

test.o: test.ll
	llc -filetype=obj -relocation-model=pic $<

test1: test.o
	gcc $< -o $@