package jadex.xwiki;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.plugin.XWikiPluginInterface;

/**
 *  XWiki plugin for keeping a list of currently connected users.
 */
public class OnlineUsersPlugin implements XWikiPluginInterface
{
	//-------- constants --------
	
	/** Name of XWiki guest user. */
	protected static final String XWIKI_GUEST = "XWiki.XWikiGuest";

	/** Key and identifier prefix for guest users. */
	protected static final String	USERID	= "OnlineUsersPlugin.User";
	
	//-------- attributes --------
	
	/** The guest counter. */
	protected int	guestcnt;
	
	/** The currently online users. */
	// Users are added in beginParsing() and removed in updateOnlineUsers() called from getOnlineUsers() and getGuestCount()
	protected List	users;

	/** The currently online users (name->time of last access). */
	protected Map	userdates;
	
	/** The currently online guests. */
	protected Set	guests;
	
	//-------- constructors --------
	
	/**
	 *  Create a plugin instance.
	 */
	public OnlineUsersPlugin(String name, String classname, XWikiContext context)
	{
//		System.out.println("OnlineUsersPlugin: "+name+", "+classname+", "+context);
		guestcnt	= 0;
		users	= new LinkedList();
		userdates	= new HashMap();
		guests	= new LinkedHashSet();
	}
	
	//-------- plugin methods --------
	
	/**
	 *  Remove outdated users.
	 */
	protected void	updateOnlineUsers(XWikiContext context)
	{
		// Remove users and guests, which have been idle for more than the session timeout.
		long	cutoff	= System.currentTimeMillis()
			- 1000*context.getRequest().getHttpServletRequest().getSession().getMaxInactiveInterval();
		synchronized(users)
		{
			// Remove users.
			while(users.size()>0 && cutoff>((Long)userdates.get(users.get(0))).longValue())
			{
//				System.out.println("removing: "+users.get(0));
				userdates.remove(users.get(0));
				users.remove(0);
			}
			
			// Remove guests.
			for(Iterator it=guests.iterator(); it.hasNext(); )
			{
				Object	guest	= it.next();
				if(cutoff>((Long)userdates.get(guest)).longValue())
				{
//					System.out.println("removing: "+guest);
					userdates.remove(guest);
					it.remove();
				}
				else
				{
					break;
				}
			}
		}
	}
	
	/**
	 *  Get the names of currently online users.
	 *  @param max	The maximum number (-1 for no max.).
	 */
	public List	getOnlineUsers(int max, XWikiContext context)
	{
		List	result	= new ArrayList();
		synchronized(users)
		{
			updateOnlineUsers(context);
			
			// Add users from end of list (i.e. newest first)
			for(int i=users.size()-1; i>=0 && (result.size()<max || max==-1); i--)
			{
				String	user	= (String)users.get(i);
				if(user.startsWith(context.getDatabase()+":"))
					result.add(user.substring(context.getDatabase().length()+1));
			}
		}
//		System.out.println("getOnlineUsers: "+result);
		return result;
	}
	
	/**
	 *  Get the number of current guest users.
	 */
	public int	getGuestCount(XWikiContext context)
	{
		int	result	= 0;
		synchronized(users)
		{
			updateOnlineUsers(context);

			result	= guests.size();
		}
//		System.out.println("getGuestCount: "+result);
		return result;
	}

	//-------- XWikiPluginInterface management --------
	
	public XWikiAttachment downloadAttachment(XWikiAttachment attachment, XWikiContext context)
	{
//		System.out.println("downloadAttachment: "+attachment+","+context);
		return attachment;
	}
	public void flushCache()
	{
//		System.out.println("flushCache");
	}
	public void flushCache(XWikiContext context)
	{
//		System.out.println("flushCache: "+context);
	}
	public String getClassName()
	{
//		System.out.println("getClassName");
		return getClass().getName();
	}
	public String getName()
	{
//		System.out.println("getName");
		return "onlineusers";
	}
	public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context)
	{
//		System.out.println("getPluginApi"+plugin+","+context);
		return new OnlineUsersPluginApi((OnlineUsersPlugin)plugin, context);
	}
	public void init(XWikiContext context) throws XWikiException
	{
//		System.out.println("init: "+context);
	}
	public void setClassName(String name)
	{
//		System.out.println("setClassName: "+name);
	}
	public void setName(String name)
	{
//		System.out.println("setName: "+name);
	}
	public void virtualInit(XWikiContext context)
	{
//		System.out.println("virtualInit: "+context);
	}

	//-------- XWikiPluginInterface page rendering --------
	
	/**
	 *  Called once for each document(?).
	 */
	public void beginParsing(XWikiContext context)
	{
//		System.out.println("beginParsing: "+context.getDoc().getFullName());
		HttpSession	session	= context.getRequest().getHttpServletRequest().getSession();
		String	contextuser	= context.getUser();
		String	sessionuser	= (String)session.getAttribute(USERID);
		
		synchronized(users)
		{
			if(XWIKI_GUEST.equals(contextuser))
			{
				if(sessionuser==null || !sessionuser.startsWith(USERID))
				{
					// Remove user, who has just logged out. 
					if(sessionuser!=null)
					{
						users.remove(sessionuser);	// Hack!!! linear complexity.
					}
					
					sessionuser	= USERID + ++guestcnt;
					session.setAttribute(USERID, sessionuser);
				}
				guests.add(sessionuser);
			}
			else
			{
				// Remove guest, when user logs in
				if(sessionuser!=null && sessionuser.startsWith(USERID))
				{
					guests.remove(sessionuser);
				}
				
				sessionuser	= context.getDatabase()+":"+contextuser;
				session.setAttribute(USERID, sessionuser);
				users.remove(sessionuser);	// Hack!!! linear complexity.
				users.add(sessionuser);				
			}
			
			userdates.put(sessionuser, new Long(System.currentTimeMillis()));
		}
//		System.out.println("user: "+sessionuser);
//		System.out.println("users: "+users);
//		System.out.println("guests: "+guests);
	}
	
	/**
	 *  Called once for each document(?).
	 */
	public String endParsing(String content, XWikiContext context)
	{
//		System.out.println("endParsing: "+context.getDoc().getFullName());
		return content;
	}

	public void beginRendering(XWikiContext context)
	{
//		System.out.println("beginRendering: "+context.getDoc().getFullName());
	}
	public void endRendering(XWikiContext context)
	{
//		System.out.println("endRendering: "+context.getDoc().getFullName());
	}

	public String commonTagsHandler(String line, XWikiContext context)
	{
//		System.out.println("commonTagsHandler: "+line+","+context);
		return line;
	}
	public String endRenderingHandler(String line, XWikiContext context)
	{
//		System.out.println("endRenderingHandler: "+line+","+context);
		return line;
	}
	public String insidePREHandler(String line, XWikiContext context)
	{
//		System.out.println("insidePREHandler: "+line+","+context);
		return line;
	}
	public String outsidePREHandler(String line, XWikiContext context)
	{
//		System.out.println("outsidePREHandler: "+line+","+context);
		return line;
	}
	public String startRenderingHandler(String line, XWikiContext context)
	{
//		System.out.println("startRenderingHandler: "+line+","+context);
		return line;
	}
}
