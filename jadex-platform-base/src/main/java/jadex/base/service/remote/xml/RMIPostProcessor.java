package jadex.base.service.remote.xml;

import jadex.base.service.remote.CallContext;
import jadex.base.service.remote.ProxyInfo;
import jadex.base.service.remote.RemoteMethodInvocationHandler;
import jadex.micro.IMicroExternalAccess;
import jadex.xml.IContext;
import jadex.xml.IPostProcessor;

import java.lang.reflect.Proxy;

/**
 * 
 */
public class RMIPostProcessor implements IPostProcessor
{
	/** The rms. */
	protected IMicroExternalAccess rms;
	
	/**
	 * 
	 */
	public RMIPostProcessor(IMicroExternalAccess rms)
	{
		this.rms = rms;
	}
	
	/**
	 * 
	 */
	public Object postProcess(IContext context, Object object)
	{
		ProxyInfo pi = (ProxyInfo)object;
		return Proxy.newProxyInstance(rms.getModel().getClassLoader(), 
			new Class[]{pi.getTargetClass()},
			new RemoteMethodInvocationHandler(rms, pi, (CallContext)context.getUserContext()));
	}
	
	/**
	 * 
	 */
	public int getPass()
	{
		return 0;
	}
}
