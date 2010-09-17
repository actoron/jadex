package jadex.bdi.examples.disastermanagement.commander;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.examples.disastermanagement.IClearChemicalsService;
import jadex.bdi.examples.disastermanagement.IExtinguishFireService;
import jadex.bdi.examples.disastermanagement.ITreatVictimsService;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.SServiceProvider;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Handle a disaster by assigning units.
 */
public class HandleDisasterPlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{
		final ISpaceObject disaster = (ISpaceObject)getParameter("disaster").getValue();
		final IGoal	goal	= (IGoal)getReason();
		
		int chemicals = ((Integer)disaster.getProperty("chemicals")).intValue();
		int fire = ((Integer)disaster.getProperty("fire")).intValue();
		int victims = ((Integer)disaster.getProperty("victims")).intValue();
		
		final IBeliefSet busy = getBeliefbase().getBeliefSet("busy_entities");	
		
		if(chemicals>goal.getParameterSet("chemicalunits").getValues().length)
		{
			Collection clearchemser = (Collection)SServiceProvider.getServices(getScope().getServiceProvider(), IClearChemicalsService.class).get(this);
			if(clearchemser.size()>0)
			{
				Iterator it=clearchemser.iterator();
				while(goal.getParameterSet("chemicalunits").getValues().length<chemicals && it.hasNext())
				{
					final IClearChemicalsService ccs = (IClearChemicalsService)it.next();
					final Object provid = ccs.getServiceIdentifier().getProviderId();
					if(!busy.containsFact(provid))
					{
						busy.addFact(provid);
						goal.getParameterSet("chemicalunits").addValue(ccs);
//						System.out.println("Unit assigned: "+provid);
						ccs.clearChemicals(disaster).addResultListener(createResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
//								System.out.println("Unit finished: "+provid);
								busy.removeFact(provid);
								if(!goal.isFinished())
									goal.getParameterSet("chemicalunits").removeValue(ccs);
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
//								System.out.println("Unit exception: "+provid);
								busy.removeFact(provid);
								if(!goal.isFinished())
									goal.getParameterSet("chemicalunits").removeValue(ccs);
							}
						}));
					}
				}
			}
		}
		
		if(fire>goal.getParameterSet("fireunits").getValues().length)
		{
			Collection exfireser = (Collection)SServiceProvider.getServices(getScope().getServiceProvider(), IExtinguishFireService.class).get(this);
			if(exfireser.size()>0)
			{
				Iterator it=exfireser.iterator();
				while(goal.getParameterSet("fireunits").getValues().length<fire && it.hasNext())
				{
					final IExtinguishFireService fes = (IExtinguishFireService)it.next();
					final Object provid = fes.getServiceIdentifier().getProviderId();
					if(!busy.containsFact(provid))
					{
						busy.addFact(provid);
						goal.getParameterSet("fireunits").addValue(fes);
//						System.out.println("Unit assigned: "+provid);
						fes.extinguishFire(disaster).addResultListener(createResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
//								System.out.println("Unit finished: "+provid);
								busy.removeFact(provid);
								if(!goal.isFinished())
									goal.getParameterSet("fireunits").removeValue(fes);
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
//								System.out.println("Unit exception: "+provid);
								busy.removeFact(provid);
								if(!goal.isFinished())
									goal.getParameterSet("fireunits").removeValue(fes);
							}
						}));
					}
				}
			}
		}
		
		if(chemicals==0 && victims>goal.getParameterSet("ambulanceunits").getValues().length)
		{
			Collection treatvicser = (Collection)SServiceProvider.getServices(getScope().getServiceProvider(), ITreatVictimsService.class).get(this);
			if(treatvicser.size()>0)
			{
				Iterator it=treatvicser.iterator();
				while(goal.getParameterSet("ambulanceunits").getValues().length<victims && it.hasNext())
				{
					final ITreatVictimsService tvs = (ITreatVictimsService)it.next();
					final Object provid = tvs.getServiceIdentifier().getProviderId();
					if(!busy.containsFact(provid))
					{
						busy.addFact(provid);
						goal.getParameterSet("ambulanceunits").addValue(tvs);
//						System.out.println("Unit assigned: "+provid);
						tvs.treatVictims(disaster).addResultListener(createResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
//								System.out.println("Unit finished: "+provid+", "+goal.isFinished()+", "+disaster);
								busy.removeFact(provid);
								if(!goal.isFinished())
									goal.getParameterSet("ambulanceunits").removeValue(tvs);
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
//								System.out.println("Unit exception: "+provid);
								busy.removeFact(provid);
								if(!goal.isFinished())
									goal.getParameterSet("ambulanceunits").removeValue(tvs);
							}
						}));
					}
				}
			}
		}
	}
}
