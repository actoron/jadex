/**
 * 
 */
package jadex.bridge;

import jadex.bridge.service.annotation.Reference;

import java.net.URL;

/**
 *  Default implementation for global resource identification.
 */
@Reference(local=true, remote=false)
public class GlobalResourceIdentifier implements IGlobalResourceIdentifier
{
	//-------- attributes --------
	
	/** The id. */
	protected String id;
	
	/** The URL. */
	protected URL url;
	
	/** The version info. */
	protected String versioninfo;
	
	//-------- constructors --------

	/**
	 *  Create a resource identifier.
	 */
	public GlobalResourceIdentifier()
	{
		// bean constructor
	}
	
	/**
	 *  Create a resource identifier.
	 *  @param cid The platform identifier.
	 *  @param url The local URL.
	 */
	public GlobalResourceIdentifier(String id, URL url, String versioninfo)
	{
		if(id==null)
			throw new IllegalArgumentException("Id must not null.");
		
		this.id = id;
		this.url = url;
		this.versioninfo = versioninfo;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the resource id. E.g. in case of maven the global
	 *  coordinates in the form groupid:artifactid:version
	 *  @return The id. 
	 */
	public String getResourceId()
	{
		return id;
	}
	
	/**
	 *  todo: make struct to also allow containing
	 *  Get the url.
	 *  @return The resource url.
	 */
	public URL getRepositoryInfo()
	{
		return url;
	}
	
	/**
	 *  Set the id.
	 *  @param id The id.
	 */
	public void setResourceId(String id)
	{
		this.id = id;
	}
	
	/**
	 *  Set the repository info.
	 *  @param info The info.
	 */
	public void setRepositoryInfo(URL url)
	{
		this.url = url;
	}
	
	/**
	 *  Get the version info. Important in case
	 *  of snapshot versions, here the concrete
	 *  timestamp of the version is included.
	 *  @return The version info.
	 */
	public String getVersionInfo()
	{
		return versioninfo;
	}
	
	/**
	 *  Set the version info.
	 */
	public void setVersionInfo(String versioninfo)
	{
		this.versioninfo = versioninfo;
	}
	
	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		return  31 + id.hashCode();
	}

	/**
	 *  Test if equals.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof IGlobalResourceIdentifier)
		{
			IGlobalResourceIdentifier other = (IGlobalResourceIdentifier)obj;
			ret = other.getResourceId().equals(getResourceId());
		}
		return ret;
	}
	
	/**
	 *  Get a string representation of this object.
	 */
	public String	toString()
	{
		String ret = id;
		if(url!=null)
			ret += " ("+url+")";
		return ret;
	}
}
