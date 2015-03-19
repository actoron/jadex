package jadex.bdi.runtime.interpreter;

import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBeliefListener;
import jadex.bdi.runtime.IBeliefSetListener;
import jadex.bdi.runtime.IGoalListener;
import jadex.bdi.runtime.IInternalEventListener;
import jadex.bdi.runtime.IMessageEventListener;
import jadex.bdi.runtime.IPlanListener;
import jadex.bdi.runtime.impl.flyweights.BeliefFlyweight;
import jadex.bdi.runtime.impl.flyweights.BeliefSetFlyweight;
import jadex.bdi.runtime.impl.flyweights.GoalFlyweight;
import jadex.bdi.runtime.impl.flyweights.InternalEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.MessageEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.PlanFlyweight;
import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.IConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.IPriorityEvaluator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.OrConstraint;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

import java.util.Collection;


/**
 *  Static helper class for agent/goal/etc listener rules and actions.
 *  
 *  Listener aspects are implemented directly via a state listener on the IOAVState.
 */

// Todo: Call listeners on external thread.

public class ListenerRules
{
	//-------- rule methods --------

	/**
	 *  Create a rule to notify agent listeners, when an agent is terminating.
	 */
	// Removed: Listeners called by direct invocation.
	/*protected static Rule createAgentTerminationListenerRule()
	{
		Variable ragent = new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		Variable listenerentry = new Variable("?listenerentry", OAVBDIRuntimeModel.listenerentry_type);
		Variable ce = new Variable("?ce", OAVBDIRuntimeModel.changeevent_type);
		
		ObjectCondition	cecon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		cecon.addConstraint(new BoundConstraint(null, ce));
		IConstraint terminating = new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATING);
		IConstraint terminated = new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATED);
		cecon.addConstraint(new OrConstraint(new IConstraint[]{terminating, terminated}));
		cecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, ragent));
		
		ObjectCondition	liscon	= new ObjectCondition(OAVBDIRuntimeModel.listenerentry_type);
		liscon.addConstraint(new BoundConstraint(null, listenerentry));
		liscon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.listenerentry_has_relevants, 
			ragent, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
//				Object ragent = assignments.getVariableValue("?ragent");
				Object le	= assignments.getVariableValue("?listenerentry");
				Object ce = assignments.getVariableValue("?ce");
				
				IComponentListener lis	= (IComponentListener)state.getAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_listener);
				ChangeEvent	ae	= new ChangeEvent(BDIAgentFeature.getInterpreter(state).getAgentAdapter().getChildrenIdentifiers(), null,
					state.getAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_value));
				
				String cetype = (String)state.getAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_type);
				if(OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATING.equals(cetype))
					lis.componentTerminating(ae);
				else //if(OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATED.equals(cetype))
					lis.componentTerminated(ae);
			}
		};
		
		Rule listener_termination = new Rule("listener_termination", new AndCondition(new ICondition[]{cecon, liscon}),
			action, IPriorityEvaluator.PRIORITY_1);
		return listener_termination;
	}*/
	
	/**
	 *  Create a rule to notify belief listeners, when a belief has changed.
	 */
	public static Rule createBeliefChangedListenerRule()
	{
		Variable rbelief = new Variable("?rbelief", OAVBDIRuntimeModel.belief_type);
		Variable listenerentry = new Variable("?listenerentry", OAVBDIRuntimeModel.listenerentry_type);
//		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable ce = new Variable("?ce", OAVBDIRuntimeModel.changeevent_type);
		
		ObjectCondition	cecon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		cecon.addConstraint(new BoundConstraint(null, ce));
		cecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTCHANGED));
		cecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rbelief));
		cecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_element, OAVBDIRuntimeModel.belief_type, IOperator.INSTANCEOF));
		
		ObjectCondition	liscon	= new ObjectCondition(OAVBDIRuntimeModel.listenerentry_type);
		liscon.addConstraint(new BoundConstraint(null, listenerentry));
		liscon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.listenerentry_has_relevants, 
			rbelief, IOperator.CONTAINS));
		
//		ObjectCondition capacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
//		capacon.addConstraint(new BoundConstraint(null, rcapa));
//		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_beliefs, rbelief, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
//				Object rcapa = assignments.getVariableValue("?rcapa");
				Object le	= assignments.getVariableValue("?listenerentry");
				Object ce = assignments.getVariableValue("?ce");
				Object rbelief	= assignments.getVariableValue("?rbelief");
				
				Object rcapa = state.getAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_scope);
				IBeliefListener lis	= (IBeliefListener)state.getAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_listener);
				AgentEvent	ae	= new AgentEvent(BeliefFlyweight.getBeliefFlyweight(state, rcapa, rbelief), 
					state.getAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_value));
				lis.beliefChanged(ae);
			}
		};
		
		Rule listener_belief_changed = new Rule("listener_belief_changed", new AndCondition(new ICondition[]{cecon, liscon}),
			action, IPriorityEvaluator.PRIORITY_1);
		return listener_belief_changed;
	}
	
	/**
	 *  Create a rule to notify belief listeners, when a fact is added.
	 * /
	protected static Rule createBeliefSetFactAddedListenerRule()
	{
		Variable rbeliefset = new Variable("?rbeliefset", OAVBDIRuntimeModel.beliefset_type);
		Variable listenerentry = new Variable("?listenerentry", OAVBDIRuntimeModel.listenerentry_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable ce = new Variable("?ce", OAVBDIRuntimeModel.changeevent_type);
		
		ObjectCondition	cecon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		cecon.addConstraint(new BoundConstraint(null, ce));
		cecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED));
		cecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rbeliefset));
		
		ObjectCondition	liscon	= new ObjectCondition(OAVBDIRuntimeModel.listenerentry_type);
		liscon.addConstraint(new BoundConstraint(null, listenerentry));
		liscon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.listenerentry_has_relevants, 
			rbeliefset, IOperator.CONTAINS));
		
		ObjectCondition capacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(null, rcapa));
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_beliefsets, rbeliefset, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object rcapa = assignments.getVariableValue("?rcapa");
				Object le	= assignments.getVariableValue("?listenerentry");
				Object ce = assignments.getVariableValue("?ce");
				Object rbeliefset	= assignments.getVariableValue("?rbeliefset");
				
				IBeliefSetListener lis	= (IBeliefSetListener)state.getAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_listener);
				AgentEvent	ae	= new AgentEvent(new BeliefSetFlyweight(state, rcapa, rbeliefset), ce);
				
				lis.factAdded(ae);
			}
		};
		
		Rule listener_factadded = new Rule("listener_fact_added", new AndCondition(new ICondition[]{cecon, liscon, capacon}),
			action, IPriorityEvaluator.PRIORITY_1);
		return listener_factadded;
	}*/
	
	/**
	 *  Create a rule to notify belief listeners, when a fact is removed.
	 * /
	protected static Rule createBeliefSetFactRemovedListenerRule()
	{
		Variable rbeliefset = new Variable("?rbeliefset", OAVBDIRuntimeModel.beliefset_type);
		Variable listenerentry = new Variable("?listenerentry", OAVBDIRuntimeModel.listenerentry_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable ce = new Variable("?ce", OAVBDIRuntimeModel.changeevent_type);
		
		ObjectCondition	cecon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		cecon.addConstraint(new BoundConstraint(null, ce));
		cecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTREMOVED));
		cecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rbeliefset));
		
		ObjectCondition	liscon	= new ObjectCondition(OAVBDIRuntimeModel.listenerentry_type);
		liscon.addConstraint(new BoundConstraint(null, listenerentry));
		liscon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.listenerentry_has_relevants, 
			rbeliefset, IOperator.CONTAINS));
		
		ObjectCondition capacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(null, rcapa));
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_beliefsets, rbeliefset, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object rcapa = assignments.getVariableValue("?rcapa");
				Object le	= assignments.getVariableValue("?listenerentry");
				Object ce = assignments.getVariableValue("?ce");
				Object rbeliefset	= assignments.getVariableValue("?rbeliefset");
				
				IBeliefSetListener lis	= (IBeliefSetListener)state.getAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_listener);
				AgentEvent	ae	= new AgentEvent(new BeliefSetFlyweight(state, rcapa, rbeliefset), ce);
				
				lis.factAdded(ae);
			}
		};
		
		Rule listener_removed = new Rule("listener_fact_removed", new AndCondition(new ICondition[]{cecon, liscon, capacon}),
			action, IPriorityEvaluator.PRIORITY_1);
		return listener_removed;
	}*/
	
	/**
	 *  Create a rule to notify belief listeners, when a fact is removed.
	 */
	public static Rule createBeliefSetListenerRule()
	{
		Variable rbeliefset = new Variable("?rbeliefset", OAVBDIRuntimeModel.beliefset_type);
		Variable listenerentry = new Variable("?listenerentry", OAVBDIRuntimeModel.listenerentry_type);
//		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable ce = new Variable("?ce", OAVBDIRuntimeModel.changeevent_type);
		
		ObjectCondition	cecon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		cecon.addConstraint(new BoundConstraint(null, ce));
		IConstraint add = new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED);
		IConstraint rem = new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTREMOVED);
		IConstraint change = new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTCHANGED);
		cecon.addConstraint(new OrConstraint(new IConstraint[]{add, rem, change}));
		cecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rbeliefset));
		cecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_element, OAVBDIRuntimeModel.beliefset_type, IOperator.INSTANCEOF));
		
		ObjectCondition	liscon	= new ObjectCondition(OAVBDIRuntimeModel.listenerentry_type);
		liscon.addConstraint(new BoundConstraint(null, listenerentry));
		liscon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.listenerentry_has_relevants, 
			rbeliefset, IOperator.CONTAINS));
		
//		ObjectCondition capacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
//		capacon.addConstraint(new BoundConstraint(null, rcapa));
//		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_beliefsets, rbeliefset, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
//				Object rcapa = assignments.getVariableValue("?rcapa");
				Object ce = assignments.getVariableValue("?ce");
				Object rbeliefset	= assignments.getVariableValue("?rbeliefset");
				Object le	= assignments.getVariableValue("?listenerentry");
			
				Object rcapa = state.getAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_scope);
				IBeliefSetListener lis	= (IBeliefSetListener)state.getAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_listener);
				AgentEvent	ae	= new AgentEvent(BeliefSetFlyweight.getBeliefSetFlyweight(state, rcapa, rbeliefset), 
					state.getAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_value));
				
				String cetype = (String)state.getAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_type);
				if(OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED.equals(cetype))
					lis.factAdded(ae);
				else if(OAVBDIRuntimeModel.CHANGEEVENT_FACTREMOVED.equals(cetype))
					lis.factRemoved(ae);
				else //if(OAVBDIRuntimeModel.CHANGEEVENT_FACTCHANGED.equals(cetype))
					lis.factChanged(ae);
			}
		};
		
		Rule listener_beliefset = new Rule("listener_beliefset", new AndCondition(new ICondition[]{cecon, liscon}),
			action, IPriorityEvaluator.PRIORITY_1);
		return listener_beliefset;
	}
	
	/**
	 *  Create a rule to internal event listeners.
	 */
	public static Rule createInternalEventListenerRule()
	{
		Variable revent = new Variable("?revent", OAVBDIRuntimeModel.internalevent_type);
		Variable mevent = new Variable("?mevent", OAVBDIMetaModel.internalevent_type);
		Variable listenerentry = new Variable("?listenerentry", OAVBDIRuntimeModel.listenerentry_type);
//		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
//		Variable mcapa = new Variable("?mcapa", OAVBDIMetaModel.capability_type);
		Variable ce = new Variable("?ce", OAVBDIRuntimeModel.changeevent_type);
		
		ObjectCondition	cecon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		cecon.addConstraint(new BoundConstraint(null, ce));
		cecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_INTERNALEVENTOCCURRED));
		cecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, revent));
		cecon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.changeevent_has_element, OAVBDIRuntimeModel.element_has_model}, mevent));

//		ObjectCondition eventcon = new ObjectCondition(OAVBDIRuntimeModel.internalevent_type);
//		eventcon.addConstraint(new BoundConstraint(null, revent));
//		eventcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mevent));
		
		ObjectCondition	liscon	= new ObjectCondition(OAVBDIRuntimeModel.listenerentry_type);
		liscon.addConstraint(new BoundConstraint(null, listenerentry));
//		IConstraint relme = new BoundConstraint(OAVBDIRuntimeModel.listenerentry_has_relevants, mevent, IOperator.CONTAINS);
//		IConstraint relre = new BoundConstraint(OAVBDIRuntimeModel.listenerentry_has_relevants, revent, IOperator.CONTAINS);
//		liscon.addConstraint(new OrConstraint(new IConstraint[]{relme, relre}));
		liscon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.listenerentry_has_relevants, mevent, IOperator.CONTAINS));
		
//		ObjectCondition capacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
//		capacon.addConstraint(new BoundConstraint(null, rcapa));
//		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_internalevents, revent));
//		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mcapa));
//		
//		ObjectCondition mcapacon = new ObjectCondition(OAVBDIMetaModel.capability_type);
//		mcapacon.addConstraint(new BoundConstraint(null, mcapa));
//		mcapacon.addConstraint(new BoundConstraint(OAVBDIMetaModel.capability_has_internalevents, mevent, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
//				Object rcapa = assignments.getVariableValue("?rcapa");
				Object revent = assignments.getVariableValue("?revent");
				Object le	= assignments.getVariableValue("?listenerentry");
				Object ce = assignments.getVariableValue("?ce");
				Object rcapa = state.getAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_scope);
				
				IInternalEventListener lis	= (IInternalEventListener)state.getAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_listener);
				AgentEvent	ae	= new AgentEvent(InternalEventFlyweight.getInternalEventFlyweight(state, rcapa, revent), 
						state.getAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_value));
				
				lis.internalEventOccurred(ae);
			}
		};
		
		Rule listener_event = new Rule("listener_internaleventoccurred", new AndCondition(new ICondition[]{cecon, liscon}), //, capacon, mcapacon}),
			action, IPriorityEvaluator.PRIORITY_1);
		return listener_event;
	}
	
	/**
	 *  Create a rule to message event listeners.
	 */
	public static Rule createMessageEventListenerRule()
	{
		Variable revent = new Variable("?revent", OAVBDIRuntimeModel.messageevent_type);
		Variable mevent = new Variable("?mevent", OAVBDIMetaModel.messageevent_type);
		Variable listenerentry = new Variable("?listenerentry", OAVBDIRuntimeModel.listenerentry_type);
//		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
//		Variable mcapa = new Variable("?mcapa", OAVBDIMetaModel.capability_type);
		Variable ce = new Variable("?ce", OAVBDIRuntimeModel.changeevent_type);

		ObjectCondition	cecon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		cecon.addConstraint(new BoundConstraint(null, ce));
		IConstraint msgrec = new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTRECEIVED);
		IConstraint msgsent = new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTSENT);
		cecon.addConstraint(new OrConstraint(new IConstraint[]{msgrec, msgsent}));
		cecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, revent));
		cecon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.changeevent_has_element, OAVBDIRuntimeModel.element_has_model}, mevent));
		
//		ObjectCondition eventcon = new ObjectCondition(OAVBDIRuntimeModel.messageevent_type);
//		eventcon.addConstraint(new BoundConstraint(null, revent));
//		eventcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mevent));
		
		ObjectCondition	liscon	= new ObjectCondition(OAVBDIRuntimeModel.listenerentry_type);
		liscon.addConstraint(new BoundConstraint(null, listenerentry));
		IConstraint relme = new BoundConstraint(OAVBDIRuntimeModel.listenerentry_has_relevants, mevent, IOperator.CONTAINS);
		IConstraint relre = new BoundConstraint(OAVBDIRuntimeModel.listenerentry_has_relevants, revent, IOperator.CONTAINS);
		liscon.addConstraint(new OrConstraint(new IConstraint[]{relme, relre}));
		
//		ObjectCondition capacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
//		capacon.addConstraint(new BoundConstraint(null, rcapa));
//		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mcapa));
//		
//		ObjectCondition mcapacon = new ObjectCondition(OAVBDIMetaModel.capability_type);
//		mcapacon.addConstraint(new BoundConstraint(null, mcapa));
//		mcapacon.addConstraint(new BoundConstraint(OAVBDIMetaModel.capability_has_messageevents, mevent, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
//				Object rcapa = assignments.getVariableValue("?rcapa");
				Object revent = assignments.getVariableValue("?revent");
				Object le	= assignments.getVariableValue("?listenerentry");
				Object ce = assignments.getVariableValue("?ce");
				Object rcapa = state.getAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_scope);

				IMessageEventListener lis	= (IMessageEventListener)state.getAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_listener);
				AgentEvent	ae	= new AgentEvent(MessageEventFlyweight.getMessageEventFlyweight(state, rcapa, revent), 
						state.getAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_value));

				String cetype = (String)state.getAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_type);
				if(OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTRECEIVED.equals(cetype))
					lis.messageEventReceived(ae);
				else
					lis.messageEventSent(ae);
			}
		};
		
		Rule listener_message = new Rule("listener_messageevent", new AndCondition(new ICondition[]{cecon, liscon}),// , capacon, mcapacon}),
			action, IPriorityEvaluator.PRIORITY_1);
		return listener_message;
	}
	
	/**
	 *  Create a rule to notify goal listeners.
	 */
	public static Rule createGoalListenerRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable mgoal = new Variable("?mgoal", OAVBDIMetaModel.goal_type);
		Variable listenerentry = new Variable("?listenerentry", OAVBDIRuntimeModel.listenerentry_type);
//		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
//		Variable mcapa = new Variable("?mcapa", OAVBDIMetaModel.capability_type);
		Variable ce = new Variable("?ce", OAVBDIRuntimeModel.changeevent_type);
		
		ObjectCondition	cecon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		cecon.addConstraint(new BoundConstraint(null, ce));
		IConstraint msgrec = new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_GOALADDED);
		IConstraint msgsent = new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_GOALDROPPED);
		cecon.addConstraint(new OrConstraint(new IConstraint[]{msgrec, msgsent}));
		cecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rgoal));
		cecon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.changeevent_has_element, OAVBDIRuntimeModel.element_has_model}, mgoal));
		
//		ObjectCondition goalcon = new ObjectCondition(OAVBDIRuntimeModel.goal_type);
//		goalcon.addConstraint(new BoundConstraint(null, rgoal));
//		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mgoal));
		
		ObjectCondition	liscon	= new ObjectCondition(OAVBDIRuntimeModel.listenerentry_type);
		liscon.addConstraint(new BoundConstraint(null, listenerentry));
		IConstraint relme = new BoundConstraint(OAVBDIRuntimeModel.listenerentry_has_relevants, mgoal, IOperator.CONTAINS);
		IConstraint relre = new BoundConstraint(OAVBDIRuntimeModel.listenerentry_has_relevants, rgoal, IOperator.CONTAINS);
		liscon.addConstraint(new OrConstraint(new IConstraint[]{relme, relre}));
		
//		ObjectCondition capacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
//		capacon.addConstraint(new BoundConstraint(null, rcapa));
//		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mcapa));
//		
//		ObjectCondition mcapacon = new ObjectCondition(OAVBDIMetaModel.capability_type);
//		mcapacon.addConstraint(new BoundConstraint(null, mcapa));
//		mcapacon.addConstraint(new BoundConstraint(OAVBDIMetaModel.capability_has_goals, mgoal, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object rgoal = assignments.getVariableValue("?rgoal");
				Object le	= assignments.getVariableValue("?listenerentry");
				Object ce = assignments.getVariableValue("?ce");
				Object rcapa = state.getAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_scope);
				
				IGoalListener lis	= (IGoalListener)state.getAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_listener);
				AgentEvent	ae	= new AgentEvent(GoalFlyweight.getGoalFlyweight(state, rcapa, rgoal), 
					state.getAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_value));

				
				String cetype = (String)state.getAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_type);
				if(OAVBDIRuntimeModel.CHANGEEVENT_GOALADDED.equals(cetype))
				{
					lis.goalAdded(ae);
				}
				else
				{
					lis.goalFinished(ae);
					// Remove goal listener if not yet removed, to avoid memory leaks
					if(state.containsObject(le))
					{
						Collection coll = state.getAttributeValues(le, OAVBDIRuntimeModel.listenerentry_has_relevants);
						if(coll!=null && coll.contains(rgoal))
						{
							state.removeAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_relevants, rgoal);
							coll = state.getAttributeValues(le, OAVBDIRuntimeModel.listenerentry_has_relevants);
							if(coll==null || coll.isEmpty())
							{
								state.removeAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_listeners, lis);
							}
							BDIAgentFeature.getInterpreter(state).getEventReificator().removeObservedElement(rgoal);
						}
					}					
				}
				
//				System.err.println("listener_goal rule: "+rgoal+", "+lis+", "+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier());
			}
		};
		
		Rule listener_goal = new Rule("listener_goal", new AndCondition(new ICondition[]{cecon, liscon}), //, capacon, mcapacon}),
			action, IPriorityEvaluator.PRIORITY_1);
		return listener_goal;
	}
	
	/**
	 *  Create a rule to plan listeners.
	 */
	public static Rule createPlanListenerRule()
	{
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable mplan = new Variable("?mplan", OAVBDIMetaModel.plan_type);
		Variable listenerentry = new Variable("?listenerentry", OAVBDIRuntimeModel.listenerentry_type);
//		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
//		Variable mcapa = new Variable("?mcapa", OAVBDIMetaModel.capability_type);
		Variable ce = new Variable("?ce", OAVBDIRuntimeModel.changeevent_type);
		
		ObjectCondition	cecon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		cecon.addConstraint(new BoundConstraint(null, ce));
		IConstraint msgrec = new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_PLANADDED);
		IConstraint msgsent = new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_PLANREMOVED);
		cecon.addConstraint(new OrConstraint(new IConstraint[]{msgrec, msgsent}));
		cecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rplan));
		cecon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.changeevent_has_element, OAVBDIRuntimeModel.element_has_model}, mplan));
		
//		ObjectCondition eventcon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
//		eventcon.addConstraint(new BoundConstraint(null, rplan));
//		eventcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mplan));
		
		ObjectCondition	liscon	= new ObjectCondition(OAVBDIRuntimeModel.listenerentry_type);
		liscon.addConstraint(new BoundConstraint(null, listenerentry));
		IConstraint relme = new BoundConstraint(OAVBDIRuntimeModel.listenerentry_has_relevants, mplan, IOperator.CONTAINS);
		IConstraint relre = new BoundConstraint(OAVBDIRuntimeModel.listenerentry_has_relevants, rplan, IOperator.CONTAINS);
		liscon.addConstraint(new OrConstraint(new IConstraint[]{relme, relre}));
		
//		ObjectCondition capacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
//		capacon.addConstraint(new BoundConstraint(null, rcapa));
//		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mcapa));
//		
//		ObjectCondition mcapacon = new ObjectCondition(OAVBDIMetaModel.capability_type);
//		mcapacon.addConstraint(new BoundConstraint(null, mcapa));
//		mcapacon.addConstraint(new BoundConstraint(OAVBDIMetaModel.capability_has_goals, mgoal, IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
//				Object rcapa = assignments.getVariableValue("?rcapa");
				Object rplan = assignments.getVariableValue("?rplan");
				Object le	= assignments.getVariableValue("?listenerentry");
				Object ce = assignments.getVariableValue("?ce");
				Object rcapa = state.getAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_scope);
				
				IPlanListener lis	= (IPlanListener)state.getAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_listener);
				AgentEvent	ae	= new AgentEvent(PlanFlyweight.getPlanFlyweight(state, rcapa, rplan), 
					state.getAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_value));
				
				String cetype = (String)state.getAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_type);
				if(OAVBDIRuntimeModel.CHANGEEVENT_PLANADDED.equals(cetype))
				{
					lis.planAdded(ae);
				}
				else
				{
					lis.planFinished(ae);
					// Remove plan listener if not yet removed, to avoid memory leaks
					if(state.containsObject(le))
					{
						Collection coll = state.getAttributeValues(le, OAVBDIRuntimeModel.listenerentry_has_relevants);
						if(coll!=null && coll.contains(rplan))
						{
							state.removeAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_relevants, rplan);
							coll = state.getAttributeValues(le, OAVBDIRuntimeModel.listenerentry_has_relevants);
							if(coll==null || coll.isEmpty())
							{
								state.removeAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_listeners, lis);
							}
							BDIAgentFeature.getInterpreter(state).getEventReificator().removeObservedElement(rplan);
						}
					}
				}
			}
		};
		
		Rule listener_plan = new Rule("listener_plan", new AndCondition(new ICondition[]{cecon, liscon}), //, capacon, mcapacon}),
			action, IPriorityEvaluator.PRIORITY_1);
		return listener_plan;
	}
}
