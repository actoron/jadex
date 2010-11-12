package jadex.micro.examples.helloworld;

import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
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
		waitFor(2000, new IComponentStep()
		{			
			public Object execute(IInternalAccess args)
			{
				System.out.println("Good bye world.");
				killAgent();
				return null;
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
			null, new IArgument[]{
			new Argument("welcome text", "This parameter is the text printed by the agent.", "String", "Hello world, this is a Jadex micro agent."),	
			}, null);
	}
}
