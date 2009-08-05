package jadex.bdi.examples.marsworld.producer;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;
import java.util.Map;


/**
 *  Inform the sentry agent about a new target.
 */
public class ProduceOrePlan extends Plan
{
	//-------- attributes --------
	
	/** The id of the produce ore task (if any). */
	protected Object	taskid;
	
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public ProduceOrePlan()
	{
		getLogger().info("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		ISpaceObject target = (ISpaceObject)getParameter("target").getValue();

		// Move to the target.
		IGoal go_target = createGoal("move.move_dest");
		go_target.getParameter("destination").setValue(target.getProperty(Space2D.PROPERTY_POSITION));
		dispatchSubgoalAndWait(go_target);

		// Produce ore at the target.
		ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("move.myself").getFact();
		SyncResultListener	res	= new SyncResultListener();
		Map props = new HashMap();
		props.put(ProduceOreTask.PROPERTY_TARGET, target);
		IEnvironmentSpace space = (IEnvironmentSpace)getBeliefbase().getBelief("move.environment").getFact();
		taskid	= space.createObjectTask(ProduceOreTask.PROPERTY_TYPENAME, props, myself.getId());
		space.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
//		System.out.println("Produced ore at target: "+getAgentName()+", "+ore+" ore produced.");
	}
	
	/**
	 *  Called, when the plan is aborted.
	 */
	public void aborted()
	{
		if(taskid!=null)
		{
			IEnvironmentSpace space = (IEnvironmentSpace)getBeliefbase().getBelief("move.environment").getFact();
			ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("move.myself").getFact();
			space.removeObjectTask(taskid, myself.getId());
		}
	}
}
