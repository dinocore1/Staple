package com.devsmart.staple.ir;


import java.util.HashSet;

public class PhiInst extends AssignmentInst {

    public static class Predecessor {
        public final Label label;
        public final Var operand;

        public Predecessor(Label label, Var operand) {
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

        @Override
        public String toString() {
            return String.format("%s %s", label.name, operand.name);
        }
    }

    public final HashSet<Predecessor> args = new HashSet<Predecessor>();

    public PhiInst(Var result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return String.format("%s = phi(%s)", result.name, args);
    }
}
