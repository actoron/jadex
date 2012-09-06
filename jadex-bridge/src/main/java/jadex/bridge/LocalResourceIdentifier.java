package jadex.bridge;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.SUtil;

import java.net.URL;

/**
 *  Default implementation for resource identification.
 */
@Reference(local=true, remote=false)
public class LocalResourceIdentifier implements ILocalResourceIdentifier
{
	//-------- attributes --------
	
	/** The component identifier. */
	protected IComponentIdentifier cid;
	
	/** The URL. */
	protected URL url;
	
	//-------- constructors --------

	/**
	 *  Create a resource identifier.
	 */
	public LocalResourceIdentifier()
	{
		// bean constructor
	}
	
	/**
	 *  Create a resource identifier.
	 *  @param cid The platform identifier.
	 *  @param url The local URL.
	 */
	public LocalResourceIdentifier(IComponentIdentifier cid, URL url)
	{
		if(cid==null)
		{
			throw new IllegalArgumentException("Cid must not null.");
		}
		if(url==null)
		{
			throw new IllegalArgumentException("Url must not null.");
		}
		if(url.toString().indexOf("..")!=-1)
		{
			throw new IllegalArgumentException("Url must use canonical path: "+url);
		}
		if(url.getFile().startsWith("."))
		{
			throw new IllegalArgumentException("Url must be absolute: "+url);
		}
		
		this.cid = cid;
		this.url = url;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the platform identifier belonging to the resource identifier.
	 *  @return The component identifier of the platform. 
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return cid;
	}
	
	/**
	 *  Get the url.
	 *  @return The resource url.
	 */
	public URL	getUrl()
	{
		return url;
	}
	
	/**
	 *  Set the platform identifier belonging to the resource identifier.
	 *  @param cid The component identifier of the platform. 
	 */
	public void	setComponentIdentifier(IComponentIdentifier cid)
	{
		this.cid	= cid;
	}
	
	/**
	 *  Set the url.
	 *  @param url The resource url.
	 */
	public void	setUrl(URL url)
	{
		this.url	= url;
	}
	
	
	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (cid!=null? cid.hashCode(): 0);
		result = prime * result + (url!=null? url.hashCode(): 0);
		return result;
	}

	/**
	 *  Test if equals.

	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof ILocalResourceIdentifier)
		{
			ILocalResourceIdentifier other = (ILocalResourceIdentifier)obj;
			ret = SUtil.equals(getComponentIdentifier(), other.getComponentIdentifier())
				&& SUtil.equals(getUrl(), other.getUrl());
		}
		return ret;
	}
	
	/**
	 *  Get a string representation of this object.
	 */
	public String	toString()
	{
		return url+"@"+cid;
	}
}
