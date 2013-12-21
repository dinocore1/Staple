package com.devsmart.staple;

import java.util.ArrayList;
import java.util.Iterator;

import org.antlr.v4.runtime.Token;


public class ClassMemberDeRef {

	public ArrayList<Token> parts = new ArrayList<Token>();

	public ClassMemberDeRef(Token name) {
		parts.add(name);
	}
	
	public ClassMemberDeRef(ClassMemberDeRef parent) {
		parts.addAll(parent.parts);
	}

	public String getBase() {
		StringBuilder buf = new StringBuilder();
		for(int i=0;i<parts.size()-1;i++) {
			buf.append(parts.get(i).getText());
			if(i+2<parts.size()){
				buf.append(".");
			}
		}
		return buf.toString();
	}
	
	public String getMember() {
		return parts.get(parts.size()-1).getText();
	}
	
	public String getName() {
		StringBuilder buf = new StringBuilder();
		for(int i=0;i<parts.size();i++) {
			buf.append(parts.get(i).getText());
			if(i+1<parts.size()){
				buf.append(".");
			}
		}
		return buf.toString();
	}

}
