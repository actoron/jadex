package jadex.tools.web.jcc;

import javax.ws.rs.core.Response;

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
	
	// todo: Response is used to set the mimetype :-(
	// should set in gateway near rest call
	/**
	 *  Load a string-based resource (style or js).
	 *  @param filename The filename.
	 *  @return The text from the file.
	 */
	//public IFuture<String> loadResource(String filename);
	public IFuture<Response> loadResource(String filename);
}
