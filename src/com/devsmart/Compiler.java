package com.devsmart;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import com.devsmart.StapleParser.compilationUnit_return;

public class Compiler {
	
	
	public static class Config {
		
		public static final int VERBOSE_NONE = 0;
		public static final int VERBOSE_SOME = 1;
		public static final int VERBOSE_ALL = 2;
		
		public String inputfile;
		public int verbose = 0;
		public StringTemplateGroup codegentemplate;
		public File runtimefolder;
		public Scope globalScope = new Scope(null);
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
	
	public static StapleParser.compilationUnit_return parse(String inputfilepath) throws IOException, RecognitionException {
		CharStream input = new ANTLRFileStream(inputfilepath);
		// BUILD AST
		StapleLexer lex = new StapleLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lex);
		StapleParser parser = new StapleParser(tokens);
		parser.setTreeAdaptor(new StapleTreeAdapter());
		compilationUnit_return r = parser.compilationUnit();

		if (config.verbose >= Config.VERBOSE_ALL) {
			System.out.println("tree=" + ((Tree) r.tree).toStringTree());
		}

		return r;
	}
	
	public static boolean doSemPass2(ErrorStream estream, Scope globalScope, CommonTree t) {
		boolean retval = true;
		
        // Do Semantic Pass 2
        SemPass2 sempass2 = new SemPass2(new CommonTreeNodeStream(t), globalScope, estream);
        sempass2.downup(t);
        
        
        if(estream.hasError()){
        	estream.printMessages(System.out);
        	retval = false;
        }
        
        return retval;
	}
	
	public static boolean doSemPass1(ErrorStream estream, Scope globalScope, CommonTree t) {
		boolean retval = true;
		
		// Do Semantic Pass 1
		SemPass1 sempass1 = new SemPass1(new CommonTreeNodeStream(t), globalScope, estream);
        sempass1.downup(t);
        
        
        if(estream.hasError()){
        	estream.printMessages(System.out);
        	retval = false;
        }
        
        return retval;
	}
	
	public static boolean doBlockReorg(ErrorStream estream, Scope globalScope, CommonTree t) {
		boolean retval = true;
		
		BlockReorg reorg = new BlockReorg(new CommonTreeNodeStream(t));
		reorg.setTreeAdaptor(new StapleTreeAdapter());
		t = (StapleTree) reorg.downup(t);
        
		if (config.verbose >= Config.VERBOSE_ALL) {
			System.out.println("tree=" + t.toStringTree());
		}
        
        if(estream.hasError()){
        	estream.printMessages(System.out);
        	retval = false;
        }
        
        return retval;
	}
	
	
	public static int compile() throws Exception {
		
		
		ErrorStream estream = new ErrorStream();
		StapleParser.compilationUnit_return r = parse(config.inputfile);
		
        
        // Do Semantic Passes
        CommonTree t = (CommonTree) r.getTree();
        doSemPass1(estream, config.globalScope, t);
        doSemPass2(estream, config.globalScope, t);
        
        
        doBlockReorg(estream, config.globalScope, t);
        
        // Do Code Generation
        CodeGen codegen = new CodeGen(new CommonTreeNodeStream(t));
        codegen.setTemplateLib(config.codegentemplate);
        String output = codegen.code_unit().getTemplate().toString();
        if(config.verbose >= Config.VERBOSE_ALL) {
        	System.out.println(output);
        }
        
        return 0;
	}

	static FilenameFilter textFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			String lowercaseName = name.toLowerCase();
			if (lowercaseName.endsWith(".stp")) {
				return true;
			} else {
				return false;
			}
		}
	};

	public static void setRuntime(File file) throws IOException, RecognitionException {
		config.runtimefolder = file;
		if(config.runtimefolder.exists() && config.runtimefolder.isDirectory()){
			for(File f : config.runtimefolder.listFiles(textFilter)){
				ErrorStream estream = new ErrorStream();
				String filepath = f.getAbsolutePath();
				compilationUnit_return r = parse(filepath);
				CommonTree t = (CommonTree) r.getTree();
				doSemPass1(estream, config.globalScope, t);
			}
			
			for(File f : config.runtimefolder.listFiles(textFilter)){
				ErrorStream estream = new ErrorStream();
				String filepath = f.getAbsolutePath();
				compilationUnit_return r = parse(filepath);
				CommonTree t = (CommonTree) r.getTree();
				doSemPass2(estream, config.globalScope, t);
			}
			
		}
		
	}

}
