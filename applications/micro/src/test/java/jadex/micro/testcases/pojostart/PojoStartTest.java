package jadex.micro.testcases.pojostart;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.micro.testcases.pojostart.PojoStartAgent.StaticInnerPojoAgent;

/**
 *  Contains test methods.
 */
public class PojoStartTest
{
	IExternalAccess platform;
	
	@Before
	public void	setup()
	{
		platform = Starter.createPlatform(STest.createRealtimeTestConfig(getClass())).get();
	}
	
	@After
	public void	tearDown()
	{
		platform.killComponent().get();
	}
	
	/**
	 *  Test pojo agent creation.
	 */
	@Test
	public void testPojoCreation()
	{
		// simple pojo
		PojoStartAgent	pojo	= new PojoStartAgent();
		platform.addComponent(pojo).get();
		pojo.started.get();
		
		// dynamic inner pojo
		pojo	= pojo.new InnerPojoAgent();
		platform.addComponent(pojo).get();
		pojo.started.get();
		
		// static inner pojo
		pojo	= new StaticInnerPojoAgent();
		platform.addComponent(pojo).get();
		pojo.started.get();
	}

	/**
	 *  Test anonymous pojo agent creation.
	 */
	@Test
	@Ignore
	// anonymous pojo not supported due to missing @Agent annotation?
	public void testAnonymousPojoCreation()
	{
		PojoStartAgent	pojo	= new PojoStartAgent() {};
		platform.addComponent(pojo).get();
		pojo.started.get();
	}
}
