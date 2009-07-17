package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  A lane is a subpart of a pool representing e.g. a role or some
 *  resposibility sphere.
 */
public class MLane extends MAssociationTarget
{
	//-------- attributes --------
	
	/** The activities description. */
	protected String activitiesdescription;
	
	
	/** The activities. */
	protected List activities;
	
	/** The type. */
	protected String type;
	
	/** The parent lane (if any). */
	protected MLane lane;
		
	//-------- methods --------
	
	/**
	 *  Get the activities description.
	 *  @return The activities description.
	 */
	public String getActivitiesDescription()
	{
		return this.activitiesdescription;
	}

	/**
	 *  Set the activities description.
	 *  @param activitiesdescription The activities description to set.
	 */
	public void setActivitiesDescription(String activitiesdescription)
	{
		this.activitiesdescription = activitiesdescription;
	}
	
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

		// Todo: Use post processor!?
		activity.setLane(this);
	}
	
	/**
	 *  Remove an activity.
	 *  @param activity The activity.
	 */
	public void removeActivity(MActivity activity)
	{
		if(activities!=null)
			activities.remove(activity);
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}
	
	/**
	 *  Get the associations.
	 *  return The associations.
	 */
	public List getAssociations()
	{
		return associations;
	}

	/**
	 *  Get the parent lane of the lane (if any).
	 *  @return The parent lane of the lane.
	 */
	public MLane getLane()
	{
		return lane;
	}

	/**
	 *  Set the parent lane of the lane.
	 *  @param lane The parent lane of the lane.
	 */
	public void setLane(MLane lane)
	{
		this.lane	= lane;
	}
}
