package jadex.bdi.interpreter;

import jadex.commons.SReflect;
import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.IPostProcessor;
import jadex.commons.xml.ITypeConverter;
import jadex.commons.xml.Namespace;
import jadex.commons.xml.SubobjectInfo;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.bean.IBeanObjectCreator;
import jadex.commons.xml.reader.Reader;
import jadex.commons.xml.writer.Writer;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.rules.parser.conditions.ParserHelper;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.io.xml.OAVObjectReaderHandler;
import jadex.rules.state.io.xml.OAVObjectWriterHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.xml.QName;

/**
 *  Reader for loading BDI XML models into OAV states.
 */
public class OAVBDIXMLReader
{
	//-------- attributes --------
	
	/** The singleton reader instance. */
	protected static Reader reader;
	protected static Writer writer;
	
	// Initialize reader instance.
	static
	{
		// Post processors.
		IPostProcessor expost = new ExpressionProcessor();
		IPostProcessor tepost = new ClassPostProcessor(OAVBDIMetaModel.typedelement_has_classname, OAVBDIMetaModel.typedelement_has_class); 
//		IPostProcessor bopost = new ClassPostProcessor(OAVBDIMetaModel.body_has_classname, OAVBDIMetaModel.body_has_class); 
		ITypeConverter exconv = new ExpressionToStringConverter();
		
		Set typeinfos = new HashSet();

//		typeinfos.add(new TypeInfo("import", OAVJavaType.java_string_type));

		typeinfos.add(new TypeInfo(null, "capabilities/capability", OAVBDIMetaModel.capabilityref_type));
		
		TypeInfo ti_performgoal = new TypeInfo(null, "performgoal", OAVBDIMetaModel.performgoal_type);
		TypeInfo ti_performgoalref = new TypeInfo(null, "performgoalref", OAVBDIMetaModel.goalreference_type);
		TypeInfo ti_achievegoal = new TypeInfo(null, "achievegoal", OAVBDIMetaModel.achievegoal_type);
		TypeInfo ti_achievegoalref = new TypeInfo(null, "achievegoalref", OAVBDIMetaModel.goalreference_type);
		TypeInfo ti_querygoal = new TypeInfo(null, "querygoal", OAVBDIMetaModel.querygoal_type);
		TypeInfo ti_querygoalref = new TypeInfo(null, "querygoalref", OAVBDIMetaModel.goalreference_type);
		TypeInfo ti_maintaingoal = new TypeInfo(null, "maintaingoal", OAVBDIMetaModel.maintaingoal_type);
		TypeInfo ti_maintaingoalref = new TypeInfo(null, "maintaingoalref", OAVBDIMetaModel.goalreference_type);
		TypeInfo ti_metagoal = new TypeInfo(null, "metagoal", OAVBDIMetaModel.metagoal_type);
		TypeInfo ti_metagoalref = new TypeInfo(null, "metagoalref", OAVBDIMetaModel.goalreference_type);
		typeinfos.add(ti_performgoal);
		typeinfos.add(ti_performgoalref);
		typeinfos.add(ti_achievegoal);
		typeinfos.add(ti_achievegoalref);
		typeinfos.add(ti_querygoal);
		typeinfos.add(ti_querygoalref);
		typeinfos.add(ti_maintaingoal);
		typeinfos.add(ti_maintaingoalref);
		typeinfos.add(ti_metagoal);
		typeinfos.add(ti_metagoalref);
		typeinfos.add(new TypeInfo(null, "creationcondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo(null, "dropcondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo(null, "targetcondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo(null, "maintaincondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo(null, "recurcondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo(null, "metagoal/trigger", OAVBDIMetaModel.metagoaltrigger_type));
		typeinfos.add(new TypeInfo(null, "inhibits", OAVBDIMetaModel.inhibits_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo(null, "deliberation", null));
		typeinfos.add(new TypeInfo(null, "unique", new IBeanObjectCreator()
		{
			public Object createObject(Object context, Map rawattributes,
				ClassLoader classloader) throws Exception
			{
				return Boolean.TRUE;
			}
		}));
		
		TypeInfo ti_capability = new TypeInfo(null, "capability", OAVBDIMetaModel.capability_type, OAVBDIMetaModel.modelelement_has_description, null, 
			new AttributeInfo[]{
			new AttributeInfo(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"), null, AttributeInfo.IGNORE_READWRITE)}, null, null, 
			new SubobjectInfo[]{
			new SubobjectInfo("imports", new AttributeInfo("import", OAVBDIMetaModel.capability_has_imports)),
			
			new SubobjectInfo("beliefs", new AttributeInfo("beliefref", OAVBDIMetaModel.capability_has_beliefrefs)),
			new SubobjectInfo("beliefs", new AttributeInfo("beliefsetref", OAVBDIMetaModel.capability_has_beliefsetrefs)),
			new SubobjectInfo("beliefs", new AttributeInfo("belief", OAVBDIMetaModel.capability_has_beliefs)),
			new SubobjectInfo("beliefs", new AttributeInfo("beliefset", OAVBDIMetaModel.capability_has_beliefsets)),

			new SubobjectInfo("goals", new AttributeInfo("performgoal", OAVBDIMetaModel.capability_has_goals), null, ti_performgoal),
			new SubobjectInfo("goals", new AttributeInfo("achievegoal", OAVBDIMetaModel.capability_has_goals), null, ti_achievegoal),
			new SubobjectInfo("goals", new AttributeInfo("querygoal", OAVBDIMetaModel.capability_has_goals), null, ti_querygoal),
			new SubobjectInfo("goals", new AttributeInfo("maintaingoal", OAVBDIMetaModel.capability_has_goals), null, ti_maintaingoal),
			new SubobjectInfo("goals", new AttributeInfo("metagoal", OAVBDIMetaModel.capability_has_goals), null, ti_metagoal),
			new SubobjectInfo("goals", new AttributeInfo("performgoalref", OAVBDIMetaModel.capability_has_goalrefs), null, ti_performgoalref),
			new SubobjectInfo("goals", new AttributeInfo("achievegoalref", OAVBDIMetaModel.capability_has_goalrefs), null, ti_achievegoalref),
			new SubobjectInfo("goals", new AttributeInfo("querygoalref", OAVBDIMetaModel.capability_has_goalrefs), null, ti_querygoalref),
			new SubobjectInfo("goals", new AttributeInfo("maintaingoalref", OAVBDIMetaModel.capability_has_goalrefs), null, ti_maintaingoalref),
			new SubobjectInfo("goals", new AttributeInfo("metagoalref", OAVBDIMetaModel.capability_has_goalrefs), null, ti_metagoalref),

			new SubobjectInfo("plans", new AttributeInfo("plan", OAVBDIMetaModel.capability_has_plans)),

			new SubobjectInfo("events", new AttributeInfo("messageeventref", OAVBDIMetaModel.capability_has_messageeventrefs)),
			new SubobjectInfo("events", new AttributeInfo("internaleventref", OAVBDIMetaModel.capability_has_internaleventrefs)),

			new SubobjectInfo("properties", new AttributeInfo("property", OAVBDIMetaModel.capability_has_properties)),

			new SubobjectInfo("configurations", new AttributeInfo("configuration", OAVBDIMetaModel.capability_has_configurations)),
			}
		);
		typeinfos.add(ti_capability);
		
		typeinfos.add(new TypeInfo(ti_capability, "agent", OAVBDIMetaModel.agent_type, OAVBDIMetaModel.modelelement_has_description, null));
				
		typeinfos.add(new TypeInfo(null, "belief", OAVBDIMetaModel.belief_type, null, null, 
			new AttributeInfo[]{
			new AttributeInfo("class", OAVBDIMetaModel.typedelement_has_classname),
			new AttributeInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AttributeInfo.IGNORE_WRITE)
			}, tepost));
		typeinfos.add(new TypeInfo(null, "beliefref", OAVBDIMetaModel.beliefreference_type));
		
		TypeInfo ti_belset = new TypeInfo(null, "beliefset", OAVBDIMetaModel.beliefset_type, null, null, 
			new AttributeInfo[]{
			new AttributeInfo("class", OAVBDIMetaModel.typedelement_has_classname),
			new AttributeInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AttributeInfo.IGNORE_WRITE)
			}, tepost, null, 
			new SubobjectInfo[]{
			new SubobjectInfo(new AttributeInfo("facts", OAVBDIMetaModel.beliefset_has_factsexpression)),
			new SubobjectInfo(new AttributeInfo("fact", OAVBDIMetaModel.beliefset_has_facts))
			});
		typeinfos.add(ti_belset);
		typeinfos.add(new TypeInfo(null, "beliefsetref", OAVBDIMetaModel.beliefsetreference_type));
		typeinfos.add(new TypeInfo(null, "fact", OAVBDIMetaModel.expression_type, null, 
			new AttributeInfo((String)null, OAVBDIMetaModel.expression_has_content, null, null, exconv), null, expost));
		typeinfos.add(new TypeInfo(null, "facts", OAVBDIMetaModel.expression_type, null, 
			new AttributeInfo((String)null, OAVBDIMetaModel.expression_has_content, null, null, exconv), null, expost));
		
		typeinfos.add(new TypeInfo(null, "plan", OAVBDIMetaModel.plan_type, OAVBDIMetaModel.modelelement_has_description, null, null, null, null, 
			new SubobjectInfo[]{
			new SubobjectInfo(new AttributeInfo("parameter", OAVBDIMetaModel.parameterelement_has_parameters)),	
			new SubobjectInfo(new AttributeInfo("parameterset", OAVBDIMetaModel.parameterelement_has_parametersets))	
		}));
		
		typeinfos.add(new TypeInfo(null, "body", OAVBDIMetaModel.body_type, null, null, new AttributeInfo[]{
			new AttributeInfo("class", OAVBDIMetaModel.body_has_impl, AttributeInfo.IGNORE_WRITE), 
			new AttributeInfo("impl", OAVBDIMetaModel.body_has_impl)}, null));//, bopost));
		typeinfos.add(new TypeInfo(null, "precondition", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo(null, "contextcondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		
		typeinfos.add(new TypeInfo(null, "trigger", OAVBDIMetaModel.plantrigger_type, null, null,
			null, null, null, 
			new SubobjectInfo[]{
			new SubobjectInfo(new AttributeInfo("internalevent", OAVBDIMetaModel.trigger_has_internalevents)),
			new SubobjectInfo(new AttributeInfo("messageevent", OAVBDIMetaModel.trigger_has_messageevents)),
			new SubobjectInfo(new AttributeInfo("goalfinished", OAVBDIMetaModel.trigger_has_goalfinisheds)),
			new SubobjectInfo(new AttributeInfo("factadded", OAVBDIMetaModel.trigger_has_factaddeds)),
			new SubobjectInfo(new AttributeInfo("factremoved", OAVBDIMetaModel.trigger_has_factremoveds)),
			new SubobjectInfo(new AttributeInfo("factchanged", OAVBDIMetaModel.trigger_has_factchangeds)),
			new SubobjectInfo(new AttributeInfo("goal", OAVBDIMetaModel.plantrigger_has_goals)),
			new SubobjectInfo(new AttributeInfo("condition", OAVBDIMetaModel.plantrigger_has_condition))
			}));
		
		typeinfos.add(new TypeInfo(null, "trigger/internalevent", OAVBDIMetaModel.triggerreference_type));
		typeinfos.add(new TypeInfo(null, "trigger/messageevent", OAVBDIMetaModel.triggerreference_type));
		typeinfos.add(new TypeInfo(null, "trigger/goalfinished", OAVBDIMetaModel.triggerreference_type));
		typeinfos.add(new TypeInfo(null, "trigger/goal", OAVBDIMetaModel.triggerreference_type));
//		typeinfos.add(new TypeInfo(null, "trigger/factadded", OAVJavaType.java_string_type));
//		typeinfos.add(new TypeInfo(null, "trigger/factremoved", OAVJavaType.java_string_type));
//		typeinfos.add(new TypeInfo(null, "trigger/factchanged", OAVJavaType.java_string_type));
		typeinfos.add(new TypeInfo(null, "waitqueue", OAVBDIMetaModel.trigger_type));
		typeinfos.add(new TypeInfo(null, "waitqueue/internalevent", OAVBDIMetaModel.triggerreference_type));
		typeinfos.add(new TypeInfo(null, "waitqueue/messageevent", OAVBDIMetaModel.triggerreference_type));
		typeinfos.add(new TypeInfo(null, "waitqueue/goalfinished", OAVBDIMetaModel.triggerreference_type));
		
		typeinfos.add(new TypeInfo(null, "internalevent", OAVBDIMetaModel.internalevent_type));
		typeinfos.add(new TypeInfo(null, "internaleventref", OAVBDIMetaModel.internaleventreference_type));
		typeinfos.add(new TypeInfo(null, "messageevent", OAVBDIMetaModel.messageevent_type));
		typeinfos.add(new TypeInfo(null, "messageeventref", OAVBDIMetaModel.messageeventreference_type));
		typeinfos.add(new TypeInfo(null, "match", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		
		typeinfos.add(new TypeInfo(null, "expression", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
//		typeinfos.add(new TypeInfo(null, "expression/parameter", OAVBDIMetaModel.expressionparameter_type));
		typeinfos.add(new TypeInfo(null, "condition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		
		typeinfos.add(new TypeInfo(null, "property", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		
		typeinfos.add(new TypeInfo(null, "parameter", OAVBDIMetaModel.parameter_type, null, null, 
			new AttributeInfo[]{
			new AttributeInfo("class", OAVBDIMetaModel.typedelement_has_classname),
			new AttributeInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AttributeInfo.IGNORE_WRITE)
			}, tepost));
		
		TypeInfo ti_paramset = new TypeInfo(null, "parameterset", OAVBDIMetaModel.parameterset_type, null, null, 
			new AttributeInfo[]{
			new AttributeInfo("class", OAVBDIMetaModel.typedelement_has_classname),
			new AttributeInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AttributeInfo.IGNORE_WRITE)
			}, tepost, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new AttributeInfo("values", OAVBDIMetaModel.parameterset_has_valuesexpression)),	
			new SubobjectInfo(new AttributeInfo("value", OAVBDIMetaModel.parameterset_has_values))	
			});
		typeinfos.add(ti_paramset);
		
		typeinfos.add(new TypeInfo(null, "plan/parameter", OAVBDIMetaModel.planparameter_type, null, null, 
			new AttributeInfo[]{
			new AttributeInfo("class", OAVBDIMetaModel.typedelement_has_classname),
			new AttributeInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AttributeInfo.IGNORE_WRITE)
			}, tepost));
		typeinfos.add(new TypeInfo(null, "plan/parameterset", OAVBDIMetaModel.planparameterset_type, null, null, 
			new AttributeInfo[]{
			new AttributeInfo("class", OAVBDIMetaModel.typedelement_has_classname),
			new AttributeInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AttributeInfo.IGNORE_WRITE)
			}, tepost, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new AttributeInfo("values", OAVBDIMetaModel.parameterset_has_valuesexpression)),	
			new SubobjectInfo(new AttributeInfo("value", OAVBDIMetaModel.parameterset_has_values))	
			}));
		typeinfos.add(new TypeInfo(null, "value", OAVBDIMetaModel.expression_type, null, 
			new AttributeInfo((String)null, OAVBDIMetaModel.expression_has_content, null, null, exconv), null, expost));
		typeinfos.add(new TypeInfo(null, "values", OAVBDIMetaModel.expression_type, null, 
			new AttributeInfo((String)null, OAVBDIMetaModel.expression_has_content, null, null, exconv), null, expost));
//		typeinfos.add(new TypeInfo(null, "goalmapping", OAVJavaType.java_string_type));
//		typeinfos.add(new TypeInfo(null, "messageeventmapping", OAVJavaType.java_string_type));
//		typeinfos.add(new TypeInfo(null, "internaleventmapping", OAVJavaType.java_string_type));
		typeinfos.add(new TypeInfo(null, "bindingoptions", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
				
//		typeinfos.add(new TypeInfo(null, "concrete", OAVJavaType.java_string_type));

		typeinfos.add(new TypeInfo(null, "configurations", null, null, null, 
			new AttributeInfo[]{
			new AttributeInfo("default", OAVBDIMetaModel.capability_has_defaultconfiguration)
			}, null));
		typeinfos.add(new TypeInfo(null, "configuration", OAVBDIMetaModel.configuration_type, null, null, null, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo("beliefs", new AttributeInfo("initialbelief", OAVBDIMetaModel.configuration_has_initialbeliefs)),
			new SubobjectInfo("beliefs", new AttributeInfo("initialbeliefset", OAVBDIMetaModel.configuration_has_initialbeliefsets)),
			new SubobjectInfo("goals", new AttributeInfo("initialgoal", OAVBDIMetaModel.configuration_has_initialgoals)),
			new SubobjectInfo("goals", new AttributeInfo("endgoal", OAVBDIMetaModel.configuration_has_endgoals)),
			new SubobjectInfo("plans", new AttributeInfo("initialplan", OAVBDIMetaModel.configuration_has_initialplans)),
			new SubobjectInfo("plans", new AttributeInfo("endplan", OAVBDIMetaModel.configuration_has_endplans)),
			new SubobjectInfo("events", new AttributeInfo("initialinternalevent", OAVBDIMetaModel.configuration_has_initialinternalevents)),
			new SubobjectInfo("events", new AttributeInfo("initialmessageevent", OAVBDIMetaModel.configuration_has_initialmessageevents)),
			new SubobjectInfo("events", new AttributeInfo("endinternalevent", OAVBDIMetaModel.configuration_has_endinternalevents)),
			new SubobjectInfo("events", new AttributeInfo("endmessageevent", OAVBDIMetaModel.configuration_has_endmessageevents))
			}));
		typeinfos.add(new TypeInfo(null, "initialcapability", OAVBDIMetaModel.initialcapability_type));
		typeinfos.add(new TypeInfo(null, "initialbelief", OAVBDIMetaModel.configbelief_type));
		typeinfos.add(new TypeInfo(ti_belset, "initialbeliefset", OAVBDIMetaModel.configbeliefset_type));
		typeinfos.add(new TypeInfo(null, "initialgoal", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new TypeInfo(null, "initialplan", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new TypeInfo(null, "initialinternalevent", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new TypeInfo(null, "initialmessageevent", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new TypeInfo(null, "endgoal", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new TypeInfo(null, "endplan", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new TypeInfo(null, "endinternalevent", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new TypeInfo(null, "endmessageevent", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new TypeInfo(null, "initialgoal/parameter", OAVBDIMetaModel.configparameter_type));
		typeinfos.add(new TypeInfo(ti_paramset, "initialgoal/parameterset", OAVBDIMetaModel.configparameterset_type));
		typeinfos.add(new TypeInfo(null, "initialplan/parameter", OAVBDIMetaModel.configparameter_type));
		typeinfos.add(new TypeInfo(ti_paramset, "initialplan/parameterset", OAVBDIMetaModel.configparameterset_type));
		typeinfos.add(new TypeInfo(null, "initialinternalevent/parameter", OAVBDIMetaModel.configparameter_type));
		typeinfos.add(new TypeInfo(ti_paramset, "initialinternalevent/parameterset", OAVBDIMetaModel.configparameterset_type));
		typeinfos.add(new TypeInfo(null, "initialmessageevent/parameter", OAVBDIMetaModel.configparameter_type));
		typeinfos.add(new TypeInfo(ti_paramset, "initialmessageevent/parameterset", OAVBDIMetaModel.configparameterset_type));
		typeinfos.add(new TypeInfo(null, "endgoal/parameter", OAVBDIMetaModel.configparameter_type));
		typeinfos.add(new TypeInfo(ti_paramset, "endgoal/parameterset", OAVBDIMetaModel.configparameterset_type));
		typeinfos.add(new TypeInfo(null, "endplan/parameter", OAVBDIMetaModel.configparameter_type));
		typeinfos.add(new TypeInfo(ti_paramset, "endplan/parameterset", OAVBDIMetaModel.configparameterset_type));
		typeinfos.add(new TypeInfo(null, "endinternalevent/parameter", OAVBDIMetaModel.configparameter_type));
		typeinfos.add(new TypeInfo(ti_paramset, "endinternalevent/parameterset", OAVBDIMetaModel.configparameterset_type));
		typeinfos.add(new TypeInfo(null, "endmessageevent/parameter", OAVBDIMetaModel.configparameter_type));
		typeinfos.add(new TypeInfo(ti_paramset, "endmessageevent/parameterset", OAVBDIMetaModel.configparameterset_type));

//		Set linkinfos = new HashSet();
//		linkinfos.add(new LinkInfo("properties/property", OAVBDIMetaModel.capability_has_properties));
//		linkinfos.add(new LinkInfo("goals/performgoal", OAVBDIMetaModel.capability_has_goals));
//		linkinfos.add(new LinkInfo("goals/achievegoal", OAVBDIMetaModel.capability_has_goals));
//		linkinfos.add(new LinkInfo("goals/querygoal", OAVBDIMetaModel.capability_has_goals));
//		linkinfos.add(new LinkInfo("goals/maintaingoal", OAVBDIMetaModel.capability_has_goals));
//		linkinfos.add(new LinkInfo("goals/metagoal", OAVBDIMetaModel.capability_has_goals));
//		linkinfos.add(new LinkInfo("goals/performgoalref", OAVBDIMetaModel.capability_has_goalrefs));
//		linkinfos.add(new LinkInfo("goals/achievegoalref", OAVBDIMetaModel.capability_has_goalrefs));
//		linkinfos.add(new LinkInfo("goals/querygoalref", OAVBDIMetaModel.capability_has_goalrefs));
//		linkinfos.add(new LinkInfo("goals/maintaingoalref", OAVBDIMetaModel.capability_has_goalrefs));
//		linkinfos.add(new LinkInfo("goals/metagoalref", OAVBDIMetaModel.capability_has_goalrefs));
//		linkinfos.add(new LinkInfo("events/messageeventref", OAVBDIMetaModel.capability_has_messageeventrefs));
//		linkinfos.add(new LinkInfo("events/internaleventref", OAVBDIMetaModel.capability_has_internaleventrefs));

//		linkinfos.add(new LinkInfo("plan/parameter", OAVBDIMetaModel.parameterelement_has_parameters));
//		linkinfos.add(new LinkInfo("plan/parameterset", OAVBDIMetaModel.parameterelement_has_parametersets));
//		linkinfos.add(new LinkInfo("initialplan/parameter", OAVBDIMetaModel.configparameterelement_has_parameters));
//		linkinfos.add(new LinkInfo("initialplan/parameterset", OAVBDIMetaModel.configparameterelement_has_parametersets));
//		linkinfos.add(new LinkInfo("initialgoal/parameter", OAVBDIMetaModel.configparameterelement_has_parameters));
//		linkinfos.add(new LinkInfo("initialgoal/parameterset", OAVBDIMetaModel.configparameterelement_has_parametersets));
//		linkinfos.add(new LinkInfo("initialinternalevent/parameter", OAVBDIMetaModel.configparameterelement_has_parameters));
//		linkinfos.add(new LinkInfo("initialinternalevent/parameterset", OAVBDIMetaModel.configparameterelement_has_parametersets));
//		linkinfos.add(new LinkInfo("initialmessageevent/parameter", OAVBDIMetaModel.configparameterelement_has_parameters));
//		linkinfos.add(new LinkInfo("initialmessageevent/parameterset", OAVBDIMetaModel.configparameterelement_has_parametersets));
//		linkinfos.add(new LinkInfo("endplan/parameter", OAVBDIMetaModel.configparameterelement_has_parameters));
//		linkinfos.add(new LinkInfo("endplan/parameterset", OAVBDIMetaModel.configparameterelement_has_parametersets));
//		linkinfos.add(new LinkInfo("endgoal/parameter", OAVBDIMetaModel.configparameterelement_has_parameters));
//		linkinfos.add(new LinkInfo("endgoal/parameterset", OAVBDIMetaModel.configparameterelement_has_parametersets));
//		linkinfos.add(new LinkInfo("endinternalevent/parameter", OAVBDIMetaModel.configparameterelement_has_parameters));
//		linkinfos.add(new LinkInfo("endinternalevent/parameterset", OAVBDIMetaModel.configparameterelement_has_parametersets));
//		linkinfos.add(new LinkInfo("endmessageevent/parameter", OAVBDIMetaModel.configparameterelement_has_parameters));
//		linkinfos.add(new LinkInfo("endmessageevent/parameterset", OAVBDIMetaModel.configparameterelement_has_parametersets));
//		linkinfos.add(new LinkInfo("beliefref", OAVBDIMetaModel.capability_has_beliefrefs));
//		linkinfos.add(new LinkInfo("beliefsetref", OAVBDIMetaModel.capability_has_beliefsetrefs));
//		linkinfos.add(new LinkInfo("values", OAVBDIMetaModel.parameterset_has_valuesexpression));
//		linkinfos.add(new LinkInfo("facts", OAVBDIMetaModel.beliefset_has_factsexpression));
		
//		Set ignoredattrs = new HashSet();
//		ignoredattrs.add("schemaLocation");
		
		reader = new Reader(new OAVObjectReaderHandler(typeinfos));
		writer = new Writer(new OAVObjectWriterHandler(typeinfos));
	}
	
	/**
	 *  Get the reader instance.
	 */
	public static Reader getReader()
	{
		return reader;
	}
	
	/**
	 *  Get the writer instance.
	 */
	public static Writer getWriter()
	{
		return writer;
	}

	//-------- helper classes --------
	
	/**
	 *  Parse expression text.
	 */
	public static class ExpressionProcessor	implements IPostProcessor
	{
		// Hack!!! Should be configurable.
		protected static IExpressionParser	exp_parser	= new JavaCCExpressionParser();

		/**
		 *  Parse expression text.
		 */
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			IOAVState state = (IOAVState)context;
			Object	ret	= null;
			String	value	= (String)state.getAttributeValue(object, OAVBDIMetaModel.expression_has_content);
			
			if(value!=null)
			{
				String lang = (String)state.getAttributeValue(object, OAVBDIMetaModel.expression_has_language);
				
				if(state.getType(object).isSubtype(OAVBDIMetaModel.condition_type))
				{
					// Conditions now parsed in createAgentModelEntry...
					
//					System.out.println("Found condition: "+se.object);

					if(lang==null || "clips".equals(lang))
					{
						List	errors	= null;//new ArrayList();
						try
						{
							ret = ParserHelper.parseClipsCondition(value, state.getTypeModel(), OAVBDIMetaModel.getImports(state, root), errors);
						}
						catch(Exception e)
						{
//							report.put(se, e.toString());
							e.printStackTrace();
						}
//						if(!errors.isEmpty())
//						{
//							for(int i=0; i<errors.size(); i++)
//							{
//								report.put(se, errors.get(i));
//							}
//						}
					}
					else if(lang.equals("jcl"))
					{
						// Java conditions parsed later in createAgentModelEntry()
					}
					else
					{
//						report.put(se, "Unknown condition language: "+lang);
						throw new RuntimeException("Unknown condition language: "+lang);
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
							ret = exp_parser.parseExpression(value, OAVBDIMetaModel.getImports(state, root), null, state.getTypeModel().getClassLoader());
						}
						catch(Exception e)
						{
//							report.put(se, e.toString());
							e.printStackTrace();
						}
					}
					else if("clips".equals(lang))
					{
						// Conditions now parsed in createAgentModelEntry...

						List	errors	= new ArrayList();
						try
						{
							ret = ParserHelper.parseClipsCondition(value, state.getTypeModel(), OAVBDIMetaModel.getImports(state, root), errors);
						}
						catch(Exception e)
						{
//							report.put(se, e.toString());
							e.printStackTrace();
						}
						if(!errors.isEmpty())
						{
//							for(int i=0; i<errors.size(); i++)
//							{
//								report.put(se, errors.get(i));
//							}
							throw new RuntimeException("Parse errors: "+value+" "+errors);
						}
					}
					else if(lang.equals("jcl"))
					{
						// Java conditions parsed later in createAgentModelEntry()
					}
					else
					{
//						report.put(se, "Unknown condition language: "+lang);
						throw new RuntimeException("Unknown condition language: "+lang);
					}
				}
			}
			
			if(ret!=null)
				state.setAttributeValue(object, OAVBDIMetaModel.expression_has_content, ret);
		}
		
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 1;
		}
	}
	
	/**
	 *  Load class.
	 */
	public static class ClassPostProcessor	implements IPostProcessor
	{
		//-------- attributes --------
		
		/** The class name attribute. */
		protected OAVAttributeType	classnameattr;
		
		/** The class attribute. */
		protected OAVAttributeType	classattr;
		
		//-------- constructors --------
		
		/**
		 *  Create a class post processor.
		 */
		public ClassPostProcessor(OAVAttributeType classnameattr, OAVAttributeType classattr)
		{
			this.classnameattr	= classnameattr;
			this.classattr	= classattr;
		}
		
		//-------- IPostProcessor interface --------
		
		/**
		 *  Load class.
		 */
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			IOAVState state = (IOAVState)context;
			String	value	= (String)state.getAttributeValue(object, classnameattr);
			if(value!=null)
			{
				try
				{
					Class	clazz = SReflect.findClass(value, OAVBDIMetaModel.getImports(state, root), state.getTypeModel().getClassLoader());
					state.setAttributeValue(object, classattr, clazz);
				}
				catch(Exception e)
				{
//					report.put(se, e.toString());
					e.printStackTrace();
				}
			}
		}
		
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 1;
		}
	}
	
	/**
	 *  Converter for IParsedExpressions to string.
	 */
	public static class ExpressionToStringConverter implements ITypeConverter
	{
		public Object convertObject(Object val, Object root, ClassLoader classloader, Object context)
		{
			Object ret = val;
			if(val instanceof IParsedExpression)
			{
				ret = ((IParsedExpression)val).getExpressionText();
			}
			return ret;
//			return ((IOAVState)context).getAttributeValue(val, OAVBDIMetaModel.expression_has_content);
		}
		
		/*public boolean acceptsInputType(Class inputtype)
		{
			return true;
		}*/
	}
}
