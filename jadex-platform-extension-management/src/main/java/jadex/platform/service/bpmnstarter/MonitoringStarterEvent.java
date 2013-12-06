package jadex.platform.service.bpmnstarter;

import jadex.bridge.IComponentIdentifier;

/**
 * 
 */
public class MonitoringStarterEvent
{
	public static final String ADDED = "added";
	
	public static final String REMOVED = "removed";
	
	public static final String INSTANCE_CREATED = "instance_created";
	
	public static final String INSTANCE_TERMINATED = "instance_terminated";
	
	/** The event type. */
	protected String type;
	
	/** The component id. */
	protected IComponentIdentifier cid;
	
	/** The content. */
	protected Object content;

	/**
	 *  Create a new MonitoringStarterEvent. 
	 */
	public MonitoringStarterEvent()
	{
	}
	
	/**
	 *  Create a new MonitoringStarterEvent. 
	 */
	public MonitoringStarterEvent(String type, IComponentIdentifier cid, Object content)
	{
		this.type = type;
		this.cid = cid;
		this.content = content;
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return type;
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
	 *  Get the componentIdentifier.
	 *  @return The componentIdentifier.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return cid;
	}

	/**
	 *  Set the componentIdentifier.
	 *  @param componentIdentifier The componentIdentifier to set.
	 */
	public void setComponentIdentifier(IComponentIdentifier componentIdentifier)
	{
		this.cid = componentIdentifier;
	}

	/**
	 *  Get the content.
	 *  @return The content.
	 */
	public Object getContent()
	{
		return content;
	}

	/**
	 *  Set the content.
	 *  @param content The content to set.
	 */
	public void setContent(Object content)
	{
		this.content = content;
	}

	/** 
	 * 
	 */
	public String toString()
	{
		return "MonitoringStarterEvent(type=" + type + ", cid=" + cid
				+ ", content=" + content + "]";
	}
}
