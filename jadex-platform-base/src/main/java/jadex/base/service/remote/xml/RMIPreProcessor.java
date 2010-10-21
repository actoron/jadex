package jadex.base.service.remote.xml;

import jadex.base.service.remote.CallContext;
import jadex.base.service.remote.ProxyInfo;
import jadex.base.service.remote.RemoteServiceManagementService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.IProxyable;
import jadex.commons.SReflect;
import jadex.xml.IContext;
import jadex.xml.IPreProcessor;
import jadex.xml.writer.WriteContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class RMIPreProcessor implements IPreProcessor
{
	//-------- attributes --------
	
	/** The rms. */
	protected IComponentIdentifier rms;
	
	//-------- constructors --------
	
	/**
	 *  Create a new pre processor.
	 */
	public RMIPreProcessor(IComponentIdentifier rms)
	{
		this.rms = rms;
	}
	
	/**
	 *  Pre-process an object after an XML has been loaded.
	 *  @param context The context.
	 *  @param object The object to post process.
	 *  @return A possibly other object for replacing the original. 
	 *  		Null for no change.
	 *  		Only possibly when processor is applied in first pass.
	 */
	public Object preProcess(IContext context, Object object)
	{
		Class[] remoteinterfaces = getRemoteInterfaces(object);
		
		if(remoteinterfaces.length>0)
		{
			WriteContext wc = (WriteContext)context;
			CallContext cc = (CallContext)wc.getUserContext();
			object = RemoteServiceManagementService.getProxyInfo(rms, object, remoteinterfaces, cc);
		
			if(((ProxyInfo)object).getTargetInterfaces().length==0)
				System.out.println("here");
		}
		
		return object;
	}
	
	/**
	 *  Get the proxy interface (null if none).
	 */
	protected Class[] getRemoteInterfaces(Object object)
	{
		List ret = new ArrayList();
		
		if(object!=null)
		{
			// todo?! search super types etc.
			Class[] interfaces = object.getClass().getInterfaces();
			for(int i=0; i<interfaces.length; i++)
			{
				if(SReflect.isSupertype(IProxyable.class, interfaces[i]))
					ret.add(interfaces[i]);
			}
		}
		
		return (Class[])ret.toArray(new Class[ret.size()]);
	}
}
