package com.twitter.raptortech97.git.rand.fimcompiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {
	private static String PUNC_REGEX = "[,?\\.\\!]";
	private static String QUOTE_MARK = "\\u0022";

	public static String interpretLine(String text){
		if(text == null || text.equals(""))
			return "";
		
		String[] results = new String[]{interpretComment(text), interpretClassDeclaration(text), interpretEndClass(text),
				interpretVarDecType(text), interpretVarDecTypeVal(text), interpretPrint(text),
				interpretMethodStart(text), interpretMethodEnd(text), interpretMainMethod(text)};
		
		for(String res : results)
			if(res != null)
				return res;

		return "null // Could not interpret";
	}
	
	private static String interpretVarDecType(String text){
		String pattern = "Did you know that "+getVarRegex("varName")+" is a "+getTypeRegex("typeName")+PUNC_REGEX;

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return normalizeType(matcher.group("typeName"))+" "+normalizeVarName(matcher.group("varName"))+";";
		return null;
	}
	
	private static String interpretVarDecTypeVal(String text){
		String pattern = "Did you know that "+getVarRegex("varName")+" is the "+getTypeRegex("typeName")+
				" (?<literal>(\\d+|\\u0022([\\w\\s]+)\\u0022))"+PUNC_REGEX;

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return normalizeType(matcher.group("typeName"))+" "+normalizeVarName(matcher.group("varName"))+"="+
						matcher.group("literal")+";";
		return null;
	}
	
	private static String interpretPrint(String text){
		String pattern = "I said "+getVarRegex("varName")+PUNC_REGEX;

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "System.out.println("+normalizeVarName(matcher.group("varName"))+");";
		return null;
	}
	
	private static String interpretComment(String text){
		String pattern = "(P.)+S.(?<comment>[\\w\\s]*)";

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "//"+matcher.group("comment");
		return null;
	}
	
	private static String interpretMethodStart(String text){
		String pattern = "I learned "+getVarRegex("methodName")+PUNC_REGEX;

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "public static void "+normalizeVarName(matcher.group("methodName"))+"(){";
		return null;
	}
	
	private static String interpretMethodEnd(String text){
		String pattern = "That's (all )?about "+getVarRegex("methodName")+PUNC_REGEX;

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "}";
		return null;
	}
	
	private static String interpretMainMethod(String text){
		String pattern = "Today I learned "+getVarRegex("methodName")+PUNC_REGEX;

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "public static void main(String[] args){ "+normalizeVarName(matcher.group("methodName"))+"(); }";
		return null;
	}

	private static String interpretClassDeclaration(String text){
		String pattern = "Dear "+getClassRegex("A")+": "+getClassRegex("B");
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "public class "+normalizeClassName(matcher.group("B"))+" extends "
					+normalizeClassName(matcher.group("A"))+"{";
		return null;
	}

	private static String interpretEndClass(String text){
		String pattern = "Your faithful student, ([\\w\\s]+)"+PUNC_REGEX;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "} // Author: "+matcher.group(1);
		return null;
	}

	private static String CLASS_REGEX = "(([A-Z]+[\\w]*)+( [A-Z]+[\\w]*)*)";
	private static String normalizeClassName(String str){
		Matcher matcher = (Pattern.compile(CLASS_REGEX)).matcher(str);
		if(!matcher.matches())
			System.err.println("Error. Bad class name.");

		str = str.replace(" ", "_"); // Replace all spaces with underscores in class names.
		return str;
	}

	private static String VAR_REGEX = "[\\w ']+";
	private static String normalizeVarName(String str){
		Matcher classMatcher = (Pattern.compile(VAR_REGEX)).matcher(str);
		if(!classMatcher.matches())
			System.err.println("Error. Bad variable name.");

		str = str.replace(" ", "_"); // Replace all spaces with underscores in variable names.
		return str;
	}

	private static String TYPE_REGEX = "((logical)|(argument)|(number)|(name)|(character)|(letter))(s|es)*";
	private static String normalizeType(String str){
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
	
	private static String getVarRegex(String str){
		return "(?<"+str+">"+VAR_REGEX+")";
	}
	private static String getTypeRegex(String str){
		return "(?<"+str+">"+TYPE_REGEX+")";
	}
	private static String getClassRegex(String str){
		return "(?<"+str+">"+CLASS_REGEX+")";
	}
}