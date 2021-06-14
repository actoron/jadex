package jadex.bridge.service.component;

import java.util.logging.Logger;

import jadex.bridge.ComponentResultListener;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.commons.ICommand;

/**
 *  Schedule forward future executions (e.g. results) on component thread,
 *  i.e. the component is the callee side of the future.
 */
public class ComponentFutureFunctionality extends FutureFunctionality
{
	//-------- attributes --------
	
	/** The adapter. */
	protected IInternalAccess	access;
	
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public ComponentFutureFunctionality(IInternalAccess access)
	{
		super((Logger)access.getLogger());
		this.access = access;
	}
	
	/**
	 *  Send a foward command.
	 */
	@Override
	public <T> void scheduleForward(final ICommand<T> command, final T args)
	{
		ComponentResultListener.scheduleForward(access, null, new Runnable()
		{
			@Override
			public void run()
			{
				command.execute(args);
			}
			
			@Override
			public String toString()
			{
				return "Command(" + access + ", " + command +", " + args + ")";
			}
		});
	}
}
