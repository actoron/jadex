package ecatest;

import jadex.rules.eca.annotations.Condition;
import jadex.rules.eca.annotations.Event;

/**
 * 
 */
public class CleanerAgent
{
//	@Belief
	protected double chargestate = 1.0;
//	
//	@MaintainGoal
//	class MaintainBatteryLoaded
//	{
//		@MaintainCondition("battery")
//		public boolean	batteryLoadCondition(@Event("chargestate") double chargestate)
//		{
//			return chargestate < 0.2;
//		}
//	}
//	
//	@Plan(trigger=MaintainBatteryLoaded.class)
//	protected void load()
//	{
//		chargestate += 0.01;
//	}
	
	/**
	 *  Get the chargestate.
	 *  @return the chargestate.
	 */
	public double getChargeState()
	{
		return chargestate;
	}
	
	@Event("chargestate")
	public void decreaseChargeState()
	{
		chargestate -= 0.01;
//		new Event(new Double(chargestate));
	}
}
