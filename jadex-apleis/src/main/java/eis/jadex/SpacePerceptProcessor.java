package eis.jadex;

import jadex.adapter.base.envsupport.environment.IPerceptProcessor;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ISpace;
import jadex.commons.SimplePropertyObject;

import eis.AgentListener;
import eis.iilang.Parameter;
import eis.iilang.Percept;

/**
 *  Simple percept processor that forwards the 
 *  percepts to the fitting registered agent listeners.
 */
public class SpacePerceptProcessor extends SimplePropertyObject implements IPerceptProcessor
{
	//-------- attributes --------
	
	/** The space. */
	protected JadexDelegationEisImpl eis;
	
	//-------- constructors --------
	
	/**
	 *  Create a new processor.
	 */
	public SpacePerceptProcessor(JadexDelegationEisImpl eis)
	{
		this.eis = eis;
	}
	
	//-------- methods --------
	
	/**
	 *  Process a new percept.
	 *  @param space The space.
	 *  @param type The type.
	 *  @param percept The percept.
	 *  @param agent The agent identifier.
	 *  @param agent The avatar of the agent (if any).
	 */
	public void processPercept(final ISpace space, final String type, final Object percept, final IComponentIdentifier agent, final ISpaceObject avatar)
	{
		AgentListener[] als = eis.getAgentListeners(agent.getName());
		if(als!=null)
		{
			for(int i=0; i<als.length; i++)
			{
				// todo: parameter, needs to pass the custom percept object!
				Percept per = new Percept(type, new Parameter[0]);
				als[i].handlePercept(agent.getName(), per);
			}
		}
	}
}