package jadex.quickstart.cleanerworld;

import java.util.HashSet;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.quickstart.cleanerworld.environment.SensorActuator;
import jadex.quickstart.cleanerworld.gui.SensorGui;

/**
 *  Simple cleaner with a main loop for moving randomly.
 *  
 *  @author Alexander Pokahr
 *  @version 1.0 (2017/10/26)
 */
@Agent
public class SimpleCleanerAgent
{
	//-------- simple example behavior --------
	
	/**
	 *  The body is executed when the agent is started.
	 */
	@AgentBody
	private void	exampleBehavior()
	{
		// Create the sensor/actuator interface object.
		SensorActuator	actsense	= new SensorActuator(new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
		
		// Open a window showing the agent's perceptions
		new SensorGui(actsense).setVisible(true);

		// Agent uses one main loop for its random move behavior
		while(true)
		{
			actsense.moveTo(Math.random(), Math.random());
		}
	}
}
