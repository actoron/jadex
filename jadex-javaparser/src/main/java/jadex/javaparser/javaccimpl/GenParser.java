package jadex.javaparser.javaccimpl;

import java.lang.reflect.Method;

/**
 *  Run this class to generate the parser.
 *  Have to uncomment javacc dependency in POM first.
 */
public class GenParser
{
	/**
	 *  Invoke JJTree and JavaCC main classes.
	 */
	public static void main(String[] args) throws Exception
	{
//		System.out.println("Re-run with outcommented JJTree to generate actual parser!\n");
//		Class<?>	jjtree	= Class.forName("jjtree");
//		Method	main	= jjtree.getMethod("main", new Class<?>[]{String[].class});
//		// main.invoke(null, new Object[]{new String[]{}});	// help
//		main.invoke(null, new Object[]{new String[]{
//			"-OUTPUT_DIRECTORY=src/main/java/jadex/javaparser/javaccimpl",
//			"src/main/java/jadex/javaparser/javaccimpl/ParserImpl.jjt"
//		}});

		System.out.println("Run first with commented in JJTree to generate .jj file!\n");
		Class<?>	javacc	= Class.forName("javacc");
		Method	main	= javacc.getMethod("main", new Class<?>[]{String[].class});
		// main.invoke(null, new Object[]{new String[]{}});	// help
		main.invoke(null, new Object[]{new String[]{
			"-OUTPUT_DIRECTORY=src/main/java/jadex/javaparser/javaccimpl",
			"src/main/java/jadex/javaparser/javaccimpl/ParserImpl.jj"
		}});
	}
}
