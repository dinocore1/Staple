package com.devsmart.staple.ir;


public class Label extends SSAInst {

    public String nameFormat = "l%s";
    public String name;


    @Override
    public String toString() {
        return String.format("%s:", name);
    }


}
