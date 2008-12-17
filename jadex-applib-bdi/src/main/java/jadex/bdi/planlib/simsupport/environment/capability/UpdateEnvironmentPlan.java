package jadex.bdi.planlib.simsupport.environment.capability;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.Vector1Long;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IClock;

import javax.swing.event.ChangeListener;

public class UpdateEnvironmentPlan extends Plan
{
	ChangeListener cl;
	
	public void body()
	{
		ChangeListener cl = new ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent e) 
			{
				if(IClock.STATE_RUNNING.equals(getClock().getState()))
				{
					ISimulationEngine engine = (ISimulationEngine) getBeliefbase().getBelief("simulation_engine").getFact();
					long lastTime = ((Long) getBeliefbase().getBelief("sim_time").getFact()).longValue();
					IVector1 timeCoeff = (IVector1)getBeliefbase().getBelief("time_coefficient").getFact();
					
					//long currentTime = ((Long) b.getBelief("time").getFact()).longValue();
					// TODO: dynamic evaluation not working?
					
					long currentTime = getClock().getTime(); // ((IClockService) b.getBelief("clock_service").getFact()).getTime();
	//				System.out.println("SimTime: " + lastTime);
	//				System.out.println("Time: " + currentTime);
					IVector1 deltaT = timeCoeff.copy().multiply(new Vector1Long(currentTime - lastTime));
					
					engine.simulateStep(deltaT);
					getBeliefbase().getBelief("sim_time").setFact(new Long(currentTime));
				}
//				else
//				{
//					System.out.println("Clock paused.");
//				}
			};
		};
		
		getClock().addChangeListener(cl);
	}
	
	public void aborted()
	{
		if(cl!=null)
			getClock().removeChangeListener(cl);
	}
	
	public void failed()
	{
		if(cl!=null)
			getClock().removeChangeListener(cl);
	}
	
	public void passed()
	{
		if(cl!=null)
			getClock().removeChangeListener(cl);
	}
}
