package jadex.adapter.base.appdescriptor;

import jadex.bridge.IApplicationContext;
import jadex.bridge.ISpace;

import java.util.List;

/**
 *  Space instance representation.
 */
public abstract class MSpaceInstance
{
	//-------- attributes --------

	/** The name. */
	protected String name;

	/** The space type name. */
	// Todo: resolve to object while loading XML?
	protected String type;
	
	//-------- constructors --------

	/**
	 *  Create a new space type.
	 */
	public MSpaceInstance()
	{
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
	 *  @param apptype The application type this element is used in.
	 *  @return The structure type.
	 */
	public MSpaceType getType(MApplicationType apptype)
	{
		MSpaceType ret = null;
		List structuretypes = apptype.getMSpaceTypes();
		for(int i=0; ret==null && i<structuretypes.size(); i++)
		{
			MSpaceType st = (MSpaceType)structuretypes.get(i);
			if(st.getName().equals(getTypeName()))
				ret = st;
		}
		return ret;
	}
	
	/**
	 *  Create a space.
	 */
	public abstract ISpace createSpace(IApplicationContext app) throws Exception;
	
	/**
	 *  Initialize a space.
	 *  Do all initialization that requires the space already being registered in the context.
	 *  Override, if needed. 
	 */
	public void	initSpace(ISpace space, IApplicationContext app) throws Exception
	{
		// empty default implementation. 
	}
}