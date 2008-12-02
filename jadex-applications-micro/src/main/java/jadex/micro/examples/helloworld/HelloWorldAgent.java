package jadex.micro.examples.helloworld;

import jadex.bridge.IArgument;
import jadex.microkernel.MicroAgent;
import jadex.microkernel.MicroAgentMetaInfo;

/**
 *  The micro version of the hello world agent.
 */
public class HelloWorldAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The state. */
	protected int step;
	
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public boolean executeAction()
	{
		if(step==0)
		{
			System.out.println(getArgument("welcome text"));
			waitFor(2000);
			step++;
		}
		else if(step==1)
		{
			System.out.println("Good bye world.");
			killAgent();
			step++;
		}
		return false;
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static Object getMetaInfo()
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
		}});
	}
}
