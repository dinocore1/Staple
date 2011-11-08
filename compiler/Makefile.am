


StapleLexer.java StapleParser.java Staple.tokens: Staple.g
	java -classpath $(top_srcdir)/libs/antlr-3.4-complete.jar org.antlr.Tool Staple.g
	
DefRef.java Gen.java: Staple.tokens

libantlr.so: $(top_srcdir)/libs/antlr-3.4-complete.jar
	gcj -findirect-dispatch -fPIC -shared -o libantlr.so $(top_srcdir)/libs/antlr-3.4-complete.jar


bin_PROGRAMS = stpc
stpc_SOURCES = \
	DefRef.java \
	Gen.java \
	ArrayType.java \
	BaseScope.java \
	BuiltInTypeSymbol.java \
	CC.java \
	CErrorNode.java \
	ClassSymbol.java \
	ClassType.java \
	StapleLexer.java \
	StapleParser.java \
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
	 
stpc_GCJFLAGS = -I$(top_srcdir)/libs/antlr-3.4-complete.jar -I. -findirect-dispatch
stpc_LDFLAGS = -findirect-dispatch --main=CC
stpc_LDADD = libantlr.so llvm.o C.o
#stpc_LDADD = $(top_srcdir)/libs/antlr-3.4-complete.jar
	

.stg.o:
	$(GCJ) -c -o $@ --resource=$< $<	

.g.java: 
	java -classpath $(top_srcdir)/libs/antlr-3.4-complete.jar org.antlr.Tool $<
	

	
CLEANFILES = Gen.java DefRef.java StapleLexer.java StapleParser.java *.tokens