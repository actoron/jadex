package jadex.xwiki;

import java.io.File;

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
	 *  Get all builds sorted by date (newest first).
	 *  @return NightlyBuild objects.
	 */
	public NightlyBuild[]	getAllBuilds(String dir)
	{
		return plugin.getAllBuilds(getXWikiContext(), new File(dir));
	}
}
