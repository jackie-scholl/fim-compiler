/*
 * Author: Jackson Scholl 
 * 		Twitter: @raptortech97
 * 		Email:   shades97@gmail.com
 * 		Google+: https://plus.google.com/113116905639811214312/
 * 		GitHub:  raptortech-js
 * 
 * A FiM++ compiler/interpreter written in Java. Compiles the FiM++ to Java as an intermediary.
 * 
 */

package com.twitter.raptortech97.git.rand.fimcompiler;

import java.io.*;
import java.util.regex.*;
import javax.tools.*;

public class Compiler {
	public static void main(String[] args) throws FileNotFoundException, IOException{
		String fileInName = "HelloWorld.fim.txt";
		String fileOutName = "src\\com\\twitter\\raptortech97\\git\\rand\\fimcompiler\\Hello_World.java";
		BufferedReader in = new BufferedReader(new FileReader(fileInName));
		PrintStream out = new PrintStream(new FileOutputStream(fileOutName));
		String text = in.readLine();
		
		out.println("// AUTO-GENERATED CLASS");
		out.println("package com.twitter.raptortech97.git.rand.fimcompiler;");
		
		out.println(interpretLine(text));

		out.println("public static void main(String[] args){");
		out.println("System.out.println(\"Hello! I'm an auto-generated program!\");");
		out.println("}");
		out.println("}");
		out.close();
		in.close();
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		System.out.println(compiler);
		int compilationResult = compiler.run(null, null, null, fileOutName);
		if(compilationResult == 0)
			System.out.println("Compilation is successful");
		else
			System.out.println("Compilation Failed");
	}

	private static String interpretLine(String text){
		String classString = "Dear (([A-Z]+[\\w]*)+( [A-Z]+[\\w]*)*): (([A-Z]+[\\w]*)+( [A-Z]+[\\w]*)*)";
		Matcher classMatcher = (Pattern.compile(classString)).matcher(text);

		if(classMatcher.find())
			return "public class "+normalizeClassName(classMatcher.group(4))+" extends "
					+normalizeClassName(classMatcher.group(1))+"{";
		
		return null;
	}
	
	private static String normalizeClassName(String str){
		String name = "(([A-Z]+[\\w]*)+( [A-Z]+[\\w]*)*)";
		Matcher classMatcher = (Pattern.compile(name)).matcher(str);
		if(!classMatcher.find())
			System.err.println("Error. Bad class name.");
		
		str = str.replace(" ", "_"); // Replace all spaces with underscores in class names.
		return str;
	}
	
	private static String normalizeVarName(String str){
		String name = "(([A-Z]+[\\w]*)+( [A-Z]+[\\w]*)*)";
		Matcher classMatcher = (Pattern.compile(name)).matcher(str);
		if(!classMatcher.find())
			System.err.println("Error. Bad class name.");
		
		str = str.replace(" ", "_"); // Replace all spaces with underscores in variable names.
		return str;
	}
}
