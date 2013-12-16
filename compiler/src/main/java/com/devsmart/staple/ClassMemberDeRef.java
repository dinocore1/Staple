package com.devsmart.staple;

import org.antlr.v4.runtime.Token;

import com.devsmart.staple.symbols.MemberVarableSymbol;


public class ClassMemberDeRef {

	public Token base;
	public Token member;
	public MemberVarableSymbol memberSymbol;

	public ClassMemberDeRef(Token name) {
		this.base = name;
	}

}
