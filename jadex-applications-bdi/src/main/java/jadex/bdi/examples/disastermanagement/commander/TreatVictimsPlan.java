package jadex.bdi.examples.disastermanagement.commander;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.examples.disastermanagement.ITreatVictimsService;
import jadex.bdi.runtime.Plan;
import jadex.commons.IFuture;

/**
 * 
 */
public class TreatVictimsPlan extends Plan
{
	//-------- attributes --------
	
	/** The service future. */
	protected IFuture	tv;
	
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
		tv.get(this);
	}
	
	/**
	 *  Called when the plan is aborted.
	 */
	public void aborted()
	{
		if(tv!=null && !tv.isDone())
		{
			ITreatVictimsService force = (ITreatVictimsService)getParameter("rescueforce").getValue();
			try
			{
				force.abort().get(this);
			}
			catch(Exception e)
			{
				// Wait until service is finished before superordinated goal is dropped.
				tv.get(this);
			}
		}
	}
}
