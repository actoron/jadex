package jadex.micro.testcases.syncservices;

import java.util.Collection;

import jadex.bridge.service.annotation.Service;

/**
 * 
 */
@Service
public interface ISynchronousExampleService
{
	/**
	 *  Void method.
	 */
	public void doVoid();
	
	/**
	 *  Int method.
	 */
	public int getInt();
	
	/**
	 *  Collection method.
	 */
	public Collection<String> getCollection();
}
