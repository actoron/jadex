package jadex.platform;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.commons.future.Future;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;

/**
 *  Test argument passing.
 */
public class ArgumentsTest
{
	/**
	 *  Test that more specific arguments have precedence (e.g. -myval vs -myagent.myval).
	 *  Cf. https://git.actoron.com/jadex/jadex/-/issues/2
	 */
	@Test
	public void	testArgumentSpecificity()
	{
		IPlatformConfiguration	config	= STest.getLocalTestConfig(getClass())
			.setValue("before.data", "before")
			.setValue("data", "default")
			.setValue("past.data", "past")
			;
		
		config.addComponent("before:"+ArgumentTestAgent.class.getName()+".class");
		config.addComponent("default:"+ArgumentTestAgent.class.getName()+".class");
		config.addComponent("past:"+ArgumentTestAgent.class.getName()+".class");
		
		IExternalAccess	platform	= Starter.createPlatform(config).get();
		
		check(platform, "before", "before");
		check(platform, "default", "default");
		check(platform, "past", "past");
		
		platform.killComponent().get();
	}
	
	/**
	 *  Check that the data argument has the desired value.
	 */
	protected void	check(IExternalAccess platform, String agent, String expected)
	{
		IExternalAccess	rt	= platform.getExternalAccess(new ComponentIdentifier(agent, platform.getId()));
		String	actual	= rt.scheduleStep(ia -> 
		{
			ArgumentTestAgent	ata	= (ArgumentTestAgent)ia.getFeature(IPojoComponentFeature.class).getPojoAgent();
			return new Future<>(ata.data);
		}).get();
		assertEquals(agent, expected, actual);	
	}
	
	@Agent
	public static class ArgumentTestAgent
	{
		@AgentArgument
		String	data	= "unset";
	}
}
