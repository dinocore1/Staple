package com.devsmart.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class StringUtils {

	public static boolean isEmptyString(String str){
		return str == null || str.trim().length() == 0;
	}
	
	public static String delimStr(Collection<? extends Object> str, String delimiter){
		StringBuilder retval = new StringBuilder();
		
		Iterator<? extends Object> it = str.iterator();
		while(it.hasNext()){
			Object item = it.next();
			retval.append(item.toString());
			if(it.hasNext()){
				retval.append(delimiter);
			}
			
		}
		
		
		return retval.toString();
	}

	public static String delimStr(Object[] arguments, String delimiter) {
		return delimStr(Arrays.asList(arguments), delimiter);
	}
	
}
