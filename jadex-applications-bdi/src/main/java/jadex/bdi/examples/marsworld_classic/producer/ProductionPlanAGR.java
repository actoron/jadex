package jadex.bdi.examples.marsworld_classic.producer;

import jadex.bdi.examples.marsworld_classic.AgentInfo;
import jadex.bdi.examples.marsworld_classic.Environment;
import jadex.bdi.examples.marsworld_classic.Location;
import jadex.bdi.examples.marsworld_classic.RequestCarry;
import jadex.bdi.examples.marsworld_classic.RequestProduction;
import jadex.bdi.examples.marsworld_classic.Target;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.extension.agr.AGRSpace;
import jadex.extension.agr.Group;

/**
 *  The main plan for the Production Agent. <br>
 *  first the Agent waits for an incomming request.
 *  It can be called to move home or to a given location.
 *  Being called to a location it will dispatch a subgoal to produce
 *  the ore there look up available carry agents and call one to collect it.
 */
public class ProductionPlanAGR extends Plan
{
	//-------- attributes --------

	protected int visited;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public ProductionPlanAGR()
	{
		getLogger().info("Created: "+this);
		this.visited = 0;

		Environment env = ((Environment)getBeliefbase().getBelief("move.environment").getFact());
		env.setAgentInfo(new AgentInfo(getComponentName(),
			(String)getBeliefbase().getBelief("move.my_type").getFact(), (Location)getBeliefbase()
			.getBelief("move.my_home").getFact(),((Number)getBeliefbase().getBelief("move.my_vision").getFact()).doubleValue()));
	}

	//-------- methods --------

	/**
	 *  Method body.
	 */
	public void body()
	{
		while(true)
		{
			// Wait for a request.
			IMessageEvent req = waitForMessageEvent("request_production");

			Target ot = ((RequestProduction)req.getParameter(SFipa.CONTENT).getValue()).getTarget();
			Environment env = (Environment)getBeliefbase().getBelief("move.environment").getFact();
			Target target = env.getTarget(ot.getId());

			// Producing ore here.
			IGoal produce_ore = createGoal("produce_ore");
			produce_ore.getParameter("target").setValue(target);
			dispatchSubgoalAndWait(produce_ore);

			//System.out.println("Production of ore has finished....");
			//System.out.println("Calling Carry Agent....");
			callCarryAgent(target);

			/*RGoal go_home = createGoal("move_dest");
			go_home.getParameter("destination", getBeliefbase().getBelief("???").getFact("my_home"));
			RGoalEvent ev_home = dispatchSubgoalAndWait(go_home);*/
		}
	}

	/**
	 *  Call carry agents to location.
	 *  @param target	The target to call carries to.
	 */
	private void callCarryAgent(Target target)
	{
		AGRSpace agrs = (AGRSpace)((IExternalAccess)getScope().getParentAccess()).getExtension("myagrspace").get(this);

		Group group = agrs.getGroup("mymarsteam");
		IComponentIdentifier[]	carriers	= group.getAgentsForRole("carrier");
		
		if(carriers.length>0)
		{
			//System.out.println("Carry Agent: Found Carry Agents: "+carriers.length);

			RequestCarry rc = new RequestCarry();
			rc.setTarget(target);
			//Action action = new Action();
			//action.setAction(rc);
			//action.setActor(new AID("dummy", true)); // Hack!! What to do with more than one receiver?
			IMessageEvent mevent = createMessageEvent("request_carries");
				for(int i=0; i<carriers.length; i++)
				mevent.getParameterSet(SFipa.RECEIVERS).addValue(carriers[i]);
			mevent.getParameter(SFipa.CONTENT).setValue(rc);
			sendMessage(mevent);
			//System.out.println("Production Agent sent target to: "+carriers.length);
		}
	}
}
