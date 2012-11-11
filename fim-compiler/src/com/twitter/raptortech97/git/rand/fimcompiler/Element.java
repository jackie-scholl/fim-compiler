package com.twitter.raptortech97.git.rand.fimcompiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

interface Element{
	public String getName();
	public String getSimple();
	public String norm(String str);
}

class NormalElement implements Element{
	private String name;
	private Method norm;
	private String simple;
	private Pattern regex;
	
	private NormalElement(String nameIn, String simpleIn, Pattern regexIn, Method normalize){
		name = nameIn;
		simple = simpleIn;
		regex = regexIn;
		norm = normalize;
		assert norm.getReturnType() == String.class;
		assert Arrays.deepEquals(norm.getParameterTypes(), new Class[]{Matcher.class});
	}
	private NormalElement(String name, String simpleIn, String regexIn, Method normalize){
		this(name, simpleIn, Regex.myPatternCompile(regexIn), normalize);
	}
	
	public NormalElement(String name, String simpleIn, String regexIn, String normalize, Class<?> cls) throws
				NoSuchMethodException,	SecurityException{
		this(name, simpleIn, regexIn, cls.getMethod(normalize, Matcher.class));
	}

	
	public String getSimple(){
		return simple;
	}
	
	public String norm(String str){
		Matcher matcher = regex.matcher(str);
		if(matcher.matches())
			return (String) this.norm(matcher);
		return null;
	}
	public String norm(Matcher matcher) {
		try {
			return (String) norm.invoke(null, matcher);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return "Invocation failure";
	}
	
	public String getName(){
		return name;	
	}
	public String toString(){
		return "Name: "+name;
	}
}

class OrElement implements Element{
	private List<Element> subs;
	private String name;
	private String simple;
	private boolean auto=true;
	public OrElement(String name, String simpleIn, Element... elements){
		this.name = name;
		this.simple = simpleIn;
		auto=false;
		subs = new ArrayList<Element>();
		for(Element e : elements)
			subs.add(e);
	}
	public OrElement(String name, Element...elements){
		this(name, null, elements);
		makeSimple();
	}
	public void addElement(Element e){
		subs.add(e);
		if(auto)
			makeSimple();
	}
	public void addElements(Element... elements){
		for(Element e : elements)
			addElement(e);
	}
	private void makeSimple(){
		simple = "(";
		for(Element e : subs){
			simple += "("+e.getSimple()+")|";
		}
		simple = simple.substring(0, simple.length()-1);
		simple += ")";
	}
	
	public String getSimple(){
		return simple;
	}
	public String norm(String str){
		for(Element e : subs){
			String temp = e.norm(str);
			if(temp != null)
				return temp;
		}
		return null;
	}
	public String getName(){
		return name;	
	}
	public String toString(){
		return "Name: "+name;
	}
}
