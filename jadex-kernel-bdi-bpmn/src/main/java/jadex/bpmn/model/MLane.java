package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MLane extends MNamedIdElement
{
	//-------- attributes --------
	
	/** The activities description. */
	protected String activitiesdescription;
	
	/** The activities. */
	protected List activities;
	
	/** The type. */
	protected String type;
	
	//-------- constructors --------
	
	//-------- methods --------
	
	/**
	 * @return the activitiesdescription
	 */
	public String getActivitiesDescription()
	{
		return this.activitiesdescription;
	}

	/**
	 * @param activitiesdescription the activitiesdescription to set
	 */
	public void setActivitiesDescription(String activitiesdescription)
	{
		this.activitiesdescription = activitiesdescription;
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
	public void addActivities(MVertex vertex)
	{
		if(activities==null)
			activities = new ArrayList();
		activities.add(vertex);
	}
	
	/**
	 * 
	 */
	public void removeVertex(MVertex vertex)
	{
		if(activities!=null)
			activities.remove(vertex);
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
}
