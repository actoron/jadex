package jadex.micro.testcases.stream;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;

import jadex.bridge.IConnection;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.types.context.IContextService;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentServiceSearch;
import jadex.micro.annotation.AgentStreamArrived;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Arguments(@Argument(name="filename", clazz=String.class, defaultvalue="\"copy.copy\""))
@Results(@Result(name="filesize", clazz=long.class))
@RequiredServices({
	@RequiredService(name="contextService", type=IContextService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM))
})
@Agent
public class ReceiverAgent
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentArgument
	protected String filename;
	
	@AgentServiceSearch
	protected IContextService contextService;
	
	/**
	 * 
	 */
	@AgentStreamArrived
	public void streamArrvied(final IConnection con)
	{
		// todo: how to avoid garbage collection of connection?
//		final IInputConnection con = (IInputConnection)msg.get(SFipa.CONTENT);
//		System.out.println("received: "+con+" "+con.hashCode());
		
		receiveBehavior((IInputConnection)con);
	}

	/**
	 * 
	 */
	public void receiveBehavior(IInputConnection con)
	{
		try
		{
			final long[] cnt = new long[1];
			File f = contextService.getFile(filename).get();
			final FileOutputStream fos = new FileOutputStream(f);
			
			ISubscriptionIntermediateFuture<byte[]> fut = ((IInputConnection)con).aread();
			fut.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IIntermediateResultListener<byte[]>()
			{
				public void resultAvailable(Collection<byte[]> result)
				{
//					try
//					{
//						for(Iterator<Byte> it=result.iterator(); it.hasNext(); )
//						{
//							fos.write(it.next().byteValue());
//						}
//					}
//					catch(Exception e)
//					{
//						e.printStackTrace();
//					}
				}
				public void intermediateResultAvailable(byte[] result)
				{
					cnt[0] += result.length;
//					if(cnt[0]%1000==0)
//						System.out.println("bytes: "+cnt[0]);
					try
					{
						fos.write(result);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				public void finished()
				{
					try
					{
//						System.out.println("finished, size: "+cnt[0]);
						fos.close();
						agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("filesize", Long.valueOf(cnt[0]));
						agent.killComponent();
					}
					catch(Exception e)
					{
						agent.killComponent();
//						e.printStackTrace();
					}
				}
				public void exceptionOccurred(Exception exception)
				{
//					System.out.println("ex:"+exception);
					agent.killComponent();
				}
			}));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			agent.killComponent();
		}
	}
	
//	/**
//	 * 
//	 */
//	public void receiveBehavior()
//	{
//		final int[] cnt = new int[1];
//		((IInputConnection)con).aread().addResultListener(new IIntermediateResultListener<Byte>()
//		{
//			public void resultAvailable(Collection<Byte> result)
//			{
//				System.out.println("Result: "+result);
//				cnt[0] += result.size();
//			}
//			public void intermediateResultAvailable(Byte result)
//			{
//				System.out.println("Intermediate result: "+result+" :"+cnt[0]);
//				cnt[0] += 1;
//				if(cnt[0]==5050)
//					((IInputConnection)con).close();
//			}
//			public void finished()
//			{
//				System.out.println("finished: "+cnt[0]);
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("ex:"+exception);
//			}
//		});
//		
////		IComponentStep<Void> step = new IComponentStep<Void>()
////		{
////			public IFuture<Void> execute(IInternalAccess ia)
////			{
////				try
////				{
////					byte[] buffer = new byte[2];
////					con.read(buffer);
////					System.out.println("buffer: "+SUtil.arrayToString(buffer));
//////					int res = con.read();
//////					System.out.println("read: "+res);
////				}
////				catch(Exception e)
////				{
////					agent.killComponent()
////					e.printStackTrace();
////				}
////				agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000, this);
////				return null;
////			}
////		};
////		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000, step);
//	}
	
//	@AgentKilled
//	public void killed()
//	{
//		if(con!=null)
//			con.close();
//	}
}
