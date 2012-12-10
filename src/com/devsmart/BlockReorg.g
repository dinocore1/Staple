tree grammar BlockReorg;
options {
	tokenVocab = Staple;
	ASTLabelType = StapleTree;
	output = AST;
	filter = true;
}

@header {
package com.devsmart;

import com.devsmart.symbol.*;
import com.devsmart.type.*;
}

@members {
   
    ErrorStream errorstream;
    Scope currentScope;
    
    public BlockReorg(TreeNodeStream input, ErrorStream estream) {
        this(input);
        errorstream = estream;
    }
}

topdown
	: 
	;
	
bottomup
	: objAssign
	;
	
objAssign
	: ^(ASSIGN lside=. rside=.)  
	 	-> {  ((VarableSymbol)$lside.symbol).type instanceof ClassType }? ^(BLOCK ^(CALL $lside ID["release"] ^(ARGS $lside)) ^(ASSIGN $lside $rside) )
		-> ^(ASSIGN $lside $rside ) 
	;
	