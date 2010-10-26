package jadex.base.service.remote;

public class ProxyReference
{
	/** The proxy info. */
	protected ProxyInfo pi;
	
	protected RemoteReference rr;
	
	
	/**
	 * 
	 */
	public ProxyReference()
	{
		super();
	}

	/**
	 * 
	 */
	public ProxyReference(ProxyInfo pi, RemoteReference rr)
	{
		this.pi = pi;
		this.rr = rr;
	}

	/**
	 *  Get the proxy info.
	 *  @return The proxy info.
	 */
	public ProxyInfo getProxyInfo()
	{
		return pi;
	}

	/**
	 *  Set the pi.
	 *  @param pi The pi to set.
	 */
	public void setProxyInfo(ProxyInfo pi)
	{
		this.pi = pi;
	}

	/**
	 *  Get the remoteReference.
	 *  @return the remoteReference.
	 */
	public RemoteReference getRemoteReference()
	{
		return rr;
	}

	/**
	 *  Set the remote reference.
	 *  @param remote reference The remote reference to set.
	 */
	public void setRemoteReference(RemoteReference remoteReference)
	{
		this.rr = remoteReference;
	}
	
}
