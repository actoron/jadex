package jadex.extension.agr;

import java.util.ArrayList;
import java.util.List;

import jadex.commons.SReflect;

/**
 * An AGR space description.
 */
public class MAGRSpaceType
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;

	/** The class name. */
	protected String classname;
	
	/** The grouptypes. */
	protected List	grouptypes;

	//-------- methods --------
		
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the classname.
	 *  @return the classname.
	 */
	public String getClassName()
	{
		return classname;
	}

	/**
	 *  Set the classname.
	 *  @param classname The classname to set.
	 */
	public void setClassName(String classname)
	{
		this.classname = classname;
	}
	
	/**
	 *  Get the group types of this space type.
	 *  @return An array of group types (if any).
	 */
	public MGroupType[] getMGroupTypes()
	{
		return grouptypes==null ? null :
			(MGroupType[])grouptypes.toArray(new MGroupType[grouptypes.size()]);
	}
	
	

	/**
	 *  Add a group type to this space type.
	 *  @param grouptype	The group type to add. 
	 */
	public void addMGroupType(MGroupType grouptype)
	{
		if(grouptypes==null)
			grouptypes	= new ArrayList();
		grouptypes.add(grouptype);
	}

	/**
	 *  Remove a group type from this space type.
	 *  @param grouptype	The group type to remove. 
	 */
	public void removeMGroupType(MGroupType grouptype)
	{
		if(grouptypes!=null)
		{
			grouptypes.remove(grouptype);
			if(grouptypes.isEmpty())
				grouptypes	= null;
		}
	}
	
	/**
	 *  Get a group type by name.
	 */
	public MGroupType getGroupType(String name)
	{
		MGroupType ret = null;
		for(int i=0; ret==null && grouptypes!=null && i<grouptypes.size(); i++)
		{
			MGroupType	gt	= (MGroupType)grouptypes.get(i);
			if(gt.getName().equals(name))
				ret = gt;
		}
		return ret;
	}

	/**
	 *  Get a string representation of this AGR space type.
	 *  @return A string representation of this AGR space type.
	 */
	public String	toString()
	{
		StringBuffer	sbuf	= new StringBuffer();
		sbuf.append(SReflect.getInnerClassName(getClass()));
		sbuf.append("(name=");
		sbuf.append(getName());
		if(grouptypes!=null)
		{
			sbuf.append(", grouptypes=");
			sbuf.append(grouptypes);
		}
		sbuf.append(")");
		return sbuf.toString();
	}
}
