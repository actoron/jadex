package jadex.rules.parser.conditions.javagrammar;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;


/**
 *  Test the functionality of the clips parser.
 */
public class JavaParserTest
{
	/**
	 *  Main for testing only.
	 */
	public static void main(String[] args)
	{
		try
		{
//			String c	= "$beliefbase.chargestate > 0.2";
			String c	= "$beliefbase.waste.getDistance($beliefbase.location) > 0.2";
			
			ANTLRStringStream exp = new ANTLRStringStream(c);
			JavaJadexLexer lexer = new JavaJadexLexer(exp);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			JavaJadexParser parser = new JavaJadexParser(tokens);
		
			Constraint	con	= parser.relationalExpression();
			System.out.println("done: "+con);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
