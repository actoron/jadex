package jadex.base.service.remote.xml;

import jadex.base.service.remote.RemoteReferenceModule;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.IRemotable;
import jadex.commons.SReflect;
import jadex.xml.IContext;
import jadex.xml.IPreProcessor;

import java.util.ArrayList;
import java.util.List;

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
		Class[] remoteinterfaces = getRemoteInterfaces(object);
		
		if(remoteinterfaces.length==0)
			throw new RuntimeException("Proxyable object has no remote interfaces: "+object);

		object = rrm.getProxyReference(object, remoteinterfaces, 
			(IComponentIdentifier)context.getUserContext(), context.getClassLoader());
		
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
			List todo = new ArrayList();
			todo.add(object.getClass());
			
			while(todo.size()>0)
			{
				Class clazz = (Class)todo.remove(0);
				if(clazz.isInterface())
				{
					boolean isref = SReflect.isSupertype(IRemotable.class, clazz);
					if(!isref)
					{
						Reference ref = (Reference)clazz.getAnnotation(Reference.class);
						isref = ref!=null && ref.remote();
					}
					if(isref)
					{
						if(!ret.contains(clazz))
							ret.add(clazz);
					}
				}
				Class superclazz = clazz.getSuperclass();
				if(superclazz!=null && !superclazz.equals(Object.class))
					todo.add(superclazz);
				Class[] interfaces = clazz.getInterfaces();
				for(int i=0; i<interfaces.length; i++)
				{
					todo.add(interfaces[i]);
				}
			}
			
			if(object instanceof IService)
			{
				Class serviceinterface = ((IService)object).getServiceIdentifier().getServiceType();
				if(!ret.contains(serviceinterface))
					ret.add(serviceinterface);
			}
		}
		
		return (Class[])ret.toArray(new Class[ret.size()]);
	}
}
