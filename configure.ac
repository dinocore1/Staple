AC_PREREQ(2.59)
AC_INIT(staple, 0.1, paul@dev-smart.com)

AC_CONFIG_MACRO_DIR([m4])

AC_CANONICAL_SYSTEM # create a cross platform build system
AM_INIT_AUTOMAKE # init automake

AC_ARG_ENABLE(debug,
AS_HELP_STRING([--enable-debug],
               [enable debugging, default: no]),
[case "${enableval}" in
             yes) debug=true ;;
             no)  debug=false ;;
             *)   AC_MSG_ERROR([bad value ${enableval} for --enable-debug]) ;;
esac],
[debug=false])

AM_CONDITIONAL(DEBUG, test x"$debug" = x"true")


AC_CHECK_HEADERS(stdint.h stdlib.h string.h)
AC_CHECK_FUNCS(malloc free memcpy)

AC_DEFINE([SAFTY_CHECK], [1], [Description])

AM_CONFIG_HEADER(include/config.h:include/config.in) #setup config header

AC_PROG_CC #check for C compiler
AM_PROG_AS
AM_PROG_GCJ
LT_INIT


#output makefile
AC_CONFIG_FILES([
	Makefile 
	compiler/Makefile
	runtime/Makefile
	tests/Makefile
	])
AC_OUTPUT