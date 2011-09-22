package jadex.micro.testcases.semiautomatic;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;

/**
 *  An agent that dynamically adds services at runtime.
 */
public class DynamicServiceAgent extends MicroAgent
{
	/**
	 *  Perform the agents actions.
	 */
	public void executeBody()
	{
		IComponentStep<Void> addservice = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				addService("dummyservice", IDummyService.class, new DummyService(getServiceContainer()), BasicServiceInvocationHandler.PROXYTYPE_DIRECT);
				waitFor(3000, this);
				return IFuture.DONE;
			}
		};
		
		addservice.execute(this);
	}
	
	public class DummyService	extends BasicService	implements IDummyService
	{
		public DummyService(IServiceProvider provider)
		{
			super(provider.getId(), IDummyService.class, null);
		}
		
		public String toString()
		{
			return getServiceIdentifier().getServiceName();
		}
	}
	
	public interface IDummyService
	{
		public String	toString();
	}
}
