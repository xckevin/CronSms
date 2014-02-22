package com.xckevin.cronsms.util;

import java.util.regex.Pattern;

public class NumberUtils {
	
	private static Pattern pattern = Pattern.compile("^[-\\+]?[\\d]+$");

	/*
	  * 判断是否为整数 
	  * @param str 传入的字符串 
	  * @return 是整数返回true,否则返回false 
	*/


	  public static boolean isInteger(String str) {  
	    return pattern.matcher(str).matches();  
	  }

}
