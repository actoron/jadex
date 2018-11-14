package jadex.quickstart.cleanerworld;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.quickstart.cleanerworld.environment.IChargingstation;
import jadex.quickstart.cleanerworld.environment.IWaste;
import jadex.quickstart.cleanerworld.environment.IWastebin;
import jadex.quickstart.cleanerworld.environment.SensorActuator;
import jadex.quickstart.cleanerworld.gui.SensorGui;

/**
 *  Simple cleaner with a main loop for moving randomly.
 */
@Agent
public class SimpleCleanerAgentZero
{
	//-------- simple example behavior --------
	
	/**
	 *  The body is executed when the agent is started.
	 */
	@AgentBody
	private void	exampleBehavior()
	{
		// Create the sensor/actuator interface object.
		SensorActuator	actsense	= new SensorActuator();
		
		// Open a window showing the agent's perceptions
		new SensorGui(actsense).setVisible(true);


		// Agent uses one main loop for its behavior
		while(true)
		{
			// Need to recharge (less than 20% battery)?
			if(actsense.getSelf().getChargestate()<0.2)
			{
				// Charging station known?
				if(!actsense.getChargingstations().isEmpty())
				{
					// Move to charging station and recharge
					IChargingstation	station	= actsense.getChargingstations().iterator().next();
					actsense.moveTo(station.getLocation());
					actsense.recharge(station, 0.9);
				}
				else
				{
					// Move around to find charging station
					actsense.moveTo(Math.random(), Math.random());					
				}
			}
			
			// Waste picked up?
			else if(actsense.getSelf().getCarriedWaste()!=null)
			{
				// Waste bin known?
				if(!actsense.getWastebins().isEmpty())
				{
					// Move to waste bin and drop waste
					IWastebin	bin	= actsense.getWastebins().iterator().next();
					actsense.moveTo(bin.getLocation());
					actsense.dropWasteInWastebin(actsense.getSelf().getCarriedWaste(), bin);
				}
				else
				{
					// Move around to find waste bin
					actsense.moveTo(Math.random(), Math.random());					
				}
			}
			
			// Not carrying waste
			else
			{
				// Waste known?
				if(!actsense.getWastes().isEmpty())
				{
					// Move to waste and pick it up
					IWaste	waste	= actsense.getWastes().iterator().next();
					actsense.moveTo(waste.getLocation());
					actsense.pickUpWaste(waste);
				}
				else
				{
					// Move around to find waste
					actsense.moveTo(Math.random(), Math.random());					
				}
			}
		}
	}
}
