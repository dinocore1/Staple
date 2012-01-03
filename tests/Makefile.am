
COMPILER = $(top_builddir)/compiler/stpc

check_PROGRAMS = call

call_SOURCES = call.s common_start.c

.stp.llvm:
	java -cp $(top_srcdir)/compiler/:$(top_srcdir)/libs/antlr-3.4-complete.jar CC -o $@ $< -template C.stg

.llvm.s:
	llc -o $@ $<
	
CLEANFILES = *.llvm *.o
	
