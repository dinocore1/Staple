tree grammar SemPass1;
options {
	tokenVocab = Staple;
	ASTLabelType = StapleTree;
	filter = true;
}

@header {
package com.devsmart;

import com.devsmart.symbol.*;
}

@members {
    Scope currentScope;
    ErrorStream errorstream;
    
    public SemPass1(TreeNodeStream input, Scope globalScope, ErrorStream estream) {
        this(input);
        currentScope = globalScope;
        errorstream = estream;
    }
}

topdown
	: enterClass
    ;

bottomup
	: exitClass
    ;
    
enterClass
	: ^(CLASS cname=ID subclass=ID .*) 
	{
		ClassSymbol newclass = new ClassSymbol($cname.text);
		$CLASS.symbol = newclass;
		
		currentScope.define(newclass);
		currentScope = currentScope.push();
		
		newclass.scope = currentScope;
		
	}
	;
	
exitClass
	: CLASS
	{
		currentScope = currentScope.pop();
	}
	;