package jadex.bdi.examples.garbagecollector2;

import jadex.adapter.base.envsupport.environment.IPerceptProcessor;
import jadex.adapter.base.fipa.IAMS;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IApplicationContext;
import jadex.bridge.ISpace;
import jadex.commons.concurrent.IResultListener;

/**
 *  Simple collector/burner vision processer.
 *  Updates the agent's "garbages" beliefset according to the percepts of new/disappeared waste.
 */
public class VisionProcessor implements IPerceptProcessor
{
	/**
	 *  Process a new percept.
	 *  @param space The space.
	 *  @param type The type.
	 *  @param percept The percept.
	 *  @param agent The agent identifier.
	 */
	public void processPercept(ISpace space, final String type, final Object percept, IAgentIdentifier agent)
	{
		IAMS ams = (IAMS)((IApplicationContext)space.getContext()).getPlatform().getService(IAMS.class);
		ams.getExternalAccess(agent, new IResultListener()
		{
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
			public void resultAvailable(Object result)
			{
				try
				{
					IExternalAccess exta = (IExternalAccess)result;
					IBeliefSet garbages = exta.getBeliefbase().getBeliefSet("garbages");
					if("garbage_appeared".equals(type))
					{
	//					System.out.println("garbage appeared: "+percept);
						if(!garbages.containsFact(percept))
							garbages.addFact(percept);
					}
					else if("garbage_disappeared".equals(type))
					{
	//					System.out.println("garbage disappeared: "+percept);
						if(garbages.containsFact(percept))
							garbages.removeFact(percept);
					}
				}
				catch(Exception e)
				{
					// try catch for the case that the agent is not yet inited and
					// the belief value is not accessible
				}
			}
		});
	}
}