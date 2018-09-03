package jadex.bdiv3.features.impl;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import jadex.bdiv3.IBDIAgent;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.runtime.impl.BDIServiceInvocationHandler;
import jadex.bdiv3.runtime.impl.CapabilityPojoWrapper;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.component.ProvidedServicesComponentFeature;
import jadex.commons.IParameterGuesser;
import jadex.commons.IValueFetcher;
import jadex.commons.SimpleParameterGuesser;
import jadex.javaparser.SimpleValueFetcher;

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
	protected Object createServiceImplementation(ProvidedServiceInfo info, IValueFetcher fetcher) throws Exception
	{
		// todo: cleanup this HACK!!!
		if(getComponent().getFeature0(IPojoComponentFeature.class)!=null)
		{
			int i = info.getName()!=null ? info.getName().indexOf(MElement.CAPABILITY_SEPARATOR) : -1;
			Object	ocapa	= getComponent().getFeature(IPojoComponentFeature.class).getPojoAgent();
			String	capa	= null;
			if(i!=-1)
			{
				capa	= info.getName().substring(0, i); 
				SimpleValueFetcher fet = new SimpleValueFetcher(fetcher);
				ocapa = ((BDIAgentFeature)getComponent().getFeature(IBDIAgentFeature.class)).getCapabilityObject(capa);
				fet.setValue("$pojocapa", ocapa);
				fetcher = fet;
				
				Set<Object> vals = new HashSet<Object>();
				vals.add(ocapa);
				vals.add(new CapabilityPojoWrapper(getInternalAccess(), ocapa, capa));
				hackguesser = new SimpleParameterGuesser(super.getParameterGuesser(), vals);
			}
			else
			{
				hackguesser = null;
			}
		}
		
		// Support special case that BDI should implement provided service with plans.
		Object ret = null;
		ProvidedServiceImplementation impl = info.getImplementation();
		if(impl!=null && impl.getClazz()!=null && impl.getClazz().getType(getComponent().getClassLoader()).equals(IBDIAgent.class))
		{
			Class<?> iface = info.getType().getType(getComponent().getClassLoader());
			ret = ProxyFactory.newProxyInstance(getComponent().getClassLoader(), new Class[]{iface}, 
				new BDIServiceInvocationHandler(getInternalAccess(), iface));
		}
		else
		{
			ret = super.createServiceImplementation(info, fetcher);
		}
		
//		hackguesser = null;
		
		return ret;
	}
	
//	public IValueFetcher getValueFetcher()
//	{
//		return super.getValueFetcher();
//	}
	
	protected IParameterGuesser hackguesser;
	public IParameterGuesser getParameterGuesser()
	{
		return hackguesser!=null? hackguesser: super.getParameterGuesser();
	}
}