package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;

/**
 *  Remote reference for locating a specific target object in another platform.
 */
public class RemoteReference
{
	//-------- attributes --------
	
	/** The rms. */
	protected IComponentIdentifier rms;
	
	/** The target identifier (sid, cid, or tid). */
	protected Object targetid;
	
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

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "RemoteReference(rms=" + rms + ", targetid=" + targetid + ")";
	}
	
	
	
}
