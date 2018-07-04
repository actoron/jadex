package jadex.platform.service.awareness.discovery.intravm;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.awareness.IDiscoveryService;
import jadex.commons.Boolean3;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.AgentServiceSearch;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;

/**
 *  Simple agent providing intra vm discovery for scalability tests.
 *  Do not use in production! (no cleanup performed)
 */
@Agent(autoprovide=Boolean3.TRUE)
@Service
@ProvidedServices(@ProvidedService(type=IDiscoveryService.class, scope=Binding.SCOPE_PLATFORM))
public class IntraVMDiscoveryAgent implements IDiscoveryService
{
	/** The started discovery agents. */
	protected static final Set<IntraVMDiscoveryAgent>	discoveries	= Collections.synchronizedSet(new HashSet<IntraVMDiscoveryAgent>());
	
	//-------- attributes --------
	
	/** Awareness manager receives found platforms. */
	@AgentServiceSearch(requiredservice=@RequiredService(name="awa", type=IAwarenessManagementService.class))
	protected IAwarenessManagementService	awa;
	
	/** The agent access. */
	@Agent
	protected IInternalAccess	agent;
	
	/** Running flag required for deregistration. */
	protected boolean	running	= true;
	
	//-------- lifecycle --------
	
	/**
	 *  Init.
	 */
	@AgentCreated
	protected IFuture<Void>	init()
	{
		discoveries.add(this);
		return sendInfos();
	}
	
	/**
	 *  Shutdown.
	 */
	@AgentKilled
	protected IFuture<Void>	shutdown()
	{
		running	= false;
		discoveries.remove(this);
		return sendInfos();
	}
	
	//-------- intra vm "communication" --------
	
	/**
	 *  Notification of others about new or changes info.
	 *  @return Info of this agent for immediate bidirectional connection.
	 */
	protected IFuture<AwarenessInfo>	infoAvailable(final AwarenessInfo info)
	{
		return agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<AwarenessInfo>()
		{
			@Override
			public IFuture<AwarenessInfo> execute(IInternalAccess ia)
			{
				awa.addAwarenessInfo(info);
				return new Future<AwarenessInfo>(getAwarenessInfo());
			}
		});
	}
	
	//-------- IDiscoveryService interface --------
	
	/**
	 *  Set the send delay.
	 *  @param delay The delay.
	 */
	public void setDelay(long delay)
	{
		
	}
	
	/**
	 *  Set the fast awareness flag.
	 *  @param fast The fast flag.
	 */
	public void setFast(boolean fast)
	{
		
	}
	
	/**
	 *  Set the includes.
	 *  @param includes The includes.
	 */
	public void setIncludes(String[] includes)
	{
		
	}
	
	/**
	 *  Set the excludes.
	 *  @param excludes The excludes.
	 */
	public void setExcludes(String[] excludes)
	{
		
	}
	
	/**
	 *  Republish the awareness info.
	 *  Called when some important property has changed, e.g. platform addresses.
	 */
	public void republish()
	{
		sendInfos();
	}

	protected IFuture<Void> sendInfos()
	{
		FutureBarrier<Void>	fubar	= new FutureBarrier<Void>();
		IntraVMDiscoveryAgent[]	discos	= discoveries.toArray(new IntraVMDiscoveryAgent[0]);
		AwarenessInfo info = getAwarenessInfo();

		for(IntraVMDiscoveryAgent disco: discos)
		{
			if(disco!=this)
			{
				final Future<Void>	done	= new Future<Void>();
				fubar.addFuture(done);
//				System.out.println("sendInfo: "+agent+" to "+disco.agent+", "+info);
				disco.infoAvailable(info).addResultListener(
					agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<AwarenessInfo, Void>(done)
				{
					@Override
					public void customResultAvailable(final AwarenessInfo otherinfo) throws Exception
					{
						awa.addAwarenessInfo(otherinfo).addResultListener(new ExceptionDelegationResultListener<Boolean, Void>(done)
						{
							@Override
							public void customResultAvailable(Boolean result) throws Exception
							{
//								System.out.println("sentInfo: "+agent+", received: "+otherinfo);
								done.setResult(null);
							}
						});
					}
				}));
			}
		}
		return fubar.waitFor();
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the current awareness info.
	 */
	protected AwarenessInfo getAwarenessInfo()
	{
		IComponentIdentifier root = agent.getComponentIdentifier().getRoot();
		List<TransportAddress> addr = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( ITransportAddressService.class)).getAddresses().get();
		AwarenessInfo info = new AwarenessInfo(root, addr, running ? AwarenessInfo.STATE_ONLINE : AwarenessInfo.STATE_OFFLINE, Timeout.NONE, null, null, null, "IntraVM");
		return info;
	}
}
