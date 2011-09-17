import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.Token;
public class CTree extends CommonTree {
    public Symbol symbol; // set during DefRef traversal
    public CTree(Token t) { super(t); }
}
