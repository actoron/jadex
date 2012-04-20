package jadex.bridge.service.types.chat;

import jadex.bridge.IComponentIdentifier;

/**
 *  Information about some event that happened in the chat component.
 */
public class ChatEvent
{
	//-------- constants --------
	
	/** Event type for a chat user status change (value is user state). */
	public static final String	TYPE_STATECHANGE	= "statechange";

	/** Event type for a received message (value is message text). */
	public static final String	TYPE_MESSAGE	= "message";
	
	/** Event type for a received file (value is file info, nick only available for initial file events). */
	public static final String	TYPE_FILE	= "file";
	
	//-------- attributes --------
	
	/** The event type. */
	protected String	type;
	
	/** The nick name. */
	protected String	nick;
	
	/** The user cid. */
	protected IComponentIdentifier	cid;
	
	/** The event value (depends on type). */
	protected Object	value;

	//-------- constructors --------
	
	/**
	 *  Create a new chat event.
	 */
	public ChatEvent()
	{
		// Bean constructor.
	}
	
	/**
	 *  Create a new chat event.
	 */
	public ChatEvent(String type, String nick, IComponentIdentifier cid, Object value)
	{
		this.type	= type;
		this.nick	= nick;
		this.cid	= cid;
		this.value	= value;
	}
	
	//-------- accessors --------

	/**
	 *  Get the type.
	 */
	public String	getType()
	{
		return type;
	}

	/**
	 *  Get the nick name.
	 */
	public String	getNick()
	{
		return nick;
	}

	/**
	 *  Get the cid.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return cid;
	}

	/**
	 *  Get the value.
	 */
	public Object	getValue()
	{
		return value;
	}


	/**
	 *  Set the type.
	 */
	public void	setType(String type)
	{
		this.type	= type;
	}

	/**
	 *  Set the nick name.
	 */
	public void	setNick(String nick)
	{
		this.nick	= nick;
	}

	/**
	 *  Set the cid.
	 */
	public void	setComponentIdentifier(IComponentIdentifier cid)
	{
		this.cid	= cid;
	}

	/**
	 *  Set the value.
	 */
	public void	setValue(Object value)
	{
		this.value	= value;
	}

}
