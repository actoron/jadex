package jadex.micro.examples.noplatform;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.micro.MicroAgentFactory;
import jadex.platform.service.execution.ExecutionServiceTest;
import jadex.platform.service.serialization.SerializationServices;

/**
 * 
 */
public class NoPlatformStarter
{
	public static void main(String[] args)
	{
		String agentclazz = "jadex.micro.examples.helloworld.PojoHelloWorldAgent";
		
		IComponentIdentifier cid = Starter.createPlatformIdentifier(null);
		Map<String, Object> argsmap = new HashMap<String, Object>();
		Starter.putPlatformValue(cid, IPlatformConfiguration.PLATFORMARGS, argsmap);
		Starter.putPlatformValue(cid, Starter.DATA_SERVICEREGISTRY, new ServiceRegistry());
		Starter.putPlatformValue(cid, Starter.DATA_SERIALIZATIONSERVICES, new SerializationServices(cid));
			
		IPlatformComponentAccess component = SComponentManagementService.createPlatformComponent(NoPlatformStarter.class.getClassLoader());
		IComponentFactory cfac = new MicroAgentFactory("rootid");
		IModelInfo model = cfac.loadModel(agentclazz, null, null).get();
		String ctype = cfac.getComponentType(agentclazz, null, model.getResourceIdentifier()).get();
		CMSComponentDescription desc = new CMSComponentDescription(cid).setType(ctype).setModelName(model.getFullName())
			.setResourceIdentifier(model.getResourceIdentifier()).setCreationTime(System.currentTimeMillis())
			.setFilename(model.getFilename()).setSystemComponent(SComponentManagementService.isSystemComponent(model, null, null));

		ComponentCreationInfo cci = new ComponentCreationInfo(model, null, null, desc, null, null);
		Collection<IComponentFeatureFactory> features = cfac.getComponentFeatures(model).get();
		component.create(cci, features);
		component.init().get();
		component.body().get();
		component.shutdown().get();
		
		//SComponentManagementService.createComponent(null, null, "jadex.micro.examples.helloworld.PojoHelloWorldAgent", null).add
		
		/*IInternalAccess fakeplatform = new IInternalAccess()
		{
			@Override
			public boolean hasRequiredServicePropertyProvider(IServiceIdentifier sid)
			{
				return false;
			}
			
			@Override
			public INFMixedPropertyProvider getRequiredServicePropertyProvider(IServiceIdentifier sid)
			{
				return null;
			}
			
			@Override
			public INFMixedPropertyProvider getProvidedServicePropertyProvider(IServiceIdentifier sid)
			{
				return null;
			}
			
			@Override
			public INFPropertyProvider getComponentPropertyProvider()
			{
				return null;
			}
			
			@Override
			public boolean hasEventTargets(PublishTarget pt, PublishEventLevel pi)
			{
				return false;
			}
			
			@Override
			public String getLocalType()
			{
				return null;
			}
			
			@Override
			public String getComponentFilename(String ctype)
			{
				return null;
			}
			
			@Override
			public int getChildcount()
			{
				return 0;
			}
			
			@Override
			public <T> Collection<T> searchLocalServices(ServiceQuery<T> query)
			{
				return null;
			}
			
			@Override
			public <T> T searchLocalService(ServiceQuery<T> query)
			{
				return null;
			}
			
			@Override
			public <T> ITerminableIntermediateFuture<T> getServices(Class<T> type)
			{
				return null;
			}
			
			@Override
			public <T> ITerminableIntermediateFuture<T> getServices(String name)
			{
				return null;
			}
			
			@Override
			public <T> IFuture<T> getService(Class<T> type)
			{
				return null;
			}
			
			@Override
			public <T> IFuture<T> getService(String name)
			{
				return null;
			}
			
			@Override
			public <T> Collection<T> getLocalServices(Class<T> type)
			{
				return null;
			}
			
			@Override
			public <T> Collection<T> getLocalServices(String name)
			{
				return null;
			}
			
			@Override
			public <T> T getLocalService0(Class<T> type)
			{
				return null;
			}
			
			@Override
			public <T> T getLocalService(Class<T> type)
			{
				return null;
			}
			
			@Override
			public <T> T getLocalService(String name)
			{
				return null;
			}
			
			@Override
			public <T> ISubscriptionIntermediateFuture<T> addQuery(Class<T> type)
			{
				return null;
			}
			
			@Override
			public <T> ISubscriptionIntermediateFuture<T> addQuery(String name)
			{
				return null;
			}
			
			@Override
			public void removeMethodInvocationListener(IServiceIdentifier sid, MethodInfo mi, IMethodInvocationListener listener)
			{
			}
			
			@Override
			public void removeInterceptor(IServiceInvocationInterceptor interceptor, Object service)
			{
			}
			
			@Override
			public void notifyMethodListeners(IServiceIdentifier sid, boolean start, Object proxy, Method method, Object[] args, Object callid, ServiceInvocationContext context)
			{
			}
			
			@Override
			public boolean hasMethodListeners(IServiceIdentifier sid, MethodInfo mi)
			{
				return false;
			}
			
			@Override
			public <T> T[] getProvidedServices(Class<T> clazz)
			{
				return null;
			}
			
			@Override
			public Object getProvidedServiceRawImpl(IServiceIdentifier sid)
			{
				return null;
			}
			
			@Override
			public Object getProvidedServiceRawImpl(String name)
			{
				return null;
			}
			
			@Override
			public <T> T getProvidedServiceRawImpl(Class<T> clazz)
			{
				return null;
			}
			
			@Override
			public <T> T getProvidedService(IServiceIdentifier sid)
			{
				return null;
			}
			
			@Override
			public <T> T getProvidedService(Class<T> clazz)
			{
				return null;
			}
			
			@Override
			public IService getProvidedService(String name)
			{
				return null;
			}
			
			@Override
			public IServiceInvocationInterceptor[] getInterceptors(Object service)
			{
				return null;
			}
			
			@Override
			public void addMethodInvocationListener(IServiceIdentifier sid, MethodInfo mi, IMethodInvocationListener listener)
			{
			}
			
			@Override
			public void addInterceptor(IServiceInvocationInterceptor interceptor, Object service, int pos)
			{
			}
			
			@Override
			public Map<String, Object> getResults()
			{
				return null;
			}
			
			@Override
			public Map<String, Object> getArguments()
			{
				return null;
			}
			
			@Override
			public boolean isComponentThread()
			{
				return false;
			}
			
			@Override
			public IComponentDescription getDescription()
			{
				return null;
			}
			
			@Override
			public <T> IIntermediateResultListener<T> createResultListener(IIntermediateResultListener<T> listener)
			{
				return null;
			}
			
			@Override
			public <T> IResultListener<T> createResultListener(IResultListener<T> listener)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> shutdownRequiredNFPropertyProvider(IServiceIdentifier sid)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> shutdownNFPropertyProvider(IServiceIdentifier sid)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> shutdownNFPropertyProvider()
			{
				return null;
			}
			
			@Override
			public IFuture<Void> removeRequiredNFProperty(IServiceIdentifier sid, String name)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> removeRequiredMethodNFProperty(IServiceIdentifier sid, MethodInfo method, String name)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> removeNFProperty(IServiceIdentifier sid, String name)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> removeNFProperty(String name)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> removeMethodNFProperty(IServiceIdentifier sid, MethodInfo method, String name)
			{
				return null;
			}
			
			@Override
			public <T, U> IFuture<T> getRequiredNFPropertyValue(IServiceIdentifier sid, String name, U unit)
			{
				return null;
			}
			
			@Override
			public <T> IFuture<T> getRequiredNFPropertyValue(IServiceIdentifier sid, String name)
			{
				return null;
			}
			
			@Override
			public IFuture<String[]> getRequiredNFPropertyNames(IServiceIdentifier sid)
			{
				return null;
			}
			
			@Override
			public IFuture<Map<String, INFPropertyMetaInfo>> getRequiredNFPropertyMetaInfos(IServiceIdentifier sid)
			{
				return null;
			}
			
			@Override
			public IFuture<INFPropertyMetaInfo> getRequiredNFPropertyMetaInfo(IServiceIdentifier sid, String name)
			{
				return null;
			}
			
			@Override
			public IFuture<String[]> getRequiredNFAllPropertyNames(IServiceIdentifier sid)
			{
				return null;
			}
			
			@Override
			public <T, U> IFuture<T> getRequiredMethodNFPropertyValue(IServiceIdentifier sid, MethodInfo method, String name, U unit)
			{
				return null;
			}
			
			@Override
			public <T> IFuture<T> getRequiredMethodNFPropertyValue(IServiceIdentifier sid, MethodInfo method, String name)
			{
				return null;
			}
			
			@Override
			public IFuture<String[]> getRequiredMethodNFPropertyNames(IServiceIdentifier sid, MethodInfo method)
			{
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IFuture<Map<String, INFPropertyMetaInfo>> getRequiredMethodNFPropertyMetaInfos(IServiceIdentifier sid, MethodInfo method)
			{
				return null;
			}
			
			@Override
			public IFuture<Map<MethodInfo, Map<String, INFPropertyMetaInfo>>> getRequiredMethodNFPropertyMetaInfos(IServiceIdentifier sid)
			{
				return null;
			}
			
			@Override
			public IFuture<INFPropertyMetaInfo> getRequiredMethodNFPropertyMetaInfo(IServiceIdentifier sid, MethodInfo method, String name)
			{
				return null;
			}
			
			@Override
			public IFuture<String[]> getRequiredMethodNFAllPropertyNames(IServiceIdentifier sid, MethodInfo method)
			{
				return null;
			}
			
			@Override
			public <T, U> IFuture<T> getNFPropertyValue(IServiceIdentifier sid, String name, U unit)
			{
				return null;
			}
			
			@Override
			public <T> IFuture<T> getNFPropertyValue(IServiceIdentifier sid, String name)
			{
				return null;
			}
			
			@Override
			public <T, U> IFuture<T> getNFPropertyValue(String name, U unit)
			{
				return null;
			}
			
			@Override
			public <T> IFuture<T> getNFPropertyValue(String name)
			{
				return null;
			}
			
			@Override
			public IFuture<String[]> getNFPropertyNames(IServiceIdentifier sid)
			{
				return null;
			}
			
			@Override
			public IFuture<String[]> getNFPropertyNames()
			{
				return null;
			}
			
			@Override
			public IFuture<Map<String, INFPropertyMetaInfo>> getNFPropertyMetaInfos(IServiceIdentifier sid)
			{
				return null;
			}
			
			@Override
			public IFuture<Map<String, INFPropertyMetaInfo>> getNFPropertyMetaInfos()
			{
				return null;
			}
			
			@Override
			public IFuture<INFPropertyMetaInfo> getNFPropertyMetaInfo(IServiceIdentifier sid, String name)
			{
				return null;
			}
			
			@Override
			public IFuture<INFPropertyMetaInfo> getNFPropertyMetaInfo(String name)
			{
				return null;
			}
			
			@Override
			public IFuture<String[]> getNFAllPropertyNames(IServiceIdentifier sid)
			{
				return null;
			}
			
			@Override
			public IFuture<String[]> getNFAllPropertyNames()
			{
				return null;
			}
			
			@Override
			public <T, U> IFuture<T> getMethodNFPropertyValue(IServiceIdentifier sid, MethodInfo method, String name, U unit)
			{
				return null;
			}
			
			@Override
			public <T> IFuture<T> getMethodNFPropertyValue(IServiceIdentifier sid, MethodInfo method, String name)
			{
				return null;
			}
			
			@Override
			public IFuture<String[]> getMethodNFPropertyNames(IServiceIdentifier sid, MethodInfo method)
			{
				return null;
			}
			
			@Override
			public IFuture<Map<String, INFPropertyMetaInfo>> getMethodNFPropertyMetaInfos(IServiceIdentifier sid, MethodInfo method)
			{
				return null;
			}
			
			@Override
			public IFuture<Map<MethodInfo, Map<String, INFPropertyMetaInfo>>> getMethodNFPropertyMetaInfos(IServiceIdentifier sid)
			{
				return null;
			}
			
			@Override
			public IFuture<INFPropertyMetaInfo> getMethodNFPropertyMetaInfo(IServiceIdentifier sid, MethodInfo method, String name)
			{
				return null;
			}
			
			@Override
			public IFuture<String[]> getMethodNFAllPropertyNames(IServiceIdentifier sid, MethodInfo method)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> addRequiredNFProperty(IServiceIdentifier sid, INFProperty< ? , ? > nfprop)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> addRequiredMethodNFProperty(IServiceIdentifier sid, MethodInfo method, INFProperty< ? , ? > nfprop)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> addNFProperty(IServiceIdentifier sid, INFProperty< ? , ? > nfprop)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> addNFProperty(INFProperty< ? , ? > nfprop)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> addMethodNFProperty(IServiceIdentifier sid, MethodInfo method, INFProperty< ? , ? > nfprop)
			{
				return null;
			}
			
			@Override
			public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(IFilter<IMonitoringEvent> filter, boolean initial, PublishEventLevel elm)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> publishEvent(IMonitoringEvent event, PublishTarget pt)
			{
				return null;
			}
			
			@Override
			public IFuture<IComponentDescription[]> searchComponents(IComponentDescription adesc, ISearchConstraints con)
			{
				return null;
			}
			
			@Override
			public IIntermediateFuture<Tuple2<IComponentIdentifier, Map<String, Object>>> killComponents(IComponentIdentifier... cids)
			{
				return null;
			}
			
			@Override
			public IFuture<String> getLocalTypeAsync()
			{
				return null;
			}
			
			@Override
			public IFuture<String> getFileName(String ctype)
			{
				return null;
			}
			
			@Override
			public IFuture<IComponentIdentifier[]> getChildren(String type, IComponentIdentifier parent)
			{
				return null;
			}
			
			@Override
			public IIntermediateFuture<IExternalAccess> createComponents(CreationInfo... infos)
			{
				return null;
			}
			
			@Override
			public ISubscriptionIntermediateFuture<CMSStatusEvent> createComponentWithEvents(CreationInfo info)
			{
				return null;
			}
			
			@Override
			public IFuture<IExternalAccess> createComponent(CreationInfo info)
			{
				return null;
			}
			
			@Override
			public IFuture<IExternalAccess> addComponent(Object pojocomponent)
			{
				return null;
			}
			
			@Override
			public <T> ITerminableIntermediateFuture<T> searchServices(ServiceQuery<T> query)
			{
				return null;
			}
			
			@Override
			public <T> IFuture<T> searchService(ServiceQuery<T> query)
			{
				return null;
			}
			
			@Override
			public <T> ISubscriptionIntermediateFuture<T> addQuery(ServiceQuery<T> query)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> setTags(IServiceIdentifier sid, String... tags)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> removeService(IServiceIdentifier sid)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> addService(String name, Class< ? > type, Object service, PublishInfo pi, ServiceScope scope)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> addService(String name, Class< ? > type, Object service, String proxytype)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> addService(String name, Class< ? > type, Object service)
			{
				return null;
			}
			
			@Override
			public ISubscriptionIntermediateFuture<Tuple2<String, Object>> subscribeToResults()
			{
				return null;
			}
			
			@Override
			public IFuture<Map<String, Object>> getResultsAsync()
			{
				return null;
			}
			
			@Override
			public IFuture<Exception> getExceptionAsync()
			{
				return null;
			}
			
			@Override
			public IFuture<Map<String, Object>> getArgumentsAsync()
			{
				return null;
			}
			
			@Override
			public IFuture<Void> waitForTick()
			{
				return null;
			}
			
			@Override
			public IFuture<Void> waitForTick(IComponentStep<Void> run)
			{
				return null;
			}
			
			@Override
			public IFuture<Map<String, Object>> waitForTermination()
			{
				return null;
			}
			
			@Override
			public IFuture<Void> waitForDelay(long delay)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> waitForDelay(long delay, boolean realtime)
			{
				return null;
			}
			
			@Override
			public <T> IFuture<T> waitForDelay(long delay, IComponentStep<T> step)
			{
				return null;
			}
			
			@Override
			public <T> IFuture<T> waitForDelay(long delay, IComponentStep<T> step, boolean realtime)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> suspendComponent()
			{
				return null;
			}
			
			@Override
			public IFuture<Void> stepComponent(String stepinfo)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> setComponentBreakpoints(String[] breakpoints)
			{
				return null;
			}
			
			@Override
			public <T> IFuture<T> scheduleStep(int priority, IComponentStep<T> step)
			{
				return null;
			}
			
			@Override
			public <T> IFuture<T> scheduleStep(IComponentStep<T> step)
			{
				return null;
			}
			
			@Override
			public IFuture<Void> resumeComponent()
			{
				return null;
			}
			
			@Override
			public <T> ISubscriptionIntermediateFuture<T> repeatStep(long initialDelay, long delay, IComponentStep<T> step, boolean ignorefailures)
			{
				return null;
			}
			
			@Override
			public <T> ISubscriptionIntermediateFuture<T> repeatStep(long initialDelay, long delay, IComponentStep<T> step)
			{
				return null;
			}
			
			@Override
			public ISubscriptionIntermediateFuture<CMSStatusEvent> listenToComponent()
			{
				return null;
			}
			
			@Override
			public ISubscriptionIntermediateFuture<CMSStatusEvent> listenToAll()
			{
				return null;
			}
			
			@Override
			public IFuture<Map<String, Object>> killComponent(Exception e)
			{
				return null;
			}
			
			@Override
			public IFuture<Map<String, Object>> killComponent()
			{
				return null;
			}
			
			@Override
			public IFuture<IExternalAccess> getExternalAccessAsync(IComponentIdentifier cid)
			{
				return null;
			}
			
			@Override
			public IExternalAccess getExternalAccess(IComponentIdentifier cid)
			{
				return null;
			}
			
			@Override
			public IFuture<IComponentDescription[]> getDescriptions()
			{
				return null;
			}
			
			@Override
			public IFuture<IComponentDescription> getDescriptionAsync()
			{
				return null;
			}
			
			@Override
			public IFuture<IComponentDescription> getDescription(IComponentIdentifier cid)
			{
				return null;
			}
			
			@Override
			public IFuture<IModelInfo> getModelAsync()
			{
				return null;
			}
			
			@Override
			public <T> T getExternalFeature(Class< ? extends T> type)
			{
				return null;
			}
			
			@Override
			public IParameterGuesser getParameterGuesser()
			{
				return null;
			}
			
			@Override
			public IModelInfo getModel()
			{
				return null;
			}
			
			@Override
			public Logger getLogger()
			{
				return null;
			}
			
			protected IComponentIdentifier cid = new ComponentIdentifier("platform");
			@Override
			public IComponentIdentifier getId()
			{
				return cid;
			}
			
			@Override
			public IValueFetcher getFetcher()
			{
				return null;
			}
			
			@Override
			public <T> T getFeature0(Class< ? extends T> type)
			{
				return null;
			}
			
			@Override
			public <T> T getFeature(Class< ? extends T> type)
			{
				return null;
			}
			
			@Override
			public IExternalAccess getExternalAccess()
			{
				return null;
			}
			
			@Override
			public Exception getException()
			{
				return null;
			}
			
			@Override
			public String getConfiguration()
			{
				return null;
			}
			
			@Override
			public ClassLoader getClassLoader()
			{
				return null;
			}
			
			@Override
			public Object getArgument(String name)
			{
				return null;
			}
		};
		
		final IPlatformComponentAccess component = SComponentManagementService.createPlatformComponent(NoPlatformStarter.class.getClassLoader());

		IComponentIdentifier cid = fakeplatform.getId();
		
		Starter.putPlatformValue(cid, Starter.DATA_PLATFORMACCESS, component);
		//putPlatformValue(cid, DATA_BOOTSTRAPFACTORY, cfac);
//			putPlatformValue(cid, IPlatformConfiguration.PLATFORMARGS, args);
		//putPlatformValue(cid, IPlatformConfiguration.PLATFORMCONFIG, config);
		//putPlatformValue(cid, IPlatformConfiguration.PLATFORMMODEL, model);
		
		Starter.putPlatformValue(cid, Starter.DATA_CMSSTATE, new CmsState());
		
		CmsComponentState compstate = new CmsComponentState();
		compstate.setAccess(component);
		((CmsState)Starter.getPlatformValue(cid, Starter.DATA_CMSSTATE)).getComponentMap().put(cid, compstate);
		
		ComponentCreationInfo cci = new ComponentCreationInfo(model, config.getConfigurationName(), argsmap, desc, null, null);
		Collection<IComponentFeatureFactory> features = cfac.getComponentFeatures(model).get();
		component.create(cci, features);
		
		SComponentManagementService.createComponent(null, "jadex.micro.examples.helloworld.PojoHelloWorldAgent", null, 
			null, fakeplatform).addResultListener(new IResultListener<IComponentIdentifier>()
		{
			@Override
			public void resultAvailable(IComponentIdentifier result)
			{
				System.out.println("result: "+result);
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		});*/
	}
}
