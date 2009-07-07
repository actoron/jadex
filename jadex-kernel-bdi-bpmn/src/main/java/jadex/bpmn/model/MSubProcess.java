package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  A sub process represents an activity with and a sub activity flow. 
 */
public class MSubProcess extends MActivity
{
	//-------- attributes --------
	
	/** The vertices. */
	protected List activities;
	
	/** The sequence edges. */
	protected List sequenceedges;
	
	/** The event handlers. */
	protected List eventhandlers;
	
	/** The artifacts. */
	protected List artifacts;
	
	//-------- methods --------
	
	/**
	 *  Get the activities.
	 *  @return The activities.
	 */
	public List getActivities()
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
			activities = new ArrayList();
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
	 *  Get the sequence edges.
	 *  @return The sequence edges.
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
	 *  Get the event handlers.
	 *  @return The event handlers.
	 */
	public List getEventHandlers()
	{
		return eventhandlers;
	}
	
	/**
	 *  Add an event handler.
	 *  @param eventhandler The event handler.
	 */
	public void addEventHandler(MActivity eventhandler)
	{
		if(eventhandlers==null)
			eventhandlers = new ArrayList();
		eventhandlers.add(eventhandler);
	}
	
	/**
	 *  Remove an event handler.
	 *  @param eventhandler The event handler.
	 */
	public void removeEventHandler(MActivity eventhandler)
	{
		if(eventhandlers!=null)
			eventhandlers.remove(eventhandler);
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
