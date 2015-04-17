package jadex.bdiv3.features.impl;

import jadex.bdiv3.IBDIAgent;
import jadex.bdiv3.runtime.impl.BDIServiceInvocationHandler;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.component.ProvidedServicesComponentFeature;

import java.lang.reflect.Proxy;

/**
 *  Overriden to allow for service implementations to be directly mapped to plans.
 */
public class BDIProvidedServicesComponentFeature extends ProvidedServicesComponentFeature
{
	//-------- constructors --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public BDIProvidedServicesComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Init a service.
	 *  Overriden to allow for service implementations as BPMN processes using signal events.
	 */
	protected Object createServiceImplementation(ProvidedServiceInfo info) throws Exception
	{
		// Support special case that BDI should implement provided service with plans.
		Object ret = null;
		ProvidedServiceImplementation impl = info.getImplementation();
		if(impl!=null && impl.getClazz()!=null && impl.getClazz().getType(getComponent().getClassLoader()).equals(IBDIAgent.class))
		{
			Class<?> iface = info.getType().getType(getComponent().getClassLoader());
			ret = Proxy.newProxyInstance(getComponent().getClassLoader(), new Class[]{iface}, 
				new BDIServiceInvocationHandler(getComponent(), iface));
		}
		else
		{
			ret = super.createServiceImplementation(info);
		}
		return ret;
	}
}