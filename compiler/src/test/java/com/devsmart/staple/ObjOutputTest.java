package com.devsmart.staple;


import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;

public class ObjOutputTest {

    CompilerContext ctx;


    @Before
    public void setup() throws Exception {


        ctx = new CompilerContext();
        ctx.input = new FileInputStream(new File("compiler/src/test/linkedlist.stp"));
        ctx.output = new OutputStreamWriter(System.out);
    }

    @Test
    public void testObjOutput() throws Exception {

        StapleLexer lexer = new StapleLexer(new ANTLRInputStream(ctx.input));
        StapleParser parser = new StapleParser(new CommonTokenStream(lexer));

        StapleParser.CompileUnitContext tree = parser.compileUnit();
        tree.inspect(parser);

        SemPass1 semPass1 = new SemPass1(ctx);
        semPass1.visit(tree);

        SemPass2 semPass2 = new SemPass2(ctx);
        semPass2.visit(tree);

        CCodeGen codeGen = new CCodeGen(ctx);
        codeGen.visit(tree);

        ctx.output.flush();

        System.out.println("done");
    }
}
