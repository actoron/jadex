package jadex.micro.testcases.stream;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentMessageArrived;

import java.util.Map;

@Agent
public class Receiver2Agent
{
	@Agent
	protected MicroAgent agent;
	
	protected IOutputConnection icon;
	
	/**
	 * 
	 */
	@AgentMessageArrived
	public void messageArrvied(Map<String, Object> msg, MessageType mt)
	{
		// todo: how to avoid garbage collection of connection?
		icon = (IOutputConnection)msg.get(SFipa.CONTENT);
		System.out.println("received: "+msg+" "+icon.hashCode());
		
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
				icon.write(tosend)
					.addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						if(cnt[0]++<max)
						{
							agent.waitFor(200, self);
						}
						else
						{
							icon.close();
//							ret.setResult(null);
						}
					}
				});
				return IFuture.DONE;
			}
		};
		agent.waitFor(200, step);
	}
}
