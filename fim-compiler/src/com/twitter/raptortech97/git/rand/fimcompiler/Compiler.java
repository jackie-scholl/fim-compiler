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

import javax.tools.*;

public class Compiler {
	public static final File CELESTIA = new File("C://Users//Jackson//git//fim-compiler//fim-compiler//src//com//twitter"+
			"//raptortech97//git//rand//fimcompiler//Princess_Celestia.java");
	public static final String LINEBREAK = System.getProperty("line.separator");

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException,
	NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		File fimFile = new File("C://Users//Jackson//git//fim-compiler//fim-compiler//Hello_World", "HelloWorld.fim");
		File javaFile = interpret(fimFile); // Interpret FiM++ into Java
		File classFile = compile(javaFile); // Compile Java into bytecode
		Class cls = loadClass(classFile);   // Load class from bytecode
		runClassMain(cls);                  // Run the main method of the class.
	}
	
	// Interprets FiM++ into Java.
	public static File interpret(File fileIn) throws FileNotFoundException, IOException, ClassNotFoundException,
			NoSuchMethodException,	IllegalAccessException, IllegalArgumentException, InvocationTargetException{

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
				out.println(Interpreter.interpretLine(str));
		}
		
		out.close(); in.close();
		return fileOut;
	}

	// Compiles a given Java file and returns the ClassLoader to load it.
	private static File compile(File source) throws ClassNotFoundException, IOException, IllegalAccessException,
				InvocationTargetException{
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null, source.getAbsolutePath());
		return new File(source.getAbsolutePath().replace(".java", ".class"));
	}

	@SuppressWarnings("rawtypes")
	// Loads a .class file
	private static Class loadClass(File source) throws ClassNotFoundException, IOException{
		String name = source.getName().replace(".class", "");		
		String[] dirs = new String[]{source.getParent()};
		URL[] urls = new URL[dirs.length];
		for(int i=0; i<dirs.length; i++)
			urls[i] = new File(dirs[i]).toURI().toURL();
		URLClassLoader loader = new URLClassLoader(urls);
		Class cls = loader.loadClass(name);
		loader.close();
		return cls;
	}

	@SuppressWarnings("rawtypes")
	// Runs the main method of a class.
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
}