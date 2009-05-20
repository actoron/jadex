package jadex.bdi.examples.garbagecollector2;

import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.EnvironmentEvent;
import jadex.adapter.base.envsupport.environment.IPerceptGenerator;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IApplicationContext;
import jadex.bridge.ISpace;
import jadex.commons.SimplePropertyObject;

import java.util.ArrayList;
import java.util.List;

/**
 *  Percept generator for burner agents.
 */
public class BurnerVisionGenerator extends SimplePropertyObject implements IPerceptGenerator
{
	//-------- constants --------
	
	/** Constant for garabge appeared. */
	public static final String GARBAGE_APPEARED = "garbage_appeared";
	
	/** Constant for garabge disappeared. */
	public static final String GARBAGE_DISAPPEARED = "garbage_disappeared";

	//-------- attributes --------
	
	/** The consuming agents. */
	protected List agents;
	
	//-------- IPerceptGenerator --------
		
	/**
	 *  Called when an agent was added to the space.
	 *  @param agent The agent identifier.
	 *  @param space The space.
	 */
	public void agentAdded(IAgentIdentifier agent, ISpace space)
	{
		// Only add agents of type "Burner"
		if("Burner".equals(((IApplicationContext)space.getContext()).getAgentType(agent)))
		{
			if(agents==null)
				agents = new ArrayList();
			agents.add(agent);
		}
	}
	
	/**
	 *  Called when an agent was remove from the space.
	 *  @param agent The agent identifier.
	 *  @param space The space.
	 */
	public void agentRemoved(IAgentIdentifier agent, ISpace space)
	{
		agents.remove(agent);
		if(agents.size()==0)
			agents = null;
	}
	
	//-------- IEnvironmentListener --------
	
	/**
	 *  Test if an event is relevant for the percept generator.
	 *  @param event The event.
	 * /
	public boolean isRelevant(EnvironmentEvent event)
	{
		return agents!=null && "garbage".equals(event.getSpaceObject().getType()) && 
		EnvironmentEvent.OBJECT_POSITION_CHANGED.equals(event.getType());
	}*/
	
	/**
	 *  Dispatch an environment event to this listener.
	 *  @param event The event.
	 */
	public void dispatchEnvironmentEvent(EnvironmentEvent event)
	{
		if(agents!=null && "garbage".equals(event.getSpaceObject().getType()))
		{
			for(int i=0; i<agents.size(); i++)
			{
				IAgentIdentifier agent = (IAgentIdentifier)agents.get(i);

				if(EnvironmentEvent.OBJECT_POSITION_CHANGED.equals(event.getType()))
				{
					IVector2 pos = (IVector2)event.getSpaceObject().getProperty(Space2D.POSITION);
					IVector2 oldpos = (IVector2)event.getInfo();
					ISpaceObject agentobj = event.getSpace().getOwnedObjects(agent)[0];
					
					if(agentobj.getProperty(Space2D.POSITION).equals(pos))
					{
						// percept garbage appeared
//						perproc.processPercept(event.getSpace(), GARBAGE_APPEARED, event.getSpaceObject(), agent);
						((AbstractEnvironmentSpace)event.getSpace()).createPercept(GARBAGE_APPEARED, event.getSpaceObject(), agent);
					}
					else if(agentobj.getProperty(Space2D.POSITION).equals(oldpos))
					{
						// percept garbage disappeared
//						perproc.processPercept(event.getSpace(), GARBAGE_DISAPPEARED, event.getSpaceObject(), agent);
						((AbstractEnvironmentSpace)event.getSpace()).createPercept(GARBAGE_DISAPPEARED, event.getSpaceObject(), agent);
					}
				}
				else if(EnvironmentEvent.OBJECT_DESTROYED.equals(event.getType()))
				{
					// percept garbage disappeared
//					perproc.processPercept(event.getSpace(), GARBAGE_DISAPPEARED, event.getSpaceObject(), agent);
					((AbstractEnvironmentSpace)event.getSpace()).createPercept(GARBAGE_DISAPPEARED, event.getSpaceObject(), agent);
				}
			}
		}
	}
}
