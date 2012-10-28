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

public class Interpreter {
	public static String interpretLine(String text){
		if(text == null || text.equals(""))
			return "";
		
		String[] results = new String[]{interpretComment(text), interpretClassDeclaration(text), interpretEndClass(text),
				interpretMainMethod(text), interpretMethodStart(text), interpretMethodStartReturn(text),
				interpretMethodEnd(text), interpretVarDecType(text), interpretVarDecLit(text), interpretVarDecVar(text),
				interpretPrint(text)};
		
		for(String res : results)
			if(res != null)
				return res;

		return "null // Could not interpret the line: \""+text+"\"";
	}
	
	private static String interpretComment(String text){
		String pattern = "(P\\.)+S\\.(?<comment>.*)";
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "//"+matcher.group("comment");
		return null;
	}
	
	private static String interpretClassDeclaration(String text){
		String pattern = "Dear "+Regex.getClassRegex("A")+": "+Regex.getClassRegex("B");
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "public class "+Regex.normalizeClassName(matcher.group("B"))+" extends "
					+Regex.normalizeClassName(matcher.group("A"))+"{";
		return null;
	}

	private static String interpretEndClass(String text){
		String pattern = "Your faithful student, ([\\w\\s]+)"+Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "} // Author: "+matcher.group(1);
		return null;
	}
	
	private static String interpretMainMethod(String text){
		String pattern = "Today I learned "+Regex.getVarRegex("methodName")+Regex.PUNC;;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "public static void main(String[] args){ "+Regex.normalizeVarName(matcher.group("methodName"))+"(); }";
		return null;
	}
	
	private static String interpretMethodStart(String text){
		String pattern = "I learned "+Regex.getVarRegex("methodName")+Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "public static void "+Regex.normalizeVarName(matcher.group("methodName"))+"(){";
		return null;
	}
	
	private static String interpretMethodStartReturn(String text){
		String pattern = "I learned "+Regex.getVarRegex("methodName")+" with "+Regex.getTypeRegex("returnType")+Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "public static "+Regex.normalizeType(matcher.group("returnType"))+
					Regex.normalizeVarName(matcher.group("methodName"))+"(){";
		return null;
	}
	
	private static String interpretMethodEnd(String text){
		String pattern = "That's( all)? about "+Regex.getVarRegex("methodName")+Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "}";
		return null;
	}	

	private static String interpretVarDecType(String text){
		String pattern = "Did you know that "+Regex.getVarRegex("varName")+" is "+Regex.getTypeRegex("typeName")+
				Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return Regex.normalizeType(matcher.group("typeName"))+" "+Regex.normalizeVarName(matcher.group("varName"))+";";
		return null;
	}
	
	private static String interpretVarDecLit(String text){
		String pattern = "Did you know that "+Regex.getVarRegex("varName")+" is "+Regex.getTypeRegex("typeName")+
				" "+Regex.getLitRegex("literal")+Regex.PUNC;

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return Regex.normalizeType(matcher.group("typeName"))+" "+Regex.normalizeVarName(matcher.group("varName"))+"="+
						Regex.normalizeLiteral(matcher.group("literal"))+";";
		return null;
	}
	
	private static String interpretVarDecVar(String text){
		String pattern = "Did you know that "+Regex.getVarRegex("varName")+" is "+Regex.getTypeRegex("typeName")+
				" "+Regex.getVarRegex("var2")+Regex.PUNC;

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return Regex.normalizeType(matcher.group("typeName"))+" "+Regex.normalizeVarName(matcher.group("varName"))+"="+
						Regex.normalizeLiteral(matcher.group("var2"))+";";
		return null;
	}
	
	private static String interpretPrint(String text){
		String pattern = "I (said|wrote|sang|spoke|proclaimed) "+Regex.getVarRegex("varName")+Regex.PUNC;

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "System.out.println("+Regex.normalizeVarName(matcher.group("varName"))+");";
		return null;
	}
}