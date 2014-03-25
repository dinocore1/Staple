package com.devsmart.staple;


import com.devsmart.staple.ir.SSAInst;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

public class IRTest {

    private CompilerContext mCompilerContext;

    @Before
    public void setup() {
        mCompilerContext = new CompilerContext();
    }

    @Test
    public void test1() throws Exception {
        InputStream in = getClass().getResourceAsStream("test1.stp");
        Assert.assertNotNull(in);
        StapleLexer lexer = new StapleLexer(new ANTLRInputStream(in));
        StapleParser parser = new StapleParser(new CommonTokenStream(lexer));

        StapleParser.CompileUnitContext tree = parser.compileUnit();
        tree.inspect(parser);

        ASTVisitor astNodeVisitor = new ASTVisitor(mCompilerContext);
        astNodeVisitor.visit(tree);

        IRVisitor irVisitor = new IRVisitor(mCompilerContext);
        irVisitor.visit(tree);

        for(SSAInst i : mCompilerContext.code){
            System.out.println(i.toString());
        }


        System.out.println("done");
    }
}
