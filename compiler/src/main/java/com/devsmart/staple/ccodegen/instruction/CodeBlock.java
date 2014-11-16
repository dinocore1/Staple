package com.devsmart.staple.ccodegen.instruction;


import com.devsmart.staple.ccodegen.CCodeGen;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.stringtemplate.v4.ST;

import java.util.LinkedList;

public class CodeBlock implements Instruction {

    public CodeBlock parent;
    public LinkedList<Instruction> code = new LinkedList<Instruction>();

    @Override
    public String render() {
        ST blockTmp = CCodeGen.codegentemplate.getInstanceOf("block");
        blockTmp.add("inst", Collections2.transform(code, new Function<Instruction, String>() {
            @Override
            public String apply(Instruction input) {
                return input.render();
            }
        }));
        return blockTmp.render();
    }
}
