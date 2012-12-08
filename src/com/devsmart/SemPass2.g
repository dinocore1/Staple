tree grammar SemPass2;
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
    ClassSymbol currentClass;
    MethodSymbol currentMethod;
    
    public SemPass2(TreeNodeStream input, ErrorStream estream) {
        this(input);
        errorstream = estream;
    }
}

topdown
	: enterClass
	| enterFieldDefinition
	| enterMethodDefinition
	| enterFormalArgs
	| enterBlock
    ;

bottomup
	: exitClass
	| exitMethodDefinition
	| exitBlock
    ;
    
enterClass
	: ^(CLASS cname=ID subclass=ID .*) 
	{
		currentClass = (ClassSymbol)$CLASS.symbol;
		currentClass.subclass = (ClassSymbol)currentClass.scope.resolve($subclass.text);
		if(currentClass.subclass == null){
			errorstream.addSymanticError($subclass.token, "Undefined class " + $subclass.text);
		}
		currentScope = currentClass.scope;
	}
	;
	
exitClass
	: CLASS
	{
		currentClass = null;
		currentScope = currentScope.pop();
	}
	;
	
enterFieldDefinition
	: ^(FIELDS 
		(
		^(t=typeDefinition name=ID)
			{
				VarableSymbol fs = new VarableSymbol($name.text, t);
				$name.symbol = fs;
				if(currentScope.resolve($name.text) != null){
					errorstream.addSymanticError($name.token, "Redefinition of symbol " + $name.text);
				}
				currentScope.define(fs);
				currentClass.fields.put(fs.getName(), fs);
			} 
		)*
		)
	;

enterMethodDefinition
	: ^(METHODS
		(
		^(FUNCTION name=ID rtype=typeDefinition .*)
			{
				MethodSymbol ms = new MethodSymbol($name.text, rtype, currentClass);
				$FUNCTION.symbol = ms;
				
				if(currentScope.resolve($name.text) != null){
					errorstream.addSymanticError($name.token, "Redefinition of symbol " + $name.text);
				}
				currentScope.define(ms);
				currentClass.methods.put($name.text , ms);
				
				currentScope = currentScope.push();
				ms.scope = currentScope;
				
				currentMethod = ms;
				
			}
		)*
		)
	;
	
exitMethodDefinition
	: FUNCTION
		{
			currentScope = currentScope.pop();
		}
	;
	
enterFormalArgs
	: ^(FORMALARGS 
		(
		^(t=typeDefinition name=ID) 
			{
				VarableSymbol fs = new VarableSymbol($name.text, t);
				$name.symbol = fs;
				if(currentScope.resolve($name.text) != null){
					errorstream.addSymanticError($name.token, "Redefinition of symbol " + $name.text);
				}
				currentScope.define(fs);
				currentMethod.formalArgs.add(fs);
			} 
		)* 
		)
	;
	
	
typeDefinition returns [AbstractType value]
	: 'void' { $value = PrimitiveType.VOID; }
	| 'int'  { $value = PrimitiveType.INT; }
	| 'bool' { $value = PrimitiveType.BOOL; }
	| name=ID	{ 
					AbstractSymbol classtype = currentScope.resolve($name.text);
					if(classtype == null){
						errorstream.addSymanticError($name.token, "Undefined class " + $name.text);
						return null;
					}
					if(classtype instanceof ClassSymbol){
						errorstream.addSymanticError($name.token, $name.text + " is not a class type");
						return null;
					}
					$value = new ClassType((ClassSymbol)classtype);
					
				}
	;
	
enterBlock
	: ^(BLOCK { currentScope = currentScope.push(); }
		statement*
		)
	;
	
statement
	: assignment
	| typeTree 
	;
	
exitBlock
	: BLOCK { currentScope = currentScope.pop(); }
	;
	
assignment
	: ^(ASSIGN l=typeTree r=typeTree)
		{
			if(l != r){
				errorstream.addSymanticError($ASSIGN.token, "not matching assignable types");
			}
		}
	;
	
typeTree returns [AbstractType value]
	: name=ID	
		{ 
			VarableSymbol vs = (VarableSymbol)currentScope.resolve($name.text);
			if(vs == null){
				errorstream.addSymanticError($name.token, "Undefined symbol " + $name.text);
			}
			$value = vs.type;
		}
	| THIS { $value = currentClass.type; }
	| INT { $value = PrimitiveType.INT; }
	| ^( s=('+'|'-'|'*'|'/') l=typeTree r=typeTree ) 
		{ 
			if(l != PrimitiveType.INT || r != PrimitiveType.INT){
				errorstream.addSymanticError($s.token, "Cannot perform operation on incompatible types");
			}
			$value = PrimitiveType.INT; 
		}
	| ^( FIELDACCESS base=typeTree field=ID ) 
		{ 
			if(!(base instanceof ClassType)){
				errorstream.addSymanticError($FIELDACCESS.token, "Cannot access a field of a non-class type");
			} else {
				ClassSymbol classSymbol = ((ClassType)base).symbol;
				VarableSymbol fieldSymbol = classSymbol.fields.get($field.text);
				if(fieldSymbol == null){
					errorstream.addSymanticError($field.token, "Class '" + classSymbol.getName() + "' does not have a field: " + $field.text);
				} else {
					$value = fieldSymbol.type;
				}
			}
			 
		}
	| ^(CALL base=typeTree name=ID .*)
		{
			if(!(base instanceof ClassType)){
				errorstream.addSymanticError($CALL.token, "Cannot make a class on a non-class type");
			} else {
				ClassSymbol classSymbol = ((ClassType)base).symbol;
				MethodSymbol methodSymbol = classSymbol.methods.get($name.text);
				if(methodSymbol == null){
					errorstream.addSymanticError($name.token, "Class '" + classSymbol.getName() + "' does not have a method: " + $name.text);
				} else {
					$value = methodSymbol.returnType;
				}
			}
		}
	| ^(VARDEF t=typeDefinition name=ID)
		{
			VarableSymbol vs = new VarableSymbol($name.text, t);
			$name.symbol = vs;
			if(currentScope.resolve($name.text) != null){
				errorstream.addSymanticError($name.token, "Redefinition of symbol " + $name.text);
			}
			currentScope.define(vs);
			$value = t;
		}
	;
	
	
