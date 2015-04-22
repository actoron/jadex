package jadex.platform.service.dht;

import java.util.List;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.types.dht.IDebugRingNode;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingNode;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Agent
@ProvidedServices({
	@ProvidedService(name="providedRing", type = IRingNode.class, implementation = @Implementation(RingNode.class), scope = RequiredServiceInfo.SCOPE_GLOBAL),
	@ProvidedService(name="providedDebugRing", type = IDebugRingNode.class, implementation = @Implementation(expression="$component.getComponentFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(\"providedRing\")"), scope = RequiredServiceInfo.SCOPE_GLOBAL),
})
@RequiredServices({
	@RequiredService(name = "ringnodes", type = IRingNode.class, multiple = true, binding=@Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL, dynamic = true))
})
public class RingAgent 
{

//	private RingNode ringNode;
//
//	@Agent
//	private IInternalAccess agent;
//	
//	@AgentCreated
//	public void onCreate() {
//		IRingNode rn = agent.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(IRingNode.class);
//		// cast works because i got this from provided services.
//		System.out.println(rn);
//		ringNode = (RingNode)rn;
//	}
//	
//	@Override
//	public IFuture<Boolean> join(IRingNode other)
//	{
//		return ringNode.join(other);
//	}
//
//	public IFuture<IID> getId()
//	{
//		return ringNode.getId();
//	}
//
//	public IFuture<IRingNode> findSuccessor(IID id)
//	{
//		return ringNode.findSuccessor(id);
//	}
//
//	public IFuture<IRingNode> getSuccessor()
//	{
//		return ringNode.getSuccessor();
//	}
//
//	public IFuture<Void> setPredecessor(IRingNode predecessor)
//	{
//		return ringNode.setPredecessor(predecessor);
//	}
//
//	public IFuture<IRingNode> getClosestPrecedingFinger(IID id)
//	{
//		return ringNode.getClosestPrecedingFinger(id);
//	}
//
//	public IFuture<Void> notify(IRingNode nDash)
//	{
//		return ringNode.notify(nDash);
//	}
//
//	public IFuture<List<IFinger>> getFingers()
//	{
//		return ringNode.getFingers();
//	}
//
//	@Override
//	public IFuture<IComponentIdentifier> getCID()
//	{
//		return ringNode.getCID();
//	}
//
//	@Override
//	public IFuture<IRingNode> getPredecessor()
//	{
//		return ringNode.getPredecessor();
//	}
	
	
}
