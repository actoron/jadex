package jadex.adapter.base.appdescriptor;

import java.util.List;

/**
 *  Structuring type representation.
 */
public class Structuring
{
	//-------- attributes --------

	/** The name. */
	protected String name;

	/** The structuring type (e.g. continuous, grid). */
	protected String type;
	
	//-------- constructors --------

	/**
	 *  Create a new structuring type.
	 */
	public Structuring()
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
	public StructuringType getModel(ApplicationType apptype)
	{
		StructuringType ret = null;
		List structuretypes = apptype.getStructuringTypes();
		for(int i=0; ret==null && i<structuretypes.size(); i++)
		{
			StructuringType st = (StructuringType)structuretypes.get(i);
			if(st.getName().equals(getType()))
				ret = st;
		}
		return ret;
	}
	
}