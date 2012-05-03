package jadex.commons.future;


/**
 *  Future that can be terminated from caller side. 
 *  A termination request leads to setException() being 
 *  called with a FutureTerminatedException.
 *  
 *  The future can be supplied with a command that
 *  gets executed if terminate is called.
 */
public class TerminableFuture<E> extends Future<E> implements ITerminableFuture<E>
{
	//-------- attributes --------
	
	/** The termination code. */
	protected Runnable terminate;
	
	//-------- constructors --------

	/**
	 *  Create a new future.
	 */
	public TerminableFuture()
	{
	}
	
	/**
	 *  Create a future that is already done.
	 *  @param result	The result, if any.
	 */
	public TerminableFuture(Runnable terminate)
	{
		this.terminate = terminate;
	}
	
	//-------- methods --------
	
	/**
	 *  Terminate the future.
	 *  The exception will be set to FutureTerminatedException.
	 */
	public void terminate()
	{
		terminate(new FutureTerminatedException());
	}
	
	/**
	 *  Terminate the future and supply a custom reason.
	 */
	public void terminate(Exception reason)
	{
		if(setExceptionIfUndone(reason))
		{
			if(terminate!=null)
				terminate.run();
		}
	}

//	/**
//	 *  Test if future is terminated.
//	 *  @return True, if terminated.
//	 */
//	public boolean isTerminated()
//	{
//		return isDone() && exception instanceof FutureTerminatedException;
//	}
	
//	/**
//	 *  Main for testing.
//	 */
//	public static void main(String[] args)
//	{
//		Called called = new Called();
//		
//		IResultListener<String> pl = new IResultListener<String>()
//		{
//			public void resultAvailable(String result)
//			{
//				System.out.println("result: "+result);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("ex: "+exception);
//			}
//		};
//		
////		ITerminableFuture<String> fut1 = called.getName();
////		fut1.addResultListener(pl);
//		
////		ITerminableFuture<String> fut2 = called.getName();
////		fut2.addResultListener(pl);
////		fut2.terminate();
//		
//		ITerminableFuture<String> fut3 = called.getName2();
//		fut3.addResultListener(pl);
//		fut3.terminate();
//	}
}

///**
// * 
// */
//class Called 
//{
//	/**
//	 * 
//	 */
//	public ITerminableFuture<String> getName()
//	{
//		final TerminableFuture<String> ret = new TerminableFuture<String>();
//		
//		Thread t = new Thread(new Runnable()
//		{
//			public void run()
//			{
//				try
//				{
//					Thread.sleep(1000);
//				}
//				catch(InterruptedException e)
//				{
//				}
//				System.out.println("setting result");
//				ret.setResultIfUndone("hello world");
//			}
//		});
//		t.start();
//		
//		return ret;
//	}
//	
//	/**
//	 * 
//	 */
//	public ITerminableFuture<String> getName2()
//	{
//		final TerminableDelegationFuture<String> ret = new TerminableDelegationFuture<String>();
//		
////		getName().addResultListener(new DelegationResultListener<String>(ret));
//		ITerminableFuture<String> src = getName();
//		src.addResultListener(new TerminableDelegationResultListener<String>(ret, src));
//		
//		return ret;
//	}
//}
