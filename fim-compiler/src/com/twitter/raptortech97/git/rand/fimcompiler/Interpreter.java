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
	private static List<Element> interpreters;

	public static void setup(){
		interpreters = new ArrayList<Element>();
		try {		
			add(commentSimple, commentRegex, "commentNorm");
			interpreters.add(new NormalElement("comment", commentSimple, commentRegex, "commentNorm",
					Interpreter.class));
			
			add(classDecSimple, classDecRegex, "classDecNorm");
			add(endClassSimple, endClassRegex, "endClassNorm");
			add(mainMethodSimple, mainMethodRegex, "mainMethodNorm");
			add(methodStartArgsSimple, methodStartArgsRegex, "methodStartArgsNorm");
			add(methodEndSimple, methodEndRegex, "methodEndNorm");
			add(methodCallSimple, methodCallRegex, "methodCallNorm");
			add(varDecTypeSimple, varDecTypeRegex, "varDecTypeNorm");
			add(varDecArrSimple, varDecArrRegex, "varDecArrNorm");
			add(varDecValSimple, varDecValRegex, "varDecValNorm");
			add(varReAssignSimple, varReAssignRegex, "varReAssignNorm");
			add(varModSimple, varModRegex, "varModNorm");
			add(varModPromptSimple, varModPromptRegex, "varModPromptNorm");
			add(ifStartSimple, ifStartRegex, "ifStartNorm");
			add(whileStartSimple, whileStartRegex, "whileStartNorm");
			add(elseIfSimple, elseIfRegex, "elseIfNorm");
			add(elseSimple, elseRegex, "elseNorm");
			add(ifEndSimple, ifEndRegex, "ifEndNorm");
			add(printSimple, printRegex, "printNorm");
			add(returnSimple, returnRegex, "returnNorm");

		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	private static void add(String simple, String regex, String norm) throws NoSuchMethodException, SecurityException{
		interpreters.add(new NormalElement(norm.replaceAll("Norm\\z", ""), simple+Regex.PUNC, regex+Regex.PUNC, norm,
				Interpreter.class));
	}

	public static String interpretLine(String text){
		if(text == null || text.equals(""))
			return "";
		for(Element i : interpreters){
			String res = i.norm(text);
			if(res != null)
				return res;
		}
		return "null // Could not interpret the line: \""+text+"\"";
	}

	private static String commentSimple = "(P\\.)+S\\.(?<comment>.*)";
	private static String commentRegex = "(P\\.)+S\\.(?<comment>.*)";
	public static String commentNorm(Matcher matcher){
		return "//"+matcher.group("comment");
	}

	private static String classDecSimple = "Dear (?<simple=className>): (?<simple=className>)";
	private static String classDecRegex = "Dear (?<super>(?<simple=className>)): (?<name>(?<simple=className>))";
	public static String classDecNorm(Matcher matcher){
		return "public class "+Regex.CLASS.norm(matcher.group("name"))+" extends "+Regex.CLASS.norm(matcher.group("super"))+"{";
	}
	
	private static String endClassSimple = "Your faithful student, ([\\w\\s]+)";
	private static String endClassRegex = "Your faithful student, (?<name>[\\w\\s]+)";
	public static String endClassNorm(Matcher matcher){
		return "} // Author: "+matcher.group("name");
	}
	
	private static String mainMethodSimple = "Today I learned (how to )?(?<simple=var>)";
	private static String mainMethodRegex = "Today I learned( how to)? (?<methodName>(?<simple=var>))";
	public static String mainMethodNorm(Matcher matcher){
		return "public static void main(String[] args){ "+Regex.VAR.norm(matcher.group("methodName"))+"(); }";
	}
	
	private static String methodStartArgsSimple = "I learned( how to)? (?<simple=varName>)( with (?<simple=type>) " +
			"(?<simple=val>)( and (?<simple=type>) (?<simple=var>))*?)?( to get (?<simple=type>))?";
	private static String methodStartArgsRegex = "I learned( how to)? (?<methodName>(?<simple=varName>))( with " +
			"(?<arg0Type>(?<simple=type>)) (?<arg0Name>(?<simple=var>))(?<otherArgs>( and (?<simple=type>) " +
			"(?<simple=val>))*?))?( to get (?<returnType>(?<simple=type>)))?";
	private static String argsPattern = " and (?<curType>(?<simple=type>)) (?<curName>(?<simple=var>))" +
			"(?<otherArgs>( and (?<simple=val>))*?)";
	public static String methodStartArgsNorm(Matcher matcher){
		String res="public static ";
		if(matcher.group("returnType") != null)
			res += Regex.TYPE.norm(matcher.group("returnType"));
		else
			res += "void";
		res += " "+Regex.VAR_NAME.norm(matcher.group("methodName"))+"(";
		
		if(matcher.group("arg0Name") != null){
			res += Regex.TYPE.norm(matcher.group("arg0Type"));
			res += " "+Regex.VAR_NAME.norm(matcher.group("arg0Name"));
		}
		String args = matcher.group("otherArgs");
		while((args != null)&&(!args.equals(""))){
			Matcher m = Regex.myPatternCompile(argsPattern).matcher(args);
			m.matches();
			res += ","+Regex.TYPE.norm(m.group("curType"));
			res += " "+Regex.VAR.norm(m.group("curName"));
			args = m.group("otherArgs");
		}
		res += "){";
		return res;
	}
	
	private static String methodEndSimple = "That's( all)? about( how to)? (?<simple=var>)";
	private static String methodEndRegex = "That's( all)? about( how to)? (?<methodName>(?<simple=var>))";
	public static String methodEndNorm(Matcher matcher){
		return "}";
	}
	
	private static String methodCallSimple = "I ((remembered)|(did)|(would)) "+Regex.METHOD_CALL.getSimple();
	private static String methodCallRegex = "I ((remembered)|(did)|(would)) (?<method>"+Regex.METHOD_CALL.getSimple()+")";
	public static String methodCallNorm(Matcher matcher){
		return Regex.METHOD_CALL.norm(matcher.group("method"))+";";
	}
		
	private static String varDecTypeSimple = "Did you know that (?<simple=var>) ((is)|(are)|(was)|(were)|(has)|(had)) "+
			"(?<simple=type>)";
	private static String varDecTypeRegex = "Did you know that (?<varName>(?<simple=var>)) ((is)|(are)|(was)|"+
			"(were)|(has)|(had)) (?<typeName>(?<simple=type>))";
	public static String varDecTypeNorm(Matcher matcher){
		return Regex.TYPE.norm(matcher.group("typeName"))+" "+Regex.VAR.norm(matcher.group("varName"))+";";
	}
	
	private static String varDecArrSimple = "Did you know that (?<simple=varName>) ((is)|(are)|(was)|(were)|(has)|(had)) "+
			"(\\d+) (?<simple=type>))";
	private static String varDecArrRegex = "Did you know that (?<varName>(?<simple=varName>)) ((is)|(are)|(was)|(were)|" +
			"(has)|(had)) (?<number>\\d+) (?<typeName>(?<simple=type>))";
	public static String varDecArrNorm(Matcher matcher){
		return Regex.TYPE.norm(matcher.group("typeName"))+" "+Regex.VAR.norm(matcher.group("varName"))+" = new "+
				Regex.TYPE.norm(matcher.group("typeName")).replace("[]", "["+matcher.group("number")+"]")+";";
	}
	
	private static String varDecValSimple = "Did you know that (?<simple=var>) ((is)|(are)|(was)|(were)) " +
			"(?<simple=type>) (?<simple=val>)";
	private static String varDecValRegex = "Did you know that (?<varName>(?<simple=var>)) ((is)|(are)|(was)|(were)) " +
			"(?<typeName>(?<simple=type>)) (?<val>(?<simple=val>))";
	public static String varDecValNorm(Matcher matcher){
		return Regex.TYPE.norm(matcher.group("typeName"))+" "+Regex.VAR.norm(matcher.group("varName"))+
				"="+Regex.VAL.norm(matcher.group("val"))+";";
	}

	private static String varReAssignSimple = "(Did you know that )?(?<varName>(?<simple=var>)) (then )?became"+
			"( whether)? (?<val>(?<simple=val>))";
	private static String varReAssignRegex = "(Did you know that )?(?<varName>(?<simple=var>)) (then )?became"+
			"( whether)? (?<val>(?<simple=val>))";
	public static String varReAssignNorm(Matcher matcher){
		return Regex.VAR.norm(matcher.group("varName"))+" = "+Regex.VAL.norm(matcher.group("val"))+";";
	}
	
	private static String varModSimple = "(Did you know that )?(?<simple=var>) (was )?(then )?(?<simple=op>)";
	private static String varModRegex = "(Did you know that )?(?<varName>(?<simple=var>)) (was )?(then )?(?<op>(?<simple=op>))";
	public static String varModNorm(Matcher matcher){
		return Regex.VAR.norm(matcher.group("varName"))+Regex.OP.norm(matcher.group("op"))+";";
	}
	
	private static String varModPromptSimple = Regex.PRONOUN+" asked (?<simple=var>) (?<simple=string>)";
	private static String varModPromptRegex = Regex.PRONOUN+" asked (?<var>(?<simple=var>)) (?<prompt>(?<simple=string>))";
	public static String varModPromptNorm(Matcher matcher){
		return "System.out.print("+Regex.STRING.norm(matcher.group("prompt"))+"); "+
				Regex.VAR.norm(matcher.group("var"))+" = new java.util.Scanner(System.in).nextLine();";
	}
	
	private static String ifStartSimple = "((If)|(When)) (?<simple=val>)( then)?";
	private static String ifStartRegex = "((If)|(When)) (?<bool>(?<simple=val>))( then)?";	
	public static String ifStartNorm(Matcher matcher){
		return "if("+Regex.VAL.norm(matcher.group("bool"))+"){";
	}
	
	private static String whileStartSimple = "((While)|(As long as)) (?<simple=val>)";
	private static String whileStartRegex = "((While)|(As long as)) (?<bool>(?<simple=val>))";
	public static String whileStartNorm(Matcher matcher){
		return "while("+Regex.VAL.norm(matcher.group("bool"))+"){";
	}
	
	private static String elseIfSimple = "((Otherwise)|(Or else)) ((if)|(when)) (?<simple=val>)( then)?";
	private static String elseIfRegex = "((Otherwise)|(Or else)) ((if)|(when)) (?<bool>(?<simple=val>))( then)?";
	public static String elseIfNorm(Matcher matcher){
		return "} else if("+Regex.VAL.norm(matcher.group("bool"))+"){";
	}
	
	private static String elseSimple = "((Otherwise)|(Or else))";
	private static String elseRegex = "((Otherwise)|(Or else))";
	public static String elseNorm(Matcher matcher){
		return "} else {";
	}
	
	private static String ifEndSimple = "That"+Regex.APOSTROPHE+"s what I ((did)|(would do))";
	private static String ifEndRegex = "That"+Regex.APOSTROPHE+"s what I ((did)|(would do))";
	public static String ifEndNorm(Matcher matcher){
		return "}";
	}
	
	static String printSimple = "I ((said)|(wrote)|(sang)|(spoke)|(proclaimed)|(thought)) (?<simple=string>)";
	private static String printRegex = "I ((said)|(wrote)|(sang)|(spoke)|(proclaimed)|(thought)) (?<value>(?<simple=string>))";
	public static String printNorm(Matcher matcher){
		return "System.out.println("+Regex.STRING.norm(matcher.group("value"))+");";
	}
	
	private static String returnSimple = "Then "+Regex.PRONOUN+" ((got)|(get)) (?<simple=val>)";
	private static String returnRegex = "Then "+Regex.PRONOUN+" ((got)|(get)) (?<value>(?<simple=val>))";
	public static String returnNorm(Matcher matcher){
		return "return "+Regex.VAL.norm(matcher.group("value"))+";";
	}
}