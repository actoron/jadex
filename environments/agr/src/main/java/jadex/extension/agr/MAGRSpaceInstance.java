package jadex.extension.agr;

import java.util.ArrayList;
import java.util.List;

import jadex.application.IExtensionInfo;
import jadex.application.IExtensionInstance;
import jadex.bridge.IExternalAccess;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  An instance of an AGR space. 
 */
public class MAGRSpaceInstance	implements IExtensionInfo
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;

	/** The space type name. */
	protected String type;
	
	/** The space type (resolved during loading). */
	protected MAGRSpaceType spacetype;
	
	/** The groups. */
	protected List groups;
	
	//-------- IExtensionInfo interface --------

	/**
	 *  Instantiate the extension for a specific component instance.
	 *  @param access	The external access of the component.
	 *  @param fetcher	The value fetcher of the component to be used for evaluating dynamic expressions. 
	 *  @return The extension instance object.
	 */
	public IFuture createInstance(IExternalAccess access, IValueFetcher fetcher)
	{
		return new Future<IExtensionInstance>(new AGRSpace(access, this));
	}
	
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
	 *  Get the type name.
	 *  @return The type name. 
	 */
	public String getTypeName()
	{
		return this.type;
	}

	/**
	 *  Set the type name.
	 *  @param type The type name to set.
	 */
	public void setTypeName(String type)
	{
		this.type = type;
	}
	
	/**
	 *  Get the type of this element.
	 *  @return The structure type.
	 */
	public MAGRSpaceType getType()
	{
		return spacetype;
	}

	/**
	 *  Set the type of this element.
	 */
	public void	setType(MAGRSpaceType spacetype)
	{
		this.spacetype	= spacetype;
	}
	
	/**
	 *  Get the groups of this space.
	 *  @return An array of groups (if any).
	 */
	public MGroupInstance[] getMGroupInstances()
	{
		return groups==null? null:
			(MGroupInstance[])groups.toArray(new MGroupInstance[groups.size()]);
	}

	/**
	 *  Add a group to this space.
	 *  @param group The group to add. 
	 */
	public void addMGroupInstance(MGroupInstance group)
	{
		if(groups==null)
			groups	= new ArrayList();
		groups.add(group);
	}
	
	/**
	 *  Get a group per name.
	 *  @param name The name.
	 *  @return The group.
	 */
	public MGroupInstance getMGroupInstance(String name)
	{
		MGroupInstance	ret	= null;
		for(int i=0; ret==null && i<groups.size(); i++)
		{
			MGroupInstance	gi	= (MGroupInstance)groups.get(i);
			if(gi.getName().equals(name))
				ret	= gi;
		}
		return ret;
	}
	
	/**
	 *  Get a string representation of this AGR space instance.
	 *  @return A string representation of this AGR space instance.
	 */
	public String	toString()
	{
		StringBuffer	sbuf	= new StringBuffer();
		sbuf.append(SReflect.getInnerClassName(getClass()));
		sbuf.append("(name=");
		sbuf.append(getName());
		if(groups!=null)
		{
			sbuf.append(", groups=");
			sbuf.append(groups);
		}
		sbuf.append(")");
		return sbuf.toString();
	}

//	/**
//	 *  Get the implementation class of the space.
//	 */
//	public Class getClazz()
//	{
//		return AGRSpace.class;
//	}

}
