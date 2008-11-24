package jadex.micro.examples.helloworld;

import jadex.bridge.IArgument;
import jadex.microkernel.MicroAgent;
import jadex.microkernel.MicroAgentMetaInfo;

/**
 *  The micro version of the hello world agent.
 */
public class HelloWorldAgent extends MicroAgent
{
	/**
	 *  Execute an agent step.
	 */
	public boolean executeAction()
	{
		
		System.out.println("Hello world, this is a Jadex micro agent");
		System.out.println(getArgument("text"));
		System.out.println(getConfiguration());
		return false;
	}
	
	/**
	 * 
	 */
	public static Object getMetaInfo()
	{
		return new MicroAgentMetaInfo(new String[]{"c1", "c2"}, new IArgument[]{new IArgument()
		{
			public Object getDefaultValue(String configname)
			{
				return "default value";
			}
			public String getDescription()
			{
				return "This parameter is the text printed by the agent.";
			}
			public String getName()
			{
				return "text";
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
