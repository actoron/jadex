package jadex.bridge;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.Base64;
import jadex.commons.SUtil;
import jadex.commons.collection.LRU;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 *  Default implementation for resource identification.
 *  Contains only a local identifier and a global identifier
 *  that can be used to find the resource.
 */
@Reference(local=true, remote=false)
public class ResourceIdentifier implements IResourceIdentifier
{
	//-------- constants --------
	
	/** LRU for hashes (hack!!!). */
	protected static LRU<String, String>	hashes	= new LRU<String, String>(200);
	
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
		
		File	f;
		if(gid==null && lid!=null && (f=new File(lid.getUri().getPath())).exists() && f.isFile())
		{
			String	hash	= hashes.get(lid.getUri().getPath());
			if(hash==null)
			{
				try
				{
					long	start	= System.nanoTime();
					MessageDigest md = MessageDigest.getInstance("MD5");
					DigestInputStream	dis	= new DigestInputStream(new FileInputStream(f), md);
					byte[]	buf	= new byte[8192];
					int	total	= 0;
					int	read;
					while((read=dis.read(buf))!=-1)
					{
						total	+= read;
					}
					dis.close();
					hash	= "::" + new String(Base64.encode(md.digest()), "UTF-8");
					long	end	= System.nanoTime();				
					
					System.out.println("Hashing "+SUtil.bytesToString(total)+" of "+f.getName()+" took "+((end-start)/100000)/10.0+" ms.");
					hashes.put(lid.getUri().getPath(), hash);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
			
			this.gid	= new GlobalResourceIdentifier(hash, null, null);
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
