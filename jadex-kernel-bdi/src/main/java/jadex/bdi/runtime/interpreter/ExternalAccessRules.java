package jadex.bdi.runtime.interpreter;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.impl.WakeupAction;
import jadex.bridge.service.types.clock.ITimer;
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

/**
 *  Rules for the external access.
 */
public class ExternalAccessRules
{
	/**
	 *  Trigger external access (on goal finished).
	 */
	protected static Rule createExternalAccessGoalTriggeredRule()
	{
		Variable ea	= new Variable("?ea", OAVBDIRuntimeModel.externalaccess_type);
		Variable wa	= new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.goal_type);
		Variable mpe = new Variable("?mpe", OAVBDIMetaModel.processableelement_type);
//		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	eacon = new ObjectCondition(OAVBDIRuntimeModel.externalaccess_type);
		eacon.addConstraint(new BoundConstraint(null, ea));
		eacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.externalaccess_has_waitabstraction, wa));

		ObjectCondition	rpecon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		rpecon.addConstraint(new BoundConstraint(null, rpe));
		rpecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPED));
		rpecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mpe));
		
//		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
//		capcon.addConstraint(new BoundConstraint(null, rcapa));
//		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_goals, rpe, IOperator.CONTAINS));

		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
		wacon.addConstraint(new BoundConstraint(null, wa));
		IConstraint co1 = new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_goals, rpe, IOperator.CONTAINS);
		IConstraint co2 = new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds, mpe, IOperator.CONTAINS);
		wacon.addConstraint(new OrConstraint(new IConstraint[]{co1, co2}));
		
		Rule ea_trigger = new Rule("externalaccess_goaltrigger",
			new AndCondition(new ICondition[]{eacon, rpecon, wacon}),
			EXTERNALACCESS_NOTIFY, IPriorityEvaluator.PRIORITY_1);
		return ea_trigger;
	}	
	
	/**
	 *  Trigger external access (on message events).
	 */
	protected static Rule createExternalAccessMessageEventTriggeredRule()
	{
		Variable ea	= new Variable("?ea", OAVBDIRuntimeModel.externalaccess_type);
		Variable wa	= new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.processableelement_type);
		Variable mpe = new Variable("?mpe", OAVBDIMetaModel.processableelement_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable orig = new Variable("?orig", OAVBDIRuntimeModel.messageevent_type);
		
		ObjectCondition	eacon = new ObjectCondition(OAVBDIRuntimeModel.externalaccess_type);
		eacon.addConstraint(new BoundConstraint(null, ea));
		eacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.externalaccess_has_waitabstraction, wa));

		ObjectCondition	rpecon	= new ObjectCondition(OAVBDIRuntimeModel.processableelement_type);
		rpecon.addConstraint(new BoundConstraint(null, rpe));
		rpecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mpe));
		rpecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.messageevent_has_original, orig));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		capcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_messageevents, rpe, IOperator.CONTAINS));

		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
		wacon.addConstraint(new BoundConstraint(null, wa));
		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_messageevents, orig, IOperator.CONTAINS));
		
		Rule ea_trigger = new Rule("externalaccess_messageeventtrigger",
			new AndCondition(new ICondition[]{eacon, rpecon, capcon, wacon}),
			EXTERNALACCESS_NOTIFY, IPriorityEvaluator.PRIORITY_1);
		return ea_trigger;
	}	
	
	/**
	 *  Trigger external access (on events).
	 */
	protected static Rule createExternalAccessEventTriggeredRule()
	{
		Variable ea	= new Variable("?ea", OAVBDIRuntimeModel.externalaccess_type);
		Variable wa	= new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.processableelement_type);
		Variable mpe = new Variable("?mpe", OAVBDIMetaModel.processableelement_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		
		ObjectCondition	eacon = new ObjectCondition(OAVBDIRuntimeModel.externalaccess_type);
		eacon.addConstraint(new BoundConstraint(null, ea));
		eacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.externalaccess_has_waitabstraction, wa));

		ObjectCondition	rpecon	= new ObjectCondition(OAVBDIRuntimeModel.processableelement_type);
		rpecon.addConstraint(new BoundConstraint(null, rpe));
		rpecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mpe));
		
		ObjectCondition	capcon	= new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		capcon.addConstraint(new BoundConstraint(null, rcapa));
		IConstraint c1 = new BoundConstraint(OAVBDIRuntimeModel.capability_has_internalevents, rpe, IOperator.CONTAINS);
		IConstraint c2 = new BoundConstraint(OAVBDIRuntimeModel.capability_has_messageevents, rpe, IOperator.CONTAINS);
		capcon.addConstraint(new OrConstraint(new IConstraint[]{c1, c2}));
//		capcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.agent_has_eventprocessing, null));

		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
		wacon.addConstraint(new BoundConstraint(null, wa));
		IConstraint co1 = new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes, mpe, IOperator.CONTAINS);
		IConstraint co2 = new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes, mpe, IOperator.CONTAINS);
		wacon.addConstraint(new OrConstraint(new IConstraint[]{co1, co2}));
		
		Rule ea_trigger = new Rule("externalaccess_eventtrigger",
			new AndCondition(new ICondition[]{eacon, rpecon, capcon, wacon}),
			EXTERNALACCESS_NOTIFY, IPriorityEvaluator.PRIORITY_1);
		return ea_trigger;
	}	
	
	/**
	 *  Trigger plan creation on fact changed event.
	 */
	protected static Rule createExternalAccessFactChangedTriggeredRule()
	{
		Variable ea	= new Variable("?ea", OAVBDIRuntimeModel.externalaccess_type);
		Variable wa	= new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable rel = new Variable("?rel", OAVBDIRuntimeModel.processableelement_type);
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.changeevent_type);
			
		ObjectCondition	eacon = new ObjectCondition(OAVBDIRuntimeModel.externalaccess_type);
		eacon.addConstraint(new BoundConstraint(null, ea));
		eacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.externalaccess_has_waitabstraction, wa));

		ObjectCondition	changecon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		changecon.addConstraint(new BoundConstraint(null, rpe));
		changecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTCHANGED));
		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rel));

		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
		wacon.addConstraint(new BoundConstraint(null, wa));
		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factchangeds, rel, IOperator.CONTAINS));
		
		Rule externalaccess_facttrigger = new Rule("externalaccess_factchangedtrigger",
			new AndCondition(new ICondition[]{eacon, changecon, wacon}),
			EXTERNALACCESS_NOTIFY, IPriorityEvaluator.PRIORITY_1);
		return externalaccess_facttrigger;
	}
	
	/**
	 *  Trigger plan creation on fact added event.
	 */
	protected static Rule createExternalAccessFactAddedTriggeredRule()
	{
		Variable ea	= new Variable("?ea", OAVBDIRuntimeModel.externalaccess_type);
		Variable wa	= new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable rel = new Variable("?rel", OAVBDIRuntimeModel.processableelement_type);
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.changeevent_type);
			
		ObjectCondition	eacon = new ObjectCondition(OAVBDIRuntimeModel.externalaccess_type);
		eacon.addConstraint(new BoundConstraint(null, ea));
		eacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.externalaccess_has_waitabstraction, wa));

		ObjectCondition	changecon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		changecon.addConstraint(new BoundConstraint(null, rpe));
		changecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED));
		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rel));

		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
		wacon.addConstraint(new BoundConstraint(null, wa));
		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factaddeds, rel, IOperator.CONTAINS));
		
		Rule externalaccess_facttrigger = new Rule("externalaccess_factaddedtrigger",
			new AndCondition(new ICondition[]{eacon, changecon, wacon}),
			EXTERNALACCESS_NOTIFY, IPriorityEvaluator.PRIORITY_1);
		return externalaccess_facttrigger;
	}
	
	/**
	 *  Trigger plan creation on fact removed event.
	 */
	protected static Rule createExternalAccessFactRemovedTriggeredRule()
	{
		Variable ea	= new Variable("?ea", OAVBDIRuntimeModel.externalaccess_type);
		Variable wa	= new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable rel = new Variable("?rel", OAVBDIRuntimeModel.processableelement_type);
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.changeevent_type);
			
		ObjectCondition	eacon = new ObjectCondition(OAVBDIRuntimeModel.externalaccess_type);
		eacon.addConstraint(new BoundConstraint(null, ea));
		eacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.externalaccess_has_waitabstraction, wa));

		ObjectCondition	changecon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		changecon.addConstraint(new BoundConstraint(null, rpe));
		changecon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTREMOVED));
		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rel));

		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
		wacon.addConstraint(new BoundConstraint(null, wa));
		wacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factremoveds, rel, IOperator.CONTAINS));
		
		Rule externalaccess_facttrigger = new Rule("externalaccess_factremovedtrigger",
			new AndCondition(new ICondition[]{eacon, changecon, wacon}),
			EXTERNALACCESS_NOTIFY, IPriorityEvaluator.PRIORITY_1);
		return externalaccess_facttrigger;
	}
	
	/**
	 *  Trigger plan creation on fact changed/added/removed event.
	 * /
	protected static Rule createExternalAccessFactTriggeredRule()
	{
		Variable ea	= new Variable("?ea", OAVBDIRuntimeModel.externalaccess_type);
		Variable wa	= new Variable("?wa", OAVBDIRuntimeModel.waitabstraction_type);
		Variable rel = new Variable("?rel", OAVBDIRuntimeModel.processableelement_type);
		Variable rpe = new Variable("?rpe", OAVBDIRuntimeModel.changeevent_type);
			
		ObjectCondition	eacon = new ObjectCondition(OAVBDIRuntimeModel.externalaccess_type);
		eacon.addConstraint(new BoundConstraint(null, ea));
		eacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.externalaccess_has_waitabstraction, wa));

		ObjectCondition	changecon = new ObjectCondition(OAVBDIRuntimeModel.changeevent_type);
		changecon.addConstraint(new BoundConstraint(null, rpe));
		IConstraint con1 = new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTCHANGED);
		IConstraint con2 = new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTADDED);
		IConstraint con3 = new LiteralConstraint(OAVBDIRuntimeModel.changeevent_has_type, OAVBDIRuntimeModel.CHANGEEVENT_FACTREMOVED);
		changecon.addConstraint(new OrConstraint(new IConstraint[]{con1, con2, con3}));
		changecon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.changeevent_has_element, rel));

		ObjectCondition	wacon = new ObjectCondition(OAVBDIRuntimeModel.waitabstraction_type);
		wacon.addConstraint(new BoundConstraint(null, wa));
		IConstraint co1 = new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factchangeds, rel, IOperator.CONTAINS);
		IConstraint co2 = new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factaddeds, rel, IOperator.CONTAINS);
		IConstraint co3 = new BoundConstraint(OAVBDIRuntimeModel.waitabstraction_has_factremoveds, rel, IOperator.CONTAINS);
		wacon.addConstraint(new OrConstraint(new IConstraint[]{co1, co2, co3}));
		
		Rule externalaccess_facttrigger = new Rule("externalaccess_facttrigger",
			new AndCondition(new ICondition[]{eacon, changecon, wacon}),
			EXTERNALACCESS_NOTIFY, IPriorityEvaluator.PRIORITY_1);
		return externalaccess_facttrigger;
	}*/
		
	/**
	 *  External access notifcation action. Resumes the external access thread.
	 */
	protected static final IAction EXTERNALACCESS_NOTIFY = new IAction()
	{
		public void execute(IOAVState state, IVariableAssignments assignments)
		{
			Object ea = assignments.getVariableValue("?ea");
			Object rpe = assignments.getVariableValue("?rpe");
//			System.out.println("External access notified: "+ea+" "+rpe);
			if(state.containsObject(ea))
			{
				state.setAttributeValue(ea, OAVBDIRuntimeModel.externalaccess_has_dispatchedelement, rpe);
				WakeupAction wakeup = (WakeupAction)state.getAttributeValue(ea, OAVBDIRuntimeModel.externalaccess_has_wakeupaction);

				// Cleanup
				ITimer timer = (ITimer)state.getAttributeValue(ea, OAVBDIRuntimeModel.externalaccess_has_timer);
				if(timer!=null)
				{
					timer.cancel();
					state.setAttributeValue(ea, OAVBDIRuntimeModel.externalaccess_has_timer, null);
				}
				state.setAttributeValue(ea, OAVBDIRuntimeModel.externalaccess_has_waitabstraction, null);

				// Notify external thread.
				wakeup.setTimeout(false);
				wakeup.run();
			}
		}
	};
}
