package jadex.micro.testcases.stream;

import jadex.bridge.IConnection;
import jadex.bridge.IInputConnection;
import jadex.commons.future.IIntermediateResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.AgentStreamArrived;

import java.util.Collection;

@Agent
public class ReceiverAgent
{
	@Agent
	protected MicroAgent agent;
	
	protected IInputConnection con;
	
	/**
	 * 
	 */
	@AgentStreamArrived
	public void streamArrvied(IConnection con)
	{
		// todo: how to avoid garbage collection of connection?
//		final IInputConnection con = (IInputConnection)msg.get(SFipa.CONTENT);
		System.out.println("received: "+con+" "+con.hashCode());
		
		((IInputConnection)con).aread().addResultListener(new IIntermediateResultListener<Byte>()
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
		
//		IComponentStep<Void> step = new IComponentStep<Void>()
//		{
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				try
//				{
//					byte[] buffer = new byte[2];
//					con.read(buffer);
//					System.out.println("buffer: "+SUtil.arrayToString(buffer));
////					int res = con.read();
////					System.out.println("read: "+res);
//				}
//				catch(Exception e)
//				{
//					agent.killAgent();
//					e.printStackTrace();
//				}
//				agent.waitFor(1000, this);
//				return null;
//			}
//		};
//		agent.waitFor(1000, step);
	}
	
	@AgentKilled
	public void killed()
	{
		if(con!=null)
			con.close();
	}
}
