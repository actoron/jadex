package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMExpressionReference;
import jadex.bdi.model.IMExpressionbase;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEExpression;
import jadex.bdi.model.editable.IMEExpressionbase;
import jadex.bdi.model.editable.IMEMetaGoal;
import jadex.bdi.model.impl.flyweights.MElementFlyweight.AgentInvocation;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for expression base model.
 */
public class MExpressionbaseFlyweight  extends MElementFlyweight implements IMExpressionbase, IMEExpressionbase
{
	//-------- constructors --------
	
	/**
	 *  Create a new Expressionbase flyweight.
	 */
	public MExpressionbaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	//-------- methods concerning Expressions --------

	
	/**
	 *  Get a expression for a name.
	 *  @param name	The expression name.
	 */
	public IMExpression getExpression(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_expressions, name);
					if(handle==null)
						throw new RuntimeException("Expression not found: "+name);
					object = new MExpressionFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMExpression)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_expressions, name);
			if(handle==null)
				throw new RuntimeException("Expression not found: "+name);
			return new MExpressionFlyweight(getState(), getScope(), handle);
		}
	}

	/**
	 *  Returns all expressions.
	 *  @return All expressions.
	 */
	public IMExpression[] getExpressions()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_expressions);
					IMExpression[] ret = new IMExpression[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MExpressionFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMExpression[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_expressions);
			IMExpression[] ret = new IMExpression[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MExpressionFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get a expression reference for a name.
	 *  @param name	The expression name.
	 */
	public IMExpressionReference getExpressionReference(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_expressionrefs, name);
					if(handle==null)
						throw new RuntimeException("Expression reference not found: "+name);
					object = new MExpressionReferenceFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMExpressionReference)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_expressionrefs, name);
			if(handle==null)
				throw new RuntimeException("Expression reference not found: "+name);
			return new MExpressionReferenceFlyweight(getState(), getScope(), handle);
		}
	}

	/**
	 *  Returns all expression references.
	 *  @return All expression references.
	 */
	public IMExpressionReference[] getExpressionReferences()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_expressionrefs);
					IMExpressionReference[] ret = new IMExpressionReference[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MExpressionReferenceFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMExpressionReference[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_expressionrefs);
			IMExpressionReference[] ret = new IMExpressionReference[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MExpressionReferenceFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Create a expression with a name.
	 *  @param name	The expression name.
	 *  @param content The expression content.
	 *  @param lang The language.
	 */
	public IMEExpression createExpression(final String name, final String content, final String lang)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					MExpressionFlyweight fly = createExpression(content, lang, getState(), getScope());
					fly.setName(name);
					object = fly;
				}
			};
			return (IMEExpression)invoc.object;
		}
		else
		{
			MExpressionFlyweight fly = createExpression(content, lang, getState(), getScope());
			fly.setName(name);
			return fly;
		}
	}

	/**
	 *  Create an expression reference with a name.
	 *  @param name	The expression name.
	 *  @param ref The reference element name.
	 */
	public IMExpressionReference createExpressionReference(String name, String ref)
	{
		
	}
	
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
