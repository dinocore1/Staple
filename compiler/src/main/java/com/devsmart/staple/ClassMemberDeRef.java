package com.devsmart.staple;

import java.util.ArrayList;

import org.antlr.v4.runtime.Token;


public class ClassMemberDeRef {

	
	public Token base;
	public ArrayList<Token> members = new ArrayList<Token>();

	public ClassMemberDeRef(Token name) {
		this.base = name;
	}

}
