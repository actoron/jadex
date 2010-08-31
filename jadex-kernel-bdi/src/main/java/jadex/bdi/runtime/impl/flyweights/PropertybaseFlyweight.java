package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MPropertybaseFlyweight;
import jadex.bdi.runtime.IPropertybase;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for the property base.
 */
public class PropertybaseFlyweight extends ElementFlyweight implements IPropertybase 
{
	//-------- constructors --------
	
	/**
	 *  Create a new beliefbase flyweight.
	 *  @param state The state.
	 *  @param scope The scope handle.
	 */
	private PropertybaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static PropertybaseFlyweight getPropertybaseFlyweight(IOAVState state, Object scope)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		PropertybaseFlyweight ret = (PropertybaseFlyweight)ip.getFlyweightCache(IPropertybase.class, new Tuple(IPropertybase.class, scope));
		if(ret==null)
		{
			ret = new PropertybaseFlyweight(state, scope);
			ip.putFlyweightCache(IPropertybase.class, new Tuple(IPropertybase.class, scope), ret);
		}
		return ret;
	}
	
	//-------- methods --------

	/**
	 *  Get a property.
	 *  @param name The property name.
	 *  @return The property value.
	 */
	public Object getProperty(final String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = AgentRules.getPropertyValue(getState(), getScope(), name);
				}
			};
			return invoc.object;
		}
		else
		{
			return AgentRules.getPropertyValue(getState(), getScope(), name);
		}
	}

	/**
	 *  Get all properties.
	 *  @return An array of property names.
	 */
	public String[] getPropertyNames()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					sarray = SFlyweightFunctionality.getPropertyNames(getState(), getHandle());
				}
			};
			return invoc.sarray;
		}
		else
		{
			return SFlyweightFunctionality.getPropertyNames(getState(), getHandle());
		}
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
