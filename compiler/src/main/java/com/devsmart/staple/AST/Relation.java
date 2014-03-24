package com.devsmart.staple.AST;


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

    private final Operator operator;
    private final ASTNode left;
    private final ASTNode right;

    public Relation(Operator op, ASTNode left, ASTNode right) {
        this.operator = op;
        this.left = left;
        this.right = right;
    }


}
