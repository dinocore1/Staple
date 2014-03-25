package com.devsmart.staple.AST;


public class MathOp extends ASTNode {

    public enum Operation {
        ADDITION("+"),
        SUBTRACTION("-"),
        MULTIPLICATION("*"),
        DIVISION("/");

        public final String symbol;

        private Operation(String symbol) {
            this.symbol = symbol;
        }

        public static Operation getOperation(String symbol) {
            Operation retval = null;
            for(Operation o : Operation.values()) {
                if(o.symbol.equals(symbol)){
                    retval = o;
                    break;
                }
            }
            return retval;
        }
    }

    public final Operation operation;
    public final ASTNode left;
    public final ASTNode right;

    public MathOp(Operation op, ASTNode left, ASTNode right) {
        this.operation = op;
        this.left = left;
        this.right = right;
    }
}
