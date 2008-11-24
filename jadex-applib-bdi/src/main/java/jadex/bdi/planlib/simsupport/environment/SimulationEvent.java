package jadex.bdi.planlib.simsupport.environment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Generic simulation event.
 */
public class SimulationEvent implements Serializable
{
	// Default Event Types
	public static final String DESTINATION_REACHED = "simobj_dest_reached";
	public static final String GO_TO_DESTINATION_REACHED = "simobj_go_to_dest_reached";
	public static final String OBJECT_DESTROYED = "simobj_destroyed";
	
	/** Event type
	 */
	private String type;
	
	private Map parameters;
	
	/** Creates a new SimulationEvent
	 * 
	 *  @param name event name
	 */
	
	/** Creates a new SimulationEvent
	 * 
	 *  @param name event type
	 */
	public SimulationEvent(String type)
	{
		this.type = type;
		parameters = new HashMap();
	}
	
	/** Returns the event type.
	 * 
	 *  @return event type
	 */
	public String getType()
	{
		return type;
	}
	
	/** Returns whether the event has parameters.
	 * 
	 *  @return true, if the event has parameters
	 */
	public boolean hasParameters()
	{
		return parameters.size() > 0;
	}
	
	/** Returns the parameters.
	 * 
	 *  @return parameters as Set of Map.Entry 
	 */
	public Set getParameters()
	{
		return parameters.entrySet();
	}
	
	/** Returns an event parameter.
	 * 
	 * @param parameter parameter name
	 * @return event parameter
	 */
	public Object getParameter(String parameter)
	{
		return parameters.get(parameter);
	}
	
	/** Sets an event parameter.
	 * 
	 * @param parameter parameter name
	 * @param obj parameter object
	 */
	public void setParameter(String parameter, Object obj)
	{
		parameters.put(parameter, obj);
	}
}
