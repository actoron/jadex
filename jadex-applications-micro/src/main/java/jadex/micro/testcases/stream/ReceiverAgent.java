package jadex.micro.testcases.stream;

import jadex.base.fipa.SFipa;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentMessageArrived;

import java.util.Map;

@Agent
public class ReceiverAgent
{
	@Agent
	protected MicroAgent agent;
	
	/**
	 * 
	 */
	@AgentMessageArrived
	public void messageArrvied(Map<String, Object> msg, MessageType mt)
	{
		System.out.println("received: "+msg);
		final IInputConnection con = (IInputConnection)msg.get(SFipa.CONTENT);
		
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				try
				{
					byte[] buffer = new byte[1];
					con.read(buffer);
					System.out.println("buffer: "+SUtil.arrayToString(buffer));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				agent.waitFor(1000, this);
				return null;
			}
		};
		agent.waitFor(1000, step);
	}
}
