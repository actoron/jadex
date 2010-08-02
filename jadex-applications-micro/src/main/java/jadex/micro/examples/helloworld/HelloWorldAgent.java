package jadex.micro.examples.helloworld;

import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

/**
 *  The micro version of the hello world agent.
 */
public class HelloWorldAgent extends MicroAgent
{
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public void executeBody()
	{
		System.out.println(getArgument("welcome text"));
		waitFor(2000, new Runnable()
		{			
			public void run()
			{
				System.out.println("Good bye world.");
				killAgent();
			}
		});
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent prints out a hello message.", 
			new String[]{"c1", "c2"}, 
			new IArgument[]{
			new Argument("welcome text", "This parameter is the text printed by the agent.", "String", "Hello world, this is a Jadex micro agent."),	
			}, null, null, null);
	}
}
