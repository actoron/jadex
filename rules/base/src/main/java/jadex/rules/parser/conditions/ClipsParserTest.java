package jadex.rules.parser.conditions;

//import jadex.bdi.interpreter.OAVBDIRuntimeModel;
//import jadex.rules.rulesystem.ICondition;
//
//import org.antlr.runtime.ANTLRStringStream;
//import org.antlr.runtime.CommonTokenStream;

/**
 *  Test the functionality of the clips parser.
 */
public class ClipsParserTest
{
	/**
	 *  Main for testing only.
	 */
	public static void main(String[] args)
	{
		try
		{
//			?z <- (Block (color "red"))
//			?y <- (Block (left ?z))
//		  	?x <- (Block (on ?y))
			
//			String c = "?z <- (block (block_has_color \"red\"))"
//				+"?y <- (block (block_has_left ?z))"
//				+"?x <- (block (block_has_on ?y))";
			
//			String c	= "?block = (jadex.bdi.samples.blocksworld.Block (clear true))";
//				+ "?param = (parameter (parameter_has_name \"block\") (parameter_has_value ?block))"
//				+ "?mgoal = (mgoal (element_has_name \"clear\"))"
//				+ "?rgoal = (goal (element_has_model ?mgoal) (parameterelement_has_parameters contains ?param))";
//			
			
//			String c	= "?a = (java.lang.Object (wait(1) \"A\"))";
//			
//			ANTLRStringStream exp = new ANTLRStringStream(c);
//			ClipsJadexLexer lexer = new ClipsJadexLexer(exp);
//			CommonTokenStream tokens = new CommonTokenStream(lexer);
//			ClipsJadexParser parser = new ClipsJadexParser(tokens);
//		
//			ICondition cond = parser.rhs(OAVJavaType.java_type_model);
//			System.out.println(cond);
			
//			String c	= "$beliefbase.chargestate > 0.2";
//			
//			ANTLRStringStream exp = new ANTLRStringStream(c);
//			JadexJavaRulesLexer lexer = new JadexJavaRulesLexer(exp);
//			CommonTokenStream tokens = new CommonTokenStream(lexer);
//			JadexJavaRulesParser parser = new JadexJavaRulesParser(tokens);
//		
//			JavaRulesContext	jrc	= new JavaRulesContext(null);
//			parser.setContext(jrc);
//			parser.rhs();
//			System.out.println(jrc);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
