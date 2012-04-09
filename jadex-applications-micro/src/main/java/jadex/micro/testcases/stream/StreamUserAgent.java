package jadex.micro.testcases.stream;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
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
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();

		IFuture<IStreamService> fut = agent.getServiceContainer().getRequiredService("ss");
		fut.addResultListener(new ExceptionDelegationResultListener<IStreamService, Void>(ret)
		{
			public void customResultAvailable(final IStreamService ss)
			{
				testInputStream(ss).addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						testOutputStream(ss).addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
		});
		
		return ret;
	}
	
	protected IFuture<Void> testInputStream(IStreamService ss)
	{
		final Future<Void> ret = new Future<Void>();
		
		ss.getInputStream().addResultListener(new IResultListener<IInputConnection>()
		{
			public void resultAvailable(IInputConnection is)
			{
				System.out.println("received icon: "+is);
				final long[] size = new long[1];
				is.aread().addResultListener(new IIntermediateResultListener<byte[]>()
				{
					public void resultAvailable(Collection<byte[]> result)
					{
						System.out.println("Result: "+result);
						ret.setResult(null);
					}
					public void intermediateResultAvailable(byte[] result)
					{
						size[0] += result.length;
						System.out.println("Intermediate result: "+SUtil.arrayToString(result));
					}
					public void finished()
					{
						System.out.println("finished, size: "+size[0]);
						ret.setResult(null);
					}
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("ex:"+exception);
						ret.setResult(null);
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception);
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	protected IFuture<Void> testOutputStream(IStreamService ss)
	{
		final Future<Void> ret = new Future<Void>();
		
		ss.getOutputStream().addResultListener(new IResultListener<IOutputConnection>()
		{
			public void resultAvailable(final IOutputConnection oc)
			{
				System.out.println("received ocon: "+oc);
				
				final int[] cnt = new int[]{0};
				IComponentStep<Void> step = new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						oc.write(new byte[]{(byte)cnt[0]});
						if(cnt[0]++<50)
						{
							agent.waitFor(100, this);
						}
						else
						{
							oc.close();
							ret.setResult(null);
						}
						return IFuture.DONE;
					}
				};
				
				agent.waitFor(1000, step);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception);
				ret.setException(exception);
			}
		});
		
		return ret;
	}
}
