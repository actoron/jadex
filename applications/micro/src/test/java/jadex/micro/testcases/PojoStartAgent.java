package jadex.micro.testcases;


import org.junit.Test;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.OnStart;
import jadex.commons.future.Future;
import jadex.micro.annotation.Agent;

/**
 *  Test component creation from pojos.
 */
@Agent
public class PojoStartAgent
{
	Future<Void>	started	= new Future<>();
	
	/**
	 *  The agent body.
	 */
	@OnStart
	public void body()
	{
		started.setResult(null);
	}
	
	@Agent
	static class InnerPojoAgent	extends PojoStartAgent {}
	
	/**
	 *  Test pojo agent creation.
	 */
	@Test
	public void testPojoCreation()
	{
		long timeout	= 3000;
		IExternalAccess platform = Starter.createPlatform().get(timeout);
		
		// simple pojo
		PojoStartAgent	pojo	= new PojoStartAgent();
		platform.addComponent(pojo).get(timeout);
		pojo.started.get(timeout);
		
		// static inner pojo
		pojo	= new InnerPojoAgent();
		platform.addComponent(pojo).get(timeout);
		pojo.started.get(timeout);
		
		// anonymous pojo not supported?
//		pojo	= new PojoStartAgent() {};
//		platform.addComponent(pojo).get(timeout);
//		pojo.started.get(timeout);

	}
}
