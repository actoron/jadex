package jadex.bdi.runtime.interpreter;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IExternalCondition;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.IResultCommand;
import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.future.Future;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;

import java.util.HashMap;
import java.util.Map;

/**
 *  OAV runtime BDI-meta model.
 */
public class OAVBDIRuntimeModel
{
	//-------- agent states --------
	
//	/** The agent init0 state. */
//	public static final String	AGENTLIFECYCLESTATE_INITING0	= "initing0";
//	
//	/** The agent init1 state. */
//	public static final String	AGENTLIFECYCLESTATE_INITING1	= "initing1";
	
	/** The agent alive state. */
	public static final String	AGENTLIFECYCLESTATE_ALIVE	= "alive";
	
	/** The agent terminating state (only cleanup-related activities are allowed). */
	public static final String	AGENTLIFECYCLESTATE_TERMINATING	= "terminating";

	/** The agent terminated state (no more actions are allowed). */
	public static final String	AGENTLIFECYCLESTATE_TERMINATED	= "terminated";

	
	/** The processable element state unprocessed. */
	public static final String	PROCESSABLEELEMENT_UNPROCESSED	= "unprocessed";

	/** The processable element state apl available. */
	public static final String	PROCESSABLEELEMENT_APLAVAILABLE	= "aplavailable";
	
	/** The processable element state meta-level reasoning. */
	public static final String	PROCESSABLEELEMENT_METALEVELREASONING	= "metalevelreasoning";
	
	/** The processable element state no candidates. */
	public static final String	PROCESSABLEELEMENT_NOCANDIDATES	= "nocandidates";

	/** The processable element state candidate selected. */
	public static final String	PROCESSABLEELEMENT_CANDIDATESSELECTED	= "candidatesselected";
		
	//-------- goal lifecycle states --------
	
	/** The lifecycle state "new" (just created). */
	public static final String	GOALLIFECYCLESTATE_NEW	= "new";

	/** The lifecycle state "adopted" (adopted, but not active). */
	public static final String	GOALLIFECYCLESTATE_ADOPTED	= "adopted";

	/** The lifecycle state "option" (adopted, but not active). */
	public static final String	GOALLIFECYCLESTATE_OPTION	= "option";

	/** The lifecycle state "active" (adopted and processed or monitored). */
	public static final String	GOALLIFECYCLESTATE_ACTIVE	= "active";

	/** The lifecycle state "active" (adopted and processed or monitored). */
	public static final String	GOALLIFECYCLESTATE_SUSPENDED	= "suspended";

	/** The lifecycle state "dropping" (just before finished, but still dropping its subgoals). */
	public static final String	GOALLIFECYCLESTATE_DROPPING	= "dropping";

	/** The lifecycle state "dropped" (goal and all subgoals finished). */
	public static final String	GOALLIFECYCLESTATE_DROPPED	= "dropped";
	
	//-------- goal processing states --------
	
	/** The goal idle state. */
	public static final String	GOALPROCESSINGSTATE_IDLE	= "idle";
	
	/** The goal in-process state. */
	public static final String	GOALPROCESSINGSTATE_INPROCESS	= "in-process";

	/** The goal paused state. */
	public static final String	GOALPROCESSINGSTATE_PAUSED	= "paused";
	
	/** The goal succeeded state. */
	public static final String	GOALPROCESSINGSTATE_SUCCEEDED	= "succeeded";
	
	/** The goal failed state. */
	public static final String	GOALPROCESSINGSTATE_FAILED	= "failed";
	
	//-------- plan states --------
	
	/** The plan ready state. */
	public static final String	PLANPROCESSINGTATE_READY	= "ready";
	
	/** The plan running state. */
	public static final String	PLANPROCESSINGTATE_RUNNING	= "running";
	
	/** The plan waiting state. */
	public static final String	PLANPROCESSINGTATE_WAITING	= "waiting";
	
	/** The plan goalcleanup state (wait for subgoals being dropped
	 *  after body is exited and before passed/failed/aborted is called). */
	public static final String	PLANPROCESSINGTATE_GOALCLEANUP	= "goalcleanup";
	
	/** The plan finished state. */
	public static final String	PLANPROCESSINGTATE_FINISHED	= "finished";
	
	/** The lifecycle state "new" (just created). */
	public static final String	PLANLIFECYCLESTATE_NEW	= "new";
	
	/** The state, indicating the execution of the plan body. */
	public static final String	PLANLIFECYCLESTATE_BODY	= "body";
	
	/** The state, indicating the execution of the passed code. */
	public static final String	PLANLIFECYCLESTATE_PASSED	= "passed";
	
	/** The state, indicating the execution of the failed code. */
	public static final String	PLANLIFECYCLESTATE_FAILED	= "failed";
	
	/** The state, indicating the execution of the aborted. */
	public static final String	PLANLIFECYCLESTATE_ABORTED	= "aborted";
	
	//-------- changeevent types --------
	
	/** The fact changed changeeevent type. */
	public static final String	CHANGEEVENT_FACTCHANGED	= "factchanged";
	
	/** The fact added changeeevent type. */
	public static final String	CHANGEEVENT_FACTADDED	= "factadded";
	
	/** The fact removed changeeevent type. */
	public static final String	CHANGEEVENT_FACTREMOVED	= "factremoved";
	
	/** The internal event changeeevent type. */
	public static final String	CHANGEEVENT_INTERNALEVENTOCCURRED	= "internaleventoccurred";
	
	/** The message event sent changeeevent type. */
	public static final String	CHANGEEVENT_MESSAGEEVENTSENT	= "messageeventsent";
	
	/** The message event received changeeevent type. */
	public static final String	CHANGEEVENT_MESSAGEEVENTRECEIVED	= "messageeventreceived";
	
	/** The goal added changeeevent type. */
	public static final String	CHANGEEVENT_GOALADDED	= "goaladded";
	
	/** The goal dropped changeeevent type. */
	public static final String	CHANGEEVENT_GOALDROPPED	= "goaldropped";
	
	/** The goal changed changeeevent type. */
	// Hack!!! only for BDI viewer
	public static final String	CHANGEEVENT_GOALCHANGED	= "goalchanged";
	
	/** The agent terminating changeeevent type. */
	public static final String	CHANGEEVENT_AGENTTERMINATING	= "agentterminating";
	
	/** The agent terminated changeeevent type. */
	public static final String	CHANGEEVENT_AGENTTERMINATED	= "agentterminated";
	
	/** The plan added changeeevent type. */
	public static final String	CHANGEEVENT_PLANADDED	= "planadded";
	
	/** The plan finished changeeevent type. */
	public static final String	CHANGEEVENT_PLANREMOVED	= "planremoved";
	
	/** The goal changed changeeevent type. */
	// Hack!!! only for BDI viewer
	public static final String	CHANGEEVENT_PLANCHANGED	= "planchanged";
	
	//-------- model --------
	
	/** The runtime model. */
	public static final OAVTypeModel bdi_rt_model;

	//-------- java types --------
	
	/** Java type for runnables. */
	public static final OAVJavaType java_runnable_type;
	
	/** Java type for maps. */
	public static final OAVJavaType java_map_type;
	
	/** Java type for IMessageAdapter. */
	public static final OAVJavaType java_imessageadapter_type;
	
//	/** The plan executor type. */
//	public static OAVJavaType java_planexecutor_type;
	
	/** The property change listener type. */
	public static final OAVJavaType java_propertychangelistener_type;
	
	/** The timer type. */
	public static final OAVJavaType java_timer_type;
	
	/** The future type. */
	public static final OAVJavaType java_future_type;
	
	/** The external condition type. */
	public static final OAVJavaType java_externalcondition_type;
	
	/** The service provider type. */
//	public static final OAVJavaType java_serviceprovider_type;
	
	/** The result command type. */
	public static final OAVJavaType java_resultcommand_type;
	
	/** The component listener type. */
//	public static OAVJavaType java_componentlistener_type;

	
	//-------- element --------
	
	/** The element (model) type. */
	public static final OAVObjectType element_type;
	
	/** The element has a model. */
	public static final OAVAttributeType element_has_model;
	
	//-------- agent --------
	
	/** The agent (model) type. */
	public static final OAVObjectType agent_type;
	
//	/** The agent has a name. */
//	public static OAVAttributeType agent_has_name;
	
//	/** The agent has a local name. */
//	public static OAVAttributeType agent_has_localname;
	
	/** The agent has a state. */
	public static final OAVAttributeType agent_has_state;
	
	/** The agent has an inbox for raw messages. */
	public static final OAVAttributeType agent_has_inbox;
	
	/** The agent has actions (invoke later actions to be executed by the agent). */
	public static final OAVAttributeType agent_has_actions;

	/** The agent has arguments (only available during start agent action). */
	// Hack!!! remove???
	public static final OAVAttributeType agent_has_arguments;
	
	/** The agent results. */
//	public static OAVAttributeType agent_has_results;
	
//	/** The agent has service bindings. */
//	public static OAVAttributeType agent_has_bindings;
	
	/** The agent has init parents (only available during start agent action). */
	// Hack!!! remove???
	public static final OAVAttributeType agent_has_initparents;
	
	/** The agent has a timer attribute (when waiting for termination). */
	public static final OAVAttributeType agent_has_timer;
	
	/** The agent has kill future (waiting to be notified after termination). */
	public static final OAVAttributeType agent_has_killfuture;
	
//	/** The agent has component listeners (waiting to be notified about component events */
//	public static final OAVAttributeType agent_has_componentlisteners;
	
	/** The agent has change events. */
	public static final OAVAttributeType agent_has_changeevents;
	
	/** The agent has a processable element processing attribute. 
	    It is set to the element in the buildAPL rules to prevent that other 
	    buildAPL rules are triggered before the corresponding select and schedule
		candidate has been done. (Allowing interleaved buildAPLs could
		lead to the same plan instance candidate being selected which is
		only valid in the first schedule action). */
	// todo: hack remove somehow (e.g. rule priorities / ruleflow processing?!)
//	public static OAVAttributeType agent_has_eventprocessing;
	
	/** The agent has a service provider. */
//	public static OAVAttributeType agent_has_serviceprovider;

	
	//-------- capability --------
	
	/** The capability type. */
	public static final OAVObjectType capability_type;
	
	/** Capability has subcapabilities attribute. */
	public static final OAVAttributeType capability_has_subcapabilities;	
	
	/** Capability has beliefs attribute. */
	public static final OAVAttributeType capability_has_beliefs;
	
	/** Capability has belief sets attribute. */
	public static final OAVAttributeType capability_has_beliefsets;
	
	/** Capability has goals attribute. */
	public static final OAVAttributeType capability_has_goals;
	
	/** Capability has plans attribute. */
	public static final OAVAttributeType capability_has_plans;
	
//	/** Capability has events attribute. */
//	public static final OAVAttributeType capability_has_events;
	
	/** Capability has message events. */
	public static final OAVAttributeType capability_has_messageevents;
	
	/** Capability has message events. */
	public static final OAVAttributeType capability_has_internalevents;
	
//	/** Capability has expressions attribute. */
//	public static final OAVAttributeType capability_has_expressions;
		
	/** The capability has configuration. */
	public static final OAVAttributeType capability_has_configuration;
	
//	/** Capability has properties attribute. */
//	public static OAVAttributeType capability_has_properties;

	

	/** The capability has listeners. */
	public static final OAVAttributeType capability_has_listeners;
	
	/** The capability has sent messages (for conversation tracking). */
	public static final OAVAttributeType capability_has_sentmessageevents;
	
	/** The capability has an outbox for outgoing message events. */
	public static final OAVAttributeType capability_has_outbox;
	
	/** The capability has external access elements (timer and dispatched element). */
	public static final OAVAttributeType capability_has_externalaccesses;
		
	/** The capability has precandidates (prematched mplans created on init). */
	public static final OAVAttributeType capability_has_precandidates;
		
	/** The capability has assigntosources (sources for abstract elements, assigned on init). */
	public static final OAVAttributeType capability_has_abstractsources;
			
	
	//-------- capability reference --------
	
	/** The capability reference type. */
	public static final OAVObjectType capabilityreference_type;
	
	/** Capability reference has name attribute. */
	public static final OAVAttributeType capabilityreference_has_name;	
	
	/** Capability reference has capability attribute. */
	public static final OAVAttributeType capabilityreference_has_capability;	
	
	//-------- external access --------
	
	/** The external access type. */
	public static final OAVObjectType externalaccess_type;
	
	/** External access has timer. */
	public static final OAVAttributeType externalaccess_has_waitabstraction;	
	
	/** External access has timer. */
	public static final OAVAttributeType externalaccess_has_timer;	
	
	/** External access has dispatched element. */
	public static final OAVAttributeType externalaccess_has_dispatchedelement;	
	
	/** External access has wakeup action. */
	public static final OAVAttributeType externalaccess_has_wakeupaction;
	
	//-------- abstract typed element --------
	
	/** The typed element type. */
	public static final OAVObjectType typedelement_type;
	
	/** Typed element has property change listener. */
	public static final OAVAttributeType typedelement_has_propertychangelistener;
	
	/** Typed element has a timer (when update rate is used). */
	public static final OAVAttributeType typedelement_has_timer;
	
//	/** Typed element has class attribute. */
//	public static OAVAttributeType typedelement_has_class;
//	
//	/** Typed element has update rate attribute. */
//	public static OAVAttributeType typedelement_has_updaterate;
//	
//	/** Typed element has transient attribute. */
//	public static OAVAttributeType typedelement_has_transient;
//	
//	/** Typed element has dynamic attribute. */
//	public static OAVAttributeType typedelement_has_dynamic;

	//-------- belief --------
	
	/** The belief type. */
	public static final OAVObjectType belief_type;
	
	/** The belief has a fact. */
	public static final OAVAttributeType belief_has_fact;
	
	/** The beliefset type. */
	public static final OAVObjectType beliefset_type;
	
	/** The beliefset has facts. */
	public static final OAVAttributeType beliefset_has_facts;

	//-------- goal --------
	
	/** The goal type. */
	public static final OAVObjectType goal_type;
	
	/** The goal has a parent attribute. */
//	public static OAVAttributeType goal_has_parent;
	
	/** The goal has plans attribute. */
	public static final OAVAttributeType goal_has_finishedplans;
	
	/** The goal has lifecyclestate attribute. */
	public static final OAVAttributeType goal_has_lifecyclestate;

	/** The goal has processingstate attribute. */
	public static final OAVAttributeType goal_has_processingstate;
	
	/** Protected goals will not be dropped when terminating. */
	public static final OAVAttributeType goal_has_protected;
	
	/** The goal has a parent plan attribute (hack!!! redundancy to plan_has_subgoals). */
	public static final OAVAttributeType goal_has_parentplan;
	
	/** The goal has exception attribute. */
	public static final OAVAttributeType goal_has_exception;
	
	/** The goal has tried mplan candidates. */
	public static final OAVAttributeType goal_has_triedmplans;
	
	/** The goal has a retry timer. */
	public static final OAVAttributeType goal_has_retrytimer;
	
	/** The goal has a recur timer. */
	public static final OAVAttributeType goal_has_recurtimer;
	
	/** The goal has plans to which the finished event was already dispatched. */
	public static final OAVAttributeType goal_has_finisheddispatchedplans;
	
	/** The goal has inhibitors attribute. */
	public static final OAVAttributeType goal_has_inhibitors;
	
	//-------- plan --------
	
	/** The plan type. */
	public static final OAVObjectType plan_type;
	
	/** The plan has a body attribute. */
	public static final OAVAttributeType plan_has_body;
	
	/** The plan has a body attribute. */
	public static final OAVAttributeType plan_has_step;
	
	/** The plan has an executor attribute. */
	//public static OAVAttributeType plan_has_executor;
	
	/** The plan has an event attribute. */
//	public static OAVAttributeType plan_has_event;
	
	/** The plan has a reason. */
	public static final OAVAttributeType plan_has_reason;

	/** The plan has a dispatched element (current goal/event). */
	public static final OAVAttributeType plan_has_dispatchedelement;
	
	/** The plan has a root goal attribute. */
//	public static OAVAttributeType plan_has_rootgoal;
	
	/** The plan has a context condition attribute. */
	//public static OAVAttributeType plan_has_contextcondition;
	
	/** The plan has subgoals attribute (hack!!! redundancy to goal_has_parentplan). */
	public static final OAVAttributeType plan_has_subgoals;
		
	/** The plan has a wait abstraction attribute. */
	public static final OAVAttributeType plan_has_waitabstraction;
		
	/** The plan has a waitqueue wait abstraction attribute. */
	public static final OAVAttributeType plan_has_waitqueuewa;
	
	/** The plan has a waitqueue processable elements attribute. */
	public static final OAVAttributeType plan_has_waitqueueelements;
	
	/** The plan has exception attribute. */
	public static final OAVAttributeType plan_has_exception;
	
	/** The plan has lifecycle state attribute. */
	public static final OAVAttributeType plan_has_lifecyclestate;
	
	/** The plan has processing state attribute (ready or waiting). */
	public static final OAVAttributeType plan_has_processingstate;
	
	/** The plan has a timer attribute (when waiting). */
	public static final OAVAttributeType plan_has_timer;
	
	/** The plan has user variables. */
//	public static OAVAttributeType plan_has_uservariables;
	
	/** The plan has a candidate (for exclude set / apl removal). */
	public static final OAVAttributeType plan_has_plancandidate;
	public static final OAVAttributeType plan_has_planinstancecandidate;
	public static final OAVAttributeType plan_has_waitqueuecandidate;
	
	//-------- parameter --------
	
	/** The parameter type. */
	public static final OAVObjectType parameter_type;
	
	/** The parameter has a name attribute. */
	public static final OAVAttributeType parameter_has_name;
	
	/** The parameter has a value attribute. */
	public static final OAVAttributeType parameter_has_value;
	
	/** The parameter has a type. */
	public static final OAVAttributeType parameter_has_type;

	
	/** The parameterset type. */
	public static final OAVObjectType parameterset_type;
	
	/** The parameterset has a name attribute. */
	public static final OAVAttributeType parameterset_has_name;
	
	/** The parameterset has a values attribute. */
	public static final OAVAttributeType parameterset_has_values;
	
	/** The parameterset has a type. */
	public static final OAVAttributeType parameterset_has_type;
	
	//-------- parameter element --------
	
	/** The parameter element type. */
	public static final OAVObjectType parameterelement_type;
	
	/** The parameter element has parameters. */
	public static final OAVAttributeType parameterelement_has_parameters;
	
	/** The parameter element has parameter sets. */
	public static final OAVAttributeType parameterelement_has_parametersets;
	
	//-------- processable element (knows apl) --------
	
	/** The processable element type. */
	public static final OAVObjectType processableelement_type;
	
	/** The processable element has an apl. */
	public static final OAVAttributeType processableelement_has_apl;
	
	/** The processable element has a state. */
	public static final OAVAttributeType processableelement_has_state;
	
	//-------- The apl type. --------
	
	/** The applicable candidate list. */
	public static final OAVObjectType apl_type;
	
	/** The apl has metagoal. */
	public static final OAVAttributeType apl_has_metagoal;
	
	/** The apl has plan candidates. */
	public static final OAVAttributeType apl_has_plancandidates;
	
	/** The apl has plan instance candidates. */
	public static final OAVAttributeType apl_has_planinstancecandidates;
	
	/** The apl has plan waitqueue candidates. */
	public static final OAVAttributeType apl_has_waitqueuecandidates;
	
	//-------- mplancandidate --------
	
	/** The mplancandidate type. */
	public static final OAVObjectType	mplancandidate_type;
	
	/** The mplancandidate has an mplan. */
	public static final OAVAttributeType	mplancandidate_has_mplan;

	/** The mplancandidate has bindings. */
	public static final OAVAttributeType	mplancandidate_has_bindings;
	
	/** The mplancandidate has plan instance. */
	public static final OAVAttributeType	mplancandidate_has_plan;

	/** The mplancandidate has an rcapability. */
	public static final OAVAttributeType	mplancandidate_has_rcapa;

	//-------- plancandidate --------
	
	/** The plancandidate type. */
	public static final OAVObjectType	plancandidate_type;
	
	/** The plancandidate has plan instance. */
	public static final OAVAttributeType	plancandidate_has_plan;

	/** The plancandidate has an rcapability. */
	public static final OAVAttributeType	plancandidate_has_rcapa;
	
	//-------- waitqueuecandidate --------
	
	/** The waitqueuecandidate type. */
	public static final OAVObjectType	waitqueuecandidate_type;
	
	/** The waitqueuecandidate has plan instance. */
	public static final OAVAttributeType	waitqueuecandidate_has_plan;

	/** The waitqueuecandidate has an rcapability. */
	public static final OAVAttributeType	waitqueuecandidate_has_rcapa;

	//-------- precandidate --------
	
	/** The precandidate type represents mplans that match an event or goal,
	 *  but before precondition and bindings have been evaluated
	 *  (i.e. relevant, but maybe not applicable). */
	public static final OAVObjectType	precandidate_type;
	
	/** The precandidate has an mplan. */
	public static final OAVAttributeType	precandidate_has_mplan;

	/** The precandidate has an rcapability. */
	public static final OAVAttributeType	precandidate_has_capability;
	
	/** The precandidate has a trigger reference. */
	public static final OAVAttributeType	precandidate_has_triggerreference;

	//-------- precandidate list --------
	
	/** The precandidate list contains precandidates for a given processable element. */
	public static final OAVObjectType	precandidatelist_type;
	
	/** The precandidate list has a processable element. */
	public static final OAVAttributeType	precandidatelist_has_processableelement;

	/** The precandidate list has precandidates. */
	public static final OAVAttributeType	precandidatelist_has_precandidates;
	
	//-------- assigntosource list --------
	
	/** The abstractsource is the concrete element (source) for an abstract element. */
	public static final OAVObjectType	abstractsource_type;
	
	/** The abstractsource has the abstract element. */
	public static final OAVAttributeType	abstractsource_has_abstract;

	/** The abstractsource has the source scope. */
	public static final OAVAttributeType	abstractsource_has_rcapa;

	/** The abstractsource has the source (original) element. */
	public static final OAVAttributeType	abstractsource_has_source;
	
	//-------- event --------
	
	/** The event type. */
//	public static OAVObjectType event_type;
	
	//-------- message event --------
	
	/** The message event type. */
	public static final OAVObjectType messageevent_type;
	
	/** The message event has a native message. */
	public static final OAVAttributeType messageevent_has_nativemessage;
	
	/** The message event has an explicit id. */
//	public static OAVAttributeType messageevent_has_id;
	
	/** The message event has an original message event (if it is a reply). */
	// Hack!!! Currently already available in processable element to unify rules.
	public static final OAVAttributeType messageevent_has_original;
	
	/** The message event has a send future (hack???). */
	public static final OAVAttributeType messageevent_has_sendfuture;
	
	/** The message event has a send future (hack???). */
	public static final OAVAttributeType messageevent_has_codecids;
	
	//-------- internal event --------
	
	/** The internal event type. */
	public static final OAVObjectType internalevent_type;
	
//	//-------- expression --------
//	
//	/** The expressions type. */
//	public static final OAVObjectType expression_type;
	
//	//-------- property --------
//	
//	/** The properties type. */
//	public static final OAVObjectType property_type;
	
	//-------- wait abstraction --------

	/** The wait abstraction type. */
	public static final OAVObjectType waitabstraction_type;

	/** The wait abstraction has goals to wait for. */
	public static final OAVAttributeType waitabstraction_has_goals;
	
	/** The wait abstraction has messageevent to wait for a reply. */
	public static final OAVAttributeType waitabstraction_has_messageevents;

	/** The wait abstraction has goal types finisheds to wait for. */
	public static final OAVAttributeType waitabstraction_has_goalfinisheds;
	
	/** The wait abstraction has message event types to wait for. */
	public static final OAVAttributeType waitabstraction_has_messageeventtypes;
	
	/** The wait abstraction has internal event types to wait for. */
	public static final OAVAttributeType waitabstraction_has_internaleventtypes;
	
	/** The wait abstraction has belief(set) types to wait for fact changes. */
	public static final OAVAttributeType waitabstraction_has_factchangeds;
	
	/** The wait abstraction has beliefset types to wait for added facts. */
	public static final OAVAttributeType waitabstraction_has_factaddeds;
	
	/** The wait abstraction has beliefset types to wait for removed fact. */
	public static final OAVAttributeType waitabstraction_has_factremoveds;
	
	/** The wait abstraction has conditions to wait for. */
	public static final OAVAttributeType waitabstraction_has_conditiontypes;
	
	/** The wait abstraction has external conditions to wait for. */
	public static final OAVAttributeType waitabstraction_has_externalconditions;
	
	//-------- change events --------
	
	/** The changeevent type. */
	public static final OAVObjectType changeevent_type;

	/** The changeevent has an element. */
	public static final OAVAttributeType changeevent_has_element;	
	
	/** The changeevent has an element scope. */
	public static final OAVAttributeType changeevent_has_scope;	
	
	/** The changeevent has a type. */
	public static final OAVAttributeType changeevent_has_type;	
	
	/** The changeevent has a value. */
	public static final OAVAttributeType changeevent_has_value;	
	
	//-------- listeners --------
	
	/** The listener entry type. */
	public static final OAVObjectType listenerentry_type;

	/** The listener has a listener object. */
	public static final OAVAttributeType listenerentry_has_listener;
	
	/** The listener has a scope. */
	public static final OAVAttributeType listenerentry_has_scope;
	
	/** The listener has relevants (runtime and/or modelelements). */
	public static final OAVAttributeType listenerentry_has_relevants;
	
//	/** The listener has a modelelement. */
//	public static OAVAttributeType listenerentry_has_modelelement;
//
//	/** The listener has a runtimeelement. */
//	public static OAVAttributeType listenerentry_has_runtimeelement;
	
	
	/** The runtime element to model element mapping (OAV type -> OAV type). */
	public static final Map modelmap;
	
	static
	{
		bdi_rt_model = new OAVTypeModel("bdi_rt_model");
		bdi_rt_model.addTypeModel(OAVBDIMetaModel.bdimm_type_model);
		
		// java types
		java_imessageadapter_type = createJavaType(IMessageAdapter.class, OAVJavaType.KIND_OBJECT);
		java_propertychangelistener_type	= createJavaType(PropertyChangeListener.class, OAVJavaType.KIND_VALUE);
		java_timer_type	= createJavaType(ITimer.class, OAVJavaType.KIND_VALUE);
		java_future_type	= createJavaType(Future.class, OAVJavaType.KIND_OBJECT);
//		java_componentlistener_type = createJavaType(IComponentListener.class, OAVJavaType.KIND_OBJECT);
		java_externalcondition_type	= createJavaType(IExternalCondition.class, OAVJavaType.KIND_BEAN);
//		java_serviceprovider_type	= createJavaType(IServiceProvider.class, OAVJavaType.KIND_OBJECT);
		//java_planexecutor_type = createJavaType(IPlanExecutor.class);
		//java_waitabstraction_type = bdirt_model_type.createJavaType(WaitAbstraction.class);
		java_runnable_type = createJavaType(Runnable.class, OAVJavaType.KIND_OBJECT);
		java_resultcommand_type = createJavaType(IResultCommand.class, OAVJavaType.KIND_OBJECT);
		
		// object types (done first, before attributes are created).
		element_type = bdi_rt_model.createType("element", OAVBDIMetaModel.object_type);
		typedelement_type = bdi_rt_model.createType("typedelement", element_type); //???
		parameter_type = bdi_rt_model.createType("parameter", typedelement_type); // HACK!!! Has no model.
		parameterset_type = bdi_rt_model.createType("parameterset", typedelement_type); // HACK!!! Has no model.
		parameterelement_type = bdi_rt_model.createType("parameter_element", element_type);
		processableelement_type = bdi_rt_model.createType("processable_element", parameterelement_type);
//		event_type = bdi_rt_model.createType("event", processableelement_type);
		messageevent_type = bdi_rt_model.createType("messageevent", processableelement_type);
		internalevent_type = bdi_rt_model.createType("internalevent", processableelement_type);
		goal_type = bdi_rt_model.createType("goal", processableelement_type);
		plan_type = bdi_rt_model.createType("plan", parameterelement_type);
		belief_type = bdi_rt_model.createType("belief", typedelement_type);
		beliefset_type = bdi_rt_model.createType("beliefset", typedelement_type);
		apl_type = bdi_rt_model.createType("apl");
		mplancandidate_type = bdi_rt_model.createType("mplancandidate", OAVBDIMetaModel.object_type);
		plancandidate_type = bdi_rt_model.createType("plancandidate", OAVBDIMetaModel.object_type);
		waitqueuecandidate_type = bdi_rt_model.createType("waitqueuecandidate", OAVBDIMetaModel.object_type);
		precandidate_type = bdi_rt_model.createType("precandidate", OAVBDIMetaModel.object_type);
		precandidatelist_type = bdi_rt_model.createType("precandidatelist", OAVBDIMetaModel.object_type);
		abstractsource_type = bdi_rt_model.createType("abstractsource", OAVBDIMetaModel.object_type);
		capabilityreference_type = bdi_rt_model.createType("capabilityreference");
		capability_type = bdi_rt_model.createType("capability", element_type);
		agent_type = bdi_rt_model.createType("agent", capability_type);
		changeevent_type = bdi_rt_model.createType("changeevent", OAVBDIMetaModel.object_type);
		listenerentry_type = bdi_rt_model.createType("listenerentry");
		waitabstraction_type = bdi_rt_model.createType("waitabstraction");
		externalaccess_type = bdi_rt_model.createType("externalaccess");
		
		// parameter
		parameter_has_name = parameter_type.createAttributeType("parameter_has_name", OAVJavaType.java_string_type);
		parameter_has_value = parameter_type.createAttributeType("parameter_has_value", OAVJavaType.java_object_type);
		parameter_has_type = parameter_type.createAttributeType("parameter_has_type", OAVJavaType.java_class_type);
		
		// parameter set
		parameterset_has_name = parameterset_type.createAttributeType("parameterset_has_name", OAVJavaType.java_string_type);
		parameterset_has_values = parameterset_type.createAttributeType("parameterset_has_values", 
			OAVJavaType.java_object_type, OAVAttributeType.LIST, null);
		parameterset_has_type = parameterset_type.createAttributeType("parameterset_has_type", OAVJavaType.java_class_type);

		// element
		element_has_model = element_type.createAttributeType("element_has_model", OAVBDIMetaModel.modelelement_type);
		
		// typed element
		typedelement_has_propertychangelistener = typedelement_type.createAttributeType("typedelement_has_propertychangelistener", java_propertychangelistener_type);
		typedelement_has_timer = typedelement_type.createAttributeType("typedelement_has_timer", java_timer_type);

		
		// parameter element
		parameterelement_has_parameters = parameterelement_type.createAttributeType("parameterelement_has_parameters",
			parameter_type, OAVAttributeType.ORDEREDMAP, null, parameter_has_name);
		parameterelement_has_parametersets = parameterelement_type.createAttributeType("parameterelement_has_parametersets",
			parameterset_type, OAVAttributeType.ORDEREDMAP, null, parameterset_has_name);
		
		// processable element
		processableelement_has_apl = processableelement_type.createAttributeType("processableelement_has_apl", apl_type);
		processableelement_has_state = processableelement_type.createAttributeType("processableelement_has_state", OAVJavaType.java_string_type);
		
		// belief
		belief_has_fact = belief_type.createAttributeType("belief_has_fact", OAVJavaType.java_object_type);
//		belief_has_listeners = belief_type.createAttributeType("belief_has_listeners", listenerentry_type, OAVAttributeType.LIST);

		
		// belief set
		beliefset_has_facts = beliefset_type.createAttributeType("beliefset_has_facts", OAVJavaType.java_object_type, OAVAttributeType.LIST);
		
		// goal
//		goal_has_parent = goal_type.createAttributeType("goal_has_parent", goal_type);
		goal_has_finishedplans = goal_type.createAttributeType("goal_has_finishedplans", plan_type, OAVAttributeType.SET);
		//goal_has_context = goal_type.createAttributeType("goal_has_context", );
		//goal_has_drop = goal_type.createAttributeType("goal_has_drop", );
		goal_has_lifecyclestate = goal_type.createAttributeType("goal_has_lifecyclestate", OAVJavaType.java_string_type);
		goal_has_processingstate = goal_type.createAttributeType("goal_has_processingstate", OAVJavaType.java_string_type);
		goal_has_protected = goal_type.createAttributeType("goal_has_protected", OAVJavaType.java_boolean_type, OAVAttributeType.NONE, Boolean.FALSE);
		goal_has_parentplan = goal_type.createAttributeType("goal_has_parentplan", plan_type, OAVAttributeType.NONE);
		goal_has_exception = goal_type.createAttributeType("goal_has_exception", OAVJavaType.java_exception_type);
		goal_has_triedmplans = goal_type.createAttributeType("goal_has_triedmplans", OAVBDIMetaModel.object_type, OAVAttributeType.LIST);
		goal_has_retrytimer = goal_type.createAttributeType("goal_has_retrytimer", java_timer_type);
		goal_has_recurtimer = goal_type.createAttributeType("goal_has_recurtimer", java_timer_type);
		goal_has_finisheddispatchedplans = goal_type.createAttributeType("goal_has_finisheddispatchedplans", plan_type, OAVAttributeType.SET);
		goal_has_inhibitors = goal_type.createAttributeType("goal_has_inhibitors", goal_type, OAVAttributeType.SET);
		
		// apl
		apl_has_metagoal = apl_type.createAttributeType("apl_has_metagoal", OAVBDIRuntimeModel.goal_type);
		apl_has_plancandidates = apl_type.createAttributeType("apl_has_plancandidates", OAVBDIRuntimeModel.mplancandidate_type, OAVAttributeType.LIST);
		apl_has_planinstancecandidates = apl_type.createAttributeType("apl_has_planinstancecandidates", OAVBDIRuntimeModel.plancandidate_type, OAVAttributeType.LIST);
		apl_has_waitqueuecandidates = apl_type.createAttributeType("apl_has_waitqueuecandidates", OAVBDIRuntimeModel.waitqueuecandidate_type, OAVAttributeType.LIST);
		
		// mplancandidate
		mplancandidate_has_mplan	= mplancandidate_type.createAttributeType("mplancandidate_has_mplan", OAVBDIMetaModel.plan_type); 
		mplancandidate_has_bindings	= mplancandidate_type.createAttributeType("mplancandidate_has_bindings", OAVBDIRuntimeModel.parameter_type, OAVAttributeType.LIST); 
		mplancandidate_has_plan	= mplancandidate_type.createAttributeType("mplancandidate_has_plan", OAVBDIRuntimeModel.plan_type); 
		mplancandidate_has_rcapa	= mplancandidate_type.createAttributeType("mplancandidate_has_rcapa", OAVBDIRuntimeModel.capability_type); 

		// plancandidate
		plancandidate_has_plan	= plancandidate_type.createAttributeType("plancandidate_has_plan", OAVBDIRuntimeModel.plan_type); 
		plancandidate_has_rcapa	= plancandidate_type.createAttributeType("plancandidate_has_rcapa", OAVBDIRuntimeModel.capability_type); 

		// waitqueuecandidate
		waitqueuecandidate_has_plan	= waitqueuecandidate_type.createAttributeType("waitqueuecandidate_has_plan", OAVBDIRuntimeModel.plan_type); 
		waitqueuecandidate_has_rcapa = waitqueuecandidate_type.createAttributeType("waitqueuecandidate_has_rcapa", OAVBDIRuntimeModel.capability_type); 
		
		// precandidate
		precandidate_has_mplan	= precandidate_type.createAttributeType("precandidate_has_mplan", OAVBDIMetaModel.plan_type); 
		precandidate_has_capability	= precandidate_type.createAttributeType("precandidate_has_capability", OAVBDIRuntimeModel.capability_type); 
		precandidate_has_triggerreference	= precandidate_type.createAttributeType("precandidate_has_triggerreference", OAVBDIMetaModel.triggerreference_type); 

		// precandidate
		precandidatelist_has_processableelement	= precandidatelist_type.createAttributeType("precandidatelist_has_processableelement", OAVBDIMetaModel.processableelement_type); 
		precandidatelist_has_precandidates	= precandidatelist_type.createAttributeType("precandidatelist_has_precandidates", OAVBDIRuntimeModel.precandidate_type, OAVAttributeType.LIST); 

		// abstractsource
		abstractsource_has_abstract	= abstractsource_type.createAttributeType("abstractsource_has_abstract", OAVBDIMetaModel.elementreference_type);
		abstractsource_has_rcapa	= abstractsource_type.createAttributeType("abstractsource_has_rcapa", OAVBDIRuntimeModel.capability_type);
		abstractsource_has_source	= abstractsource_type.createAttributeType("abstractsource_has_source", OAVBDIMetaModel.referenceableelement_type);
		
		// plan
		plan_has_body = plan_type.createAttributeType("plan_has_body", OAVJavaType.java_object_type);
		plan_has_step = plan_type.createAttributeType("plan_has_step", OAVJavaType.java_integer_type, OAVAttributeType.NONE, Integer.valueOf(0));
		//plan_has_executor = plan_type.createAttributeType("plan_has_executor", java_planexecutor_type);
//		plan_has_event = plan_type.createAttributeType("plan_has_event", event_type);
		plan_has_reason = plan_type.createAttributeType("plan_has_reason", OAVBDIMetaModel.object_type);
		plan_has_dispatchedelement = plan_type.createAttributeType("plan_has_dispatchedelement", OAVBDIMetaModel.object_type);
//		plan_has_rootgoal = plan_type.createAttributeType("plan_has_rootgoal", goal_type);
		//plan_type.createAttributeType("plan_has_contextcondition", );
		plan_has_subgoals = plan_type.createAttributeType("plan_has_subgoals", goal_type, OAVAttributeType.SET);
		plan_has_waitabstraction = plan_type.createAttributeType("plan_has_waitabstraction", waitabstraction_type);
		plan_has_waitqueuewa = plan_type.createAttributeType("plan_has_waitqueuewa", waitabstraction_type);
		plan_has_waitqueueelements = plan_type.createAttributeType("plan_has_waitqueueelements", OAVBDIMetaModel.object_type, OAVAttributeType.LIST);
		plan_has_exception = plan_type.createAttributeType("plan_has_exception", OAVJavaType.java_exception_type);
		plan_has_lifecyclestate = plan_type.createAttributeType("plan_has_lifecyclestate", OAVJavaType.java_string_type);
		plan_has_processingstate = plan_type.createAttributeType("plan_has_processingstate", OAVJavaType.java_string_type);
		plan_has_timer = plan_type.createAttributeType("plan_has_timer", java_timer_type);
//		plan_has_uservariables = plan_type.createAttributeType("plan_has_uservariables", element_type, OAVAttributeType.LIST);
		plan_has_plancandidate = plan_type.createAttributeType("plan_has_plancandidate", mplancandidate_type);		
		plan_has_planinstancecandidate = plan_type.createAttributeType("plan_has_planinstancecandidate", plan_type);		
		plan_has_waitqueuecandidate = plan_type.createAttributeType("plan_has_waitqueuecandidate", plan_type);		
		
		// event
		
		// message event
		// todo: necessary?
		messageevent_has_nativemessage = messageevent_type.createAttributeType("messageevent_has_nativemessage", OAVJavaType.java_object_type);
//		messageevent_has_id = messageevent_type.createAttributeType("messageevent_has_id", OAVJavaType.java_integer_type);
		// Hack!!! Add to processable element as required by createBuildRPlanAPLRules.
		messageevent_has_original = processableelement_type.createAttributeType("messageevent_has_original", messageevent_type);
		messageevent_has_sendfuture = messageevent_type.createAttributeType("messageevent_has_sendfuture", OAVJavaType.java_object_type);
		messageevent_has_codecids = messageevent_type.createAttributeType("messageevent_has_codecids", OAVJavaType.java_object_type);
		
		// capability reference
		capabilityreference_has_name = capabilityreference_type.createAttributeType("capabilityreference_has_name", OAVJavaType.java_string_type);
		capabilityreference_has_capability = capabilityreference_type.createAttributeType("capabilityreference_has_capability", capability_type);

		// listener
		listenerentry_has_listener = listenerentry_type.createAttributeType("listenerentry_has_listener", OAVJavaType.java_object_type);
		listenerentry_has_scope = listenerentry_type.createAttributeType("listenerentry_has_scope", OAVBDIRuntimeModel.capability_type);
//		listenerentry_has_modelelement = listenerentry_type.createAttributeType("listenerentry_has_modelelement", OAVBDIMetaModel.modelelement_type);
//		listenerentry_has_runtimeelement = listenerentry_type.createAttributeType("listenerentry_has_runtimeelement", element_type);
		listenerentry_has_relevants = listenerentry_type.createAttributeType("listenerentry_has_relevants", OAVBDIMetaModel.object_type, OAVAttributeType.SET);
		
		// capability
		// Todo: use ordered sets for lists that shouldn't have duplicates (improves contains checks in rules!).
		capability_has_subcapabilities = capability_type.createAttributeType("capability_has_subcapabilities", capabilityreference_type, OAVAttributeType.MAP, null, capabilityreference_has_name);
		capability_has_beliefs = capability_type.createAttributeType("capability_has_beliefs", belief_type, OAVAttributeType.MAP, null, element_has_model);
		capability_has_beliefsets = capability_type.createAttributeType("capability_has_beliefsets", beliefset_type, OAVAttributeType.MAP, null, element_has_model);
		capability_has_goals = capability_type.createAttributeType("capability_has_goals", goal_type, OAVAttributeType.LIST);
		capability_has_plans = capability_type.createAttributeType("capability_has_plans", plan_type, OAVAttributeType.LIST);
		capability_has_messageevents = capability_type.createAttributeType("capability_has_messageevents", messageevent_type, OAVAttributeType.LIST);
		capability_has_internalevents = capability_type.createAttributeType("capability_has_internalevents", internalevent_type, OAVAttributeType.LIST);
//		capability_has_expressions = capability_type.createAttributeType("capability_has_expressions", expression_type, OAVAttributeType.LIST);
		capability_has_configuration = capability_type.createAttributeType("capability_has_configuration", OAVJavaType.java_string_type);
		capability_has_listeners = capability_type.createAttributeType("capability_has_listeners", listenerentry_type, OAVAttributeType.MAP, null, listenerentry_has_listener);
		capability_has_sentmessageevents = capability_type.createAttributeType("capability_has_sentmessageevents", messageevent_type, OAVAttributeType.LIST);
		capability_has_outbox = capability_type.createAttributeType("capability_has_outbox", messageevent_type, OAVAttributeType.LIST);
		capability_has_externalaccesses = capability_type.createAttributeType("capability_has_externalaccesses", externalaccess_type, OAVAttributeType.LIST);
		capability_has_precandidates = capability_type.createAttributeType("capability_has_precandidates", precandidatelist_type, OAVAttributeType.MAP, null, precandidatelist_has_processableelement);
		capability_has_abstractsources = capability_type.createAttributeType("capability_has_abstractsources", abstractsource_type, OAVAttributeType.MAP, null, abstractsource_has_abstract);
//		capability_has_properties = capability_type.createAttributeType("capability_has_properties", parameter_type, OAVAttributeType.MAP, null, parameter_has_name);
		
		// agent
//		agent_has_name = agent_type.createAttributeType("agent_has_name", OAVJavaType.java_string_type);
//		agent_has_localname = agent_type.createAttributeType("agent_has_localname", OAVJavaType.java_string_type);
		agent_has_state = agent_type.createAttributeType("agent_has_state", OAVJavaType.java_string_type, OAVAttributeType.NONE);
		// todo: use IMessageAdapter?
		agent_has_inbox = agent_type.createAttributeType("agent_has_inbox", java_imessageadapter_type, OAVAttributeType.LIST, null);
		agent_has_actions = agent_type.createAttributeType("agent_has_actions", OAVJavaType.java_object_type, OAVAttributeType.LIST, null);
//		agent_has_serviceprovider = agent_type.createAttributeType("agent_has_serviceprovider", java_serviceprovider_type, OAVAttributeType.NONE);

		java_map_type = createJavaType(Map.class, OAVJavaType.KIND_OBJECT);
		
		agent_has_arguments = agent_type.createAttributeType("agent_has_arguments", java_map_type);
//		agent_has_results = agent_type.createAttributeType("agent_has_results", java_map_type);
		agent_has_initparents = agent_type.createAttributeType("agent_has_initparents", java_map_type);
		agent_has_timer = agent_type.createAttributeType("agent_has_timer", java_timer_type);
		agent_has_killfuture = agent_type.createAttributeType("agent_has_killfuture", java_future_type);
//		agent_has_componentlisteners = agent_type.createAttributeType("agent_has_componentlisteners", java_componentlistener_type, OAVAttributeType.LIST);
		agent_has_changeevents = agent_type.createAttributeType("agent_has_changeevents", changeevent_type, OAVAttributeType.LIST);
//		agent_has_bindings = agent_type.createAttributeType("agent_has_bindings", OAVJavaType.java_object_type);
//		agent_has_eventprocessing = agent_type.createAttributeType("agent_has_eventprocessing", processableelement_type);

		// changeevents
		changeevent_has_element = changeevent_type.createAttributeType("changeevent_has_element", element_type);
		changeevent_has_scope = changeevent_type.createAttributeType("changeevent_has_scope", capability_type);
		changeevent_has_type	= changeevent_type.createAttributeType("changeevent_has_type", OAVJavaType.java_string_type);
		changeevent_has_value	= changeevent_type.createAttributeType("changeevent_has_value", OAVJavaType.java_object_type);
		
		// wait abstraction
		waitabstraction_has_goals = waitabstraction_type.createAttributeType("waitabstraction_has_goals", OAVBDIRuntimeModel.goal_type, OAVAttributeType.SET);
		waitabstraction_has_messageevents = waitabstraction_type.createAttributeType("waitabstraction_has_messageevents", OAVBDIRuntimeModel.messageevent_type, OAVAttributeType.SET);
		waitabstraction_has_goalfinisheds = waitabstraction_type.createAttributeType("waitabstraction_has_goalfinisheds", OAVBDIMetaModel.goal_type, OAVAttributeType.SET);
		waitabstraction_has_messageeventtypes = waitabstraction_type.createAttributeType("waitabstraction_has_messageeventtypes", OAVBDIMetaModel.messageevent_type, OAVAttributeType.SET);
		waitabstraction_has_internaleventtypes = waitabstraction_type.createAttributeType("waitabstraction_has_internaleventtypes", OAVBDIMetaModel.internalevent_type, OAVAttributeType.SET);
		waitabstraction_has_factchangeds = waitabstraction_type.createAttributeType("waitabstraction_has_factchangeds", OAVBDIRuntimeModel.typedelement_type, OAVAttributeType.SET);
		waitabstraction_has_factaddeds = waitabstraction_type.createAttributeType("waitabstraction_has_factaddeds", OAVBDIRuntimeModel.beliefset_type, OAVAttributeType.SET);
		waitabstraction_has_factremoveds = waitabstraction_type.createAttributeType("waitabstraction_has_factremoveds", OAVBDIRuntimeModel.beliefset_type, OAVAttributeType.SET);
		waitabstraction_has_conditiontypes = waitabstraction_type.createAttributeType("waitabstraction_has_conditions", OAVBDIMetaModel.condition_type, OAVAttributeType.SET);
		waitabstraction_has_externalconditions = waitabstraction_type.createAttributeType("waitabstraction_has_externalconditions", java_externalcondition_type, OAVAttributeType.SET);
	
		// external access
		externalaccess_has_dispatchedelement = externalaccess_type.createAttributeType("externalaccess_has_dispatchedelement", OAVBDIMetaModel.object_type);
		externalaccess_has_timer = externalaccess_type.createAttributeType("externalaccess_has_timer", java_timer_type);
		externalaccess_has_waitabstraction = externalaccess_type.createAttributeType("externalaccess_has_waitabstraction", waitabstraction_type);
		externalaccess_has_wakeupaction = externalaccess_type.createAttributeType("externalaccess_has_wakeupaction", OAVJavaType.java_object_type);

		
		// Mapping from runtime element to their models.
		// Used for optimizing rule loader to exclude unused rules.
		// Todo: synchronized Map required for concurrent read access?
		modelmap	= new HashMap();
		modelmap.put(element_type, OAVBDIMetaModel.modelelement_type);
		modelmap.put(typedelement_type, OAVBDIMetaModel.typedelement_type);
		modelmap.put(parameter_type, OAVBDIMetaModel.parameter_type);
		modelmap.put(parameterset_type, OAVBDIMetaModel.parameterset_type);
		modelmap.put(parameterelement_type, OAVBDIMetaModel.parameterelement_type);
		modelmap.put(processableelement_type, OAVBDIMetaModel.processableelement_type);
		modelmap.put(messageevent_type, OAVBDIMetaModel.messageevent_type);
		modelmap.put(internalevent_type, OAVBDIMetaModel.internalevent_type);
		modelmap.put(goal_type, OAVBDIMetaModel.goal_type);
		modelmap.put(plan_type, OAVBDIMetaModel.plan_type);
		modelmap.put(belief_type, OAVBDIMetaModel.belief_type);
		modelmap.put(beliefset_type, OAVBDIMetaModel.beliefset_type);
		modelmap.put(goal_type, OAVBDIMetaModel.goal_type);
		modelmap.put(capabilityreference_type, OAVBDIMetaModel.capabilityref_type);
		modelmap.put(capability_type, OAVBDIMetaModel.capability_type);
		modelmap.put(agent_type, OAVBDIMetaModel.agent_type);
		modelmap.put(capabilityreference_type, OAVBDIMetaModel.capabilityref_type);
	}

	/**
	 *  Create a java type if it does not already exist.
	 *  Required because some (user) Java types are created
	 *  on the fly when loading agent models
	 *  (e.g. Map in TranslationC1).
	 */
	protected static OAVJavaType	createJavaType(Class clazz, String kind)
	{
		OAVJavaType	ret;
		if(bdi_rt_model.contains(new OAVJavaType(clazz, kind, bdi_rt_model)))
		{
			ret	= bdi_rt_model.getJavaType(clazz);
		}
		else
		{
			ret	= bdi_rt_model.createJavaType(clazz, kind);
		}
		
		return ret;
	}
}
