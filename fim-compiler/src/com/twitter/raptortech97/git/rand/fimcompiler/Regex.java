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
	public static String QUOTE_MARK = "[\\u0022\\u0027\\u0029\\u2018\\u2019\\u201C\\u201D]";
	public static String APOSTROPHE = "[\\u0027\\u2019\\u02BC\\u2019]";
	public static String COMMA = "(\\u002C|\\uFE10|\\uFE11|\\uFE50|\\uFE51|\\uFF0C)";
	public static String PUNC = "[,:\\?\\.\\!"+COMMA+"]";
	private static int STR_DEPTH = 5;

	public static String normalizeClassName(String str){
		Matcher matcher = Pattern.compile(getClassRegex("A")).matcher(str);
		if(!matcher.matches())
			System.err.println("Error. Bad class name.");

		str = str.replace(" ", "_"); // Replace all spaces with underscores in class names.
		return str;
	}

	public static String normalizeVariable(String str){
		String head = "normalizeVariable";
		Matcher matcher = Pattern.compile(getVarRegex(head)).matcher(str);
		if(!matcher.matches())
			return null;

		str = matcher.group(head+"Name1");
		str = str.replace(" ", "_"); // Replace all spaces with underscores in variable names.
		str = str.replace("'", "u0027");
		str = str.replace("\"", "u0029");
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
	private static String LITERAL_BOOL_REGEX = "((true)|(false)|(correct)|(incorrect))";
	public static String normalizeLiteral(String str){
		String head = "normalizeLiteral";
		Matcher matcher = Pattern.compile(getLitRegex(head)).matcher(str);
		if(!matcher.matches())
			System.err.println("Error. Bad literal.");

		matcher = Pattern.compile(LITERAL_NUMBER_REGEX).matcher(str);
		if(matcher.matches())
			return str;

		matcher = Pattern.compile(getLitStringRegex(head)).matcher(str);
		if(matcher.matches())
			return "\""+matcher.group(head+"XLitstringQuote1")+"\"";

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
		String head = "normalizeValue";
		Matcher matcher = Pattern.compile(getLitRegex(head)).matcher(str);
		if(matcher.matches())
			return normalizeLiteral(str);

		matcher = Pattern.compile(getVarRegex(head)).matcher(str);
		if(matcher.matches())
			return normalizeVariable(str);
		
		matcher = Pattern.compile(getString3Regex(head)).matcher(str);
		if(matcher.matches())
			return normalizeString3(str);
		return null;
	}
	
	public static String normalizeValue2(String str){
		String head = "normalizeValue2";
		Matcher matcher;
		
		matcher = Pattern.compile(getValRegex(head)).matcher(str);
		if(matcher.matches())
			return normalizeValue(str);

		matcher = Pattern.compile(getCompRegex(head)).matcher(str);
		if(matcher.matches())
			return normalizeComparator(str);
		
		return null;
	}
	
	public static String normalizeOperation(String str){
		String head = "normalizeOperation";
		String pattern = getOpRegex(head);
		Matcher matcher = Pattern.compile(pattern).matcher(str);
		matcher.find();
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
		return s+normalizeValue(matcher.group(head+"XOpVal1"));
	}

	public static String normalizeComparator(String str){
		String head = "NormComp";
		String pattern = getCompRegex(head);
		Matcher matcher = Pattern.compile(pattern).matcher(str);
		matcher.find();
		String comp = matcher.group(head+"XCompC1");
		comp = comp.replace("is", "was");
		comp = comp.replace("has", "was");
		comp = comp.replace("had", "was");
		comp = comp.replace("more", "greater");
		comp = comp.replace("less", "fewer");
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
		return normalizeValue(val1)+s+normalizeValue(val2);
	}
	
	public static String normalizeString1(String str){
		String head = "normalizeString1";
		
		Matcher matcher = Pattern.compile(getLitStringRegex(head)).matcher(str);
		if(matcher.matches()){  return normalizeLiteral(str);  }
		
		matcher = Pattern.compile(getVarRegex(head)).matcher(str);
		if(matcher.matches()){	return normalizeVariable(str);  }
		
		return null;
	}
	
	public static String normalizeString3(String str){
		String head = "normalizeString3";
		
		String pattern = getString3Regex(head);
		Matcher matcher = Pattern.compile(pattern).matcher(str);
		/*
		while(matcher.group("AString2String"+STR_DEPTH) != null){ // While STR_DEPTH might me too low, increase STR_DEPTH.
			STR_DEPTH *= 1.5;
			pattern = getString2Regex(head);
			matcher = Pattern.compile(pattern).matcher(str);
		}
		*/
		if(matcher.matches()){
			String res = "";
			for(int i=1; i<=STR_DEPTH; i++){
				String temp = matcher.group(head+"XString3XString2String"+i);
				if(temp != null)
					res += "+"+normalizeString1(temp);
			}
			res = res.substring(1); // Cuts off the initial plus.
			return res;
		}
		
		return null;
	}

	public static String getVarRegex(String str){
		return "(?<"+str+">(a |an |the )??_(?<"+str+"Name1>[\\w ']+?)_)";
	}
	public static String getTypeRegex(String str){
		return "(?<"+str+">(a |the |an )*((logical)|(argument)|(number)|(name)|(character)|(letter)|(void))(s|es)*)";
	}
	public static String getClassRegex(String str){
		return "(?<"+str+">[A-Z]+[\\w ]*)";
	}
	public static String getLitRegex(String str){
		return "(?<"+str+">"+LITERAL_NUMBER_REGEX+"|"+getLitStringRegex(str+"XLit1")+"|"+LITERAL_BOOL_REGEX+")";
	}
	public static String getLitNumRegex(String str){
		return "(?<"+str+">"+LITERAL_NUMBER_REGEX+")";
	}
	private static String getLitStringRegex(String str){
		String head = str+"XLitstring";
		return "(?<"+head+"Qm1>"+QUOTE_MARK+")(?<"+head+"Quote1>(?s).+?)\\k<"+head+"Qm1>";
	}
	private static String getString1Regex(String str){
		String head = str+"XString1";
		return "(?<"+str+">("+getLitStringRegex(head+"Set1")+"|"+getVarRegex(head+"Set2")+"))";
	}
	// Allows for concatenation of strings.
	public static String getString3Regex(String str){
		String head = str+"XString3";
		return "(?<"+str+">("+getString1Regex(head+"XString2String1")+getString2Regex(head, STR_DEPTH-2)+"))";
	}
	public static String getValRegex(String str){
		String head = str+"XVal";
		return "(?<"+str+">("+getLitRegex(head+"Set1")+"|"+getVarRegex(head+"Set2")+"|"+getString3Regex(head+"Set3")+"))";
	}
	public static String getOpRegex(String str){
		String head = str+"XOp";
		return "(?<"+str+">((?<"+head+"Op1>((increased)|(decreased)|(multiplied)|(divided)|(anded)|(ored)|(xored))) by )"+
				getValRegex(head+"Val1")+")";
	}
	public static String getCompRegex(String str){
		String head = str+"XComp";
		return "(?<"+str+">"+getValRegex(head+"Val1")+" (?<"+head+"C1>(is|was|has|had)( not)?( ((less)|(fewer)|(greater)"+
				"|(more)) than)?) "+getValRegex(head+"Val2")+")";
	}
	public static String getVal2Regex(String str){
		return "(?<"+str+">"+"("+getValRegex(str+"XVal2Set1")+"|"+getCompRegex(str+"XVal2Set2")+"))";
	}
	
	
	private static String getString2Regex(String str, int i){
		// Let x=0;
		String head = str+"XString2";
		if(i <= -1)
			return "";
		else
			return "("+getString1Regex(head+"String"+(STR_DEPTH-i))+getString2Regex(str, i-1)+")??";
	}
}