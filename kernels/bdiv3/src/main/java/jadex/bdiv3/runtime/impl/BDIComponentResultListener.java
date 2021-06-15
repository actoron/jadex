package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.runtime.impl.RPlan.PlanLifecycleState;
import jadex.bdiv3.runtime.impl.RPlan.PlanProcessingState;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IUndoneResultListener;

/**
 *  This listener has the purpose to keep the current plan
 *  in the RPLANS thread local. One problem is that the listener
 *  calls are not async so that RPLANS have to be resetted after
 *  the sync listener call.
 */
public class BDIComponentResultListener<E>	extends ComponentResultListener<E>
{
	/** The plan. */
	protected RPlan rplan;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component result listener.
	 *  @param listener The listener.
	 *  @param adapter The adapter.
	 */
	public BDIComponentResultListener(IResultListener<E> listener, IInternalAccess component)
	{
		super(listener, component);
		this.rplan = RPlan.RPLANS.get();
//		if(rplan==null)
//			System.out.println("ash");
	}
	
	//-------- methods --------

	@Override
	protected void scheduleForward(Runnable notification)
	{
		super.scheduleForward(() -> 
		{
			if(rplan!=null)
			{
				try
				{
	//				System.out.println("plan state was: "+rplan.getProcessingState());
					rplan.setProcessingState(PlanProcessingState.RUNNING);
					assert RPlan.RPLANS.get()==null : RPlan.RPLANS.get()+", "+rplan;
					RPlan.RPLANS.set(rplan);
					
					// TODO: Change to exception when aborted in mean time.
//					if(rplan.isFinishing() && rplan.getLifecycleState()==PlanLifecycleState.BODY)
//					{
//						if(undone && listener instanceof IUndoneResultListener)
//						{
//							((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(new PlanAbortedException());
//						}
//						else
//						{
//							listener.exceptionOccurred(new PlanAbortedException());
//						}			
//					}
//					else
					{
						notification.run();
					}
				}
				finally
				{
					//				System.out.println("setting to null "+this+" "+Thread.currentThread());
					rplan.setProcessingState(PlanProcessingState.WAITING);
					assert RPlan.RPLANS.get()==rplan : RPlan.RPLANS.get()+", "+rplan;
					RPlan.RPLANS.set(null);
				}
			}
			else
			{
				notification.run();
			}
		});
	}
}
