package jadex.bdi;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.commons.SReflect;
import jadex.commons.Tuple;
import jadex.commons.collection.MultiCollection;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.rules.parser.conditions.ParserHelper;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.io.xml.OAVObjectReaderHandler;
import jadex.rules.state.io.xml.OAVObjectWriterHandler;
import jadex.rules.state.io.xml.OAVUserContext;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeConverter;
import jadex.xml.AttributeInfo;
import jadex.xml.IAttributeConverter;
import jadex.xml.IContext;
import jadex.xml.IObjectStringConverter;
import jadex.xml.IPostProcessor;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.StackElement;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.IBeanObjectCreator;
import jadex.xml.reader.ReadContext;
import jadex.xml.reader.Reader;
import jadex.xml.writer.Writer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;


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
		IObjectStringConverter exconv = new ExpressionToStringConverter();
		
		IAttributeConverter exatconv = new AttributeConverter(null, exconv);
		
		Set typeinfos = new HashSet();

		String uri = "http://jadex.sourceforge.net/jadex-bdi";
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "exclude")),  new ObjectInfo(new IBeanObjectCreator()
		{
			public Object createObject(IContext context, Map rawattributes) throws Exception
			{
				return rawattributes.get("parameterref");
			}
			}), new MappingInfo(null, new AttributeInfo[]
			{
				// Using URI doesn't work (bug in reader?)
				//new AttributeInfo(new AccessInfo(new QName(uri,"parameterref"), null, AccessInfo.IGNORE_READ))
				new AttributeInfo(new AccessInfo("parameterref", null, AccessInfo.IGNORE_READ))
			})));

		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "capabilities"), new QName(uri, "capability")}), new ObjectInfo(OAVBDIMetaModel.capabilityref_type)));
		
		TypeInfo ti_performgoal = new TypeInfo(new XMLInfo(new QName(uri, "performgoal")), new ObjectInfo(OAVBDIMetaModel.performgoal_type));
		TypeInfo ti_performgoalref = new TypeInfo(new XMLInfo(new QName(uri, "performgoalref")), new ObjectInfo(OAVBDIMetaModel.goalreference_type));
		TypeInfo ti_achievegoal = new TypeInfo(new XMLInfo(new QName(uri, "achievegoal")), new ObjectInfo(OAVBDIMetaModel.achievegoal_type));
		TypeInfo ti_achievegoalref = new TypeInfo(new XMLInfo(new QName(uri, "achievegoalref")), new ObjectInfo(OAVBDIMetaModel.goalreference_type));
		TypeInfo ti_querygoal = new TypeInfo(new XMLInfo(new QName(uri, "querygoal")), new ObjectInfo(OAVBDIMetaModel.querygoal_type));
		TypeInfo ti_querygoalref = new TypeInfo(new XMLInfo(new QName(uri, "querygoalref")), new ObjectInfo(OAVBDIMetaModel.goalreference_type));
		TypeInfo ti_maintaingoal = new TypeInfo(new XMLInfo(new QName(uri, "maintaingoal")), new ObjectInfo(OAVBDIMetaModel.maintaingoal_type));
		TypeInfo ti_maintaingoalref = new TypeInfo(new XMLInfo(new QName(uri, "maintaingoalref")), new ObjectInfo(OAVBDIMetaModel.goalreference_type));
		TypeInfo ti_metagoal = new TypeInfo(new XMLInfo(new QName(uri, "metagoal")), new ObjectInfo(OAVBDIMetaModel.metagoal_type));
		TypeInfo ti_metagoalref = new TypeInfo(new XMLInfo(new QName(uri, "metagoalref")), new ObjectInfo(OAVBDIMetaModel.goalreference_type));
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
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "creationcondition")), new ObjectInfo(OAVBDIMetaModel.condition_type, expost), 
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_text)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "dropcondition")), new ObjectInfo(OAVBDIMetaModel.condition_type, expost), 
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_text)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "targetcondition")), new ObjectInfo(OAVBDIMetaModel.condition_type, expost), 
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_text)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "maintaincondition")), new ObjectInfo(OAVBDIMetaModel.condition_type), 
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_text)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "recurcondition")), new ObjectInfo(OAVBDIMetaModel.condition_type), 
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_text)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "metagoal"), new QName(uri, "trigger")}), new ObjectInfo(OAVBDIMetaModel.metagoaltrigger_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "inhibits")), new ObjectInfo(OAVBDIMetaModel.inhibits_type, expost), 
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_text)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "deliberation")), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "unique")), new ObjectInfo(new IBeanObjectCreator()
			{
				public Object createObject(IContext context, Map rawattributes) throws Exception
				{
					return Boolean.TRUE;
				}
			})));
//			}), new MappingInfo(null, new SubobjectInfo[]
//			{
//				new SubobjectInfo(new XMLInfo(new QName(uri, "exclude")), new AccessInfo("parameterref"))
//			})));
		
		TypeInfo ti_capability = new TypeInfo(new XMLInfo(new QName(uri, "capability")), new ObjectInfo(OAVBDIMetaModel.capability_type), 
			new MappingInfo(null, OAVBDIMetaModel.modelelement_has_description, null, 
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"), null, AccessInfo.IGNORE_READWRITE))},  
			new SubobjectInfo[]{
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "imports"), new QName(uri, "import")}), new AccessInfo(new QName(uri, "import"), OAVBDIMetaModel.capability_has_imports)),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "beliefs"), new QName(uri, "beliefref")}), new AccessInfo(new QName(uri, "beliefref"), OAVBDIMetaModel.capability_has_beliefrefs)),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "beliefs"), new QName(uri, "beliefsetref")}), new AccessInfo(new QName(uri, "beliefsetref"), OAVBDIMetaModel.capability_has_beliefsetrefs)),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "beliefs"), new QName(uri, "belief")}), new AccessInfo(new QName(uri, "belief"), OAVBDIMetaModel.capability_has_beliefs)),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "beliefs"), new QName(uri, "beliefset")}), new AccessInfo(new QName(uri, "beliefset"), OAVBDIMetaModel.capability_has_beliefsets)),

			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "performgoal")}), new AccessInfo(new QName(uri, "performgoal"), OAVBDIMetaModel.capability_has_goals), null, false, ti_performgoal.getObjectInfo()),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "achievegoal")}), new AccessInfo(new QName(uri, "achievegoal"), OAVBDIMetaModel.capability_has_goals), null, false, ti_achievegoal.getObjectInfo()),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "querygoal")}), new AccessInfo(new QName(uri, "querygoal"), OAVBDIMetaModel.capability_has_goals), null, false, ti_querygoal.getObjectInfo()),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "maintaingoal")}), new AccessInfo(new QName(uri, "maintaingoal"), OAVBDIMetaModel.capability_has_goals), null, false, ti_maintaingoal.getObjectInfo()),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "metagoal")}), new AccessInfo(new QName(uri, "metagoal"), OAVBDIMetaModel.capability_has_goals), null, false, ti_metagoal.getObjectInfo()),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "performgoalref")}), new AccessInfo(new QName(uri, "performgoalref"), OAVBDIMetaModel.capability_has_goalrefs), null, false, ti_performgoalref.getObjectInfo()),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "achievegoalref")}), new AccessInfo(new QName(uri, "achievegoalref"), OAVBDIMetaModel.capability_has_goalrefs), null, false, ti_achievegoalref.getObjectInfo()),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "querygoalref")}), new AccessInfo(new QName(uri, "querygoalref"), OAVBDIMetaModel.capability_has_goalrefs), null, false, ti_querygoalref.getObjectInfo()),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "maintaingoalref")}), new AccessInfo(new QName(uri, "maintaingoalref"), OAVBDIMetaModel.capability_has_goalrefs), null, false, ti_maintaingoalref.getObjectInfo()),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "metagoalref")}), new AccessInfo(new QName(uri, "metagoalref"), OAVBDIMetaModel.capability_has_goalrefs), null, false, ti_metagoalref.getObjectInfo()),

			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "plans"), new QName(uri, "plan")}), new AccessInfo(new QName(uri, "plan"), OAVBDIMetaModel.capability_has_plans)),

			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "messageeventref")}), new AccessInfo(new QName(uri, "messageeventref"), OAVBDIMetaModel.capability_has_messageeventrefs)),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "internaleventref")}), new AccessInfo(new QName(uri, "internaleventref"), OAVBDIMetaModel.capability_has_internaleventrefs)),

			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "expressions"), new QName(uri, "expressionref")}), new AccessInfo(new QName(uri, "expressionref"), OAVBDIMetaModel.capability_has_expressionrefs)),
			
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "properties"), new QName(uri, "property")}), new AccessInfo(new QName(uri, "property"), OAVBDIMetaModel.capability_has_properties)),

			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "services"), new QName(uri, "usedservice")}), new AccessInfo(new QName(uri, "usedservice"), OAVBDIMetaModel.capability_has_usedservices)),
			
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "services"), new QName(uri, "offeredservice")}), new AccessInfo(new QName(uri, "offeredservice"), OAVBDIMetaModel.capability_has_offeredservices)),

			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "configurations"), new QName(uri, "configuration")}), new AccessInfo(new QName(uri, "configuration"), OAVBDIMetaModel.capability_has_configurations)),
			}));
		
		typeinfos.add(ti_capability);
		
		TypeInfo ti_expression = new TypeInfo(null, new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.expression_has_text), exatconv), 
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.expression_has_classname)),
			new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.expression_has_class, AccessInfo.IGNORE_WRITE)),
			}));
				
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "agent")), new ObjectInfo(OAVBDIMetaModel.agent_type), 
			new MappingInfo(ti_capability, OAVBDIMetaModel.modelelement_has_description, null, null, new SubobjectInfo[]{
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "services"), new QName(uri, "container")})
			, new AccessInfo(new QName(uri, "import"), OAVBDIMetaModel.agent_has_servicecontainer))})));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "container")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(ti_expression)));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "belief")), new ObjectInfo(OAVBDIMetaModel.belief_type, tepost), 
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.typedelement_has_classname)),
				new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AccessInfo.IGNORE_WRITE))
			})));	
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "beliefref")), new ObjectInfo(OAVBDIMetaModel.beliefreference_type)));
		
		TypeInfo ti_belset = new TypeInfo(new XMLInfo(new QName(uri, "beliefset")), new ObjectInfo(OAVBDIMetaModel.beliefset_type, tepost), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.typedelement_has_classname)),
			new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AccessInfo.IGNORE_WRITE))
			}, 
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "facts"), OAVBDIMetaModel.beliefset_has_factsexpression)),
			new SubobjectInfo(new AccessInfo(new QName(uri, "fact"), OAVBDIMetaModel.beliefset_has_facts))
			}));
		
		typeinfos.add(ti_belset);
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "beliefsetref")), new ObjectInfo(OAVBDIMetaModel.beliefsetreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "fact")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(ti_expression)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "facts")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(ti_expression)));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "plan")), new ObjectInfo(OAVBDIMetaModel.plan_type), 
			new MappingInfo(null, OAVBDIMetaModel.modelelement_has_description, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "parameter"), OAVBDIMetaModel.parameterelement_has_parameters)),	
			new SubobjectInfo(new AccessInfo(new QName(uri, "parameterset"), OAVBDIMetaModel.parameterelement_has_parametersets))	
			})));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "body")), new ObjectInfo(OAVBDIMetaModel.body_type), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.body_has_impl, AccessInfo.IGNORE_WRITE)), 
			new AttributeInfo(new AccessInfo("impl", OAVBDIMetaModel.body_has_impl))}, null)));//, bopost));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "precondition")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(ti_expression)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "contextcondition")), new ObjectInfo(OAVBDIMetaModel.condition_type, expost), 
			new MappingInfo(ti_expression)));
			
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "trigger")), new ObjectInfo(OAVBDIMetaModel.plantrigger_type), 
			new MappingInfo(null, 
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "internalevent"), OAVBDIMetaModel.trigger_has_internalevents)),
			new SubobjectInfo(new AccessInfo(new QName(uri, "messageevent"), OAVBDIMetaModel.trigger_has_messageevents)),
			new SubobjectInfo(new AccessInfo(new QName(uri, "goalfinished"), OAVBDIMetaModel.trigger_has_goalfinisheds)),
			new SubobjectInfo(new AccessInfo(new QName(uri, "factadded"), OAVBDIMetaModel.trigger_has_factaddeds)),
			new SubobjectInfo(new AccessInfo(new QName(uri, "factremoved"), OAVBDIMetaModel.trigger_has_factremoveds)),
			new SubobjectInfo(new AccessInfo(new QName(uri, "factchanged"), OAVBDIMetaModel.trigger_has_factchangeds)),
			new SubobjectInfo(new AccessInfo(new QName(uri, "goal"), OAVBDIMetaModel.plantrigger_has_goals)),
			new SubobjectInfo(new AccessInfo(new QName(uri, "condition"), OAVBDIMetaModel.plantrigger_has_condition))
			})));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "trigger"), new QName(uri, "internalevent")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "trigger"), new QName(uri, "messageevent")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "trigger"), new QName(uri, "goalfinished")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "trigger"), new QName(uri, "goal")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "waitqueue")), new ObjectInfo(OAVBDIMetaModel.trigger_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "waitqueue"), new QName(uri, "internalevent")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "waitqueue"), new QName(uri, "messageevent")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "waitqueue"), new QName(uri, "goalfinished")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type)));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "internalevent")), new ObjectInfo(OAVBDIMetaModel.internalevent_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "internaleventref")), new ObjectInfo(OAVBDIMetaModel.internaleventreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "messageevent")), new ObjectInfo(OAVBDIMetaModel.messageevent_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "messageeventref")), new ObjectInfo(OAVBDIMetaModel.messageeventreference_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "match")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(ti_expression)));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "expression")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost),
			new MappingInfo(ti_expression)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "expressionref")), new ObjectInfo(OAVBDIMetaModel.expressionreference_type)));

		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "condition")), new ObjectInfo(OAVBDIMetaModel.condition_type, expost),
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_text)));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "usedservice")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost),
			new MappingInfo(ti_expression)));
		
		TypeInfo ti_service = new TypeInfo(new XMLInfo(new QName(uri, "offeredservice")), new ObjectInfo(OAVBDIMetaModel.service_type, expost), 
			new MappingInfo(ti_expression, 
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("decoupled", OAVBDIMetaModel.service_has_decoupled)),
			}));
		typeinfos.add(ti_service);
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "property")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost),
			new MappingInfo(ti_expression)));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "parameter")), new ObjectInfo(OAVBDIMetaModel.parameter_type, tepost), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.typedelement_has_classname)),
			new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AccessInfo.IGNORE_WRITE))
			})));
		
		TypeInfo ti_paramset = new TypeInfo(new XMLInfo(new QName(uri, "parameterset")), new ObjectInfo(OAVBDIMetaModel.parameterset_type, tepost), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.typedelement_has_classname)),
			new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AccessInfo.IGNORE_WRITE))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "values"), OAVBDIMetaModel.parameterset_has_valuesexpression)),	
			new SubobjectInfo(new AccessInfo(new QName(uri, "value"), OAVBDIMetaModel.parameterset_has_values))	
			}));
		typeinfos.add(ti_paramset);
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "plan"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.planparameter_type, tepost), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.typedelement_has_classname)),
			new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AccessInfo.IGNORE_WRITE))
			})));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "plan"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.planparameterset_type, tepost), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.typedelement_has_classname)),
			new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AccessInfo.IGNORE_WRITE))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "values"), OAVBDIMetaModel.parameterset_has_valuesexpression)),	
			new SubobjectInfo(new AccessInfo(new QName(uri, "value"), OAVBDIMetaModel.parameterset_has_values))	
			})));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "value")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(ti_expression)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "values")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(ti_expression)));

		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "bindingoptions")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(ti_expression)));
					
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "configurations")), null, 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("default", OAVBDIMetaModel.capability_has_defaultconfiguration))})));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "configuration")), new ObjectInfo(OAVBDIMetaModel.configuration_type),
			new MappingInfo(null, null, new SubobjectInfo[]{
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "beliefs"), new QName(uri, "initialbelief")}), new AccessInfo("initialbelief", OAVBDIMetaModel.configuration_has_initialbeliefs)),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "beliefs"), new QName(uri, "initialbeliefset")}), new AccessInfo("initialbeliefset", OAVBDIMetaModel.configuration_has_initialbeliefsets)),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "initialgoal")}), new AccessInfo("initialgoal", OAVBDIMetaModel.configuration_has_initialgoals)),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "endgoal")}), new AccessInfo("endgoal", OAVBDIMetaModel.configuration_has_endgoals)),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "plans"), new QName(uri, "initialplan")}), new AccessInfo("initialplan", OAVBDIMetaModel.configuration_has_initialplans)),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "plans"), new QName(uri, "endplan")}), new AccessInfo("endplan", OAVBDIMetaModel.configuration_has_endplans)),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "initialinternalevent")}), new AccessInfo("initialinternalevent", OAVBDIMetaModel.configuration_has_initialinternalevents)),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "initialmessageevent")}), new AccessInfo("initialmessageevent", OAVBDIMetaModel.configuration_has_initialmessageevents)),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "endinternalevent")}), new AccessInfo("endinternalevent", OAVBDIMetaModel.configuration_has_endinternalevents)),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "endmessageevent")}), new AccessInfo("endmessageevent", OAVBDIMetaModel.configuration_has_endmessageevents))
			})));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialcapability")), new ObjectInfo(OAVBDIMetaModel.initialcapability_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialbelief")), new ObjectInfo(OAVBDIMetaModel.configbelief_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialbeliefset")), new ObjectInfo(OAVBDIMetaModel.configbeliefset_type), new MappingInfo(ti_belset)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialgoal")), new ObjectInfo(OAVBDIMetaModel.configelement_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialplan")), new ObjectInfo(OAVBDIMetaModel.configelement_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialinternalevent")), new ObjectInfo(OAVBDIMetaModel.configelement_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialmessageevent")), new ObjectInfo(OAVBDIMetaModel.configelement_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "endgoal")), new ObjectInfo(OAVBDIMetaModel.configelement_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "endplan")), new ObjectInfo(OAVBDIMetaModel.configelement_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "endinternalevent")), new ObjectInfo(OAVBDIMetaModel.configelement_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "endmessageevent")), new ObjectInfo(OAVBDIMetaModel.configelement_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialgoal"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialgoal"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type), new MappingInfo(ti_paramset)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialplan"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialplan"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type), new MappingInfo(ti_paramset)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialinternalevent"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialinternalevent"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type), new MappingInfo(ti_paramset)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialmessageevent"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialmessageevent"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type), new MappingInfo(ti_paramset)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endgoal"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endgoal"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type), new MappingInfo(ti_paramset)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endplan"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endplan"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type), new MappingInfo(ti_paramset)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endinternalevent"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endinternalevent"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type), new MappingInfo(ti_paramset)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endmessageevent"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endmessageevent"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type), new MappingInfo(ti_paramset)));

		// Different readers and writers should not work on the same typeinfos
		// because they may alter them (e.g. add additional array types).
		reader = new Reader(new OAVObjectReaderHandler(typeinfos), false, false, new XMLReporter()
		{
			public void report(String msg, String type, Object info, Location location) throws XMLStreamException
			{
//				System.out.println("XML error: "+msg+", "+type+", "+info+", "+location);
				IContext	context	= (IContext)Reader.READ_CONTEXT.get();
				reportError(context, msg);
			}
		});
		writer = new Writer(new OAVObjectWriterHandler(new HashSet(typeinfos)));
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

	/**
	 *  Report an error including the line and column.
	 */
	protected static void reportError(IContext context, String error)
	{
		MultiCollection	report	= (MultiCollection)((OAVUserContext)context.getUserContext()).getCustom();
		String	pos;
		Tuple	stack	= new Tuple(((ReadContext)context).getStack().toArray());
		if(stack.getEntities().length>0)
		{
			StackElement	se	= (StackElement)stack.get(stack.getEntities().length-1);
			pos	= " (line "+se.getLocation().getLineNumber()+", column "+se.getLocation().getColumnNumber()+")";
		}
		else
		{
			pos	= " (line 0, column 0)";			
		}
		report.put(stack, error+pos);
	}
	
	//-------- helper classes --------
	
	/**
	 *  Parse expression text.
	 */
	public static class ExpressionProcessor	implements IPostProcessor
	{
		// Hack!!! Should be configurable.
		protected static IExpressionParser	exp_parser	= new JavaCCExpressionParser();

		protected ClassPostProcessor clpost = new ClassPostProcessor(OAVBDIMetaModel.expression_has_classname, OAVBDIMetaModel.expression_has_class);
		
		/**
		 *  Parse expression text.
		 */
		public Object postProcess(IContext context, Object object)
		{
			clpost.postProcess(context, object);
			OAVUserContext	ouc	= (OAVUserContext)context.getUserContext();
			IOAVState state = (IOAVState)ouc.getState();
			
			Object	ret	= null;
			String	value	= (String)state.getAttributeValue(object, OAVBDIMetaModel.expression_has_text);
			
			if(value!=null)
			{
				String lang = (String)state.getAttributeValue(object, OAVBDIMetaModel.expression_has_language);
				
				if(state.getType(object).isSubtype(OAVBDIMetaModel.condition_type) && !state.getType(object).isSubtype(OAVBDIMetaModel.inhibits_type))
				{
					// Conditions now parsed in createAgentModelEntry...
					
//					System.out.println("Found condition: "+se.object);

					if(lang==null || lang.equals("jcl"))
					{
						// Java conditions parsed later in createAgentModelEntry()
					}
					else if("clips".equals(lang))
					{
						List	errors	= new ArrayList();
						try
						{
							ret = ParserHelper.parseClipsCondition(value, state.getTypeModel(), 
								OAVBDIMetaModel.getImports(state, context.getRootObject()), errors);
						}
						catch(Exception e)
						{
							reportError(context, e.toString());
						}
						if(!errors.isEmpty())
						{
							for(int i=0; i<errors.size(); i++)
							{
								reportError(context, (String)errors.get(i));
							}
						}
					}
					else
					{
						reportError(context, "Unknown condition language: "+lang);
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
							ret = exp_parser.parseExpression(value, OAVBDIMetaModel.getImports(
								state, context.getRootObject()), null, state.getTypeModel().getClassLoader());
						}
						catch(Exception e)
						{
							reportError(context, e.toString());
						}
					}
					else if("clips".equals(lang))
					{
						// Conditions now parsed in createAgentModelEntry...

						List	errors	= new ArrayList();
						try
						{
							ret = ParserHelper.parseClipsCondition(value, state.getTypeModel(), 
								OAVBDIMetaModel.getImports(state, context.getRootObject()), errors);
						}
						catch(Exception e)
						{
							reportError(context, e.toString());
						}
						if(!errors.isEmpty())
						{
							for(int i=0; i<errors.size(); i++)
							{
								reportError(context, (String)errors.get(i));
							}
						}
					}
					else if(lang.equals("jcl"))
					{
						// Java conditions parsed later in createAgentModelEntry()
					}
					else
					{
						reportError(context, "Unknown condition language: "+lang);
					}
				}
			}
			
			if(ret!=null)
				state.setAttributeValue(object, OAVBDIMetaModel.expression_has_parsed, ret);
		
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
		public Object postProcess(IContext context, Object object)
		{
			OAVUserContext	ouc	= (OAVUserContext)context.getUserContext();
			IOAVState state = (IOAVState)ouc.getState();
			String	value	= (String)state.getAttributeValue(object, classnameattr);
			if(value!=null)
			{
				try
				{
					Class	clazz = SReflect.findClass(value, OAVBDIMetaModel.getImports(
						state, context.getRootObject()), state.getTypeModel().getClassLoader());
					state.setAttributeValue(object, classattr, clazz);
				}
				catch(Exception e)
				{
					reportError(context, e.toString());
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
	public static class ExpressionToStringConverter implements IObjectStringConverter
	{
		public String convertObject(Object val, IContext context)
		{
			String ret = null;
			if(val instanceof IParsedExpression)
			{
				ret = ((IParsedExpression)val).getExpressionText();
			}
			return ret;
		}
	}
}
