package jadex.bdi.examples.disastermanagement.commander;

import jadex.bdi.examples.disastermanagement.ITreatVictimsService;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.service.annotation.Timeout;
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
		tv	= force.treatVictims(disaster.getId());
		tv.get(Timeout.NONE);	// hack??? treating victims may take longer than default timeout
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
