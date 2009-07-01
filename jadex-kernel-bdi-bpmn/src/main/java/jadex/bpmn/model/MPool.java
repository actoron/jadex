package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MPool extends MNamedIdElement
{
	//-------- attributes --------
	
	/** The association description. */
	protected String associationsdescription;
	
	
	/** The vertices. */
	protected List activities;
	
	/** The sequence edges. */
	protected List sequenceedges;
	
	/** The pools. */
	protected List lanes;
	
	/** The artifacs. */
	protected List artifacts;
	
	/** The associations. */
	protected List associations;
	
	/** The messages. */
//	protected List messages;
	
	/** The type. */
	protected String type;
	
	//-------- methods --------
	
	/**
	 * @return the associationsdescription
	 */
	public String getAssociationsDescription()
	{
		return this.associationsdescription;
	}

	/**
	 * @param associationsdescription the associationsdescription to set
	 */
	public void setAssociationsDescription(String associationsdescription)
	{
		this.associationsdescription = associationsdescription;
	}


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
	public void addActivity(MActivity activity)
	{
		if(activities==null)
			activities = new ArrayList();
		activities.add(activity);
	}
	
	/**
	 * 
	 */
	public void removeActivity(MActivity activity)
	{
		if(activities!=null)
			activities.remove(activity);
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
	public List getLanes()
	{
		return lanes;
	}
	
	/**
	 * 
	 */
	public void addLane(MLane lane)
	{
		if(lanes==null)
			lanes = new ArrayList();
		lanes.add(lane);
	}
	
	/**
	 * 
	 */
	public void removeLane(MLane lane)
	{
		if(lanes!=null)
			lanes.remove(lane);
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
	public void addArtifacts(MArtifact artifact)
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
	
	/**
	 * 
	 */
	public List getAssociations()
	{
		return associations;
	}
	
	/**
	 * 
	 */
	public void addAssociation(MAssociation association)
	{
		if(associations==null)
			associations = new ArrayList();
		associations.add(association);
	}
	
	/**
	 * 
	 */
	public void removeAssociation(MAssociation association)
	{
		if(associations!=null)
			associations.remove(association);
	}
	
	/**
	 * @return the type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}
	
	/**
	 *  Get all start events of the pool.
	 *  @return A non-empty List of start events or null, if none.
	 */
	public List getStartEvents()
	{
		List	ret	= null;
		for(int i=0; activities!=null && i<activities.size(); i++)
		{
			MActivity	act	= (MActivity)activities.get(i);
			// Todo: use constants
			// Todo: other start event types.
			if("EventStartEmpty".equals(act.getActivityType()))
			{
				if(ret==null)
					ret	= new ArrayList();
				ret.add(act);
			}
		}
		return ret;
	}
	
	
}
