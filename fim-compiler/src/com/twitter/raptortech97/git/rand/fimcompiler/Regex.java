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

import java.util.regex.*;

public class Regex {
	public static String QUOTE_MARK = "[\\u0022\\u0027\\u0029\\u2018\\u2019\\u201C\\u201D]";
	public static String APOSTROPHE = "[\\u0027\\u2019\\u02BC\\u2019]";
	public static String COMMA = "(\\u002C|\\uFE10|\\uFE11|\\uFE50|\\uFE51|\\uFF0C)";
	public static String PUNC = "[,:\\?\\.\\!"+COMMA+"]";
	public static String PRONOUN = "((I)|(you)|(he)|(she)|(we)|(ya'll)|(they))";
	private static int STR_DEPTH = 10; // Determines the maximum allowed number of strings to be concatenated at once. Higher values run slower.
	
	public static Element CLASS;
	public static Element VAR;
	public static Element TYPE;
	public static Element LIT;
	public static Element VAL;
	public static Element OP;
	public static Element COMP;
	public static Element STRING;
	public static Element METHOD_CALL;
	
	private static Element STRING1;
	private static Element VAL1;
	
	public static void setup(){
		try {
			CLASS  = new NormalElement("Class", "normalizeClassName",  "getClassRegex");
			VAR    = new NormalElement("var", "normalizeVariable",   "getVarRegex");
			TYPE   = new NormalElement("type", "normalizeType",       "getTypeRegex");
			OP     = new NormalElement("op", "normalizeOperation",  "getOpRegex");
			COMP   = new NormalElement("comp", "normalizeComparator", "getCompRegex");
			STRING = new NormalElement("string", "normalizeString3",    "getString3Regex");
			METHOD_CALL = new NormalElement("method", "normalizeMethodCall", "getMethodCallRegex");
			Element LIT_NUM = new NormalElement("lit_num", "normalizeLiteralNumber", "getLitNumRegex");
			Element LIT_STRING = new NormalElement("lit_string", "normalizeLiteralString", "getLitStringRegex");
			Element LIT_BOOL = new NormalElement("lit_bool", "normalizeLiteralBoolean", "getLitBoolRegex");
			LIT = new OrElement("lit", LIT_NUM, LIT_STRING, LIT_BOOL);
			VAL1   = new OrElement("val1", LIT, VAR, STRING, METHOD_CALL);
			VAL    = new OrElement("val", VAL1, COMP);
			STRING1 = new OrElement("string1", LIT_STRING, VAR);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public static String getClassRegex(String str){
		return "(?<"+str+">[A-Z]+[\\w ]*)";
	}
	public static String normalizeClassName(String str, Matcher matcher, String head){
		str = str.replace(" ", "_"); // Replace all spaces with underscores in class names.
		return str;
	}

	public static String getVarRegex(String str){
		return "(?<"+str+">(a |an |the )??_(?<"+str+"Name1>[\\w ']+?)_)";
	}
	public static String normalizeVariable(String str, Matcher matcher, String head){
		str = matcher.group(head+"Name1");
		str = str.replace(" ", "_"); // Replace all spaces with underscores in variable names.
		str = str.replace("'", "u0027");
		str = str.replace("\"", "u0029");
		return str;
	}

	public static String getTypeRegex(String str){
		return "(?<"+str+">(a |the |an )*((logical)|(argument)|(number)|(name)|(character)|(letter)|(nothing))(s|es)*)";
	}
	public static String normalizeType(String str, Matcher matcher, String head){
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
		str = str.replace("nothing", "void");
		str = str.replace("es", "[]");
		str = str.replace("s", "[]");
		return str;
	}
	
	public static String getLitBoolRegex(String str){
		return "(?<"+str+">((true)|(false)|(correct)|(incorrect)))";
	}
	
	public static String normalizeLiteralBoolean(String str, Matcher matcher, String head){
		if(str.equals("true") || str.equals("correct"))
			return "true";
		else if(str.equals("false") || str.equals("incorrect"))
			return "false";
		return null;
	}
	public static String normalizeLiteralString(String str, Matcher matcher, String head){
		return "\""+matcher.group(head+"XLitstringQuote1")+"\"";
	}	
	public static String getLitStringRegex(String str){
		String head = str+"XLitstring";
		return "(?<"+head+"Qm1>"+QUOTE_MARK+")(?<"+head+"Quote1>(?s).+?)\\k<"+head+"Qm1>";
	}

	public static String normalizeLiteralNumber(String str, Matcher matcher, String head){
		return matcher.group(head);
	}
	public static String getLitNumRegex(String str){
		return "(?<"+str+">\\d+(\\.\\d+)?)";
	}
	
	public static String getLitNothingRegex(String str){
		return "(?<"+str+">nothing)";
	}
	public static String normalizeLiteralNothing(String str, Matcher matcher, String head){
		return "void";
	}
	
	public static String getMethodCallRegex(String str){
		String head = str+"XMethodcall";
		return "(?<"+str+">the result of "+VAR.get(head+"Method1")+")";
	}
	public static String normalizeMethodCall(String str, Matcher matcher, String head){
		return VAR.norm(matcher.group(head+"XMethodcallMethod1"))+"()";
	}

	public static String getOpRegex(String str){
		String head = str+"XOp";
		return "(?<"+str+">((?<"+head+"Op1>((increas)|(decreas)|(multipli)|(divid)|(and)|(or)|(xor))ed) by )"+
				VAL.get(head+"Val1")+")";
	}
	public static String normalizeOperation(String str, Matcher matcher, String head){
		String operation = matcher.group(head+"XOpOp1");
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
		return s+VAL.norm(matcher.group(head+"XOpVal1"));
	}


	public static String getCompRegex(String str){
		String head = str+"XComp";
		return "(?<"+str+">"+VAL1.get(head+"Val1")+" (?<"+head+"C1>(is|was|has|had)( not)?( ((less)|(fewer)|(greater)"+
				"|(more)) than)?) "+VAL1.get(head+"Val2")+")";
	}
	public static String normalizeComparator(String str, Matcher matcher, String head){
		String comp = matcher.group(head+"XCompC1");
		comp = comp.replace("is", "was");
		comp = comp.replace("has", "was");
		comp = comp.replace("had", "was");
		comp = comp.replace("more", "greater");
		comp = comp.replace("fewer", "less");
		String s = "?";
		if(comp.equals("was"))
			s = "==";
		else if (comp.equals("was not"))
			s = "!=";
		else if (comp.equals("was less than"))
			s = "<";
		else if (comp.equals("was not less than"))
			s = ">=";
		else if (comp.equals("was greater than"))
			s = ">";
		else if (comp.equals("was not greater than"))
			s = "<=";

		String val1 = matcher.group(head+"XCompVal1");
		String val2 = matcher.group(head+"XCompVal2");
		return VAL1.norm(val1)+s+VAL1.norm(val2);
	}

	public static String normalizeString3(String str, Matcher matcher, String head){		
		if(matcher.matches()){
			String res = "";
			for(int i=1; i<=STR_DEPTH; i++){
				String temp = matcher.group(head+"XString3XString2String"+i);
				if(temp != null)
					res += "+"+STRING1.norm(temp);
			}
			res = res.substring(1); // Cuts off the initial plus.
			return res;
		}
		
		return null;
	}
	
	// Allows for concatenation of strings.
	public static String getString3Regex(String str){
		String head = str+"XString3";
		return "(?<"+str+">("+STRING1.get(head+"XString2String1")+getString2Regex(head, STR_DEPTH-2)+"))";
	}

	public static String getString2Regex(String str, int i){
		String head = str+"XString2";
		if(i <= -1)
			return "";
		return "("+STRING1.get(head+"String"+(STR_DEPTH-i))+getString2Regex(str, i-1)+")??";
	}

}