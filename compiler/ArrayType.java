import org.antlr.runtime.tree.CommonTree;

/** A symbol to represent arrays with a single element type */
public class ArrayType extends Symbol implements Type {
    Type elementType;
    CommonTree sizeExpr;

    public ArrayType(Type elementType, CommonTree sizeExpr) {
        super(elementType+"[]");
        this.elementType = elementType;
        this.sizeExpr = sizeExpr;
    }
    public int getTypeIndex() { return 0; }
    
    public CommonTree getSizeExpr() { return (CommonTree)sizeExpr.getChild(0); }
}
