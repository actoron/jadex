package jadex.gpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MProcess extends MNamedIdElement
{
	//-------- attributes --------
	
	/** The goals. */
	protected List goals;
	
	/** The plans. */
	protected List plans;
	
	/** The sequence edges. */
	protected List sequenceedges;
	
	/** The artifacs. */
	protected List artifacts;
	
	//-------- methods --------
	
	/**
	 *  Get the goals.
	 *  @return The goals.
	 */
	public List getGoals()
	{
		return goals;
	}
	
	/**
	 *  Add an goal.
	 *  @param goal The goal.
	 */ 
	public void addGoal(MProcessElement goal)
	{
		if(goals==null)
			goals = new ArrayList();
		goals.add(goal);
	}
	
	/**
	 *  Remove an goal.
	 *  @param goal The goal. 
	 */
	public void removeGoal(MProcessElement vertex)
	{
		if(goals!=null)
			goals.remove(vertex);
	}
	
	/**
	 *  Get the plans.
	 *  @return The plans.
	 */
	public List getPlans()
	{
		return plans;
	}
	
	/**
	 *  Add an plan.
	 *  @param plan The plan.
	 */ 
	public void addPlan(MPlan plan)
	{
		if(plans==null)
			plans = new ArrayList();
		plans.add(plan);
	}
	
	/**
	 *  Remove an plan.
	 *  @param plan The plan. 
	 */
	public void removePlan(MPlan vertex)
	{
		if(plans!=null)
			plans.remove(vertex);
	}
	
	/**
	 *  Get the sequence edges.
	 *  @return The edges. 
	 */
	public List getSequenceEdges()
	{
		return sequenceedges;
	}
	
	/**
	 *  Add a sequence edge.
	 *  @param edge The edge.
	 */
	public void addSequenceEdge(MSequenceEdge edge)
	{
		if(sequenceedges==null)
			sequenceedges = new ArrayList();
		sequenceedges.add(edge);
	}
	
	/**
	 *  Remove a sequence edge.
	 *  @param edge The edge.
	 */
	public void removeSequenceEdge(MSequenceEdge edge)
	{
		if(sequenceedges!=null)
			sequenceedges.remove(edge);
	}
	
	/**
	 *  Get the artifacts.
	 *  @return The artifacts.
	 */
	public List getArtifacts()
	{
		return artifacts;
	}
	
	/**
	 *  Add an artifact.
	 *  @param artifact The artifact.
	 */
	public void addArtifact(MArtifact artifact)
	{
		if(artifacts==null)
			artifacts = new ArrayList();
		artifacts.add(artifact);
	}
	
	/**
	 *  Remove an artifact.
	 *  @param artifact The artifact.
	 */
	public void removeArtifact(MArtifact artifact)
	{
		if(artifacts!=null)
			artifacts.remove(artifact);
	}
}
