
all: test

test: test.s
	gcc -g test.s -o test

test.s: test.ll
	llc -O0 test.ll


