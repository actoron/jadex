package jadex.micro.features.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.ProvidedServicesComponentFeature;
import jadex.commons.IValueFetcher;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.InjectionInfoHolder;
import jadex.micro.MicroClassReader;
import jadex.micro.MicroModel.ServiceInjectionInfo;
import jadex.micro.annotation.Agent;

/**
 *  Feature for provided services.
 */
public class MicroProvidedServicesComponentFeature extends ProvidedServicesComponentFeature
{
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(IProvidedServicesFeature.class, MicroProvidedServicesComponentFeature.class);
	
	//-------- constructors --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public MicroProvidedServicesComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Create a service implementation from description.
	 *  
	 *  Add the injections for many aspects.
	 *  
	 *  todo: move injections from BasicServiceInvocationHandler to ProvidedServicesComponentFeature?!
	 */
	public IFuture<Object> createServiceImplementation(ProvidedServiceInfo info, IValueFetcher fetcher)
	{
		Future<Object> ret = new Future<>();

		super.createServiceImplementation(info, fetcher).thenAccept(impl -> 
		{
			// Proxy class can happen when service reuses impl, e.g. external access service 
			// service impl can also be pojo agent itself -> do not inject twice, checks agent annotation
			if(impl!=null && !ProxyFactory.isProxyClass(impl.getClass()) && impl.getClass().getAnnotation(Agent.class)==null)
			{
				//if(impl.getClass().toString().indexOf("TestSer")!=-1)
				//	System.out.println("testser");
				
				//MicroModel model = (MicroModel)component.getModel().getRawModel();
				
				Map<String, Object> rsers = Arrays.stream(component.getModel().getServices()).collect(Collectors.toMap(r -> r.getName(), r -> r));
				
				InjectionInfoHolder holder = new InjectionInfoHolder();
				MicroClassReader.findInjections(impl.getClass(), component.getClassLoader(), holder, rsers);
				
				MicroInjectionComponentFeature.injectStuff(component, impl, holder)
					.thenAccept(p -> 
				{
					String[] sernames = holder.getServiceInjectionNames();
					Stream<Tuple2<String, ServiceInjectionInfo[]>> s = Arrays.stream(sernames).map(sername -> new Tuple2<String, ServiceInjectionInfo[]>(sername, holder.getServiceInjections(sername)));
					Map<String, ServiceInjectionInfo[]> serinfos = s.collect(Collectors.toMap(t -> t.getFirstEntity(), t -> t.getSecondEntity())); 
					
					MicroServiceInjectionComponentFeature.injectServices(component, impl, sernames, serinfos, component.getModel())
						.thenAccept(q ->
					{
						ret.setResult(impl);
					}).exceptionally(ret);
					
				}).exceptionally(ret);
				
			}
			else
			{
				ret.setResult(impl);
			}
		}).exceptionally(ret);
			
		return ret;
	}
}
