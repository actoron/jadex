package org.activecomponents.platform.service.message.transport.udpmtp.holepunching;

import java.net.SocketException;

import jadex.commons.SUtil;

public abstract class AbstractSolution implements IFirewallSolution
{
	/** The schema. */
	protected String schema;
	
	/** The external IP in case of NAT. */
	protected String extip;
	
	/** The port. */
	protected int port;
	
	/**
	 *  Creates the solution
	 *  @param schema The schema used.
	 *  @param extip External IP in case of NAT.
	 *  @param port The port used.
	 */
	public AbstractSolution(String schema, String extip, int port)
	{
		this.schema = schema;
		this.extip = extip;
		this.port = port;
	}
	
	/** Gets the addresses for the solution. */
	public String[] getAddresses()
	{
		String[] nwaddrs = null;
		
		if (extip == null)
		{
			try
			{
				nwaddrs = SUtil.getNetworkAddresses();
			}
			catch (SocketException e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			nwaddrs = new String[] { extip };
		}
		for (int i = 0; i < nwaddrs.length; ++i)
		{
			String baseaddr = nwaddrs[i];
			if (baseaddr.contains(":"))
				baseaddr = "[" + baseaddr + "]";
			nwaddrs[i] = (schema + baseaddr + ":" + port);
		}
		return nwaddrs;
	}
}
