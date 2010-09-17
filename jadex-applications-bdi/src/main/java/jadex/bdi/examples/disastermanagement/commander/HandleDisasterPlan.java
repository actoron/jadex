package jadex.bdi.examples.disastermanagement.commander;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.examples.disastermanagement.IClearChemicalsService;
import jadex.bdi.examples.disastermanagement.IExtinguishFireService;
import jadex.bdi.examples.disastermanagement.ITreatVictimsService;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.Plan;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.SServiceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *  Handle a disaster by assigning units.
 */
public class HandleDisasterPlan extends Plan
{
	/** The already assigned fire units. */
	protected List	fireunits;
	
	/** The already assigned chemical units. */
	protected List	chemicalunits;
	
	/** The already assigned ambulance units. */
	protected List	ambulanceunits;
	
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{
		// Keep track of assigned units in case plan gets aborted.
		this.fireunits	= new ArrayList();
		this.chemicalunits	= new ArrayList();
		this.ambulanceunits	= new ArrayList();
		
		final ISpaceObject disaster = (ISpaceObject)getParameter("disaster").getValue();
		
		final IBeliefSet busy = getBeliefbase().getBeliefSet("busy_entities");	
		
		// Plan runs in an endless loop until the goal is achieved and the plan is aborted.
		while(true)
		{
			int chemicals = ((Integer)disaster.getProperty("chemicals")).intValue();
			int fire = ((Integer)disaster.getProperty("fire")).intValue();
			int victims = ((Integer)disaster.getProperty("victims")).intValue();

			if(chemicals>chemicalunits.size())
			{
				Collection clearchemser = (Collection)SServiceProvider.getServices(getScope().getServiceProvider(), IClearChemicalsService.class).get(this);
				if(clearchemser.size()>0)
				{
					Iterator it=clearchemser.iterator();
					while(chemicals>chemicalunits.size() && it.hasNext())
					{
						final IClearChemicalsService ccs = (IClearChemicalsService)it.next();
						final Object provid = ccs.getServiceIdentifier().getProviderId();
						if(!busy.containsFact(provid))
						{
							busy.addFact(provid);
							chemicalunits.add(ccs);
	//						System.out.println("Unit assigned: "+provid);
							ccs.clearChemicals(disaster).addResultListener(createResultListener(new IResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
	//								System.out.println("Unit finished: "+provid);
									busy.removeFact(provid);
									chemicalunits.remove(ccs);
								}
								
								public void exceptionOccurred(Object source, Exception exception)
								{
	//								System.out.println("Unit exception: "+provid);
									busy.removeFact(provid);
									chemicalunits.remove(ccs);
								}
							}));
						}
					}
				}
			}
			
			if(fire>fireunits.size())
			{
				Collection exfireser = (Collection)SServiceProvider.getServices(getScope().getServiceProvider(), IExtinguishFireService.class).get(this);
				if(exfireser.size()>0)
				{
					Iterator it=exfireser.iterator();
					while(fire>fireunits.size() && it.hasNext())
					{
						final IExtinguishFireService fes = (IExtinguishFireService)it.next();
						final Object provid = fes.getServiceIdentifier().getProviderId();
						if(!busy.containsFact(provid))
						{
							busy.addFact(provid);
							fireunits.add(fes);
	//						System.out.println("Unit assigned: "+provid);
							fes.extinguishFire(disaster).addResultListener(createResultListener(new IResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
	//								System.out.println("Unit finished: "+provid);
									busy.removeFact(provid);
									fireunits.remove(fes);
								}
								
								public void exceptionOccurred(Object source, Exception exception)
								{
	//								System.out.println("Unit exception: "+provid);
									busy.removeFact(provid);
									fireunits.remove(fes);
								}
							}));
						}
					}
				}
			}
			
			if(chemicals==0 && victims>ambulanceunits.size())
			{
				Collection treatvicser = (Collection)SServiceProvider.getServices(getScope().getServiceProvider(), ITreatVictimsService.class).get(this);
				if(treatvicser.size()>0)
				{
					Iterator it=treatvicser.iterator();
					while(victims>ambulanceunits.size() && it.hasNext())
					{
						final ITreatVictimsService tvs = (ITreatVictimsService)it.next();
						final Object provid = tvs.getServiceIdentifier().getProviderId();
						if(!busy.containsFact(provid))
						{
							busy.addFact(provid);
							ambulanceunits.add(tvs);
	//						System.out.println("Unit assigned: "+provid);
							tvs.treatVictims(disaster).addResultListener(createResultListener(new IResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
	//								System.out.println("Unit finished: "+provid+", "+goal.isFinished()+", "+disaster);
									busy.removeFact(provid);
									ambulanceunits.remove(tvs);
								}
								
								public void exceptionOccurred(Object source, Exception exception)
								{
	//								System.out.println("Unit exception: "+provid);
									busy.removeFact(provid);
									ambulanceunits.remove(tvs);
								}
							}));
						}
					}
				}
			}
			
			waitFor(1000);	// Wait before looking again for free units.
		}
	}
	
	/**
	 *  Called when the plan is aborted,
	 *  e.g. because the goal is achieved or inactivated.
	 */
	public void	aborted()
	{
		// Abort all units.
		// Use arrays, because collection might be altered by abort().
		
		IExtinguishFireService[]	fus	= (IExtinguishFireService[])fireunits.toArray(new IExtinguishFireService[fireunits.size()]);
		for(int i=0; i<fus.length; i++)
			fus[i].abort();

		IClearChemicalsService[]	ccs	= (IClearChemicalsService[])chemicalunits.toArray(new IClearChemicalsService[chemicalunits.size()]);
		for(int i=0; i<ccs.length; i++)
			ccs[i].abort();

		ITreatVictimsService[]	aus	= (ITreatVictimsService[])ambulanceunits.toArray(new ITreatVictimsService[ambulanceunits.size()]);
		for(int i=0; i<aus.length; i++)
			aus[i].abort();
	}
}
