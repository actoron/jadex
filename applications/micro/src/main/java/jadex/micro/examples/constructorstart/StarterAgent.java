package jadex.micro.examples.constructorstart;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.OnStart;
import jadex.micro.annotation.Agent;

/**
 *  This example shows how to create an agent via the pojo agent
 *  instead of the class/filename of the agent.
 *  
 *  platform.addComponent(pojo) can be used for that purpose 
 *  instead of platform.createComponent(info/class).
 */
@Agent
public class StarterAgent
{
	/**
	 *  Create a new pojo agent.
	 */
	public StarterAgent()
	{
		System.out.println("my hashcode: "+this.hashCode());
	}

	/**
	 *  The agent body.
	 */
	@OnStart
	public void body()
	{
		System.out.println("body");
		System.out.println("my hashcode: "+this.hashCode());
	}
	
	/**
	 *  Main for testing.
	 *  @param args The arguments.
	 */
	public static void main(String[] args)
	{
		IExternalAccess platform = Starter.createPlatform().get();
		IExternalAccess agent = platform.addComponent(new StarterAgent()).get();
	}
}
