package jadex.micro.examples.constructorstart;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 * 
 */
@Agent
public class StarterAgent
{
	/**
	 * 
	 */
	public StarterAgent()
	{
		System.out.println("my hashcode: "+this.hashCode());
	}

	/**
	 *  The agent body.
	 */
	@AgentBody
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
		IExternalAccess agent = platform.createComponent(new CreationInfo().setPojo(new StarterAgent())).get();
	}
}
