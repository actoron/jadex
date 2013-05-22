package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  A sub process represents an activity with and a sub activity flow. 
 */
public class MSubProcess extends MActivity
{
	//-------- constants --------
	
	/** The default subprocess type. */
	public static final String	SUBPROCESSTYPE_NONE	= "none";
	
	/** The parallel subprocess type. */
	public static final String	SUBPROCESSTYPE_PARALLEL	= "parallel";
	
	/** The looping subprocess type. */
	public static final String	SUBPROCESSTYPE_LOOPING	= "looping";
	
	//-------- attributes --------
	
	/** The vertices. */
	protected List<MActivity> activities;
	
	/** The sequence edges. */
//	protected List sequenceedges;
	
	/** The artifacts. */
	protected List artifacts;
	
	//-------- added --------
	
	/** The subprocess type (e.g. looping). */
	protected String	subprocesstype;
	
	//-------- methods --------
	
	/**
	 *  Get the activities.
	 *  @return The activities.
	 */
	public List<MActivity> getActivities()
	{
		return activities;
	}
	
	/**
	 *  Add an activity.
	 *  @param activity The activity.
	 */ 
	public void addActivity(MActivity activity)
	{
		if(activities==null)
			activities = new ArrayList<MActivity>();
		activities.add(activity);
	}
	
	/**
	 *  Remove an activity.
	 *  @param activity The activity. 
	 */
	public void removeActivity(MActivity vertex)
	{
		if(activities!=null)
			activities.remove(vertex);
	}
	
	/**
	 *  Get all start activities of the pool.
	 *  @return A non-empty List of start activities or null, if none.
	 */
	public List<MActivity> getStartActivities()
	{
		return MBpmnModel.getStartActivities(activities);
	}	
	
	/**
	 *  Get the sequence edges.
	 *  @return The sequence edges.
	 */
//	public List getSequenceEdges()
//	{
//		return sequenceedges;
//	}
	
	/**
	 *  Add a sequence edge.
	 *  @param edge The edge.
	 */
//	public void addSequenceEdge(MSequenceEdge edge)
//	{
//		if(sequenceedges==null)
//			sequenceedges = new ArrayList();
//		sequenceedges.add(edge);
//	}
	
	/**
	 *  Remove a sequence edge.
	 *  @param edge The edge.
	 */
//	public void removeSequenceEdge(MSequenceEdge edge)
//	{
//		if(sequenceedges!=null)
//			sequenceedges.remove(edge);
//	}
	
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
	
	/**
	 *  Get the subprocess type.
	 */
	public String	getSubprocessType()
	{
		return subprocesstype!=null ? subprocesstype : SUBPROCESSTYPE_NONE;
	}
	
	/**
	 *  Set the subprocess type.
	 */
	public void	setSubprocessType(String subprocesstype)
	{
		assert SUBPROCESSTYPE_NONE.equals(subprocesstype)
			|| SUBPROCESSTYPE_PARALLEL.equals(subprocesstype)
			|| SUBPROCESSTYPE_LOOPING.equals(subprocesstype) : subprocesstype+", "+this;

		this.subprocesstype	= subprocesstype;
	}

}
