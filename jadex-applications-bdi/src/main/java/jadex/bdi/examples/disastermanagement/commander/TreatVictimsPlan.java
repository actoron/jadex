package jadex.bdi.examples.disastermanagement.commander;

import jadex.bdi.examples.disastermanagement.ITreatVictimsService;
import jadex.bdi.runtime.Plan;
import jadex.commons.future.ITerminableFuture;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 * 
 */
public class TreatVictimsPlan extends Plan
{
	//-------- attributes --------
	
	/** The service future. */
	protected ITerminableFuture<Void>	tv;
	
	//-------- plan methods --------
	
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{
		ISpaceObject disaster = (ISpaceObject)getParameter("disaster").getValue();
		ITreatVictimsService force = (ITreatVictimsService)getParameter("rescueforce").getValue();
		tv	= force.treatVictims(disaster);
		tv.get();
	}
	
	/**
	 *  Called when the plan is aborted.
	 */
	public void aborted()
	{
		if(tv!=null)
		{
			try
			{
				tv.terminate();
			}
			catch(Exception e)
			{
				// Wait until service is finished before superordinated goal is dropped.
				tv.get();
			}
		}
	}
}
