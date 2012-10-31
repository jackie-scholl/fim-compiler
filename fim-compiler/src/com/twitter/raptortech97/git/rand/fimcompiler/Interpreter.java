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
		
		String[] results = new String[]{interpretComment(text),
				interpretClassDeclaration(text), interpretEndClass(text),
				interpretMainMethod(text), interpretMethodStart(text), interpretMethodStartReturn(text),
						interpretMethodEnd(text), interpretMethodCallRes(text), interpretMethodCall(text), 
				interpretVarDecType(text), interpretVarDecVal(text), interpretVarReAssign(text), interpretVarMod(text),
								interpretVarModComp(text),
				interpretIfStart(text), interpretWhileStart(text), interpretElseIf(text), interpretElse(text), interpretIfEnd(text),
				interpretPrint(text), interpretReturn(text)};
		
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
			return "public static void main(String[] args){ "+Regex.normalizeVariable(matcher.group("methodName"))+"(); }";
		return null;
	}
	
	private static String interpretMethodStart(String text){
		String pattern = "I learned "+Regex.getVarRegex("methodName")+Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "public static void "+Regex.normalizeVariable(matcher.group("methodName"))+"(){";
		return null;
	}
	
	private static String interpretMethodStartReturn(String text){
		String pattern = "I learned "+Regex.getVarRegex("methodName")+" to get "+Regex.getTypeRegex("returnType")+Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "public static "+Regex.normalizeType(matcher.group("returnType"))+" "+
					Regex.normalizeVariable(matcher.group("methodName"))+"(){";
		return null;
	}
	
	private static String interpretMethodEnd(String text){
		String pattern = "That's( all)? about (a |an |the )?"+Regex.getVarRegex("methodName")+Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "}";
		return null;
	}	

	private static String interpretVarDecType(String text){
		String pattern = "Did you know that "+Regex.getVarRegex("varName")+" (was|were) "+
				Regex.getTypeRegex("typeName")+Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return Regex.normalizeType(matcher.group("typeName"))+" "+Regex.normalizeVariable(matcher.group("varName"))+";";
		return null;
	}
	
	private static String interpretVarDecVal(String text){
		String pattern = "Did you know that "+Regex.getVarRegex("varName")+" (is|are|was|were) "+
				Regex.getTypeRegex("typeName")+" "+Regex.getVal2Regex("val")+Regex.PUNC;

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return Regex.normalizeType(matcher.group("typeName"))+" "+Regex.normalizeVariable(matcher.group("varName"))+
					"="+Regex.normalizeValue2(matcher.group("val"))+";";
		return null;
	}
	
	private static String interpretVarReAssign(String text){
		String pattern = "Did you know that "+Regex.getVarRegex("varName")+" (then)? became "+
				Regex.getVal2Regex("val")+Regex.PUNC;

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return Regex.normalizeVariable(matcher.group("varName"))+" = "+Regex.normalizeValue2(matcher.group("val"))+";";
		return null;
	}
	
	private static String interpretVarMod(String text){
		String pattern = "Did you know that "+Regex.getVarRegex("varName")+" (was )?(then )?"+
				Regex.getOpRegex("op")+Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return Regex.normalizeVariable(matcher.group("varName"))+Regex.normalizeOperation(matcher.group("op"))+";";
		return null;
	}
	
	private static String interpretVarModComp(String text){
		String pattern = "Did you know that "+Regex.getVarRegex("varName")+"( then)? became whether "+
				Regex.getCompRegex("comp")+Regex.PUNC;
		
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		matcher.find();
		if(matcher.matches()){
			return Regex.normalizeVariable(matcher.group("varName"))+" = ("+
				Regex.normalizeComparator(matcher.group("comp"))+");";
		}
		return null;
	}
	
	private static String interpretMethodCall(String text){
		String pattern = "I did "+Regex.getVarRegex("methodName")+Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return Regex.normalizeVariable(matcher.group("methodName"))+"();";
		return null;
	}
	
	private static String interpretMethodCallRes(String text){
		String pattern = "I did "+Regex.getVarRegex("methodName")+" and gave the result to "+
				Regex.getVarRegex("varName")+Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return Regex.normalizeVariable(matcher.group("varName"))+"="+Regex.normalizeVariable(matcher.group("methodName"))+"();";
		return null;
	}
	
	private static String interpretIfStart(String text){
		String pattern = "((If)|(When)) "+Regex.getVal2Regex("bool")+"( then)?"+Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "if("+Regex.normalizeValue2(matcher.group("bool"))+"){";
		return null;
	}
	
	static String interpretWhileStart(String text){
		String pattern = "((As long as)|(While)) "+Regex.getVal2Regex("bool")+Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "while("+Regex.normalizeValue2(matcher.group("bool"))+"){";
		return null;
	}
	
	private static String interpretElse(String text){
		String pattern = "((Otherwise)|(Or else))"+Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "} else {";
		return null;
	}
	
	private static String interpretElseIf(String text){
		String pattern = "((Otherwise)|(Or else)) ((if)|(when)) "+Regex.getVal2Regex("bool")+"( then)?"+Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches()){
			return "} else if("+Regex.normalizeValue2(matcher.group("bool"))+"){";
		}
		return null;
	}
	
	static String interpretIfEnd(String text){
		String pattern = "That"+Regex.APOSTROPHE+"s what I ((did)|(would do))"+Regex.PUNC;
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "}";
		return null;
	}
	
	static String interpretPrint(String text){
		String pattern = "I (said|wrote|sang|spoke|proclaimed|thought) "+Regex.getString3Regex("value")+Regex.PUNC;

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "System.out.println("+Regex.normalizeString3(matcher.group("value"))+");";
		return null;
	}
	
	private static String interpretReturn(String text){
		String pattern = "Then (I|you||he|she|we|you'll|they) (got|get) "+Regex.getVal2Regex("value")+Regex.PUNC;

		Matcher matcher = Pattern.compile(pattern).matcher(text);
		if(matcher.matches())
			return "return "+Regex.normalizeValue2(matcher.group("value"))+";";
		return null;
	}
}