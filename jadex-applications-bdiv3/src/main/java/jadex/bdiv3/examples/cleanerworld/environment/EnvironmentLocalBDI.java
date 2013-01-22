package jadex.bdiv3.examples.cleanerworld.environment;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.examples.cleanerworld.world.Environment;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 * 
 */
@Agent
public class EnvironmentLocalBDI
{
//	static
//	{
//		System.out.println("loading EnvironmentLocalBDI: "+EnvironmentLocalBDI.class.getClassLoader());
//	}
	
	@Agent
	protected BDIAgent agent;
	
	protected Environment environment = Environment.getInstance();
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
//		System.out.println(EnvironmentLocalBDI.class.getClassLoader());
		System.out.println("body: "+getClass().getClassLoader()+" "+agent.getClassLoader());
		EnvironmentGui envgui = new EnvironmentGui(agent.getExternalAccess());
	}
	
	/**
	 * 
	 */
	public Environment getEnvironment()
	{
		return environment;
	}
}
