package deco4mas.coordinate.interpreter.coordination_information;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.IPerceptProcessor;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 * This interface specifies the "Coordination Information Interpreter" component.
 * 
 * @author Ante Vilenica & Jan Sudeikat
 * 
 *         Interface for CoordinationInformationInterpreter, i.e. a "V2 Percept Processor".
 */
public interface ICoordinationInformationInterpreter extends IPerceptProcessor {

	// /**
	// * Get the context.
	// * @return The context.
	// */
	// public IContext getContext();
	//
	// /**
	// * Called from application context, when an agent was added.
	// * Also called once for all agents in the context, when a space
	// * is newly added to the context.
	// * @param aid The id of the added agent.
	// */
	// public void agentAdded(IAgentIdentifier aid);
	//
	// /**
	// * Called from application context, when an agent was removed.
	// * @param aid The id of the removed agent.
	// */
	// public void agentRemoved(IAgentIdentifier aid);

	// -------- methods -------------

	/**
	 * This method checks if a Coordination Information triggers the dispatchment (positive direction) or removal (negative direction) of an agent property like belief, goal or ISpaceProperty.
	 * 
	 * @param type
	 *            The percept type.
	 * @param data
	 *            The content of the percept (if any).
	 * @param agent
	 *            The agent that should receive the percept.
	 * @param avatar
	 *            The avatar of the agent (if any).
	 * @param processor
	 *            The percept processor.
	 */
	// public void dispatchWhenApplicable(String type, Object data, IAgentIdentifier agent, ISpaceObject avatars);

	// public void processPercept(ISpace space, String type, Object percept, IComponentIdentifier agent, ISpaceObject avatar);
	public void processPercept(IEnvironmentSpace space, String type, Object percept, IComponentDescription component, ISpaceObject avatar);
}