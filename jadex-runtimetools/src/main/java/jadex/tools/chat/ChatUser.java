package jadex.tools.chat;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.chat.IChatService;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *  Struct to hold the user state.
 */
public class ChatUser
{
	//-------- constants --------
	
	/** The icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"yellow",	SGUI.makeIcon(ChatPanel.class, "images/user_yellow.png"),
		"yellow_typing",	SGUI.makeIcon(ChatPanel.class, "images/user_yellow_typing.png"),
		"red",		SGUI.makeIcon(ChatPanel.class, "images/user_red.png"),
		IChatService.STATE_IDLE,	SGUI.makeIcon(ChatPanel.class, "images/user_green.png"),
		IChatService.STATE_TYPING,	SGUI.makeIcon(ChatPanel.class, "images/user_green_typing.png"),
		IChatService.STATE_DEAD, SGUI.makeIcon(ChatPanel.class, "images/user_gray.png")
	});
	
	//-------- attributes --------
	
	/** The chat user. */
	protected IComponentIdentifier	cid;
	
	/** The chat service (if already found). */
	protected IChatService	chat;
	
	/** The user state (idle, typing, ...). */
	protected String	state;
	
	/** The receiving state (id). */
	protected int receiving;
	
	/** The cached nickname. */
	protected String nick;
	
	//-------- constructors --------
	
	/**
	 *  Create a new chat user.
	 */
	public ChatUser(IComponentIdentifier cid)
	{
		this.cid	= cid;
		this.state	= IChatService.STATE_IDLE;
		this.receiving	= -1;
		this.nick = "unknown";
	}

	/**
	 *  Create a new chat user.
	 */
	public ChatUser(IChatService chat)
	{
		this(((IService)chat).getServiceIdentifier().getProviderId());
		this.chat	= chat;
		
//		if(chat!=null)
//		{
			chat.getNickName().addResultListener(new IResultListener<String>()
			{
				public void resultAvailable(String result)
				{
					nick = result;
				}
				
				public void exceptionOccurred(Exception exception)
				{
				}
			});
//		}
	}
	
	//-------- methods --------
	
	/**
	 *  Get an icon for the user.
	 */
	public Icon	getIcon()
	{
		Icon	ret;
		if(receiving==-1 || IChatService.STATE_DEAD.equals(state))
		{
			ret	= icons.getIcon(state);
		}
		else if(receiving==-2)
		{
			ret	= icons.getIcon("red");
		}
		else if(IChatService.STATE_TYPING.equals(state))
		{
			ret	= icons.getIcon("yellow_typing");
		}
		else //if(IChatService.STATE_IDLE.equals(state))
		{
			ret	= icons.getIcon("yellow");
		}
		return ret;
	}
	
	/**
	 *  Set the state
	 */
	public void	setState(String state)
	{
		this.state	= state;
		if(receiving==-2)
			receiving	= -1;
	}
	
	/**
	 *  Set the state
	 */
	public void	setReceiving(int id, boolean rec)
	{
		if(IChatService.STATE_DEAD.equals(state))
		{
			state	= IChatService.STATE_IDLE;
		}
		
		if(rec)
		{
			this.receiving	= id;
		}
		else if(id==receiving)
		{
			this.receiving	= -1;
		}
	}

	/**
	 *  Get the state.
	 */
	public String getState()
	{
		return state;
	}

	/**
	 *  Get the nick.
	 *  @return the nick.
	 */
	public String getNick()
	{
		return nick;
	}
	
	/**
	 *  Set the nick.
	 *  @param nick The nick to set.
	 */
	public void setNick(String nick)
	{
		this.nick = nick;
	}

	/**
	 *  Get the cid.
	 *  @return the cid.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return cid;
	}
}
