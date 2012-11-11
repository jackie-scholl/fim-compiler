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
	public static String PUNC = "[,:\\?\\.!\\!"+COMMA+"]";
	public static String PRONOUN = "((I)|(you)|(he)|(she)|(we)|(ya'll)|(they))";
	//private static int STR_DEPTH = 10; // Determines the maximum allowed number of strings to be concatenated at once. Higher values run slower.
	//private static int ARGS_DEPTH = 10;
	
	public static Element ARTICLE;
	public static Element CLASS;
	public static Element VAR;
	public static Element VAR_NAME;
	public static Element VAR_ARR;
	public static Element TYPE;
	public static Element LIT;
	public static Element VAL;
	public static Element OP;
	public static Element COMP;
	public static Element STRING;
	public static Element METHOD_CALL;
	
	public static Element STRING_BASE;
	public static Element LIT_STRING;
	
	public static String NULL = null;
	
	public static int FLAGS = Pattern.CASE_INSENSITIVE;
	
	public static void setup(){
		try {
			VAL = new OrElement("val", "(.+?)");
			
			ARTICLE = get(articleSimple, articleRegex, "articleNorm");
			
			Element LIT_NUM = get(litNumSimple, litNumRegex, "litNumNorm");
			LIT_STRING = get(litStringSimple, litStringRegex, "litStringNorm");
			Element LIT_BOOL = get(litBoolSimple, litBoolRegex, "litBoolNorm");
			LIT = new OrElement("lit", LIT_NUM, LIT_STRING, LIT_BOOL);
			
			VAR_NAME = get(varNameSimple, varNameRegex, "varNameNorm");
			VAR_ARR = get(arraySimple, arrayRegex, "arrayNorm");
			VAR = new OrElement("var", VAR_ARR, VAR_NAME);
			
			CLASS = get(classNameSimple, classNameRegex, "classNameNorm");
			TYPE = get(typeSimple, typeRegex, "typeNorm");
			
			COMP = get(compSimple, compRegex, "compNorm");
			METHOD_CALL = get(methodCallSimple, methodCallRegex, "methodCallNorm");
			OP = get(opSimple, opRegex, "opNorm");
			
			//VAL1   = new OrElement("val1", VAR_NORM, LIT, METHOD_CALL);
			//STRING_BASE = get(stringBaseSimple, stringBaseRegex, "stringBaseNorm");
			STRING_BASE = new OrElement("stringBase", LIT_STRING, VAR);
			STRING = get(stringSimple, stringRegex, "stringNorm");
			
			VAL = new OrElement("val", "(.+?)", LIT, METHOD_CALL, COMP, VAR, STRING);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	private static Element get(String simple, String regex, String norm) throws NoSuchMethodException, SecurityException{
		return new NormalElement(norm.replaceAll("Norm\\z", ""), simple, regex, norm, Regex.class);
	}
	
	public static String getSimpleByName(String str){
		Element[] elements = new Element[]{ARTICLE, CLASS, VAR, VAR_NAME, VAR_ARR, TYPE, LIT, VAL, OP, COMP, STRING_BASE,
				STRING, METHOD_CALL};
		for(Element e : elements){
			if(e != null){
				if(e.getName().equals(str))
					return e.getSimple();
			}
		}
		return null;
	}
	
	public static Pattern myPatternCompile(String str){
		String pattern = "\\(\\?<simple=(?<name>[a-zA-Z]+?)>\\)";
		Matcher matcher = Pattern.compile(pattern, FLAGS).matcher(str);
		while(matcher.find()){
			String s = Matcher.quoteReplacement(getSimpleByName(matcher.group("name")));
			str = matcher.replaceFirst(s);
			matcher = Pattern.compile(pattern, FLAGS).matcher(str);
		}
		return Pattern.compile(str, FLAGS);
	}

	public static String articleSimple = "(((a )|(an )|(the ))?)";
	public static String articleRegex = "((a )|(an )|(the ))?";
	public static String articleNorm(Matcher matcher){
		return "";
	}
	
	public static String classNameSimple = "([A-Z]+[\\w ]*)";
	public static String classNameRegex = "([A-Z]+[\\w ]*)";
	public static String classNameNorm(Matcher matcher){
		return matcher.group().replace(" ", "_");  // Replace all spaces with underscores in class names.
	}
	
	public static String varNameSimple = "(?<simple=article>)??_([\\w ']+?)_";
	public static String varNameRegex = "(?<simple=article>)??_(?<Name>[\\w ']+?)_";
	public static String varNameNorm(Matcher matcher){
		String str = matcher.group("Name");
		str = str.replace(" ", "_"); // Replace all spaces with underscores in variable names.
		str = str.replace("'", "u0027");
		str = str.replace("\"", "u0029");
		return str;
	}
	
	public static String arraySimple = "((?<simple=article>)(?<simple=val>)((st)|(nd)|(rd)|(th))? of (?<simple=varName>))";
	public static String arrayRegex = "((?<simple=article>)(?<Index>(?<simple=val>))((st)|(nd)|(rd)|(th))? of (?<arrName>(?<simple=varName>)))";
	public static String arrayNorm(Matcher matcher){
		return VAR.norm(matcher.group("arrName"))+"[(int)"+VAL.norm(matcher.group("Index"))+"]";
	}

	public static String typeSimple = "(?<simple=article>)((logical)|(argument)|(number)|(name)|(character)|(letter)|(nothing))(s|es)*";
	public static String typeRegex = "(?<simple=article>)(?<type>(((logical)|(argument)|(number)|(name)|(character)|(letter)|(nothing))((s)|(es))?))";
	public static String typeNorm(Matcher matcher){
		String str = matcher.group("type");
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
		str = str.replace("es\\z", "[]");
		str = str.replaceAll("s\\z", "[]");
		return str;
	}
	
	public static String litBoolSimple = "((true)|(false)|(correct)|(incorrect))";
	public static String litBoolRegex = "((true)|(false)|(correct)|(incorrect))";
	public static String litBoolNorm(Matcher matcher){
		String str = matcher.group();
		if(str.equals("true") || str.equals("correct"))
			return "true";
		else if(str.equals("false") || str.equals("incorrect"))
			return "false";
		return null;
	}
	
	public static String litStringSimple = QUOTE_MARK+"((?s).+?)"+QUOTE_MARK;
	public static String litStringRegex = "(?<Qm>"+QUOTE_MARK+")(?<Quote>(?s).+?)\\k<Qm>";
	public static String litStringNorm(Matcher matcher){
		return "\""+matcher.group("Quote")+"\"";
	}

	public static String litNumSimple = "([-\\+\\d\\.]+)";
	public static String litNumRegex = "((-)|(\\+))?\\d+(\\.\\d+)?";
	public static String litNumNorm(Matcher matcher){
		return matcher.group();
	}
	
	public static String litNothingSimple = "nothing";
	public static String litNothingRegex = "nothing";
	public static String litNothingNorm(Matcher matcher){
		return "void";
	}
	
	public static String methodCallSimple = "(the result of )?(?<simple=varName>)( using (?<simple=val>)( and (?<simple=val>))*?)??";
	public static String methodCallRegex = "the result of (?<methodName>(?<simple=varName>))( using (?<arg0>(?<simple=val>))"+
			"(?<otherArgs>( and (?<simple=val>))*?))?";
	private static String argsPattern = " and (?<curArg>(?<simple=val>))(?<otherArgs>( and (?<simple=val>))*?)";
	public static String methodCallNorm(Matcher matcher){
		String res="";
		res += VAR_NAME.norm(matcher.group("methodName"));
		res += "(";
		if(matcher.group("arg0") != null)
			res += VAL.norm(matcher.group("arg0"));
		String args = matcher.group("otherArgs");
		while((args != null)&&(!args.equals(""))){
			Matcher m = myPatternCompile(argsPattern).matcher(args);
			m.matches();
			res += ","+VAL.norm(m.group("curArg"));
			args = m.group("otherArgs");
		}
		res += ")";
		return res;
	}
	
	private static String opSimple = "((increased)|(decreased)|(multiplied)|(divided)|(anded)|(ored)|(xored)) by (?<simple=val>)";
	private static String opRegex = "(?<Op>(increased)|(decreased)|(multiplied)|(divided)|(anded)|(ored)|(xored)) by " +
			"(?<val>(?<simple=val>))";
	public static String opNorm(Matcher matcher){
		String operation = matcher.group("Op");
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
		return s+VAL.norm(matcher.group("val"));
	}
	
	private static String compSimple = "(?<simple=val>) (is|was|has|had)( not)?( ((less)|(fewer)|(greater)|(more)) than)?)"+
			"(?<simple=val>))";
	private static String compRegex = "(?<val1>(?<simple=val>)) (?<C1>((is)|(was)|(has)|(had))( not)?( ((less)|(fewer)|(greater)"+
			"|(more)) than)?) (?<val2>(?<simple=val>))";
	public static String compNorm(Matcher matcher){
		String comp = matcher.group("C1");
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

		String val1 = matcher.group("val1");
		String val2 = matcher.group("val2");
		return VAL.norm(val1)+s+VAL.norm(val2);
	}

	private static String stringBaseSimple = "((?<simple=litString>)|(?<simple=val>))";
	private static String stringBaseRegex = "((?<simple=litString>)|(?<simple=val>))";
	public static String stringBaseNorm(Matcher matcher){
		String str = matcher.group();
		String res = LIT_STRING.norm(str);
		if(res != null)
			return res;
		res = VAL.norm(str);
		if(res != null)
			return res;
		return null;
	}
	
	private static String stringSimple = "(?<simple=stringBase>)+?";
	private static String stringRegex = "( )?(?<first>(?<simple=stringBase>))(?<others>( )?(?<simple=stringBase>)*?)";
	public static String stringNorm(Matcher matcher){
		String str = VAL.norm(matcher.group("first"));
		if(!matcher.group("others").equals(""))
			str += "+"+STRING.norm(matcher.group("others"));
		return str;
	}
}