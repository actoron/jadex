package jadex.tools.chat;


import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIDefaults;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.SGUI;

/**
 *  Struct to hold the user state in the chat GUI.
 */
public class ChatUser
{
	//-------- constants --------
	
	/** The icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"overlay_away",		SGUI.makeIcon(ChatPanel.class, "images/overlay_away.png"),
		"overlay_typing",	SGUI.makeIcon(ChatPanel.class, "images/overlay_typing.png"),
		"overlay_sending",	SGUI.makeIcon(ChatPanel.class, "images/overlay_sending.png"),
		"default_avatar",	SGUI.makeIcon(ChatPanel.class, "images/user_anon.png")
	});
	
	//-------- attributes --------
	
	/** The chat user. */
	protected IComponentIdentifier	cid;
	
//	/** The chat service (if already found). */
//	protected IChatService	chat;
	
	/** The typing flag. */
	protected boolean	typing;
	
	/** The away flag. */
	protected boolean	away;
	
	/** The open message ids. */
	protected Set<Integer>	messages;
	
	/** The cached nickname. */
	protected String	nick;
	
	/** The avatar image. */
	protected Icon	avatar;
	
	/** The time of the last update for checking when user becomes offline. */
	protected long	lastupdate;
	
	//-------- constructors --------
	
	/**
	 *  Create a new chat user object.
	 */
	public ChatUser(IComponentIdentifier cid)
	{
		if(cid==null)
		{
			throw new NullPointerException();
		}
		this.cid	= cid;
		this.nick = "unknown";
		this.messages	= new HashSet<Integer>();
		this.lastupdate	= System.currentTimeMillis();
	}

	//-------- methods --------
	
	/**
	 *  Get an icon for the user.
	 */
	public Icon	getIcon()
	{
		Icon	ret	= avatar!=null ? avatar : icons.getIcon("default_avatar");
		
		if(System.currentTimeMillis()-lastupdate>15000)	// offline after 15 seconds.
		{
			BufferedImage image = new BufferedImage(ret.getIconWidth(), ret.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
	        ret.paintIcon(null, image.getGraphics(), 0, 0);
			ret	= new ImageIcon(GrayFilter.createDisabledImage(image));
		}
		
		List<Icon>	ics	= new ArrayList<Icon>();
		ics.add(ret);
		if(away)
		{
			ics.add(icons.getIcon("overlay_away"));
		}
		if(typing)
		{
			ics.add(icons.getIcon("overlay_typing"));
		}
		if(!messages.isEmpty())
		{
			ics.add(icons.getIcon("overlay_sending"));
		}
		
		if(icons.size()>1)
		{
			ret	= new CombiIcon(ics.toArray(new Icon[ics.size()]));
		}
		
		return ret;
	}
	
	/**
	 *  Set the typing state.
	 */
	public void	setTyping(boolean typing)
	{
		this.typing	= typing;
		this.lastupdate	= System.currentTimeMillis();
	}
	
	/**
	 *  Get the typing state.
	 */
	public boolean isTyping()
	{
		return typing;
	}
	
	/**
	 *  Set the away state.
	 */
	public void	setAway(boolean away)
	{
		this.away	= away;
		this.lastupdate	= System.currentTimeMillis();
	}
	
	/**
	 *  Get the away state.
	 */
	public boolean isAway()
	{
		return away;
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
		this.lastupdate	= System.currentTimeMillis();
	}
	
	/**
	 *  Test if nickname is unknown.
	 */
	public boolean isNickUnknown()
	{
		return "unknown".equals(nick);
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
		this.lastupdate	= System.currentTimeMillis();
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
		this.messages.add(Integer.valueOf(id));
		this.lastupdate	= System.currentTimeMillis();
	}
	
	/**
	 *  Remove a message when sending is finished.
	 */
	public void	removeMessage(int id)
	{
		this.messages.remove(Integer.valueOf(id));
		this.lastupdate	= System.currentTimeMillis();
	}
	
	/**
	 *  Called when the user has been detected offline or online
	 *  @return True, when to much offline detections appeared and the user should be removed.
	 */
	public boolean	setOnline(Boolean online)
	{
		if(online!=null && online.booleanValue())
		{
			lastupdate	= System.currentTimeMillis();	// User is known to be online.
		}
		else if(online!=null)
		{
			lastupdate	= 0;	// User is known to be offline.			
		}
		
		return System.currentTimeMillis()-lastupdate > 45000;	// Offline when no update for 45 seconds.
	}
}
