package jadex.micro.testcases.distributedservicepool;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 * 
 */
@Service
public interface ITestService
{
	public IFuture<Void> ok();
	
	public IFuture<Void> ex();
	
	public IIntermediateFuture<String> inter();

	public IIntermediateFuture<String> interex();
}
