package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMExpression;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEExpression;
import jadex.javaparser.IParsedExpression;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for expression model.
 */
public class MExpressionFlyweight extends MReferenceableElementFlyweight implements IMExpression, IMEExpression
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MExpressionFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the expression language.
	 *  @return The language.
	 */
	public String getLanguage()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_language);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_language);
		}
	}
	
	/**
	 *  Get the expression text.
	 *  @return The text.
	 */
	public String	getText()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_text);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_text);
		}
	}
	
	/**
	 *  Get the parsed expression.
	 *  @return The parsed expression.
	 */
	public Object getParsedExpression()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_parsed);
				}
			};
			return invoc.object;
		}
		else
		{
			return getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_parsed);
		}
	}
	
	/**
	 *  Get the clazz.
	 *  @return The clazz. 
	 */
	public Class getClazz()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					clazz = (Class)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_class);
				}
			};
			return invoc.clazz;
		}
		else
		{
			return (Class)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_class);
		}
	}
	
	/**
	 *  Get the class name.
	 *  @return The class name. 
	 */
	public String getClassname()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_classname);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_classname);
		}
	}
	
	/**
	 *  Get the variable name.
	 *  @return The variable name.
	 */
	public String getVariable()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_variable);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_variable);
		}
	}

	/**
	 *  Set the expression.
	 *  @param expression The expression.
	 *  @param language The language (null for default java-like language).
	 */
	public void setExpression(final String expression, final String language)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					IParsedExpression pexp = MExpressionbaseFlyweight.parseExpression(expression, language, getState(), getHandle());
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_parsed, pexp);
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_language, language);
				}
			};
		}
		else
		{
			IParsedExpression pexp = MExpressionbaseFlyweight.parseExpression(expression, language, getState(), getHandle());
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_parsed, pexp);
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_language, language);
		}
	}
	
	/**
	 *  Set the expression content (i.e. parsed expression or condition).
	 *  @param content The content.
	 */
	public void setContent(final Object content)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_parsed, content);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_parsed, content);
		}
		
	}
	
	/**
	 *  Set the clazz.
	 *  @param clazz The clazz. 
	 */
	public void setClazz(final Class clz)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_class, clz);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_class, clz);
		}
	}
	
//	/**
//	 *  Set the class name.
//	 *  @param name The class name. 
//	 */
//	public void setClassname(String name);
	
	/**
	 *  Set the variable name.
	 *  @param var The variable name.
	 */
	public void setVariable(final String var)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_variable, var);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_variable, var);
		}
		
	}
}
