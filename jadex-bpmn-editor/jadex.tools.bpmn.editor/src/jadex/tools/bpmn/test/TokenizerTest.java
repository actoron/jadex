/**
 * 
 */
package jadex.tools.bpmn.test;

import java.util.StringTokenizer;

/**
 * @author Claas
 *
 */
public class TokenizerTest
{

	// 0x240B	9227	SYMBOL FOR VERTICAL TABULATION	
	static String detail_delim = "\u240B";
	
	// 0x241F	9247	SYMBOL FOR UNIT SEPARATOR
	static String list_delim = "\u241F";
	
	static String test_val = "aaa" +detail_delim+ "bbb" +detail_delim+ "ccc" +detail_delim+ "ddd"; 
	static String test_eval = "aaa" +detail_delim+ "bbb" +detail_delim+ "" +detail_delim+ "ddd"; 
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String[] tests = new String[]{test_eval, test_val};
		
		for (int i = 0; i < tests.length; i++)
		{
			System.out.println("String: " + tests[i]);
			StringTokenizer tokenizer = new StringTokenizer(tests[i],detail_delim, true);
			System.out.println("tokens:" + tokenizer.countTokens());
			int x = 0;
			while (tokenizer.hasMoreTokens())
			{
				x=x+1;
				String s = tokenizer.nextToken();
				System.out.println("["+x+"]"+s);
			}
			
		}
		
		
	}

}
