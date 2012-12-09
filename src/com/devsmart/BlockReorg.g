tree grammar BlockReorg;
options {
	tokenVocab = Staple;
	ASTLabelType = StapleTree;
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
	: enterBlock
	;
	
bottomup
	: exitBlock
	;
	
enterBlock
	: ^(BLOCK .* ) 
	;
	
exitBlock
	: BLOCK
	;	

variableDefinition
	:	^(VARDEF .*)
	;