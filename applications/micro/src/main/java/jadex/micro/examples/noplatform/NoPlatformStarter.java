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
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.JavaThreadPool;
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
		ServiceRegistry reg = new ServiceRegistry();
		Starter.putPlatformValue(cid, Starter.DATA_SERVICEREGISTRY, reg);
		Starter.putPlatformValue(cid, Starter.DATA_SERIALIZATIONSERVICES, new SerializationServices(cid));
		
		IPlatformComponentAccess component = SComponentManagementService.createPlatformComponent(NoPlatformStarter.class.getClassLoader());
		IComponentFactory cfac = new MicroAgentFactory("rootid");
		IModelInfo model = cfac.loadModel(agentclazz, null, null).get();
		String ctype = cfac.getComponentType(agentclazz, null, model.getResourceIdentifier()).get();
		CMSComponentDescription desc = new CMSComponentDescription(cid).setType(ctype).setModelName(model.getFullName())
			.setResourceIdentifier(model.getResourceIdentifier()).setCreationTime(System.currentTimeMillis())
			.setFilename(model.getFilename()).setSystemComponent(SComponentManagementService.isSystemComponent(model, null, null));

		IThreadPool threadpool = new JavaThreadPool(false);
		
		// Create necessary platform services
		
		ExecutionService es = new ExecutionService(cid, threadpool);
		es.startService().get();
		reg.addLocalService(es);
		
		ClockService cs = new ClockService(cid, null, threadpool);
		cs.startService().get();
		reg.addLocalService(cs);
		
		ComponentCreationInfo cci = new ComponentCreationInfo(model, null, null, desc, null, null);
		Collection<IComponentFeatureFactory> features = cfac.getComponentFeatures(model).get();
		
		component.create(cci, features);
		component.init().thenAccept(x ->
		{
			System.out.println("Thread: "+Thread.currentThread());
			component.body().get();
			component.shutdown().get();
		});
		
		//component.getInternalAccess().getFeature(type)
	}
}
