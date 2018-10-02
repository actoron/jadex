package jadex.quickstart.cleanerworld.single;

import java.util.LinkedHashSet;
import java.util.Set;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.quickstart.cleanerworld.environment.IWaste;
import jadex.quickstart.cleanerworld.environment.SensorActuator;
import jadex.quickstart.cleanerworld.gui.SensorGui;

/**
 *  Simple example of using the environment sensor.
 *  @author Alexander Pokahr
 *  @version 1.0 (2017/10/19)
 *
 */
@Agent(type="bdi")
public class SimpleCleanerBDIAgent
{
	//-------- beliefs that can be used in plan and goal conditions --------
	
	/** Set of the known wastes. Managed by SensorActuator object. */
	@Belief
	private Set<IWaste>	wastes	= new LinkedHashSet<>();
	
	/** The sensor gives access to the environment. */
	private SensorActuator	actsense	= new SensorActuator(wastes, null, null, null);
	
	//-------- simple example behavior --------
	
	/**
	 *  The body is executed when the agent is started.
	 *  @param bdifeature	Provides access to bdi specific methods
	 */
	@AgentBody
	private void	exampleBehavior(IBDIAgentFeature bdifeature)
	{
		// Open a window showing the agent's perceptions
		new SensorGui(actsense).setVisible(true);
		
		// Agent uses one main loop for its random move behavior
		while(true)
		{
			actsense.moveTo(Math.random(), Math.random());
		}
	}
	
	//-------- simple examples of using belief and goal events --------
	
	/**
	 *  Declare a plan using a method with @Plan and @Trigger annotation.
	 *  @param waste The new fact that triggered the plan execution.
	 */
	@Plan(trigger=@Trigger(factaddeds="wastes"))
	private void	examplePlanMethod(IWaste waste)
	{
		System.out.println("New waste seen: "+waste);
	}
}
