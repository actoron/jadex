package jadex.base.relay;

import java.io.IOException;
import java.io.OutputStream;

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
		boolean firstout = false;
		out.write("{\"data\":[".getBytes("UTF-8"));		
		PlatformInfo[]	pis	= handler.getCurrentPlatforms();
		for(int i=0; i<pis.length; i++)
		{
			if(firstout)
			{
				out.write(",[\"".getBytes("UTF-8"));
			}
			else
			{
				firstout	= true;
				out.write("[\"".getBytes("UTF-8"));
			}
			out.write(pis[i].getId().getBytes("UTF-8"));
			out.write("\",\"".getBytes("UTF-8"));
			out.write(pis[i].getHostName().getBytes("UTF-8"));
			out.write("\",\"".getBytes("UTF-8"));
			out.write(pis[i].getLocation().getBytes("UTF-8"));
			out.write("\",\"".getBytes("UTF-8"));
			out.write(pis[i].getCountryCode().getBytes("UTF-8"));
			out.write("\",\"".getBytes("UTF-8"));
			out.write(pis[i].getConnectTime().getBytes("UTF-8"));
			out.write("\",\"".getBytes("UTF-8"));
			out.write(Integer.toString(pis[i].getMessageCount()).getBytes("UTF-8"));
			out.write("\",\"".getBytes("UTF-8"));
			out.write(pis[i].getTransferRate().getBytes("UTF-8"));
			out.write("\"]".getBytes("UTF-8"));
		}
		
		out.write("]}".getBytes("UTF-8"));		
	}
}
