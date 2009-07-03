package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
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
	 * 
	 */
	public List getActivities()
	{
		return activities;
	}
	
	/**
	 * 
	 */
	public void addActivity(MActivity vertex)
	{
		if(activities==null)
			activities = new ArrayList();
		activities.add(vertex);
	}
	
	/**
	 * 
	 */
	public void removeActivity(MActivity vertex)
	{
		if(activities!=null)
			activities.remove(vertex);
	}
	
	/**
	 * 
	 */
	public List getSequenceEdges()
	{
		return sequenceedges;
	}
	
	/**
	 * 
	 */
	public void addSequenceEdge(MSequenceEdge edge)
	{
		if(sequenceedges==null)
			sequenceedges = new ArrayList();
		sequenceedges.add(edge);
	}
	
	/**
	 * 
	 */
	public void removeSequenceEdge(MSequenceEdge edge)
	{
		if(sequenceedges!=null)
			sequenceedges.remove(edge);
	}
	
	/**
	 * 
	 */
	public List getEventHandlers()
	{
		return eventhandlers;
	}
	
	/**
	 * 
	 */
	public void addEventHandler(MActivity eventhandler)
	{
		if(eventhandlers==null)
			eventhandlers = new ArrayList();
		eventhandlers.add(eventhandler);
	}
	
	/**
	 * 
	 */
	public void removeEventHandler(MActivity eventhandler)
	{
		if(eventhandlers!=null)
			eventhandlers.remove(eventhandler);
	}
	
	/**
	 * 
	 */
	public List getArtifacts()
	{
		return artifacts;
	}
	
	/**
	 * 
	 */
	public void addArtifact(MArtifact artifact)
	{
		if(artifacts==null)
			artifacts = new ArrayList();
		artifacts.add(artifact);
	}
	
	/**
	 * 
	 */
	public void removeArtifact(MArtifact artifact)
	{
		if(artifacts!=null)
			artifacts.remove(artifact);
	}
}
