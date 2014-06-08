package com.devsmart.staple.AST;


import com.devsmart.staple.type.BoolType;

public class Relation extends ASTNode {

    public enum Operator {
        LT("<"),
        GT(">"),
        LTE("<="),
        GTE(">="),
        E("=="),
        NE("!=");

        public final String symbol;

        private Operator(String symbol) {
            this.symbol = symbol;
        }

        public static Operator getOperation(String symbol) {
            Operator retval = null;
            for(Operator o : Operator.values()) {
                if(o.symbol.equals(symbol)){
                    retval = o;
                    break;
                }
            }
            return retval;
        }
    }

    public final Operator operator;
    public final ASTNode left;
    public final ASTNode right;

    public Relation(Operator op, ASTNode left, ASTNode right) {
        this.type = BoolType.BOOL;
        this.operator = op;
        this.left = left;
        this.right = right;
    }


}
