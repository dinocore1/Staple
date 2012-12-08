package com.devsmart;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

public class Compiler {
	
	
	public static class Config {
		
		public static final int VERBOSE_NONE = 0;
		public static final int VERBOSE_SOME = 1;
		public static final int VERBOSE_ALL = 2;
		
		public String inputfile;
		public int verbose = 0;
		public StringTemplateGroup codegentemplate;
	}
	
	public static Config config = new Config();
	
	
	public static void parseCmdLine(String[] args) throws IOException{
		int i = 0;
		while ( i<args.length ) {
			config.inputfile = args[i]; i++;
        }
		
		
		InputStream in = Compiler.class.getResourceAsStream("/com/devsmart/templates/C.st");
        Reader rd = new InputStreamReader(in);
        Compiler.config.codegentemplate = new StringTemplateGroup(rd);
		rd.close();
        
	}
	
	public static int main(String[] args) throws Exception {
		
		parseCmdLine(args);
		
		return compile();
        
	}
	
	public static int compile() throws Exception {
		CharStream input = new ANTLRFileStream(config.inputfile);
		
		ErrorStream estream = new ErrorStream();
		
		// BUILD AST
		StapleLexer lex = new StapleLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        StapleParser parser = new StapleParser(tokens);
        parser.setTreeAdaptor(new StapleTreeAdapter());
        StapleParser.compilationUnit_return r = parser.compilationUnit();
        
        if(config.verbose >= Config.VERBOSE_ALL) {
        	System.out.println("tree="+((Tree)r.tree).toStringTree());
        }
        
        // Do Semantic Pass 1
        Scope globalScope = new Scope(null);
        CommonTree t = (CommonTree) r.getTree();
        SemPass1 sempass1 = new SemPass1(new CommonTreeNodeStream(t), globalScope, estream);
        sempass1.downup(t);
        
        estream.printMessages(System.out);
        if(estream.hasError()){
        	return 1;
        }
        
        // Do Semantic Pass 2
        SemPass2 sempass2 = new SemPass2(new CommonTreeNodeStream(t), estream);
        sempass2.downup(t);
        
        estream.printMessages(System.out);
        if(estream.hasError()){
        	//return 1;
        }
        
        // Do Code Generation
        CodeGen codegen = new CodeGen(new CommonTreeNodeStream(t));
        codegen.setTemplateLib(config.codegentemplate);
        String output = codegen.code_unit().getTemplate().toString();
        if(config.verbose >= Config.VERBOSE_ALL) {
        	System.out.println(output);
        }
        
        return 0;
	}

}
