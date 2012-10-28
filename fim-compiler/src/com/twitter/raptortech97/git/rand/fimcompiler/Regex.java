/*
 * Author: Jackson Scholl 
 *   Twitter: @raptortech97
 *   Email:   shades97@gmail.com
 *   Google+: https://plus.google.com/113116905639811214312/
 *   GitHub:  raptortech-js
 * 
 * A FiM++ compiler/interpreter written in Java. Compiles the FiM++ to Java as an intermediary.
 * 
 */

package com.twitter.raptortech97.git.rand.fimcompiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
	public static String PUNC = "[,?\\.\\!]";
	public static String QUOTE_MARK = "(\\u0022|\\u0029|\\u2018|\\u2019)";
	
	public static String normalizeClassName(String str){
		Matcher matcher = Pattern.compile(getClassRegex("A")).matcher(str);
		if(!matcher.matches())
			System.err.println("Error. Bad class name.");

		str = str.replace(" ", "_"); // Replace all spaces with underscores in class names.
		return str;
	}
	
	public static String normalizeVarName(String str){
		Matcher classMatcher = Pattern.compile(getVarRegex("A")).matcher(str);
		if(!classMatcher.matches())
			System.err.println("Error. Bad variable name.");

		str = str.replace(" ", "_"); // Replace all spaces with underscores in variable names.
		return str;
	}

	public static String normalizeType(String str){
		if(str.startsWith("a "))
			str = str.substring(1);
		else if(str.startsWith("an "))
			str = str.substring(2);
		else if(str.startsWith("the "))
			str = str.substring(3);
		str = str.replace("logical", "boolean");
		str = str.replace("argument", "boolean");
		str = str.replace("number", "double");
		str = str.replace("name", "String");
		str = str.replace("character", "char");
		str = str.replace("letter", "char");
		str = str.replace("es", "[]");
		str = str.replace("s", "[]");
		return str;
	}

	private static String LITERAL_NUMBER_REGEX = "\\d+(\\.\\d+)?";
	private static String LITERAL_STRING_REGEX = "(?<quoteMark>"+QUOTE_MARK+")(?<quote>.+)\\k<quoteMark>";
	public static String normalizeLiteral(String str){
		Matcher matcher = Pattern.compile(getLitRegex("A")).matcher(str);
		if(!matcher.matches())
			System.err.println("Error. Bad literal.");

		matcher = Pattern.compile(LITERAL_NUMBER_REGEX).matcher(str);
		if(matcher.matches())
			return str;

		matcher = Pattern.compile(LITERAL_STRING_REGEX).matcher(str);
		if(matcher.matches())
			return "\""+matcher.group("quote")+"\"";
		return null;
	}
	
	public static String getVarRegex(String str){
		return "(?<"+str+">(a |an |the )?[A-Z][\\w ']*)";
	}
	public static String getTypeRegex(String str){
		return "(?<"+str+">(a |the |an )*((logical)|(argument)|(number)|(name)|(character)|(letter))(s|es)*)";
	}
	public static String getClassRegex(String str){
		return "(?<"+str+">[A-Z]+[\\w ]*)";
	}
	public static String getLitRegex(String str){
		return "(?<"+str+">"+LITERAL_NUMBER_REGEX+"|"+LITERAL_STRING_REGEX+")";
	}
}