package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.impl.flyweights.MExpressionbaseFlyweight;
import jadex.bdi.runtime.IExpression;
import jadex.bdi.runtime.IExpressionbase;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Tuple;
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
		BDIInterpreter ip = BDIAgentFeature.getInterpreter(state);
		ExpressionbaseFlyweight ret = (ExpressionbaseFlyweight)ip.getFlyweightCache(IExpressionbase.class, new Tuple(IExpressionbase.class, scope));
		if(ret==null)
		{
			ret = new ExpressionbaseFlyweight(state, scope);
			ip.putFlyweightCache(IExpressionbase.class, new Tuple(IExpressionbase.class, scope), ret);
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
	public IExpression	getExpression(final String name)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(name)
			{
				public void run()
				{
					Object[] scope = AgentRules.resolveCapability(name, OAVBDIMetaModel.expression_type, getScope(), getState());
					object = SFlyweightFunctionality.createExpression(getState(), scope[1], (String)scope[0]);
				}
			};
			return (IExpression)invoc.object;
		}
		else
		{
			Object[] scope = AgentRules.resolveCapability(name, OAVBDIMetaModel.expression_type, getScope(), getState());
			return (IExpression)SFlyweightFunctionality.createExpression(getState(), scope[1], (String)scope[0]);
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
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = SFlyweightFunctionality.createExpression(getState(), getScope(), expression, paramnames, paramtypes);
				}
			};
			return (IExpression)invoc.object;
		}
		else
		{
			return (IExpression)SFlyweightFunctionality.createExpression(getState(), getScope(), expression, paramnames, paramtypes);
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
	 */
	public IMElement getModelElement()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
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
	}
}
