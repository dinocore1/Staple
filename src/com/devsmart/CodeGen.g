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
import com.devsmart.utils.*;
}

@members {

	List mClassPackageName;
	String mClassName;
	
	StringTemplate getFieldTemplate(VarableSymbol vs) {
		StringTemplate retval = null;
		switch(TypeFactory.getType(vs.type)){
			case TypeFactory.TYPE_BOOL:
        		retval = templateLib.getInstanceOf("bool_field_def",new STAttrMap().put("name", vs.getName()));
            break;
            
            case TypeFactory.TYPE_INT:
            	retval = templateLib.getInstanceOf("int_field_def",new STAttrMap().put("name", vs.getName()));
            break;
            		
            case TypeFactory.TYPE_CLASS:
            	retval = templateLib.getInstanceOf("obj_field_def",new STAttrMap().put("name", vs.getName()));
            break;
         }
         return retval;
	}
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
	: ^(CLASS name=. superclass=. . ^(METHODS methodDefs=methodDefinition*))
	{
		ClassSymbol classsym = (ClassSymbol)$CLASS.symbol;
		mClassName = $name.getText();
		ArrayList className = new ArrayList(mClassPackageName);
		className.add($name);
		
		final ArrayList<String> fieldsstr = new ArrayList<String>();
		Utils.dfClassVisitor(classsym, new Visitor(){
			public void visit(Object o){
				ClassSymbol c = (ClassSymbol)o;
				for(VarableSymbol v : c.fields.values()){
					fieldsstr.add(getFieldTemplate(v).toString());
				}
			}
		});
		
	}
	
	 -> class_def(name={className}, superclass={$superclass}, fields={fieldsstr}, code={$methodDefs.st})
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
	: ^(BLOCK { StringBuilder stm = new StringBuilder(); }
		( code=statement
		   {  stm.append($code.st); stm.append("\n"); }
		)*
		) -> basic_block(code={stm})
	;
	
statement
	: block {retval.st = $block.st;}
	| assignment -> statement(code={$assignment.st})
	| vardef -> statement(code={$vardef.st})
	| integerOp {retval.st = $integerOp.st;}
	| ID -> varref(name={$ID.text})
	| call -> statement(code={$call.st})
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
	
callobj
	: ID
	| ^(FIELDACCESS ID ID)
	;
	
call
	: ^(CALL callobj mname=ID ^(ARGS argsv=.*)) -> call(obj={$callobj.st}, mname={$mname}, args={$argsv})
	;
	
