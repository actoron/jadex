package jadex.micro.testcases.semiautomatic.message;

import java.io.IOException;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.service.annotation.OnStart;
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
	//@AgentBody
	@OnStart
	protected void	run(IMessageFeature mf)
	{
		// Dry run.
		for(int i=0;i<count/100; i++)
		{
			mf.sendMessageAndWait(receiver, "Hello?").get();
		}
		
		// To start profiling after setup.
		try
		{
			System.out.println("Press [ANY] key to start...");
			System.in.read();
		}
		catch(IOException e)
		{
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
