package jadex.micro.examples.messagequeue;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * 
 */
@Agent
@RequiredServices(@RequiredService(name="mq", type=IMessageQueueService.class))
@Arguments(@Argument(name="topic", clazz=String.class, defaultvalue="\"default_topic\""))
public class UserAgent
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The message queue. */
	@AgentService
	protected IMessageQueueService mq;
	
	/** . */
	@AgentArgument
	protected String topic;
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		final ISubscriptionIntermediateFuture<Event> fut = mq.subscribe(topic);
		fut.addResultListener(new IntermediateDefaultResultListener<Event>()
		{
			public void intermediateResultAvailable(Event event)
			{
				System.out.println("Received: "+agent.getComponentIdentifier()+" "+event);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("Ex: "+exception);
			}
		});
		
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			final int[] cnt = new int[1];
			public IFuture<Void> execute(IInternalAccess ia)
			{
				mq.publish(topic, new Event("some type", cnt[0]++));
				if(cnt[0]<10)
					agent.waitFor(1000, this);
				else
					fut.terminate();
				return IFuture.DONE;
			}
		};
		agent.waitFor(1000, step);
	}
}
