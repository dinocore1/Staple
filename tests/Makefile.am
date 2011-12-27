
COMPILER = $(top_builddir)/compiler/stpc

check_PROGRAMS = call

call_SOURCES = call.s common_start.c

.stp.llvm:
	LD_LIBRARY_PATH=$(top_builddir)/compiler:$$LD_LIBRARY_PATH $(COMPILER) -o $@ $< 

.llvm.s:
	llc -o $@ $<
	
CLEANFILES = *.llvm *.o
	
