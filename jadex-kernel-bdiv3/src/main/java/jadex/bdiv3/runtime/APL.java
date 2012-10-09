package jadex.bdiv3.runtime;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MPlan;
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
	
	/** The metagoal. */
//	protected Object apl_has_metagoal;
	
	/** The plan candidates. */
	protected List<MPlan> plancandidates;
	
//	/** The plan instance candidates. */
//	protected List<PlanInstance> planinstancecandidates;
	
//	/** The waitqueue candidates. */
//	protected List<> waitqueuecandidates;

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
	public List<MPlan> getPlancandidates()
	{
		return plancandidates;
	}

	/**
	 *  Set the plancandidates.
	 *  @param plancandidates The plancandidates to set.
	 */
	public void setPlancandidates(List<MPlan> plancandidates)
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
	public void build(BDIModel bdimodel)
	{
		if(!built || element.getModelElement().isRebuild())
		{
			plancandidates = new ArrayList<MPlan>();
			
			List<MPlan> mplans = bdimodel.getPlans();
			for(int i=0; i<mplans.size(); i++)
			{
				MPlan mplan = mplans.get(i);
				MTrigger mtrigger = mplan.getTrigger();
				if(element instanceof RGoal)
				{
					List<MGoal> mgoals = mtrigger.getGoals();
					if(mgoals!=null && mgoals.contains(element.getModelElement()))
					{
						plancandidates.add(mplan);
					}
				}
			}
		}
	}
}
