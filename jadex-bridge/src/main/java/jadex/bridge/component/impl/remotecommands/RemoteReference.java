package jadex.bridge.component.impl.remotecommands;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.SUtil;

/**
 *  Remote reference for locating a specific target object in another platform.
 */
public class RemoteReference
{
	//-------- attributes --------
	
	/** The target component. */
	protected IComponentIdentifier comp;
	
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
	public RemoteReference(IComponentIdentifier comp, Object targetid)
	{
		this.comp = comp;
		this.targetid = targetid;
	
		if(targetid instanceof RemoteReference)
			throw new RuntimeException();
	}
	
	//-------- methods --------

	/**
	 *  Get the rms.
	 *  @return the rms.
	 */
	public IComponentIdentifier getRemoteComponent()
	{
		return comp;
	}

	/**
	 *  Set the rms.
	 *  @param rms The rms to set.
	 */
	public void setRemoteComponent(IComponentIdentifier comp)
	{
		this.comp = comp;
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
	
	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = prime * comp.hashCode();
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
			ret = SUtil.equals(comp, other.comp) && SUtil.equals(targetid, other.targetid);
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
		return "RemoteReference(comp=" + comp + ", targetid=" + targetid + ")";
	}
}
