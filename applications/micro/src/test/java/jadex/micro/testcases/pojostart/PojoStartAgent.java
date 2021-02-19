package jadex.micro.testcases.pojostart;


import jadex.bridge.service.annotation.OnStart;
import jadex.commons.future.Future;
import jadex.micro.annotation.Agent;

/**
 *  Base class for pojo agent.
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
	
	/**
	 *  Instance inner class.
	 */
	@Agent
	public class InnerPojoAgent	extends PojoStartAgent {}
	
	/**
	 *  Static inner class
	 */
	@Agent	// required for finding factory
	static class StaticInnerPojoAgent	extends PojoStartAgent {}
	
}
