package jadex.rules.parser.conditions;

import jadex.rules.rulesystem.ICondition;
import jadex.rules.state.OAVTypeModel;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;

/**
 *  The parser helper class for parsing conditions.
 */
public class ParserHelper
{

	/**
	 *  Parse a condition.
	 *  @param text The text.
	 *  @param model The model.
	 *  @return The condition.
	 */
	public static ICondition parseCondition(String text, OAVTypeModel model)
	{
		ICondition	ret;
		ANTLRStringStream exp = new ANTLRStringStream(text);
		ClipsJadexLexer lexer = new ClipsJadexLexer(exp);			
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ClipsJadexParser parser = new ClipsJadexParser(tokens);
		try
		{
			ret	= parser.rhs(model);
		}
		catch(Exception e)
//		catch(RecognitionException e)
		{
//			e.printStackTrace();
			throw new RuntimeException(e);
		}
		if(ret==null)
			throw new RuntimeException("Cannot parse: "+text);
		return ret;
	}
}
