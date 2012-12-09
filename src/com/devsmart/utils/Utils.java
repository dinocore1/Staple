package com.devsmart.utils;

import com.devsmart.symbol.ClassSymbol;

public class Utils {

	
	public static void dfClassVisitor(ClassSymbol clazz, Visitor v) {
		if(clazz.superclass != null && clazz != clazz.superclass){
			dfClassVisitor(clazz.superclass, v);
		}
		v.visit(clazz);
	}
}
