package jadex.bridge;

import java.net.URI;
import java.net.URL;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.SUtil;

/**
 *  Default implementation for resource identification.
 */
@Reference(local=true, remote=false)
public class LocalResourceIdentifier implements ILocalResourceIdentifier
{
	//-------- attributes --------
	
	/** The component identifier. */
	protected IComponentIdentifier cid;
	
	/** The URI. */
	protected URI uri;
	
	/** The host id. */
	protected String hostid;
	
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
		this(cid, SUtil.toURI(url));
	}
	
	/**
	 *  Create a resource identifier.
	 *  @param cid The platform identifier.
	 *  @param url The local URL.
	 */
	public LocalResourceIdentifier(IComponentIdentifier cid, URI uri)
	{
		this(cid, uri,
			SUtil.getMacAddress());
//			cid.getRoot().getName());	// Hack for testing jar transfer.
	}
		
	/**
	 *  Create a resource identifier.
	 *  @param cid The platform identifier.
	 *  @param url The local URL.
	 */
	public LocalResourceIdentifier(IComponentIdentifier cid, URI uri, String hostid)
	{
		if(cid==null)
		{
			throw new IllegalArgumentException("Cid must not null.");
		}
		if(uri==null)
		{
			throw new IllegalArgumentException("Url must not null.");
		}
		if(hostid==null)
		{
			hostid = cid.getName(); // in case no mac is available use cid (strict as before)
		}
		if(uri.toString().indexOf("..")!=-1)
		{
			throw new IllegalArgumentException("Url must use canonical path: "+uri);
		}
		try
		{
			if(uri.toURL().getFile().startsWith(".")
				&& !uri.toURL().getFile().equals("./"))	// Hack for eclipse jar resource loader using "./" as main URL when exporting fat jar.
			{
				throw new IllegalArgumentException("Url must be absolute: "+uri);
			}
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("Url must be absolute: "+uri);
		}
		
		this.hostid = hostid;
		this.cid = cid;
		this.uri = uri;
		
//		// Automatically add transport addresses. hack?
//		if(!(cid instanceof ITransportComponentIdentifier))
//		{
//			ComponentIdentifier	tcid	= new ComponentIdentifier(cid.getName());
//			TransportAddressBook	tab	= TransportAddressBook.getAddressBook(cid);
//			if(tab!=null)
//			{
//				tcid.setAddresses(tab.getPlatformAddresses(cid));
//			}
//			this.cid	= tcid;
//		}
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
	 *  Set the platform identifier belonging to the resource identifier.
	 *  @param cid The component identifier of the platform. 
	 */
	public void	setComponentIdentifier(IComponentIdentifier cid)
	{
		this.cid	= cid;
	}
	
	/**
	 *  Get the uri.
	 *  @return The resource uri.
	 */
	public URI	getUri()
	{
		return uri;
	}
	
	/**
	 *  Set the url.
	 *  @param url The resource url.
	 */
	public void	setUri(URI uri)
	{
		this.uri = uri;
	}
	
//	/**
//	 *  Get the uri.
//	 *  @return The resource uri.
//	 */
//	public URL	getUrl()
//	{
//		return SUtil.toURL0(uri);
//	}
//	
//	/**
//	 *  Set the url.
//	 *  @param url The resource url.
//	 */
//	public void	setUrl(URL url)
//	{
//		this.uri = SUtil.toURI(url);
//	}
	
	/**
	 *  Get the host identifier.
	 *  @return The host identifier.
	 */
	public String	getHostIdentifier()
	{
		return hostid;
	}
	
	/**
	 *  Set the host identifier.
	 *  @param hostid The host identifier.
	 */
	public void	setHostIdentifier(String hostid)
	{
		this.hostid = hostid;
	}
	
	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
//		result = prime * result + (cid!=null? cid.hashCode(): 0);
		result = prime * result + hostid.hashCode();
		if(uri!=null) // hack due to old platforms sending with url :-(
			result = prime * result + uri.hashCode();
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
//			ret = SUtil.equals(getComponentIdentifier(), other.getComponentIdentifier())
//				&& SUtil.equals(getUrl(), other.getUrl());
			ret = SUtil.equals(getHostIdentifier(), other.getHostIdentifier())
				&& SUtil.equals(getUri(), other.getUri());
		}
		return ret;
	}
	
	/**
	 *  Get a string representation of this object.
	 */
	public String	toString()
	{
		return uri+"-"+hostid+"@"+cid;
	}
}
