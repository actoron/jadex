package jadex.bdiv3.examples.cleanerworld.environment;

import javax.swing.SwingUtilities;

import jadex.bdiv3.examples.cleanerworld.world.Environment;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;

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
	protected IInternalAccess agent;
	
	protected Environment environment = Environment.getInstance();
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
//		System.out.println(EnvironmentLocalBDI.class.getClassLoader());
//		System.out.println("body: "+getClass().getClassLoader()+" "+agent.getClassLoader());
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new EnvironmentGui(agent.getExternalAccess());
			}
		});
	}
	
	@AgentKilled
	public void	killed()
	{
		Environment.clearInstance();
	}
	
	/**
	 * 
	 */
	public Environment getEnvironment()
	{
		return environment;
	}
}
