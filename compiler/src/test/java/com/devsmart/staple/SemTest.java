package com.devsmart.staple;


import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

public class SemTest {

    private CompilerContext mCompilerContext;

    @Before
    public void setup() {
        mCompilerContext = new CompilerContext();
    }

    @Test
    public void test1() throws Exception {

        InputStream in = getClass().getResourceAsStream("semtest1.stp");
        Assert.assertNotNull(in);
        StapleLexer lexer = new StapleLexer(new ANTLRInputStream(in));
        StapleParser parser = new StapleParser(new CommonTokenStream(lexer));

        StapleParser.CompileUnitContext tree = parser.compileUnit();
        tree.inspect(parser);

        SemPass1 semPass1 = new SemPass1(mCompilerContext);
        semPass1.visit(tree);

        SemPass2 semPass2 = new SemPass2(mCompilerContext);
        semPass2.visit(tree);

        mCompilerContext.errorStream.print(System.out);

        Assert.assertTrue(mCompilerContext.errorStream.hasErrors());
        ErrorStream.ErrorMessage message = mCompilerContext.errorStream.getMessages().get(0);
        Assert.assertNotNull(message);


    }
}
