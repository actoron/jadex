package jadex.rules.rulesystem.rete.extractors;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;

/**
 * 
 */
public class AttributeSet implements Cloneable
{
	//-------- constants --------
	
	/** The constant empty attribute set. */
	public static final AttributeSet EMPTY_ATTRIBUTESET = new AttributeSet();
	
	//-------- attributes --------
	
	/** The set of normal attributes. */
	protected Set attributes;

	/** The set of 'all' attributes for OAVJavaTypes. */
	protected Set alltypes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new attribute set. 
	 */
	public AttributeSet()
	{
	}
	
	//-------- methods --------
	
	/**
	 *  Add a new attributes.
	 *  @param attr The attribute.
	 */
	public void addAttribute(OAVAttributeType attr)
	{
//		boolean found	= false;
//		StackTraceElement[]	ste	= Thread.currentThread().getStackTrace();
//		for(int i=0; !found && i<ste.length; i++)
//		{
//			found	= ste[i].getClassName().equals("jadex.rules.rulesystem.rete.nodes.ReteNode")
//			&& (ste[i].getMethodName().equals("getRelevantAttributes")
//				|| ste[i].getMethodName().equals("getIndirectNodes"));
//		}
//		if(!found)
//			Thread.dumpStack();
		if(attributes==null)
			attributes = new HashSet();
		
		attributes.add(attr);
	}
	
	/**
	 *  Remove an attributes.
	 *  @param attr The attribute.
	 */
	public void removeAttribute(OAVAttributeType attr)
	{
//		boolean found	= false;
//		StackTraceElement[]	ste	= Thread.currentThread().getStackTrace();
//		for(int i=0; !found && i<ste.length; i++)
//		{
//			found	= ste[i].getClassName().equals("jadex.rules.rulesystem.rete.nodes.ReteNode")
//			&& (ste[i].getMethodName().equals("getRelevantAttributes")
//				|| ste[i].getMethodName().equals("getIndirectNodes"));
//		}
//		if(!found)
//			Thread.dumpStack();
		if(attributes!=null)
			attributes.remove(attr);
	}
	
	/**
	 *  Add type for all attributes.
	 *  @param alltype The alltype.
	 */
	public void addAllType(OAVJavaType alltype)
	{
		if(alltypes==null)
			alltypes = new HashSet();
			
		alltypes.add(alltype);
	}
	
	/**
	 *  Test if an attribute is contained in the attribute set.
	 *  @param attr The attribute.
	 *  @return True, if contained.
	 */
	public boolean contains(OAVAttributeType attr)
	{
		boolean ret = attributes==null? false: attributes.contains(attr);
		
		if(!ret && alltypes!=null && attr.getObjectType() instanceof OAVJavaType)
		{
			ret = alltypes.contains(attr.getObjectType());
			if(!ret)
			{
				OAVJavaType attrtype = (OAVJavaType)attr.getObjectType();
				for(Iterator it=alltypes.iterator(); it.hasNext() && !ret; )
				{
					OAVJavaType type = (OAVJavaType)it.next();
					ret = attrtype.isSubtype(type);
				}
			}
		}
		
		// todo: build up negative list for already tested java types to improve performance.
		
		return ret;
	}
	
	/**
	 *  Get the attribute set.
	 *  @return The attribute set. 
	 */
	public Set getAttributeSet()
	{
		return attributes;
	}
	
	/**
	 *  Get the all types set.
	 *  @return The all types set. 
	 */
	public Set getAllTypesSet()
	{
		return alltypes;
	}
	
	/**
	 *  Add all elements on another attribute set.
	 *  @param attrset The set to add. 
	 */
	public void addAll(AttributeSet attrset)
	{
//		boolean found	= false;
//		StackTraceElement[]	ste	= Thread.currentThread().getStackTrace();
//		for(int i=0; !found && i<ste.length; i++)
//		{
//			found	= (ste[i].getClassName().equals("jadex.rules.rulesystem.rete.nodes.ReteNode")
//				|| ste[i].getClassName().equals("jadex.rules.rulesystem.rete.extractors.JavaMethodExtractor"))
//				&& (ste[i].getMethodName().equals("getRelevantAttributes")
//					|| ste[i].getMethodName().equals("getIndirectNodes"));
//		}
//		if(!found)
//			Thread.dumpStack();
		if(attrset.getAttributeSet()!=null)
		{
			if(attributes==null)
				attributes = new HashSet();
			attributes.addAll(attrset.getAttributeSet());
		}
		if(attrset.getAllTypesSet()!=null)
		{
			if(alltypes==null)
				alltypes = new HashSet();
			alltypes.addAll(attrset.getAllTypesSet());
		}
	}
	
	/**
	 *  Clone this object.
	 *  @return The clone.
	 */
	public Object clone() 
	{
		try
		{
			AttributeSet ret = (AttributeSet)super.clone();
			if(attributes!=null)
				ret.attributes = (Set)((HashSet)attributes).clone();
			if(alltypes!=null)
				ret.alltypes = (Set)((HashSet)alltypes).clone();
			return ret;
		}
		catch(CloneNotSupportedException ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
