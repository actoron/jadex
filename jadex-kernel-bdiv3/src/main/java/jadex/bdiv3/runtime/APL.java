package jadex.bdiv3.runtime;

import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MProcessableElement;
import jadex.bdiv3.model.MTrigger;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class APL
{	
	/** The processable element. */
	protected RProcessableElement element;
	
	/** The list of candidates. */
	protected List<Object> candidates;
	
	/** The metagoal. */
//	protected Object apl_has_metagoal;
	
	/** The mplan candidates. */
	protected List<MPlan> precandidates;
	
//	/** The plan instance candidates. */
//	protected List<RPlan> planinstancecandidates;
	
//	/** The waitqueue candidates. */
//	protected List<RPlan> waitqueuecandidates;
	
	/**
	 *  Create a new APL.
	 */
	public APL(RProcessableElement element)
	{
		this.element = element;
	}
	
//	/**
//	 *  Get the plancandidates.
//	 *  @return The plancandidates.
//	 */
//	public List<MPlan> getPlanCandidates()
//	{
//		return plancandidates;
//	}
//
//	/**
//	 *  Set the plancandidates.
//	 *  @param plancandidates The plancandidates to set.
//	 */
//	public void setPlanCandidates(List<MPlan> plancandidates)
//	{
//		this.plancandidates = plancandidates;
//	}
	
//	/**
//	 *  Get the next candidate.
//	 */
//	public Object getNextCandidate()
//	{
//		Object ret = null;
//		if(plancandidates!=null && plancandidates.size()>0)
//		{
//			// todo exclude modes
//			ret = plancandidates.remove(0);
//		}
//		return ret;
//	}
	
	/**
	 * 
	 */
	public void build(RCapability capa)
	{
		if(candidates==null || ((MProcessableElement)element.getModelElement()).isRebuild())
		{
			if(candidates==null)
				candidates = new ArrayList<Object>();
			
			// Use the plan priorities to sort the candidates.
			// If the priority is the same use the following order:
			// running plan - waitque of running plan - passive plan

//			MProcessableElement mpe = (MProcessableElement)element.getModelElement();
			
			// todo: generate binding candidates
			if(precandidates==null)
			{
				precandidates = new ArrayList<MPlan>();
				List<MPlan> mplans = ((MCapability)capa.getModelElement()).getPlans();
				if(mplans!=null)
				{
					for(int i=0; i<mplans.size(); i++)
					{
						MPlan mplan = mplans.get(i);
						MTrigger mtrigger = mplan.getTrigger();
						if(element instanceof RGoal)
						{
							List<MGoal> mgoals = mtrigger.getGoals();
							if(mgoals!=null && mgoals.contains(element.getModelElement()))
							{
								precandidates.add(mplan);
								candidates.add(mplan);
							}
						}
					}
				}
			}
			else
			{
				candidates.addAll(precandidates);
			}
			
			List<RPlan> rplans = capa.getPlans();
			if(rplans!=null)
			{
				for(RPlan rplan: rplans)
				{
					if(rplan.isWaitingFor(element))
					{
						candidates.add(rplan);
					}
				}
			}
			// todo waitqueue
		}
		else
		{
			// check rplans and waitqueues
			// first remove all rplans that do not wait
			for(Object cand: candidates)
			{
				if(cand instanceof RPlan && !((RPlan)cand).isWaitingFor(element))
				{
					candidates.remove(cand);
				}
			}
			// add new rplans that are not contained already
			List<RPlan> rplans = capa.getPlans();
			if(rplans!=null)
			{
				for(RPlan rplan: rplans)
				{
					if(!candidates.contains(rplan) && rplan.isWaitingFor(element))
					{
						candidates.add(rplan);
					}
				}
			}
		}
	}
	
	//-------- helper methods --------

	/**
	 * 
	 */
	public boolean isEmpty()
	{
		return candidates==null? true: candidates.isEmpty();
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Object> selectCandidates()
	{
		List<Object> ret = new ArrayList<Object>();
		
		MProcessableElement mpe = (MProcessableElement)element.getModelElement();
		// todo: include a number of retries...
		int numcandidates = 1;
		if(mpe.isPostToAll())
		{
			numcandidates = Integer.MAX_VALUE;
		}
		
		for(int i=0; i<numcandidates && candidates.size()>0; i++)
		{
			ret.add(getNextCandidate());
		}
		
		return ret;
	}
	
	/**
	 *  Get the next candidate with respect to the plan
	 *  priority and the rank of the candidate.
	 *  @param candidatelist The candidate list.
	 *  @param random The random selection flag.
	 *  @return The next candidate.
	 */
	public Object getNextCandidate()
	{
		// first find the list of highest ranked candidates
		// then choose one or more of them
		
		List<Object> finals = new ArrayList<Object>();
		finals.add(candidates.get(0));
		int candprio = getPriority(finals.get(0));
		for(int i=1; i<candidates.size(); i++)
		{
			Object tmp = candidates.get(i);
			int tmpprio = getPriority(tmp);
			if(tmpprio>candprio || (tmpprio == candprio && getRank(tmp)>getRank(finals.get(0))))
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
		MProcessableElement mpe = (MProcessableElement)element.getModelElement();
		if(mpe.isRandomSelection())
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
	
	/**
	 * 
	 */
	protected void planFinished(RPlan rplan)
	{
		MProcessableElement mpe = (MProcessableElement)element.getModelElement();
		String exclude = mpe.getExcludeMode();

		// Do nothing is APL is always rebuilt or exclude is never
		if(((MProcessableElement)element.getModelElement()).isRebuild()
			|| MProcessableElement.EXCLUDE_NEVER.equals(exclude))
		{
			return;
		}

		if(exclude.equals(MProcessableElement.EXCLUDE_WHEN_TRIED))
		{
			candidates.remove(rplan.getCandidate());
		}
		else
		{
			String state = rplan.getLifecycleState();
			if(state.equals(RPlan.PLANLIFECYCLESTATE_PASSED)
				&& exclude.equals(MProcessableElement.EXCLUDE_WHEN_SUCCEEDED)
				|| (state.equals(RPlan.PLANLIFECYCLESTATE_FAILED) 
				&& exclude.equals(MProcessableElement.EXCLUDE_WHEN_FAILED)))
			{
				candidates.remove(rplan.getCandidate());
			}
		}
	}
}
