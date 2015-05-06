package jadex.platform.service.dht;

import java.lang.reflect.Method;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.dht.IKVStore;
import jadex.bridge.service.types.dht.IRingNode;
import jadex.commons.SReflect;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Agent
@RequiredServices({
	@RequiredService(name="ringnodes", type=IRingNode.class, multiple = true, binding=@Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL, dynamic = true)),
	@RequiredService(name="storage", type=IKVStore.class, binding=@Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL, dynamic = true)),
	@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM))
})
/**
 * Agent to launch the DHT Viewer interface. 
 */
public class DhtViewerAgent {
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	@AgentCreated
	public void onCreate() {
		Class<?> cl = SReflect.classForName0("jadex.tools.dhtgraph.DhtViewerPanel", null);
		if(cl!=null)
		{
			try
			{
				Method m = cl.getMethod("createFrame", new Class[]{IInternalAccess.class});
				m.invoke(null, new Object[]{agent});
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
