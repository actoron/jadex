package jadex.micro.testcases.stream;

import jadex.base.test.TestReport;
import jadex.commons.future.Future;
import jadex.commons.future.IResultListener;

/**
 * 
 */
public class TestReportListener implements IResultListener<Long>
{
	protected TestReport tr;
	
	protected Future<TestReport> delegate;
	
	protected long length;
	
	/**
	 * 
	 */
	public TestReportListener(TestReport tr, Future<TestReport> delegate, long length)
	{
		this.tr = tr;
		this.delegate = delegate;
		this.length = length;
	}

	/**
	 * 
	 */
	public void resultAvailable(Long result)
	{
		if(result.longValue()==length)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setFailed("Wrong length (current / expected): ("+result+" / "+StreamProviderAgent.getWriteLength()+")");
		}
		delegate.setResult(tr);
	}

	/**
	 * 
	 */
	public void exceptionOccurred(Exception exception)
	{
		System.out.println("ex: "+exception);
		tr.setFailed("Exception: "+exception.getMessage());
		delegate.setResult(tr);
	}
}