package jadex.platform.service.processengine;

import jadex.bridge.IComponentIdentifier;

/**
 * 
 */
public class ProcessEngineEvent
{
	public static final String PROCESSMODEL_ADDED = "processmodel_added";
	
	public static final String PROCESSMODEL_REMOVED = "processmodel_removed";
	
	public static final String INSTANCE_CREATED = "instance_created";
	
	public static final String INSTANCE_TERMINATED = "instance_terminated";
	
	public static final String INSTANCE_RESULT_RECEIVED = "instance_result_received";

	
	/** The event type. */
	protected String type;
	
	/** The component id. */
	protected IComponentIdentifier cid;
	
	/** The content. */
	protected Object content;

	/**
	 *  Create a new MonitoringStarterEvent. 
	 */
	public ProcessEngineEvent()
	{
	}
	
	/**
	 *  Create a new MonitoringStarterEvent. 
	 */
	public ProcessEngineEvent(String type, IComponentIdentifier cid, Object content)
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
