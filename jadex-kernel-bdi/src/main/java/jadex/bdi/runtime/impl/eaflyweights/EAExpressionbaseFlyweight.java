package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IEAExpressionbase;
import jadex.bdi.runtime.impl.FlyweightFunctionality;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for the expression base.
 */
public class EAExpressionbaseFlyweight extends ElementFlyweight implements IEAExpressionbase
{
	//-------- constructors --------
	
	/**
	 *  Create a new expressionbase flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private EAExpressionbaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static EAExpressionbaseFlyweight getExpressionbaseFlyweight(IOAVState state, Object scope)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		EAExpressionbaseFlyweight ret = (EAExpressionbaseFlyweight)ip.getFlyweightCache(IEAExpressionbase.class).get(new Tuple(IEAExpressionbase.class, scope));
		if(ret==null)
		{
			ret = new EAExpressionbaseFlyweight(state, scope);
			ip.getFlyweightCache(IEAExpressionbase.class).put(new Tuple(IEAExpressionbase.class, scope), ret);
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
	public IFuture getExpression(final String name)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object[] scope = AgentRules.resolveCapability(name, OAVBDIMetaModel.expression_type, getScope(), getState());
					ret.setResult(FlyweightFunctionality.createExpression(getState(), scope[1], (String)scope[0], true));
				}
			});
		}
		else
		{
			Object[] scope = AgentRules.resolveCapability(name, OAVBDIMetaModel.expression_type, getScope(), getState());
			ret.setResult(FlyweightFunctionality.createExpression(getState(), scope[1], (String)scope[0], true));
		}
		
		return ret;
	}

	/**
	 *  Create a precompiled expression.
	 *  @param expression	The expression string.
	 *  @return The precompiled expression.
	 */
	public IFuture createExpression(String expression)
	{
		return createExpression(expression, null, null);
	}

	/**
	 *  Create a precompiled expression.
	 *  @param expression	The expression string.
	 *  @return The precompiled expression.
	 */
	public IFuture createExpression(final String expression, final String[] paramnames, final Class[] paramtypes)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(FlyweightFunctionality.createExpression(getState(), getScope(), false, expression, paramnames, paramtypes));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.createExpression(getState(), getScope(), false, expression, paramnames, paramtypes));
		}
		
		return ret;
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

