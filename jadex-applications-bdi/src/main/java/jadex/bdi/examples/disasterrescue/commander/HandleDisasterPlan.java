package jadex.bdi.examples.disasterrescue.commander;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.bdi.examples.disasterrescue.IClearChemicalsService;
import jadex.bdi.examples.disasterrescue.IFireExtinguishService;
import jadex.bdi.examples.disasterrescue.ITreatVictimsService;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentManagementService;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.SServiceProvider;

import java.util.Collection;
import java.util.Iterator;

/**
 * 
 */
public class HandleDisasterPlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{
		ISpaceObject disaster = (ISpaceObject)getParameter("disaster").getValue();
		System.out.println("handle disaster: "+disaster);
		
		int chemicals = ((Integer)disaster.getProperty("chemicals")).intValue();
		int fire = ((Integer)disaster.getProperty("fire")).intValue();
		int victims = ((Integer)disaster.getProperty("victims")).intValue();
		
		final IBeliefSet busy = getBeliefbase().getBeliefSet("busy_entities");	
		
		Collection clearchemser = (Collection)SServiceProvider.getServices(getScope().getServiceProvider(), IClearChemicalsService.class).get(this);
		if(chemicals>0 && clearchemser.size()>0)
		{
			for(Iterator it=clearchemser.iterator(); it.hasNext(); )
			{
				IClearChemicalsService ccs = (IClearChemicalsService)it.next();
				final Object provid = ccs.getServiceIdentifier().getProviderId();
				if(!busy.containsFact(provid))
				{
					busy.addFact(provid);
					ccs.clearChemicals(disaster).addResultListener(createResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							busy.removeFact(provid);
						}
						
						public void exceptionOccurred(Object source, Exception exception)
						{
							busy.removeFact(provid);
						}
					}));
				}
			}
		}
		
		Collection exfireser = (Collection)SServiceProvider.getServices(getScope().getServiceProvider(), IFireExtinguishService.class).get(this);
		if(fire>0 && exfireser.size()>0)
		{
			for(Iterator it=exfireser.iterator(); it.hasNext(); )
			{
				IFireExtinguishService fes = (IFireExtinguishService)it.next();
				final Object provid = fes.getServiceIdentifier().getProviderId();
				if(!busy.containsFact(provid))
				{
					busy.addFact(provid);
					fes.extinguishFire(disaster).addResultListener(createResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							busy.removeFact(provid);
						}
						
						public void exceptionOccurred(Object source, Exception exception)
						{
							busy.removeFact(provid);
						}
					}));
				}
			}
		}
		
		Collection treatvicser = (Collection)SServiceProvider.getServices(getScope().getServiceProvider(), ITreatVictimsService.class).get(this);
		if(victims>0 && treatvicser.size()>0)
		{
			for(Iterator it=treatvicser.iterator(); it.hasNext(); )
			{
				ITreatVictimsService tvs = (ITreatVictimsService)it.next();
				final Object provid = tvs.getServiceIdentifier().getProviderId();
				if(!busy.containsFact(provid))
				{
					busy.addFact(provid);
					tvs.treatVictims(disaster).addResultListener(createResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							busy.removeFact(provid);
						}
						
						public void exceptionOccurred(Object source, Exception exception)
						{
							busy.removeFact(provid);
						}
					}));
				}
			}
		}
		
	}
}
