package jadex.bdi.runtime.interpreter;

import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.rules.state.IOAVState;

import java.util.Map;

/**
 *  Rules and helpers for handling internal events.
 */
public class InternalEventRules
{
	/**
	 *  Create an internal event of a given type but does not add to state.
	 *  @param state The state.
	 *  @param rcapa The scope.
	 *  @param type The type.
	 *  @param rplan The plan (if created from plan).
	 */
	public static Object createInternalEvent(IOAVState state, Object rcapa, String type)
	{
		Object mcapa = state.getAttributeValue(rcapa, OAVBDIRuntimeModel.element_has_model);
		if(!state.containsKey(mcapa, OAVBDIMetaModel.capability_has_internalevents, type))
			throw new RuntimeException("Unknown internal event: "+type);
		Object mevent = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_internalevents, type);
		Object revent = instantiateInternalEvent(state, rcapa, mevent, null, null, null, null);
		return revent;
	}
	
	/**
	 *  Instantiate an internal event.
	 *  @param state	The state
	 *  @param rcapa	The capability.
	 *  @param mevent	The event model.
	 *  @param cevent	The event configuration (if any).
	 *  @return The event instance.
	 */
	public static Object	instantiateInternalEvent(IOAVState state, Object rcapa, Object mevent, Object cevent, Map bindings, OAVBDIFetcher fetcher, OAVBDIFetcher configfetcher)
	{
		Object revent = state.createObject(OAVBDIRuntimeModel.internalevent_type);
		state.setAttributeValue(revent, OAVBDIRuntimeModel.element_has_model, mevent);
		state.setAttributeValue(revent, OAVBDIRuntimeModel.processableelement_has_state, 
			OAVBDIRuntimeModel.PROCESSABLEELEMENT_UNPROCESSED);
		
		// todo: adapter?
		if(fetcher==null)
			fetcher = new OAVBDIFetcher(state, rcapa);
		fetcher.setRInternalEvent(revent);
		AgentRules.initParameters(state, revent, cevent, fetcher, configfetcher, null, bindings, rcapa);
		
		return revent;
	}
	
	/**
	 *  Adopt an internal event.
	 *  Adds the event to the state (eventbase).
	 *  @param state	The state
	 *  @param rcapa	The capability.
	 *  @param rgoal	The goal.
	 */
	public static void	adoptInternalEvent(IOAVState state, Object rcapa, Object rinternalevent)
	{
		state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_internalevents, rinternalevent);
		
		// Hack!!! Only needed for external access!
		((IInternalExecutionFeature)BDIAgentFeature.getInternalAccess(state).getComponentFeature(IExecutionFeature.class)).wakeup();
	}
}
