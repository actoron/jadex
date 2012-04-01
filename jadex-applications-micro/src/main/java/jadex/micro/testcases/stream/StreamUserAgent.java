package jadex.micro.testcases.stream;

import jadex.bridge.IInputConnection;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.Collection;

/**
 *  Agent that provides a service with a stream.
 */
@RequiredServices(@RequiredService(name="ss", type=IStreamService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL)))
@Agent
public class StreamUserAgent 
{
	@Agent
	protected MicroAgent agent;
	
	protected IInputConnection icon;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		IFuture<IStreamService> fut = agent.getServiceContainer().getRequiredService("ss");
		fut.addResultListener(new DefaultResultListener<IStreamService>()
		{
			public void resultAvailable(IStreamService ss)
			{
				ss.getInputStream().addResultListener(new IResultListener<IInputConnection>()
				{
					public void resultAvailable(IInputConnection is)
					{
						System.out.println("received icon: "+is);
						StreamUserAgent.this.icon = is;
						is.aread().addResultListener(new IIntermediateResultListener<byte[]>()
						{
							public void resultAvailable(Collection<byte[]> result)
							{
								System.out.println("Result: "+result);
							}
							public void intermediateResultAvailable(byte[] result)
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
					
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("ex: "+exception);
					}
				});
			}
		});
	}
}
