package jadex.micro.testcases.nflatency;

import jadex.commons.future.IFuture;

/**
 *  Test interface that has a timeout annotation specifying
 *  the default timeout. In the test the timeout is provided
 *  by the caller via the non-functional properties in the
 *  ServiceCall object (CallLocal).
 */
public interface ITestService
{
	/**
	 *  A test method.
	 */
//	@NFProperties({@NFProperty(ExecutionTimeProperty.class)})
	public IFuture<Void> methodA(long wait);
}
