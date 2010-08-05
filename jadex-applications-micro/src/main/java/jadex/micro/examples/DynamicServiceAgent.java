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
			public void run()
			{
				addService(new DummyService(getServiceProvider()));
				waitFor(3000, this);
			}
		};
		
		addservice.run();
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
