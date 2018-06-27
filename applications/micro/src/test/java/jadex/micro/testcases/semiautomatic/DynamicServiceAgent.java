package jadex.micro.testcases.semiautomatic;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  An agent that dynamically adds services at runtime.
 */
@Agent
public class DynamicServiceAgent
{
	/** The internal access. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Perform the agents actions.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		IComponentStep<Void> addservice = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				agent.getComponentFeature(IProvidedServicesFeature.class).addService("dummyservice", IDummyService.class, new DummyService(agent.getComponentIdentifier()), BasicServiceInvocationHandler.PROXYTYPE_DIRECT);
				agent.getComponentFeature(IExecutionFeature.class).waitForDelay(3000, this);
				return IFuture.DONE;
			}
		};
		
		addservice.execute(agent);
		
		return ret; // never kill?!
	}
	
	public class DummyService	extends BasicService	implements IDummyService
	{
		public DummyService(IComponentIdentifier providerid)
		{
			super(providerid, IDummyService.class, null);
		}
		
		public String toString()
		{
			return getServiceIdentifier().getServiceName();
		}
	}
	
//	@Service
//	public class DummyService2	implements IDummyService
//	{
//	}
	
	public interface IDummyService
	{
		public String	toString();
	}
}
