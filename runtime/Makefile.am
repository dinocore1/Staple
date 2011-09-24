

CFLAGS = -g3 -O0
CXXFLAGS = -g3 -O0
CCASFLAGS = -g3 -O0


lib_LTLIBRARIES = libstapleruntime.la libutils.la

libstapleruntime_la_SOURCES = \
	staple.c staple.h \
	gc.c gc.h \
	main.c
	
libutils_la_SOURCES = \
	utils/linkedlist.c utils/linkedlist.h
	
check_PROGRAMS = test_gc

test_gc_SOURCES = tests/test_gc.c
test_gc_LDADD = libstapleruntime.la libutils.la
test_gc_LDFLAGS = -all-static


	
	
