package jadex.bdi.interpreter;

import jadex.bridge.MessageType;
import jadex.bridge.MessageType.ParameterSpecification;
import jadex.commons.collection.MultiCollection;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.rules.parser.conditions.ParserHelper;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.io.xml.BasicValueConverter;
import jadex.rules.state.io.xml.IOAVXMLMapping;
import jadex.rules.state.io.xml.IValueConverter;
import jadex.rules.state.io.xml.OAVXMLHelper;
import jadex.rules.state.io.xml.OAVXMLMapping;
import jadex.rules.state.io.xml.StackElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  OAV BDI-meta model.
 */
public class OAVBDIMetaModel
{	
	//-------- constants --------

	/** Don't export an element. */
	public static final String EXPORTED_FALSE = "false";

	/** Fully export an element. */
	public static final String EXPORTED_TRUE = "true";

	/** Shielded export of an element. */
	public static final String EXPORTED_SHIELDED = "shielded";

	/** The "when_active" inhibition type. */
	public static final String	INHIBITS_WHEN_ACTIVE = "when_active";

	/** The "when_in_process" inhibition type. */
	public static final String	INHIBITS_WHEN_IN_PROCESS = "when_in_process";

	/** Never exclude plan candidates from apl. */
	public static final String EXCLUDE_NEVER = "never";

	/** Exclude tried plan candidates from apl. */ 
	public static final String EXCLUDE_WHEN_TRIED = "when_tried";
	
	/** Exclude failed plan candidates from apl. */
	public static final String EXCLUDE_WHEN_FAILED = "when_failed";

	/** Exclude succeeded plan candidates from apl. */
	public static final String EXCLUDE_WHEN_SUCCEEDED = "when_succeeded";

	/** Describing a message event that can be received. */
	public static final String	MESSAGE_DIRECTION_RECEIVE = "receive";

	/** Describing a message event that can be send. */
	public static final String	MESSAGE_DIRECTION_SEND = "send";

	/** Describing a message event that can be send and received. */
	public static final String	MESSAGE_DIRECTION_SEND_RECEIVE = "send_receive";

	/** Fixed values are not allowed to be changed and are used for matching messages. */
	public static final String PARAMETER_DIRECTION_FIXED = "fixed";

	/** In parameters for parameter elements. */
	public static final String PARAMETER_DIRECTION_IN = "in";

	/** Out parameters for parameter elements. */
	public static final String PARAMETER_DIRECTION_OUT = "out";

	/** Inout parameters for parameter elements. */
	public static final String PARAMETER_DIRECTION_INOUT = "inout";

	/** The "static" evaluation mode (value evaluated once on init). */
	public static final String EVALUATIONMODE_STATIC = "static";

	/** The "push" evaluation mode (value-updates triggered by condition). */
	public static final String EVALUATIONMODE_PUSH = "push";

	/** The "pull" evaluation mode (value re-evaluated on every access or evaluated and stored at fixed intervals). */
	public static final String EVALUATIONMODE_PULL = "pull";

	//-------- type model --------

	/** The type model. */
	public static OAVTypeModel bdimm_type_model;
	
	//-------- java types --------
	
	/** The parsed expression java type. */
	public static OAVJavaType	java_parsedexpression_type;
	
	/** The message event type java type. */
	public static OAVJavaType	java_messagetype_type;
	
	//-------- object --------
	
	/** The object type (dummy root type). */
	public static OAVObjectType object_type;
	
	//-------- abstract model element --------
	
	/** The model element type. */
	public static OAVObjectType modelelement_type;
	
	/** Model element has name attribute. */
	public static OAVAttributeType modelelement_has_name;
	
	/** Model element has description attribute. */
	public static OAVAttributeType modelelement_has_description;
	
	//-------- referenceable element --------
	
	/** The referenceable element type. */
	public static OAVObjectType referenceableelement_type;
	
	/** Referenceable element has exported attribute. */
	public static OAVAttributeType referenceableelement_has_exported;
//	
//	/** Referenceable element has assignto attribute. */
//	public static OAVAttributeType referenceableelement_has_assignto;
	
	//-------- element reference --------
	
	/** The element reference type. */
	public static OAVObjectType elementreference_type;
	
	/** Element reference has concrete attribute. */
	public static OAVAttributeType elementreference_has_concrete;
//	
//	/** Element reference has abstract attribute. */
//	public static OAVAttributeType elementreference_has_abstract;
//	
//	/** Element reference has (abstract) required attribute. */
//	public static OAVAttributeType elementreference_has_required;
	
	//-------- abstract parameter element --------
	
	/** The parameter element type. */
	public static OAVObjectType parameterelement_type;
	
	/** Parameter element has parameters attribute. */
	public static OAVAttributeType parameterelement_has_parameters;
	
	/** Parameter element has parametersets attribute. */
	public static OAVAttributeType parameterelement_has_parametersets;
	
	//-------- abstract processable element --------
	
	/** The (abstract) processable element type. */
	public static OAVObjectType processableelement_type;
	
	/** Processable element has posttoall attribute. */
	public static OAVAttributeType processableelement_has_posttoall;
	
	/** Processable element has random selection attribute. */
	public static OAVAttributeType processableelement_has_randomselection;
	
	//-------- abstract parameter element reference --------
	
//	/** The parameter element reference type. */
//	public static OAVObjectType parameterelementreference_type;
//	
//	/** Parameter element reference has parameter references attribute. */
//	public static OAVAttributeType parameterelementreference_has_parameterrefs;
//	
//	/** Parameter element reference has parameterset references attribute. */
//	public static OAVAttributeType parameterelementreference_has_parametersetrefs;
	
	//-------- abstract typed element --------
	
	/** The typed element type. */
	public static OAVObjectType typedelement_type;
	
	/** Typed element has class attribute. */
	public static OAVAttributeType typedelement_has_class;
	
	/** Typed element has classname attribute. */
	// Required for XML loading. Todo: remove?
	public static OAVAttributeType typedelement_has_classname;
	
	/** Typed element has update rate attribute. */
	public static OAVAttributeType typedelement_has_updaterate;
	
	/** Typed element has transient attribute. */
	public static OAVAttributeType typedelement_has_transient;
	
	/** Typed element has dynamic attribute. */
	public static OAVAttributeType typedelement_has_evaluationmode;

	//-------- typed element reference --------
	
//	/** The typed element reference type. */
//	public static OAVObjectType typedelementreference_type;
//	
//	/** Typed element reference has class attribute. */
//	public static OAVAttributeType typedelementreference_has_class;
	
	//-------- abstract parameter element --------
	
	/** The parameter type. */
	public static OAVObjectType parameter_type;
	
	/** Parameter has value attribute. */
	public static OAVAttributeType parameter_has_value;
	
	/** Parameter has direction attribute. */
	public static OAVAttributeType parameter_has_direction;
	
	/** Parameter has optional attribute. */
	public static OAVAttributeType parameter_has_optional;
	
	/** Parameter has binding options attribute. */
	public static OAVAttributeType parameter_has_bindingoptions;
	
	//-------- abstract parameterset element --------
	
	/** The parameter set element type. */
	public static OAVObjectType parameterset_type;
	
	/** Parameter set has values attribute. */
	public static OAVAttributeType parameterset_has_values;
	
	/** Parameter set has values expression attribute. */
	public static OAVAttributeType parameterset_has_valuesexpression;
		
	/** Parameter set has direction attribute. */
	public static OAVAttributeType parameterset_has_direction;
	
	/** Parameter set has optional attribute. */
	public static OAVAttributeType parameterset_has_optional;

	//-------- abstract goal --------
	
	/** The (abstract) goal type. */
	public static OAVObjectType goal_type;
	
	/** Goal has creation condition attribute. */
	public static OAVAttributeType goal_has_creationcondition;
	
	/** Goal has context condition attribute. */
	public static OAVAttributeType goal_has_contextcondition;
	
	/** Goal has drop condition attribute. */
	public static OAVAttributeType goal_has_dropcondition;
	
	/** Goal has retry attribute. */
	public static OAVAttributeType goal_has_retry;
	
	/** Goal has retry delay attribute. */
	public static OAVAttributeType goal_has_retrydelay;
	
	/** Goal has recur attribute. */
	public static OAVAttributeType goal_has_recur;
	
	/** Goal has recur delay attribute. */
	public static OAVAttributeType goal_has_recurdelay;
	
	/** Goal has recur condition attribute. */
	public static OAVAttributeType goal_has_recurcondition;
	
	/** Goal has exclude attribute. */
	public static OAVAttributeType goal_has_exclude;
	
	/** Goal has rebuild attribute. */
	public static OAVAttributeType goal_has_rebuild;
	
	/** Goal has unique attribute. */
	public static OAVAttributeType goal_has_unique;
	
	/** Goal has excluded parameter (for unique) attribute. */
	public static OAVAttributeType goal_has_excludedparameter;
	
	/** Goal has inhibits attribute. */
	public static OAVAttributeType goal_has_inhibits;
	
	/** Goal has cardinality attribute. */
	public static OAVAttributeType goal_has_cardinality;
	
	//-------- goal reference --------
	
	/** The goal reference type. */
	public static OAVObjectType goalreference_type;
	
	/** Goalreference has inhibits attribute. */
//	public static OAVAttributeType goalreference_has_inhibits;
	
	/** Goalreference has cardinality attribute. */
//	public static OAVAttributeType goalreference_has_cardinality;
	
	//-------- inhibit --------
	
	/** The inhibit type. */
	public static OAVObjectType inhibits_type;
	
	/** Inhibit has ref attribute. */
	public static OAVAttributeType inhibits_has_ref;
	
	/** Inhibit has inhibits attribute. */
	public static OAVAttributeType inhibits_has_inhibit;
	
	//-------- expression --------
	
	/** The expression type. */
	public static OAVObjectType expression_type;

	/** Expression has language. */
	public static OAVAttributeType expression_has_language;

	/** Expression has content attribute. */
	public static OAVAttributeType expression_has_content;
	
	/** Expression has class attribute. */
//	public static OAVAttributeType expression_has_class;
	
	/** Expression has parameters attribute. */
	public static OAVAttributeType expression_has_parameters;
	
	/** Expression has relevants attribute. */
	public static OAVAttributeType expression_has_relevants;

	/** Expression has evaluationmode attribute. */
	public static OAVAttributeType expression_has_evaluationmode;

	//-------- expression parameter --------

	/** The expression parameter type. */
	public static OAVObjectType expressionparameter_type;
	
	/** Expression parameter has class attribute. */
	public static OAVAttributeType expressionparameter_has_class;
	
	//-------- expression relevants --------

	/** The expression relevant type. */
	public static OAVObjectType expressionrelevant_type;
	
	/** Expression relevant has reference attribute. */
	public static OAVAttributeType expressionrelevant_has_ref;
	
	/** Expression relevant has reference attribute. */
	public static OAVAttributeType expressionrelevant_has_eventtype;
	
	/** The relevant belief type. */
	public static OAVObjectType relevantbelief_type;
	
	/** The relevant beliefset type. */
	public static OAVObjectType relevantbeliefset_type;
	
	/** The relevant goal type. */
	public static OAVObjectType relevantgoal_type;
	
	/** The relevant parameter type. */
	public static OAVObjectType relevantparameter_type;
	
	/** The relevant parameterset type. */
	public static OAVObjectType relevantparameterset_type;
	
	//-------- condition --------
	
	/** The condition type. */
	public static OAVObjectType condition_type;
	
	/** Condition has trigger attribute. */
	public static OAVAttributeType condition_has_trigger;
	
	//-------- agent --------
	
	/** The agent (model) type. */
	public static OAVObjectType agent_type;

	/** Agent has propertyfile attribute. */
	public static OAVAttributeType agent_has_propertyfile;

	//-------- capability --------
	
	/** The capability type. */
	public static OAVObjectType capability_type;
	
	/** Capability has package attribute. */
	public static OAVAttributeType capability_has_package;
	
	/** Capability has abstract attribute. */
	public static OAVAttributeType capability_has_abstract;
	
	/** Capability has imports attribute. */
	public static OAVAttributeType capability_has_imports;
	
	/** Capability has capabilities attribute. */
	public static OAVAttributeType capability_has_capabilityrefs;
	
	/** Capability has beliefs attribute. */
	public static OAVAttributeType capability_has_beliefs;
	
	/** Capability has beliefsets attribute. */
	public static OAVAttributeType capability_has_beliefsets;
	
	/** Capability has belief references attribute. */
	public static OAVAttributeType capability_has_beliefrefs;
	
	/** Capability has beliefset references attribute. */
	public static OAVAttributeType capability_has_beliefsetrefs;
	
	/** Capability has goals attribute. */
	public static OAVAttributeType capability_has_goals;
	
	/** Capability has goal references attribute. */
	public static OAVAttributeType capability_has_goalrefs;
	
	/** Capability has plans attribute. */
	public static OAVAttributeType capability_has_plans;
	
	/** Capability has message events attribute. */
	public static OAVAttributeType capability_has_messageevents;
	
	/** Capability has internal events attribute. */
	public static OAVAttributeType capability_has_internalevents;
	
	/** Capability has message event references attribute. */
	public static OAVAttributeType capability_has_messageeventrefs;
	
	/** Capability has internal event references attribute. */
	public static OAVAttributeType capability_has_internaleventrefs;
	
	/** Capability has expressions attribute. */
	public static OAVAttributeType capability_has_expressions;
	
	/** Capability has conditions attribute. */
	public static OAVAttributeType capability_has_conditions;
				
	/** Capability has properties attribute. */
	public static OAVAttributeType capability_has_properties;
	
	/** Capability has default configuration attribute. */
	public static OAVAttributeType capability_has_defaultconfiguration;
	
	/** Capability has configurations attribute. */
	public static OAVAttributeType capability_has_configurations;
	
	//-------- capability --------
	
	/** The properties (file) type. */
	public static OAVObjectType properties_type;
	
	/** Properties has properties attribute. */
	public static OAVAttributeType properties_has_properties;

	//-------- capability reference --------
	
	/** The capability reference type. */
	public static OAVObjectType	capabilityref_type;
		
	/** Capability ref has file attribute. */
	public static OAVAttributeType capabilityref_has_file;
	
	/** Capability ref has capability attribute. */
	public static OAVAttributeType capabilityref_has_capability;
	
	//-------- belief --------
	
	/** The belief type. */
	public static OAVObjectType belief_type;
	
	/** Belief has default value attribute. */
	public static OAVAttributeType belief_has_fact;
	
	//-------- belief reference --------
	
	/** The belief reference type. */
	public static OAVObjectType beliefreference_type;
		
	//-------- belief set --------
	
	/** The belief set type. */
	public static OAVObjectType beliefset_type;
	
	/** Beliefset has dynamic attribute. */
//	public static OAVAttributeType beliefset_has_dynamic;
	
	/** Beliefset has default facts attribute. */
	public static OAVAttributeType beliefset_has_facts;
	
	/** Beliefset has default facts expression attribute. */
	public static OAVAttributeType beliefset_has_factsexpression;
	
	//-------- belief set reference --------
	
	/** The belief set reference type. */
	public static OAVObjectType beliefsetreference_type;

	//-------- perform goal --------
	
	/** The perform goal type. */
	public static OAVObjectType performgoal_type;
	
//	/** The perform goal ref type. */
//	public static OAVObjectType performgoalref_type;
	
	//-------- achieve goal --------
	
	/** The achieve goal type. */
	public static OAVObjectType achievegoal_type;
	
	/** Achieve goal has target condition attribute . */
	public static OAVAttributeType achievegoal_has_targetcondition;
	
	/** Achieve goal has failure condition attribute . */
	public static OAVAttributeType achievegoal_has_failurecondition;
	
//	/** The achieve goal ref type. */
//	public static OAVObjectType achievegoalref_type;
	
	//-------- query goal --------
	
	/** The query goal type. */
	public static OAVObjectType querygoal_type;
	
	/** Query goal has failure condition attribute . */
	public static OAVAttributeType querygoal_has_failurecondition;
	
//	/** The query goal ref type. */
//	public static OAVObjectType querygoalref_type;

	//-------- maintain goal --------

	/** The maintain goal type. */
	public static OAVObjectType maintaingoal_type;
	
	/** Maintain goal has target condition attribute . */
	public static OAVAttributeType maintaingoal_has_targetcondition;
	
	/** Maintain goal has maintain condition attribute . */
	public static OAVAttributeType maintaingoal_has_maintaincondition;
	
//	/** The maintain goal ref type. */
//	public static OAVObjectType maintaingoalref_type;

	//-------- meta goal --------

	/** The meta goal type. */
	public static OAVObjectType metagoal_type;
	
	/** Meta goal has trigger attribute. */
	public static OAVAttributeType metagoal_has_trigger;
	
//	/** The meta goal ref type. */
//	public static OAVObjectType metagoalref_type;

	//-------- plan --------
	
	/** The plan type. */
	public static OAVObjectType plan_type;
	
	/** Plan has priority attribute. */
	public static OAVAttributeType plan_has_priority;
	
	/** Plan has precondition attribute. */
	public static OAVAttributeType plan_has_precondition;
	
	/** Plan has contextcondition attribute. */
	public static OAVAttributeType plan_has_contextcondition;
	
	/** Plan has body . */
	public static OAVAttributeType plan_has_body;
	
	/** Plan has waitqueue attribute. */
	public static OAVAttributeType plan_has_waitqueue;
	
	/** Plan has trigger attribute. */
	public static OAVAttributeType plan_has_trigger;
	
	//-------- plan body --------
	
	/** The body type. */
	public static OAVObjectType body_type;
	
	/** Body has type attribute. */
	public static OAVAttributeType body_has_type;

	/** Body has class attribute. */
	public static OAVAttributeType body_has_class;

	/** Body has class name attribute. */
	public static OAVAttributeType body_has_classname;

//	/** Body has inline attribute. */
//	public static OAVAttributeType body_has_inline;
//
//	/** Body has passed attribute. */
//	public static OAVAttributeType body_has_passed;
//	
//	/** Body has failed attribute. */
//	public static OAVAttributeType body_has_failed;
//	
//	/** Body has aborted attribute. */
//	public static OAVAttributeType body_has_aborted;
	
	//-------- plan parameters --------
	
	/** The plan parameter type. */
	public static OAVObjectType planparameter_type;
	
	/** Planparameter has goalmapping. */
	public static OAVAttributeType	planparameter_has_goalmapping;
	
	/** Planparameter has internaleventmapping. */
	public static OAVAttributeType	planparameter_has_internaleventmapping;
	
	/** Planparameter has messagemapping. */
	public static OAVAttributeType	planparameter_has_messageeventmapping;

	/** The plan parameter set type. */
	public static OAVObjectType planparameterset_type;
	
	/** Planparameterset has goalmapping. */
	public static OAVAttributeType	planparameterset_has_goalmapping;
	
	/** Planparameterset has internaleventmapping. */
	public static OAVAttributeType	planparameterset_has_internaleventmapping;
	
	/** Planparameterset has messageeventmapping. */
	public static OAVAttributeType	planparameterset_has_messageeventmapping;
	
	//-------- event --------
	
	/** The event type. */
	public static OAVObjectType event_type;
	
	//-------- internal event --------
	
	/** The internal event type. */
	public static OAVObjectType internalevent_type;
	
	/** The internal event ref type. */
	public static OAVObjectType internaleventreference_type;
	
	//-------- message event --------
	
	/** The message event type. */
	public static OAVObjectType messageevent_type;
	
	/** Message event has direction attribute. */
	public static OAVAttributeType messageevent_has_direction;
	
	/** Message event has type attribute. */
	public static OAVAttributeType messageevent_has_type;
	
	/** Message event has match attribute. */
	public static OAVAttributeType messageevent_has_match;
	
	/** Message event has a degree for matching. */
//	public static OAVAttributeType messageevent_has_degree;

	/** The message event ref type. */
	public static OAVObjectType messageeventreference_type;
	
	//-------- trigger --------
	
	/** The trigger type. */
	public static OAVObjectType trigger_type;
	
	/** Trigger has internal events attribute. */
	public static OAVAttributeType trigger_has_internalevents;
	
	/** Trigger has message events attribute. */
	public static OAVAttributeType trigger_has_messageevents;
	
	/** Trigger has goal finisheds attribute. */
	public static OAVAttributeType trigger_has_goalfinisheds;
	
//	/** Trigger has filter attribute. */
//	public static OAVAttributeType trigger_has_filter;
	
	/** Trigger has fact addeds attribute. */
	public static OAVAttributeType trigger_has_factaddeds;
	
	/** Trigger has fact removeds attribute. */
	public static OAVAttributeType trigger_has_factremoveds;
	
	/** Trigger has fact changeds attribute. */
	public static OAVAttributeType trigger_has_factchangeds;
	
	/** The trigger reference type. */
	public static OAVObjectType triggerreference_type;
	
	/** Trigger reference has ref attribute. */
	public static OAVAttributeType triggerreference_has_ref;
	
	/** Trigger reference has match expression attribute. */
	public static OAVAttributeType triggerreference_has_match;
	
	//-------- plan trigger --------
	
	/** The plan trigger type. */
	public static OAVObjectType plantrigger_type;
	
	/** Plan trigger has goals attribute. */
	public static OAVAttributeType plantrigger_has_goals;
	
	/** Plan trigger has condition attribute. */
	public static OAVAttributeType plantrigger_has_condition;
	
	//-------- meta goal trigger --------
	
	/** The meta goal trigger type. */
	public static OAVObjectType metagoaltrigger_type;
	
	/** Meta goal trigger has goals attribute. */
	public static OAVAttributeType metagoaltrigger_has_goals;
	
	//-------- configuration --------
	
	/** The configuration type. */
	public static OAVObjectType configuration_type;
	
	/** Configuration has initial capabilities attribute. */
	public static OAVAttributeType configuration_has_initialcapabilities;
	
	/** Configuration has initial beliefs attribute. */
	public static OAVAttributeType configuration_has_initialbeliefs;
	
	/** Configuration has initial beliefsets attribute. */
	public static OAVAttributeType configuration_has_initialbeliefsets;
	
	/** Configuration has initial goals attribute. */
	public static OAVAttributeType configuration_has_initialgoals;
	
	/** Configuration has end goals attribute. */
	public static OAVAttributeType configuration_has_endgoals;
	
	/** Configuration has initial plans attribute. */
	public static OAVAttributeType configuration_has_initialplans;
	
	/** Configuration has end plans attribute. */
	public static OAVAttributeType configuration_has_endplans;
	
	/** Configuration has events attribute. */
	public static OAVAttributeType configuration_has_initialinternalevents;
	
	/** Configuration has initial message events attribute. */
	public static OAVAttributeType configuration_has_initialmessagevents;
	
	/** Configuration has end internal events attribute. */
	public static OAVAttributeType configuration_has_endinternalevents;
	
	/** Configuration has end message events attribute. */
	public static OAVAttributeType configuration_has_endmessageevents;
	
	//-------- configuration elements --------
	
	/** The configelement type. */
	public static OAVObjectType configelement_type;
	
	/** Configelement has ref attribute. */
	public static OAVAttributeType configelement_has_ref;
	
	//-------- configuration beliefs --------

	/** The configbelief type. */
	public static OAVObjectType configbelief_type;
	
	/** Configbelief has ref attribute. */
	public static OAVAttributeType configbelief_has_ref;

	/** The configbeliefset type. */
	public static OAVObjectType configbeliefset_type;
	
	/** Configbeliefset has ref attribute. */
	public static OAVAttributeType configbeliefset_has_ref;

	//-------- configuration parameters --------

	/** The configparameter type. */
	public static OAVObjectType configparameter_type;
	
	/** Configparameter has ref attribute. */
	public static OAVAttributeType configparameter_has_ref;

	/** The configparameterset type. */
	public static OAVObjectType configparameterset_type;
	
	/** Configparameterset has ref attribute. */
	public static OAVAttributeType configparameterset_has_ref;
	
	//-------- initial capability --------
	// todo: rename to (config-) capability in XML 
	
	/** The initialcapability type. */
	public static OAVObjectType initialcapability_type;
	
	/** Initialcapability has ref attribute. */
	public static OAVAttributeType initialcapability_has_ref;

	/** Initialcapability has configuration attribute. */
	public static OAVAttributeType initialcapability_has_configuration;
	
	//-------- config parameterelement --------
	
	/** The config parameter element type. */
	public static OAVObjectType configparameterelement_type;
	
	/** Config parameter element has parameters attribute. */
	public static OAVAttributeType configparameterelement_has_parameters;
	
	/** Config parameter element has parametersets attribute. */
	public static OAVAttributeType configparameterelement_has_parametersets;
	
	
	static
	{
		// type model
		bdimm_type_model = new OAVTypeModel("bdimm_type_model");
		bdimm_type_model.addTypeModel(OAVJavaType.java_type_model);
		
		// All types.
		java_parsedexpression_type	= bdimm_type_model.createJavaType(IParsedExpression.class, OAVJavaType.KIND_VALUE);
		java_messagetype_type	= bdimm_type_model.createJavaType(MessageType.class, OAVJavaType.KIND_VALUE);
		
		object_type = bdimm_type_model.createType("object");
		modelelement_type = bdimm_type_model.createType("melement", object_type);
		capabilityref_type	= bdimm_type_model.createType("mcapabilityref", modelelement_type);
		referenceableelement_type	= bdimm_type_model.createType("mreferenceableelement", modelelement_type);
		expressionrelevant_type = bdimm_type_model.createType("mexpressionrelevant", modelelement_type);
		elementreference_type	= bdimm_type_model.createType("melementreference", referenceableelement_type);
		expressionparameter_type = bdimm_type_model.createType("mexpressionparameter", modelelement_type);
		expression_type = bdimm_type_model.createType("mexpression", referenceableelement_type);
		condition_type = bdimm_type_model.createType("mcondition", expression_type);
		relevantbelief_type = bdimm_type_model.createType("mrelevantbelief", expressionrelevant_type);
		relevantbeliefset_type = bdimm_type_model.createType("mrelevantbeliefset", expressionrelevant_type);
		relevantgoal_type = bdimm_type_model.createType("mrelevantgoal", expressionrelevant_type);
		relevantparameter_type = bdimm_type_model.createType("mrelevantparameter", expressionrelevant_type);
		relevantparameterset_type = bdimm_type_model.createType("mrelevantparameterset", expressionrelevant_type);
		typedelement_type = bdimm_type_model.createType("mtypedelement", referenceableelement_type);
		parameter_type = bdimm_type_model.createType("mparameter", typedelement_type);
		parameterset_type = bdimm_type_model.createType("mparameterset", typedelement_type);
		parameterelement_type = bdimm_type_model.createType("mparameterelement", referenceableelement_type);
//		parameterelementreference_type = bdimm_type_model.createType("mparameterelementreference", elementreference_type);
		processableelement_type = bdimm_type_model.createType("mprocessableelement", parameterelement_type);
		triggerreference_type = bdimm_type_model.createType("mtriggerreference", modelelement_type);
//		typedelementreference_type = bdimm_type_model.createType("mtypedelementreference", elementreference_type);
		trigger_type = bdimm_type_model.createType("mtrigger", modelelement_type);
		plantrigger_type = bdimm_type_model.createType("mplantrigger", trigger_type);
		metagoaltrigger_type = bdimm_type_model.createType("mmetagoaltrigger", trigger_type);
		belief_type = bdimm_type_model.createType("mbelief", typedelement_type);
//		beliefreference_type = bdimm_type_model.createType("mbeliefreference", typedelementreference_type);
		beliefreference_type = bdimm_type_model.createType("mbeliefreference", elementreference_type);
		beliefset_type = bdimm_type_model.createType("mbeliefset", typedelement_type);
//		beliefsetreference_type = bdimm_type_model.createType("mbeliefsetreference", typedelementreference_type);
		beliefsetreference_type = bdimm_type_model.createType("mbeliefsetreference", elementreference_type);
		inhibits_type = bdimm_type_model.createType("minhibits", condition_type);
		goal_type = bdimm_type_model.createType("mgoal", processableelement_type);
		goalreference_type = bdimm_type_model.createType("mgoalreference", elementreference_type);
		performgoal_type = bdimm_type_model.createType("mperformgoal", goal_type);
//		performgoalref_type = bdimm_type_model.createType("mperformgoalref", goalreference_type);
		achievegoal_type = bdimm_type_model.createType("machievegoal", goal_type);
//		achievegoalref_type = bdimm_type_model.createType("machievegoalref", goalreference_type);
		querygoal_type = bdimm_type_model.createType("mquerygoal", goal_type);
//		querygoalref_type = bdimm_type_model.createType("mquerygoalref", goalreference_type);
		maintaingoal_type = bdimm_type_model.createType("mmaintaingoal", goal_type);
//		maintaingoalref_type = bdimm_type_model.createType("mmaintaingoalref", goalreference_type);
		metagoal_type = bdimm_type_model.createType("mmetagoal", querygoal_type);	// todo: querygoal???
//		metagoalref_type = bdimm_type_model.createType("mmetagoalref", goalreference_type);
		planparameter_type	= bdimm_type_model.createType("mplanparameter", parameter_type);
		planparameterset_type	= bdimm_type_model.createType("mplanparameterset", parameterset_type);
		body_type	= bdimm_type_model.createType("mbody", expression_type);
		plan_type = bdimm_type_model.createType("mplan", parameterelement_type);
		event_type = bdimm_type_model.createType("mevent", processableelement_type);
		internalevent_type = bdimm_type_model.createType("minternalevent", event_type);
		internaleventreference_type = bdimm_type_model.createType("minternaleventreference", elementreference_type);
		messageevent_type = bdimm_type_model.createType("mmessageevent", event_type);
		messageeventreference_type = bdimm_type_model.createType("mmessageeventreference", elementreference_type);
		
		configparameterelement_type	= bdimm_type_model.createType("mconfigparameterelement", modelelement_type);
		configelement_type	= bdimm_type_model.createType("mconfigelement", configparameterelement_type);
		configbelief_type	= bdimm_type_model.createType("mconfigbelief", belief_type);
		configbeliefset_type	= bdimm_type_model.createType("mconfigbeliefset", beliefset_type);
		configparameter_type	= bdimm_type_model.createType("mconfigparameter", parameter_type);
		configparameterset_type	= bdimm_type_model.createType("mconfigparameterset", parameterset_type);
		initialcapability_type	= bdimm_type_model.createType("minitialcapability");
		configuration_type = bdimm_type_model.createType("mconfiguration", modelelement_type);
		capability_type = bdimm_type_model.createType("mcapability", modelelement_type);
		agent_type = bdimm_type_model.createType("magent", capability_type);
		properties_type	= bdimm_type_model.createType("mproperties", modelelement_type);
		
		// model element
		modelelement_has_name = modelelement_type.createAttributeType("melement_has_name", OAVJavaType.java_string_type);
		modelelement_has_description = modelelement_type.createAttributeType("melement_has_description", OAVJavaType.java_string_type);
		
		// capability reference
		capabilityref_has_file	= capabilityref_type.createAttributeType("mcapabilityref_has_file" , OAVJavaType.java_string_type);
		capabilityref_has_capability	= capabilityref_type.createAttributeType("mcapabilityref_has_capability" , capability_type);

		// referenceable element
		referenceableelement_has_exported	= referenceableelement_type.createAttributeType("mreferenceableelement_has_exported", OAVJavaType.java_string_type, OAVAttributeType.NONE, EXPORTED_FALSE);
//		referenceableelement_has_assignto	= referenceableelement_type.createAttributeType("referenceableelement_has_assignto", OAVJavaType.java_string_type, OAVAttributeType.LIST);
		
		// element reference
		elementreference_has_concrete	= elementreference_type.createAttributeType("melementreference_has_concrete", OAVJavaType.java_string_type);
//		elementreference_has_abstract	= elementreference_type.createAttributeType("elementreference_has_abstract", OAVJavaType.java_boolean_type);
//		elementreference_has_required	= elementreference_type.createAttributeType("elementreference_has_required", OAVJavaType.java_boolean_type);
		
		// expression
//		expressionparameter_has_class = expressionparameter_type.createAttributeType("mexpressionparameter_has_class", OAVJavaType.java_class_type);
		
		expressionrelevant_has_ref = expressionrelevant_type.createAttributeType("mexpressionrelevant_has_ref", OAVJavaType.java_string_type);
		expressionrelevant_has_eventtype = expressionrelevant_type.createAttributeType("mexpressionrelevant_has_eventtype", OAVJavaType.java_string_type);

		expression_has_language = expression_type.createAttributeType("mexpression_has_language", OAVJavaType.java_string_type, OAVAttributeType.NONE);//, "java");
		expression_has_content = expression_type.createAttributeType("mexpression_has_content", OAVJavaType.java_object_type);// java_parsedexpression_type)
//		expression_has_class = expression_type.createAttributeType("mexpression_has_class", OAVJavaType.java_class_type);
		expression_has_parameters = expression_type.createAttributeType("mexpression_has_parameters",  expressionparameter_type, OAVAttributeType.LIST);
		expression_has_relevants = expression_type.createAttributeType("mexpression_has_relevants",  expressionrelevant_type, OAVAttributeType.LIST);
		expression_has_evaluationmode = expression_type.createAttributeType("mexpression_has_evaluationmode", OAVJavaType.java_string_type);
				
		// condition
		// todo: separate conditions from expressions
		condition_has_trigger = condition_type.createAttributeType("mcondition_has_trigger", OAVJavaType.java_string_type);

		// typed element
		typedelement_has_classname = typedelement_type.createAttributeType("mtypedelement_has_classname", OAVJavaType.java_string_type, OAVAttributeType.NONE);
		typedelement_has_class = typedelement_type.createAttributeType("mtypedelement_has_class", OAVJavaType.java_class_type, OAVAttributeType.NONE, Object.class);
		typedelement_has_updaterate = typedelement_type.createAttributeType("mtypedelement_has_updaterate", OAVJavaType.java_long_type);
		typedelement_has_transient = typedelement_type.createAttributeType("mtypedelement_has_transient", OAVJavaType.java_boolean_type);
		typedelement_has_evaluationmode = typedelement_type.createAttributeType("mtypedelement_has_evaluationmode", OAVJavaType.java_string_type, OAVAttributeType.NONE, EVALUATIONMODE_STATIC);
		
//		// typed element reference
//		typedelementreference_has_class = typedelementreference_type.createAttributeType("typedelementreference_has_class", OAVJavaType.java_class_type);

		// parameter
		parameter_has_value = parameter_type.createAttributeType("mparameter_has_value", expression_type);
		parameter_has_direction = parameter_type.createAttributeType("mparameter_has_direction", OAVJavaType.java_string_type, OAVAttributeType.NONE, "in");
		parameter_has_optional = parameter_type.createAttributeType("mparameter_has_optional", OAVJavaType.java_boolean_type);		
		parameter_has_bindingoptions = parameter_type.createAttributeType("mparameter_has_bindingoptions", expression_type);
		
		// parameter set
		parameterset_has_values = parameterset_type.createAttributeType("mparameterset_has_values", expression_type, OAVAttributeType.LIST);
		parameterset_has_valuesexpression = parameterset_type.createAttributeType("mparameterset_has_valuesexpression", expression_type);
		parameterset_has_direction = parameterset_type.createAttributeType("mparameterset_has_direction", OAVJavaType.java_string_type, OAVAttributeType.NONE,  "in");
		parameterset_has_optional = parameterset_type.createAttributeType("mparameterset_has_optional", OAVJavaType.java_boolean_type);		
		
		// parameter element
		parameterelement_has_parameters = parameterelement_type.createAttributeType("mparameterelement_has_mparameters", parameter_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
		parameterelement_has_parametersets = parameterelement_type.createAttributeType("mparameterelement_has_mparametersets", parameterset_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
			
		// processable element
		processableelement_has_posttoall = processableelement_type.createAttributeType("mprocessableelement_has_posttoall", OAVJavaType.java_boolean_type, OAVAttributeType.NONE, Boolean.FALSE);
		processableelement_has_randomselection = processableelement_type.createAttributeType("mprocessableelement_has_randomselection", OAVJavaType.java_boolean_type);
		
//		// parameter element reference
//		parameterelementreference_has_parameterrefs = parameterelementreference_type.createAttributeType("parameterelementreference_has_parameterrefs", typedelementreference_type, OAVAttributeType.LIST);
//		parameterelementreference_has_parametersetrefs = parameterelementreference_type.createAttributeType("parameterelementreference_has_parametersetrefs", typedelementreference_type, OAVAttributeType.LIST);
						
		// trigger
		triggerreference_has_ref = triggerreference_type.createAttributeType("mtriggerreference_has_ref", OAVJavaType.java_string_type);
		triggerreference_has_match = triggerreference_type.createAttributeType("mtriggerreference_has_match", expression_type);

		trigger_has_internalevents = trigger_type.createAttributeType("mtrigger_has_internalevents", triggerreference_type, OAVAttributeType.LIST);
		trigger_has_messageevents = trigger_type.createAttributeType("mtrigger_has_messageevents", triggerreference_type, OAVAttributeType.LIST);
		trigger_has_goalfinisheds = trigger_type.createAttributeType("mtrigger_has_goalfinisheds", triggerreference_type, OAVAttributeType.LIST);
//		trigger_has_filter = trigger_type.createAttributeType("trigger_has_filter", expression_type);
		// Todo: support match expression for fact changes/adds/removes also?
		trigger_has_factaddeds = trigger_type.createAttributeType("mtrigger_has_factaddeds", OAVJavaType.java_string_type, OAVAttributeType.LIST);
		trigger_has_factremoveds = trigger_type.createAttributeType("mtrigger_has_factremoveds", OAVJavaType.java_string_type, OAVAttributeType.LIST);
		trigger_has_factchangeds = trigger_type.createAttributeType("mtrigger_has_factchangeds", OAVJavaType.java_string_type, OAVAttributeType.LIST);
		
		// plan trigger
		plantrigger_has_goals = plantrigger_type.createAttributeType("mplantrigger_has_goals", triggerreference_type, OAVAttributeType.LIST);
		plantrigger_has_condition = plantrigger_type.createAttributeType("mplantrigger_has_condition", condition_type);
		
		// meta goal trigger
		metagoaltrigger_has_goals = metagoaltrigger_type.createAttributeType("mmetagoaltrigger_has_goals", triggerreference_type, OAVAttributeType.LIST);
		
		// belief
//		belief_has_dynamic = belief_type.createAttributeType("belief_has_dynamic", OAVJavaType.java_boolean_type, OAVAttributeType.NONE, Boolean.FALSE);
		belief_has_fact = belief_type.createAttributeType("mbelief_has_fact", expression_type);
		
		// belief set
		// todo: make configurable belief store type (list, set, ...)
//		beliefset_has_dynamic = beliefset_type.createAttributeType("beliefset_has_dynamic", OAVJavaType.java_boolean_type, OAVAttributeType.NONE, Boolean.FALSE);
		beliefset_has_facts = beliefset_type.createAttributeType("mbeliefset_has_facts", expression_type, OAVAttributeType.LIST);
		beliefset_has_factsexpression = beliefset_type.createAttributeType("mbeliefset_has_factsexpression", expression_type);
		
		// inhibit
		inhibits_has_ref = inhibits_type.createAttributeType("minhibits_has_ref", OAVJavaType.java_string_type);
		inhibits_has_inhibit = inhibits_type.createAttributeType("minhibits_has_inhibit", OAVJavaType.java_string_type, OAVAttributeType.NONE, INHIBITS_WHEN_ACTIVE);
		
		// goal
		//goal_has_exported = new OAVAttributeType("goal_has_exported", java_boolean_type, Boolean.FALSE);
		goal_has_creationcondition = goal_type.createAttributeType("mgoal_has_creationcondition", condition_type);
		goal_has_contextcondition = goal_type.createAttributeType("mgoal_has_contextcondition", condition_type);
		goal_has_dropcondition = goal_type.createAttributeType("mgoal_has_dropcondition", condition_type);
		goal_has_retry = goal_type.createAttributeType("mgoal_has_retry", OAVJavaType.java_boolean_type, OAVAttributeType.NONE, Boolean.TRUE);
		goal_has_retrydelay = goal_type.createAttributeType("mgoal_has_retrydelay", OAVJavaType.java_long_type, OAVAttributeType.NONE, new Long(0));
		goal_has_recur = goal_type.createAttributeType("mgoal_has_recur", OAVJavaType.java_boolean_type, OAVAttributeType.NONE, Boolean.FALSE);
		goal_has_recurdelay = goal_type.createAttributeType("mgoal_has_recurdelay", OAVJavaType.java_long_type, OAVAttributeType.NONE, new Long(0));
		goal_has_recurcondition = goal_type.createAttributeType("mgoal_has_recurcondition", condition_type);
		goal_has_exclude = goal_type.createAttributeType("mgoal_has_exclude", OAVJavaType.java_string_type, OAVAttributeType.NONE,  EXCLUDE_WHEN_TRIED);
		goal_has_rebuild = goal_type.createAttributeType("mgoal_has_recalculate", OAVJavaType.java_boolean_type, OAVAttributeType.NONE, Boolean.FALSE);
		goal_has_inhibits = goal_type.createAttributeType("mgoal_has_inhibits", inhibits_type, OAVAttributeType.LIST);
//		goal_has_cardinality = goal_type.createAttributeType("mgoal_has_cardinality", OAVJavaType.java_integer_type, OAVAttributeType.NONE, new Integer(-1));
		goal_has_cardinality = goal_type.createAttributeType("mgoal_has_cardinality", OAVJavaType.java_integer_type, OAVAttributeType.NONE, new Integer(Integer.MAX_VALUE));
		goal_has_unique = goal_type.createAttributeType("mgoal_has_unique", OAVJavaType.java_boolean_type);
		goal_has_excludedparameter = goal_type.createAttributeType("mgoal_has_excludedparameter", typedelement_type, OAVAttributeType.LIST);
		
		// perform goal
		
		// achieve goal
		achievegoal_has_targetcondition = achievegoal_type.createAttributeType("machievegoal_has_targetcondition", condition_type);
		achievegoal_has_failurecondition = achievegoal_type.createAttributeType("machievegoal_has_failurecondition", condition_type);

		// query goal
		querygoal_has_failurecondition = querygoal_type.createAttributeType("mquerygoal_has_failurecondition", condition_type);
	
		// maintain goal
		maintaingoal_has_maintaincondition = maintaingoal_type.createAttributeType("mmaintaingoal_has_maintaincondition", condition_type);
		maintaingoal_has_targetcondition = maintaingoal_type.createAttributeType("mmaintaingoal_has_targetcondition", condition_type);

		// meta goal
		metagoal_has_trigger = metagoal_type.createAttributeType("mmetagoal_has_trigger", metagoaltrigger_type);
		
		// planparameters
		// todo: multiplicity
		planparameter_has_goalmapping	= planparameter_type.createAttributeType("mplanparameter_has_goalmapping", OAVJavaType.java_string_type);//, OAVAttributeType.LIST);
		planparameter_has_internaleventmapping	= planparameter_type.createAttributeType("mplanparameter_has_internaleventmapping", OAVJavaType.java_string_type);//, OAVAttributeType.LIST);
		planparameter_has_messageeventmapping	= planparameter_type.createAttributeType("mplanparameter_has_messageeventmapping", OAVJavaType.java_string_type);//, OAVAttributeType.LIST);

		planparameterset_has_goalmapping	= planparameterset_type.createAttributeType("mplanparameterset_has_goalmapping", OAVJavaType.java_string_type);//, OAVAttributeType.LIST);
		planparameterset_has_internaleventmapping	= planparameterset_type.createAttributeType("mplanparameterset_has_internaleventmapping", OAVJavaType.java_string_type);//, OAVAttributeType.LIST);
		planparameterset_has_messageeventmapping	= planparameterset_type.createAttributeType("mplanparameterset_has_messageeventmapping", OAVJavaType.java_string_type);//, OAVAttributeType.LIST);

		// plan body
		body_has_type = body_type.createAttributeType("mbody_has_type", OAVJavaType.java_string_type, OAVAttributeType.NONE,  "standard");
		body_has_class = body_type.createAttributeType("mbody_has_class", OAVJavaType.java_class_type, OAVAttributeType.NONE);
		body_has_classname = body_type.createAttributeType("mbody_has_classname", OAVJavaType.java_string_type, OAVAttributeType.NONE);
//		body_has_inline = body_type.createAttributeType("mbody_has_inline", OAVJavaType.java_boolean_type);
//		body_has_passed	= body_type.createAttributeType("mbody_has_passed", OAVJavaType.java_string_type);	// Todo: should be expression?
//		body_has_failed	= body_type.createAttributeType("mbody_has_failed", OAVJavaType.java_string_type);	// Todo: should be expression?
//		body_has_aborted	= body_type.createAttributeType("mbody_has_aborted", OAVJavaType.java_string_type);	// Todo: should be expression?

		// plan
		plan_has_body = plan_type.createAttributeType("mplan_has_body", body_type);
		plan_has_trigger = plan_type.createAttributeType("mplan_has_trigger", plantrigger_type);
		plan_has_precondition = plan_type.createAttributeType("mplan_has_precondition", expression_type);
		plan_has_contextcondition = plan_type.createAttributeType("mplan_has_contextcondition", condition_type);
		plan_has_priority = plan_type.createAttributeType("mplan_has_priority", OAVJavaType.java_integer_type);
		plan_has_waitqueue = plan_type.createAttributeType("mplan_has_waitqueue", trigger_type);
				
		// internal event
		
		// message event
		messageevent_has_direction = messageevent_type.createAttributeType("mmessageevent_has_direction", OAVJavaType.java_string_type, OAVAttributeType.NONE, MESSAGE_DIRECTION_SEND_RECEIVE);
		messageevent_has_type = messageevent_type.createAttributeType("mmessageevent_has_type", OAVJavaType.java_string_type);
		messageevent_has_match = messageevent_type.createAttributeType("mmessageevent_has_match", expression_type);
//		messageevent_has_degree = messageevent_type.createAttributeType("messageevent_has_degree", OAVJavaType.java_integer_type);
		
		// config elments
		
		// config parameter element
		configparameterelement_has_parameters = configparameterelement_type.createAttributeType("mconfigparameterelement_has_parameters", configparameter_type, OAVAttributeType.LIST);
		configparameterelement_has_parametersets = configparameterelement_type.createAttributeType("mconfigparameterelement_has_parametersets", configparameterset_type, OAVAttributeType.LIST);
		
//		configelement_has_ref	= configelement_type.createAttributeType("mconfigelement_has_ref", referenceableelement_type);
//		configbelief_has_ref	= configbelief_type.createAttributeType("mconfigbelief_has_ref", referenceableelement_type);
//		configbeliefset_has_ref	= configbeliefset_type.createAttributeType("mconfigbeliefset_has_ref", referenceableelement_type);
//		configparameter_has_ref	= configparameter_type.createAttributeType("mconfigparameter_has_ref", referenceableelement_type);
//		configparameterset_has_ref	= configparameterset_type.createAttributeType("mconfigparameterset_has_ref", referenceableelement_type);
		
		configelement_has_ref	= configelement_type.createAttributeType("mconfigelement_has_ref", OAVJavaType.java_string_type);
		configbelief_has_ref	= configbelief_type.createAttributeType("mconfigbelief_has_ref", OAVJavaType.java_string_type);
		configbeliefset_has_ref	= configbeliefset_type.createAttributeType("mconfigbeliefset_has_ref", OAVJavaType.java_string_type);
		configparameter_has_ref	= configparameter_type.createAttributeType("mconfigparameter_has_ref", OAVJavaType.java_string_type);
		configparameterset_has_ref	= configparameterset_type.createAttributeType("mconfigparameterset_has_ref", OAVJavaType.java_string_type);
		
		// initial capability
		initialcapability_has_ref	= initialcapability_type.createAttributeType("minitialcapability_has_ref", OAVJavaType.java_string_type);
		initialcapability_has_configuration	= initialcapability_type.createAttributeType("minitialcapability_has_configuration", OAVJavaType.java_string_type);
		
		// configuration
		configuration_has_initialcapabilities	= configuration_type.createAttributeType("mconfiguration_has_initialcapabilities", initialcapability_type, OAVAttributeType.LIST);
		configuration_has_initialbeliefs = configuration_type.createAttributeType("mconfiguration_has_initialbeliefs", configbelief_type, OAVAttributeType.LIST);
		configuration_has_initialbeliefsets = configuration_type.createAttributeType("mconfiguration_has_initialbeliefsets", configbeliefset_type, OAVAttributeType.LIST);
		configuration_has_initialgoals = configuration_type.createAttributeType("mconfiguration_has_initialgoals", configelement_type, OAVAttributeType.LIST);
		configuration_has_endgoals = configuration_type.createAttributeType("mconfiguration_has_endgoals", configelement_type, OAVAttributeType.LIST);
		configuration_has_initialplans = configuration_type.createAttributeType("mconfiguration_has_initialplans", configelement_type, OAVAttributeType.LIST);
		configuration_has_endplans = configuration_type.createAttributeType("mconfiguration_has_endplans", configelement_type, OAVAttributeType.LIST);
		configuration_has_initialinternalevents = configuration_type.createAttributeType("mconfiguration_has_initialinternalevents", configelement_type, OAVAttributeType.LIST);
		configuration_has_initialmessagevents = configuration_type.createAttributeType("mconfiguration_has_initialmessageevents", configelement_type, OAVAttributeType.LIST);
		configuration_has_endinternalevents = configuration_type.createAttributeType("mconfiguration_has_endinternalevents", configelement_type, OAVAttributeType.LIST);
		configuration_has_endmessageevents = configuration_type.createAttributeType("mconfiguration_has_endmessageevents", configelement_type, OAVAttributeType.LIST);
				
		// capability
		capability_has_package = capability_type.createAttributeType("mcapability_has_package", OAVJavaType.java_string_type);
		capability_has_abstract = capability_type.createAttributeType("mcapability_has_abstract", OAVJavaType.java_boolean_type);
		capability_has_imports = capability_type.createAttributeType("mcapability_has_imports", OAVJavaType.java_string_type, OAVAttributeType.LIST);
		capability_has_capabilityrefs = capability_type.createAttributeType("mcapability_has_mcapabilityrefs", capabilityref_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
		capability_has_beliefs = capability_type.createAttributeType("mcapability_has_mbeliefs", belief_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
		capability_has_beliefsets = capability_type.createAttributeType("mcapability_has_mbeliefsets", beliefset_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
		capability_has_beliefrefs = capability_type.createAttributeType("mcapability_has_mbeliefrefs", beliefreference_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
		capability_has_beliefsetrefs = capability_type.createAttributeType("mcapability_has_mbeliefsetrefs", beliefsetreference_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
		capability_has_goals = capability_type.createAttributeType("mcapability_has_mgoals", goal_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
		capability_has_goalrefs = capability_type.createAttributeType("mcapability_has_mgoalrefs", goalreference_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
		capability_has_plans = capability_type.createAttributeType("mcapability_has_mplans", plan_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
		capability_has_messageevents = capability_type.createAttributeType("mcapability_has_mmessageevents", messageevent_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
		capability_has_internalevents = capability_type.createAttributeType("mcapability_has_minternalevents", internalevent_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
		capability_has_messageeventrefs = capability_type.createAttributeType("mcapability_has_mmessageeventrefs", messageeventreference_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
		capability_has_internaleventrefs = capability_type.createAttributeType("mcapability_has_minternaleventrefs", internaleventreference_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
		capability_has_expressions = capability_type.createAttributeType("mcapability_has_mexpressions", expression_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
		capability_has_conditions = capability_type.createAttributeType("mcapability_has_mconditions", condition_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
		capability_has_properties = capability_type.createAttributeType("mcapability_has_mproperties", expression_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);
		capability_has_defaultconfiguration = capability_type.createAttributeType("mcapability_has_defaultconfiguration", configuration_type);
		capability_has_configurations = capability_type.createAttributeType("mcapability_has_mconfigurations", configuration_type, OAVAttributeType.ORDEREDMAP, null, modelelement_has_name);

		// agent
		agent_has_propertyfile	= agent_type.createAttributeType("magent_has_propertyfile", OAVJavaType.java_string_type);
		
		// propertybase
		properties_has_properties	= properties_type.createAttributeType("properties_has_properties", expression_type, OAVAttributeType.LIST);
	}
	
	//-------- xml mapping --------
	
	/**
	 *  Get the XML to meta model mapping.
	 *  @return A mapping from XML path to object type.
	 */
	public static IOAVXMLMapping	getXMLMapping(Map props)
	{
		OAVXMLHelper	expansions	= new OAVXMLHelper();
		expansions.addIgnoreAttribute(capabilityref_has_capability);
		expansions.addExpansion(capability_has_imports, "imports/import");
		expansions.addExpansion(capability_has_capabilityrefs, "capabilities/capability");
		expansions.addExpansion(capability_has_beliefs, "beliefs/belief", belief_type);
		expansions.addExpansion(capability_has_beliefrefs, "beliefs/beliefref", beliefreference_type);
		expansions.addExpansion(capability_has_beliefsets, "beliefs/beliefset", beliefset_type);
		expansions.addExpansion(capability_has_beliefsetrefs, "beliefs/beliefsetref", beliefsetreference_type);
		expansions.addExpansion(capability_has_goals, "goals/achievegoal", achievegoal_type);
//		expansions.addExpansion(capability_has_goalrefs, "goals/achievegoalref", achievegoalref_type);
		expansions.addExpansion(capability_has_goals, "goals/performgoal", performgoal_type);
//		expansions.addExpansion(capability_has_goalrefs, "goals/performgoalref", performgoalref_type);
		expansions.addExpansion(capability_has_goals, "goals/querygoal", querygoal_type);
//		expansions.addExpansion(capability_has_goalrefs, "goals/querygoalref", querygoalref_type);
		expansions.addExpansion(capability_has_goals, "goals/maintaingoal", maintaingoal_type);
//		expansions.addExpansion(capability_has_goalrefs, "goals/maintaingoalref", maintaingoalref_type);
		expansions.addExpansion(capability_has_goals, "goals/metagoal", metagoal_type);
//		expansions.addExpansion(capability_has_goalrefs, "goals/metagoalref", metagoalref_type);
		expansions.addExpansion(capability_has_goalrefs, "goals/performgoalref", goalreference_type);
		expansions.addExpansion(capability_has_goalrefs, "goals/achievegoalref", goalreference_type);
		expansions.addExpansion(capability_has_goalrefs, "goals/querygoalref", goalreference_type);
		expansions.addExpansion(capability_has_goalrefs, "goals/maintaingoalref", goalreference_type);
		expansions.addExpansion(capability_has_goalrefs, "goals/metagoalref", goalreference_type);
		expansions.addExpansion(capability_has_plans, "plans/plan");
		expansions.addExpansion(capability_has_internalevents, "events/internalevent", internalevent_type);
		expansions.addExpansion(capability_has_internaleventrefs, "events/internaleventref", internaleventreference_type);
		expansions.addExpansion(capability_has_messageevents, "events/messageevent", messageevent_type);
		expansions.addExpansion(capability_has_messageeventrefs, "events/messageeventref", messageeventreference_type);
		expansions.addExpansion(capability_has_expressions, "expressions/expression", expression_type);
		expansions.addExpansion(capability_has_conditions, "expressions/condition", condition_type);
		expansions.addExpansion(capability_has_properties, "properties/property");
		expansions.addExpansion(capability_has_defaultconfiguration, "configurations/default");
		expansions.addExpansion(capability_has_configurations, "configurations/configuration");
		
		expansions.addExpansion(goal_has_inhibits, "deliberation/inhibits");
		expansions.addExpansion(goal_has_cardinality, "deliberation/cardinality");
		expansions.addExpansion(goal_has_excludedparameter, "unique/exclude/parameterref");

//		expansions.addExpansion(goalreference_has_inhibits, "deliberation/inhibits");
//		expansions.addExpansion(goalreference_has_cardinality, "deliberation/cardinality");

		expansions.addExpansion(beliefset_has_factsexpression, "facts");

		expansions.addExpansion(parameterset_has_valuesexpression, "values");
		expansions.addExpansion(planparameter_has_goalmapping, "goalmapping/ref");
		expansions.addExpansion(planparameter_has_internaleventmapping, "internaleventmapping/ref");
		expansions.addExpansion(planparameter_has_messageeventmapping, "messageeventmapping/ref");
		expansions.addExpansion(planparameterset_has_goalmapping, "goalmapping/ref");
		expansions.addExpansion(planparameterset_has_internaleventmapping, "internaleventmapping/ref");
		expansions.addExpansion(planparameterset_has_messageeventmapping, "messageeventmapping/ref");

		expansions.addExpansion(trigger_has_factaddeds, "factadded/ref");
		expansions.addExpansion(trigger_has_factremoveds, "factremoved/ref");
		expansions.addExpansion(trigger_has_factchangeds, "factchanged/ref");
//		expansions.addExpansion(trigger_has_goalfinisheds, "goalfinished/ref");
//		expansions.addExpansion(trigger_has_messageevents,	"messageevent/ref");
//		expansions.addExpansion(trigger_has_internalevents,	"internalevent/ref");

//		expansions.addExpansion(plantrigger_has_goals, "goal/ref");
		
		expansions.addExpansion(metagoaltrigger_has_goals, "goal/ref");

		expansions.addExpansion(expression_has_content, "");	// content has no attribute.
		expansions.addExpansion(expression_has_relevants, "relevantbelief", relevantbelief_type);
		expansions.addExpansion(expression_has_relevants, "relevantbeliefset", relevantbeliefset_type);
		expansions.addExpansion(expression_has_relevants, "relevantgoal", relevantgoal_type);
		expansions.addExpansion(expression_has_relevants, "relevantparameter", relevantparameter_type);
		expansions.addExpansion(expression_has_relevants, "relevantparameterset", relevantparameterset_type);

		expansions.addExpansion(configuration_has_initialcapabilities, "capabilities/initialcapability");
		expansions.addExpansion(configuration_has_initialgoals, "goals/initialgoal");
		expansions.addExpansion(configuration_has_initialbeliefs, "beliefs/initialbelief", configbelief_type);
		expansions.addExpansion(configuration_has_initialbeliefsets, "beliefs/initialbeliefset", configbeliefset_type);
		expansions.addExpansion(configuration_has_endgoals, "goals/endgoal");
		expansions.addExpansion(configuration_has_initialplans, "plans/initialplan");
		expansions.addExpansion(configuration_has_endplans, "plans/endplan");
		expansions.addExpansion(configuration_has_initialinternalevents, "events/initialinternalevent");
		expansions.addExpansion(configuration_has_endinternalevents, "events/endinternalevent");
		expansions.addExpansion(configuration_has_initialmessagevents, "events/initialmessageevent");
		expansions.addExpansion(configuration_has_endmessageevents,"events/endmessageevent");

//		expansions.addExpansion(referenceableelement_has_assignto,	"assignto/ref");
		expansions.addExpansion(elementreference_has_concrete,	"concrete/ref");
//		expansions.addExpansion(elementreference_has_required,	"abstract/required");
		
		expansions.addExpansion(properties_has_properties, "property");
		
		Set	roottypes	= new HashSet();
		roottypes.add(OAVBDIMetaModel.agent_type);
		roottypes.add(OAVBDIMetaModel.capability_type);
		roottypes.add(OAVBDIMetaModel.properties_type);
		
		IValueConverter	basic	= new BasicValueConverter();
		IValueConverter	elementresolver	= new ElementResolver();
		IValueConverter	excluderesolver	= new ExcludeResolver();
//		IValueConverter	parameterresolver	= new ParameterResolver();
//		IValueConverter mappingresolver = new MappingResolver();
		IValueConverter messagetyperesolver = new MessageTypeResolver(props);
		IValueConverter	dummy	= new IValueConverter()
		{
			public boolean	isTwoPass()
			{
				return false;
			}
	
			public Object convertValue(IOAVState state, List stack, OAVAttributeType attribute, String value, MultiCollection report)
			{
				// If tag (e.g. <unique> or <abstract>) exists, attribute is true.
				return Boolean.TRUE;
			}
		};
		Map	valuemapping	= new HashMap();
		valuemapping.put(OAVJavaType.java_boolean_type, basic);
		valuemapping.put(OAVJavaType.java_integer_type, basic);
		valuemapping.put(OAVJavaType.java_long_type, basic);
		valuemapping.put(OAVJavaType.java_string_type, basic);
		valuemapping.put(OAVJavaType.java_class_type, new ClassValueConverter());
		valuemapping.put(goal_has_unique, dummy);
//		valuemapping.put(elementreference_has_abstract, dummy);
		valuemapping.put(expression_has_content, new ExpressionParser());
		
		// References to resolve.
//		valuemapping.put(triggerreference_has_ref, elementresolver);
//		valuemapping.put(trigger_has_messageevents, elementresolver);
//		valuemapping.put(trigger_has_internalevents, elementresolver);
//		valuemapping.put(trigger_has_goalfinisheds, elementresolver);
//		valuemapping.put(trigger_has_factadded, elementresolver);
//		valuemapping.put(trigger_has_factremoved, elementresolver);
//		valuemapping.put(trigger_has_factchanged, elementresolver);
//		valuemapping.put(plantrigger_has_goals, elementresolver);
		valuemapping.put(metagoaltrigger_has_goals, elementresolver);
//		valuemapping.put(inhibits_has_ref, elementresolver);
//		valuemapping.put(configelement_has_ref, elementresolver);
//		valuemapping.put(configbelief_has_ref, elementresolver);
//		valuemapping.put(configbeliefset_has_ref, elementresolver);
		valuemapping.put(initialcapability_has_ref, elementresolver);
		valuemapping.put(capability_has_defaultconfiguration, elementresolver);
		
		valuemapping.put(goal_has_excludedparameter, excluderesolver);
		
//		valuemapping.put(configparameter_has_ref, parameterresolver);
//		valuemapping.put(configparameterset_has_ref, parameterresolver);
		
//		valuemapping.put(planparameter_has_goalmapping, mappingresolver);
//		valuemapping.put(planparameterset_has_goalmapping, mappingresolver);
//		valuemapping.put(planparameter_has_internaleventmapping, mappingresolver);
//		valuemapping.put(planparameterset_has_internaleventmapping, mappingresolver);
//		valuemapping.put(planparameter_has_messageeventmapping, mappingresolver);
//		valuemapping.put(planparameterset_has_messageeventmapping, mappingresolver);
		
		valuemapping.put(messageevent_has_type, messagetyperesolver);
			
		OAVXMLMapping	xmlmapping	= new OAVXMLMapping(roottypes, expansions, valuemapping, "m");

		xmlmapping.addElementMapping("agent/plans/plan/parameter", planparameter_type, parameterelement_has_parameters);
		xmlmapping.addElementMapping("agent/plans/plan/parameterset", planparameterset_type, parameterelement_has_parametersets);

		xmlmapping.addContentMapping("agent/goals/performgoal/unique", goal_has_unique);
		xmlmapping.addContentMapping("agent/goals/achievegoal/unique", goal_has_unique);
		xmlmapping.addContentMapping("agent/goals/querygoal/unique", goal_has_unique);
		xmlmapping.addContentMapping("agent/goals/maintaingoal/unique", goal_has_unique);
		xmlmapping.addContentMapping("agent/goals/metagoal/unique", goal_has_unique);

		xmlmapping.addElementMapping("capability/plans/plan/parameter", planparameter_type, parameterelement_has_parameters);
		xmlmapping.addElementMapping("capability/plans/plan/parameterset", planparameterset_type, parameterelement_has_parametersets);

		xmlmapping.addContentMapping("capability/goals/performgoal/unique", goal_has_unique);
		xmlmapping.addContentMapping("capability/goals/achievegoal/unique", goal_has_unique);
		xmlmapping.addContentMapping("capability/goals/querygoal/unique", goal_has_unique);
		xmlmapping.addContentMapping("capability/goals/maintaingoal/unique", goal_has_unique);
		xmlmapping.addContentMapping("capability/goals/metagoal/unique", goal_has_unique);
		
		xmlmapping.addContentMapping(expression_type, expression_has_content);

//		xmlmapping.addContentMapping(triggerreference_type, expression_has_content);
		
		// Ignore XML namespace attributes (hack???).
		xmlmapping.ignorePath("agent/xmlns");
		xmlmapping.ignorePath("agent/xmlns:xsi");
		xmlmapping.ignorePath("agent/xsi:schemaLocation");
		xmlmapping.ignorePath("capability/xmlns");
		xmlmapping.ignorePath("capability/xmlns:xsi");
		xmlmapping.ignorePath("capability/xsi:schemaLocation");
		xmlmapping.ignorePath("properties/xmlns");
		xmlmapping.ignorePath("properties/xmlns:xsi");
		xmlmapping.ignorePath("properties/xsi:schemaLocation");

		return xmlmapping;
	}
	
	//-------- helper classes --------

	/**
	 *  Resolve a referenceable element (e.g. in trigger).
	 */
	protected static class ExpressionParser implements IValueConverter
	{
		// Hack!!! Should be configurable.
		protected static IExpressionParser	exp_parser	= new JavaCCExpressionParser();
	
		public boolean	isTwoPass()
		{
			return true;
		}

		public Object convertValue(IOAVState state, List stack, OAVAttributeType attribute, String value, MultiCollection report)
		{
//			System.out.println("convert value: "+value);
			Object ret = null;
			
			if(value!=null)
			{
				StackElement se = (StackElement)stack.get(stack.size()-1);
				
				String lang = (String)state.getAttributeValue(se.object, OAVBDIMetaModel.expression_has_language);
				
				if(state.getType(se.object).isSubtype(condition_type))
				{
//					System.out.println("Found condition: "+se.object);

					if(lang==null || "clips".equals(lang))
					{
						List	errors	= new ArrayList();
						try
						{
							ret = ParserHelper.parseCondition(value, state.getTypeModel(), errors);
						}
						catch(Exception e)
						{
							report.put(se, e.toString());
//							e.printStackTrace();
						}
						if(!errors.isEmpty())
						{
							for(int i=0; i<errors.size(); i++)
							{
								report.put(se, errors.get(i));
							}
						}
					}
					else
					{
						report.put(se, "Unknown condition language: "+lang);
//						throw new RuntimeException("Unknown condition language: "+lang);
					}	
//					System.out.println(ret);
					
				}
				else
				{
//					System.out.println("Found expression: "+se.object);
					if(lang==null || "java".equals(lang))
					{
						try
						{
							ret = exp_parser.parseExpression(value, ClassValueConverter.getImports(state, stack), null, state.getTypeModel().getClassLoader());
						}
						catch(Exception e)
						{
							report.put(se, e.toString());
//							e.printStackTrace();
						}
						
//						// Trigger reference has expression and referenced element
//						if(state.getType(se.object).equals(triggerreference_type))
//						{
//							Collection	triggerelems	= null;
//							Object trigger = ((StackElement)stack.get(stack.size()-3)).object;
//							if(((StackElement)stack.get(stack.size()-2)).path.endsWith("goalfinished"))
//							{
//								triggerelems	= state.getAttributeValues(trigger, trigger_has_goalfinisheds);
//							}
//							else if(((StackElement)stack.get(stack.size()-2)).path.endsWith("internalevent"))
//							{
//								triggerelems	= state.getAttributeValues(trigger, trigger_has_internalevents);
//							}
//							else if(((StackElement)stack.get(stack.size()-2)).path.endsWith("messageevent"))
//							{
//								triggerelems	= state.getAttributeValues(trigger, trigger_has_messageevents);
//							}
//							else if(((StackElement)stack.get(stack.size()-2)).path.endsWith("goal"))
//							{
//								triggerelems	= state.getAttributeValues(trigger, plantrigger_has_goals);
//							}
//							
//							// Find last element (hack???)
//							Object	triggerelem	= null;
//							if(triggerelems!=null)
//								for(Iterator it=triggerelems.iterator(); it.hasNext();)
//									triggerelem	= it.next();
//
//							state.setAttributeValue(se.object, triggerreference_has_ref, triggerelem);
//						}
					}
					else if("clips".equals(lang))
					{
						List	errors	= new ArrayList();
						try
						{
							ret = ParserHelper.parseCondition(value, state.getTypeModel(), errors);
						}
						catch(Exception e)
						{
							report.put(se, e.toString());
//							e.printStackTrace();
						}
						if(!errors.isEmpty())
						{
							for(int i=0; i<errors.size(); i++)
							{
								report.put(se, errors.get(i));
							}
						}
					}
					else
					{
						report.put(se, "Unknown condition language: "+lang);
//						throw new RuntimeException("Unknown condition language: "+lang);
					}
				}
			}
			
			return ret;
		}
	}

	/**
	 *  Resolve a referenceable element (e.g. in trigger).
	 */
	protected static class ElementResolver implements IValueConverter
	{
		public boolean	isTwoPass()
		{
			return true;
		}

		public Object convertValue(IOAVState state, List stack, OAVAttributeType attribute, String value, MultiCollection report)
		{
			Object	ret	= null;
			
			// Find capability object in stack.
			Object	capa	= null;
			for(int i=stack.size()-1; capa==null && i>=0; i--)
			{
				Object	obj	= ((StackElement)stack.get(i)).object;
				if(obj!=null && state.getType(obj).isSubtype(OAVBDIMetaModel.capability_type))
					capa	= obj;
			}
			if(capa==null)
				throw new RuntimeException("No capability on stack: "+stack);
			
			String	referrer	= ((StackElement)stack.get(stack.size()-1)).path;
			referrer	= referrer.substring(referrer.lastIndexOf("/")+1);
			
			// Find collection of elements.
			Collection	coll	= null;
			
			// Capabilities.
			if(referrer.equals("initialcapability"))
			{
				coll	= state.getAttributeValues(capa, OAVBDIMetaModel.capability_has_capabilityrefs); 
			}

			// Beliefs or Beliefsets
			else if(referrer.equals("factchanged"))
			{
				Collection	coll1	= state.getAttributeValues(capa, OAVBDIMetaModel.capability_has_beliefs); 
				Collection	coll2	= state.getAttributeValues(capa, OAVBDIMetaModel.capability_has_beliefsets);
				if(coll1==null)
				{
					coll	= coll2;
				}
				else if(coll2==null)
				{
					coll	= coll1;
				}
				
				if(coll1!=null && coll2!=null)
				{
					coll	= new ArrayList();
					coll.addAll(coll1);
					coll.addAll(coll2);
				}
			}

			// Beliefs.
			else if(referrer.equals("initialbelief"))
			{
				coll	= state.getAttributeValues(capa, OAVBDIMetaModel.capability_has_beliefs); 
			}

			// Beliefsets.
			else if(referrer.equals("initialbeliefset")
				|| referrer.equals("factadded") || referrer.equals("factremoved"))
			{
				coll	= state.getAttributeValues(capa, OAVBDIMetaModel.capability_has_beliefsets); 
			}
			
			// Goals.
			else if(referrer.equals("initialgoal") || referrer.equals("endgoal")
				|| referrer.equals("goal") || referrer.equals("goalfinished")
				|| referrer.equals("inhibits"))
			{
				coll	= state.getAttributeValues(capa, OAVBDIMetaModel.capability_has_goals); 
			}

			// Plans.
			else if(referrer.equals("initialplan") || referrer.equals("endplan"))
			{
				coll	= state.getAttributeValues(capa, OAVBDIMetaModel.capability_has_plans); 
			}

			// Events.
			else if(referrer.equals("initialmessageevent") 
				|| referrer.equals("endmessageevent") 
				|| referrer.equals("messageevent"))
			{
				coll	= state.getAttributeValues(capa, OAVBDIMetaModel.capability_has_messageevents); 					
			}
			
			// Events.
			else if(referrer.equals("initialinternalevent")
				|| referrer.equals("endinternalevent")
				|| referrer.equals("internalevent"))
			{
				coll	= state.getAttributeValues(capa, OAVBDIMetaModel.capability_has_internalevents); 					
			}

			// Configurations.
			else if(referrer.equals("configurations"))
			{
				coll	= state.getAttributeValues(capa, OAVBDIMetaModel.capability_has_configurations); 					
			}
			
			else
			{
				throw new RuntimeException("Unknown trigger reference: "+referrer);
			}

			if(coll==null)
			{
				throw new RuntimeException("Could not resolve reference to "+referrer+": "+value);
			}

			for(Iterator it=coll.iterator(); ret==null && it.hasNext();)
			{
				Object	obj	= it.next();
				if(value.equals(state.getAttributeValue(obj, OAVBDIMetaModel.modelelement_has_name)))
				{
					ret	= obj;
				}	
			}
			
			if(ret==null)
			{
				report.put(stack.get(stack.size()-1), "Could not resolve reference to: "+value);
//				throw new RuntimeException("Could not resolve reference to "+referrer+": "+value);
			}
			
			return ret;
		}
	}

	/**
	 *  Resolve an excluded parameter or parameter set in goal unique settings.
	 */
	protected static class ExcludeResolver implements IValueConverter
	{
		public boolean	isTwoPass()
		{
			return true;
		}

		public Object convertValue(IOAVState state, List stack, OAVAttributeType attribute, String value, MultiCollection report)
		{
			Object	ret	= null;
			
			// Find parameter element in stack.
			Object	pe	= null;
			for(int i=stack.size()-1; pe==null && i>=0; i--)
			{
				Object	obj	= ((StackElement)stack.get(i)).object;
				if(obj!=null && state.getType(obj).isSubtype(OAVBDIMetaModel.parameterelement_type))
					pe	= obj;
			}
			if(pe==null)
				throw new RuntimeException("No parameter element on stack: "+stack);
			
			// Try to find parameter. 
			Collection	coll	= state.getAttributeValues(pe, OAVBDIMetaModel.parameterelement_has_parameters);
			if(coll!=null)
			{
				for(Iterator it=coll.iterator(); ret==null && it.hasNext();)
				{
					Object	obj	= it.next();
					if(value.equals(state.getAttributeValue(obj, OAVBDIMetaModel.modelelement_has_name)))
					{
						ret	= obj;
					}	
				}
			}
			
			// Try to find parameter set
			if(ret==null)
			{
				coll	= state.getAttributeValues(pe, OAVBDIMetaModel.parameterelement_has_parametersets);
				if(coll!=null)
				{
					for(Iterator it=coll.iterator(); ret==null && it.hasNext();)
					{
						Object	obj	= it.next();
						if(value.equals(state.getAttributeValue(obj, OAVBDIMetaModel.modelelement_has_name)))
						{
							ret	= obj;
						}	
					}
				}
			}
			
			if(ret==null)
			{
				report.put(stack.get(stack.size()-1), "Could not resolve parameter: "+value);
//				throw new RuntimeException("Could not resolve parameter: "+value);
			}
			
			return ret;
		}
	}

	/**
	 *  Resolve a parameter (set) of a config element to the original paremeter (set).
	 */
	protected static class ParameterResolver implements IValueConverter
	{
		public boolean	isTwoPass()
		{
			return true;
		}

		public Object convertValue(IOAVState state, List stack, OAVAttributeType attribute, String value, MultiCollection report)
		{
			Object	ret	= null;
			
			// Find config element in stack.
			Object	ce	= null;
			for(int i=stack.size()-1; ce==null && i>=0; i--)
			{
				Object	obj	= ((StackElement)stack.get(i)).object;
				if(obj!=null && state.getType(obj).isSubtype(OAVBDIMetaModel.configelement_type))
					ce	= obj;
			}
			if(ce==null)
				throw new RuntimeException("No config element on stack: "+stack);
			
			String	referrer	= ((StackElement)stack.get(stack.size()-1)).path;
			referrer	= referrer.substring(referrer.lastIndexOf("/")+1);
			
			// Find collection of elements.
			Collection	coll;
			
			// Parameter.
			if(referrer.equals("parameter"))
			{
				// Todo: find element for config ref
				Object	orig	= state.getAttributeValue(ce, OAVBDIMetaModel.configelement_has_ref);
				if(state.getType(orig).isSubtype(OAVBDIMetaModel.parameterelement_type))
					coll	= state.getAttributeValues(orig, parameterelement_has_parameters);
//				else if(state.getType(orig).isSubtype(OAVBDIMetaModel.parameterelementreference_type))
//					coll	= state.getAttributeValues(orig, parameterelementreference_has_parameterrefs);
				else
					throw new RuntimeException("Unexpected object type: "+state.getType(orig));
			}

			// Parameter set.
			else if(referrer.equals("parameterset"))
			{
				// Todo: find element for config ref
				Object	orig	= state.getAttributeValue(ce, OAVBDIMetaModel.configelement_has_ref);
				if(state.getType(orig).isSubtype(OAVBDIMetaModel.parameterelement_type))
					coll	= state.getAttributeValues(orig, parameterelement_has_parametersets);
//				else if(state.getType(orig).isSubtype(OAVBDIMetaModel.parameterelementreference_type))
//					coll	= state.getAttributeValues(orig, parameterelementreference_has_parametersetrefs);
				else
					throw new RuntimeException("Unexpected object type: "+state.getType(orig));
			}

			else
			{
				throw new RuntimeException("Unknown parameter reference: "+referrer);
			}

			if(coll==null)
			{
				report.put(stack.get(stack.size()-1), "Could not resolve reference to: "+value);
//				throw new RuntimeException("Could not resolve reference to "+referrer+": "+value+", "+stack);
			}

			for(Iterator it=coll.iterator(); ret==null && it.hasNext();)
			{
				Object	obj	= it.next();
				if(value.equals(state.getAttributeValue(obj, OAVBDIMetaModel.modelelement_has_name)))
				{
					ret	= obj;
				}	
			}
			
			if(ret==null)
			{
				report.put(stack.get(stack.size()-1), "Could not resolve reference to: "+value);
//				throw new RuntimeException("Could not resolve reference to "+referrer+": "+value);
			}
			
			return ret;
		}
	}
	
	/**
	 *  Resolve a parameter (set) of a mapping element (goal, internal, message) 
	 *  to the original paremeter (set).
	 */
	protected static class MappingResolver implements IValueConverter
	{
		public boolean	isTwoPass()
		{
			return true;
		}

		public Object convertValue(IOAVState state, List stack, OAVAttributeType attribute, String value, MultiCollection report)
		{
			Object	ret	= null;
			
			// Find capability object in stack.
			Object	capa	= null;
			for(int i=stack.size()-1; capa==null && i>=0; i--)
			{
				Object	obj	= ((StackElement)stack.get(i)).object;
				if(obj!=null && state.getType(obj).isSubtype(OAVBDIMetaModel.capability_type))
					capa	= obj;
			}
			if(capa==null)
				throw new RuntimeException("No capability on stack: "+stack);
			
			String	referrer	= ((StackElement)stack.get(stack.size()-1)).path;
			referrer	= referrer.substring(referrer.lastIndexOf("/")+1);
			
			// Find collection of elements.
			Collection	coll = null;
			
			int pidx = value.indexOf('.');
			String elemname = value.substring(0, pidx);
			String paramname = value.substring(pidx+1);
			
			Object orig = null;
			
			// Goal mapping.
			if(referrer.equals("goalmapping"))
			{
				orig = state.getAttributeValue(capa, OAVBDIMetaModel.capability_has_goals, elemname);
			}

			// Internal event mapping.
			else if(referrer.equals("internaleventmapping"))
			{
				orig = state.getAttributeValue(capa, OAVBDIMetaModel.capability_has_internalevents, elemname);
			}
			
			// Message event mapping.
			else if(referrer.equals("messageeventmapping"))
			{
				orig = state.getAttributeValue(capa, OAVBDIMetaModel.capability_has_messageevents, elemname);
			}
			
			else
			{
				throw new RuntimeException("Unknown mapping reference: "+referrer);
			}
			
			if(orig==null)
				report.put(stack.get(stack.size()-1), "Unknown referenced element: "+elemname);
//				throw new RuntimeException("Unknown referenced element: "+elemname);
				
			OAVObjectType otype = state.getType(orig);
			boolean	set	= ((StackElement)stack.get(stack.size()-2)).path.endsWith("parameterset");
			
			if(state.getType(orig).isSubtype(OAVBDIMetaModel.parameterelement_type))
			{
				coll = set? state.getAttributeValues(orig, parameterelement_has_parametersets)
					: state.getAttributeValues(orig, parameterelement_has_parameters);
			}
//			else if(state.getType(orig).isSubtype(OAVBDIMetaModel.parameterelementreference_type))
//			{
//				coll = set? state.getAttributeValues(orig, parameterelementreference_has_parametersetrefs)
//					: state.getAttributeValues(orig, parameterelementreference_has_parameterrefs);
//			}
			else
			{
				throw new RuntimeException("Unexpected object type: "+state.getType(orig));
			}
			
			
//			if(coll==null)
//				throw new RuntimeException("Could not resolve reference to "+referrer+": "+value+", "+stack);

			if(coll!=null)
			{
				for(Iterator it=coll.iterator(); ret==null && it.hasNext();)
				{
					Object	obj	= it.next();
					if(paramname.equals(state.getAttributeValue(obj, OAVBDIMetaModel.modelelement_has_name)))
					{
						ret	= obj;
					}	
				}
			}
			
			if(ret==null)
			{
				// Special case message events with implicit parameters
				if(otype.equals(OAVBDIMetaModel.messageevent_type))
				{
					// Create referenced parameter on demand if in metamodel.
					MessageType mtype = (MessageType)state.getAttributeValue(orig, OAVBDIMetaModel.messageevent_has_type);
//					MessageType mtype = Configuration.getConfiguration().getMessageType(type);
					ParameterSpecification ps = mtype.getParameter(paramname);
					if(ps==null)
						throw new RuntimeException("Unknown message event parameter "+referrer+": "+value+" "+paramname);
					if(!ps.isSet())
					{
						ret = state.createObject(OAVBDIMetaModel.parameter_type);
						state.setAttributeValue(ret, OAVBDIMetaModel.modelelement_has_name, paramname);
						state.addAttributeValue(orig, OAVBDIMetaModel.parameterelement_has_parameters, ret);
					}
					else
					{
						ret = state.createObject(OAVBDIMetaModel.parameterset_type);
						state.setAttributeValue(ret, OAVBDIMetaModel.modelelement_has_name, paramname);						
						state.addAttributeValue(orig, OAVBDIMetaModel.parameterelement_has_parametersets, ret);
					}
				}
			}
			
			if(ret==null)
				report.put(stack.get(stack.size()-1), "Could not resolve reference to: "+value);
//				throw new RuntimeException("Could not resolve reference to "+referrer+": "+value);
			
			return ret;
		}
	}
	
	/**
	 *  Resolve a message type name to a message type.
	 */
	protected static class MessageTypeResolver implements IValueConverter
	{	
		protected Map messagetypes;
		
		public MessageTypeResolver(Map props)
		{
			this.messagetypes = new HashMap();
			// todo:
			
			Iterator it = props.keySet().iterator();
			while(it.hasNext())
			{
				String key = (String)it.next();
				if(key.startsWith("messagetype"))
				{
					String tmp = key.substring(12);
//					System.out.println("MT:"+tmp);
					messagetypes.put(tmp, props.get(key));
				}
			}
			
			/*Property[] mtypes = props.getProperties("messagetype");
			for(int i=0; i<mtypes.length; i++)
			{
				messagetypes.put(mtypes[i].getName(), mtypes[i].getJavaObject(null));
			}*/
		}
		
		public boolean	isTwoPass()
		{
			return false;
		}

		public Object convertValue(IOAVState state, List stack, OAVAttributeType attribute, String value, MultiCollection report)
		{
			Object	ret	= messagetypes.get(value);
			if(ret==null)
				report.put(stack.get(stack.size()-1), "Unknown message type: "+value);
//				throw new RuntimeException("Could not resolve: "+attribute+" "+value);
			
			return ret;
		}
	}
}
