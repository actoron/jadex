package jadex.bdi.examples.marsworld_classic.producer;

import java.util.Collection;

import jadex.bdi.examples.marsworld_classic.AgentInfo;
import jadex.bdi.examples.marsworld_classic.Environment;
import jadex.bdi.examples.marsworld_classic.Location;
import jadex.bdi.examples.marsworld_classic.RequestCarry;
import jadex.bdi.examples.marsworld_classic.RequestProduction;
import jadex.bdi.examples.marsworld_classic.Target;
import jadex.bdi.examples.marsworld_classic.carrier.ICarryService;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalDroppedException;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.IService;

/**
 *  The main plan for the Production Agent. <br>
 *  first the Agent waits for an incomming request.
 *  It can be called to move home or to a given location.
 *  Being called to a location it will dispatch a subgoal to produce
 *  the ore there look up available carry agents and call one to collect it.
 */
public class ProductionPlan extends Plan
{
	//-------- attributes --------

	protected int visited;

	//-------- methods --------

	/**
	 *  Method body.
	 */
	public void body()
	{
		getLogger().info("Created: "+this);
		this.visited = 0;

		Environment env = ((Environment)getBeliefbase().getBelief("move.environment").getFact());
		env.setAgentInfo(new AgentInfo(getComponentName(),
			(String)getBeliefbase().getBelief("move.my_type").getFact(), (Location)getBeliefbase()
			.getBelief("move.my_home").getFact(),((Number)getBeliefbase().getBelief("move.my_vision").getFact()).doubleValue()));
		
		try
		{
			while(true)
			{
				// Wait for a request.
				IMessageEvent req = waitForMessageEvent("request_production");
	
				Target ot = ((RequestProduction)req.getParameter(SFipa.CONTENT).getValue()).getTarget();
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
		catch(GoalDroppedException e) 
		{
			// nop
		}
	}

	/**
	 * Sending a locaton to the Production Agent.
	 * Therefore it has first to be looked up in the DF.
	 * @param target
	 */
	private void callCarryAgent(Target target)
	{
		// Search for Carry_Service
		// Create a service description to search for.
//		IDF	df	= (IDF)getAgent().getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IDF.class, ServiceScope.PLATFORM)).get();
//		IDFServiceDescription sd = new DFServiceDescription("service_carry", null, null);
//		IDFComponentDescription dfadesc = new DFComponentDescription(null, sd);
//
//		// A hack - default is 2! to reach more Agents, we have
//		// to increase the number of possible results.
//		ISearchConstraints constraints = df.createSearchConstraints(-1, 0);
//		IDFComponentDescription[] carriers = df.search(dfadesc, constraints).get();

		Collection<ICarryService> carriers = getAgent().getLocalServices(ICarryService.class);
		
		if(carriers.size()>0)
		{
			//System.out.println("Carry Agent: Found Carry Agents: "+carriers.length);

			RequestCarry rc = new RequestCarry();
			rc.setTarget(target);
			//Action action = new Action();
			//action.setAction(rc);
			//action.setActor(new AID("dummy", true)); // Hack!! What to do with more than one receiver?
			IMessageEvent mevent = createMessageEvent("request_carries");
			for(ICarryService cs: carriers)
				mevent.getParameterSet(SFipa.RECEIVERS).addValue(((IService)cs).getServiceId().getProviderId());
			mevent.getParameter(SFipa.CONTENT).setValue(rc);
			sendMessage(mevent);
			//System.out.println("Production Agent sent target to: "+carriers.length);
		}
	}
}
