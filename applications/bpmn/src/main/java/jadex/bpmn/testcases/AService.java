package jadex.bpmn.testcases;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
@Service
public class AService implements IAService
{
	/** The test string. */
	protected String test;
	
	/**
	 *  Create a new service.
	 */
	public AService() 
	{
		this.test = "no value";
	}
	
	/**
	 *  Create a new service.
	 */
	public AService(String test) 
	{
		this.test = test;
	}

	/**
	 *  Test method.
	 */
	public IFuture<String> test()
	{
		System.out.println("val: "+test);
		return new Future<String>(test);
	}
}
