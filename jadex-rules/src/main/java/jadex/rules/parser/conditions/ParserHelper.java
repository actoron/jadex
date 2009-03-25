package jadex.rules.parser.conditions;

import jadex.rules.parser.conditions.javagrammar.ConditionBuilder;
import jadex.rules.parser.conditions.javagrammar.Constraint;
import jadex.rules.parser.conditions.javagrammar.IParserHelper;
import jadex.rules.parser.conditions.javagrammar.JavaJadexLexer;
import jadex.rules.parser.conditions.javagrammar.JavaJadexParser;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.AndCondition;
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
	public static ICondition parseCondition(ICondition precon, String text, String language, OAVTypeModel model, String[] imports, List errors, IParserHelper helper)
	{
		ICondition	ret	= null;

		if(language==null || language.equals("clips"))
		{
			ICondition	usercon	= parseClipsCondition(text, model, imports, errors);
			ret	= precon==null ? usercon : usercon==null ? precon : new AndCondition(new ICondition[]{precon, usercon});
		}
		else if(language.equals("jcl"))
		{
			ret	= parseJavaCondition(precon, text, model, imports, errors, helper);
		}
		
		return ret;
	}
		
	/**
	 *  Parse a condition.
	 *  @param text The text.
	 *  @param model The model.
	 *  @return The condition.
	 */
	public static ICondition parseClipsCondition(String text, OAVTypeModel model, String[] imports, List errors)
	{
		ICondition	ret	= null;
		ANTLRStringStream exp = new ANTLRStringStream(text);
		ClipsJadexLexer lexer = new ClipsJadexLexer(exp);			
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ClipsJadexParser parser = new ClipsJadexParser(tokens);
		parser.setImports(imports);
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

	/**
	 *  Parse a condition.
	 *  @param text The text.
	 *  @param model The model.
	 *  @return The condition.
	 */
	public static ICondition parseJavaCondition(ICondition precon, String text, OAVTypeModel model, String[] imports, List errors, IParserHelper helper)
	{
		ICondition	ret	= null;
		ANTLRStringStream exp = new ANTLRStringStream(text);
		JavaJadexLexer lexer = new JavaJadexLexer(exp);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		JavaJadexParser parser = new JavaJadexParser(tokens);
		try
		{
			parser.setParserHelper(helper);
			parser.lhs();
			precon	= new AndCondition(helper.getConditions());

			Constraint[]	constraints	= (Constraint[])parser.getStack()
				.toArray(new Constraint[parser.getStack().size()]);

			ret	= ConditionBuilder.buildCondition(constraints, precon, model);
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
