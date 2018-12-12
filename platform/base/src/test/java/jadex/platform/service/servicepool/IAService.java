package jadex.platform.service.servicepool;

import java.util.Map;

import jadex.base.test.TestReport;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 *  Example service a.
 */
@Service
public interface IAService
{
	/**
	 *  Example method 1.
	 */
	public IFuture<String> ma1(String str); 
	
	/**
	 *  Example method 2.
	 */
	public IIntermediateFuture<Integer> ma2();

	/**
	 *  Example method 3 (for non func).
	 */
	public IFuture<TestReport> ma3(Map<String, Object> tprops); 

}
