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

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.*;

public class Compiler {
	public static final File CELESTIA = new File("C://Users//Jackson//git//fim-compiler//fim-compiler//src//com//twitter"+
			"//raptortech97//git//rand//fimcompiler//Princess_Celestia.java");

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException,
	NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		File fileIn = new File("C://Users//Jackson//git//fim-compiler//fim-compiler//Hello_World", "HelloWorld.fim");
		act(fileIn);
	}
	
	public static void act(File fileIn) throws FileNotFoundException, IOException, ClassNotFoundException, NoSuchMethodException,
	IllegalAccessException, IllegalArgumentException, InvocationTargetException{

		File fileOut = new File(fileIn.getAbsolutePath().replace(".fim", ".java"));
		BufferedReader in = new BufferedReader(new FileReader(fileIn));
		PrintStream out = new PrintStream(new FileOutputStream(fileOut));
		
		out.println("import com.twitter.raptortech97.git.rand.fimcompiler.Princess_Celestia;");
		out.println("// AUTO-GENERATED CLASS");
		
		List<String> lines = (List<String>) new ArrayList<String>();
		String text = in.readLine();
		while(text != null){
			lines.add(text);
			text = in.readLine();
		}
		
		boolean comment = false;
		for(int i=0; i<lines.size(); i++){
			String str = lines.get(i);
			
			if(str.startsWith(")"))
				comment = false;
			else if (str.startsWith("("))
				comment = true;
			else if (!comment)
				out.println(interpret(str));
		}
		
		out.close(); in.close();
		@SuppressWarnings("rawtypes")
		Class cls2 = compileLoad(fileOut).loadClass("HelloWorld");
		runClassMain(cls2);
	}

	private static ClassLoader compileLoad(File source) throws ClassNotFoundException, IOException, IllegalAccessException,
				InvocationTargetException{

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null, source.getAbsolutePath());
		System.out.println("Finished compiling.");

		String[] dirs = new String[]{source.getParent(), CELESTIA.getParent()};
		URL[] urls = new URL[dirs.length];
		for(int i=0; i<dirs.length; i++)
			urls[i] = new File(dirs[i]).toURI().toURL();
		ClassLoader loader = new URLClassLoader(urls);
		return loader;
	}

	@SuppressWarnings("rawtypes")
	private static void runClassMain(Class cls) throws InvocationTargetException, IllegalAccessException{
		String className = cls.getName();
		String[] strs = new String[1];
		try {
			@SuppressWarnings("unchecked")
			Method m = cls.getMethod("main", strs.getClass());
			m.invoke(null, (Object[]) strs);
		} catch (NoSuchMethodException e) {
			System.out.println("No main method in class "+className);
		}
	}

	private static String interpret(String text){
		return Interpreter.interpretLine(text);
	}
}