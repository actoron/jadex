package jadex.micro.testcases.servicequeries;

import jadex.base.test.TestReport;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

@Service
public interface ITestService
{
	public IFuture<TestReport[]> test();
}
