import org.antlr.runtime.*;

public class CErrorNode extends CTree {
    public CErrorNode(TokenStream input, Token start, Token stop,
                            RecognitionException e)
    {
	    super(start); // no need to record anything for this example
    }
}
