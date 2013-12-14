
all: test

test: test.s
	gcc test.s -o test

test.s: test.ll
	llc -O3 test.ll


