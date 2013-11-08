package jadex.bdi.runtime.interpreter;

import jadex.bdi.runtime.impl.flyweights.CapabilityFlyweight;
import jadex.bdi.runtime.impl.flyweights.GoalFlyweight;
import jadex.bdi.runtime.impl.flyweights.InternalEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.MessageEventFlyweight;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IOAVStateListener;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  This object is responsible for inserting changeevent
 *  objects into the state that represent (i.e. reify)
 *  state changes (such as new fact value of belief).
 */
public class EventReificator implements IOAVStateListener
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;
	
	/** The agent. */
	protected Object agent;
	
	/** The model/runtime elements, which are observed (element -> cnt). */
	protected Map	observed;
	
	//-------- constructors --------
	
	/**
	 *  Create and register an event reificator.
	 */
	public EventReificator(IOAVState state, Object agent)
	{
		this.state = state;
		this.agent = agent;
		this.observed	= new HashMap();
		state.addStateListener(this, false);
	}
	
	//-------- IOAVStateListener interface --------
	
	/**
	 *  Notification when an attribute value of an object has been set.
	 *  @param id The object id.
	 *  @param type The object type.
	 *  @param attr The attribute type.
	 *  @param oldvalue The oldvalue.
	 *  @param newvalue The newvalue.
	 */
	public void objectModified(Object id, OAVObjectType type, OAVAttributeType 
		attr, Object oldvalue, Object newvalue)
	{
		// Push bean changes up to referring belief(set)s and/or parameter(set)s
		if(type instanceof OAVJavaType)
		{
			Collection	 refs	= state.getReferencingObjects(id);
			for(Iterator it=refs.iterator(); it.hasNext(); )
			{
				Object	ref	= it.next();
//				System.err.println("id: "+id);
				type	= state.getType(ref);
				if(type.isSubtype(OAVBDIRuntimeModel.belief_type)
					|| type.isSubtype(OAVBDIRuntimeModel.beliefset_type))
				{
					// Hack!!! Use belief_has_fact for beliefsets also, to create FACTCHANGED (instead added/removed).
					objectModified(ref, type, OAVBDIRuntimeModel.belief_has_fact, id, id);
				}
				else if(type.isSubtype(OAVBDIRuntimeModel.parameter_type))
				{
					objectModified(ref, type, OAVBDIRuntimeModel.parameter_has_value, id, id);
				}
				else if(type.isSubtype(OAVBDIRuntimeModel.parameterset_type))
				{
					objectModified(ref, type, OAVBDIRuntimeModel.parameterset_has_values, id, id);
				}
			}
		}
		
		// Handle OAV object change.
		else
		{
			if(attr.equals(OAVBDIRuntimeModel.agent_has_state))
			{
				if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATING.equals(newvalue))
				{
//					System.out.println("agent dying: "+newvalue+" "+id);
					createChangeEvent(id, id, OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATING, null);
				}
				
				// Now done in abstract interpreter
//				else if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATED.equals(newvalue))
//				{
////					System.out.println("agent died: "+newvalue+" "+id);
//					createChangeEvent(id, id, OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATED, null);
//				}
			}
			else if(OAVBDIRuntimeModel.belief_has_fact.equals(attr))
			{
//				System.out.println("fact changed: "+id+", "+newvalue);
				createChangeEvent(id, null, OAVBDIRuntimeModel.CHANGEEVENT_FACTCHANGED, newvalue);
			}
			else if(OAVBDIRuntimeModel.beliefset_has_facts.equals(attr))
			{
				if(oldvalue==null)
				{
//					System.out.println("fact added: "+id+", "+newvalue);
					createChangeEvent(id, null, OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED, newvalue);
				}
				else if(newvalue==null)
				{
//					System.out.println("fact removed: "+id+", "+oldvalue);
					createChangeEvent(id, null, OAVBDIRuntimeModel.CHANGEEVENT_FACTREMOVED, oldvalue);
				}
				else
				{
//					System.out.println("fact removed: "+id+", "+oldvalue);
					createChangeEvent(id, null, OAVBDIRuntimeModel.CHANGEEVENT_FACTCHANGED, newvalue);
				}
			}
			else if(OAVBDIRuntimeModel.capability_has_internalevents.equals(attr) && newvalue!=null)
			{
//				System.out.println("internal event occurred: "+newvalue);
				createChangeEvent(newvalue, id, OAVBDIRuntimeModel.CHANGEEVENT_INTERNALEVENTOCCURRED, null);
			}
			else if(OAVBDIRuntimeModel.capability_has_messageevents.equals(attr) && newvalue!=null)
			{
//				System.out.println("message event received: "+newvalue);
				createChangeEvent(newvalue, id, OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTRECEIVED, null);
			}
			else if(OAVBDIRuntimeModel.capability_has_outbox.equals(attr) && newvalue!=null)
			{
//				System.out.println("message event sent: "+newvalue);
				createChangeEvent(newvalue, id, OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTSENT, null);
			}
			else if(OAVBDIRuntimeModel.capability_has_goals.equals(attr))
			{
				if(newvalue!=null)
				{
//					System.out.println("goal added: "+newvalue+" "+state.getAttributeValue(newvalue, OAVBDIRuntimeModel.element_has_model));
					createChangeEvent(newvalue, id, OAVBDIRuntimeModel.CHANGEEVENT_GOALADDED, null);
				}
				else
				{
//					System.out.println("goal finished: "+id+" "+attr+" "+oldvalue+" "+state.getAttributeValue(oldvalue, OAVBDIRuntimeModel.element_has_model));
					createChangeEvent(oldvalue, id, OAVBDIRuntimeModel.CHANGEEVENT_GOALDROPPED, null);
				}
			}
			else if(OAVBDIRuntimeModel.capability_has_plans.equals(attr))
			{
				if(newvalue!=null)
				{
//					System.out.println("plan added: "+newvalue);
					createChangeEvent(newvalue, id, OAVBDIRuntimeModel.CHANGEEVENT_PLANADDED, null);
				}
				else
				{
//					System.out.println("plan finished: "+oldvalue);
					createChangeEvent(oldvalue, id, OAVBDIRuntimeModel.CHANGEEVENT_PLANREMOVED, null);
				}
			}

			// Goal changed. (Hack!!! Only required for BDI viewer)
			else if(OAVBDIRuntimeModel.goal_has_lifecyclestate.equals(attr)
					|| OAVBDIRuntimeModel.goal_has_processingstate.equals(attr))
			{
				fireComponentChangeEvent(state, id, null, OAVBDIRuntimeModel.CHANGEEVENT_GOALCHANGED, null);
			}
			// Plan changed. (Hack!!! Only required for BDI viewer)
			else if(OAVBDIRuntimeModel.plan_has_lifecyclestate.equals(attr))
			{
				fireComponentChangeEvent(state, id, null, OAVBDIRuntimeModel.CHANGEEVENT_PLANCHANGED, null);
			}
		}
	}
	
	// only for debugging
//	protected int objcnt=0;
//	protected OAVObjectType objtype=OAVBDIRuntimeModel.plan_type;
//	protected Set	unrefs	= new LinkedHashSet();
	
	/**
	 *  Notification when an object has been added to the state.
	 *  @param id The object id.
	 *  @param type The object type.
	 */
	public void objectAdded(Object id, OAVObjectType type, boolean root)
	{
		// ignored for now...
		
//		// only for debugging
//		if(type.isSubtype(objtype))
//		{
//			objcnt++;
//			System.err.println(objtype.toString()+" added: "+objcnt);
//		}
	}
	
	/**
	 *  Notification when an object has been removed from state.
	 *  @param id The object id.
	 *  @param type The object type.
	 */
	public void objectRemoved(Object id, OAVObjectType type)
	{
		// ignored for now...

//		// only for debugging
//		if(type.isSubtype(objtype))
//		{
////			objcnt--;
////			System.err.println(objtype.toString()+" removed: "+objcnt);
//			boolean	test	= state.containsObject(id);
//			assert test==false;
//			if(!test && state.getReferencingObjects(id)!=null
//				&& !state.getReferencingObjects(id).isEmpty())
//			{
//				final	Object	fid	= id;
//				new Thread(new Runnable()
//				{
//					public void run()
//					{
//						synchronized(unrefs)
//						{
//							unrefs.add(fid);
//							BDIInterpreter	bdii	= BDIInterpreter.getInterpreter(state);
//							if(bdii!=null)
//								System.out.println("+ext.referenced plans("+bdii.getAgentAdapter().getComponentIdentifier().getLocalName()+"): "+unrefs);
//						}
//						while(state.getReferencingObjects(fid)!=null
//							&& !state.getReferencingObjects(fid).isEmpty())
//						{
//							try
//							{
//								Thread.sleep(1000);
//							}
//							catch(InterruptedException e)
//							{
//							}
//						}
//						synchronized(unrefs)
//						{
//							unrefs.remove(fid);
//							BDIInterpreter	bdii	= BDIInterpreter.getInterpreter(state);
//							if(bdii!=null)
//								System.out.println("-ext.referenced plans("+bdii.getAgentAdapter().getComponentIdentifier().getLocalName()+"): "+unrefs);
//						}
//					}
//				}).start();
//			}
//		}
	}
	
	/**
	 *  Create a change event.
	 *  @param element The element source.
	 *  @param type The event type.
	 *  @param value The optional value.
	 */
	protected void	createChangeEvent(Object element, Object scope, String type, Object value)
	{
		assert element!=null;
		
		fireComponentChangeEvent(state, element, scope, type, value);
		
		boolean	create;
		// Hack! Special case for message events.
		if(state.getType(element).isSubtype(OAVBDIRuntimeModel.messageevent_type) 
			&& state.getAttributeValue(element, OAVBDIRuntimeModel.messageevent_has_original)!=null)
		{
			create = observed.containsKey(state.getAttributeValue(element, OAVBDIRuntimeModel.messageevent_has_original));
		}
		else
		{
			create = observed.containsKey(element);
		}
		
		if(!create)
		{
			Object	melement = state.getAttributeValue(element, OAVBDIRuntimeModel.element_has_model);
			create = observed.containsKey(melement);
		}
		
		if(create)
		{
			Object	ce	= state.createObject(OAVBDIRuntimeModel.changeevent_type);
			state.setAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_element, element);
			state.setAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_type, type);
			if(scope!=null)
				state.setAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_scope, scope);
			if(value!=null)
				state.setAttributeValue(ce, OAVBDIRuntimeModel.changeevent_has_value, value);
			state.addAttributeValue(agent, OAVBDIRuntimeModel.agent_has_changeevents, ce);
		}
	}
	
	/**
	 *  Add an element to the list of observed elements.
	 *  Only for observed elements, change events will be generated.
	 */
	public void	addObservedElement(Object element)
	{
		Integer	cnt	= (Integer)observed.get(element);
		if(cnt==null)
			cnt	= new Integer(1);
		else
			cnt	= new Integer(cnt.intValue()+1);
		observed.put(element, cnt);
	}
	
	/**
	 *  Remove an element from the list of observed elements.
	 */
	public void	removeObservedElement(Object element)
	{
		Integer	cnt	= (Integer)observed.get(element);
		if(cnt.intValue()>1)
			cnt	= new Integer(cnt.intValue()-1);
		else
			observed.remove(element);
	}
	
//	protected void fireComponentChangeEvent(IOAVState state, Object element, Object scope, String type, Object value)
//	{
//		Collection componentlisteners = state.getAttributeValues(agent, OAVBDIRuntimeModel.agent_has_componentlisteners);
//		if (componentlisteners != null && !componentlisteners.isEmpty())
//		{
//			BDIInterpreter bdiint = BDIInterpreter.getInterpreter(state);
//			long time = bdiint.getClockService().getTime();
//			ComponentChangeEvent event = new ComponentChangeEvent();
//			event.setComponent(bdiint.getAgentAdapter().getComponentIdentifier());
//			if(scope == null)
//				scope = bdiint.getAgent();
//			
//			event.setTime(time);
//			event.setComponentCreationTime(bdiint.getComponentDescription().getCreationTime());
//			
//			if(OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED.equals(type) ||
//				OAVBDIRuntimeModel.CHANGEEVENT_GOALADDED.equals(type) ||
//				OAVBDIRuntimeModel.CHANGEEVENT_PLANADDED.equals(type))
//			{
//				event.setEventType(ComponentChangeEvent.EVENT_TYPE_CREATION);
//			}
//			else if(OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATED.equals(type) ||
//				 OAVBDIRuntimeModel.CHANGEEVENT_FACTREMOVED.equals(type) ||
//				 OAVBDIRuntimeModel.CHANGEEVENT_GOALDROPPED.equals(type) ||
//				 OAVBDIRuntimeModel.CHANGEEVENT_PLANREMOVED.equals(type))
//			{
//				event.setEventType(ComponentChangeEvent.EVENT_TYPE_DISPOSAL);
//			}
//			else if(OAVBDIRuntimeModel.CHANGEEVENT_FACTCHANGED.equals(type) ||
//				OAVBDIRuntimeModel.CHANGEEVENT_GOALCHANGED.equals(type) ||
//				OAVBDIRuntimeModel.CHANGEEVENT_PLANCHANGED.equals(type))
//			{
//				event.setEventType(ComponentChangeEvent.EVENT_TYPE_MODIFICATION);
//			}
//			else if (OAVBDIRuntimeModel.CHANGEEVENT_INTERNALEVENTOCCURRED.equals(type) ||
//					 OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTRECEIVED.equals(type) ||
//					 OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTSENT.equals(type) ||
//					 OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATING.equals(type))
//			{		
//				event.setEventType(ComponentChangeEvent.EVENT_TYPE_OCCURRENCE);
//			}
//			
//			if (OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED.equals(type) ||
//				OAVBDIRuntimeModel.CHANGEEVENT_FACTCHANGED.equals(type) ||
//				OAVBDIRuntimeModel.CHANGEEVENT_FACTREMOVED.equals(type))
//			{
//				BeliefInfo	info	= BeliefInfo.createBeliefInfo(state, element, scope);
//				event.setSourceCategory(ComponentChangeEvent.SOURCE_CATEGORY_FACT);
//				event.setSourceName(element.toString());
//				event.setSourceType(info.getType());
//				event.setDetails(info);
//			}
//			else if(OAVBDIRuntimeModel.CHANGEEVENT_PLANADDED.equals(type) ||
//				OAVBDIRuntimeModel.CHANGEEVENT_PLANCHANGED.equals(type) ||
//				OAVBDIRuntimeModel.CHANGEEVENT_PLANREMOVED.equals(type))
//			{
//				PlanInfo	info	= PlanInfo.createPlanInfo(state, element, scope);
//				event.setSourceCategory(ComponentChangeEvent.SOURCE_CATEGORY_PLAN);
//				event.setSourceName(element.toString());
//				event.setSourceType(info.getType());
//				Object reason = state.getAttributeValue(element, OAVBDIRuntimeModel.plan_has_reason);
//				if (reason != null)
//					event.setParent(reason.toString());
//				event.setDetails(info);
//			}
//			else if(OAVBDIRuntimeModel.CHANGEEVENT_GOALADDED.equals(type) ||
//				 OAVBDIRuntimeModel.CHANGEEVENT_GOALCHANGED.equals(type) ||
//				 OAVBDIRuntimeModel.CHANGEEVENT_GOALDROPPED.equals(type))
//			{
//				GoalInfo	info	= GoalInfo.createGoalInfo(state, element, scope);
//				event.setSourceCategory(ComponentChangeEvent.SOURCE_CATEGORY_GOAL);
//				event.setSourceName(element.toString());
//				event.setSourceType(info.getType());
//				event.setDetails(info);
//				Object parentplan = state.getAttributeValue(element, OAVBDIRuntimeModel.goal_has_parentplan);
//				if (parentplan != null)
//				{
//					event.setParent(parentplan.toString());
//				}
//				else
//				{
////					GoalFlyweight x = GoalFlyweight.getGoalFlyweight(state, scope, element);
//					
////					System.out.println("This goal has no parent plan: " + x.getType() + " " + type);
//					//event.setParent(bdiint.getAgentAdapter().getComponentIdentifier().getName());
//				}
//				if (OAVBDIRuntimeModel.CHANGEEVENT_GOALDROPPED.equals(type))
//				{
//					GoalFlyweight gf = GoalFlyweight.getGoalFlyweight(state, scope, element);
//					if (gf.isSucceeded())
//					{
//						event.setReason("Success");
//					}
//					else if (gf.getException() != null)
//					{
//						event.setReason(gf.getException().toString());
////						gf.getException().printStackTrace();
//					}
//				}
//			}
//			else if (OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTRECEIVED.equals(type) ||
//					 OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTSENT.equals(type))
//			{
//				event.setSourceCategory(ComponentChangeEvent.SOURCE_CATEGORY_MESSAGE);
//				MessageEventFlyweight mef = MessageEventFlyweight.getMessageEventFlyweight(state, scope, element);
//				event.setSourceName(mef.getHandle().toString());
//				event.setSourceType(mef.getType());
//			}
//			else if (OAVBDIRuntimeModel.CHANGEEVENT_INTERNALEVENTOCCURRED.equals(type))
//			{
//				event.setSourceCategory(ComponentChangeEvent.SOURCE_CATEGORY_IEVENT);
//				InternalEventFlyweight ief = InternalEventFlyweight.getInternalEventFlyweight(state, scope, element);
//				event.setSourceName(ief.getHandle().toString());
//				event.setSourceType(ief.getType());
//			}
//			else if (OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATING.equals(type) ||
//					 OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATED.equals(type))
//			{
//				event.setSourceCategory(ComponentChangeEvent.SOURCE_CATEGORY_COMPONENT);
//				CapabilityFlyweight cf = new CapabilityFlyweight(state, scope);
//				event.setSourceName(cf.getComponentIdentifier().getName());
//				event.setSourceType(cf.getAgentModel().getName());
//				if (OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATING.equals(type))
//					event.setDetails("Terminating");
//			}
//			
//			bdiint.notifyListeners(event);
//		}
//	}
	
	protected void fireComponentChangeEvent(IOAVState state, Object element, Object scope, String type, Object value)
	{
//		Collection componentlisteners = state.getAttributeValues(agent, OAVBDIRuntimeModel.agent_has_componentlisteners);
		BDIInterpreter bdiint = BDIInterpreter.getInterpreter(state);
		
//		if(componentlisteners != null && !componentlisteners.isEmpty())
		
		if((bdiint.subscriptions!=null && !bdiint.subscriptions.isEmpty()) || bdiint.getter.getLastService()!=null)
		{
//			BDIInterpreter bdiint = BDIInterpreter.getInterpreter(state);
			long time = bdiint.getClockService().getTime();
//			ComponentChangeEvent event = new ComponentChangeEvent();
			MonitoringEvent event = new MonitoringEvent();
			event.setLevel(PublishEventLevel.FINE);
//			event.setComponent(bdiint.getAgentAdapter().getComponentIdentifier());
			event.setSourceIdentifier(bdiint.getAgentAdapter().getComponentIdentifier());
			if(scope == null)
				scope = bdiint.getAgent();
			
			event.setTime(time);
			// todo:
//			event.setComponentCreationTime(bdiint.getComponentDescription().getCreationTime());
			
			if(OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED.equals(type) ||
				OAVBDIRuntimeModel.CHANGEEVENT_GOALADDED.equals(type) ||
				OAVBDIRuntimeModel.CHANGEEVENT_PLANADDED.equals(type))
			{
				event.setType(IMonitoringEvent.EVENT_TYPE_CREATION);
			}
			else if(OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATED.equals(type) ||
				 OAVBDIRuntimeModel.CHANGEEVENT_FACTREMOVED.equals(type) ||
				 OAVBDIRuntimeModel.CHANGEEVENT_GOALDROPPED.equals(type) ||
				 OAVBDIRuntimeModel.CHANGEEVENT_PLANREMOVED.equals(type))
			{
				event.setType(IMonitoringEvent.EVENT_TYPE_DISPOSAL);
			}
			else if(OAVBDIRuntimeModel.CHANGEEVENT_FACTCHANGED.equals(type) ||
				OAVBDIRuntimeModel.CHANGEEVENT_GOALCHANGED.equals(type) ||
				OAVBDIRuntimeModel.CHANGEEVENT_PLANCHANGED.equals(type))
			{
				event.setType(IMonitoringEvent.EVENT_TYPE_MODIFICATION);
			}
			else if (OAVBDIRuntimeModel.CHANGEEVENT_INTERNALEVENTOCCURRED.equals(type) ||
					 OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTRECEIVED.equals(type) ||
					 OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTSENT.equals(type) ||
					 OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATING.equals(type))
			{		
				event.setType(IMonitoringEvent.EVENT_TYPE_OCCURRENCE);
			}
			
			if (OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED.equals(type) ||
				OAVBDIRuntimeModel.CHANGEEVENT_FACTCHANGED.equals(type) ||
				OAVBDIRuntimeModel.CHANGEEVENT_FACTREMOVED.equals(type))
			{
				BeliefInfo	info	= BeliefInfo.createBeliefInfo(state, element, scope);
//				event.setSourceCategory(ComponentChangeEvent.SOURCE_CATEGORY_FACT);
//				event.setSourceName(element.toString());
//				event.setSourceType(info.getType());
//				event.setDetails(info);
				event.setType(event.getType()+"."+IMonitoringEvent.SOURCE_CATEGORY_FACT);
				event.setProperty("sourcename", element.toString());
				event.setProperty("sourcetype", info.getType());
				event.setProperty("details", info);
			}
			else if(OAVBDIRuntimeModel.CHANGEEVENT_PLANADDED.equals(type) ||
				OAVBDIRuntimeModel.CHANGEEVENT_PLANCHANGED.equals(type) ||
				OAVBDIRuntimeModel.CHANGEEVENT_PLANREMOVED.equals(type))
			{
				PlanInfo	info	= PlanInfo.createPlanInfo(state, element, scope);
//				event.setSourceCategory(ComponentChangeEvent.SOURCE_CATEGORY_PLAN);
//				event.setSourceName(element.toString());
//				event.setSourceType(info.getType());
				Object reason = state.getAttributeValue(element, OAVBDIRuntimeModel.plan_has_reason);
//				if(reason != null)
//					event.setParent(reason.toString());
//				event.setDetails(info);
				
				event.setType(event.getType()+"."+IMonitoringEvent.SOURCE_CATEGORY_PLAN);
				event.setProperty("sourcename", element.toString());
				event.setProperty("sourcetype", info.getType());
				event.setProperty("details", info);
				if(reason != null)
					event.setProperty("parent", reason.toString());
			}
			else if(OAVBDIRuntimeModel.CHANGEEVENT_GOALADDED.equals(type) ||
				 OAVBDIRuntimeModel.CHANGEEVENT_GOALCHANGED.equals(type) ||
				 OAVBDIRuntimeModel.CHANGEEVENT_GOALDROPPED.equals(type))
			{
				GoalInfo	info	= GoalInfo.createGoalInfo(state, element, scope);
//				event.setSourceCategory(ComponentChangeEvent.SOURCE_CATEGORY_GOAL);
//				event.setSourceName(element.toString());
//				event.setSourceType(info.getType());
//				event.setDetails(info);
				
				event.setType(event.getType()+"."+IMonitoringEvent.SOURCE_CATEGORY_GOAL);
				event.setProperty("sourcename", element.toString());
				event.setProperty("sourcetype", info.getType());
				event.setProperty("details", info);
				
				Object parentplan = state.getAttributeValue(element, OAVBDIRuntimeModel.goal_has_parentplan);
				if (parentplan != null)
				{
//					event.setParent(parentplan.toString());
					event.setProperty("parent", parentplan.toString());
				}
				else
				{
//					GoalFlyweight x = GoalFlyweight.getGoalFlyweight(state, scope, element);
					
//					System.out.println("This goal has no parent plan: " + x.getType() + " " + type);
					//event.setParent(bdiint.getAgentAdapter().getComponentIdentifier().getName());
				}
				if (OAVBDIRuntimeModel.CHANGEEVENT_GOALDROPPED.equals(type))
				{
					GoalFlyweight gf = GoalFlyweight.getGoalFlyweight(state, scope, element);
					if(gf.isSucceeded())
					{
//						event.setReason("Success");
						event.setProperty("reason", "Success");
					}
					else if(gf.getException() != null)
					{
//						event.setReason(gf.getException().toString());
						event.setProperty("reason", gf.getException().toString());
//						gf.getException().printStackTrace();
					}
				}
			}
			else if (OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTRECEIVED.equals(type) ||
					 OAVBDIRuntimeModel.CHANGEEVENT_MESSAGEEVENTSENT.equals(type))
			{
//				event.setSourceCategory(ComponentChangeEvent.SOURCE_CATEGORY_MESSAGE);
				MessageEventFlyweight mef = MessageEventFlyweight.getMessageEventFlyweight(state, scope, element);
//				event.setSourceName(mef.getHandle().toString());
//				event.setSourceType(mef.getType());
				
				event.setType(event.getType()+"."+IMonitoringEvent.SOURCE_CATEGORY_MESSAGE);
				event.setProperty("sourcename", mef.getHandle().toString());
				event.setProperty("sourcetype", mef.getType());
			}
			else if (OAVBDIRuntimeModel.CHANGEEVENT_INTERNALEVENTOCCURRED.equals(type))
			{
//				event.setSourceCategory(ComponentChangeEvent.SOURCE_CATEGORY_IEVENT);
				InternalEventFlyweight ief = InternalEventFlyweight.getInternalEventFlyweight(state, scope, element);
//				event.setSourceName(ief.getHandle().toString());
//				event.setSourceType(ief.getType());
				
				event.setType(event.getType()+"."+IMonitoringEvent.SOURCE_CATEGORY_IEVENT);
				event.setProperty("sourcename", ief.getHandle().toString());
				event.setProperty("sourcetype", ief.getType());

			}
			else if (OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATING.equals(type) ||
					 OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATED.equals(type))
			{
//				event.setSourceCategory(ComponentChangeEvent.SOURCE_CATEGORY_COMPONENT);
				CapabilityFlyweight cf = new CapabilityFlyweight(state, scope);
//				event.setSourceName(cf.getComponentIdentifier().getName());
//				event.setSourceType(cf.getAgentModel().getName());
				if(OAVBDIRuntimeModel.CHANGEEVENT_AGENTTERMINATING.equals(type))
					event.setProperty("details", "Terminating");
//					event.setDetails("Terminating");
				
				event.setType(event.getType()+"."+IMonitoringEvent.SOURCE_CATEGORY_COMPONENT);
				event.setProperty("sourcename", cf.getComponentIdentifier().getName());
				event.setProperty("sourcetype", cf.getAgentModel().getName());
			}
			
//			bdiint.notifyListeners(event);
			bdiint.publishEvent(event, PublishTarget.TOALL);
		}
	}
}
