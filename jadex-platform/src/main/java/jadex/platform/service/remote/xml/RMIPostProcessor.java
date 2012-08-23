package jadex.platform.service.remote.xml;

import jadex.base.service.remote.ProxyReference;
import jadex.platform.service.remote.RemoteReferenceModule;
import jadex.xml.IContext;
import jadex.xml.IPostProcessor;

/**
 *  The rmi postprocessor has the task to create a proxy from a proxyinfo.
 */
public class RMIPostProcessor implements IPostProcessor
{
	//-------- attributes --------
	
	/** The remote reference module. */
	protected RemoteReferenceModule rrm;
	
	//-------- constructors --------
	
	/**
	 *  Create a new post processor.
	 */
	public RMIPostProcessor(RemoteReferenceModule rrm)
	{
		this.rrm = rrm;
	}
	
	//-------- methods --------
	
	/**
	 *  Post-process an object after an XML has been loaded.
	 *  @param context The context.
	 *  @param object The object to post process.
	 *  @return A possibly other object for replacing the original. 
	 *  		Null for no change.
	 *  		Only possibly when processor is applied in first pass.
	 */
	public Object postProcess(IContext context, Object object)
	{
		try
		{
			return rrm.getProxy((ProxyReference)object, context.getClassLoader());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Get the pass number.
	 *  @return The pass number (starting with 0 for first pass).
	 */
	public int getPass()
	{
		return 0;
	}
}
