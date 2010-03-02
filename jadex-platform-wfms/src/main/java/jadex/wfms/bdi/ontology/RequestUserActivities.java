package jadex.wfms.bdi.ontology;

import java.util.Map;
import jadex.base.fipa.IComponentAction;

/**
 * Class representing a request for activities of all users
 *
 */
public class RequestUserActivities implements IComponentAction
{
	/** The activities of the users */
	private Map userActivities;
	
	/**
	 * Creates a new request.
	 */
	public RequestUserActivities()
	{
	}
	
	/**
	 * Returns the activities of the users
	 * @return activities of the users
	 */
	public Map getUserActivities()
	{
		return userActivities;
	}
	
	/**
	 * Sets the activities of the users
	 * @param userActivities activities of the users
	 */
	public void setUserActivities(Map userActivities)
	{
		this.userActivities = userActivities;
	}
}
