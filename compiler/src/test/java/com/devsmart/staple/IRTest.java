package com.devsmart.staple;


import com.devsmart.staple.ir.SSAInst;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

@RunWith(Parameterized.class)
public class IRTest {

    @Parameterized.Parameters
    public static Iterable<Object[]> createTests() {
        ArrayList<Object[]> retval = new ArrayList<Object[]>();

        retval.add(new Object[]{IRTest.class.getResource("fibonacci.stp")});
        retval.add(new Object[]{IRTest.class.getResource("test1.stp")});


        return retval;
    }

    private URL mInputFile;
    private CompilerContext mCompilerContext;

    public IRTest(URL file){
        mInputFile = file;
    }

    @Before
    public void setup() {
        mCompilerContext = new CompilerContext();
    }

    @Test
    public void test1() throws Exception {
        InputStream in = mInputFile.openStream();
        Assert.assertNotNull(in);
        StapleLexer lexer = new StapleLexer(new ANTLRInputStream(in));
        StapleParser parser = new StapleParser(new CommonTokenStream(lexer));

        StapleParser.CompileUnitContext tree = parser.compileUnit();
        tree.inspect(parser);

        SemPass1 semPass1 = new SemPass1(mCompilerContext);
        semPass1.visit(tree);

        SemPass2 semPass2 = new SemPass2(mCompilerContext);
        semPass2.visit(tree);

        IRVisitor irVisitor = new IRVisitor(mCompilerContext);
        irVisitor.visit(tree);

        for(SSAInst i : mCompilerContext.code){
            System.out.println(i.toString());
        }


        System.out.println("done");
    }
}
