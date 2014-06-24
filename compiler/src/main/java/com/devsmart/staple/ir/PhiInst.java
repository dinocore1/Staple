package com.devsmart.staple.ir;


import java.util.HashSet;

public class PhiInst extends SSAInst {

    public static class Predecessor {
        public final Label label;
        public final Operand operand;

        public Predecessor(Label label, Operand operand) {
            this.label = label;
            this.operand = operand;
        }

        @Override
        public int hashCode() {
            return label.hashCode() ^ operand.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            boolean retval = false;
            if(obj instanceof Predecessor){
                retval = ((Predecessor) obj).label.equals(label) && ((Predecessor) obj).operand.equals(operand);
            }
            return retval;
        }
    }

    public final Operand result;
    public final HashSet<Predecessor> args = new HashSet<Predecessor>();

    public PhiInst(Operand result) {
        this.result = result;
    }
}
