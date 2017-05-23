package jadex.bridge.service.types.serialization;

import jadex.bridge.IComponentIdentifier;

/**
 *  This class implements the rmi handling. It mainly supports:
 *  - remote reference management
 *  - creation of proxy references for transferring IProxyable objects
 *  - creation of proxies on the remote side of a target object
 *  - distributed garbage collection for target (remote) objects using reference counting
 *  - management of interfaceproperties for metadata such as exclusion or replacement of methods
 */
public interface IRemoteReferenceModule
{
	//-------- methods --------
	
	/**
	 *  Get a remote reference for a component for transport. 
	 *  (Called during marshalling from writer).
	 */
	public Object getProxyReference(Object target, IComponentIdentifier tmpholder, final ClassLoader cl);
}
