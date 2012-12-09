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
	: enterPackage
	| enterClass
    ;

bottomup
	: exitPackage
	| exitClass
    ;
    
enterPackage
	: ^(PACKAGE 
		(ID 
		{
			NamespaceSymbol namespace = (NamespaceSymbol)currentScope.resolve($ID.text);
			if(namespace == null){
				namespace = new NamespaceSymbol($ID.text);
				currentScope.define(namespace);
				currentScope = currentScope.push();
				namespace.scope = currentScope;
			} else {
				currentScope = namespace.scope;
			}
		}
		)+ 
	  )
	;
	
exitPackage
	: PACKAGE 
		(ID 
		{
			currentScope = currentScope.pop();
		}
		)+ 
	  
	;
   
enterClass
	: ^(CLASS cname=ID subclass=ID .*) 
	{
		AbstractSymbol existingsymbol = currentScope.resolve($cname.text);
		if(existingsymbol != null && existingsymbol instanceof ClassSymbol){
			errorstream.addSymanticError($cname.token, "Redefinition of class " + $cname.text);
		} else {
			ClassSymbol newclass = new ClassSymbol($cname.text);
			$CLASS.symbol = newclass;
		
			currentScope.define(newclass);
			currentScope = currentScope.push();
		
			newclass.scope = currentScope;
		}
		
	}
	;
	
exitClass
	: CLASS
	{
		currentScope = currentScope.pop();
	}
	;