package jadex.bdi.planlib.simsupport.environment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/** Generic simulation event.
 */
public class SimulationEvent implements Serializable
{
	private Map parameters;
	
	public SimulationEvent()
	{
		parameters = new HashMap();
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
