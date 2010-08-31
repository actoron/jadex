package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MPropertybaseFlyweight;
import jadex.bdi.runtime.IEAPropertybase;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for the property base.
 */
public class EAPropertybaseFlyweight extends ElementFlyweight implements IEAPropertybase 
{
	//-------- constructors --------
	
	/**
	 *  Create a new beliefbase flyweight.
	 *  @param state The state.
	 *  @param scope The scope handle.
	 */
	private EAPropertybaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static EAPropertybaseFlyweight getPropertybaseFlyweight(IOAVState state, Object scope)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		EAPropertybaseFlyweight ret = (EAPropertybaseFlyweight)ip.getFlyweightCache(IEAPropertybase.class, new Tuple(IEAPropertybase.class, scope));
		if(ret==null)
		{
			ret = new EAPropertybaseFlyweight(state, scope);
			ip.putFlyweightCache(IEAPropertybase.class, new Tuple(IEAPropertybase.class, scope), ret);
		}
		return ret;
	}
	
	//-------- methods --------

	/**
	 *  Get a property.
	 *  @param name The property name.
	 *  @return The property value.
	 */
	public IFuture getProperty(final String name)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(AgentRules.getPropertyValue(getState(), getScope(), name));
				}
			});
		}
		else
		{
			ret.setResult(AgentRules.getPropertyValue(getState(), getScope(), name));
		}
		
		return ret;
	}

	/**
	 *  Get all properties.
	 *  @return An array of property names.
	 */
	public IFuture getPropertyNames()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(SFlyweightFunctionality.getPropertyNames(getState(), getHandle()));
				}
			});
		}
		else
		{
			ret.setResult(SFlyweightFunctionality.getPropertyNames(getState(), getHandle()));
		}
		
		return ret;
	}
	
	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public IMElement getModelElement()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					object = new MPropertybaseFlyweight(getState(), mscope);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			return new MPropertybaseFlyweight(getState(), mscope);
		}
	}
}

