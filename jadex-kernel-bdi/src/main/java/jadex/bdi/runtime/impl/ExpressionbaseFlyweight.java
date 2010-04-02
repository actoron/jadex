package jadex.bdi.runtime.impl;

import java.util.HashMap;
import java.util.Map;

import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.IExpression;
import jadex.bdi.runtime.IExpressionbase;
import jadex.commons.Tuple;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for the expression base.
 */
public class ExpressionbaseFlyweight extends ElementFlyweight implements IExpressionbase
{
	//-------- constructors --------
	
	/**
	 *  Create a new expressionbase flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private ExpressionbaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static ExpressionbaseFlyweight getExpressionbaseFlyweight(IOAVState state, Object scope)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		ExpressionbaseFlyweight ret = (ExpressionbaseFlyweight)ip.getFlyweightCache(IExpressionbase.class).get(new Tuple(IExpressionbase.class, scope));
		if(ret==null)
		{
			ret = new ExpressionbaseFlyweight(state, scope);
			ip.getFlyweightCache(IExpressionbase.class).put(new Tuple(IExpressionbase.class, scope), ret);
		}
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Get a predefined expression. 
	 *  Creates a new instance on every call.
	 *  @param name	The name of an expression defined in the ADF.
	 *  @return The expression object.
	 */
	// changed signature for javaflow, removed final
	public IExpression	getExpression(String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(name)
			{
				public void run()
				{
					Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					Object mexp = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_expressions, arg);
					if(mexp==null)
						throw new RuntimeException("Unknown expression: "+arg);
					object = ExpressionFlyweight.getExpressionFlyweight(getState(), getScope(), mexp);
				}
			};
			return (IExpression)invoc.object;
		}
		else
		{
			Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			Object mexp = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_expressions, name);
			if(mexp==null)
				throw new RuntimeException("Unknown expression: "+name);
			return ExpressionFlyweight.getExpressionFlyweight(getState(), getScope(), mexp);
		}
	}

	/**
	 *  Create a precompiled expression.
	 *  @param expression	The expression string.
	 *  @return The precompiled expression.
	 */
	public IExpression	createExpression(String expression)
	{
		return createExpression(expression, null, null);
	}

	/**
	 *  Create a precompiled expression.
	 *  @param expression	The expression string.
	 *  @return The precompiled expression.
	 */
	public IExpression	createExpression(final String expression, final String[] paramnames, final Class[] paramtypes)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					// Hack!!! Should be configurable.
					IExpressionParser	exp_parser	= new JavaCCExpressionParser();
					Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					String[] imports	= OAVBDIMetaModel.getImports(getState(), mcapa);
					
					Map	params	= null;
					if(paramnames!=null)
					{
						params	= new HashMap();
						for(int i=0; i<paramnames.length; i++)
						{
							params.put(paramnames[i], getState().getTypeModel().getJavaType(paramtypes[i]));
						}
					}
					
					IParsedExpression pex = exp_parser.parseExpression(expression, imports, params, Thread.currentThread().getContextClassLoader());
					object = new ExpressionNoModel(getState(), getScope(), pex);
				}
			};
			return (IExpression)invoc.object;
		}
		else
		{
			// Hack!!! Should be configurable.
			IExpressionParser	exp_parser	= new JavaCCExpressionParser();
			Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			String[] imports	= OAVBDIMetaModel.getImports(getState(), mcapa);
			
			Map	params	= null;
			if(paramnames!=null)
			{
				params	= new HashMap();
				for(int i=0; i<paramnames.length; i++)
				{
					params.put(paramnames[i], getState().getTypeModel().getJavaType(paramtypes[i]));
				}
			}
			
			IParsedExpression pex = exp_parser.parseExpression(expression, imports, params, Thread.currentThread().getContextClassLoader());
			return new ExpressionNoModel(getState(), getScope(), pex);
		}
	}
	
	/**
	 *  Get a condition, that is triggered whenever the expression
	 *  value changes to true.
	 *  Creates a new instance on every call.
	 *  @param name	The condition name.
	 *  @return The condition.
	 */
//	public ICondition	getCondition(String name);

	/**
	 *  Create a condition, that is triggered whenever the expression
	 *  value changes to true.
	 *  @param expression	The condition expression.
	 *  @return The condition.
	 */
//	public ICondition	createCondition(String expression);

	/**
	 *  Create a condition.
	 *  @param expression	The condition expression.
	 *  @param trigger	The condition trigger.
	 *  @return The condition.
	 */
//	public ICondition	createCondition(String expression, String trigger, String[] paramnames, Class[] paramtypes);

	/**
	 *  Register a new expression model.
	 *  @param mexpression The expression model.
	 */
//	public void registerExpression(IMExpression mexpression);

	/**
	 *  Register a new expression reference model.
	 *  @param mexpression The expression reference model.
	 */
//	public void registerExpressionReference(IMExpressionReference mexpression);

	/**
	 *  Register a new condition model.
	 *  @param mcondition The condition model.
	 */
//	public void registerCondition(IMCondition mcondition);

	/**
	 *  Register a new condition reference model.
	 *  @param mcondition The condition reference model.
	 */
//	public void registerConditionReference(IMConditionReference mcondition);

	/**
	 *  Deregister an expression model.
	 *  @param mexpression The expression model.
	 */
//	public void deregisterExpression(IMExpression mexpression);

	/**
	 *  Deregister an expression reference model.
	 *  @param mexpression The expression reference model.
	 */
//	public void deregisterExpressionReference(IMExpressionReference mexpression);
	
	/**
	 *  Deregister an condition model.
	 *  @param mcondition The condition model.
	 */
//	public void deregisterCondition(IMCondition mcondition);

	/**
	 *  Deregister an condition reference model.
	 *  @param mcondition The condition reference model.
	 */
//	public void deregisterConditionReference(IMConditionReference mcondition);
	
	//-------- listeners --------
	
	/**
	 *  Add a condition listener.
	 *  @param type The condition type.
	 *  @param listener The condition listener.
	 *  @param async True, if the notification should be done on a separate thread.
	 */
//	public void addConditionListener(String type, IConditionListener listener, boolean async);
	
	/**
	 *  Remove a condition listener.
	 *  @param type The condition type.
	 *  @param listener The condition listener.
	 */
//	public void removeConditionListener(String type, IConditionListener listener);

	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 * /
	public IMElement getModelElement()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					object = new MExpressionbaseFlyweight(getState(), mscope);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			return new MExpressionbaseFlyweight(getState(), mscope);
		}
	}*/
}
