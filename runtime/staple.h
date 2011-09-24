
#include <config.h>

#ifdef HAVE_STDINT_H
#include <stdint.h>
#endif

#ifdef HAVE_STDLIB_H
#include <stdlib.h>
#endif

#ifdef HAVE_STRING_H
#include <string.h>
#endif

#ifndef NULL
#define NULL 0
#endif

#ifndef BOOL
#define BOOL char
#endif

#ifndef TRUE
#define TRUE 1
#endif

#ifndef FALSE
#define FALSE 0
#endif

#ifndef MAX
#define MAX(a,b) (((a) > (b)) ? (a) : (b))
#endif

#ifndef MIN
#define MIN(a,b) (((a) > (b)) ? (b) : (a))
#endif

#define STP_ALLOC malloc
#define STP_FREE free



