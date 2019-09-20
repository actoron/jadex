package jadex.noplatform;

import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.micro.MicroAgentFactory;
import jadex.noplatform.services.BaseService;

/**
 *  Setting up a minimal Jadex to run components of a specific kernel. 
 */
public class NoPlatformStarter
{
	/**
	 *  Starts a component with few dependencies.
	 */
	public static void main(String[] args)
	{
		//IExternalAccess platform = createPlatform().get();
		//platform.createComponent(new CreationInfo().setFilename("jadex.micro.examples.helloworld.PojoHelloWorldAgent.class")).get();
		//platform.createComponent(new CreationInfo().setFilename("jadex.micro.benchmarks.AgentCreationAgent.class")).get();
		
//		createAgent("jadex.micro.examples.helloworld.PojoHelloWorldAgent.class").addResultListener(new IResultListener<IExternalAccess>()
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				exception.printStackTrace();
//			}
//			
//			public void resultAvailable(IExternalAccess result)
//			{
//				System.out.println("created: "+result);
//			}
//		});
		
		Tuple2<IExecutionService, IClockService> tup = BaseService.createServices();
		
		for(int i=0; i<1000; i++)
			MicroAgentFactory.createAgent("jadex.micro.examples.helloworld.PojoHelloWorldAgent.class", tup.getFirstEntity(), tup.getSecondEntity());
		
		SUtil.sleep(50000);
		System.out.println("main end");
	}
	
	/**
	 *  Create the necessary platform service replacements.
	 *  @return The services (execution and clock).
	 * /
	public static Tuple2<IExecutionService, IClockService> createServices()
	{
		IComponentIdentifier pcid = Starter.createPlatformIdentifier(null);
		IThreadPool threadpool = new JavaThreadPool(true);
		ExecutionService es = new ExecutionService(pcid, threadpool);
		es.startService().get();
		ClockService cs = new ClockService(pcid, null, threadpool);
		cs.startService().get();
		return new Tuple2<IExecutionService, IClockService>(es, cs);
	}*/
	
	/**
	 *  Create a micro agent using services.
	 *  
	 *  Note: this method automatically creates needed platform services.
	 *  Using this method frequently is inefficient as they are recreated on each call.
	 *  
	 *  @param filename The agent filename.
	 *  @return The external access of the agent.
	 * /
	public static IFuture<IExternalAccess> createMicroAgent(String filename)
	{
		Tuple2<IExecutionService, IClockService> tup = createServices();
		MicroAgentFactory cfac = new MicroAgentFactory("rootid");
		cfac.setFeatures(MicroAgentFactory.NOPLATFORM_DEFAULT_FEATURES);
		return createAgent(filename, cfac, tup.getFirstEntity(), tup.getSecondEntity());
	}*/
	
	/**
	 *  Create a micro agent using services.
	 *  @param filename The agent filename.
	 *  @param es The execution service.
	 *  @param cs The clock service.
	 *  @return The external access of the agent.
	 * /
	public static IFuture<IExternalAccess> createMicroAgent(String filename, IExecutionService es, IClockService cs)
	{
		MicroAgentFactory cfac = new MicroAgentFactory("rootid");
		cfac.setFeatures(MicroAgentFactory.NOPLATFORM_DEFAULT_FEATURES);
		return createAgent(filename, cfac, es, cs);
	}*/
	
	/**
	 *  Create an agent based on filename, agent factory and platform services.
	 *  @param filename The model filename.
	 *  @param es The execution service.
	 *  @param cs The clock service.
	 *  @return External access of the created agent.
	 * /
	public static IFuture<IExternalAccess> createAgent(String filename, IComponentFactory cfac, IExecutionService es, IClockService cs)
	{
		Future<IExternalAccess> ret = new Future<>();
				
		IComponentIdentifier pcid = ((IService)es).getServiceId().getProviderId();
		
		if(Starter.getPlatformValue(pcid, IExecutionService.class.getName())==null)
			Starter.putPlatformValue(pcid, IExecutionService.class.getName(), es);
		if(Starter.getPlatformValue(pcid, IClockService.class.getName())==null)
			Starter.putPlatformValue(pcid, IClockService.class.getName(), cs);
		if(Starter.getPlatformValue(pcid, Starter.DATA_INVOKEDMETHODS)==null)
			Starter.putPlatformValue(pcid, Starter.DATA_INVOKEDMETHODS, Collections.synchronizedMap(new WeakHashMap<Object, Set<String>>()));
		
		IModelInfo model = cfac.loadModel(filename, null, null).get();
		String ctype = cfac.getComponentType(filename, null, model.getResourceIdentifier()).get();
		
		ComponentIdentifier cid = new ComponentIdentifier(SUtil.createPlainRandomId(filename, 6)+"@"+pcid);
		
		CMSComponentDescription desc = new CMSComponentDescription(cid).setType(ctype).setModelName(model.getFullName())
			.setResourceIdentifier(model.getResourceIdentifier()).setCreationTime(System.currentTimeMillis())
			.setFilename(model.getFilename()).setSystemComponent(SComponentManagementService.isSystemComponent(model, null, null));
		
		// create component from model
		ComponentCreationInfo cci = new ComponentCreationInfo(model, null, null, desc, null, null);
		Collection<IComponentFeatureFactory> features = cfac.getComponentFeatures(model).get();
		
		IPlatformComponentAccess component = SComponentManagementService.createPlatformComponent(NoPlatformStarter.class.getClassLoader(),
			new PlatformComponent() 
		{
			public IFuture<Map<String, Object>> killComponent(IComponentIdentifier cid)
			{
				Future<Map<String, Object>> ret = new Future<>();
				
				//agent.getLogger().info("Terminating component: "+cid.getName());
				IResultListener<Void> cc = new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						//System.out.println("Killed: " + cid);
						cont();
					}
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
						ret.setException(exception);
					}
					
					protected void cont()
					{
						IArgumentsResultsFeature arf = getFeature0(IArgumentsResultsFeature.class);
						if(arf!=null)
						{
							ret.setResult(arf.getResults());
						}
						else
						{
							ret.setResult(Collections.EMPTY_MAP);
						}
					}
				};
				
				shutdown().addResultListener(cc);
				return ret;
			}
		});
		
		component.create(cci, features);
		component.init().thenAccept(x ->
		{
			//long end = System.currentTimeMillis();
			//System.out.println("init took "+(end-start)+" ms, thread: "+Thread.currentThread());
			
			ret.setResult(component.getInternalAccess());
			
			component.body().get();
			
			// Shutdown is called via killComponent()
			//component.shutdown().get();
		}).exceptionally(ret);
		
		return ret;
	}*/
	
	/**
	 * 
	 * @return
	 */
	/*public static IFuture<IExternalAccess> createPlatform()
	{
		Future<IExternalAccess> ret = new Future<>();
	
		Logger rootlogger = LogManager.getLogManager().getLogger("");
		rootlogger.setLevel(Level.SEVERE);
		
		long start = System.currentTimeMillis();
		
		IComponentIdentifier cid = Starter.createPlatformIdentifier(null);
		//System.out.println("platfrom cid: "+cid);
		
		// create helper stuff: service registry, serialization 
		
		Map<String, Object> argsmap = new HashMap<String, Object>();
		Starter.putPlatformValue(cid, IPlatformConfiguration.PLATFORMARGS, argsmap);
		ServiceRegistry reg = new ServiceRegistry();
		Starter.putPlatformValue(cid, Starter.DATA_SERVICEREGISTRY, reg);
		Starter.putPlatformValue(cid, Starter.DATA_SERIALIZATIONSERVICES, new SerializationServices(cid));
		CmsState cmsstate = new CmsState();
		Starter.putPlatformValue(cid, Starter.DATA_CMSSTATE, cmsstate);
		Starter.putPlatformValue(cid, Starter.DATA_INVOKEDMETHODS, Collections.synchronizedMap(new WeakHashMap<Object, Set<String>>()));
	
		// Create necessary platform services
		
		IThreadPool threadpool = new JavaThreadPool(false);
		
		ExecutionService es = new ExecutionService(cid, threadpool);
		es.startService().get();
		reg.addLocalService(es);
		
		ClockService cs = new ClockService(cid, null, threadpool);
		cs.startService().get();
		reg.addLocalService(cs);
		
		//LibraryService ls = new LibraryService(cid);
		//ls.startService().get();
		//reg.addLocalService(ls);
		
		// create platform component 
		
		// load model
		//String modelname = "jadex.micro.MinimalAgent";
		String modelname = "jadex.micro.KernelMicroAgent";
		IComponentFactory cfac = new MicroAgentFactory("rootid");
		IModelInfo model = cfac.loadModel(modelname, null, null).get();
		String ctype = cfac.getComponentType(modelname, null, model.getResourceIdentifier()).get();
		CMSComponentDescription desc = new CMSComponentDescription(cid).setType(ctype).setModelName(model.getFullName())
			.setResourceIdentifier(model.getResourceIdentifier()).setCreationTime(System.currentTimeMillis())
			.setFilename(model.getFilename()).setSystemComponent(SComponentManagementService.isSystemComponent(model, null, null));
		
		// create component from model
		
		ComponentCreationInfo cci = new ComponentCreationInfo(model, null, null, desc, null, null);
		Collection<IComponentFeatureFactory> features = cfac.getComponentFeatures(model).get();
		
		IPlatformComponentAccess component = SComponentManagementService.createPlatformComponent(NoPlatformStarter.class.getClassLoader());
		
		// needed for cms
		CmsComponentState coms = new CmsComponentState();
		coms.setAccess(component);
		cmsstate.getComponentMap().put(cid, coms);
		
		component.create(cci, features);
		component.init().thenAccept(x ->
		{
			ret.setResult(component.getPlatformComponent().getExternalAccess());
			long end = System.currentTimeMillis();
			
			System.out.println("platform start took "+(end-start)+" ms, thread: "+Thread.currentThread());
			
			component.body().get();
			//System.out.println("platform shutdown");
			//component.shutdown().get();
			//System.out.println("platform end");
		}).exceptionally(ret);
		
		//return 
		return ret;
	}*/
	
}
