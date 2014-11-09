package com.devsmart.staple;


import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.io.InputStream;
import java.io.OutputStreamWriter;

public class CompilerContext {

    InputStream input;
    ErrorStream errorStream = new ErrorStream();

    Scope rootScope = new Scope(null);
    public ParseTreeProperty<Object> symbols = new ParseTreeProperty<Object>();
    public ParseTreeProperty<Scope> scope = new ParseTreeProperty<Scope>();
}
