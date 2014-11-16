package com.devsmart.staple.ccodegen.instruction;

import com.devsmart.staple.ccodegen.CCodeGen;
import org.stringtemplate.v4.ST;

public class IfInst implements Instruction {

    public String condition;
    public Instruction thenInst;
    public Instruction elseInst;

    @Override
    public String render() {
        ST ifTmp = CCodeGen.codegentemplate.getInstanceOf("ifInst");
        ifTmp.add("condition", condition);
        ifTmp.add("thenstmp", thenInst.render());
        ifTmp.add("elsestmt", elseInst != null ? elseInst.render() : null);

        return ifTmp.render();
    }
}
