package jadex.tools.chat;


import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.chat.IChatService;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.SGUI;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIDefaults;

/**
 *  Struct to hold the user state in the chat GUI.
 */
public class ChatUser
{
	//-------- constants --------
	
	/** The icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"overlay_typing",	SGUI.makeIcon(ChatPanel.class, "images/overlay_typing.png"),
		"overlay_sending",	SGUI.makeIcon(ChatPanel.class, "images/overlay_sending.png"),
		"default_avatar",	SGUI.makeIcon(ChatPanel.class, "images/user_anon.png")
	});
	
	//-------- attributes --------
	
	/** The chat user. */
	protected IComponentIdentifier	cid;
	
	/** The chat service (if already found). */
	protected IChatService	chat;
	
	/** The typing flag. */
	protected boolean	typing;
	
	/** The open message ids. */
	protected Set<Integer>	messages;
	
	/** The cached nickname. */
	protected String	nick;
	
	/** The avatar image. */
	protected Icon	avatar;
	
	/** The offline detection counter. */
	protected int	offline;
	
	//-------- constructors --------
	
	/**
	 *  Create a new chat user object.
	 */
	public ChatUser(IComponentIdentifier cid)
	{
		this.cid	= cid;
		this.nick = "unknown";
		this.messages	= new HashSet<Integer>();
	}

	//-------- methods --------
	
	/**
	 *  Get an icon for the user.
	 */
	public Icon	getIcon()
	{
		Icon	ret	= avatar!=null ? avatar : icons.getIcon("default_avatar");
		
		if(offline>0)
		{
			BufferedImage image = new BufferedImage(ret.getIconWidth(), ret.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
	        ret.paintIcon(null, image.getGraphics(), 0, 0);
			ret	= new ImageIcon(GrayFilter.createDisabledImage(image));
		}
		
		if(typing && !messages.isEmpty())
		{
			ret	= new CombiIcon(new Icon[]{ret, icons.getIcon("overlay_typing"), icons.getIcon("overlay_sending")});
		}
		else if(typing)
		{
			ret	= new CombiIcon(new Icon[]{ret, icons.getIcon("overlay_typing")});
		}
		else if(!messages.isEmpty())
		{
			ret	= new CombiIcon(new Icon[]{ret, icons.getIcon("overlay_sending")});
		}
		
		return ret;
	}
	
	/**
	 *  Set the typing state.
	 */
	public void	setTyping(boolean typing)
	{
		this.typing	= typing;
	}
	
	/**
	 *  Get the typing state.
	 */
	public boolean isTyping()
	{
		return typing;
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
	 *  Test if nickname is unknown.
	 */
	public boolean isNickUnknown()
	{
		return "unknown".equals(nick);
	}
	
	/**
	 *  Get the chat.
	 *  @return the chat.
	 */
	public IChatService getChatService()
	{
		return chat;
	}

	/**
	 *  renamed to not be bean conform
	 *  Set the chat.
	 *  @param chat The chat to set.
	 */
	public void setChatService(IChatService chat)
	{
		this.chat = chat;
	}

	/**
	 *  Test if image is unknown.
	 */
	public boolean isAvatarUnknown()
	{
		return avatar==null;
	}
	
	/**
	 *  Set the image.
	 *  @param image The image to set.
	 */
	public void setAvatar(Icon avatar)
	{
		this.avatar = avatar;
	}

	/**
	 *  Get the cid.
	 *  @return the cid.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return cid;
	}
	
	/**
	 *  Add a message that is currently being sent.
	 */
	public void	addMessage(int id)
	{
		this.messages.add(new Integer(id));
	}
	
	/**
	 *  Remove a message when sending is finished.
	 */
	public void	removeMessage(int id)
	{
		this.messages.remove(new Integer(id));
	}
	
	/**
	 *  Called when the user has been detected offline or online
	 *  @return True, when to much offline detections appeared and the user should be removed.
	 */
	public boolean	setOffline(boolean offline)
	{
		if(!offline)
		{
			this.offline	= 0;
		}
		else
		{
			this.offline++;
		}
		return this.offline>=3;
	}
}
