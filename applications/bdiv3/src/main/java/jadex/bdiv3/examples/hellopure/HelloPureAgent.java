package jadex.bdiv3.examples.hellopure;

import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.BDIAgent;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.micro.annotation.Agent;

/**
 *  Simple hello agent that activates a plan based on a belief change.
 *  
 *  Pure BDI agent that is not bytecode enhanced. 
 *  This is achieved by using the baseclass BDIAgent that signals enhancement
 *  has already been done.
 */
@Agent(type=BDIAgentFactory.TYPE)
public class HelloPureAgent extends BDIAgent
{
	/** The text that is printed. */
	@Belief
	private String sayhello;
	
	/**
	 *  The agent body.
	 * /
	@OnStart
	public void body()
	{		
		sayhello = "Hello BDI pure agent V3.";
		beliefChanged("sayhello", null, sayhello);
		System.out.println("body end: "+getClass().getName());
	}*/
	
	/**
	 *  The agent body.
	 */
	@OnStart
	public void body()
	{		
		setBeliefValue("sayhello", "Hello BDI pure agent V3.");
		System.out.println("body end: "+getClass().getName());
	}
	
	@Plan(trigger=@Trigger(factchanged = "sayhello"))
	protected void printHello1()
	{
		System.out.println("plan activated: "+sayhello);
	}
	
	/**
	 *  Start a platform and the example.
	 */
	public static void main(String[] args) 
	{
		IExternalAccess platform = Starter.createPlatform(PlatformConfigurationHandler.getDefaultNoGui()).get();
		CreationInfo ci = new CreationInfo().setFilenameClass(HelloPureAgent.class);
		platform.createComponent(ci).get();
	}
}
