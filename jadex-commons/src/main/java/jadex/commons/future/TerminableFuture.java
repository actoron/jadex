package jadex.commons.future;


/**
 * 
 */
public class TerminableFuture<E> extends Future<E> implements ITerminableFuture<E>
{
	/** The termination code. */
	protected Runnable terminate;
	
	/**
	 *  Create a new future.
	 */
	public TerminableFuture()
	{
		super();
	}
	
	/**
	 *  Create a future that is already done.
	 *  @param result	The result, if any.
	 */
	public TerminableFuture(Runnable terminate)
	{
		super();
		this.terminate = terminate;
	}
	
	/**
	 *  Terminate the future.
	 */
	public void terminate()
	{
		setException(new TerminatedException());
	}
	
	/**
	 *  Test if future is terminated.
	 *  @return True, if terminated.
	 */
	public boolean isTerminated()
	{
		if(terminate!=null)
			terminate.run();
		return isDone() && exception instanceof TerminatedException;
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		Called called = new Called();
		
		IResultListener<String> pl = new IResultListener<String>()
		{
			public void resultAvailable(String result)
			{
				System.out.println("result: "+result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception);
			}
		};
		
//		ITerminableFuture<String> fut1 = called.getName();
//		fut1.addResultListener(pl);
		
		ITerminableFuture<String> fut2 = called.getName();
		fut2.addResultListener(pl);
		fut2.terminate();
	}
}

/**
 * 
 */
class Called 
{
	/**
	 * 
	 */
	public ITerminableFuture<String> getName()
	{
		final TerminableFuture<String> ret = new TerminableFuture<String>();
		
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{
				}
				System.out.println("setting result");
				ret.setResultIfUndone("hello world");
			}
		});
		t.start();
		
		return ret;
	}
}
