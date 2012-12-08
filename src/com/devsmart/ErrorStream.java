package com.devsmart;

import java.io.PrintStream;
import java.util.LinkedList;

import org.antlr.runtime.Token;

public class ErrorStream {
	
	public static final int WARNING = 0;
	public static final int ERROR = 1;
	
	public static class Message {
		int type;
		int line;
		int column;
		String message;
		
		@Override
		public String toString() {
			return String.format("%s [%d:%d] %s", 
					getTypeString(type),
					line,
					column,
					message);
		}
		
		public String getTypeString(int type){
			switch(type){
			case WARNING:
				return "warning";
				
			case ERROR:
				return "error";
				
			default:
				return "";
			}
			
		}
		
	}
	
	private LinkedList<Message> mMessages = new LinkedList<Message>();
	
	public void addSymanticError(Token t, String message) {
		Message m = new Message();
		m.type = ERROR;
		m.line = t.getLine();
		m.column = t.getCharPositionInLine();
		m.message = message;
		mMessages.add(m);
	}
	
	public boolean hasError() {
		for(Message m : mMessages){
			if(m.type == ERROR){
				return true;
			}
		}
		return false;
	}
	
	public void printMessages(PrintStream out) {
		for(Message m : mMessages){
			out.println(m);
		}
	}

}
