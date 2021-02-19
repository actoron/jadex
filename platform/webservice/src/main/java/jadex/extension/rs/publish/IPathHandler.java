package jadex.extension.rs.publish;

import java.util.Map;

import jadex.commons.Tuple2;

/**
 * 
 */
public interface IPathHandler extends IRequestHandler
{
	/**
	 *  Adds a new subhandler.
	 *  
	 *  @param vhost Virtual host specification.
	 *  @param path Path being handled.
	 *  @param subhandler The subhandler.
	 */
	public void addSubhandler(String vhost, String path, IRequestHandler subhandler);
	
	/**
	 *  Remove a subhandler.
	 *  @param vhost Virtual host specification.
	 *  @param path Path being handled.
	 */
	public void removeSubhandler(String vhost, String path);
	
	/**
	 *  Tests if a handler for the exact URI is currently published.
	 * 
	 *  @param vhost Virtual host specification.
	 *  @param path Path being handled.
	 *  @return True, if a handler was found.
	 */
	public boolean containsSubhandlerForExactUri(String vhost, String path);
	
	/**
	 *  Get the subhandlers. 
	 *  @return The subhandlers
	 */
	public Map<Tuple2<String, String>, Tuple2<String, IRequestHandler>> getSubhandlers();
}
