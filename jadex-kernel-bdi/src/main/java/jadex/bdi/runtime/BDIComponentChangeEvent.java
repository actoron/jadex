package jadex.bdi.runtime;

import jadex.bdi.runtime.impl.flyweights.BeliefFlyweight;
import jadex.bdi.runtime.impl.flyweights.BeliefSetFlyweight;
import jadex.bdi.runtime.impl.flyweights.CapabilityFlyweight;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.impl.flyweights.GoalFlyweight;
import jadex.bdi.runtime.impl.flyweights.InternalEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.MessageEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.PlanFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.ComponentChangeEvent;
import jadex.rules.state.IOAVState;

public class BDIComponentChangeEvent extends ComponentChangeEvent
{
	public static String SOURCE_CATEGORY_PLAN	   = "Plan";
	public static String SOURCE_CATEGORY_GOAL	   = "Goal";
	public static String SOURCE_CATEGORY_FACT	   = "Fact";
	public static String SOURCE_CATEGORY_MESSAGE   = "Message";
	public static String SOURCE_CATEGORY_COMPONENT = "Component";
	public static String SOURCE_CATEGORY_IEVENT	   = "Internal Event";
	
	public BDIComponentChangeEvent(IOAVState state, Object element, Object scope, String type, Object value, long time)
	{
		BDIInterpreter bdiint = BDIInterpreter.getInterpreter(state);
		setComponent(bdiint.getAgentAdapter().getComponentIdentifier());
		if (scope == null)
			scope = bdiint.getAgent();
		
		setTime(time);
		
		if (OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED.equals(type) ||
			OAVBDIRuntimeModel.CHANGEEVENT_GOALADDED.equals(type) ||
			OAVBDIRuntimeModel.CHANGEEVENT_PLANADDED.equals(type))
				setEventType(EVENT_TYPE_CREATION);
		else if (OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATED.equals(type) ||
				 OAVBDIRuntimeModel.CHANGEEVENT_FACTREMOVED.equals(type) ||
				 OAVBDIRuntimeModel.CHANGEEVENT_GOALDROPPED.equals(type) ||
				 OAVBDIRuntimeModel.CHANGEEVENT_PLANREMOVED.equals(type))
					setEventType(EVENT_TYPE_DISPOSAL);
		else if (OAVBDIRuntimeModel.CHANGEEVENT_FACTCHANGED.equals(type))
					setEventType(EVENT_TYPE_MODIFICATION);
		else if (OAVBDIRuntimeModel.CHANGEEVENT_INTERNALEVENTOCCURRED.equals(type) ||
				 OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTRECEIVED.equals(type) ||
				 OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTSENT.equals(type) ||
				 OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATING.equals(type))
					setEventType(EVENT_TYPE_OCCURRENCE);
		
		// Default reason
		setReason("Unknown");
		
		if (OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED.equals(type) ||
			OAVBDIRuntimeModel.CHANGEEVENT_FACTCHANGED.equals(type) ||
			OAVBDIRuntimeModel.CHANGEEVENT_FACTREMOVED.equals(type))
		{
			setSourceCategory(SOURCE_CATEGORY_FACT);
			if (OAVBDIRuntimeModel.belief_type.equals(state.getType(element)))
			{
				BeliefFlyweight bf = BeliefFlyweight.getBeliefFlyweight(state, scope, element);
				setSourceType(bf.getModelElement().getName());
			}
			else if (OAVBDIRuntimeModel.beliefset_type.equals(state.getType(element)))
			{
				BeliefSetFlyweight bf = BeliefSetFlyweight.getBeliefSetFlyweight(state, scope, element);
				setSourceType(bf.getModelElement().getName());
			}
		}
		else if (OAVBDIRuntimeModel.CHANGEEVENT_PLANADDED.equals(type) ||
				 OAVBDIRuntimeModel.CHANGEEVENT_PLANREMOVED.equals(type))
		{
			setSourceCategory(SOURCE_CATEGORY_PLAN);
			PlanFlyweight pf = PlanFlyweight.getPlanFlyweight(state, scope, element);
			setSourceName(pf.getHandle().toString());
			setSourceType(pf.getType());
		}
		else if (OAVBDIRuntimeModel.CHANGEEVENT_GOALADDED.equals(type) ||
				 OAVBDIRuntimeModel.CHANGEEVENT_GOALDROPPED.equals(type))
		{
			setSourceCategory(SOURCE_CATEGORY_GOAL);
			GoalFlyweight gf = GoalFlyweight.getGoalFlyweight(state, scope, element);
			setSourceName(gf.getHandle().toString());
			setSourceType(gf.getType());
			if (OAVBDIRuntimeModel.CHANGEEVENT_GOALDROPPED.equals(type))
			{
				if (gf.isSucceeded())
					setReason("Success");
				else if (gf.getException() != null)
					setReason(gf.getException().toString());
			}
		}
		else if (OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTRECEIVED.equals(type) ||
				 OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTSENT.equals(type))
		{
			setSourceCategory(SOURCE_CATEGORY_MESSAGE);
			MessageEventFlyweight mef = MessageEventFlyweight.getMessageEventFlyweight(state, scope, element);
			setSourceName(mef.getHandle().toString());
			setSourceType(mef.getType());
		}
		else if (OAVBDIRuntimeModel.CHANGEEVENT_INTERNALEVENTOCCURRED.equals(type))
		{
			setSourceCategory(SOURCE_CATEGORY_IEVENT);
			InternalEventFlyweight ief = InternalEventFlyweight.getInternalEventFlyweight(state, scope, element);
			setSourceName(ief.getHandle().toString());
			setSourceType(ief.getType());
		}
		else if (OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATING.equals(type) ||
				 OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATED.equals(type))
		{
			setSourceCategory(SOURCE_CATEGORY_COMPONENT);
			CapabilityFlyweight cf = new CapabilityFlyweight(state, scope);
			setSourceName(cf.getComponentIdentifier().getName());
			setSourceType(cf.getAgentModel().getName());
			if (OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATING.equals(type))
				setReason("Terminating");
			else
				setReason("Terminated");
		}
	}
}
