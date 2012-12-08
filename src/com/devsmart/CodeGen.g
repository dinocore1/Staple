tree grammar CodeGen;
options {
	tokenVocab = Staple;
	ASTLabelType = StapleTree;
	output = template;
}

@header {
package com.devsmart;

import com.devsmart.symbol.*;
import com.devsmart.type.*;
}

@members {

	List mClassPackageName;
	String mClassName;
}

code_unit
	: ^(UNIT 
	^(
		PACKAGE packagename+=.*
		{
			mClassPackageName = $packagename;
		} 
	) 
	class_def) -> code_unit(packagename={$packagename}, code={$class_def.st})
	;
	
class_def
	: ^(CLASS name=. 
	{
		mClassName = $name.getText();
		ArrayList className = new ArrayList(mClassPackageName);
		className.add($name);
	}
	superclass=. ^(FIELDS fieldDefs=fieldDefinition*) ^(METHODS methodDefs=methodDefinition*))
	 -> class_def(name={className}, superclass={$superclass}, fields={$fieldDefs.st}, code={$methodDefs.st})
	;
	
fieldDefinition
	: ^(t=typeDefinition name=ID)
	{
		VarableSymbol vs = (VarableSymbol)$name.symbol;
		switch(TypeFactory.getType(vs.type)){
			case TypeFactory.TYPE_BOOL:
				retval.st = templateLib.getInstanceOf("bool_field_def",new STAttrMap().put("name", vs.getName()));
			break;
			
			case TypeFactory.TYPE_INT:
				retval.st = templateLib.getInstanceOf("int_field_def",new STAttrMap().put("name", vs.getName()));
			break;
			
			case TypeFactory.TYPE_CLASS:
				retval.st = templateLib.getInstanceOf("obj_field_def",new STAttrMap().put("name", vs.getName()));
			break;
		}
	}
	;
	
methodDefinition
	: ^(FUNCTION name=ID returnType=methodReturnDefinition ^(FORMALARGS formals=formalArg*) code=block)
		{
			ArrayList methodName = new ArrayList(mClassPackageName);
			methodName.add(mClassName);
			methodName.add($name);
		}
		-> method(name={methodName}, return={$returnType.st}, formals={$formals.st}, code={$code.st})
	;
	
methodReturnDefinition
	: 'void' -> void_return_def()
	| 'int' -> int_return_def()
	| 'bool' -> bool_return_def()
	| ID -> obj_return_def()
	;
	
formalArg
	: ^(t=typeDefinition name=ID)
	{
		VarableSymbol vs = (VarableSymbol)$name.symbol;
		switch(TypeFactory.getType(vs.type)){
			case TypeFactory.TYPE_BOOL:
				retval.st = templateLib.getInstanceOf("bool_arg",new STAttrMap().put("name", vs.getName()));
			break;
			
			case TypeFactory.TYPE_INT:
				retval.st = templateLib.getInstanceOf("int_arg",new STAttrMap().put("name", vs.getName()));
			break;
			
			case TypeFactory.TYPE_CLASS:
				retval.st = templateLib.getInstanceOf("obj_arg",new STAttrMap().put("name", vs.getName()));
			break;
		}
	}
	;
	
block
	: ^(BLOCK code=statement*) -> basic_block(code={$code.st})
	;
	
statement
	: block {retval.st = $block.st;}
	| assignment {retval.st = $assignment.st;}
	| vardef {retval.st = $vardef.st;}
	| integerOp {retval.st = $integerOp.st;}
	;
	
assignment
	: ^(ASSIGN lside=statement rside=statement) -> assignment(lside={$lside.st}, rside={$rside.st})
	;
	
vardef
	: ^(VARDEF t=typeDefinition name=ID)
	{
		VarableSymbol vs = (VarableSymbol)$name.symbol;
		switch(TypeFactory.getType(vs.type)){
			case TypeFactory.TYPE_BOOL:
				retval.st = templateLib.getInstanceOf("bool_var",new STAttrMap().put("name", vs.getName()));
			break;
			
			case TypeFactory.TYPE_INT:
				retval.st = templateLib.getInstanceOf("int_var",new STAttrMap().put("name", vs.getName()));
			break;
			
			case TypeFactory.TYPE_CLASS:
				retval.st = templateLib.getInstanceOf("obj_var",new STAttrMap().put("name", vs.getName()));
			break;
		}
	}
	;
	
integerOp
	: ^('+' lside=statement rside=statement) -> add(lside={$lside.st}, rside={$rside.st})
	| ^('-' lside=statement rside=statement) -> subtract(lside={$lside.st}, rside={$rside.st})
	| ^('*' lside=statement rside=statement) -> multiply(lside={$lside.st}, rside={$rside.st})
	| ^('/' lside=statement rside=statement) -> divide(lside={$lside.st}, rside={$rside.st})
	| value=INT -> int_literal(value={$value.text})
	;
	
typeDefinition
	: 'void' 
	| 'int'
	| 'bool'
	| ID
	;
	
