package jadex.micro.examples;

import jadex.micro.MicroAgent;
import jadex.service.BasicService;
import jadex.service.IServiceProvider;

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
		Runnable	addservice	= new Runnable()
		{
			int cnt	= 0;
			public void run()
			{
				addService(DummyService.class, new DummyService(getServiceProvider(), "DummyService#"+(++cnt)));
				waitFor(3000, this);
			}
		};
		
		addservice.run();
	}
	
	public class DummyService	extends BasicService
	{
		public DummyService(IServiceProvider provider, String name)
		{
			super(BasicService.createServiceIdentifier(provider.getId(), name));
		}
		
		public String toString()
		{
			return getServiceIdentifier().getServiceName();
		}
	}
}
