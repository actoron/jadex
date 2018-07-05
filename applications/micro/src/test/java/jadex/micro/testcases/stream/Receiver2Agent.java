package jadex.micro.testcases.stream;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentStreamArrived;

@Agent
public class Receiver2Agent
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentCreated
	public void created()
	{
		agent.getLogger().severe("Agent created: "+agent.getDescription());
	}

	/**
	 * 
	 */
	@AgentStreamArrived
	public void streamArrvied(final IOutputConnection con)
	{
//		System.out.println("received: "+con+" "+con.hashCode());
		
//		final Future<Void> ret = new Future<Void>();
		final IComponentStep<Void> step = new IComponentStep<Void>()
		{
			final int[] cnt = new int[]{1};
			final int max = getMax();
			final IComponentStep<Void> self = this;
			public IFuture<Void> execute(IInternalAccess ia)
			{
				byte[] tosend = new byte[cnt[0]];
				for(int i=0; i<cnt[0]; i++)
					tosend[i] = (byte)cnt[0];
				
//				System.out.println("Writing: "+tosend.length);
				con.write(tosend);
				con.waitForReady().addResultListener(new IResultListener<Integer>()
				{
					public void resultAvailable(Integer result)
					{
						if(cnt[0]++<max)
						{
							agent.getFeature(IExecutionFeature.class).waitForDelay(50, self);
						}
						else
						{
							con.close();
//							ret.setResult(null);
						}
					}
					public void exceptionOccurred(Exception exception)
					{
//						System.out.println("Write failed: "+exception);
					}
				});
				return IFuture.DONE;
			}
		};
		agent.getFeature(IExecutionFeature.class).waitForDelay(200, step);
	}
	
	/**
	 * 
	 */
	public static int getMax()
	{
		return 100;
	}
	
	/**
	 * 
	 */
	public static int getNumberOfBytes()
	{
		return getMax()*(getMax()+1)/2;
	}
	
}
