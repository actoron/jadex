package jadex.bdi.examples.garbagecollector2;

import jadex.adapter.base.appdescriptor.ApplicationContext;
import jadex.adapter.base.contextservice.ISpace;
import jadex.adapter.base.envsupport.environment.IPerceptProcessor;
import jadex.adapter.base.fipa.IAMS;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.concurrent.IResultListener;

/**
 *  Simple burner vision processer.
 *  Updates the agent's "garbages" beliefset according to the percepts of new/disappeared waste.
 */
public class BurnerVisionProcessor implements IPerceptProcessor
{
	public void processPercept(ISpace space, final String type, final Object percept, IAgentIdentifier agent)
	{
		IAMS ams = (IAMS)((ApplicationContext)space.getContext()).getPlatform().getService(IAMS.class);
		ams.getExternalAccess(agent, new IResultListener()
		{
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
			public void resultAvailable(Object result)
			{
				IExternalAccess exta = (IExternalAccess)result;
				IBeliefSet garbages = exta.getBeliefbase().getBeliefSet("garbages");
				if(BurnerVisionGenerator.GARBAGE_APPEARED.equals(type))
				{
					System.out.println("garbage appeared: "+percept);
					if(!garbages.containsFact(percept))
						garbages.addFact(percept);
				}
				else if(BurnerVisionGenerator.GARBAGE_DISAPPEARED.equals(type))
				{
					System.out.println("garbage disappeared: "+percept);
					if(garbages.containsFact(percept))
						garbages.removeFact(percept);
				}
			}
		});
	}
}