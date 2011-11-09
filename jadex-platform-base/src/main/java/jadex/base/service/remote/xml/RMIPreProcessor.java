package jadex.base.service.remote.xml;

import jadex.base.service.remote.RemoteReferenceModule;
import jadex.bridge.IComponentIdentifier;
import jadex.xml.IContext;
import jadex.xml.IPreProcessor;

/**
 *  Preprocessor for RMI. It replaces IProxyable objects with ProxyInfo objects.
 */
public class RMIPreProcessor implements IPreProcessor
{
	//-------- attributes --------
	
	/** The remote reference module. */
	protected RemoteReferenceModule rrm;
	
	//-------- constructors --------
	
	/**
	 *  Create a new pre processor.
	 */
	public RMIPreProcessor(RemoteReferenceModule rrm)
	{
		this.rrm = rrm;
	}
	
	//-------- methods --------

	/**
	 *  Pre-process an object before the xml is written.
	 *  @param context The context.
	 *  @param object The object to pre process.
	 *  @return A possibly other object for replacing the original. 
	 *  		Null for no change.
	 *  		Only possibly when processor is applied in first pass.
	 */
	public Object preProcess(IContext context, Object object)
	{
		return rrm.getProxyReference(object, (IComponentIdentifier)context.getUserContext(), context.getClassLoader());
	}
}
