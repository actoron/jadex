package jadex.bdi.runtime.impl;

import jadex.bdi.interpreter.AgentRules;
import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.IPropertybase;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;


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
		PropertybaseFlyweight ret = (PropertybaseFlyweight)ip.getFlyweightCache(IPropertybase.class).get(new Tuple(IPropertybase.class, scope));
		if(ret==null)
		{
			ret = new PropertybaseFlyweight(state, scope);
			ip.getFlyweightCache(IPropertybase.class).put(new Tuple(IPropertybase.class, scope), ret);
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
					sarray = new String[0];
					Collection coll = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.capability_has_properties);
					if(coll!=null)
					{
						sarray = new String[coll.size()];
						int i = 0;
						for(Iterator it=coll.iterator(); it.hasNext(); )
						{
							Object prop = it.next();
							sarray[i++] = (String)getState().getAttributeValue(prop, OAVBDIRuntimeModel.parameter_has_name);
						}
					}
				}
			};
			return invoc.sarray;
		}
		else
		{
			String[] ret = new String[0];
			Collection coll = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.capability_has_properties);
			if(coll!=null)
			{
				ret = new String[coll.size()];
				int i = 0;
				for(Iterator it=coll.iterator(); it.hasNext(); )
				{
					Object prop = it.next();
					ret[i++] = (String)getState().getAttributeValue(prop, OAVBDIRuntimeModel.parameter_has_name);
				}
			}
			return ret;
		}
	}
}
