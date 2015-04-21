package jadex.bridge;

import java.util.Map;

/**
 *  Access to internal data structures not meant for end user programmers.
 */
public interface INonUserAccess
{
	/**
	 *  Get the exception, if any.
	 *  
	 *  @return The failure reason for use during cleanup, if any.
	 */
	public Exception	getException();
	
	/**
	 *  Get the shared platform data.
	 *  
	 *  @return The objects shared by all components of the same platform (registry etc.). See starter for available data.
	 */
	public Map<String, Object>	getPlatformData();
}
