package jadex.extension.envsupport.environment;

import jadex.javaparser.IParsedExpression;

/**
 *  Relationship specification between an component type and an avatar type.
 */
public class AvatarMapping
{
	//-------- attributes --------

	/** The component type name. */
	protected String componenttype;
	
	/** The object type. */
	protected String objecttype;

	/** The flag for creating the avatar when an component is created. */
	protected boolean createavatar;
	
	/** The flag for creating the component when an avatar is created. */
	protected boolean createcomponent;
	
	/** The flag for deleting the avatar when component is killed. */
	protected boolean killavatar;
	
	/** The flag for deleting the component when avatar is killed. */
	protected boolean killcomponent;
	
	/** The name of the component to be created for an avatar. */
	protected IParsedExpression	componentname;
	
	//-------- constructors --------
	
	/**
	 *  Create a new avatar mapping. 
	 */
	public AvatarMapping()
	{
		// bean constructor
		this(null, null);
	}
	
	/**
	 *  Create a new avatar mapping. 
	 */
	public AvatarMapping(String componenttype, String objecttype)
	{
		this(componenttype, objecttype, true, false, true, false);
	}
	
	/**
	 *  Create a new avatar mapping. 
	 */
	public AvatarMapping(String componenttype, String objecttype, boolean createavatar, 
		boolean createcomponent, boolean killavatar, boolean killcomponent)
	{
		this.componenttype = componenttype;
		this.objecttype = objecttype;
		this.createavatar = createavatar;
		this.createcomponent = createcomponent;
		this.killavatar = killavatar;
		this.killcomponent = killcomponent;
	}
	
	//-------- methods --------

	/**
	 *  Get the componenttype.
	 *  @return The componenttype.
	 */
	public String getComponentType()
	{
		return this.componenttype;
	}

	/**
	 *  Get the objecttype.
	 *  @return The objecttype.
	 */
	public String getObjectType()
	{
		return this.objecttype;
	}

	/**
	 *  Should avatar be created.
	 *  @return True, for avatar being created.
	 */
	public boolean isCreateAvatar()
	{
		return this.createavatar;
	}

	/**
	 *  Should component be created.
	 *  @return True, for component being created.
	 */
	public boolean isCreateComponent()
	{
		return this.createcomponent;
	}

	/**
	 *  Should avatar be killed.
	 *  @return True, for avatar being killed.
	 */
	public boolean isKillAvatar()
	{
		return this.killavatar;
	}

	/**
	 *  Should component being killed.
	 *  @return True, for component being created.
	 */
	public boolean isKillComponent()
	{
		return this.killcomponent;
	}

	/**
	 *  Get the component name expression, if any.
	 *  @return The component name expression or null.
	 */
	public IParsedExpression	getComponentName()
	{
		return this.componentname;
	}

	/**
	 * @param componenttype the componenttype to set
	 */
	public void setComponentType(String componenttype)
	{
		this.componenttype = componenttype;
	}

	/**
	 * @param object the objecttype to set
	 */
	public void setObjectType(String object)
	{
		this.objecttype = object;
	}

	/**
	 * @param createavatar the createavatar to set
	 */
	public void setCreateAvatar(boolean createavatar)
	{
		this.createavatar = createavatar;
	}

	/**
	 * @param createcomponent the createcomponent to set
	 */
	public void setCreateComponent(boolean createcomponent)
	{
		this.createcomponent = createcomponent;
	}

	/**
	 * @param killavatar the killavatar to set
	 */
	public void setKillAvatar(boolean killavatar)
	{
		this.killavatar = killavatar;
	}

	/**
	 * @param killcomponent the killcomponent to set
	 */
	public void setKillComponent(boolean killcomponent)
	{
		this.killcomponent = killcomponent;
	}

	/**
	 *  Set the component name expression.
	 *  @param name	The component name expression.
	 */
	public void setComponentName(IParsedExpression componentname)
	{
		this.componentname = componentname;
	}
}
