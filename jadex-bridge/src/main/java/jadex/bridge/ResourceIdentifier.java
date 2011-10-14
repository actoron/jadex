package jadex.bridge;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import java.net.URL;

/**
 *  Interface for resource identification.
 *  Contains a local identifier and a global identifier
 *  that can be used to find the resource.
 */
@Reference(local=true)
public class ResourceIdentifier implements IResourceIdentifier
{
	//-------- attributes --------
	
	/** The local identifier. */
	protected Tuple2<IComponentIdentifier, URL> lid;
	
	/** The global identifier. */
	protected String gid;
	
	//-------- constructors --------

	/**
	 *  Create a resource identifier.
	 */
	public ResourceIdentifier()
	{
		// bean constructor
	}
	
	/**
	 *  Create a resource identifier.
	 *  @param lid The local identifier.
	 *  @param gid The global idenfifier.
	 */
	public ResourceIdentifier(Tuple2<IComponentIdentifier, URL> lid, String gid)
	{
		this.lid = lid;
		this.gid = gid;
	}
	
	/**
	 *  Get the local identifier.
	 *  The local identifier consists of the platform 
	 *  component identifier and the URL of the resource. 
	 *  @return The local identifier. 
	 */
	public Tuple2<IComponentIdentifier, URL> getLocalIdentifier()
	{
		return lid;
	}
	
	/**
	 *  Get the global identifier.
	 *  @return The global identifier.
	 */
	public String getGlobalIdentifier()
	{
		return gid;
	}

	/**
	 *  Set the local identifier.
	 *  @param lid The lid to set.
	 */
	public void setLocalIdentifier(Tuple2<IComponentIdentifier, URL> lid)
	{
		this.lid = lid;
	}

	/**
	 *  Set the global identifier.
	 *  @param gid The gid to set.
	 */
	public void setGlobalIdentifier(String gid)
	{
		this.gid = gid;
	}

	
	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gid == null) ? 0 : gid.hashCode());
		result = prime * result + ((lid == null) ? 0 : lid.hashCode());
		return result;
	}

	/**
	 *  Test if equals.
	 *  They are equal when
	 *  a) global ids are equal
	 *  b) or global ids are null and local ids are equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof IResourceIdentifier)
		{
			IResourceIdentifier other = (IResourceIdentifier)obj;
			ret = (getGlobalIdentifier()!=null && getGlobalIdentifier().equals(other.getGlobalIdentifier()))
				|| (getGlobalIdentifier()==null && other.getGlobalIdentifier()==null && SUtil.equals(getLocalIdentifier(), other.getLocalIdentifier()));
		}
		return ret;
	}
	
}
