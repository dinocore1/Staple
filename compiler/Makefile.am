


CLexer.java CParser.java C.tokens: C.g
	java -classpath $(top_srcdir)/libs/antlr-3.4-complete.jar org.antlr.Tool C.g
	
DefRef.g: C.tokens

libantlr.so: $(top_srcdir)/libs/antlr-3.4-complete.jar
	gcj -findirect-dispatch -fPIC -shared -o libantlr.so $(top_srcdir)/libs/antlr-3.4-complete.jar


bin_PROGRAMS = compiler
compiler_SOURCES = \
	DefRef.java \
	Gen.java \
	ArrayType.java \
	BaseScope.java \
	BuiltInTypeSymbol.java \
	CC.java \
	CErrorNode.java \
	ClassSymbol.java \
	ClassType.java \
	CLexer.java \
	CParser.java \
	CString.java \
	CTree.java \
	FunctionSymbol.java \
	GlobalScope.java \
	LocalScope.java \
	Scope.java \
	Symbol.java \
	SymbolTable.java \
	Type.java \
	VariableSymbol.java 
	 
compiler_GCJFLAGS = -I$(top_srcdir)/libs/antlr-3.4-complete.jar -I. -findirect-dispatch
compiler_LDFLAGS = -findirect-dispatch --main=CC
compiler_LDADD = libantlr.so llvm.o C.o
#compiler_LDADD = $(top_srcdir)/libs/antlr-3.4-complete.jar
	

.stg.o:
	$(GCJ) -c -o $@ --resource=$< $<	

.g.java: 
	java -classpath $(top_srcdir)/libs/antlr-3.4-complete.jar org.antlr.Tool $<
	

	
CLEANFILES = Gen.java CLexer.java CParser.java *.tokens