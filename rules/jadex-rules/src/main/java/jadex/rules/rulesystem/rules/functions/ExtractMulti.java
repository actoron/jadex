package jadex.rules.rulesystem.rules.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jadex.commons.SUtil;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.rulesystem.rules.ILazyValue;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

/**
 *  Extract values from a multivariable.
 *  Input: $?multivar, attr
 *  Output: set of attribute values (foreach object of multivar {add(get object.attr)}) 
 */
public class ExtractMulti implements IFunction
{
	//-------- attributes -------- 
	
	/** The attribute. */
	protected OAVAttributeType attr;
	
	//-------- constructors -------- 
	
	/**
	 *  Create a new function.
	 */
	public ExtractMulti(OAVAttributeType attr)
	{
		this.attr = attr;
		if(attr==null)
			throw new IllegalArgumentException("Attribute is null.");
	}
	
	//-------- methods -------- 
	
	/**
	 *  Invoke a function and return a value (optional).
	 *  @param paramvalues The parameter values.
	 *  @return The function value. 
	 */
	public Object invoke(Object[] paramvalues, IOAVState state)
	{
		if(paramvalues==null || paramvalues.length!=1)
			throw new IllegalArgumentException("Function needs one parameter: "+SUtil.arrayToString(paramvalues));
			
		Object val1 = paramvalues[0] instanceof ILazyValue? ((ILazyValue)paramvalues[0]).getValue(): paramvalues[0]; 

		Collection objects = (Collection)val1;
		if(objects==null)
			throw new IllegalArgumentException("Objects are null.");
		
		List ret = new ArrayList();
		for(Iterator it=objects.iterator(); it.hasNext(); )
		{
			ret.add(state.getAttributeValue(it.next(), attr));
		}
		
//		System.out.println("Extracted: "+ret);
		
		return ret;
	}
	
	/**
	 *  Get the return type of this function.
	 */
	public Class getReturnType()
	{
		// todo: does this make sense?
		return List.class;
	}
	
	/**
	 *  Get the set of relevant attribute types.
	 *  @return The relevant attribute types.
	 */
	public AttributeSet	getRelevantAttributes()
	{
		AttributeSet ret = new AttributeSet();
		if(attr!=null)
			ret.addAttribute(attr);
		return ret;
	}
	
	
	
	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return "extract";
	}

	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof ExtractMulti;
	}
}
