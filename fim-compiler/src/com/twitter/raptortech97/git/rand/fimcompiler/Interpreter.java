package com.twitter.raptortech97.git.rand.fimcompiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {
	private static String PUNC_REGEX = "[,?\\.\\!]";
	private static String QUOTE_MARK = "\\u0022";

	static String interpretLine(String text){
		String[] results = new String[]{interpretClassDeclaration(text), interpretEndClass(text),
				interpretVarDecType(text), interpretVarDecTypeVal(text)};
		
		for(String res : results)
			if(res != null)
				return res;

		return "null // Could not interpret";
	}
	
	static String interpretVarDecType(String text){
		String pattern = "Did you know that "+getVarRegex("varName")+" is a "+getTypeRegex("typeName")+PUNC_REGEX;

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.find()){
			return normalizeType(matcher.group("typeName"))+" "+normalizeVarName(matcher.group("varName"))+";";
		}
		return null;
	}
	
	private static String interpretVarDecTypeVal(String text){
		String pattern = "Did you know that "+getVarRegex("varName")+" is the "+getTypeRegex("typeName")+
				" (?<literal>(\\d+|\\u0022([\\w\\s]+)\\u0022))"+PUNC_REGEX;

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.find()){
			return normalizeType(matcher.group("typeName"))+" "+normalizeVarName(matcher.group("varName"))+"="+
						matcher.group("literal")+";";
		}
		return null;
	}
	
	private static String interpretPrint(String text){
		String pattern = "I said "+getVarRegex("varName")+PUNC_REGEX;

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.find()){
			return normalizeType(matcher.group("typeName"))+" "+normalizeVarName(matcher.group("varName"))+";";
		}
		return null;
	}

	static String interpretClassDeclaration(String text){
		String pattern = "Dear "+getClassRegex("A")+": "+getClassRegex("B");
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.find())
			return "public class "+normalizeClassName(matcher.group("B"))+" extends "
			+normalizeClassName(matcher.group("A"))+"{";
		return null;
	}

	static String interpretEndClass(String text){
		String pattern = "Your faithful student, ([\\w\\s]+)"+PUNC_REGEX;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.find())
			return "} // Author: "+matcher.group(1);
		return null;
	}

	static String CLASS_REGEX = "(([A-Z]+[\\w]*)+( [A-Z]+[\\w]*)*)";
	static String normalizeClassName(String str){
		String name = "(([A-Z]+[\\w]*)+( [A-Z]+[\\w]*)*)";
		Matcher matcher = (Pattern.compile(name)).matcher(str);
		if(!matcher.find())
			System.err.println("Error. Bad class name.");

		str = str.replace(" ", "_"); // Replace all spaces with underscores in class names.
		return str;
	}

	static String VAR_REGEX = "[\\w ']+";
	static String normalizeVarName(String str){
		String name = "([\\w ']+)";
		Matcher classMatcher = (Pattern.compile(name)).matcher(str);
		if(!classMatcher.find())
			System.err.println("Error. Bad variable name.");

		str = str.replace(" ", "_"); // Replace all spaces with underscores in variable names.
		return str;
	}

	static String TYPE_REGEX = "((logical)|(argument)|(number)|(name)|(character)|(letter))(s|es)*";
	static String normalizeType(String str){
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
	
	static String getVarRegex(String str){
		return "(?<"+str+">"+VAR_REGEX+")";
	}
	static String getTypeRegex(String str){
		return "(?<"+str+">"+TYPE_REGEX+")";
	}
	static String getClassRegex(String str){
		return "(?<"+str+">"+CLASS_REGEX+")";
	}
}
