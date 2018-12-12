package jadex.bdi.examples.cleanerworld_classic.cleaner;

import java.util.List;

import jadex.bdi.examples.cleanerworld_classic.Chargingstation;
import jadex.bdi.examples.cleanerworld_classic.Cleaner;
import jadex.bdi.examples.cleanerworld_classic.Location;
import jadex.bdi.examples.cleanerworld_classic.Vision;
import jadex.bdi.examples.cleanerworld_classic.Waste;
import jadex.bdi.examples.cleanerworld_classic.Wastebin;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.IBeliefSet;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.SUtil;

/**
 *  Move to a point.
 */
public class MoveToLocationPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		//long	time	= getRootGoal().getExecutionTime();
		Location target = (Location)getParameter("location").getValue();
		Location myloc = (Location)getBeliefbase().getBelief("my_location").getFact();
		while(!myloc.isNear(target))
		{
			//pause(atm);
			// calculate the new position offset.
			//long	newtime	= getTime();
			double speed = ((Double)getBeliefbase().getBelief("my_speed").getFact()).doubleValue();
			double d = myloc.getDistance(target);
			double r = speed*0.00004*100;//(newtime-time);
			double dx = target.getX()-myloc.getX();
			double dy = target.getY()-myloc.getY();
			//time	= newtime;

			// Alter the charge state
			double	charge	= ((Double)getBeliefbase().getBelief("my_chargestate").getFact()).doubleValue();
			charge	-= r*0.075;
			if(charge<0)
			{
				throw new RuntimeException("Out of battery.");
			}
			getBeliefbase().getBelief("my_chargestate").setFact(Double.valueOf(charge));

			// When radius greater than distance, just move a step.
			double rx = r<d? r*dx/d: dx;
			double ry = r<d? r*dy/d: dy;
			getBeliefbase().getBelief("my_location").setFact(new Location(myloc.getX()+rx, myloc.getY()+ry));

			waitFor(100); // wait for 0.01 seconds

			// Check if location has changed in mean time.
			myloc = (Location)getBeliefbase().getBelief("my_location").getFact();

			updateVision();
		}
	}

	//-------- helper methods --------

	/**
	 *  Update the vision, when having moved.
	 */
	protected void	updateVision()
	{		
//		long start = System.currentTimeMillis();
		
		// Create a representation of myself.
		Cleaner cl = new Cleaner((Location)getBeliefbase().getBelief("my_location").getFact(),
			getComponentName(),
			(Waste)getBeliefbase().getBelief("carriedwaste").getFact(),
			((Number)getBeliefbase().getBelief("my_vision").getFact()).doubleValue(),
			((Number)getBeliefbase().getBelief("my_chargestate").getFact()).doubleValue());

		//IEnvironment env = (IEnvironment)getBeliefbase().getBelief("environment").getFact();
		//Vision vi = env.getVision(cl);
		IGoal dg = createGoal("get_vision_action");
		dispatchSubgoalAndWait(dg);

		//Vision vi = (Vision)dg.getResult();
		Vision vi = (Vision)dg.getParameter("vision").getValue();

		if(vi!=null)
		{
			getBeliefbase().getBelief("daytime").setFact(Boolean.valueOf(vi.isDaytime()));
			
			Waste[] ws = vi.getWastes();
			Wastebin[] wbs = vi.getWastebins();
			Chargingstation[] cs = vi.getStations();
			Cleaner[] cls = vi.getCleaners();

			// When an object is not seen any longer (not
			// in actualvision, but in (near) beliefs), remove it.
			List known = (List)getExpression("query_in_vision_objects").execute();
			for(int i=0; i<known.size(); i++)
			{
				Object object = known.get(i);
				if(object instanceof Waste)
				{
					List tmp = SUtil.arrayToList(ws);
					if(!tmp.contains(object))
						getBeliefbase().getBeliefSet("wastes").removeFact(object);
				}
				else if(object instanceof Wastebin)
				{
					List tmp = SUtil.arrayToList(wbs);
					if(!tmp.contains(object))
						getBeliefbase().getBeliefSet("wastebins").removeFact(object);
				}
				else if(object instanceof Chargingstation)
				{
					List tmp = SUtil.arrayToList(cs);
					if(!tmp.contains(object))
						getBeliefbase().getBeliefSet("chargingstations").removeFact(object);
				}
				else if(object instanceof Cleaner)
				{
					List tmp = SUtil.arrayToList(cls);
					if(!tmp.contains(object))
						getBeliefbase().getBeliefSet("cleaners").removeFact(object);
				}
			}

			// Add new or changed objects to beliefs.
			for(int i=0; i<ws.length; i++)
			{
				if(!getBeliefbase().getBeliefSet("wastes").containsFact(ws[i]))
					getBeliefbase().getBeliefSet("wastes").addFact(ws[i]);
			}
			for(int i=0; i<wbs.length; i++)
			{
				// Remove contained wastes from knowledge.
				// Otherwise the agent might think that the waste is still
				// somewhere (outside its vision) and then it creates lots of
				// cleanup goals, that are instantly achieved because the
				// target condition (waste in wastebin) holds.
				Waste[]	wastes	= wbs[i].getWastes();
				for(int j=0; j<wastes.length; j++)
				{
					if(getBeliefbase().getBeliefSet("wastes").containsFact(wastes[j]))
						getBeliefbase().getBeliefSet("wastes").removeFact(wastes[j]);
				}

				// Now its safe to add wastebin to beliefs.
				IBeliefSet bs = getBeliefbase().getBeliefSet("wastebins");
				if(bs.containsFact(wbs[i]))
				{
//					bs.updateFact(wbs[i]);
					Wastebin wb = (Wastebin)bs.getFact(wbs[i]);
					wb.update(wbs[i]);
				}
				else
				{
					bs.addFact(wbs[i]);
				}
				//getBeliefbase().getBeliefSet("wastebins").updateOrAddFact(wbs[i]);
			}
			for(int i=0; i<cs.length; i++)
			{
				IBeliefSet bs = getBeliefbase().getBeliefSet("chargingstations");
				if(bs.containsFact(cs[i]))
				{
//					bs.updateFact(cs[i]);
					Chargingstation stat = (Chargingstation)bs.getFact(cs[i]);
					stat.update(cs[i]);
				}
				else
				{
					bs.addFact(cs[i]);
				}
				//getBeliefbase().getBeliefSet("chargingstations").updateOrAddFact(cs[i]);
			}
			for(int i=0; i<cls.length; i++)
			{
				if(!cls[i].equals(cl))
				{
					IBeliefSet bs = getBeliefbase().getBeliefSet("cleaners");
					if(bs.containsFact(cls[i]))
					{
//						bs.updateFact(cls[i]);
						Cleaner clea = (Cleaner)bs.getFact(cls[i]);
						clea.update(cls[i]);
					}
					else
					{
						bs.addFact(cls[i]);
					}
					//getBeliefbase().getBeliefSet("cleaners").updateOrAddFact(cls[i]);
				}
			}

			//getBeliefbase().getBelief("???").setFact("allowed_to_move", new Boolean(true));
		}
		else
		{
			//System.out.println("Error when updating vision! "+event.getGoal());
			System.out.println(getComponentName()+" Error when updating vision! ");
		}
		
//		System.out.println("update vision: "+(System.currentTimeMillis()-start));
	}
	
//	public void aborted()
//	{
//		System.out.println("Aborted: "+this);
//	}
//	
//	public void failed()
//	{
//		System.out.println("Failed: "+this);
//	}
}
