package jadex.adapter.base.appdescriptor;

import java.util.List;

/**
 *  Space instance representation.
 */
public class MSpaceInstance
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
	 *  Get the type.
	 *  @return The type. 
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}
	
	/**
	 *  Get the model of this element.
	 *  @param apptype The application type this element is used in.
	 *  @return The structure type.
	 */
	public MSpaceType getModel(MApplicationType apptype)
	{
		MSpaceType ret = null;
		List structuretypes = apptype.getMSpaceTypes();
		for(int i=0; ret==null && i<structuretypes.size(); i++)
		{
			MSpaceType st = (MSpaceType)structuretypes.get(i);
			if(st.getName().equals(getType()))
				ret = st;
		}
		return ret;
	}
	
}