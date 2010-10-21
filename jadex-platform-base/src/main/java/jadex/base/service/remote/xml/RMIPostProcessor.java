package jadex.base.service.remote.xml;

import jadex.base.service.remote.CallContext;
import jadex.base.service.remote.ProxyInfo;
import jadex.base.service.remote.RemoteMethodInvocationHandler;
import jadex.base.service.remote.RemoteServiceManagementService;
import jadex.micro.IMicroExternalAccess;
import jadex.xml.IContext;
import jadex.xml.IPostProcessor;

import java.lang.reflect.Proxy;

/**
 *  The rmi postprocessor has the task to create a proxy from a proxyinfo.
 */
public class RMIPostProcessor implements IPostProcessor
{
	//-------- attributes --------
	
	/** The rms. */
	protected IMicroExternalAccess rms;
	
	//-------- constructors --------
	
	/**
	 *  Create a new post processor.
	 */
	public RMIPostProcessor(IMicroExternalAccess rms)
	{
		this.rms = rms;
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
		ProxyInfo pi = (ProxyInfo)object;	
		return RemoteServiceManagementService.getProxy(rms, pi, (CallContext)context.getUserContext());
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
