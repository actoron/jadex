package jadex.micro.examples.compositeservice;

import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
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
					as.add(1, 1).addResultListener(createResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							System.out.println("add service result: "+result+" "+getComponentIdentifier().getLocalName());
						}
						
						public void exceptionOccurred(Object source, Exception exception)
						{
							System.out.println("invocation failed: "+exception);
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
							System.out.println("Found add service: "+as);
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
