package com.devsmart.staple;

import java.io.File;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import org.abego.treelayout.internal.util.java.lang.string.StringUtil;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;

public class ErrorStream {

	
	private List<ErrorMessage> messages = new LinkedList<ErrorMessage>();
	
	public void error(String message, Token token) {
		messages.add(new ErrorMessage(ErrorMessage.Level.Error, message, token));
	}
	
	public void print(PrintStream out) {
		for(ErrorMessage m : messages){
			out.println(m.toString());
		}
	}
	
	public boolean hasErrors() {
		boolean hasError = false;
		for(ErrorMessage m : messages){
			if(m.level == ErrorMessage.Level.Error){
				hasError = true;
				break;
			}
		}
		return hasError;
	}
	
	public static class ErrorMessage {
		public static enum Level {
			Error,
			Warn,
			Debug
		}
		
		Level level;
		String message;
		Token token;
		
		public ErrorMessage(Level level, String message, Token location){
			this.level = level;
			this.message = message;
			this.token = location;
		}
		
		@Override
		public String toString(){
			String locationStr = "";
			if(token != null){
				TokenSource source = token.getTokenSource();
				String sourceFile = source.getSourceName();
				sourceFile = sourceFile.substring(sourceFile.lastIndexOf(File.separator)+1, sourceFile.length());
				locationStr = String.format("%s:%d", sourceFile, token.getLine());
			}
			return String.format("[%s %s] %s", level.toString(), locationStr, message);
		}
	}

	

	
}