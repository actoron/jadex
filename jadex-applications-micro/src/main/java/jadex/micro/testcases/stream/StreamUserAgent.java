package jadex.micro.testcases.stream;

import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.io.InputStream;

/**
 *  Agent that provides a service with a stream.
 */
@RequiredServices(@RequiredService(name="ss", type=IStreamService.class))
@Agent
public class StreamUserAgent 
{
	@Agent
	protected MicroAgent agent;
	
	/**
	 *  The agent body.
	 */
	public void body()
	{
		IFuture<IStreamService> fut = agent.getServiceContainer().getRequiredService("ss");
		fut.addResultListener(new DefaultResultListener<IStreamService>()
		{
			public void resultAvailable(IStreamService ss)
			{
				ss.getInputStream().addResultListener(new DefaultResultListener<InputStream>()
				{
					public void resultAvailable(InputStream is)
					{
						// warning read is blocking call
						int data;
						try
						{
							while((data = is.read())!=-1)
							{
								System.out.println("Received data: "+data);
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						System.out.println("End of stream reached");
					}
				});
			}
		});
	}
}
