AC_PREREQ(2.59)
AC_INIT(staple, 0.1, paul@dev-smart.com)

AC_CANONICAL_SYSTEM # create a cross platform build system
AM_INIT_AUTOMAKE # init automake

AC_PROG_CC #check for C compiler
AM_PROG_AS
AM_PROG_GCJ
AC_PROG_RANLIB

#output makefile
AC_CONFIG_FILES([
	Makefile 
	compiler/Makefile
	tests/Makefile
	])
AC_OUTPUT