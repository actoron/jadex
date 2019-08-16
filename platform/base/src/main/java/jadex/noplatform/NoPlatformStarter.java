package jadex.noplatform;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.CmsState;
import jadex.bridge.service.types.cms.CmsState.CmsComponentState;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.JavaThreadPool;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgentFactory;
import jadex.platform.service.serialization.SerializationServices;

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
		IExternalAccess platform = createPlatform().get();

		//platform.createComponent(new CreationInfo().setFilename("jadex.micro.examples.helloworld.PojoHelloWorldAgent.class")).get();

		platform.createComponent(new CreationInfo().setFilename("jadex.micro.benchmarks.AgentCreationAgent.class")).get();
		
		//for(int i=0; i<10000; i++)
		//	platform.createComponent(new CreationInfo().setFilenameClass(PojoHelloWorldAgent.class)).get();
		
		//String agentclazz = "jadex.micro.examples.helloworld.PojoHelloWorldAgent";
		
		System.out.println("main end");
	}
	
	public static IFuture<IExternalAccess> createPlatform()
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
	
		// Create necessary platform services
		
		IThreadPool threadpool = new JavaThreadPool(false);
		
		ExecutionService es = new ExecutionService(cid, threadpool);
		es.startService().get();
		reg.addLocalService(es);
		
		ClockService cs = new ClockService(cid, null, threadpool);
		cs.startService().get();
		reg.addLocalService(cs);
		
		LibraryService ls = new LibraryService(cid);
		ls.startService().get();
		reg.addLocalService(ls);
		
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
		//
		
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
	}
	
}
