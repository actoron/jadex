package jadex.bdi.runtime.interpreter;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IParameterElement;
import jadex.bdi.runtime.impl.flyweights.BeliefbaseFlyweight;
import jadex.bdi.runtime.impl.flyweights.CapabilityFlyweight;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.impl.flyweights.EventbaseFlyweight;
import jadex.bdi.runtime.impl.flyweights.ExpressionbaseFlyweight;
import jadex.bdi.runtime.impl.flyweights.GoalFlyweight;
import jadex.bdi.runtime.impl.flyweights.GoalbaseFlyweight;
import jadex.bdi.runtime.impl.flyweights.InternalEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.MessageEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.PlanFlyweight;
import jadex.bdi.runtime.impl.flyweights.PlanbaseFlyweight;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.message.MessageType;
import jadex.javaparser.SimpleValueFetcher;
import jadex.rules.state.IOAVState;

import java.lang.reflect.Array;

/**
 *  Fetcher allows to inject parameters during expression/condition evaluation. 
 */
public class OAVBDIFetcher extends SimpleValueFetcher
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState state;
	
	/** The agent adapter. */
	protected IComponentAdapter adapter;
	
	/** The capability. */
	protected Object rcapa;
	
	/** The rgoal (if any). */
	protected Object rgoal;
	
	/** The rplan (if any). */
	protected Object rplan;
	
	/** The rmessageevent (if any). */
	protected Object rmessageevent;
	
	/** The rinternalevent (if any). */
	protected Object rinternalevent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new fetcher.
	 */
	public OAVBDIFetcher(IOAVState state, Object rcapa)
	{
		this.state = state;
		this.adapter = BDIInterpreter.getInterpreter(state).getAgentAdapter();
		this.rcapa = rcapa;
	}
	
	/**
	 *  Create a new fetcher.
	 */
	public OAVBDIFetcher(IOAVState state, Object rcapa, Object relem)
	{
		this(state, rcapa);
		
		if(state.getType(relem).equals(OAVBDIRuntimeModel.goal_type))
			setRGoal(relem);
		else if(state.getType(relem).equals(OAVBDIRuntimeModel.plan_type))
			setRPlan(relem);
		else if(state.getType(relem).equals(OAVBDIRuntimeModel.messageevent_type))
			setRMessageEvent(relem);
		else if(state.getType(relem).equals(OAVBDIRuntimeModel.internalevent_type))
			setRInternalEvent(relem);
		else
			throw new IllegalArgumentException("Unsoppurted object: "+relem);
	}
	
	//-------- IValueFetcher methods --------
	
	/**
	 *  Fetch a value via its name.
	 *  @param name The name.
	 *  @return The value.
	 */
	public Object fetchValue(String name)
	{
		Object ret = null;
		
		if(name==null)
			throw new RuntimeException("Name must not be null.");
		
		if(name.equals("$scope"))
			ret = new CapabilityFlyweight(state, rcapa);
		else if(name.equals("$beliefbase"))
			ret = BeliefbaseFlyweight.getBeliefbaseFlyweight(state, rcapa);
		else if(name.equals("$goalbase"))
			ret = GoalbaseFlyweight.getGoalbaseFlyweight(state, rcapa);
		else if(name.equals("$planbase"))
			ret = PlanbaseFlyweight.getPlanbaseFlyweight(state, rcapa);
		else if(name.equals("$eventbase"))
			ret = EventbaseFlyweight.getEventbaseFlyweight(state, rcapa);
		else if(name.equals("$expressionbase"))
			ret = ExpressionbaseFlyweight.getExpressionbaseFlyweight(state, rcapa);
		else if(name.equals("$properties"))
			ret = BDIInterpreter.getInterpreter(state).getProperties();
		else if(name.equals("$goal") && rgoal!=null)
			ret = GoalFlyweight.getGoalFlyweight(state, rcapa, rgoal);
		else if(name.equals("$event") && rmessageevent!=null)
			ret = MessageEventFlyweight.getMessageEventFlyweight(state, rcapa, rmessageevent);
		else if(name.equals("$event") && rinternalevent!=null)
			ret = InternalEventFlyweight.getInternalEventFlyweight(state, rcapa, rinternalevent);
		else if(name.equals("$plan") && rplan!=null)
			ret = PlanFlyweight.getPlanFlyweight(state, rcapa, rplan);
		
		else
			ret = super.fetchValue(name);
		
		return ret;
	}
	
	/**
	 *  Fetch a value via its name from an object.
	 *  @param name The name.
	 *  @param object The object.
	 *  @return The value.
	 */
	public Object fetchValue(String name, Object object)
	{
		Object ret = null;
		
		if(object instanceof IBeliefbase)
		{
			IBeliefbase bb = (IBeliefbase)object;
			if(bb.containsBelief(name))
				ret = bb.getBelief(name).getFact();
			else if(bb.containsBeliefSet(name))
				ret = bb.getBeliefSet(name).getFacts();
			else
				throw new RuntimeException("Unknown belief/set: "+name);
		}
		else if(object instanceof IParameterElement)
		{
			IParameterElement pe = (IParameterElement)object;
			if(pe.hasParameter(name))
			{
				ret = pe.getParameter(name).getValue();
			}
			else if(pe.hasParameterSet(name))
			{
				ret = pe.getParameterSet(name).getValues();
			}
			else
			{
				// Check if parameter exists, but has not been instantiated (return null or empty array).
				boolean	exists	= false;
				
				IOAVState	state	= ((ElementFlyweight)pe).getState();
				Object	pehandle	= ((ElementFlyweight)pe).getHandle();
				Object	mpe	= state.getAttributeValue(pehandle, OAVBDIRuntimeModel.element_has_model);
				if(state.getType(mpe).isSubtype(OAVBDIMetaModel.messageevent_type))
				{
					MessageType	mtype	= MessageEventRules.getMessageEventType(state, mpe);
					MessageType.ParameterSpecification	spec	= mtype.getParameter(name);
					if(spec!=null)
					{
						exists	= true;
						if(spec.isSet())
						{
							ret	= Array.newInstance(spec.getClazz(), 0);
						}
					}
				}
				else if(state.containsKey(mpe, OAVBDIMetaModel.parameterelement_has_parameters, name))
				{
					exists	= true;
				}
				else if(state.containsKey(mpe, OAVBDIMetaModel.parameterelement_has_parametersets, name))
				{
					exists	= true;
					Object	paramset	= state.getAttributeValue(mpe, OAVBDIMetaModel.parameterelement_has_parametersets, name);
					Class	clazz	= (Class)state.getAttributeValue(paramset, OAVBDIMetaModel.typedelement_has_class);
					ret	= Array.newInstance(clazz, 0);
				}
				
				if(!exists)
					throw new RuntimeException("Unknown parameter/set: "+name);
			}
		}
		else
		{
			super.fetchValue(name, object);
		}
		
		return ret;
	}

	//-------- additional methods --------
	
	/**
	 *  Set the rgoal.
	 *  @param rgoal The rgoal to set.
	 */
	public void setRGoal(Object rgoal)
	{
		this.rgoal = rgoal;
	}
	
	/**
	 *  Set the rplan.
	 *  @param rgoal The rplan to set.
	 */
	public void setRPlan(Object rplan)
	{
		this.rplan = rplan;
	}

	/**
	 *  Set the message event.
	 *  @param rmessageevent The rmessageevent to set.
	 */
	public void setRMessageEvent(Object rmessageevent)
	{
		this.rmessageevent = rmessageevent;
	}
	
	/**
	 *  Set the internal event.
	 *  @param rinternalevent The rinternalevent to set.
	 */
	public void setRInternalEvent(Object rinternalevent)
	{
		this.rinternalevent = rinternalevent;
	}
}
