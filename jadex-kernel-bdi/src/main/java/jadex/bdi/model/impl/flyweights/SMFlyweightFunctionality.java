package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.rules.state.IOAVState;

/**
 *  Static helper methods for model flyweights.
 */
public class SMFlyweightFunctionality
{
	/**
	 *  Create an expression.
	 *  @param expression	The expression.
	 *  @param language	The expression language or null for default java-like language.
	 *  @param state	The state.
	 *  @param scope	The scope.
	 *  @return	The expression
	 */
	public static MExpressionFlyweight	createExpression(String expression, String language, IOAVState state, Object scope)
	{
		Object	mexp	= state.createObject(OAVBDIMetaModel.expression_type);
		state.setAttributeValue(mexp, OAVBDIMetaModel.expression_has_language, language);
	
		IExpressionParser	exp_parser	= new JavaCCExpressionParser();	// Hack!!! Map language to parser somewhere?
		IParsedExpression	pexp	= exp_parser.parseExpression(expression,
			OAVBDIMetaModel.getImports(state, scope), null, state.getTypeModel().getClassLoader());
		state.setAttributeValue(mexp, OAVBDIMetaModel.expression_has_content, pexp);
		
		return new MExpressionFlyweight(state, scope, mexp);
	}

}
