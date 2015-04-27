package jadex.bdi.runtime.interpreter;

import jadex.bdi.features.IBDIAgentFeature;
import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.features.impl.IInternalBDIAgentFeature;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.ICandidateInfo;
import jadex.bdi.runtime.impl.flyweights.PlanFlyweight;
import jadex.bdi.runtime.impl.flyweights.PlanInfoFlyweight;
import jadex.bdi.runtime.impl.flyweights.PlanInstanceInfoFlyweight;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.IValueFetcher;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.FunctionCall;
import jadex.rules.rulesystem.rules.IConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.LiteralReturnValueConstraint;
import jadex.rules.rulesystem.rules.NotCondition;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.OrConstraint;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Static helper class for event processing rules and actions.
 *  Rules that handle the event processing, i.e.
 *  - building an APL (applicable plan list) for goals / metagoals / internal events / message events
 *  - selecting a plan candidate for execution
 *    - handle meta-level reasoning (process for selecting a candidate)
 *  - scheduling the selected candidate
 */
public class EventProcessingRules
{
	//-------- RPlan/Waitqueue APL building rules --------

	/**
	 *  Action to add matching rplan to apl.
	 */
	protected static final IAction	ADD_RPLAN_TO_APL	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rpe	= assignments.getVariableValue("?rpe");
//			Object	ragent	= assignments.getVariableValue("?ragent");
			Object	rplan	= assignments.getVariableValue("?rplan");
			Object	rcapa	= assignments.getVariableValue("?rcapa");

//			state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_eventprocessing, rpe);
			
			Object	apl	= state.getAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl);
			if(apl==null)
			{
				apl	= state.createObject(OAVBDIRuntimeModel.apl_type);
				state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl, apl);
			}
			Object	cand	= state.createObject(OAVBDIRuntimeModel.plancandidate_type);
			state.setAttributeValue(cand, OAVBDIRuntimeModel.plancandidate_has_plan, rplan);
			state.setAttributeValue(cand, OAVBDIRuntimeModel.plancandidate_has_rcapa, rcapa);
			state.addAttributeValue(apl, OAVBDIRuntimeModel.apl_has_planinstancecandidates, cand);
		}
	};
	
	/**
	 *  Action to set rplan apl building to finished.
	 * /
	protected static IAction	NO_RPLANS_FOR_APL	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rpe	= assignments.getVariableValue("?rpe");
//			Object	ragent	= assignments.getVariableValue("?ragent");

//			state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_eventprocessing, rpe);

			Object	apl	= state.getAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl);
			if(apl==null)
			{
				apl	= state.createObject(OAVBDIRuntimeModel.apl_type);
				state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl, apl);
			}

			state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_state, 
				OAVBDIRuntimeModel.PROCESSABLEELEMENT_APLRPLANSREADY);

//			state.setAttributeValue(apl, OAVBDIRuntimeModel.apl_has_buildrplansfinished, Boolean.TRUE);
		}
	};*/

	/**
	 *  Action to add matching waitqueue candidate to apl.
	 */
	protected static final IAction	ADD_WAITQUEUECAND_TO_APL	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rpe	= assignments.getVariableValue("?rpe");
			Object	rcapa	= assignments.getVariableValue("?rcapa");
			Object	rplan	= assignments.getVariableValue("?rplan");

//			state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_eventprocessing, rpe);

			Object	apl	= state.getAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl);
			if(apl==null)
			{
				apl	= state.createObject(OAVBDIRuntimeModel.apl_type);
				state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl, apl);
			}
			
			Object wc = state.createObject(OAVBDIRuntimeModel.waitqueuecandidate_type);
			state.setAttributeValue(wc, OAVBDIRuntimeModel.waitqueuecandidate_has_plan, rplan);
			state.setAttributeValue(wc, OAVBDIRuntimeModel.waitqueuecandidate_has_rcapa, rcapa);
			
			state.addAttributeValue(apl, OAVBDIRuntimeModel.apl_has_waitqueuecandidates, wc);
		}
	};
	
	/**
	 *  Action to set waitqueue candidate apl building to finished.
	 */
	protected static final IAction	MAKE_APL_AVAILABLE	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object rpe	= assignments.getVariableValue("?rpe");
			Object rcapa = assignments.getVariableValue("?rcapa");
//			Object ragent	= assignments.getVariableValue("?ragent");

//			state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_eventprocessing, rpe);

			Object	apl	= state.getAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl);
			if(apl==null)
			{
				apl	= state.createObject(OAVBDIRuntimeModel.apl_type);
				state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl, apl);
			}

//			state.setAttributeValue(apl, OAVBDIRuntimeModel.apl_has_buildwaitqueuecandsfinished, Boolean.TRUE);
		
			addMPlansToAPL(state, rpe, rcapa);
		}
	};
	
	/**
	 *  Add matching waiting rplans or plans with matching waitqueues to the APL
	 *  of an unprocessed processable element and set the APL to building finished,
	 *  when no more found.
	 */
	public static Rule[]	createBuildRPlanAPLRules()
	{
		Variable	rpe	= new Variable("?rpe", OAVBDIRuntimeModel.processableelement_type);
		Variable	apl	= new Variable("?apl", OAVBDIRuntimeModel.apl_type);
		Variable	candpi	= new Variable("?candpi", OAVBDIRuntimeModel.plancandidate_type);
		Variable	candwq	= new Variable("?candwq", OAVBDIRuntimeModel.waitqueuecandidate_type);
		Variable	org	= new Variable("?org", OAVBDIRuntimeModel.processableelement_type);
		Variable	mpe	= new Variable("?mpe", OAVBDIMetaModel.processableelement_type);
		Variable	rcapa	= new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable	rplan	= new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
//		Variable	wa	= new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
//		Variable	wqwa	= new Variable("?wqwa", OAVBDIRuntimeModel.waitabstraction_type);
		
		// Shared conditions
		ObjectCondition	rpecon	= new ObjectCondition(rpe.getType());
		rpecon.addConstraint(new BoundConstraint(null, rpe));
		rpecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.processableelement_has_apl, apl));
		rpecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.messageevent_has_original, org));
		rpecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mpe));
		rpecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_state, 
			OAVBDIRuntimeModel.PROCESSABLEELEMENT_UNPROCESSED));

		ObjectCondition	capcon	= new ObjectCondition(rcapa.getType());
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new OrConstraint(new IConstraint[]
  		{
  			new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rpe, IOperator.CONTAINS),
  			new BoundConstraint(OAVBDIRuntimeModel.capability_has_internalevents, rpe, IOperator.CONTAINS),
  			new BoundConstraint(OAVBDIRuntimeModel.capability_has_messageevents, rpe, IOperator.CONTAINS)
  		}));

//		ObjectCondition	wacon	= new ObjectCondition(wa.getType());
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		wacon.addConstraint(new OrConstraint(new IConstraint[]
//		{
//				// RPlan waiting for (new) goal not allowed, only goalfinished, which is handled elsewhere.
//				new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_messageevents, org, IOperator.CONTAINS),
//				new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes, mpe, IOperator.CONTAINS),
//				new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes, mpe, IOperator.CONTAINS),
//		}));
		
		// Conditions for plan instances
		ObjectCondition	planconwa	= new ObjectCondition(rplan.getType());
		planconwa.addConstraint(new BoundConstraint(null, rplan));
//		planconwa.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		planconwa.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, 
			OAVBDIRuntimeModel.PLANPROCESSINGTATE_WAITING));
		planconwa.addConstraint(new OrConstraint(new IConstraint[]
 		{
 			// RPlan waiting for (new) goal not allowed, only goalfinished, which is handled elsewhere.
 			new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_messageevents}, org, IOperator.CONTAINS),
 			new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes}, mpe, IOperator.CONTAINS),
 			new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes}, mpe, IOperator.CONTAINS),
 		}));
		
		ObjectCondition	candconpi	= new ObjectCondition(candpi.getType());
		candconpi.addConstraint(new BoundConstraint(null, candpi));
		candconpi.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plancandidate_has_plan, rplan));

		ObjectCondition	aplconpi	= new ObjectCondition(apl.getType());
		aplconpi.addConstraint(new BoundConstraint(null, apl));
		aplconpi.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.apl_has_planinstancecandidates, candpi, IOperator.CONTAINS));

		// todo: maybe better to ensure the rplan is not already added to wq.
		// Here a phase model is assumed that first allows plan instance candidates to be added and the waitqueue candidates.

		ObjectCondition	aplconpi2	= new ObjectCondition(apl.getType());
		aplconpi2.addConstraint(new BoundConstraint(null, apl));
		aplconpi2.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.apl_has_waitqueuecandidates, null, IOperator.NOTEQUAL));
		
		// Rules for plan instances
		Rule apl_add_rplan	= new Rule("apl_add_rplan",
			new AndCondition(new ICondition[]{
//				rpecon, capcon, wacon, planconwa,
				rpecon, capcon, planconwa,
				new NotCondition(new AndCondition(new ICondition[]{candconpi, aplconpi})),
				new NotCondition(aplconpi2)}),
			ADD_RPLAN_TO_APL);
		
		// Conditions for waitqueue candidates
		
//		ObjectCondition	waconwq	= new ObjectCondition(wqwa.getType());
//		waconwq.addConstraint(new BoundConstraint(null, wqwa));
//		waconwq.addConstraint(new OrConstraint(new IConstraint[]
//		{
//				// RPlan waiting for (new) goal not allowed, only goalfinished, which is handled elsewhere.
//				new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_messageevents, org, IOperator.CONTAINS),
//				new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes, mpe, IOperator.CONTAINS),
//				new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes, mpe, IOperator.CONTAINS),
//		}));
		
		ObjectCondition	planconwq	= new ObjectCondition(rplan.getType());
		planconwq.addConstraint(new BoundConstraint(null, rplan));
//		planconwq.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitqueuewa, wqwa));
		planconwq.addConstraint(new OrConstraint(new IConstraint[]
		{
				// RPlan waiting for (new) goal not allowed, only goalfinished, which is handled elsewhere.
				new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitqueuewa, OAVBDIRuntimeModel.waitabstraction_has_messageevents}, org, IOperator.CONTAINS),
				new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitqueuewa, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes}, mpe, IOperator.CONTAINS),
				new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitqueuewa, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes}, mpe, IOperator.CONTAINS),
		}));
		
		ObjectCondition	aplconwc	= new ObjectCondition(apl.getType());
		aplconwc.addConstraint(new BoundConstraint(null, apl));
		aplconwc.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.apl_has_waitqueuecandidates, candwq, IOperator.CONTAINS));
		
		ObjectCondition	candconwc	= new ObjectCondition(candwq.getType());
		candconwc.addConstraint(new BoundConstraint(null, candwq));
		candconwc.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitqueuecandidate_has_plan, rplan));
		
		// Rules for waitqueue candidates
		Rule apl_add_waitqueuecand = new Rule("apl_add_waitqueuecand",
			new AndCondition(new ICondition[]{
				rpecon, capcon, 
//				new NotCondition(new AndCondition(new ICondition[]{wacon, planconwa})),
				new NotCondition(planconwa),
//				waconwq, planconwq,
				planconwq,
				new NotCondition(aplconwc)}),
				ADD_WAITQUEUECAND_TO_APL);

		Rule apl_make_available	= new Rule("apl_make_available",
			new AndCondition(new ICondition[]{
				rpecon, capcon, 
//				new NotCondition(new AndCondition(new ICondition[]{wacon, planconwa, 
				new NotCondition(new AndCondition(new ICondition[]{planconwa, 
					new NotCondition(new AndCondition(new ICondition[]{candconpi, aplconpi})),
					new NotCondition(new AndCondition(new ICondition[]{candconwc, aplconwc}))})),
//				new NotCondition(new AndCondition(new ICondition[]{waconwq, planconwq, 
				new NotCondition(new AndCondition(new ICondition[]{planconwq, 
					new NotCondition(new AndCondition(new ICondition[]{candconpi, aplconpi})),
					new NotCondition(new AndCondition(new ICondition[]{candconwc, aplconwc}))
				}))}),
				MAKE_APL_AVAILABLE);
		
		return new Rule[]{apl_add_rplan, apl_add_waitqueuecand, apl_make_available};		
	}
	
	//-------- APL Building finished rule --------
	
	/**
	 * 
	 */
	protected static void addMPlansToAPL(IOAVState state, Object rpe, Object rcapa)
	{
		Object	mpe	= state.getAttributeValue(rpe, OAVBDIRuntimeModel.element_has_model);
		Object	apl	= state.getAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl);
		
		// Add mplans from precandidates
		Object	precandlist	= state.getAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_precandidates, mpe);
		if(precandlist!=null)
		{
			Collection	precands	= state.getAttributeValues(precandlist, OAVBDIRuntimeModel.precandidatelist_has_precandidates);
			if(precands!=null)
			{
				for(Iterator it=precands.iterator(); it.hasNext(); )
				{
					Object	precand	= it.next();
					Object	mplan	= state.getAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_mplan);
					Object	scope	= state.getAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_capability);
					Object	triggerref	= state.getAttributeValue(precand, OAVBDIRuntimeModel.precandidate_has_triggerreference);
					Object	mexp	= state.getAttributeValue(triggerref, OAVBDIMetaModel.triggerreference_has_match);
					
					OAVBDIFetcher	fetcher	= new OAVBDIFetcher(state, scope);
					if(OAVBDIRuntimeModel.goal_type.equals(state.getType(rpe)))
						fetcher.setRGoal(rpe);
					else if(OAVBDIRuntimeModel.internalevent_type.equals(state.getType(rpe)))
						fetcher.setRInternalEvent(rpe);
					else if(OAVBDIRuntimeModel.messageevent_type.equals(state.getType(rpe)))
						fetcher.setRMessageEvent(rpe);
					boolean	match	= true;
					if(mexp!=null)
					{
						try
						{
							match = ((Boolean)AgentRules.evaluateExpression(state, mexp, fetcher)).booleanValue();
						}
						catch(Exception e)
						{
							e.printStackTrace();
							match	= false;
						}
					}
					
					if(match)
						createMPlanCandidates(state, rpe, scope, apl, mplan, fetcher);
				}
			}
		}
		
		// When no candidates, remove apl as required by other rules (hack???)
		Collection	plancands	= state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_plancandidates);
		Collection	pinscands	= state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_planinstancecandidates);
		Collection	waitcands	= state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_waitqueuecandidates);
		
		if((plancands==null || plancands.isEmpty())
			&& (pinscands==null || pinscands.isEmpty())
			&& (waitcands==null || waitcands.isEmpty()))
		{
			state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl, null);
			state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_state, 
				OAVBDIRuntimeModel.PROCESSABLEELEMENT_NOCANDIDATES);
			
			// When no candidates found, event processing stops here.
//			state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_eventprocessing, null);
			
			if(!state.getType(rpe).isSubtype(OAVBDIRuntimeModel.goal_type)
				|| state.getAttributeValues(rpe, OAVBDIRuntimeModel.goal_has_triedmplans)==null)
			{
				IInternalBDIAgentFeature ip = BDIAgentFeature.getInterpreter(state);
				ip.getLogger(rcapa).warning("Warning: Event/goal not handled: "+BDIAgentFeature.getInternalAccess(state).getComponentIdentifier().getLocalName()+rpe+" "
					+state.getAttributeValue(state.getAttributeValue(rpe, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
				
				// Remove unprocessable event from agent.
				if(state.getType(rpe).isSubtype(OAVBDIRuntimeModel.internalevent_type))
				{
					state.removeAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_internalevents, rpe);
				}
				else if(state.getType(rpe).isSubtype(OAVBDIRuntimeModel.messageevent_type))
				{
					state.removeAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_messageevents, rpe);
				}
			}
		}
		else
		{
			state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_state,
				OAVBDIRuntimeModel.PROCESSABLEELEMENT_APLAVAILABLE);
		
//			System.out.println("APL available: "+rpe);
		}
	}
	
	//-------- rule methods --------

	/**
	 *  Create the metalevel reasoning for goal rule.
	 */
	public static Rule createMetaLevelReasoningForGoalRule()
	{
		Variable mmetagoal = new Variable("?mmetagoal", OAVBDIMetaModel.metagoal_type);
		Variable mgoaltrigger = new Variable("?mgoaltrigger", OAVBDIMetaModel.metagoaltrigger_type);
		Variable mcapa = new Variable("?mcapa", OAVBDIMetaModel.capability_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);

		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.processableelement_type);
		Variable mpe = new Variable("?mpe", OAVBDIMetaModel.processableelement_type);
		Variable rtargetcapa = new Variable("?rtargetcapa", OAVBDIRuntimeModel.capability_type);

		Variable triggerrefs = new Variable("?triggerrefs", OAVBDIMetaModel.triggerreference_type, true, false);
		Variable ref = new Variable("?ref", OAVJavaType.java_string_type);
		
		// There is a ?mmetagoal with a trigger (?mgoaltrigger)
		ObjectCondition	metagoalcon	= new ObjectCondition(OAVBDIMetaModel.metagoal_type);
		metagoalcon.addConstraint(new BoundConstraint(null, mmetagoal));
		metagoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.metagoal_has_trigger, mgoaltrigger));
		
		// The ?mmetagoal is in a capability (?mcapa)
		ObjectCondition	mcapacon	= new ObjectCondition(OAVBDIMetaModel.capability_type);
		mcapacon.addConstraint(new BoundConstraint(null, mcapa));
		mcapacon.addConstraint(new BoundConstraint(OAVBDIMetaModel.capability_has_goals, 
			mmetagoal, IOperator.CONTAINS));

		// The ?mcapa has an instance (?rcapa)
		ObjectCondition	capacon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(null, rcapa));
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mcapa));

		// There is a processable element (?rpe) that needs processing
		ObjectCondition	pecon	= new ObjectCondition(OAVBDIRuntimeModel.processableelement_type);
		pecon.addConstraint(new BoundConstraint(null, rpe));
		pecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_state, 
			OAVBDIRuntimeModel.PROCESSABLEELEMENT_APLAVAILABLE));
		pecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mpe));
		
		// The ?rpe is in a capability (?rtargetcapa)
		ObjectCondition	targetcapacon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		targetcapacon.addConstraint(new BoundConstraint(null, rtargetcapa));
		targetcapacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, 
			rpe, IOperator.CONTAINS));
		
		// The ?triggerref belongs to the metagoal trigger.
		ObjectCondition	metagoaltriggercon	= new ObjectCondition(OAVBDIMetaModel.metagoaltrigger_type);
		metagoaltriggercon.addConstraint(new BoundConstraint(null, mgoaltrigger));
		metagoaltriggercon.addConstraint(new BoundConstraint(OAVBDIMetaModel.metagoaltrigger_has_goals,	triggerrefs));
			
		// There is a trigger reference (?triggerref) that maps to the ?rpe.
		ObjectCondition trcon = new ObjectCondition(OAVBDIMetaModel.triggerreference_type);
		trcon.addConstraint(new BoundConstraint(null, triggerrefs, IOperator.CONTAINS));
		trcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.triggerreference_has_ref, ref));
		trcon.addConstraint(new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{rcapa, ref, rpe, rtargetcapa})));

		Rule metalevel_reasoning = new Rule("metalevel_reasoning_for_goal", 
			new AndCondition(new ICondition[]{metagoalcon, mcapacon, capacon, pecon, targetcapacon, metagoaltriggercon, trcon}), METALEVEL_ACTION);
		return metalevel_reasoning;
	}
	
	/**
	 *  Create the metalevel reasoning for internal event rule.
	 */
	public static Rule createMetaLevelReasoningForInternalEventRule()
	{
		Variable mmetagoal = new Variable("?mmetagoal", OAVBDIMetaModel.metagoal_type);
		Variable mgoaltrigger = new Variable("?mgoaltrigger", OAVBDIMetaModel.metagoaltrigger_type);
		Variable mcapa = new Variable("?mcapa", OAVBDIMetaModel.capability_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);

		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.processableelement_type);
		Variable mpe = new Variable("?mpe", OAVBDIMetaModel.processableelement_type);
		Variable rtargetcapa = new Variable("?rtargetcapa", OAVBDIRuntimeModel.capability_type);

		Variable triggerrefs = new Variable("?triggerrefs", OAVBDIMetaModel.triggerreference_type, true, false);
		Variable ref = new Variable("?ref", OAVJavaType.java_string_type);
		
		// There is a ?mmetagoal with a trigger (?mgoaltrigger)
		ObjectCondition	metagoalcon	= new ObjectCondition(OAVBDIMetaModel.metagoal_type);
		metagoalcon.addConstraint(new BoundConstraint(null, mmetagoal));
		metagoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.metagoal_has_trigger, mgoaltrigger));
		
		// The ?mmetagoal is in a capability (?mcapa)
		ObjectCondition	mcapacon	= new ObjectCondition(OAVBDIMetaModel.capability_type);
		mcapacon.addConstraint(new BoundConstraint(null, mcapa));
		mcapacon.addConstraint(new BoundConstraint(OAVBDIMetaModel.capability_has_goals, 
			mmetagoal, IOperator.CONTAINS));

		// The ?mcapa has an instance (?rcapa)
		ObjectCondition	capacon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(null, rcapa));
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mcapa));

		// There is a processable element (?rpe) that needs processing
		ObjectCondition	pecon	= new ObjectCondition(OAVBDIRuntimeModel.processableelement_type);
		pecon.addConstraint(new BoundConstraint(null, rpe));
		pecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_state, 
			OAVBDIRuntimeModel.PROCESSABLEELEMENT_APLAVAILABLE));
		pecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mpe));
		
		// The ?rpe is in a capability (?rtargetcapa)
		ObjectCondition	targetcapacon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		targetcapacon.addConstraint(new BoundConstraint(null, rtargetcapa));
		targetcapacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_internalevents, 
			rpe, IOperator.CONTAINS));
		
		// The ?triggerref belongs to the metagoal trigger.
		ObjectCondition	metagoaltriggercon	= new ObjectCondition(OAVBDIMetaModel.metagoaltrigger_type);
		metagoaltriggercon.addConstraint(new BoundConstraint(null, mgoaltrigger));
		metagoaltriggercon.addConstraint(new BoundConstraint(OAVBDIMetaModel.trigger_has_internalevents, triggerrefs));
			
		// There is a trigger reference (?triggerref) that maps to the ?rpe.
		ObjectCondition trcon = new ObjectCondition(OAVBDIMetaModel.triggerreference_type);
		trcon.addConstraint(new BoundConstraint(null, triggerrefs, IOperator.CONTAINS));
		trcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.triggerreference_has_ref, ref));
		trcon.addConstraint(new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{rcapa, ref, rpe, rtargetcapa})));

		Rule metalevel_reasoning = new Rule("metalevel_reasoning_for_internalevent", 
			new AndCondition(new ICondition[]{metagoalcon, mcapacon, capacon, pecon, targetcapacon, metagoaltriggercon, trcon}), METALEVEL_ACTION);
		return metalevel_reasoning;
	}
	
	/**
	 *  Create the metalevel reasoning for message event rule.
	 */
	public static Rule createMetaLevelReasoningForMessageEventRule()
	{
		Variable mmetagoal = new Variable("?mmetagoal", OAVBDIMetaModel.metagoal_type);
		Variable mgoaltrigger = new Variable("?mgoaltrigger", OAVBDIMetaModel.metagoaltrigger_type);
		Variable mcapa = new Variable("?mcapa", OAVBDIMetaModel.capability_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);

		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.processableelement_type);
		Variable mpe = new Variable("?mpe", OAVBDIMetaModel.processableelement_type);
		Variable rtargetcapa = new Variable("?rtargetcapa", OAVBDIRuntimeModel.capability_type);

		Variable triggerrefs = new Variable("?triggerrefs", OAVBDIMetaModel.triggerreference_type, true, false);
		Variable ref = new Variable("?ref", OAVJavaType.java_string_type);
		
		// There is a ?mmetagoal with a trigger (?mgoaltrigger)
		ObjectCondition	metagoalcon	= new ObjectCondition(OAVBDIMetaModel.metagoal_type);
		metagoalcon.addConstraint(new BoundConstraint(null, mmetagoal));
		metagoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.metagoal_has_trigger, mgoaltrigger));
		
		// The ?mmetagoal is in a capability (?mcapa)
		ObjectCondition	mcapacon	= new ObjectCondition(OAVBDIMetaModel.capability_type);
		mcapacon.addConstraint(new BoundConstraint(null, mcapa));
		mcapacon.addConstraint(new BoundConstraint(OAVBDIMetaModel.capability_has_goals, 
			mmetagoal, IOperator.CONTAINS));

		// The ?mcapa has an instance (?rcapa)
		ObjectCondition	capacon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capacon.addConstraint(new BoundConstraint(null, rcapa));
		capacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mcapa));

		// There is a processable element (?rpe) that needs processing
		ObjectCondition	pecon	= new ObjectCondition(OAVBDIRuntimeModel.processableelement_type);
		pecon.addConstraint(new BoundConstraint(null, rpe));
		pecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_state, 
			OAVBDIRuntimeModel.PROCESSABLEELEMENT_APLAVAILABLE));
		pecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mpe));
		
		// The ?rpe is in a capability (?rtargetcapa)
		ObjectCondition	targetcapacon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		targetcapacon.addConstraint(new BoundConstraint(null, rtargetcapa));
		targetcapacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_messageevents, 
			rpe, IOperator.CONTAINS));
		
		// The ?triggerref belongs to the metagoal trigger.
		ObjectCondition	metagoaltriggercon	= new ObjectCondition(OAVBDIMetaModel.metagoaltrigger_type);
		metagoaltriggercon.addConstraint(new BoundConstraint(null, mgoaltrigger));
		metagoaltriggercon.addConstraint(new BoundConstraint(OAVBDIMetaModel.trigger_has_messageevents,	triggerrefs));
			
		// There is a trigger reference (?triggerref) that maps to the ?rpe.
		ObjectCondition trcon = new ObjectCondition(OAVBDIMetaModel.triggerreference_type);
		trcon.addConstraint(new BoundConstraint(null, triggerrefs, IOperator.CONTAINS));
		trcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.triggerreference_has_ref, ref));
		trcon.addConstraint(new LiteralReturnValueConstraint(Boolean.TRUE, new FunctionCall(new ResolvesTo(), new Object[]{rcapa, ref, rpe, rtargetcapa})));

		Rule metalevel_reasoning = new Rule("metalevel_reasoning_for_messageevent", 
			new AndCondition(new ICondition[]{metagoalcon, mcapacon, capacon, pecon, targetcapacon, metagoaltriggercon, trcon}), METALEVEL_ACTION);
		return metalevel_reasoning;
	}
	
	protected static final IAction METALEVEL_ACTION = new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
//			System.out.println("Meta-level reasoning started.");
			
			Object rpe = assignments.getVariableValue("?rpe");
			Object rcapa	= assignments.getVariableValue("?rcapa");
//			Object ragent	= assignments.getVariableValue("?ragent");
			Object mmetagoal = assignments.getVariableValue("?mmetagoal");
			
			String type = (String)state.getAttributeValue(mmetagoal, OAVBDIMetaModel.modelelement_has_name);
			Object rmetagoal = GoalLifecycleRules.createGoal(state, rcapa, type);
			
			String appname = "applicables";
			Object appparamset = state.getAttributeValue(rmetagoal, OAVBDIRuntimeModel.parameterelement_has_parametersets, appname);
			// Todo: create parameter in runtime if not declared in model.
//			if(appparamset==null)
//			{
//				Object mapp = state.getAttributeValue(mmetagoal, OAVBDIMetaModel.parameterelement_has_parameters, appname);
//				Class clazz = (Class)state.getAttributeValue(mapp, OAVBDIMetaModel.typedelement_has_class);
//				appparamset = BeliefRules.createParameterSet(state, appname, null, clazz, rmetagoal, null, rcapa);
//			}	
			// Hack! Create result paramset for making querygoal valid :-(
//			String resultname = "result";
//			Object resultparamset = state.getAttributeValue(rmetagoal, OAVBDIRuntimeModel.parameterelement_has_parametersets, resultname);
			// Todo: create parameter in runtime if not declared in model.
//			if(resultparamset==null)
//			{
//				Object res = state.getAttributeValue(mmetagoal, OAVBDIMetaModel.parameterelement_has_parameters, resultname);
//				Class clazz = (Class)state.getAttributeValue(res, OAVBDIMetaModel.typedelement_has_class);
//				appparamset = BeliefRules.createParameterSet(state, resultname, null, clazz, rmetagoal, null, rcapa);
//			}	
			
			// Extract candidates from apl and add them all to the parameterset "applicables"
			Object apl = state.getAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl);
			Collection rcands = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_planinstancecandidates);
			if(rcands!=null)
			{
				for(Iterator it=rcands.iterator(); it.hasNext(); )
				{
					Object	rplancand = it.next();
					Object	rplan	= state.getAttributeValue(rplancand, OAVBDIRuntimeModel.plancandidate_has_plan);
					Object	rscope	= state.getAttributeValue(rplancand, OAVBDIRuntimeModel.plancandidate_has_rcapa);
					PlanInstanceInfoFlyweight piif = new PlanInstanceInfoFlyweight(state, rscope, rplan, rpe);
					BeliefRules.addParameterSetValue(state, appparamset, piif);
				}
			}
			
			Collection mcands = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_plancandidates);
			if(mcands!=null)
			{
				for(Iterator it=mcands.iterator(); it.hasNext(); )
				{
					Object mplancand = it.next();
					PlanInfoFlyweight pif = new PlanInfoFlyweight(state, rcapa, mplancand, rpe);
					BeliefRules.addParameterSetValue(state, appparamset, pif);
				}
			}
			
			// Adopt meta-level goal and let it select between applicables.
			GoalLifecycleRules.adoptGoal(state, rcapa, rmetagoal);
			
			state.setAttributeValue(apl, OAVBDIRuntimeModel.apl_has_metagoal, rmetagoal);
			state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_state, 
				OAVBDIRuntimeModel.PROCESSABLEELEMENT_METALEVELREASONING);
			
			// Set processing from original rpe to metagoal to allow buildAPL rule
			// Will be reset to null, once a candidate is selected.
//			state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_eventprocessing, rmetagoal);
		}
	};
		
	/**
	 *  Create the metalevel reasoning finished rule.
	 */
	public static Rule createMetaLevelReasoningFinishedRule()
	{
		Variable mmetagoal = new Variable("?mmetagoal", OAVBDIMetaModel.metagoal_type);
		Variable rmetagoal = new Variable("?rmetagoal", OAVBDIRuntimeModel.goal_type);
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.processableelement_type);
		Variable apl = new Variable("?apl", OAVBDIRuntimeModel.apl_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		// The type of the ?mgoal is metagoal 
		ObjectCondition	mgoalcon = new ObjectCondition(OAVBDIMetaModel.metagoal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, mmetagoal));
		
		// The ?rmetagoal belongs to the ?rmetagoal and it is finished (dropped)
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, rmetagoal));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mmetagoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPED));

		// An ?apl refers to the ?rmetagoal
		ObjectCondition	aplcon	= new ObjectCondition(OAVBDIRuntimeModel.apl_type);
		aplcon.addConstraint(new BoundConstraint(null, apl));
		aplcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.apl_has_metagoal, rmetagoal));

		// There is an ?rpe (processable element) with the ?apl
		ObjectCondition	pecon	= new ObjectCondition(OAVBDIRuntimeModel.processableelement_type);
		pecon.addConstraint(new BoundConstraint(null, rpe));
		pecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.processableelement_has_apl, apl));
						
		// The ?rpe (processable element) is contained in an ?rcapa
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new OrConstraint(new IConstraint[]{
			new BoundConstraint(OAVBDIRuntimeModel.capability_has_internalevents, rpe, IOperator.CONTAINS),	
			new BoundConstraint(OAVBDIRuntimeModel.capability_has_messageevents, rpe, IOperator.CONTAINS),	
			new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rpe, IOperator.CONTAINS)
		}));
			
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
//				System.out.println("Meta-level reasoning finished.");
				
				Object rmetagoal = assignments.getVariableValue("?rmetagoal");
				Object rpe =  assignments.getVariableValue("?rpe");
				Object apl =  assignments.getVariableValue("?apl");
				Object rcapa	= assignments.getVariableValue("?rcapa");
				
				Object rparamsetresult = state.getAttributeValue(rmetagoal, OAVBDIRuntimeModel.parameterelement_has_parametersets, "result");
				Collection result = state.getAttributeValues(rparamsetresult, OAVBDIRuntimeModel.parameterset_has_values);
				if(result==null)
				{
					state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_state, 
						OAVBDIRuntimeModel.PROCESSABLEELEMENT_NOCANDIDATES);
					BDIAgentFeature.getInterpreter(state).getLogger(rcapa).severe("Meta-level reasoning did not return a result.");
				}
				else
				{
					for(Iterator it=result.iterator(); it.hasNext(); )
					{
						ICandidateInfo ci = (ICandidateInfo)it.next();
						Object rplan = ((PlanFlyweight)ci.getPlan()).getHandle();
						PlanRules.adoptPlan(state, rcapa, rplan);
						
						if(ci instanceof PlanInfoFlyweight)
						{
							// Save candidate in plan for later apl removal and exclude list management.
							if(state.getType(rpe).isSubtype(OAVBDIRuntimeModel.goal_type))
								state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_plancandidate, ((PlanInfoFlyweight)ci).getHandle());
						}
						else if(ci instanceof PlanInstanceInfoFlyweight)
						{
							// Save candidate in plan for later apl removal and exclude list management.
							if(state.getType(rpe).isSubtype(OAVBDIRuntimeModel.goal_type))
							{
								Object plan = ((PlanInstanceInfoFlyweight)ci).getHandle();
								state.setAttributeValue(plan, OAVBDIRuntimeModel.plan_has_planinstancecandidate, plan);
							}
						}
						
//						boolean isgoal = state.getType(rpe).isSubtype(OAVBDIRuntimeModel.goal_type);
//						if(isgoal)
//						{
//							Object candidate = ((ElementFlyweight)ci).getHandle();
//							removeAPLCandidate(state, rpe, candidate);
//						}
					}
			
					state.setAttributeValue(apl, OAVBDIRuntimeModel.apl_has_metagoal, null);
					state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_state, 
						OAVBDIRuntimeModel.PROCESSABLEELEMENT_CANDIDATESSELECTED);
					
//					Collection mpcs = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_plancandidates);
//					Collection pics = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_planinstancecandidates);
//					Collection wqcs = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_waitqueuecandidates);
//					if(mpcs==null && pics==null && wqcs==null)
//						state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl, null);
				}
			}
		};
		Rule metalevel_reasoning_fini = new Rule("metalevel_reasoning_finished", 
			new AndCondition(new ICondition[]{mgoalcon, goalcon, aplcon, pecon, capcon}), action);
		return metalevel_reasoning_fini;
	}
	
	/**
	 *  Create the select candidates for goal rule.
	 */
	public static Rule createSelectCandidatesForGoalRule()
	{
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.processableelement_type);
		Variable mpe = new Variable("?mpe", OAVBDIMetaModel.processableelement_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable mmetagoal = new Variable("?mmetagoal", OAVBDIMetaModel.metagoal_type);
		Variable mgoaltrigger = new Variable("?mgoaltrigger", OAVBDIMetaModel.metagoaltrigger_type);
		Variable	ragent	= new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
	
		// Todo: Should trigger separately for each plan?
		ObjectCondition	rpecon	= new ObjectCondition(OAVBDIRuntimeModel.processableelement_type);
		rpecon.addConstraint(new BoundConstraint(null, rpe));
		rpecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_state, 
			OAVBDIRuntimeModel.PROCESSABLEELEMENT_APLAVAILABLE));
		rpecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mpe));

		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		// Specific part
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, 
			rpe, IOperator.CONTAINS));

		ObjectCondition	agentcon	= new ObjectCondition(ragent.getType());
		agentcon.addConstraint(new BoundConstraint(null, ragent));
				
		ObjectCondition	metagoalcon	= new ObjectCondition(OAVBDIMetaModel.metagoal_type);
		metagoalcon.addConstraint(new BoundConstraint(null, mmetagoal));
		metagoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.metagoal_has_trigger, mgoaltrigger));
		
		ObjectCondition	metagoaltriggercon	= new ObjectCondition(OAVBDIMetaModel.metagoaltrigger_type);
		metagoaltriggercon.addConstraint(new BoundConstraint(null, mgoaltrigger));
		metagoaltriggercon.addConstraint(new BoundConstraint(OAVBDIMetaModel.metagoaltrigger_has_goals, 
			mpe, IOperator.CONTAINS));
		
		NotCondition nometacon = new NotCondition(new AndCondition(
			new ICondition[]{metagoalcon, metagoaltriggercon}));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object rpe	= assignments.getVariableValue("?rpe");
				Object apl	= state.getAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl);

				Object mpe = state.getAttributeValue(rpe, OAVBDIRuntimeModel.element_has_model); 
			
//				System.out.println("scfg: "+state.getAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_state));
				
				// Find best candidates
				List cands = reason(state, rpe, apl);
				
//				System.out.println("createSelectCandidatesForGoalRule: schedulePlanInstance: "
//						+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName());

				// Check if candidates are still valid.
				// Uses optimistic scheme, i.e. apl may hold invalid entries.
				// Then the process must be started over again.
				Object cand = checkCandidates(state, rpe, apl, cands);
				if(cand!=null)
				{
//					System.out.println("Check candidates failed: "+cand+", "+rpe+", "+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName());

					state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_UNPROCESSED);
					boolean rebuild = ((Boolean)state.getAttributeValue(mpe, OAVBDIMetaModel.goal_has_rebuild)).booleanValue();
					
					if(rebuild)
					{
						state.getAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl, null);
					}
					else
					{
						// Remove plan candidate from apl.
						if(state.getType(cand).equals(OAVBDIRuntimeModel.waitqueuecandidate_type))
							state.removeAttributeValue(apl, OAVBDIRuntimeModel.apl_has_waitqueuecandidates, cand);
						else
							state.removeAttributeValue(apl, OAVBDIRuntimeModel.apl_has_planinstancecandidates, cand);
					}
				}
				else
				{
					// Create resp. activate plan(instances)
					scheduleCandidates(state, rpe, apl, cands);
					
					// Remove candidates from apl if not rebuild
					boolean rebuild = ((Boolean)state.getAttributeValue(mpe, OAVBDIMetaModel.goal_has_rebuild)).booleanValue();
					boolean retry = ((Boolean)state.getAttributeValue(mpe, OAVBDIMetaModel.goal_has_retry)).booleanValue();
					
	//				if(!rebuild)
	//				{
	//					for(int i=0; i<cands.size(); i++)
	//						removeAPLCandidate(state, rpe, cands.get(i));
	//					
	//					// Clear apl if empty
	//					Collection pcs = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_plancandidates);
	//					Collection pics = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_planinstancecandidates);
	//					Collection wqcs = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_waitqueuecandidates);
	//					if(pcs==null && pics==null && wqcs==null)
	//					{
	////						System.out.println("Set null apl: "+rpe+" "+apl);
	//						state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl, null);
	//					}
	//				}
					
					// If always rebuild or not retry clear apl.
					if(rebuild || !retry)
					{
						state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl, null);
					}
				
					// Reset inprocess flag.
//					state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_eventprocessing, null);
				}
			}
		};
		
		Rule rule = new Rule("candidates_select_for_goal", 
			new AndCondition(new ICondition[]{rpecon, capcon, nometacon, agentcon}), action);
		return rule;
	}
	
	/**
	 *  Create the select candidates for internal event rule.
	 */
	public static Rule createSelectCandidatesForInternalEventRule()
	{
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.processableelement_type);
		Variable mpe = new Variable("?mpe", OAVBDIMetaModel.processableelement_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable mmetagoal = new Variable("?mmetagoal", OAVBDIMetaModel.metagoal_type);
		Variable mgoaltrigger = new Variable("?mgoaltrigger", OAVBDIMetaModel.metagoaltrigger_type);
		Variable	ragent	= new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
	
		// Todo: Should trigger separately for each plan?
		ObjectCondition	rpecon	= new ObjectCondition(OAVBDIRuntimeModel.processableelement_type);
		rpecon.addConstraint(new BoundConstraint(null, rpe));
		rpecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_state, 
			OAVBDIRuntimeModel.PROCESSABLEELEMENT_APLAVAILABLE));
		rpecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mpe));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		// Specific part
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_internalevents, 
			rpe, IOperator.CONTAINS));
				
		ObjectCondition	agentcon	= new ObjectCondition(ragent.getType());
		agentcon.addConstraint(new BoundConstraint(null, ragent));

		ObjectCondition	metagoalcon	= new ObjectCondition(OAVBDIMetaModel.metagoal_type);
		metagoalcon.addConstraint(new BoundConstraint(null, mmetagoal));
		metagoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.metagoal_has_trigger, mgoaltrigger));
		
		// Todo: reasoning across scopes
		ObjectCondition	metagoaltriggercon	= new ObjectCondition(OAVBDIMetaModel.metagoaltrigger_type);
		metagoaltriggercon.addConstraint(new BoundConstraint(null, mgoaltrigger));
		metagoaltriggercon.addConstraint(new BoundConstraint(OAVBDIMetaModel.trigger_has_internalevents, 
			mpe, IOperator.CONTAINS));
		
		NotCondition nometacon = new NotCondition(new AndCondition(
			new ICondition[]{metagoalcon, metagoaltriggercon}));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	rpe	= assignments.getVariableValue("?rpe");
				Object	rcapa	= assignments.getVariableValue("?rcapa");
//				Object	ragent	= assignments.getVariableValue("?ragent");
				Object	apl	= state.getAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl);

				// Find best candidates
				List cands = reason(state, rpe, apl);
				
//				System.out.println("createSelectCandidatesForInternalEventRule: schedulePlanInstance: "
//						+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName());

				// Check if candidates are still valid.
				// Uses optimistic scheme, i.e. apl may hold invalid entries.
				// Then the process must be started over again.
//				Object mpe = state.getAttributeValue(rpe, OAVBDIRuntimeModel.element_has_model);
				Object cand = checkCandidates(state, rpe, apl, cands);
				if(cand!=null)
				{
//					System.out.println("Check candidates failed: "+cand+", "+rpe+", "+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName());

					state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_UNPROCESSED);
					state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl, null);
				}
				else
				{	
					// Create resp. activate plan(instances)
					scheduleCandidates(state, rpe, apl, cands);
					
					// Clear apl if empty
					Collection pcs = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_plancandidates);
					Collection pics = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_planinstancecandidates);
					Collection wqcs = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_waitqueuecandidates);
					if(pcs==null && pics==null && wqcs==null)
						state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl, null);
				
					// Reset inprocess flag.
	//				state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_eventprocessing, null);
				
					// Remove rpe if internal event
					state.removeAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_internalevents, rpe);
				}
			}
		};
		
		Rule rule = new Rule("candidates_select_for_internalevent", 
			new AndCondition(new ICondition[]{rpecon, capcon, nometacon, agentcon}), action);
		return rule;
	}
	
	/**
	 *  Create the select candidates for message event rule.
	 */
	public static Rule createSelectCandidatesForMessageEventRule()
	{
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.processableelement_type);
		Variable mpe = new Variable("?mpe", OAVBDIMetaModel.processableelement_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable mmetagoal = new Variable("?mmetagoal", OAVBDIMetaModel.metagoal_type);
		Variable mgoaltrigger = new Variable("?mgoaltrigger", OAVBDIMetaModel.metagoaltrigger_type);
		Variable	ragent	= new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
	
		// Todo: Should trigger separately for each plan?
		ObjectCondition	rpecon	= new ObjectCondition(OAVBDIRuntimeModel.processableelement_type);
		rpecon.addConstraint(new BoundConstraint(null, rpe));
		rpecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.processableelement_has_state, 
			OAVBDIRuntimeModel.PROCESSABLEELEMENT_APLAVAILABLE));
		rpecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mpe));

		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		// Specific part
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_messageevents, 
			rpe, IOperator.CONTAINS));
				
		ObjectCondition	agentcon	= new ObjectCondition(ragent.getType());
		agentcon.addConstraint(new BoundConstraint(null, ragent));

		ObjectCondition	metagoalcon	= new ObjectCondition(OAVBDIMetaModel.metagoal_type);
		metagoalcon.addConstraint(new BoundConstraint(null, mmetagoal));
		metagoalcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.metagoal_has_trigger, mgoaltrigger));
		ObjectCondition	metagoaltriggercon	= new ObjectCondition(OAVBDIMetaModel.metagoaltrigger_type);
		metagoaltriggercon.addConstraint(new BoundConstraint(null, mgoaltrigger));
		metagoaltriggercon.addConstraint(new BoundConstraint(OAVBDIMetaModel.trigger_has_messageevents, 
			mpe, IOperator.CONTAINS));
		
		NotCondition nometacon = new NotCondition(new AndCondition(
			new ICondition[]{metagoalcon, metagoaltriggercon}));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	rpe	= assignments.getVariableValue("?rpe");
				Object	rcapa	= assignments.getVariableValue("?rcapa");
//				Object	ragent	= assignments.getVariableValue("?ragent");
				Object	apl	= state.getAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl);

				// Find best candidates
				List cands = reason(state, rpe, apl);
				
//				System.out.println("createSelectCandidatesForMessageEventRule: schedulePlanInstance: "
//						+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName());

				// Check if candidates are still valid.
				// Uses optimistic scheme, i.e. apl may hold invalid entries.
				// Then the process must be started over again.
//				Object mpe = state.getAttributeValue(rpe, OAVBDIRuntimeModel.element_has_model);
				Object cand = checkCandidates(state, rpe, apl, cands);
				if(cand!=null)
				{
//					System.out.println("Check candidates failed: "+cand+", "+rpe+", "+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName());

					state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_state, OAVBDIRuntimeModel.PROCESSABLEELEMENT_UNPROCESSED);
					state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl, null);
				}
				else
				{
					// Create resp. activate plan(instances)
					scheduleCandidates(state, rpe, apl, cands);
					
					// Clear apl if empty
					Collection pcs = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_plancandidates);
					Collection pics = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_planinstancecandidates);
					Collection wqcs = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_waitqueuecandidates);
					if(pcs==null && pics==null && wqcs==null)
					{
	//					System.out.println("Set null apl: "+rpe+" "+apl);
						state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl, null);
					}
					
					// Reset inprocess flag.
	//				state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_eventprocessing, null);
				
					// Remove rpe if message or internal event
					state.removeAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_messageevents, rpe);
				}
			}
		};
		
		Rule rule = new Rule("candidates_select_for_messageevent", 
			new AndCondition(new ICondition[]{rpecon, capcon, nometacon, agentcon}), action);
		return rule;
	}
	
	/*protected static IAction SELECT_CANDIDATE_ACTION	= new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object	rpe	= assignments.getVariableValue("?rpe");
			Object	rcapa	= assignments.getVariableValue("?rcapa");
			Object	apl	= state.getAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl);

			OAVObjectType otype = state.getType(rpe);
			
			// Find best candidates
			List cands = reason(state, rpe, apl);
			
			// Create resp. activate plan(instances)
			scheduleCandidates(state, rcapa, rpe, apl, cands);
			
			// Remove candidates from apl if goal type
			if(OAVBDIMetaModel.goal_type.isSubtype(otype))
			{
				for(int i=0; i<cands.size(); i++)
					removeAPLCandidate(state, rpe, cands.get(i));
			}
				
			// Clear apl if empty
			Collection pcs = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_plancandidates);
			Collection pics = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_planinstancecandidates);
			Collection wqcs = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_waitqueuecandidates);
			if(pcs==null && pics==null && wqcs==null)
				state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl, null);
		
			// Reset inprocess flag.
			state.setAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_eventprocessing, null);
		
			// Remove rpe if message or internal event
			
			if(OAVBDIMetaModel.messageevent_type.equals(otype))
				state.removeAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_messageevents, rpe);
			else if(OAVBDIMetaModel.internalevent_type.equals(otype))
				state.removeAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_internalevents, rpe);
		}
	};*/
		
	/**
	 *  Create dispatch message event from waitqueue rule.
	 */
	public static Rule createDispatchMessageEventFromWaitqueueRule()
	{
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.messageevent_type);
		Variable orig = new Variable("?orig", OAVBDIRuntimeModel.messageevent_type);
		Variable mpe = new Variable("?mpe", OAVBDIMetaModel.messageevent_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
//		Variable wa = new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
//		Variable	ragent	= new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		
		ObjectCondition rpecon = new ObjectCondition(OAVBDIRuntimeModel.messageevent_type);
		rpecon.addConstraint(new BoundConstraint(null, rpe));
		rpecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mpe));
		rpecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.messageevent_has_original, orig));
		
		ObjectCondition plancon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, 
			OAVBDIRuntimeModel.PLANPROCESSINGTATE_WAITING));
		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitqueueelements, 
			rpe, IOperator.CONTAINS));
//		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		plancon.addConstraint(new OrConstraint(new IConstraint[]
		{
			new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes}, mpe, IOperator.CONTAINS), 
			new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_messageevents}, orig, IOperator.CONTAINS) 
		}));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));
		
//		ObjectCondition	agentcon	= new ObjectCondition(ragent.getType());
//		agentcon.addConstraint(new BoundConstraint(null, ragent));
//		agentcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.agent_has_eventprocessing, null));

//		// special condition for message elements
//		ObjectCondition wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		wacon.addConstraint(new OrConstraint(new IConstraint[]
//		{
//			new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes, mpe, IOperator.CONTAINS), 
//			new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_messageevents, orig, IOperator.CONTAINS) 
//		}));
		
		
		Rule rule = new Rule("waitqueue_dispatch_messageevent", 
			new AndCondition(new ICondition[]{rpecon, plancon, capcon}), DISPATCH_WAITQUEUE_ELEMENT_ACTION);
		return rule;
	}
	
	/**
	 *  Create dispatch internal event from waitqueue rule.
	 */
	public static Rule createDispatchInternalEventFromWaitqueueRule()
	{
//		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.processableelement_type);
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.internalevent_type);
		Variable mpe = new Variable("?mpe", OAVBDIMetaModel.processableelement_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
//		Variable wa = new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
//		Variable	ragent	= new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		
		ObjectCondition wqcon = new ObjectCondition(rpe.getType());
		wqcon.addConstraint(new BoundConstraint(null, rpe));
		wqcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mpe));
		
		ObjectCondition plancon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, 
			OAVBDIRuntimeModel.PLANPROCESSINGTATE_WAITING));
		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitqueueelements, 
			rpe, IOperator.CONTAINS));
//		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		plancon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes}, mpe, IOperator.CONTAINS));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));
		
//		ObjectCondition	agentcon	= new ObjectCondition(ragent.getType());
//		agentcon.addConstraint(new BoundConstraint(null, ragent));
//		agentcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.agent_has_eventprocessing, null));

		// special condition for internal events
//		ObjectCondition wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes, 
//			mpe, IOperator.CONTAINS));
		
		Rule rule = new Rule("waitqueue_dispatch_internalevent", 
			new AndCondition(new ICondition[]{wqcon, plancon, capcon}), DISPATCH_WAITQUEUE_ELEMENT_ACTION);
		return rule;
	}
	
	/**
	 *  Create dispatch goal from waitqueue rule.
	 * /
	protected static Rule createDispatchGoalFromWaitqueueRule()
	{
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.processableelement_type);
		Variable mpe = new Variable("?mpe", OAVBDIMetaModel.processableelement_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable wa = new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable rcapa = new Variable("?capability", OAVBDIRuntimeModel.capability_type);
//		Variable	ragent	= new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		
		ObjectCondition wqcon = new ObjectCondition(OAVBDIRuntimeModel.processableelement_type);
		wqcon.addConstraint(new BoundConstraint(null, rpe));
		wqcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mpe));
		
		ObjectCondition plancon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, 
			OAVBDIRuntimeModel.PLANPROCESSINGTATE_WAITING));
		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitqueueelements, 
			rpe, IOperator.CONTAINS));
		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));
		
//		ObjectCondition	agentcon	= new ObjectCondition(ragent.getType());
//		agentcon.addConstraint(new BoundConstraint(null, ragent));
//		agentcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.agent_has_eventprocessing, null));

		// special condition for goal
		ObjectCondition wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
		wacon.addConstraint(new BoundConstraint(null, wa));
		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_goals, 
			rpe, IOperator.CONTAINS));
		
		Rule dispatch_element = new Rule("dispatch_goal_from_waitqueue", 
			new AndCondition(new ICondition[]{wqcon, plancon, capcon, wacon}), DISPATCH_WAITQUEUE_ELEMENT_ACTION);
		return dispatch_element;
	}*/
	
	/**
	 *  Rule to schedule a plan waiting for a fact added event already contained in the waitqueue.
	 */
	public static Rule createDispatchFactAddedFromWaitqueueRule()
	{
		Variable change = new Variable("?change", OAVBDIRuntimeModel.changeevent_type);
		Variable rbelset = new Variable("?rbelset", OAVBDIRuntimeModel.beliefset_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
//		Variable wa = new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
//		Variable	ragent	= new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		
		ObjectCondition wqcon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		wqcon.addConstraint(new BoundConstraint(null, change));
		wqcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rbelset));
		wqcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED));
		
		ObjectCondition plancon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, 
			OAVBDIRuntimeModel.PLANPROCESSINGTATE_WAITING));
		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitqueueelements, 
			change, IOperator.CONTAINS));
//		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		plancon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_factaddeds}, rbelset, IOperator.CONTAINS));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));

//		ObjectCondition wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factaddeds, rbelset));

//		ObjectCondition	agentcon	= new ObjectCondition(ragent.getType());
//		agentcon.addConstraint(new BoundConstraint(null, ragent));
//		agentcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.agent_has_eventprocessing, null));
		
		Rule rule = new Rule("waitqueue_dispatch_factadded", 
			new AndCondition(new ICondition[]{wqcon, plancon, capcon}), DISPATCH_WAITQUEUE_ELEMENT_ACTION);
		return rule;
	}
	
	/**
	 *  Rule to schedule a plan waiting for a fact removed event already contained in the waitqueue.
	 */
	public static Rule createDispatchFactRemovedFromWaitqueueRule()
	{
		Variable change = new Variable("?change", OAVBDIRuntimeModel.changeevent_type);
		Variable rbelset = new Variable("?rbelset", OAVBDIRuntimeModel.beliefset_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
//		Variable wa = new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
//		Variable	ragent	= new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		
		ObjectCondition wqcon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		wqcon.addConstraint(new BoundConstraint(null, change));
		wqcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rbelset));
		wqcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTREMOVED));
		
		ObjectCondition plancon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, 
			OAVBDIRuntimeModel.PLANPROCESSINGTATE_WAITING));
		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitqueueelements, 
			change, IOperator.CONTAINS));
//		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		plancon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_factremoveds}, rbelset, IOperator.CONTAINS));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));

//		ObjectCondition wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factremoveds, rbelset));

//		ObjectCondition	agentcon	= new ObjectCondition(ragent.getType());
//		agentcon.addConstraint(new BoundConstraint(null, ragent));
//		agentcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.agent_has_eventprocessing, null));
		
		Rule rule = new Rule("waitqueue_dispatch_factremoved", 
			new AndCondition(new ICondition[]{wqcon, plancon, capcon}), DISPATCH_WAITQUEUE_ELEMENT_ACTION);
		return rule;
	}
	
	/**
	 *  Rule to schedule a plan waiting for a fact added event already contained in the waitqueue.
	 */
	public static Rule createDispatchFactChangedFromWaitqueueRule()
	{
		Variable change = new Variable("?change", OAVBDIRuntimeModel.changeevent_type);
		Variable rbelset = new Variable("?rbelset", OAVBDIRuntimeModel.beliefset_type);
		Variable rplan = new Variable("?rplan", OAVBDIRuntimeModel.plan_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
//		Variable wa = new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
//		Variable	ragent	= new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		
		ObjectCondition wqcon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		wqcon.addConstraint(new BoundConstraint(null, change));
		wqcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rbelset));
		wqcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTCHANGED));
		
		ObjectCondition plancon = new ObjectCondition(OAVBDIRuntimeModel.plan_type);
		plancon.addConstraint(new BoundConstraint(null, rplan));
		plancon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.plan_has_processingstate, 
			OAVBDIRuntimeModel.PLANPROCESSINGTATE_WAITING));
		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitqueueelements, 
			change, IOperator.CONTAINS));
//		plancon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.plan_has_waitabstraction, wa));
		plancon.addConstraint(new BoundConstraint(new OAVAttributeType[]{OAVBDIRuntimeModel.plan_has_waitabstraction, OAVBDIRuntimeModel.waitabstraction_has_factchangeds}, rbelset, IOperator.CONTAINS));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_plans, rplan, IOperator.CONTAINS));

//		ObjectCondition wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
//		wacon.addConstraint(new BoundConstraint(null, wa));
//		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factchangeds, rbelset));

//		ObjectCondition	agentcon	= new ObjectCondition(ragent.getType());
//		agentcon.addConstraint(new BoundConstraint(null, ragent));
//		agentcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.agent_has_eventprocessing, null));
		
		Rule rule = new Rule("waitqueue_dispatch_factchanged", 
			new AndCondition(new ICondition[]{wqcon, plancon, capcon}), DISPATCH_WAITQUEUE_ELEMENT_ACTION);
		return rule;
	}
	
	protected static final IAction DISPATCH_WAITQUEUE_ELEMENT_ACTION = new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object rplan = assignments.getVariableValue("?rplan");
			Object rcapa	= assignments.getVariableValue("?rcapa");
			Object rpe	= assignments.getVariableValue("?rpe");
			
			state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, 
				OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
			state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement, rpe);
			PlanRules.cleanupPlanWait(state, rcapa, rplan, false);
			state.removeAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements, rpe);
			
//			System.out.println("DISPATCH_WAITQUEUE_ELEMENT_ACTION: schedulePlanInstance: "
//				+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName()
//				+", "+rplan+", "+rpe);
		}
	};
	
	/**
	 *  Create the clean eventprocessing rule for non-active goals.
	 *  // todo: Hack. Only necessary because eventprocessing state must be
	 *  saved in OAVBDIRuntimeModel.capability_has_eventprocessing
	 * /
	protected static Rule createRemoveEventprocessingArtifactRule()
	{
		Variable rgoal = new Variable("?rgoal", OAVBDIRuntimeModel.goal_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable ragent = new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		
		ObjectCondition	goalcon	= new ObjectCondition(rgoal.getType());
		goalcon.addConstraint(new BoundConstraint(null, rgoal));
		goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, 
			OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE, IOperator.NOTEQUAL));

		ObjectCondition	capcon = new ObjectCondition(rcapa.getType());
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, 
			rgoal, IOperator.CONTAINS));

		ObjectCondition	agentcon = new ObjectCondition(ragent.getType());
		agentcon.addConstraint(new BoundConstraint(null, ragent));
//		agentcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.agent_has_eventprocessing, rgoal));
		
		Rule remove_eventprocessing_artifact = new Rule("remove_eventprocessing_artifact", 
			new AndCondition(new ICondition[]{goalcon, capcon, agentcon}), new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object ragent = assignments.getVariableValue("?ragent");

				// Remove that event processing artifact.
//				state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_eventprocessing, null);

			}
		});
		
		return remove_eventprocessing_artifact;
	}*/
	
	//-------- helper methods --------
	
	/** 
	 *  Create candidates for a matching mplan.
	 *  Checks precondition and evaluates bindings (if any).
	 *  @return apl	returns new apl object in case a null apl is supplied.
	 */
	protected static Object createMPlanCandidates(IOAVState state, Object processable, Object rcapa, Object apl, Object mplan, OAVBDIFetcher fetcher)
	{
		List bindings = AgentRules.calculateBindingElements(state, mplan, null, fetcher);
		if(bindings!=null)
		{
			for(int i=0; i<bindings.size(); i++)
			{
				apl	= createMPlanCandidate(state, processable, rcapa, apl, mplan, fetcher, (Map)bindings.get(i));
			}
		}
		// No binding: generate one candidate.
		else
		{
			apl	= createMPlanCandidate(state, processable, rcapa, apl, mplan, fetcher, null);
		}
		
		return apl;
	}
	
	/** 
	 *  Create candidates for a matching mplan.
	 *  Checks precondition and evaluates bindings (if any).
	 *  @return apl	returns new apl object in case a null apl is supplied.
	 */
	protected static Object createMPlanCandidate(IOAVState state, Object rpe, Object rcapa, Object apl, Object mplan, final OAVBDIFetcher fetcher, final Map bindings)
	{
		IValueFetcher f2	= fetcher;
		if(bindings!=null)
		{
			f2 = new IValueFetcher()
			{
				public Object fetchValue(String name)
				{
					Object ret = bindings.get(name);
					if(ret==null && !bindings.containsKey(name))	
						ret = fetcher.fetchValue(name);
					return ret;
				}
//				
//				public Object fetchValue(String name, Object object)
//				{
//					return fetcher.fetchValue(name, object);
//				}
			};
		}
		
		boolean preok = true;
		Object precond = state.getAttributeValue(mplan, OAVBDIMetaModel.plan_has_precondition);
		if(precond!=null)
		{
			Object ret = AgentRules.evaluateExpression(state, precond, f2);
			if(ret!=null)
				preok = ((Boolean)ret).booleanValue();
		}
		
		if(preok)
		{
			if(apl==null)
				apl = createAPL(state, rpe);
			
			Object	mplancandidate	= state.createObject(OAVBDIRuntimeModel.mplancandidate_type);
			state.setAttributeValue(mplancandidate, OAVBDIRuntimeModel.mplancandidate_has_mplan, mplan);
			state.setAttributeValue(mplancandidate, OAVBDIRuntimeModel.mplancandidate_has_rcapa, rcapa);
			// Must be added and removed to allow being used in state (otherwise state does not know object)
			state.addAttributeValue(apl, OAVBDIRuntimeModel.apl_has_plancandidates, mplancandidate);
//			System.out.println("EventProcessingRules.createMPlanCandidate() add: "+apl+", "+mplancandidate);
			
			if(bindings!=null)
			{
				for(Iterator it=bindings.keySet().iterator(); it.hasNext(); )
				{
					String	name	= (String)it.next();
					Object mparam = state.getAttributeValue(mplan, OAVBDIMetaModel.parameterelement_has_parameters, name);
					Class clazz = (Class)state.getAttributeValue(mparam, OAVBDIMetaModel.typedelement_has_class);
					Object rparam = BeliefRules.createParameter(state, name, bindings.get(name), clazz, null, mparam, rcapa);
					state.addAttributeValue(mplancandidate, OAVBDIRuntimeModel.mplancandidate_has_bindings, rparam);
				}
			}

			boolean remove = false;
			
			// Test if exclude allows adding candidate.
			if(state.getType(rpe).isSubtype(OAVBDIRuntimeModel.goal_type))
			{
				Object mgoal = state.getAttributeValue(rpe, OAVBDIRuntimeModel.element_has_model);
				String excludemode = (String)state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_exclude);
				if(!OAVBDIMetaModel.EXCLUDE_NEVER.equals(excludemode))
				{
					List tried = (List)state.getAttributeValues(rpe, OAVBDIRuntimeModel.goal_has_triedmplans);
					if(tried!=null)
					{
						for(int i=0; !remove && i<tried.size(); i++)
						{
							Object mcand = tried.get(i);
							if(equalsMPlanCandidates(state, mcand, mplancandidate))
								remove = true;
						}
					}
				}
			}
			
			if(remove)
				state.removeAttributeValue(apl, OAVBDIRuntimeModel.apl_has_plancandidates, mplancandidate);
		}
		return apl;
	}

	/**
	 *  Remove a candidate from the apl.
	 *  // Use exclude settings to decide if plan should be removed. (todo when-failed vs. when-tried ???)
	 *  @param state The state.
	 *  @param rgoal The goal.
	 *  @param candidate The candidate.
	 * /
	protected static void removeAPLCandidate(IOAVState state, Object rgoal, Object candidate)
	{
		Object mgoal = state.getAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model);
		Object apl = state.getAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_apl);
		
		OAVObjectType type = state.getType(candidate);
		if(OAVBDIRuntimeModel.mplancandidate_type.equals(type))
		{
			if(!OAVBDIMetaModel.EXCLUDE_NEVER.equals(state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_exclude)))
				state.removeAttributeValue(apl, OAVBDIRuntimeModel.apl_has_plancandidates, candidate);
	
		}
		else //if(OAVBDIRuntimeModel.plan_type.equals(type))	
		{
			if(!OAVBDIMetaModel.EXCLUDE_NEVER.equals(state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_exclude)))
			{
				// Hack?
				Collection coll = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_waitqueuecandidates);
				if(coll!=null && coll.contains(candidate))
					state.removeAttributeValue(apl, OAVBDIRuntimeModel.apl_has_waitqueuecandidates, candidate);
				else
					state.removeAttributeValue(apl, OAVBDIRuntimeModel.apl_has_planinstancecandidates, candidate);
			}
		}
	}*/
		
	/**
	 *  Create an applicable candidate list and add it to the processable element.
	 *  @param state The state.
	 *  @param rpe The processable element.
	 *  @return The apl.
	 */
	protected static Object createAPL(IOAVState state, Object rpe)
	{
		Object apl = state.createObject(OAVBDIRuntimeModel.apl_type);
		state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_apl, apl);
//		System.out.println("Created apl for: "+rpe+" "+apl);
		return apl;
	}
	
	/**
	 *  The reasoning method.
     *  @param event The event.
	 *  @param candidatelist The list of candidates.
	 *  @return The selected candidates.
	 */
	protected static List reason(IOAVState state, Object rpe, Object apl)
	{
		if(apl==null)
			System.out.println("APL must not be null: "+rpe);
		
		// Use the plan priorities to sort the candidates.
		// If the priority is the same use the following order:
		// running plan - waitque of running plan - passive plan
		ArrayList selected = SCollection.createArrayList();

		// todo: include a number of retries...
		int numcandidates = 1;
		Object mpe = state.getAttributeValue(rpe, OAVBDIRuntimeModel.element_has_model);
		Boolean posttoall = (Boolean)state.getAttributeValue(mpe, OAVBDIMetaModel.processableelement_has_posttoall);
		if(posttoall!=null && posttoall.booleanValue())
			numcandidates = Integer.MAX_VALUE;

		List candidatelist = SCollection.createArrayList();
		Collection coll = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_planinstancecandidates);
		if(coll!=null)
		{
			for(Iterator it=coll.iterator(); it.hasNext(); )
			{
				// Plan can be aborted/timouted/etc. during reasoning.
				// Have to check if candidate is still valid.
				// Hack!!! Cannot check if still waiting for same event
				Object	cand	= it.next();
//				Object	rplan	= state.getAttributeValue(cand, OAVBDIRuntimeModel.plancandidate_has_plan);
//				if(OAVBDIRuntimeModel.PLANPROCESSINGTATE_WAITING.equals(state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate)))
					candidatelist.add(cand);
			}
		}
		
		coll = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_waitqueuecandidates);
		if(coll!=null)
		{
			for(Iterator it=coll.iterator(); it.hasNext(); )
			{
				candidatelist.add(it.next());
			}
		}
		
		coll = state.getAttributeValues(apl, OAVBDIRuntimeModel.apl_has_plancandidates);
		if(coll!=null)
			candidatelist.addAll(coll);

		Boolean tmp = (Boolean)state.getAttributeValue(mpe, OAVBDIMetaModel.processableelement_has_randomselection);
		boolean random = tmp==null? false: tmp.booleanValue();
		for(int i=0; i<numcandidates && candidatelist.size()>0; i++)
			selected.add(getNextCandidate(state, candidatelist, random, apl));

		return selected;
	}

	//-------- helper methods --------

	/**
	 *  Get the next candidate with respect to the plan
	 *  priority and the rank of the candidate.
	 *  @param candidatelist The candidate list.
	 *  @param random The random selection flag.
	 *  @return The next candidate.
	 */
	protected static Object getNextCandidate(IOAVState state, List candidatelist, boolean random, Object apl)
	{
		List finals = SCollection.createArrayList();
		finals.add(candidatelist.get(0));
		int candprio = getPriority(state, finals.get(0));
		for(int i=1; i<candidatelist.size(); i++)
		{
			Object tmp = candidatelist.get(i);
			int tmpprio = getPriority(state, tmp);
			if(tmpprio>candprio
				|| (tmpprio == candprio && getRank(state, tmp, apl)>getRank(state, finals.get(0), apl)))
			{
				finals.clear();
				finals.add(tmp);
				candprio = tmpprio;
			}
			else if(tmpprio==candprio && getRank(state, tmp, apl)==getRank(state, finals.get(0), apl))
			{
				finals.add(tmp);
			}
		}

		Object cand;
		if(random)
		{
			int rand = (int)(Math.random()*finals.size());
			cand = finals.get(rand);
			//System.out.println("Random sel: "+finals.size()+" "+rand+" "+cand);
		}
		else
		{
			//System.out.println("First sel: "+finals.size()+" "+0);
			cand = finals.get(0);
		}

		candidatelist.remove(cand);
		return cand;
	}

	/**
	 *  Get the priority of a candidate.
	 *  @return The priority of a candidate.
	 */
	protected static int getPriority(IOAVState state, Object cand)
	{
		Object	mplan;
		if(state.getType(cand).equals(OAVBDIRuntimeModel.waitqueuecandidate_type))
		{
			Object	rplan	= state.getAttributeValue(cand, OAVBDIRuntimeModel.waitqueuecandidate_has_plan);
			mplan = state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
		}
		else if(state.getType(cand).equals(OAVBDIRuntimeModel.plancandidate_type))
		{
			Object	rplan	= state.getAttributeValue(cand, OAVBDIRuntimeModel.plancandidate_has_plan);
			mplan = state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
		}
		else // if(state.getType(cand).equals(OAVBDIRuntimeModel.mplancandidate_type))
		{
			mplan = state.getAttributeValue(cand, OAVBDIRuntimeModel.mplancandidate_has_mplan);
		}
			
		Integer	prio = (Integer)state.getAttributeValue(mplan, OAVBDIMetaModel.plan_has_priority);
		
		return prio==null? 0: prio.intValue();
	}

	/**
	 *  Get the rank of a candidate.
	 *  The order is as follows:
	 *  running plan (0) -> waitqueue (1) -> plan instance (2).
	 *  @return The rank of a candidate.
	 */
	protected static int getRank(IOAVState state, Object cand, Object apl)
	{
		int rank;
		if(state.getType(cand).equals(OAVBDIRuntimeModel.waitqueuecandidate_type))
		{
			rank = 1; // waitqueue
		}
		else if(state.getType(cand).equals(OAVBDIRuntimeModel.mplancandidate_type))
		{
			rank = 0; // mplan
		}
		else //if(state.getType(cand).equals(OAVBDIRuntimeModel.plancandidate_type))
		{
			rank = 2; // running plan
		}

		return rank;
	}
	
	/**
	 *  Check the selected candidates.
	 *  @param state The state.
	 *  @param rcapa The capability.
	 *  @param rpe The processable element.
	 *  @param apl The applicable candidate list.
	 *  @param cands The candidate List.
	 *  @return The invalid candidate or null if all candidates valid.
	 */
	protected static Object checkCandidates(IOAVState state, Object rpe, Object apl, List cands)
	{
		// Assure that selected candidates are still valid.
		// Checks for each rplan and waitqueue element if
		// it still waits for such an element.
		// Note that this check must not be totally correct
		// in the sense that it might return true but only because
		// more than one trigger may match (e.g. reply and template match) 
		// todo: Use some kind of version tracking of waitabstractions?!
		Object cand = null;
		boolean ok = true;
		for(int i=0; ok && i<cands.size(); i++)
		{
			cand = cands.get(i);
			Object wa;
			if(state.getType(cand).equals(OAVBDIRuntimeModel.waitqueuecandidate_type)) 
			{
				Object rplan = state.getAttributeValue(cand, OAVBDIRuntimeModel.waitqueuecandidate_has_plan);
				wa = state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueuewa);
			}
			else if(state.getType(cand).equals(OAVBDIRuntimeModel.plancandidate_type))
			{
				Object rplan = state.getAttributeValue(cand, OAVBDIRuntimeModel.plancandidate_has_plan);
				wa = state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitabstraction);
			}
			else
			{
				continue;
			}
			
			if(wa==null)
			{
				ok = false;
			}
			else
			{
				// Belief changes need not be checked because they are posttoall and do not build an apl.

				if(state.getType(rpe).isSubtype(OAVBDIRuntimeModel.goal_type))
				{
					Collection coll = (Collection)state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds);
					ok = coll!=null && coll.contains(rpe);
				}
				else if(state.getType(rpe).isSubtype(OAVBDIRuntimeModel.internalevent_type))
				{
					Collection coll = (Collection)state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes);
					Object mpe = state.getAttributeValue(rpe, OAVBDIRuntimeModel.element_has_model);
					ok = coll!=null && coll.contains(mpe);
				}
				else if(state.getType(rpe).isSubtype(OAVBDIRuntimeModel.messageevent_type))
				{
					Collection coll = (Collection)state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_messageevents);
					Object org = state.getAttributeValue(rpe, OAVBDIRuntimeModel.messageevent_has_original);
					ok = coll!=null && coll.contains(org);
					
					if(!ok)
					{
						coll = (Collection)state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes);
						Object mpe = state.getAttributeValue(rpe, OAVBDIRuntimeModel.element_has_model);
						ok = coll.contains(mpe);
					}
				}
			}
		}			
		
		return ok? null: cand;
	}
	
	/**
	 *  Schedule the selected candidates.
	 *  @param state The state.
	 *  @param rcapa The capability.
	 *  @param rpe The processable element.
	 *  @param apl The applicable candidate list.
	 *  @param cands The candidate List.
	 */
	protected static void scheduleCandidates(IOAVState state, Object rpe, Object apl, List cands)
	{	
		for(int i=0; i<cands.size(); i++)
		{
			Object cand = cands.get(i);
			scheduleCandidate(state, rpe, apl, cand);
		}
	}
	
	/**
	 *  Schedule a candidate.
	 */
	protected static void scheduleCandidate(IOAVState state, Object rpe, Object apl, Object cand)
	{
		if(state.getType(cand).equals(OAVBDIRuntimeModel.waitqueuecandidate_type)) 
		{
			Object	rplan	= state.getAttributeValue(cand, OAVBDIRuntimeModel.waitqueuecandidate_has_plan);
			scheduleWaitqueueCandidate(state, rpe, rplan);
			
			// Save candidate in plan for later apl removal and exclude list management.
			if(state.getType(rpe).isSubtype(OAVBDIRuntimeModel.goal_type))
				state.setAttributeValue(state.getAttributeValue(cand, OAVBDIRuntimeModel.waitqueuecandidate_has_plan)
					, OAVBDIRuntimeModel.plan_has_waitqueuecandidate, cand);
		}
		else if(state.getType(cand).equals(OAVBDIRuntimeModel.mplancandidate_type))
		{
			Object rplan = schedulePlanCandidate(state, rpe, cand);
			
			// Save candidate in plan for later apl removal and exclude list management.
			if(state.getType(rpe).isSubtype(OAVBDIRuntimeModel.goal_type))
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_plancandidate, cand);
		}
		else //if(state.getType(cand).equals(OAVBDIRuntimeModel.plancandidate_type))
		{
			Object rplan = state.getAttributeValue(cand, OAVBDIRuntimeModel.plancandidate_has_plan);
			Object rcapa = state.getAttributeValue(cand, OAVBDIRuntimeModel.plancandidate_has_rcapa);
			schedulePlanInstanceCandidate(state, rpe, rplan, rcapa);
			
			// Save candidate in plan for later apl removal and exclude list management.
			if(state.getType(rpe).isSubtype(OAVBDIRuntimeModel.goal_type))
			{
//				Object	rplan	= state.getAttributeValue(cand, OAVBDIRuntimeModel.plancandidate_has_plan);
				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_planinstancecandidate, cand);
			}
		}
	}
	
	/**
	 *  Schedule a plan candidate.
	 */
	protected static Object schedulePlanCandidate(IOAVState state, Object rpe, Object cand)
	{
		Object	mplan	= state.getAttributeValue(cand, OAVBDIRuntimeModel.mplancandidate_has_mplan);
		Object	rcapa	= state.getAttributeValue(cand, OAVBDIRuntimeModel.mplancandidate_has_rcapa);
		Collection	bindings	= state.getAttributeValues(cand, OAVBDIRuntimeModel.mplancandidate_has_bindings);
		Object rplan = PlanRules.instantiatePlan(state, rcapa, mplan, null, rpe, bindings, null, null);
		PlanRules.adoptPlan(state, rcapa, rplan);
	
		state.setAttributeValue(rpe, OAVBDIRuntimeModel.processableelement_has_state, 
			OAVBDIRuntimeModel.PROCESSABLEELEMENT_CANDIDATESSELECTED);
		
		return rplan;
	}
	
	/**
	 *  Schedule a plan instance candidate.
	 */
	public static void schedulePlanInstanceCandidate(IOAVState state, Object dispelem, Object rplan, Object rcapa)//Object cand)
	{
//		System.out.println("schedulePlanInstanceCandidate: Setting plan to ready: "
//			+BDIAgentFeature.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName()
//			+", "+rplan);
		
//		Object	rplan	= state.getAttributeValue(cand, OAVBDIRuntimeModel.plancandidate_has_plan);
//		Object	rcapa	= state.getAttributeValue(cand, OAVBDIRuntimeModel.plancandidate_has_rcapa);
		state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, 
			OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
		state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement, dispelem);
		PlanRules.cleanupPlanWait(state, rcapa, rplan, false);
		
		if(dispelem!=null && state.getType(dispelem).isSubtype(OAVBDIRuntimeModel.processableelement_type))
		{
			state.setAttributeValue(dispelem, OAVBDIRuntimeModel.processableelement_has_state, 
				OAVBDIRuntimeModel.PROCESSABLEELEMENT_CANDIDATESSELECTED);
		}
	}
	
	/**
	 *  Schedule a waitqueue candidate.
	 */
	protected static void scheduleWaitqueueCandidate(IOAVState state, Object rpe, Object rplan)
	{
		state.addAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements, rpe);
	}
	
	/**
	 *  Test if two plan candidates are equal.
	 *  @param state The state.
	 *  @param mplancand1 The first candidate.
	 *  @param mplancand2 The second candidate.
	 *  @return True, if equal.
	 */
	protected static boolean equalsMPlanCandidates(IOAVState state, Object mplancand1, Object mplancand2)
	{
		if(mplancand1==mplancand2)
			return true;
		if(mplancand1==null || mplancand2==null)
			return false;
		
		Object mplan1 = state.getAttributeValue(mplancand1, OAVBDIRuntimeModel.mplancandidate_has_mplan);
		Object mplan2 = state.getAttributeValue(mplancand2, OAVBDIRuntimeModel.mplancandidate_has_mplan);
		
		boolean ret = mplan1==mplan2;
		
		if(ret)
		{
			List binds1 = (List)state.getAttributeValues(mplancand1, OAVBDIRuntimeModel.mplancandidate_has_bindings);
			List binds2 = (List)state.getAttributeValues(mplancand1, OAVBDIRuntimeModel.mplancandidate_has_bindings);
			ret = equalsBindings(state, binds1, binds2);
		}
			
		return ret;
	}
	
	/**
	 *  Test if two bindinds are equal.
	 *  @param state The state.
	 *  @param binds1 The first bindings.
	 *  @param binds2 The second bindings.
	 *  @return True, if equal.
	 */
	protected static boolean equalsBindings(IOAVState state, List binds1, List binds2)
	{
		if(binds1==binds2)
			return true;
		if(binds1==null || binds2==null)
			return false;
		
		boolean ret = binds1.size()==binds2.size();
		// Assume same parameter ordering (Hack)?!
		for(int i=0; ret && i<binds1.size(); i++)
		{
			Object param1 = binds1.get(i);
			Object param2 = binds2.get(i);
			ret = equalsParam(state, param1, param2);
		}
		
		return ret;
	}
	
	/**
	 *  Test if two parameters are equal.
	 *  @param state The state.
	 *  @param param1 The first runtime parameter.
	 *  @param param2 The second runtime parameter.
	 *  @return True, if equal.
	 */
	protected static boolean equalsParam(IOAVState state, Object param1, Object param2)
	{
		if(param1==param2)
			return true;
		if(param1==null || param2==null)
			return false;
		
		String name1 = (String)state.getAttributeValue(param1, OAVBDIRuntimeModel.parameter_has_name);
		Object value1 = state.getAttributeValue(param1, OAVBDIRuntimeModel.parameter_has_value);
		String name2 = (String)state.getAttributeValue(param2, OAVBDIRuntimeModel.parameter_has_name);
		Object value2 = state.getAttributeValue(param2, OAVBDIRuntimeModel.parameter_has_value);
		
		return SUtil.equals(name1, name2) && SUtil.equals(value1, value2);
	}
	
	/**
	 *  Marker class for distinguishing between plan instance 
	 *  candidates and waitqueue candidates (both plan_type).
	 * /
	static class WaitqueueCandidate
	{
		/** The waitqueue candidate. * /
		protected Object waitqueuecandidate;
		
		/**
		 *  Create a new {@link WaitqueueCandidate} candidate. 
		 * /
		public WaitqueueCandidate(Object waitqueuecandidate)
		{
			this.waitqueuecandidate = waitqueuecandidate;
		}

		/**
		 *  Get the waitqueuecandidate.
		 *  @return The waitqueuecandidate.
		 * /
		public Object getWaitqueueCandidate()
		{
			return waitqueuecandidate;
		}
	}*/
}
