package jadex.adapter.base.agr;

/**
 *  An AGR group hold information about agent instances
 *  and their positions (i.e. role instances).
 */
public class Group
{
	//-------- attributes --------
	
	/** The group type. */
	protected MGroupType	type;
	
	/** The group instance model (if any). */
	protected MGroupInstance	instance;
	
	//-------- constructors --------
	
	/**
	 *  Create a new group from a type.
	 *  @param type	The group type.
	 */
	public Group(MGroupType type)
	{
		this(type, null);
	}

	/**
	 *  Create a new group from an instance model.
	 *  @param type	The group type.
	 *  @param instance	The instance model.
	 */
	public Group(MGroupType type, MGroupInstance instance)
	{
		this.type	= type;
		this.instance	= instance;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the instance model (if any).
	 *  @return The instance model or null.
	 */
	public MGroupInstance	getMGroupInstance()
	{
		return instance;
	}
}
