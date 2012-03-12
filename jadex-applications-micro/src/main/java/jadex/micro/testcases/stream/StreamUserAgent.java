package jadex.micro.testcases.stream;

import jadex.bridge.IInputConnection;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.io.InputStream;
import java.util.Collection;

/**
 *  Agent that provides a service with a stream.
 */
@RequiredServices(@RequiredService(name="ss", type=IStreamService.class))
@Agent
public class StreamUserAgent 
{
	@Agent
	protected MicroAgent agent;
	
	protected IInputConnection icon;
	
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
				ss.getInputStream().addResultListener(new DefaultResultListener<IInputConnection>()
				{
					public void resultAvailable(IInputConnection is)
					{
						StreamUserAgent.this.icon = is;
						is.aread().addResultListener(new IIntermediateResultListener<Byte>()
						{
							public void resultAvailable(Collection<Byte> result)
							{
								System.out.println("Result: "+result);
							}
							public void intermediateResultAvailable(Byte result)
							{
								System.out.println("Intermediate result: "+result);
							}
							public void finished()
							{
								System.out.println("finished");
							}
							public void exceptionOccurred(Exception exception)
							{
								System.out.println("ex:"+exception);
							}
						});
					}
				});
			}
		});
	}
}
