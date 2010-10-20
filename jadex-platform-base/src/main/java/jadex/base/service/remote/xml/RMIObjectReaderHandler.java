package jadex.base.service.remote.xml;

import jadex.base.service.remote.CallContext;
import jadex.base.service.remote.ProxyInfo;
import jadex.base.service.remote.RemoteMethodInvocationHandler;
import jadex.bridge.IExternalAccess;
import jadex.micro.IMicroExternalAccess;
import jadex.xml.IContext;
import jadex.xml.IPostProcessor;
import jadex.xml.TypeInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.reader.ReadContext;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class RMIObjectReaderHandler  extends BeanObjectReaderHandler
{
	//-------- attributes --------
	
	/** The proxy post processor. */
	protected IPostProcessor postprocessor;
	
	//-------- constructors --------
	
	/**
	 *  Create a new handler.
	 */
	public RMIObjectReaderHandler(Set typeinfos, final IMicroExternalAccess rms)
	{
		super(typeinfos);
		this.postprocessor = new IPostProcessor()
		{
			public Object postProcess(IContext context, Object object)
			{
				ProxyInfo pi = (ProxyInfo)object;
				return Proxy.newProxyInstance(rms.getModel().getClassLoader(), 
					new Class[]{pi.getTargetClass()},
					new RemoteMethodInvocationHandler(rms, pi, (CallContext)context.getUserContext()));
			}
			
			public int getPass()
			{
				return 0;
			}
		}; 
	}
	
	//-------- methods --------
	
	/**
	 *  Get the post-processor.
	 *  @return The post-processor
	 */
	public IPostProcessor getPostProcessor(Object object, Object typeinfo)
	{
		return object instanceof ProxyInfo? postprocessor: super.getPostProcessor(object, typeinfo);
	}
}
