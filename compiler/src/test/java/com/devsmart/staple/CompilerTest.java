package com.devsmart.staple;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class CompilerTest {

	
	@Test
	public void testCompiler() throws IOException {
		
		CompileContext context = CompileContext.defaultContext();
		context.file = new File("src/test/java/com/devsmart/staple/test1.stp");
		Compiler.compile(context);
		
	}
	
	@Test
	public void testOperator() throws IOException {
		
		CompileContext context = CompileContext.defaultContext();
		context.file = new File("src/test/java/com/devsmart/staple/testoperators.stp");
		Compiler.compile(context);
		
	}
}
