package jadex.commons.future;

import java.util.Collection;

/**
 *  A terminable intermediate delegation future can be used when a termination intermediate future 
 *  should be delegated. This kind of future needs to be connected to the
 *  termination source (another delegation or a real future). Termination
 *  calls are forwarded to the termination source. The future remembers
 *  when terminate() was called in unconnected state and forwards the request
 *  as soon as the connection is established.
 */
public class TerminableIntermediateDelegationFuture<E> extends IntermediateFuture<E>
	implements ITerminableIntermediateFuture<E>
{
	//-------- attributes --------
	
	/** The forward/backward handling helper. */
	protected TerminableDelegationFutureHandler<Collection<E>>	handler;
		
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public TerminableIntermediateDelegationFuture()
	{
		this.handler	= new TerminableDelegationFutureHandler<>();
	}
	
	/**
	 *  Create a new future.
	 */
	public TerminableIntermediateDelegationFuture(ITerminableIntermediateFuture<E> src)
	{
		this.handler	= new TerminableDelegationFutureHandler<>();
		delegateFrom(src);
	}
	
	//-------- methods --------
	
	/**
	 *  Set the source.
	 */
	public void setTerminationSource(ITerminableFuture<Collection<E>> src)
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
	public void delegateFrom(IFuture<Collection<E>> source)
	{
		super.delegateFrom(source);
		setTerminationSource((ITerminableFuture<Collection<E>>)source);
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

