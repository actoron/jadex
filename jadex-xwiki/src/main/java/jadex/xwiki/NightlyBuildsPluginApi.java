package jadex.xwiki;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;

/**
 *  Public API (i.e. velocity-accessible methods) of nightly builds plugin.
 */
public class NightlyBuildsPluginApi extends Api
{
	//-------- attributes --------
	
	/** The plugin. */
	protected NightlyBuildsPlugin	plugin;
	
	//-------- constructors --------
	
	/**
	 *  Create a new plugin API.
	 */
	public NightlyBuildsPluginApi(NightlyBuildsPlugin plugin, XWikiContext context)
	{
		super(context);
		this.plugin	= plugin;
	}
	
	//-------- methods --------
	
	/**
	 *  Get information for the latest (i.e. newest build).
	 *  @return The information of the latest build.
	 */
	protected NightlyBuild	getLatestBuild()
	{
		return plugin.getLatestBuild(getXWikiContext());
	}
	
	/**
	 *  Get all builds sorted by date (newest first).
	 *  @return NightlyBuild objects.
	 */
	public NightlyBuild[]	getAllBuilds()
	{
		return plugin.getAllBuilds(getXWikiContext());
	}
}
