package jadex.bridge;

import jadex.commons.concurrent.IResultListener;
import jadex.service.clock.ITimedObject;

/**
 *  This timed object ensures that timed objects are executed
 *  correctly within the interpreter.
 */
public class InterpreterTimedObject implements ITimedObject
{
	//-------- attributes --------
		
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	/** The runnable. */
	protected CheckedAction action;
	
	/** The component execution service. */
	protected IComponentExecutionService ces;

	/** The component identifier. */
	protected IComponentIdentifier cid;
	
	//-------- constructors --------
	
	/**
	 *  Create a new timed object.
	 */
	public InterpreterTimedObject(IComponentAdapter adapter, CheckedAction runnable)
	{
		this.adapter = adapter;
		this.action = runnable;
		this.ces = (IComponentExecutionService)adapter.getServiceContainer().getService(IComponentExecutionService.class);
		this.cid = adapter.getComponentIdentifier();
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the submitted timepoint was reached.
	 *  // todo: will be enhanced with a TimerEvent when
	 *  // we enhance the time service 
	 */
	public void timeEventOccurred(long currenttime)
	{
		ces.getComponentDescription(cid, new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentDescription desc = (IComponentDescription)result;
				if(desc!=null && !IComponentDescription.STATE_TERMINATED.equals(desc.getState()))
				{
					try
					{
						adapter.invokeLater(action);
					}
					catch(ComponentTerminatedException e)
					{
					}
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
			}
		});
		
		// else agent was terminated
	}
	
	/**
	 *  Get the action.
	 *  @return The action.
	 */
	public CheckedAction getAction()
	{
		return action;
	}

	/**
	 *  Get a string representation.
	 */
	public String	toString()
	{
		return adapter.getComponentIdentifier().getLocalName() + ": " + action; 
	}
}