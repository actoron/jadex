package jadex.rules.rulesystem.rete.nodes;

import java.util.List;

import jadex.commons.SReflect;
import jadex.rules.state.OAVAttributeType;


/**
 *  A virtual fact represents a fact with some specified multislot
 *  variable bindings.
 *  The values of the virtual fact store the bindings for the sub variables,
 *  i.e. [var0] -> {val1, val2, ..}, [var1] -> {val3}, ...
 */
public class VirtualFact
{
	//-------- attributes --------
	
	/** The real fact (object). */
	protected Object object;
	
	/** The attribute. */
	protected OAVAttributeType attr;
	
	/** The values of the sub attributes. */
	protected List values;
	
	//-------- constructors --------
	
	/**
	 *  Create a new virtual fact.
	 */
	public VirtualFact(Object object, OAVAttributeType attr, List values)
	{
		this.object = object;
		this.attr = attr;
		this.values = values;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the real fact.
	 *  @return The real fact.
	 */
	public Object getObject()
	{
		Object ret = object;
		while(ret instanceof VirtualFact)
			ret = ((VirtualFact)ret).getObject();
		return ret;
	}
	
	/**
	 *  Get the attribute.
	 *  @return The attribute.
	 */
	public OAVAttributeType getAttribute()
	{
		return attr;
	}
	
	/**
	 *  Get the containing fact.
	 *  @return The containing fact.
	 */
	public VirtualFact getSuperFact()
	{
		return object instanceof VirtualFact? (VirtualFact)object: null;
	}
	
	/**
	 *  Get the value or values of a sub attribute.
	 *  The subindex specifies the variable which should be accessed.
	 *  Each virtual fact has bindings for every subvariable, i.e.
	 *  the values contain for each variable its current values.
	 *  @param attr The attribute.
	 *  @param subindex The subindex.
	 */
	public Object getSubAttributeValue(OAVAttributeType attr, int subindex)
	{
		assert attr!=null;
		
		Object ret;
		if(attr.equals(getAttribute()))
		{
			ret = values.get(subindex);
		}
		else
		{
			ret = getSuperFact().getSubAttributeValue(attr, subindex);
		}
		return ret;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return SReflect.getInnerClassName(this.getClass())+
			"object="+object+" attribute="+attr+", values="+values+")";
	}
}
