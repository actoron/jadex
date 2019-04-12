package jadex.tools.web.jcc;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Marker interface for JCC plugin services.
 */
@Service
public interface IJCCPluginService
{
	/**
	 *  Get the plugin component (html).
	 *  @return The plugin code.
	 */
	public IFuture<String> getPluginComponent();
	
	/**
	 *  Get the plugin name.
	 *  @return The plugin name.
	 */
	public IFuture<String> getPluginName();
	
}
