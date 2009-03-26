package jadex.rules.parser.conditions;

import jadex.rules.parser.conditions.javagrammar.ConditionBuilder;
import jadex.rules.parser.conditions.javagrammar.Constraint;
import jadex.rules.parser.conditions.javagrammar.IParserHelper;
import jadex.rules.parser.conditions.javagrammar.JavaJadexLexer;
import jadex.rules.parser.conditions.javagrammar.JavaJadexParser;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.ComplexCondition;
import jadex.rules.rulesystem.rules.NotCondition;
import jadex.rules.state.OAVTypeModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public static ICondition parseCondition(ICondition precon, String text, String language, OAVTypeModel model, String[] imports, List errors, IParserHelper helper, boolean invert)
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
			ret	= parseJavaCondition(precon, text, model, imports, errors, helper, invert);
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
	public static ICondition parseJavaCondition(ICondition precon, String text, OAVTypeModel model, String[] imports, List errors, IParserHelper helper, boolean invert)
	{
		ICondition	ret	= null;
		ANTLRStringStream exp = new ANTLRStringStream(text);
		JavaJadexLexer lexer = new JavaJadexLexer(exp);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		JavaJadexParser parser = new JavaJadexParser(tokens);
		try
		{
			Set	inicons	= new HashSet(helper.getConditions());
			parser.setParserHelper(helper);
			parser.lhs();
			precon	= new AndCondition(helper.getConditions());

			Constraint[]	constraints	= new Constraint[parser.getStack().size()];
			for(int i=0; i<constraints.length; i++)
			{
				if(parser.getStack().get(i) instanceof Constraint)
					constraints[i]	= (Constraint)parser.getStack().get(i);
				else
					constraints[i]	= helper.completeConstraint(parser.getStack().get(i));
			}

			if(invert)
			{
				List	newcons	= helper.getConditions();
				Set	generated	= new HashSet();
				for(int i=0; i<newcons.size(); i++)
				{
					if(!inicons.contains(newcons.get(i)))
					{
						generated.add(newcons.get(i));
					}
				}

				ret	= ConditionBuilder.buildCondition(constraints, precon, model, generated, invert);
				
				List	positives	= ((ComplexCondition)ret).getConditions();
				List	negatives	= new ArrayList();
				for(int i=0; i<positives.size(); i++)
				{
					if(generated.contains(positives.get(i)))
					{
						negatives.add(positives.get(i));
						positives.remove(i);
						i--;
					}
				}
				
				positives.add(new NotCondition(new AndCondition(negatives)));
				ret	= new AndCondition(positives);
			}
			else
			{
				ret	= ConditionBuilder.buildCondition(constraints, precon, model, null, invert);				
			}
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
