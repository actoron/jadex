package jadex.micro.testcases.servicequeries;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentServiceQuery;

/**
 * 
 */
public class TestService implements ITestService
{
	@AgentArgument
	protected String testarg;
	
	@AgentServiceQuery
	protected ILibraryService libser;
	
	/**
	 *  Dummy test method.
	 */
	public IFuture<TestReport[]> test()
	{
		List<TestReport> res = new ArrayList<>();
		
		TestReport tr = new TestReport("#1", "Test if argument value was injected");
		if("testval".equals(testarg))
			tr.setSucceeded(true);
		else
			tr.setFailed("Testarg is: "+testarg);
		res.add(tr);
		
		final TestReport tr2 = new TestReport("#2", "Test if argument value was injected");
		try
		{
			List<IResourceIdentifier> fut = libser.getAllResourceIdentifiers().get();
			tr2.setSucceeded(true);
			res.add(tr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			tr2.setFailed(e);
			res.add(tr);
		}
		
		System.out.println(res);
		
		return new Future<TestReport[]>(res.toArray(new TestReport[res.size()]));
	}
	
}
