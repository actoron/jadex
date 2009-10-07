package jadex.adapter.base.envsupport.environment;

/**
 *  Relationship specification between an agent type and an avatar type.
 */
public class AvatarMapping
{
	//-------- attributes --------

	/** The agent type name. */
	protected String agenttype;
	
	/** The object type. */
	protected String objecttype;

	/** The flag for creating the avatar when an agent is created. */
	protected boolean createavatar;
	
	/** The flag for creating the agent when an avatar is created. */
	protected boolean createagent;
	
	/** The flag for deleting the avatar when agent is killed. */
	protected boolean killavatar;
	
	/** The flag for deleting the agent when avatar is killed. */
	protected boolean killagent;
	
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
	public AvatarMapping(String agenttype, String objecttype)
	{
		this(agenttype, objecttype, true, false, true, false);
	}
	
	/**
	 *  Create a new avatar mapping. 
	 */
	public AvatarMapping(String agenttype, String objecttype, boolean createavatar, 
		boolean createagent, boolean killavatar, boolean killagent)
	{
		this.agenttype = agenttype;
		this.objecttype = objecttype;
		this.createavatar = createavatar;
		this.createagent = createagent;
		this.killavatar = killavatar;
		this.killagent = killagent;
	}
	
	//-------- methods --------

	/**
	 *  Get the agenttype.
	 *  @return The agenttype.
	 */
	public String getAgentType()
	{
		return this.agenttype;
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
	 *  Should agent be created.
	 *  @return True, for agent being created.
	 */
	public boolean isCreateAgent()
	{
		return this.createagent;
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
	 *  Should agent being killed.
	 *  @return True, for agent being created.
	 */
	public boolean isKillAgent()
	{
		return this.killagent;
	}

	/**
	 * @param agenttype the agenttype to set
	 */
	public void setAgentType(String agenttype)
	{
		this.agenttype = agenttype;
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
	 * @param createagent the createagent to set
	 */
	public void setCreateAgent(boolean createagent)
	{
		this.createagent = createagent;
	}

	/**
	 * @param killavatar the killavatar to set
	 */
	public void setKillAvatar(boolean killavatar)
	{
		this.killavatar = killavatar;
	}

	/**
	 * @param killagent the killagent to set
	 */
	public void setKillAgent(boolean killagent)
	{
		this.killagent = killagent;
	}
}
