package jadex.rules.parser.conditions;

import jadex.rules.rulesystem.ICondition;
import jadex.rules.state.OAVTypeModel;

import java.util.List;

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
	public static ICondition parseCondition(String text, OAVTypeModel model, List errors)
	{
		ICondition	ret	= null;
		ANTLRStringStream exp = new ANTLRStringStream(text);
		ClipsJadexLexer lexer = new ClipsJadexLexer(exp);			
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ClipsJadexParser parser = new ClipsJadexParser(tokens);
		parser.setErrorList(errors);
		try
		{
			ret	= parser.rhs(model);
		}
		catch(Exception e)
//		catch(RecognitionException e)
		{
			if(errors!=null)
				errors.add(e.toString());
			else
				throw new RuntimeException(e);
		}
		
		if(ret==null && errors!=null && errors.isEmpty())
		{
			errors.add("Cannot parse: "+text);
		}
		else if(ret==null && errors==null)
		{
			throw new RuntimeException("Cannot parse: "+text);				
		}
		return ret;
	}
}
