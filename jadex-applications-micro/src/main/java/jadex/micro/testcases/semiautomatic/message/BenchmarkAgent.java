package jadex.micro.testcases.semiautomatic.message;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.IMessageFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;

/**
 *  An agent that sends messages and prints the roundtrip time.
 */
@Agent
public class BenchmarkAgent
{
	/** The receiver. */
	@AgentArgument
	protected IComponentIdentifier	receiver;
	
	/** The number of messages. */
	@AgentArgument
	protected int	count	= 20000;
	
	/**
	 *  Send the message and wait for the result.
	 */
	@AgentBody
	protected void	run(IMessageFeature mf)
	{
		// Dry run.
		for(int i=0;i<count/100; i++)
		{
			mf.sendMessageAndWait(receiver, "Hello?").get();
		}

		// Now testing.
		long	start	= System.nanoTime();
		for(int i=0;i<count; i++)
		{
			mf.sendMessageAndWait(receiver, "Hello?").get();
		}
		long	delta	= System.nanoTime() - start;

		System.out.println("Message roundtrip took "+delta/count/1000/1000.0+" milliseconds.");
	} 
}
