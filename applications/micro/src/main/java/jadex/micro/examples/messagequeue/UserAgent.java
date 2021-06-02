package jadex.micro.examples.messagequeue;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.OnStart;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.OnService;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Example queue user that registers at the queue with a topic and
 *  publishes a number of topics before terminating.
 */
@Agent(predecessors="jadex.micro.examples.messagequeue.MessageQueueAgent")
@RequiredServices(@RequiredService(name="mq", type=IMessageQueueService.class))
@Arguments(@Argument(name="topic", clazz=String.class, defaultvalue="\"default_topic\""))
public class UserAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The message queue. */
	//@AgentServiceSearch
	@OnService
	protected IMessageQueueService mq;
	
	/** The topic argument. */
	@AgentArgument
	protected String topic;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	//@AgentBody
	@OnStart
	public void body()
	{
		final ISubscriptionIntermediateFuture<Event> fut = mq.subscribe(topic);
		fut.addResultListener(new IntermediateDefaultResultListener<Event>()
		{
			public void intermediateResultAvailable(Event event)
			{
				System.out.println("Received: "+agent.getId()+" "+event);
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
				mq.publish(topic, new Event("some type", cnt[0]++, agent.getId()));
				if(cnt[0]<10)
				{
					agent.getFeature(IExecutionFeature.class).waitForDelay(1000, this);
				}
				else
				{
					fut.terminate();
				}
				return IFuture.DONE;
			}
		};
		agent.getFeature(IExecutionFeature.class).waitForDelay(1000, step);
	}
}
