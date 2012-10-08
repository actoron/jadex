package jadex.bdiv3.runtime;

import jadex.bdiv3.model.MPlan;

import java.util.List;

/**
 * 
 */
public class APL
{	
	/** The metagoal. */
//	protected Object apl_has_metagoal;
	
	/** The plan candidates. */
	protected List<MPlan> plancandidates;
	
//	/** The plan instance candidates. */
//	protected List<PlanInstance> planinstancecandidates;
	
//	/** The waitqueue candidates. */
//	protected List<> waitqueuecandidates;

	/**
	 *  Create a new APL.
	 */
	public APL(List<MPlan> plancandidates)
	{
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
			ret = plancandidates.remove(0);
		}
		return ret;
	}
}
