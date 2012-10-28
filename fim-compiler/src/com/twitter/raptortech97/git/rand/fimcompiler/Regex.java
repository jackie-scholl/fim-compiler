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
		Matcher matcher = Pattern.compile(getVarRegex("A")).matcher(str);
		if(!matcher.matches())
			System.err.println("Error. Bad variable name.");

		str = matcher.group("XA");
		str = str.replace(" ", "_"); // Replace all spaces with underscores in variable names.
		return str;
	}

	public static String normalizeType(String str){
		if(str.startsWith("a "))
			str = str.substring(2);
		else if(str.startsWith("an "))
			str = str.substring(3);
		else if(str.startsWith("the "))
			str = str.substring(4);
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
	private static String LITERAL_BOOL_REGEX = "((true)|(false)|(correct)|(incorrect))";
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
		
		matcher = Pattern.compile(LITERAL_BOOL_REGEX).matcher(str);
		if(matcher.matches()){
			if(str.equals("true") || str.equals("correct"))
				return "true";
			else if(str.equals("false") || str.equals("incorrect"))
				return "false";
		}
		return null;
	}
	
	public static String normalizeValue(String str){
		Matcher matcher = Pattern.compile(getLitRegex("A")).matcher(str);
		if(matcher.matches())
			return normalizeLiteral(str);
		
		matcher = Pattern.compile(getVarRegex("A")).matcher(str);
		if(matcher.matches())
			return normalizeVarName(str);
		return null;
	}
	
	public static String normalizeOperation(String str){
		String pattern = getOpRegex("B");
		Matcher matcher = Pattern.compile(pattern).matcher(str);
		matcher.find();
		String operation = matcher.group("YB");
		String s = "?";
		if(operation.equals("increased"))
			s = " += ";
		else if(operation.equals("decreased"))
			s = " -= ";
		else if(operation.equals("multiplied"))
			s = " *= ";
		else if(operation.equals("divided"))
			s = " /= ";
		else if(operation.equals("anded"))
			s = " &= ";
		else if(operation.equals("ored"))
			s = " |= ";
		else if(operation.equals("xored"))
			s = " ^= ";
		return s+normalizeValue(matcher.group("BXOp1"));
	}
	
	public static String normalizeComparator(String str){
		String pattern = getCompRegex("A");
		Matcher matcher = Pattern.compile(pattern).matcher(str);
		matcher.find();
		String comp = matcher.group("YA");
		String s = "?";
		if(comp.equals("is"))
			s = "==";
		else if (comp.equals("is not"))
			s = "!=";
		
		return s;
	}
	
	public static String getVarRegex(String str){
		return "(?<"+str+">(a |an |the )??_(?<X"+str+">[\\w ']+)_)";
	}
	public static String getTypeRegex(String str){
		return "(?<"+str+">(a |the |an )*((logical)|(argument)|(number)|(name)|(character)|(letter))(s|es)*)";
	}
	public static String getClassRegex(String str){
		return "(?<"+str+">[A-Z]+[\\w ]*)";
	}
	public static String getLitRegex(String str){
		return "(?<"+str+">"+LITERAL_NUMBER_REGEX+"|"+LITERAL_STRING_REGEX+"|"+LITERAL_BOOL_REGEX+")";
	}
	public static String getValRegex(String str){
		return "(?<"+str+">"+"("+getLitRegex(str+"XVal1")+"|"+getVarRegex(str+"XVal2")+"))";
	}
	public static String getOpRegex(String str){
		return "(?<"+str+">((?<Y"+str+">((increased)|(decreased)|(multiplied)|(divided)|(anded)|(ored)|(xored))) by )"+getValRegex(str+"XOp1")+")";
	}
	public static String getCompRegex(String str){
		return "(?<"+str+">"+getValRegex(str+"XComp1")+" (?<Y"+str+">is( not)?)"+getValRegex(str+"XComp2")+")";
	}
}