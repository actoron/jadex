package jadex.base.relay;

import java.io.IOException;
import java.io.OutputStream;

import jadex.transformation.jsonserializer.JsonTraverser;

/**
 *  Facade to the relay handler with operations to feed the dashboard, i.e. status page.
 */
public class RelayDashboard
{
	//-------- attributes --------
	
	/** The relay handler. */
	protected RelayHandler	handler;
	
	//-------- constructors --------
	
	/**
	 *  Create a dashboard.
	 */
	public RelayDashboard(RelayHandler handler)
	{
		this.handler	= handler;
	}
	
	//-------- methods --------
	
	/**
	 *  Write the currently connected platforms (including peer-connected platforms)
	 *  as JSON to the given output stream.
	 *  @param out	The output stream.
	 */
	public void	writeAllPlatforms(OutputStream out)	throws IOException
	{
		byte[]	val	= JsonTraverser.objectToByteArray(handler.getCurrentPlatforms(), null, "UTF-8", false);
		out.write(val);
	}
}
