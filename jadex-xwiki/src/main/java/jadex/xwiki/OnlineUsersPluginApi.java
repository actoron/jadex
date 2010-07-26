package jadex.xwiki;

import java.util.List;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;

/**
 *  Public API (i.e. velocity-accessible methods) of online users plugin.
 */
public class OnlineUsersPluginApi extends Api
{
	//-------- attributes --------
	
	/** The plugin. */
	protected OnlineUsersPlugin	plugin;
	
	//-------- constructors --------
	
	/**
	 *  Create a new plugin API.
	 */
	public OnlineUsersPluginApi(OnlineUsersPlugin plugin, XWikiContext context)
	{
		super(context);
		this.plugin	= plugin;
	}
	
	//-------- methods --------
	
	/**
	 *  Get names of currently online users sorted by last access.
	 *  @param max	The maximum number (-1 for no max.).
	 */
	public List	getOnlineUsers(int max)
	{
		return plugin.getOnlineUsers(max, getXWikiContext());
	}
	
	/**
	 *  Get number of currently online guests.
	 */
	public int	getGuestCount()
	{
		return plugin.getGuestCount(getXWikiContext());
	}
}
