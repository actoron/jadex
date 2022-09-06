package jadex.commons.future;

/**
 *  A terminable delegation future can be used when a termination future 
 *  should be delegated. This kind of future needs to be connected to the
 *  termination source (another delegation or a real future). Termination
 *  calls are forwarded to the termination source. The future remembers
 *  when terminate() was called in unconnected state and forwards the request
 *  as soon as the connection is established.
 */
public class TerminableDelegationFuture<E> extends Future<E> implements ITerminableFuture<E>
{
	//-------- attributes --------
	
	/** The forward/backward handling helper. */
	protected TerminableDelegationFutureHandler<E>	handler;
	
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public TerminableDelegationFuture()
	{
//		System.out.println("tfut: "+hashCode());
		this.handler	= new TerminableDelegationFutureHandler<>();
	}
	
	/**
	 *  Create a new future.
	 */
	public TerminableDelegationFuture(ITerminableFuture<E> src)
	{
		this.handler	= new TerminableDelegationFutureHandler<>();
		if(this.getClass().getName().indexOf("DelegatingTerminableDelegationFuture")!=-1)
			System.out.println("func: "+hashCode());
		delegateFrom(src);
	}
	
	//-------- methods --------
	
	/**
	 *  Set the termination source.
	 */
	public void setTerminationSource(ITerminableFuture<E> src)
	{
		handler.setTerminationSource(src);
	}
	
	/**
	 *  Terminate the future.
	 *  The exception will be set to FutureTerminatedException.
	 */
	public void terminate()
	{
		handler.terminate();
	}
	
	/**
	 *  Terminate the future and supply a custom reason.
	 */
	public void terminate(Exception reason)
	{
		handler.terminate(reason);
	}
	
	/**
	 *  Delegate the result and exception from another future.
	 *  @param source The source future.
	 */
	public void delegateFrom(IFuture<E> source)
	{
		super.delegateFrom(source);
		setTerminationSource((ITerminableFuture<E>)source);
	}
	
	/**
	 *  Send a backward command in direction of the source.
	 *  @param info The command info.
	 */
	public void sendBackwardCommand(Object info)
	{
		handler.sendBackwardCommand(info);
	}
}

