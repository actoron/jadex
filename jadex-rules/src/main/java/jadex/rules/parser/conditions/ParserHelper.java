package jadex.rules.parser.conditions;

import jadex.rules.parser.conditions.javagrammar.ConstraintBuilder;
import jadex.rules.parser.conditions.javagrammar.DefaultParserHelper;
import jadex.rules.parser.conditions.javagrammar.Expression;
import jadex.rules.parser.conditions.javagrammar.IParserHelper;
import jadex.rules.parser.conditions.javagrammar.JavaJadexLexer;
import jadex.rules.parser.conditions.javagrammar.JavaJadexParser;
import jadex.rules.parser.conditions.javagrammar.OperationExpression;
import jadex.rules.parser.conditions.javagrammar.UnaryExpression;
import jadex.rules.parser.conditions.javagrammar.VariableExpression;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.NotCondition;
import jadex.rules.rulesystem.rules.Variable;
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
	 *  @param invert True if the user condition needs to be inverted.
	 *  @return The condition.
	 */
	public static ICondition parseCondition(ICondition precon, String text, String language, OAVTypeModel model, String[] imports, List errors, IParserHelper helper, Variable returnvar, boolean invert)
	{
		ICondition	ret	= null;

		if(language==null || language.equals("clips"))
		{
			ICondition	usercon	= parseClipsCondition(text, model, imports, errors);
			if(invert)
				usercon	= new NotCondition(usercon);
			ret	= precon==null ? usercon : usercon==null ? precon : new AndCondition(new ICondition[]{precon, usercon});
		}
		else if(language.equals("jcl"))
		{
			ret	= parseJavaCondition(text, imports, errors, helper, returnvar, invert);
		}
		
		return ret;
	}
	
	/**
	 *  Parse a condition.
	 *  @param text The text.
	 *  @param model The model.
	 *  @return The condition.
	 */
	public static ICondition parseClipsCondition(String text, OAVTypeModel model)
	{
		return parseClipsCondition(text, model, null, null);
	}	

	/**
	 *  Parse a condition.
	 *  @param text The text.
	 *  @param model The model.
	 *  @param imports The imports.
	 *  @return The condition.
	 */
	public static ICondition parseClipsCondition(String text, OAVTypeModel model, String[] imports)
	{
		return parseClipsCondition(text, model, imports, null);
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
			{
				errors.add(e.toString());
			}
			else
			{
				if(e instanceof RuntimeException)
					throw (RuntimeException)e;
				else
					throw new RuntimeException(e);
			}
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
	public static ICondition parseJavaCondition(String text, OAVTypeModel model)
	{
		return parseJavaCondition(text, null, null, new DefaultParserHelper(null, model), null, false);
	}

	/**
	 *  Parse a condition.
	 *  @param text The text.
	 *  @param model The model.
	 *  @param imports The imports.
	 *  @return The condition.
	 */
	public static ICondition parseJavaCondition(String text, OAVTypeModel model, String[] imports)
	{
		return parseJavaCondition(text, imports, null, new DefaultParserHelper(null, model), null, false);
	}

	/**
	 *  Parse a condition.
	 *  @param text The text.
	 *  @param model The model.
	 *  @return The condition.
	 */
	public static ICondition parseJavaCondition(String text, String[] imports, List errors, IParserHelper helper, Variable returnvar, boolean invert)
	{
		ICondition	ret	= null;
		ANTLRStringStream exp = new ANTLRStringStream(text);
		JavaJadexLexer lexer = new JavaJadexLexer(exp);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		JavaJadexParser parser = new JavaJadexParser(tokens);
		try
		{
			parser.setParserHelper(helper);
			parser.setImports(imports);
			Expression	pexp	= parser.lhs();

			if(returnvar!=null)
			{
				ret	= ConstraintBuilder.buildConstraints(new OperationExpression(pexp, new VariableExpression(returnvar), IOperator.EQUAL), helper.getBuildContext());
			}
			else if(invert)
			{
				ret	= ConstraintBuilder.buildConstraints(new UnaryExpression(pexp, UnaryExpression.OPERATOR_NOT), helper.getBuildContext());
			}
			else
			{
				ret	= ConstraintBuilder.buildConstraints(pexp, helper.getBuildContext());
			}

//			if(invert)
//			{
//				List	positives	= ((ComplexCondition)ret).getConditions();
//				List	negatives	= new ArrayList();
//				for(int i=0; i<positives.size(); i++)
//				{
//					if(helper.getGeneratedConditions().contains(positives.get(i)))
//					{
//						negatives.add(positives.get(i));
//						positives.remove(i);
//						i--;
//					}
//				}
//				
//				positives.add(new NotCondition(new AndCondition(negatives)));
//				ret	= new AndCondition(positives);
//			}
		}
		catch(Exception e)
//		catch(RecognitionException e)
		{
			if(errors!=null)
				errors.add(e.toString());
			else
			{
				if(e instanceof RuntimeException)
					throw (RuntimeException)e;
				else
					throw new RuntimeException(e);
			}
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
