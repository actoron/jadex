package jadex.bdiv3.example.cleanerworld;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.example.cleanerworld.environment.EnvironmentGui;
import jadex.bdiv3.example.cleanerworld.world.Environment;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 * 
 */
@Agent
public class EnvironmentLocalBDI
{
	static
	{
		System.out.println("loading EnvironmentLocalBDI: "+EnvironmentLocalBDI.class.getClassLoader());
	}
	
	@Agent
	protected BDIAgent agent;
	
	protected Environment environment;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
//		System.out.println(EnvironmentLocalBDI.class.getClassLoader());
		System.out.println("body: "+getClass().getClassLoader());
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
