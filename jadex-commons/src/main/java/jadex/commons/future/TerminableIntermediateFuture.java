package jadex.commons.future;

import jadex.commons.ICommand;
import jadex.commons.IFilter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  Intermediate future that can be terminated from caller side. 
 *  A termination request leads to setException() being 
 *  called with a FutureTerminatedException.
 *  
 *  The future can be supplied with a command that
 *  gets executed if terminate is called.
 */
public class TerminableIntermediateFuture<E> extends IntermediateFuture<E> 
	implements ITerminableIntermediateFuture<E>
{
	//-------- attributes --------
	
	/** The termination code. */
	protected ITerminationCommand terminate;
		
	/** The list of backward commands. */
	protected Map<ICommand<Object>, IFilter<Object>> bcommands;
		
	//-------- constructors --------

	/**
	 *  Create a new future.
	 */
	public TerminableIntermediateFuture()
	{
	}
	
	/**
	 *  Create a future that is already done (failed).
	 *  @param exception	The exception.
	 */
	public TerminableIntermediateFuture(Exception exception)
	{
		super(exception);
	}
	
	/**
	 *  Create a new future.
	 *  @param terminate The runnable to be executed in case of termination.
	 */
	public TerminableIntermediateFuture(ITerminationCommand terminate)
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
		if(!isDone())
		{
			terminate(new FutureTerminatedException());
		}
	}
	
	/**
	 *  Terminate the future and supply a custom reason.
	 */
	public void terminate(Exception reason)
	{
		boolean	term = !isDone() && (terminate==null || terminate.checkTermination(reason));
		
		if(term && setExceptionIfUndone(reason))
		{
			if(terminate!=null)
			{
				terminate.terminated(reason);
			}
		}
	}
	
	/**
	 *  Get the terminate.
	 *  @return The terminate.
	 */
	public ITerminationCommand getTerminationCommand()
	{
		return terminate;
	}

	/**
	 *  Set the terminate.
	 *  @param terminate The terminate to set.
	 */
	public void setTerminationCommand(ITerminationCommand terminate)
	{
		this.terminate = terminate;
	}
	
	/**
	 *  Send a backward command in direction of the source.
	 *  @param info The command info.
	 */
	public void sendBackwardCommand(Object info)
	{
		if(bcommands!=null)
		{
			for(Map.Entry<ICommand<Object>, IFilter<Object>> entry: bcommands.entrySet())
			{
				IFilter<Object> fil = entry.getValue();
				if(fil==null || fil.filter(info))
				{
					ICommand<Object> com = entry.getKey();
					com.execute(info);
				}
			}
		}
	}
	
	/**
	 *  Add a backward command with a filter.
	 *  Whenever the future receives an info it will check all
	 *  registered filters.
	 */
	public void addBackwardCommand(IFilter<Object> filter, ICommand<Object> command)
	{
		if(bcommands==null)
		{
			bcommands = new LinkedHashMap<ICommand<Object>, IFilter<Object>>();
		}
		bcommands.put(command, filter);
	}
	
	/**
	 *  Add a command with a filter.
	 *  Whenever the future receives an info it will check all
	 *  registered filters.
	 */
	public void removeBackwardCommand(ICommand<Object> command)
	{
		if(bcommands!=null)
		{
			bcommands.remove(command);
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
	
}
