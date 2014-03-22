package com.devsmart.staple;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class CompilerTest {

	/*
	@Test
	public void testCompiler() throws IOException {
		
		CompileContext context = CompileContext.defaultContext();
		context.file = new File("src/test/java/com/devsmart/staple/test1.stp");
		Compiler.compile(context);
		Assert.assertTrue(!context.errorStream.hasErrors());
	}
	
	@Test
	public void testOperator() throws IOException {
		
		CompileContext context = CompileContext.defaultContext();
		context.file = new File("src/test/java/com/devsmart/staple/testoperators.stp");
		Compiler.compile(context);
		Assert.assertTrue(!context.errorStream.hasErrors());
		
	}
	*/
	@Test
	public void testClass() throws IOException {
		
		CompileContext context = CompileContext.defaultContext();
		context.file = new File("src/test/resources/com/devsmart/staple/classtest.stp");
		Compiler.compile(context);
		Assert.assertTrue(!context.errorStream.hasErrors());
		
	}
}
