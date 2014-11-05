package com.devsmart.staple;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;

import java.io.File;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

public class ErrorStream {

	
	private List<ErrorMessage> messages = new LinkedList<ErrorMessage>();

    public void error(String message, Token token) {
		messages.add(new ErrorMessage(ErrorMessage.Level.Error, message, token, token));
	}

    public void error(String message, ParserRuleContext ctx) {
        messages.add(new ErrorMessage(ErrorMessage.Level.Error, message, ctx.start, ctx.stop));
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

    public List<ErrorMessage> getMessages() {
        return messages;
    }

    public static class ErrorMessage {
		public static enum Level {
			Error,
			Warn,
			Debug
		}
		
		Level level;
		String message;
		Token start;
        Token stop;
		
		public ErrorMessage(Level level, String message, Token start, Token stop){
			this.level = level;
			this.message = message;
			this.start = start;
            this.stop = stop;
		}
		
		@Override
		public String toString(){
			String locationStr = "";
			if(start != null){
				TokenSource source = start.getTokenSource();
				String sourceFile = source.getSourceName();
                if(sourceFile != null) {
                    sourceFile = sourceFile.substring(sourceFile.lastIndexOf(File.separator)+1, sourceFile.length());
                } else {
                    sourceFile = "stream";
                }
				locationStr = String.format("%s:%d", sourceFile, start.getLine());
			}
			return String.format("[%s %s] %s", level.toString(), locationStr, message);
		}
	}

	

	
}