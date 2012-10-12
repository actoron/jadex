package jadex.bdiv3.runtime;

import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MProcessableElement;
import jadex.bdiv3.model.MTrigger;
import jadex.commons.collection.SCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class APL
{	
	/** The processable element. */
	protected RProcessableElement element;
	
	/** The metagoal. */
//	protected Object apl_has_metagoal;
	
	/** The plan candidates. */
	protected List<MPlan> plancandidates;
	
	/** The plan instance candidates. */
	protected List<RPlan> planinstancecandidates;
	
	/** The waitqueue candidates. */
	protected List<RPlan> waitqueuecandidates;

	protected boolean built;
	
	/**
	 *  Create a new APL.
	 */
	public APL(RProcessableElement element)
	{
		this(element, null);
	}
	
	/**
	 *  Create a new APL.
	 */
	public APL(RProcessableElement element, List<MPlan> plancandidates)
	{
		this.element = element;
		this.plancandidates = plancandidates;
	}

	/**
	 *  Get the plancandidates.
	 *  @return The plancandidates.
	 */
	public List<MPlan> getPlanCandidates()
	{
		return plancandidates;
	}

	/**
	 *  Set the plancandidates.
	 *  @param plancandidates The plancandidates to set.
	 */
	public void setPlanCandidates(List<MPlan> plancandidates)
	{
		this.plancandidates = plancandidates;
	}
	
	/**
	 *  Get the next candidate.
	 */
	public Object getNextCandidate()
	{
		Object ret = null;
		if(plancandidates!=null && plancandidates.size()>0)
		{
			// todo exclude modes
			ret = plancandidates.remove(0);
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public void build(RCapability capa, RProcessableElement element)
	{
		if(!built || ((MProcessableElement)element.getModelElement()).isRebuild())
		{
			// Use the plan priorities to sort the candidates.
			// If the priority is the same use the following order:
			// running plan - waitque of running plan - passive plan
			ArrayList selected = SCollection.createArrayList();

			MProcessableElement mpe = (MProcessableElement)element.getModelElement();
			
			// todo: include a number of retries...
			int numcandidates = 1;
			if(mpe.isPostToAll())
				numcandidates = Integer.MAX_VALUE;

			List<Object> candidatelist = new ArrayList<Object>();
			
			List<MPlan> mplans = ((MCapability)capa.getModelElement()).getPlans();
			for(int i=0; i<mplans.size(); i++)
			{
				MPlan mplan = mplans.get(i);
				MTrigger mtrigger = mplan.getTrigger();
				if(element instanceof RGoal)
				{
					List<MGoal> mgoals = mtrigger.getGoals();
					if(mgoals!=null && mgoals.contains(element.getModelElement()))
					{
						candidatelist.add(mplan);
					}
				}
			}
			
			List<RPlan> rplans = capa.getPlans();
			if(rplans!=null)
			{
				for(RPlan rplan: rplans)
				{
					if(rplan.isWaitingFor(element))
					{
						candidatelist.add(rplan);
					}
				}
			}
			
			// todo waitqueue
			

			boolean random = mpe.isRandomSelection();
			for(int i=0; i<numcandidates && candidatelist.size()>0; i++)
			{
				selected.add(getNextCandidate(candidatelist, random));
			}
		}
	}
	
	//-------- helper methods --------

	/**
	 *  Get the next candidate with respect to the plan
	 *  priority and the rank of the candidate.
	 *  @param candidatelist The candidate list.
	 *  @param random The random selection flag.
	 *  @return The next candidate.
	 */
	public Object getNextCandidate(List<Object> candidatelist, boolean random)
	{
		List<Object> finals = new ArrayList<Object>();
		finals.add(candidatelist.get(0));
		int candprio = getPriority(finals.get(0));
		for(int i=1; i<candidatelist.size(); i++)
		{
			Object tmp = candidatelist.get(i);
			int tmpprio = getPriority(tmp);
			if(tmpprio>candprio
				|| (tmpprio == candprio && getRank(tmp)>getRank(finals.get(0))))
			{
				finals.clear();
				finals.add(tmp);
				candprio = tmpprio;
			}
			else if(tmpprio==candprio && getRank(tmp)==getRank(finals.get(0)))
			{
				finals.add(tmp);
			}
		}

		Object cand;
		if(random)
		{
			int rand = (int)(Math.random()*finals.size());
			cand = finals.get(rand);
			//System.out.println("Random sel: "+finals.size()+" "+rand+" "+cand);
		}
		else
		{
			//System.out.println("First sel: "+finals.size()+" "+0);
			cand = finals.get(0);
		}

		candidatelist.remove(cand);
		return cand;
	}

	/**
	 *  Get the priority of a candidate.
	 *  @return The priority of a candidate.
	 */
	protected static int getPriority(Object cand)
	{
		MPlan mplan;
//		if(cand instanceof RWaitqueuePlan)
//		{
//			Object	rplan	= state.getAttributeValue(cand, OAVBDIRuntimeModel.waitqueuecandidate_has_plan);
//			mplan = state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
//		}
		if(cand instanceof RPlan)
		{
			mplan = (MPlan)((RPlan)cand).getModelElement();
		}
		else 
		{
			mplan = (MPlan)cand;
		}
			
		return mplan.getPriority();
	}

	/**
	 *  Get the rank of a candidate.
	 *  The order is as follows:
	 *  running plan (0) -> waitqueue (1) -> plan instance (2).
	 *  @return The rank of a candidate.
	 */
	protected static int getRank(Object cand)
	{
		int ret;
		
		if(cand instanceof RPlan)
		{
			ret = 2;
		}
//		else if() // waitqueue
//		{
//		}
		else
		{
			ret = 0;
		}
		
		return ret;
	}
}
