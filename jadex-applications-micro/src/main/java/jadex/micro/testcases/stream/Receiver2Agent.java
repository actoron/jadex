package jadex.micro.testcases.stream;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.AgentStreamArrived;

@Agent
public class Receiver2Agent
{
	@Agent
	protected MicroAgent agent;
	
	protected IOutputConnection con;
	
	/**
	 * 
	 */
	@AgentStreamArrived
	public void streamArrvied(final IOutputConnection con)
	{
		// todo: how to avoid garbage collection of connection?
		this.con = (IOutputConnection)con;
		System.out.println("received: "+con+" "+con.hashCode());
		
		final Future<Void> ret = new Future<Void>();
		final IComponentStep<Void> step = new IComponentStep<Void>()
		{
			final int[] cnt = new int[]{1};
			final int max = 100;
			final IComponentStep<Void> self = this;
			public IFuture<Void> execute(IInternalAccess ia)
			{
				byte[] tosend = new byte[cnt[0]];
				for(int i=0; i<cnt[0]; i++)
					tosend[i] = (byte)cnt[0];
				con.write(tosend).addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						if(cnt[0]++<max)
						{
							agent.waitFor(200, self);
						}
						else
						{
							con.close();
//							ret.setResult(null);
						}
					}
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("Write failed: "+exception);
					}
				});
				return IFuture.DONE;
			}
		};
		agent.waitFor(200, step);
	}
	
	@AgentKilled
	public void killed()
	{
		if(con!=null)
			con.close();
	}
}
