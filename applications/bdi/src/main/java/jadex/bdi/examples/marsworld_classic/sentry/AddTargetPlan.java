package jadex.bdi.examples.marsworld_classic.sentry;

import jadex.bdi.examples.marsworld_classic.Environment;
import jadex.bdi.examples.marsworld_classic.Target;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;

/**
 *  Add a new unknown target to test.
 */
public class AddTargetPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		getLogger().info("Created: "+this);
		
		//System.out.println("AddPlan found");
		Environment env = (Environment)getBeliefbase().getBelief("move.environment").getFact();
		IMessageEvent req = (IMessageEvent)getReason();

		Target ot = (Target)req.getParameter(SFipa.CONTENT).getValue();
		Target target = env.getTarget(ot.getId());

		//if(ts.length>0)
		//	System.out.println("Sees: "+SUtil.arrayToString(ts));

		if(target!=null)
		{
			if(!getBeliefbase().getBeliefSet("my_targets").containsFact(target)
			 && !getBeliefbase().getBeliefSet("analysed_targets").containsFact(target))
			{
				//System.out.println("Found a new target: "+target);
				getBeliefbase().getBeliefSet("my_targets").addFact(target);
			}
		}
	}

	//-------- static part --------

	/**
	 *  Get the filter.
	 * /
	public static IFilter getEventFilter()
	{
		MessageTemplate temp = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		MessageFilter filt = new MessageFilter(temp, null, OInformTarget.class);
		return filt;
	}*/
}
