package com.devsmart.staple;


import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.io.InputStream;
import java.io.OutputStreamWriter;

public class CompilerContext {

    InputStream input;
    OutputStreamWriter output;
    ErrorStream errorStream = new ErrorStream();

    Scope rootScope = new Scope(null);
    ParseTreeProperty<Symbol> symbols = new ParseTreeProperty<Symbol>();

}
