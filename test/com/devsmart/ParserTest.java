package com.devsmart;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.Assert;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.junit.Test;

public class ParserTest {

	@Test
	public void test() throws Exception {
		
		Compiler.config.inputfile = "test/com/devsmart/test1.stp";
		Compiler.config.verbose = Compiler.Config.VERBOSE_ALL;
		
		InputStream in = getClass().getResourceAsStream("/com/devsmart/templates/C.st");
        Reader rd = new InputStreamReader(in);
        Compiler.config.codegentemplate = new StringTemplateGroup(rd);
		rd.close();
		
		int retval = Compiler.compile();
		Assert.assertEquals(retval, 0);
		
		
	}

}
