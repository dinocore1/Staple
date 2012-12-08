package com.devsmart;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;

public class StapleTreeAdapter extends CommonTreeAdaptor {
	public Object create(Token token) {
        return new StapleTree(token);
    }
    public Object dupNode(Object t) {
        if ( t==null ) {
            return null;
        }
        return create(((StapleTree)t).token);
    }
}
