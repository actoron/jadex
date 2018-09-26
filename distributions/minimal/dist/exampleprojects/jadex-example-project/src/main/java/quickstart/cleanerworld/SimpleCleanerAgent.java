package cleanerworld.single;

import java.util.HashSet;

import cleanerworld.environment.IWaste;
import cleanerworld.environment.IWastebin;
import cleanerworld.environment.SensorActuator;
import cleanerworld.gui.SensorGui;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  Simple solution not using BDI at all.
 *  
 *  @author Alexander Pokahr
 *  @version 1.0 (2017/10/26)
 */
@Agent
public class CleanerNonBDIAgent
{
	//-------- simple example behavior --------
	
	/**
	 *  The body is executed when the agent is started.
	 *  @param bdifeature	Provides access to bdi specific methods
	 */
	@AgentBody
	private void	exampleBehavior()
	{
		// Create the sensor/actuator interface object.
		SensorActuator	actsense	= new SensorActuator(new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
		
		// Open a window showing the agent's perceptions
		new SensorGui(actsense).setVisible(true);

		// Agent uses one main loop for all behaviors.
		while(true)
		{
			// No waste known and no waste carried -> look for waste by moving randomly.
			if(actsense.getWastes().isEmpty() && actsense.getSelf().getCarriedWaste()==null)
			{
				actsense.moveTo(Math.random(), Math.random());
			}
			
			// Waste known and no waste carried -> move to next waste and pick it up.
			else if(!actsense.getWastes().isEmpty() && actsense.getSelf().getCarriedWaste()==null)
			{
				IWaste	next	= actsense.getWastes().iterator().next();
				actsense.moveTo(next.getLocation().getX(), next.getLocation().getY());
				actsense.pickUpWaste(next);
			}
			
			// Waste carried and no waste bin known -> look for waste bin by moving randomly.
			else if(actsense.getSelf().getCarriedWaste()!=null && actsense.getWastebins().isEmpty())
			{
				actsense.moveTo(Math.random(), Math.random());
			}
			
			// Waste carried and waste bin known -> move to waste bin and drop waste
			else if(actsense.getSelf().getCarriedWaste()!=null && !actsense.getWastebins().isEmpty())
			{
				IWastebin	bin	= actsense.getWastebins().iterator().next();
				actsense.moveTo(bin.getLocation().getX(), bin.getLocation().getY());
				actsense.dropWasteInWastebin(actsense.getSelf().getCarriedWaste(), bin);
			}
		}
	}
}
