package jadex.wfms.client;

import java.util.Map;

/** An activity being performed by a client
 */
public interface IClientActivity extends IWorkitem
{
	/**
	 * Gets the name of the activity.
	 * 
	 * @return name of the activity
	 */
	public String getName();
	
	/**
	 *  Returns the ID of the activity.
	 *  @return The activity ID.
	 */
	public String getActivityId();
	
	/**
	 *  Sets the ID of the activity.
	 *  @param id The activity ID.
	 */
	public void setActivityId(String id);
	
	/**
	 * Sets the value of a parameter.
	 * 
	 * @param parameterName name of the parameter
	 * @param value new value of the parameter
	 * @throws IllegalArgumentException if the parameter is read-only
	 */
	public void setParameterValue(String parameterName, Object value);
	
	/**
	 * Gets the parameter values.
	 * 
	 * @return values of the parameters.
	 */
	public Map getParameterValues();
	
	/**
	 * Sets the value of multiple parameters.
	 * 
	 * @param parameters the parameters
	 * @throws IllegalArgumentException if the parameter is read-only
	 */
	public void setMultipleParameterValues(Map parameters);
}
