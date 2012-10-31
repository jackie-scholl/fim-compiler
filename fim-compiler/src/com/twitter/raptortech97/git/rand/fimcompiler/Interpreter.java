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

import java.util.*;
import java.util.regex.Matcher;

public class Interpreter {
	private static List<InterpretElement> interpreters;

	public static void setup(){
		interpreters = new ArrayList<InterpretElement>();
		try {			
			String regexComment = "(P\\.)+S\\.(?<comment>.*)";
			String regexClassDeclaration = "Dear "+Regex.CLASS.get("A")+": "+Regex.CLASS.get("B");
			String regexEndClass = "Your faithful student, ([\\w\\s]+)"+Regex.PUNC;
			String regexMainMethod = "Today I learned (how to )?"+Regex.VAR.get("methodName")+Regex.PUNC;
			String regexMethodStart = "I learned (how to )?"+Regex.VAR.get("methodName")+Regex.PUNC;
			String regexMethodStartReturn = "I learned (how to )?"+Regex.VAR.get("methodName")+" to get "+
					Regex.TYPE.get("returnType")+Regex.PUNC;
			String regexMethodEnd = "That's( all)? about (how to )?"+Regex.VAR.get("methodName")+Regex.PUNC;
			String regexMethodCallRes = "I did "+Regex.VAR.get("methodName")+" and gave the result to "+
					Regex.VAR.get("varName")+Regex.PUNC;
			String regexMethodCall =  "I did "+Regex.VAR.get("methodName")+Regex.PUNC;
			String regexVarDecType = "Did you know that "+Regex.VAR.get("varName")+" (is|are|was|were) "+
					Regex.TYPE.get("typeName")+Regex.PUNC;
			String regexVarDecVal =  "Did you know that "+Regex.VAR.get("varName")+" (is|are|was|were) "+
					Regex.TYPE.get("typeName")+" "+Regex.VAL.get("val")+Regex.PUNC;
			String regexVarReAssign =  "Did you know that "+Regex.VAR.get("varName")+" (then)? became (whether )?"+
					Regex.VAL.get("val")+Regex.PUNC;
			String regexVarMod = "(Did you know that )?"+Regex.VAR.get("varName")+" (was )?(then )?"+
					Regex.OP.get("op")+Regex.PUNC;
			String regexVarModPrompt = Regex.PRONOUN+" (asked) "+Regex.VAR.get("var")+" "+Regex.STRING.get("prompt")+
					Regex.PUNC;
			String regexIfStart = "((If)|(When)) "+Regex.VAL.get("bool")+"( then)?"+Regex.PUNC;
			String regexWhileStart = "((As long as)|(While)) "+Regex.VAL.get("bool")+Regex.PUNC;
			String regexElseIf = "((Otherwise)|(Or else)) ((if)|(when)) "+Regex.VAL.get("bool")+"( then)?"+Regex.PUNC;
			String regexElse = "((Otherwise)|(Or else))"+Regex.PUNC;
			String regexIfEnd = "That"+Regex.APOSTROPHE+"s what I ((did)|(would do))"+Regex.PUNC;
			String regexPrint = "I ((said)|(wrote)|(sang)|(spoke)|(proclaimed)|(thought)) "+Regex.STRING.get("value")+Regex.PUNC;
			String regexReturn = "Then "+Regex.PRONOUN+" ((got)|(get)) "+Regex.VAL.get("value")+Regex.PUNC;

			interpreters.add(new InterpretElement("normalizeComment",           regexComment));
			interpreters.add(new InterpretElement("normalizeClassDeclaration",  regexClassDeclaration));
			interpreters.add(new InterpretElement("normalizeEndClass",          regexEndClass));	
			interpreters.add(new InterpretElement("normalizeMainMethod",        regexMainMethod));			
			interpreters.add(new InterpretElement("normalizeMethodStart",       regexMethodStart));
			interpreters.add(new InterpretElement("normalizeMethodStartReturn", regexMethodStartReturn));
			interpreters.add(new InterpretElement("normalizeMethodEnd",         regexMethodEnd));
			interpreters.add(new InterpretElement("normalizeMethodCallRes",     regexMethodCallRes));
			interpreters.add(new InterpretElement("normalizeMethodCall",        regexMethodCall));
			interpreters.add(new InterpretElement("normalizeVarDecType",        regexVarDecType));
			interpreters.add(new InterpretElement("normalizeVarDecVal",         regexVarDecVal));
			interpreters.add(new InterpretElement("normalizeVarReAssign",       regexVarReAssign));
			interpreters.add(new InterpretElement("normalizeVarMod",            regexVarMod));
			interpreters.add(new InterpretElement("normalizeVarModPrompt",      regexVarModPrompt));
			interpreters.add(new InterpretElement("normalizeIfStart",           regexIfStart));
			interpreters.add(new InterpretElement("normalizeWhileStart",        regexWhileStart));
			interpreters.add(new InterpretElement("normalizeElseIf",            regexElseIf));
			interpreters.add(new InterpretElement("normalizeElse",              regexElse));
			interpreters.add(new InterpretElement("normalizeIfEnd",             regexIfEnd));
			interpreters.add(new InterpretElement("normalizePrint",             regexPrint));
			interpreters.add(new InterpretElement("normalizeReturn",            regexReturn));
			
			List<Integer> lengths = new ArrayList<Integer>();
			for(InterpretElement i : interpreters){
				lengths.add(i.getLength());
			}
			System.out.println(lengths);

		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public static String interpretLine(String text){
		if(text == null || text.equals(""))
			return "";

		List<String> results = new ArrayList<String>();
		for(InterpretElement i : interpreters)
			results.add(i.norm(text));

		for(String res : results)
			if(res != null)
				return res;

		return "null // Could not interpret the line: \""+text+"\"";
	}

	public static String normalizeComment(String text, Matcher matcher){
		return "//"+matcher.group("comment");
	}

	public static String normalizeClassDeclaration(String text, Matcher matcher){
		return "public class "+Regex.CLASS.norm(matcher.group("B"))+" extends "+Regex.CLASS.norm(matcher.group("A"))+"{";
	}
	public static String normalizeEndClass(String text, Matcher matcher){
		return "} // Author: "+matcher.group(1);
	}
	public static String normalizeMainMethod(String text, Matcher matcher){
		return "public static void main(String[] args){ "+Regex.VAR.norm(matcher.group("methodName"))+"(); }";
	}
	public static String normalizeMethodStartReturn(String text, Matcher matcher){
		return "public static "+Regex.TYPE.norm(matcher.group("returnType"))+" "+
				Regex.VAR.norm(matcher.group("methodName"))+"(){";
	}
	public static String normalizeMethodStart(String text, Matcher matcher){
		return "public static void "+Regex.VAR.norm(matcher.group("methodName"))+"(){";
	}
	public static String normalizeMethodEnd(String text, Matcher matcher){
		return "}";
	}
	public static String normalizeMethodCallRes(String text, Matcher matcher){
		return Regex.VAR.norm(matcher.group("varName"))+"="+Regex.VAR.norm(matcher.group("methodName"))+"();";
	}
	public static String normalizeMethodCall(String text, Matcher matcher){
		return Regex.VAR.norm(matcher.group("methodName"))+"();";
	}
	public static String normalizeVarDecType(String text, Matcher matcher){
		return Regex.TYPE.norm(matcher.group("typeName"))+" "+Regex.VAR.norm(matcher.group("varName"))+";";
	}
	public static String normalizeVarDecVal(String text, Matcher matcher){
		return Regex.TYPE.norm(matcher.group("typeName"))+" "+Regex.VAR.norm(matcher.group("varName"))+
				"="+Regex.VAL.norm(matcher.group("val"))+";";
	}
	public static String normalizeVarReAssign(String text, Matcher matcher){
		return Regex.VAR.norm(matcher.group("varName"))+" = "+Regex.VAL.norm(matcher.group("val"))+";";
	}
	public static String normalizeVarMod(String text, Matcher matcher){
		return Regex.VAR.norm(matcher.group("varName"))+Regex.OP.norm(matcher.group("op"))+";";
	}
	public static String normalizeVarModPrompt(String text, Matcher matcher){
		return "System.out.print("+Regex.STRING.norm(matcher.group("prompt"))+"); "+
				Regex.VAR.norm(matcher.group("var"))+" = new java.util.Scanner(System.in).nextLine();";
	}
	public static String normalizeIfStart(String text, Matcher matcher){
		return "if("+Regex.VAL.norm(matcher.group("bool"))+"){";
	}
	public static String normalizeWhileStart(String text, Matcher matcher){
		return "while("+Regex.VAL.norm(matcher.group("bool"))+"){";
	}
	public static String normalizeElseIf(String text, Matcher matcher){
		return "} else if("+Regex.VAL.norm(matcher.group("bool"))+"){";
	}
	public static String normalizeElse(String text, Matcher matcher){
		return "} else {";
	}
	public static String normalizeIfEnd(String text, Matcher matcher){
		return "}";
	}
	public static String normalizePrint(String text, Matcher matcher){
		return "System.out.println("+Regex.STRING.norm(matcher.group("value"))+");";
	}
	public static String normalizeReturn(String text, Matcher matcher){
		return "return "+Regex.VAL.norm(matcher.group("value"))+";";
	}
}