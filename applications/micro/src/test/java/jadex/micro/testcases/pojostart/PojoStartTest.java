package jadex.micro.testcases.pojostart;

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
	
	/**
	 *  Test pojo agent creation.
	 */
	@Test
	public void testPojoCreation()
	{
		long timeout	= 3000;
		IExternalAccess platform = Starter.createPlatform(STest.getLocalTestConfig(getClass())).get(timeout, true);
		
		// simple pojo
		PojoStartAgent	pojo	= new PojoStartAgent();
		platform.addComponent(pojo).get(timeout, true);
		pojo.started.get(timeout);
		
		// dynamic inner pojo
		pojo	= pojo.new InnerPojoAgent();
		platform.addComponent(pojo).get(timeout, true);
		pojo.started.get(timeout);
		
		// static inner pojo
		pojo	= new StaticInnerPojoAgent();
		platform.addComponent(pojo).get(timeout, true);
		pojo.started.get(timeout);
		
		// anonymous pojo not supported?
//		pojo	= new PojoStartAgent() {};
//		platform.addComponent(pojo).get(timeout, true);
//		pojo.started.get(timeout);

	}

}
