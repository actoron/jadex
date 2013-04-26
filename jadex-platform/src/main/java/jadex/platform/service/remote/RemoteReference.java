package jadex.platform.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.SUtil;
import jadex.commons.transformation.annotations.Alias;

/**
 *  Remote reference for locating a specific target object in another platform.
 */
@Alias("jadex.base.service.remote.RemoteReference")
public class RemoteReference //implements Comparable
{
	//-------- attributes --------
	
	/** The rms. */
	protected IComponentIdentifier rms;
	
	/** The target identifier (sid, cid, or tid). */
	protected Object targetid;
	
//	/** The expiry date. */
//	protected long expirydate;
	
	//-------- constructors --------

	/**
	 *  Create a new remote reference. 
	 */
	public RemoteReference()
	{
	}
	
	/**
	 *  Create a new remote reference.
	 */
	public RemoteReference(IComponentIdentifier rms, Object targetid)
	{
		this.rms = rms;
		this.targetid = targetid;
	
		if(targetid instanceof RemoteReference)
			throw new RuntimeException();
	}
	
	//-------- methods --------

	/**
	 *  Get the rms.
	 *  @return the rms.
	 */
	public IComponentIdentifier getRemoteManagementServiceIdentifier()
	{
		return rms;
	}

	/**
	 *  Set the rms.
	 *  @param rms The rms to set.
	 */
	public void setRemoteManagementServiceIdentifier(IComponentIdentifier rms)
	{
		this.rms = rms;
	}
	
	/**
	 *  Get the target id.
	 *  @return The target id.
	 */
	public Object getTargetIdentifier()
	{
		return targetid;
	}

	/**
	 *  Set the target id.
	 *  @param cid The target id to set.
	 */
	public void setTargetIdentifier(Object targetid)
	{
		this.targetid = targetid;
	}
	
	/**
	 *  Test if reference is object reference (not service or component).
	 *  @return True, if object reference.
	 */
	public boolean isObjectReference()
	{
		return !(targetid instanceof IComponentIdentifier) && !(targetid instanceof IServiceIdentifier);
	}
	
//	/**
//	 *  Get the expirydate.
//	 *  @return The expirydate.
//	 */
//	public long getExpiryDate()
//	{
//		return expirydate;
//	}
//
//	/**
//	 *  Set the expirydate.
//	 *  @param expirydate The expirydate to set.
//	 */
//	public void setExpiryDate(long expirydate)
//	{
//		this.expirydate = expirydate;
//	}

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = prime * rms.hashCode();
		result = prime * result + targetid.hashCode();
		return result;
	}

	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof RemoteReference)
		{
			RemoteReference other = (RemoteReference)obj;
			ret = SUtil.equals(rms, other.rms) && SUtil.equals(targetid, other.targetid);
		}
		return ret;
	}
	
//	/**
//	 *  Compare to another object.
//	 */
//	public int compareTo(Object obj)
//	{
//		RemoteReference other = (RemoteReference)obj;
//		int ret = (int)(expirydate-other.expirydate);
//		if(ret==0)
//			ret = hashCode()-other.hashCode();
//		return ret;
//	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "RemoteReference(rms=" + rms + ", targetid=" + targetid + ")";
	}
}
