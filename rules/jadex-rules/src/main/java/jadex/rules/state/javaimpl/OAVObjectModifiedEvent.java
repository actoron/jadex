package jadex.rules.state.javaimpl;

import jadex.commons.SUtil;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;

/**
 *  Internal object modified event.
 */
class OAVObjectModifiedEvent
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;
	
	/** The object id. */
	protected Object id;
	
	/** The object type. */
	protected OAVObjectType type;
	
	/** The attribute. */
	protected OAVAttributeType attribute;
	
	/** The old value. */
	protected Object oldvalue;
	
	/** The new value. */
	protected Object newvalue;
	
	//-------- constructors --------

	/**
	 *  Create a new object modified event.
	 */
	public OAVObjectModifiedEvent(IOAVState	state, Object id, OAVObjectType type, 
		OAVAttributeType attribute, Object oldvalue, Object newvalue)
	{
		this.state	= state;
		this.id = id;
		this.type = type;
		this.attribute = attribute;
		this.oldvalue = oldvalue;
		this.newvalue = newvalue;
	}
	
	//-------- methods --------
	
	/**
	 *  Compute the hascode.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		int ret;
		if(!attribute.getMultiplicity().equals(OAVAttributeType.NONE))
		{
			ret = super.hashCode();
		}
		else
		{
			final int prime = 31; 
			ret = prime * id.hashCode();
			ret = prime * ret + type.hashCode();
			ret = prime * ret + attribute.hashCode();
		}
		
		return ret;
	}

	/**
	 *  Test for equality.
	 *  @param obj The object to test.
	 *  @return True, if equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = obj==this;
		
		if(!ret && attribute.getMultiplicity().equals(OAVAttributeType.NONE)
			&& obj instanceof OAVObjectModifiedEvent)
		{
			// Problem multi attributes:
			// a) add/remove to a list (2x same object would be different) 
			// -> keep both events 
			// b) add/remove to a set (2x same object would not be different)
			// -> last add/remove should not have worked! no event should have been created
			
			// For a single attribute only the last event should occur
			
			OAVObjectModifiedEvent evt = (OAVObjectModifiedEvent)obj;
			if(state.equals(id, evt.id) && SUtil.equals(type, evt.type)
				&& SUtil.equals(attribute, evt.attribute)
			)
			{
				ret = true;
			}
		}
		return ret;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "OAVObjectModifiedEvent(id="+id+", type="+type+", attribute="+attribute+", oldval="+oldvalue+", newval="+newvalue+")";
	}
}
