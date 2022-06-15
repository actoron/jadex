package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.runtime.impl.RPlan.PlanProcessingState;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IResultListener;

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
		//this.rplan = RPlan.RPLANS.get();
		//System.out.println("rplan set to: "+rplan);
		//Thread.dumpStack();
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
					//System.out.println("in plan state was: "+rplan.getProcessingState()+" "+rplan);
					rplan.setProcessingState(PlanProcessingState.RUNNING);
					if(RPlan.RPLANS.get()!=null)
						System.out.println("bug1: "+ RPlan.RPLANS.get()+" "+rplan);
					//assert RPlan.RPLANS.get()==null : RPlan.RPLANS.get()+", "+rplan;
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
				catch(Error e)
				{
					System.out.println("exception in bdi scheduleForward: "+ e);
					//e.printStackTrace();
				}
				finally
				{
					//System.out.println("out, setting to null "+this+" "+Thread.currentThread()+" "+rplan);
					rplan.setProcessingState(PlanProcessingState.WAITING);
					if(RPlan.RPLANS.get()!=rplan)
						System.out.println("bug2: "+ RPlan.RPLANS.get()+" "+rplan);
					//assert RPlan.RPLANS.get()==rplan : RPlan.RPLANS.get()+", "+rplan+" "+notification;
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
