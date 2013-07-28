package com.devsmart.staple;

import java.io.File;
import java.io.IOException;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;


public class Compiler  {
	
    public static void main( String[] args ) {
    	
    	CompileContext context = CompileContext.defaultContext();
    	
        if(args.length > 0){
        	File file = new File(args[0]);
        	if(!file.exists() || !file.isFile()){
        		System.out.println("file: " + file.getAbsolutePath() + " does not exist!");
        		System.exit(1);
        	}
        	context.file = file;
        	try {
				compile(context);
			} catch (IOException e) {
				System.exit(1);
			}
        }
    }
    
    public static int compile(CompileContext context) throws IOException {
    	
    	
    	CharStream input = new ANTLRFileStream(context.file.getAbsolutePath());
    	StapleLexer lex = new StapleLexer(input);
    	CommonTokenStream tokens = new CommonTokenStream(lex);
    	StapleParser parser = new StapleParser(tokens);
    	
    	//Parse
    	ParserRuleContext tree = parser.compileUnit();
    	
    	//tree.inspect(parser);
    	
    	//Sem Pass 1
    	SemPass1 sempass1 = new SemPass1(context);
    	sempass1.visit(tree);
    	
    	if(context.errorStream.hasErrors()){
    		context.errorStream.print(System.out);
    		return 1;
    	}
    	
    	SemPass2 sempass2 = new SemPass2(context);
    	sempass2.visit(tree);
    	
    	if(context.errorStream.hasErrors()){
    		context.errorStream.print(System.out);
    		return 1;
    	}
    	
    	//Code Generate
    	CodeGenerator codeGenerator = new CodeGenerator(context);
    	codeGenerator.visit(tree);
    	codeGenerator.render(context.codegentemplate, context.codeOutput);
    	
    	//ParserRuleContext tree = parser.compileUnit();
    	
		
    	
    	//System.out.println( tree.toStringTree(parser) );
    	
    	return 0;
    }
    
    
}
