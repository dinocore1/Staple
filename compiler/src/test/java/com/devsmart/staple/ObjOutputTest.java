package com.devsmart.staple;


import com.devsmart.staple.ccodegen.CCodeGen;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

public class ObjOutputTest {

    CompilerContext ctx;
    private ByteArrayOutputStream headerBuffer;
    private ByteArrayOutputStream codeBuffer;


    @Before
    public void setup() throws Exception {


        ctx = new CompilerContext();
        ctx.input = new FileInputStream(new File("src/test/linkedlist.stp"));

        headerBuffer = new ByteArrayOutputStream();
        codeBuffer = new ByteArrayOutputStream();
    }

    @Test
    public void testObjOutput() throws Exception {

        StapleLexer lexer = new StapleLexer(new ANTLRInputStream(ctx.input));
        StapleParser parser = new StapleParser(new CommonTokenStream(lexer));

        StapleParser.CompileUnitContext tree = parser.compileUnit();
        //tree.inspect(parser);

        SemPass1 semPass1 = new SemPass1(ctx);
        semPass1.visit(tree);

        SemPass2 semPass2 = new SemPass2(ctx);
        semPass2.visit(tree);

        OutputStreamWriter headerWriter = new OutputStreamWriter(headerBuffer);
        OutputStreamWriter codeWriter = new OutputStreamWriter(codeBuffer);
        CCodeGen codeGen = new CCodeGen(ctx, headerWriter, codeWriter);
        codeGen.visit(tree);

        headerWriter.close();
        System.out.println("********* Header ***********");
        System.out.println(headerBuffer.toString("UTF-8"));

        codeWriter.close();
        System.out.println("********* Code *************");
        System.out.println(codeBuffer.toString("UTF-8"));

        System.out.println("done");
    }
}
