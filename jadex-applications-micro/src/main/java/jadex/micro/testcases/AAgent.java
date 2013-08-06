package jadex.micro.testcases;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Simple test agent with one service.
 */
@ProvidedServices(@ProvidedService(type=IAService.class, implementation=@Implementation(expression="$component")))
@Results(@Result(name="testcases", clazz=List.class))
@Service(IAService.class)
@Agent
public class AAgent extends MicroAgent implements IAService
{
	/**
	 *  Init service method.
	 */
	public IFuture<Void> test()
	{
		String reason = getComponentAdapter().isExternalThread()? "Wrong thread: "+Thread.currentThread(): null;
		List<TestReport> tests = new ArrayList<TestReport>();
		tests.add(new TestReport("#A1", "Test if service is called on component thread.", !getComponentAdapter().isExternalThread(), reason));
		setResultValue("testcases", tests);
		
//		System.out.println("called service");
		return IFuture.DONE;
	}
}
