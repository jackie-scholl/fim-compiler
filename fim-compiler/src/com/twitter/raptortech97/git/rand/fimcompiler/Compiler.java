/*
 * Author: Jackson Scholl 
 * 		Twitter: @raptortech97
 * 		Email:   shades97@gmail.com
 * 		Google+: https://plus.google.com/113116905639811214312/
 * 		GitHub:  https://github.com/raptortech-js
 * 
 * A FiM++ compiler/interpreter written in Java. Compiles the FiM++ to Java as an intermediary.
 */

package com.twitter.raptortech97.git.rand.fimcompiler;

import java.io.*;
import java.util.regex.*;

public class Compiler {

	  public static void main(String[] args) throws FileNotFoundException, IOException{
	    String filename = "HelloWorld.fim.txt";
	    BufferedReader in = new BufferedReader(new FileReader(filename));
	    PrintStream out = new PrintStream(new FileOutputStream("HelloWorld.java"));
	    String text = in.readLine();
	    out.println(interpretLine(text));
	    
	    out.println("public class Compiled{");
	    out.println("public static void main(String[] args){");
	    out.println("System.out.println(\"Hello! I'm an auto-generated program!\");");
	    out.println("}");
	    out.println("}");
	    out.close();
	    in.close();
	  }
	  
	  private static String interpretLine(String text){
	    String classString = "Dear (([A-Z]+[\\w]*)+( [A-Z]+[\\w]*)*): (([A-Z]+[\\w]*)+( [A-Z]+[\\w]*)*)";
	    Matcher classMatcher = (Pattern.compile(classString)).matcher(text);

	    boolean found = false;
	    while (classMatcher.find()) {
	      System.out.printf("I found the class name \"%s\" starting at index %d and ending at index %d.%n", classMatcher.group(4), classMatcher.start(4), classMatcher.end(4));
	      System.out.printf("I found the superclass \"%s\" starting at index %d and ending at index %d.%n", classMatcher.group(1), classMatcher.start(1), classMatcher.end(1));
	      found = true;
	    }
	    if(!found){
	      System.out.printf("No match found.%n");
	    }
	    
	    Strings s = "public class "+normalize(classMatcher.group(4));
	    
	    
	    return "\\\\Hello";
	  }

}
