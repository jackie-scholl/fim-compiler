package com.twitter.raptortech97.git.rand.fimcompiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

interface Element{
	public String get(String str);
	public String norm(String str);
	public String norm(String str, Matcher matcher, String head);
}

class OrElement implements Element{
	private Element[] subs;
	private String name;
	private static Random RAND = new Random();
	public OrElement(String name, Element...elements){
		subs = elements;
		this.name = name;
	}
	public String get(String str){
		String head = name+rand()+"get";
		String s = "";
		for(int i=0; i<subs.length; i++){
			Element e = subs[i];
			s += "|"+e.get(head+"Set"+i);
		}
		s = s.substring(1); // Removes the initial "|"
		return "(?<"+str+">("+s+"))";
	}
	
	public String norm(String str){
		String head = name+rand()+"norm";
		for(Element e : subs){
			Matcher matcher = Pattern.compile(e.get(head)).matcher(str);
			if(matcher.matches())
				return e.norm(str, matcher, head);
		}
		return null;
	}
	public String norm(String str, Matcher matcher, String head) {
		return norm(str);
	}
	
	private static long rand(){
		return Math.abs(RAND.nextLong());
	}
}

class NormalElement implements Element{
	protected Method norm;
	protected Method regex;
	protected static String head = "element";
	protected String name;
	public NormalElement(String name, Method normalize, Method getRegex){
		norm = normalize;
		regex = getRegex;
		this.name = name;
		assert norm.getReturnType() == String.class;
		assert regex.getReturnType() == String.class;
		assert Arrays.deepEquals(norm.getParameterTypes(), new Class[]{String.class, Matcher.class, String.class});
		assert Arrays.deepEquals(regex.getParameterTypes(), new Class[]{String.class});
	}
	public NormalElement(Method normalize, Method getRegex){
		this("name", normalize, getRegex);
	}
	public NormalElement(String name, String normalize, String getRegex, Class<?> cls) throws NoSuchMethodException, SecurityException{
		this(name, cls.getMethod(normalize, String.class, Matcher.class, String.class), cls.getMethod(getRegex, String.class));
	}
	public NormalElement(String name, String normalize, String getRegex) throws NoSuchMethodException, SecurityException{
		this(name, normalize, getRegex, Regex.class);
	}
	public NormalElement(String normalize, String getRegex) throws NoSuchMethodException, SecurityException{
		this("name", normalize, getRegex, Regex.class);
	}
	public String get(String str){
		try {
			return (String) regex.invoke(null, str);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return "Invocation failure";
	}
	public String norm(String str){
		String pattern = get(head);
		Matcher matcher = Pattern.compile(pattern).matcher(str);
		if(matcher.matches())
			return (String) this.norm(str, matcher, head);
		return null;
	}
	public String norm(String str, Matcher matcher, String head) {
		try {
			return (String) norm.invoke(null, str, matcher, head);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return "Invocation failure";
	}
}

class InterpretElement{
	private Method norm;
	private Pattern regex;
	private String name;
	
	public InterpretElement(Method normalize, Pattern getRegex){
		norm = normalize;
		this.regex = getRegex;
		assert norm.getReturnType() == String.class;
		assert Arrays.deepEquals(norm.getParameterTypes(), new Class[]{String.class, Matcher.class});
	}
	public InterpretElement(String normalize, String getRegex) throws NoSuchMethodException, SecurityException{
		this(Interpreter.class.getMethod(normalize, String.class, Matcher.class), Pattern.compile(getRegex));
	}
	public String norm(String str){
		Matcher matcher = regex.matcher(str);
		if(matcher.matches())
			return (String) this.norm(str, matcher);
		return null;
	}
	public String norm(String str, Matcher matcher) {
		try {
			return (String) norm.invoke(null, str, matcher);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			System.err.println(this);
			e.printStackTrace();
		}
		return "Invocation failure";
	}
	public String getName(){
		return name;
	}
	public int getLength(){
		return regex.pattern().length();
	}
	public String toString(){
		return "Norm: "+norm+Compiler.LINEBREAK+"Regex: "+regex;
	}
}
