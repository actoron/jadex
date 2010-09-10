package jadex.bdi.examples.disasterrescue.commander;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.examples.disasterrescue.IClearChemicalsService;
import jadex.bdi.examples.disasterrescue.IExtinguishFireService;
import jadex.bdi.examples.disasterrescue.ITreatVictimsService;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.Plan;
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
		
		if(chemicals>0)
		{
			Collection clearchemser = (Collection)SServiceProvider.getServices(getScope().getServiceProvider(), IClearChemicalsService.class).get(this);
			if(clearchemser.size()>0)
			{
				Iterator it=clearchemser.iterator();
				for(int i=0; i<chemicals && it.hasNext(); i++)
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
		}
		
		if(fire>0)
		{
			Collection exfireser = (Collection)SServiceProvider.getServices(getScope().getServiceProvider(), IExtinguishFireService.class).get(this);
			if(exfireser.size()>0)
			{
				Iterator it=exfireser.iterator();
				for(int i=0; i<fire && it.hasNext(); i++)
				{
					IExtinguishFireService fes = (IExtinguishFireService)it.next();
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
		}
		
		if(victims>0)
		{
			Collection treatvicser = (Collection)SServiceProvider.getServices(getScope().getServiceProvider(), ITreatVictimsService.class).get(this);
			if(treatvicser.size()>0)
			{
				Iterator it=treatvicser.iterator();
				for(int i=0; i<victims && it.hasNext(); i++)
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
}
