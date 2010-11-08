package jadex.micro.examples.compositeservice;

import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.micro.MicroAgent;

/**
 * 
 */
public class UserAgent extends MicroAgent
{
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public void executeBody()
	{
		SServiceProvider.getService(getServiceProvider(), IAddService.class)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IAddService as = (IAddService)result;
				
				if(as!=null)
				{
					as.add(1, 1).addResultListener(createResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							System.out.println("add service result: "+result+" "+getComponentIdentifier().getLocalName());
						}
					}));
				}
				else
				{
					System.out.println("Did not find add service: "+getComponentIdentifier());
					
					SServiceProvider.getService(getServiceProvider(), IAddService.class)
						.addResultListener(createResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							final IAddService as = (IAddService)result;
						}
					}));
				}
				
//				SServiceProvider.getService(getServiceProvider(), ISubService.class)
//					.addResultListener(createResultListener(new DefaultResultListener()
//				{
//					public void resultAvailable(Object source, Object result)
//					{
						
//					}
//				}));
			}
		}));
	}
}
