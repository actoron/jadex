package jadex.bridge;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.Base64;
import jadex.commons.SUtil;
import jadex.commons.collection.LRU;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import org.bouncycastle.crypto.digests.SHA512Digest;

/**
 *  Default implementation for resource identification.
 *  Contains only a local identifier and a global identifier
 *  that can be used to find the resource.
 */
@Reference(local=true, remote=false)
public class ResourceIdentifier implements IResourceIdentifier
{
	//-------- attributes --------
	
	/** The local identifier. */
	protected ILocalResourceIdentifier	lid;
	
	/** The global identifier. */
	protected IGlobalResourceIdentifier gid;
	
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
	public ResourceIdentifier(ILocalResourceIdentifier lid, IGlobalResourceIdentifier gid)
	{
//		if(lid.getUrl()!=null && lid.getUrl().toString().indexOf("jar:")!=-1)
//			System.out.println("hjere");
		
		this.lid = lid;
		this.gid = gid;
		
		if(gid==null && lid!=null)
		{
			File f	= new File(lid.getUri().getPath());
			this.gid	= new GlobalResourceIdentifier("::"+SUtil.getHashCode(f), null, null);
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Get the local identifier.
	 *  The local identifier consists of the platform 
	 *  component identifier and the URL of the resource. 
	 *  @return The local identifier. 
	 */
	public ILocalResourceIdentifier getLocalIdentifier()
	{
		return lid;
	}
	
	/**
	 *  Get the global identifier.
	 *  @return The global identifier.
	 */
	public IGlobalResourceIdentifier getGlobalIdentifier()
	{
		return gid;
	}

	/**
	 *  Set the local identifier.
	 *  @param lid The lid to set.
	 */
	public void setLocalIdentifier(ILocalResourceIdentifier lid)
	{
		this.lid = lid;
	}

	/**
	 *  Set the global identifier.
	 *  @param gid The gid to set.
	 */
	public void setGlobalIdentifier(IGlobalResourceIdentifier gid)
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
		result = prime * result + (gid!=null? gid.hashCode(): lid.hashCode());
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
	
	/**
	 * 
	 */
	public static IResourceIdentifier getLocalResourceIdentifier(IResourceIdentifier rid)
	{
		IResourceIdentifier ret = null;
		// why check that global is not null???
//		if(rid!=null && rid.getGlobalIdentifier()!=null && rid.getLocalIdentifier()!=null)
		if(rid!=null && rid.getLocalIdentifier()!=null)
		{
			ret = new ResourceIdentifier(rid.getLocalIdentifier(), null);
		}
		return ret;
	}
	
	/**
	 *  Get a string representation of this object.
	 */
	public String	toString()
	{
//		return "ResourceIdentifier(globalid="+gid==null? "n/a": gid+", localid"+(lid!=null?lid.toString(): "n/a")+")";
		return "global="+(gid==null? "n/a": gid)+", local="+(lid!=null? lid.toString(): "n/a");
	}
}
