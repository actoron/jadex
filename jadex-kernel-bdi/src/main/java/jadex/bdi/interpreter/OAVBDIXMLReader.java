package jadex.bdi.interpreter;

import jadex.commons.SReflect;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.rules.parser.conditions.ParserHelper;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.io.xml.OAVObjectReaderHandler;
import jadex.rules.state.io.xml.OAVObjectWriterHandler;
import jadex.xml.AttributeInfo;
import jadex.xml.IPostProcessor;
import jadex.xml.ITypeConverter;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.IBeanObjectCreator;
import jadex.xml.reader.Reader;
import jadex.xml.writer.Writer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;


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

		typeinfos.add(new TypeInfo(new XMLInfo("capabilities/capability"), new ObjectInfo(OAVBDIMetaModel.capabilityref_type)));
		
		TypeInfo ti_performgoal = new TypeInfo(new XMLInfo("performgoal"), new ObjectInfo(OAVBDIMetaModel.performgoal_type));
		TypeInfo ti_performgoalref = new TypeInfo(new XMLInfo("performgoalref"), new ObjectInfo(OAVBDIMetaModel.goalreference_type));
		TypeInfo ti_achievegoal = new TypeInfo(new XMLInfo("achievegoal"), new ObjectInfo(OAVBDIMetaModel.achievegoal_type));
		TypeInfo ti_achievegoalref = new TypeInfo(new XMLInfo("achievegoalref"), new ObjectInfo(OAVBDIMetaModel.goalreference_type));
		TypeInfo ti_querygoal = new TypeInfo(new XMLInfo("querygoal"), new ObjectInfo(OAVBDIMetaModel.querygoal_type));
		TypeInfo ti_querygoalref = new TypeInfo(new XMLInfo("querygoalref"), new ObjectInfo(OAVBDIMetaModel.goalreference_type));
		TypeInfo ti_maintaingoal = new TypeInfo(new XMLInfo("maintaingoal"), new ObjectInfo(OAVBDIMetaModel.maintaingoal_type));
		TypeInfo ti_maintaingoalref = new TypeInfo(new XMLInfo("maintaingoalref"), new ObjectInfo(OAVBDIMetaModel.goalreference_type));
		TypeInfo ti_metagoal = new TypeInfo(new XMLInfo("metagoal"), new ObjectInfo(OAVBDIMetaModel.metagoal_type));
		TypeInfo ti_metagoalref = new TypeInfo(new XMLInfo("metagoalref"), new ObjectInfo(OAVBDIMetaModel.goalreference_type));
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
		typeinfos.add(new TypeInfo(new XMLInfo("creationcondition"), new ObjectInfo(OAVBDIMetaModel.condition_type, expost), 
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_content)));
		typeinfos.add(new TypeInfo(new XMLInfo("dropcondition"), new ObjectInfo(OAVBDIMetaModel.condition_type, expost), 
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_content)));
		typeinfos.add(new TypeInfo(new XMLInfo("targetcondition"), new ObjectInfo(OAVBDIMetaModel.condition_type, expost), 
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_content)));
		typeinfos.add(new TypeInfo(new XMLInfo("maintaincondition"), new ObjectInfo(OAVBDIMetaModel.condition_type), 
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_content)));
		typeinfos.add(new TypeInfo(new XMLInfo("recurcondition"), new ObjectInfo(OAVBDIMetaModel.condition_type), 
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_content)));
		typeinfos.add(new TypeInfo(new XMLInfo("metagoal/trigger"), new ObjectInfo(OAVBDIMetaModel.metagoaltrigger_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("inhibits"), new ObjectInfo(OAVBDIMetaModel.inhibits_type), 
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_content)));
		typeinfos.add(new TypeInfo(new XMLInfo("deliberation"), null));
		typeinfos.add(new TypeInfo(new XMLInfo("unique"), new ObjectInfo(new IBeanObjectCreator()
			{
				public Object createObject(Object context, Map rawattributes,
					ClassLoader classloader) throws Exception
				{
					return Boolean.TRUE;
				}
			})));
		
		TypeInfo ti_capability = new TypeInfo(new XMLInfo("capability"), new ObjectInfo(OAVBDIMetaModel.capability_type), 
			new MappingInfo(null, OAVBDIMetaModel.modelelement_has_description, null, 
			new AttributeInfo[]{
			new AttributeInfo(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"), null, AttributeInfo.IGNORE_READWRITE)},  
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
			}));
		
		typeinfos.add(ti_capability);
		
		typeinfos.add(new TypeInfo(new XMLInfo("agent"), new ObjectInfo(OAVBDIMetaModel.agent_type), 
			new MappingInfo(ti_capability, OAVBDIMetaModel.modelelement_has_description, null)));
		
		typeinfos.add(new TypeInfo(new XMLInfo("belief"), new ObjectInfo(OAVBDIMetaModel.belief_type, tepost), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo("class", OAVBDIMetaModel.typedelement_has_classname),
			new AttributeInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AttributeInfo.IGNORE_WRITE)
			})));
		typeinfos.add(new TypeInfo(new XMLInfo("beliefref"), new ObjectInfo(OAVBDIMetaModel.beliefreference_type)));
		
		TypeInfo ti_belset = new TypeInfo(new XMLInfo("beliefset"), new ObjectInfo(OAVBDIMetaModel.beliefset_type, tepost), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo("class", OAVBDIMetaModel.typedelement_has_classname),
			new AttributeInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AttributeInfo.IGNORE_WRITE)
			}, 
			new SubobjectInfo[]{
			new SubobjectInfo(new AttributeInfo("facts", OAVBDIMetaModel.beliefset_has_factsexpression)),
			new SubobjectInfo(new AttributeInfo("fact", OAVBDIMetaModel.beliefset_has_facts))
			}));
		
		typeinfos.add(ti_belset);
		typeinfos.add(new TypeInfo(new XMLInfo("beliefsetref"), new ObjectInfo(OAVBDIMetaModel.beliefsetreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("fact"), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(null, null, new AttributeInfo((String)null, OAVBDIMetaModel.expression_has_content, null, null, exconv))));
		typeinfos.add(new TypeInfo(new XMLInfo("facts"), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(null, null, new AttributeInfo((String)null, OAVBDIMetaModel.expression_has_content, null, null, exconv))));
		
		typeinfos.add(new TypeInfo(new XMLInfo("plan"), new ObjectInfo(OAVBDIMetaModel.plan_type), 
			new MappingInfo(null, OAVBDIMetaModel.modelelement_has_description, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new AttributeInfo("parameter", OAVBDIMetaModel.parameterelement_has_parameters)),	
			new SubobjectInfo(new AttributeInfo("parameterset", OAVBDIMetaModel.parameterelement_has_parametersets))	
			})));
		
		typeinfos.add(new TypeInfo(new XMLInfo("body"), new ObjectInfo(OAVBDIMetaModel.body_type), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo("class", OAVBDIMetaModel.body_has_impl, AttributeInfo.IGNORE_WRITE), 
			new AttributeInfo("impl", OAVBDIMetaModel.body_has_impl)}, null)));//, bopost));
		typeinfos.add(new TypeInfo(new XMLInfo("precondition"), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_content)));
		typeinfos.add(new TypeInfo(new XMLInfo("contextcondition"), new ObjectInfo(OAVBDIMetaModel.condition_type, expost), 
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_content)));
		
		typeinfos.add(new TypeInfo(new XMLInfo("trigger"), new ObjectInfo(OAVBDIMetaModel.plantrigger_type), 
			new MappingInfo(null, 
			new SubobjectInfo[]{
			new SubobjectInfo(new AttributeInfo("internalevent", OAVBDIMetaModel.trigger_has_internalevents)),
			new SubobjectInfo(new AttributeInfo("messageevent", OAVBDIMetaModel.trigger_has_messageevents)),
			new SubobjectInfo(new AttributeInfo("goalfinished", OAVBDIMetaModel.trigger_has_goalfinisheds)),
			new SubobjectInfo(new AttributeInfo("factadded", OAVBDIMetaModel.trigger_has_factaddeds)),
			new SubobjectInfo(new AttributeInfo("factremoved", OAVBDIMetaModel.trigger_has_factremoveds)),
			new SubobjectInfo(new AttributeInfo("factchanged", OAVBDIMetaModel.trigger_has_factchangeds)),
			new SubobjectInfo(new AttributeInfo("goal", OAVBDIMetaModel.plantrigger_has_goals)),
			new SubobjectInfo(new AttributeInfo("condition", OAVBDIMetaModel.plantrigger_has_condition))
			})));
		
		typeinfos.add(new TypeInfo(new XMLInfo("trigger/internalevent"), new ObjectInfo(OAVBDIMetaModel.triggerreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("trigger/messageevent"), new ObjectInfo(OAVBDIMetaModel.triggerreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("trigger/goalfinished"), new ObjectInfo(OAVBDIMetaModel.triggerreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("trigger/goal"), new ObjectInfo(OAVBDIMetaModel.triggerreference_type)));
//		typeinfos.add(new TypeInfo(null, "trigger/factadded", OAVJavaType.java_string_type));
//		typeinfos.add(new TypeInfo(null, "trigger/factremoved", OAVJavaType.java_string_type));
//		typeinfos.add(new TypeInfo(null, "trigger/factchanged", OAVJavaType.java_string_type));
		typeinfos.add(new TypeInfo(new XMLInfo("waitqueue"), new ObjectInfo(OAVBDIMetaModel.trigger_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("waitqueue/internalevent"), new ObjectInfo(OAVBDIMetaModel.triggerreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("waitqueue/messageevent"), new ObjectInfo(OAVBDIMetaModel.triggerreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("waitqueue/goalfinished"), new ObjectInfo(OAVBDIMetaModel.triggerreference_type)));
		
		typeinfos.add(new TypeInfo(new XMLInfo("internalevent"), new ObjectInfo(OAVBDIMetaModel.internalevent_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("internaleventref"), new ObjectInfo(OAVBDIMetaModel.internaleventreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("messageevent"), new ObjectInfo(OAVBDIMetaModel.messageevent_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("messageeventref"), new ObjectInfo(OAVBDIMetaModel.messageeventreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("match"), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_content)));
		
		typeinfos.add(new TypeInfo(new XMLInfo("expression"), new ObjectInfo(OAVBDIMetaModel.expression_type, expost),
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_content)));
//		typeinfos.add(new TypeInfo(null, "expression/parameter", OAVBDIMetaModel.expressionparameter_type));
		typeinfos.add(new TypeInfo(new XMLInfo("condition"), new ObjectInfo(OAVBDIMetaModel.condition_type, expost),
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_content)));
		
		typeinfos.add(new TypeInfo(new XMLInfo("property"), new ObjectInfo(OAVBDIMetaModel.expression_type, expost),
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_content)));
		
		typeinfos.add(new TypeInfo(new XMLInfo("parameter"), new ObjectInfo(OAVBDIMetaModel.parameter_type, tepost), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo("class", OAVBDIMetaModel.typedelement_has_classname),
			new AttributeInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AttributeInfo.IGNORE_WRITE)
			})));
		
		TypeInfo ti_paramset = new TypeInfo(new XMLInfo("parameterset"), new ObjectInfo(OAVBDIMetaModel.parameterset_type, tepost), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo("class", OAVBDIMetaModel.typedelement_has_classname),
			new AttributeInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AttributeInfo.IGNORE_WRITE)
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AttributeInfo("values", OAVBDIMetaModel.parameterset_has_valuesexpression)),	
			new SubobjectInfo(new AttributeInfo("value", OAVBDIMetaModel.parameterset_has_values))	
			}));
		typeinfos.add(ti_paramset);
		
		typeinfos.add(new TypeInfo(new XMLInfo("plan/parameter"), new ObjectInfo(OAVBDIMetaModel.planparameter_type, tepost), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo("class", OAVBDIMetaModel.typedelement_has_classname),
			new AttributeInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AttributeInfo.IGNORE_WRITE)
			})));
		typeinfos.add(new TypeInfo(new XMLInfo("plan/parameterset"), new ObjectInfo(OAVBDIMetaModel.planparameterset_type, tepost), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo("class", OAVBDIMetaModel.typedelement_has_classname),
			new AttributeInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AttributeInfo.IGNORE_WRITE)
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AttributeInfo("values", OAVBDIMetaModel.parameterset_has_valuesexpression)),	
			new SubobjectInfo(new AttributeInfo("value", OAVBDIMetaModel.parameterset_has_values))	
			})));
		typeinfos.add(new TypeInfo(new XMLInfo("value"), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(null, null, new AttributeInfo((String)null, OAVBDIMetaModel.expression_has_content, null, null, exconv))));
		typeinfos.add(new TypeInfo(new XMLInfo("values"), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(null, null, new AttributeInfo((String)null, OAVBDIMetaModel.expression_has_content, null, null, exconv))));
//		typeinfos.add(new TypeInfo(null, "goalmapping", OAVJavaType.java_string_type));
//		typeinfos.add(new TypeInfo(null, "messageeventmapping", OAVJavaType.java_string_type));
//		typeinfos.add(new TypeInfo(null, "internaleventmapping", OAVJavaType.java_string_type));
		typeinfos.add(new TypeInfo(new XMLInfo("bindingoptions"), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_content)));
				
//		typeinfos.add(new TypeInfo(null, "concrete", OAVJavaType.java_string_type));

		typeinfos.add(new TypeInfo(new XMLInfo("configurations"), null, 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo("default", OAVBDIMetaModel.capability_has_defaultconfiguration)})));
		typeinfos.add(new TypeInfo(new XMLInfo("configuration"), new ObjectInfo(OAVBDIMetaModel.configuration_type),
			new MappingInfo(null, null, new SubobjectInfo[]{
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
			})));
		typeinfos.add(new TypeInfo(new XMLInfo("initialcapability"), new ObjectInfo(OAVBDIMetaModel.initialcapability_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("initialbelief"), new ObjectInfo(OAVBDIMetaModel.configbelief_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("initialbeliefset"), new ObjectInfo(OAVBDIMetaModel.configbeliefset_type), new MappingInfo(ti_belset)));
		typeinfos.add(new TypeInfo(new XMLInfo("initialgoal"), new ObjectInfo(OAVBDIMetaModel.configelement_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("initialplan"), new ObjectInfo(OAVBDIMetaModel.configelement_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("initialinternalevent"), new ObjectInfo(OAVBDIMetaModel.configelement_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("initialmessageevent"), new ObjectInfo(OAVBDIMetaModel.configelement_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("endgoal"), new ObjectInfo(OAVBDIMetaModel.configelement_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("endplan"), new ObjectInfo(OAVBDIMetaModel.configelement_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("endinternalevent"), new ObjectInfo(OAVBDIMetaModel.configelement_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("endmessageevent"), new ObjectInfo(OAVBDIMetaModel.configelement_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("initialgoal/parameter"), new ObjectInfo(OAVBDIMetaModel.configparameter_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("initialgoal/parameterset"), new ObjectInfo(OAVBDIMetaModel.configparameterset_type), new MappingInfo(ti_paramset)));
		typeinfos.add(new TypeInfo(new XMLInfo("initialplan/parameter"), new ObjectInfo(OAVBDIMetaModel.configparameter_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("initialplan/parameterset"), new ObjectInfo(OAVBDIMetaModel.configparameterset_type), new MappingInfo(ti_paramset)));
		typeinfos.add(new TypeInfo(new XMLInfo("initialinternalevent/parameter"), new ObjectInfo(OAVBDIMetaModel.configparameter_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("initialinternalevent/parameterset"), new ObjectInfo(OAVBDIMetaModel.configparameterset_type), new MappingInfo(ti_paramset)));
		typeinfos.add(new TypeInfo(new XMLInfo("initialmessageevent/parameter"), new ObjectInfo(OAVBDIMetaModel.configparameter_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("initialmessageevent/parameterset"), new ObjectInfo(OAVBDIMetaModel.configparameterset_type), new MappingInfo(ti_paramset)));
		typeinfos.add(new TypeInfo(new XMLInfo("endgoal/parameter"), new ObjectInfo(OAVBDIMetaModel.configparameter_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("endgoal/parameterset"), new ObjectInfo(OAVBDIMetaModel.configparameterset_type), new MappingInfo(ti_paramset)));
		typeinfos.add(new TypeInfo(new XMLInfo("endplan/parameter"), new ObjectInfo(OAVBDIMetaModel.configparameter_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("endplan/parameterset"), new ObjectInfo(OAVBDIMetaModel.configparameterset_type), new MappingInfo(ti_paramset)));
		typeinfos.add(new TypeInfo(new XMLInfo("endinternalevent/parameter"), new ObjectInfo(OAVBDIMetaModel.configparameter_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("endinternalevent/parameterset"), new ObjectInfo(OAVBDIMetaModel.configparameterset_type), new MappingInfo(ti_paramset)));
		typeinfos.add(new TypeInfo(new XMLInfo("endmessageevent/parameter"), new ObjectInfo(OAVBDIMetaModel.configparameter_type)));
		typeinfos.add(new TypeInfo(new XMLInfo("endmessageevent/parameterset"), new ObjectInfo(OAVBDIMetaModel.configparameterset_type), new MappingInfo(ti_paramset)));

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
		public Object postProcess(Object context, Object object, Object root, ClassLoader classloader)
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
		
			return null;
		}
		
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 0;
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
		public Object postProcess(Object context, Object object, Object root, ClassLoader classloader)
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
			
			return null;
		}
		
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 0;
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
