package jadex.micro.examples.helloworld;

import jadex.bridge.IArgument;
import jadex.microkernel.MicroAgent;
import jadex.microkernel.MicroAgentMetaInfo;

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
			new String[]{"c1", "c2"}, new IArgument[]{new IArgument()
		{
			public Object getDefaultValue(String configname)
			{
				return "Hello world, this is a Jadex micro agent.";
			}
			public String getDescription()
			{
				return "This parameter is the text printed by the agent.";
			}
			public String getName()
			{
				return "welcome text";
			}
			public String getTypename()
			{
				return "String";
			}
			public boolean validate(String input)
			{
				return true;
			}
		}}, null, null);
	}
}
