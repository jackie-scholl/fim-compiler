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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.regex.*;
import javax.tools.*;

public class Compiler {
	public static final String CELESTIA = "C:\\Users\\Jackson\\git\\fim-compiler\\fim-compiler\\src\\com\\twitter\\raptortech97\\git\\rand\\fimcompiler\\";
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		File fileIn = new File("HelloWorld.fim");
		File fileOut = new File(fileIn.getAbsolutePath().replace(".fim", ".java"));
		BufferedReader in = new BufferedReader(new FileReader(fileIn));
		PrintStream out = new PrintStream(new FileOutputStream(fileOut));
		
		out.println("// AUTO-GENERATED CLASS");
		//out.println("package com.twitter.raptortech97.git.rand.fimcompiler;");
		
		String text = in.readLine();
		out.println(interpretLine(text));

		out.println("public static void main(String[] args){");
		out.println("System.out.println(\"Hello! I'm an auto-generated program!\");");
		out.println("}");
		out.println("}");
		out.close();
		in.close();
		
		compileJavaThrows(new File(CELESTIA+File.separator +"Princess_Celestia.java"), "com.twitter.raptortech97.git.rand.fimcompiler.Princess_Celestia");
		compileJavaThrows(fileOut, "HelloWorld");
	}
	
	private static boolean compileJavaSilent(File source, String className){
		try {
			compileJavaThrows(source, className);
			return true;
		} catch (ClassNotFoundException | SecurityException | IllegalArgumentException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static void compileJavaThrows(File source, String className) throws ClassNotFoundException, IOException{
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		compiler.run(null, null, null, source.getAbsolutePath());
		
		
		String[] dirs = new String[]{source.getParent(), CELESTIA};
		URL[] urls = new URL[dirs.length];
		for(int i=0; i<dirs.length; i++)
			urls[i] = new File(dirs[i]).toURI().toURL();
		System.out.println(Arrays.deepToString(urls));
		URLClassLoader loader = new URLClassLoader(urls);
		
		//Class cls = loader.loadClass(source.getName().replace(".java", ""));
		//Class cls1 = loader.loadClass("com.twitter.raptortech97.git.rand.fimcompiler.Princess_Celestia");
		Class cls = loader.loadClass(className);
		loader.close();
		System.out.println(cls.getName());
		String[] strs = new String[1];
		try {
			Method m = cls.getMethod("main", strs.getClass());
			m.invoke(null, (Object[]) strs);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.out.println("No main method in class "+className);
		}
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
