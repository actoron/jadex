package jadex.bdi;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.SReflect;
import jadex.commons.Tuple;
import jadex.commons.collection.MultiCollection;
import jadex.component.ComponentXMLReader;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.rules.parser.conditions.ParserHelper;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.io.xml.OAVObjectReaderHandler;
import jadex.rules.state.io.xml.OAVObjectWriterHandler;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeConverter;
import jadex.xml.AttributeInfo;
import jadex.xml.IAttributeConverter;
import jadex.xml.IContext;
import jadex.xml.IObjectStringConverter;
import jadex.xml.IPostProcessor;
import jadex.xml.LinkingInfo;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.StackElement;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.bean.IBeanObjectCreator;
import jadex.xml.reader.IObjectLinker;
import jadex.xml.reader.IObjectReaderHandler;
import jadex.xml.reader.AReader;
import jadex.xml.reader.ReadContext;
import jadex.xml.reader.XMLReaderFactory;
import jadex.xml.writer.AWriter;
import jadex.xml.writer.IObjectWriterHandler;
import jadex.xml.writer.Writer;
import jadex.xml.writer.XMLWriterFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.xml.stax.QName;
import jadex.xml.stax.ILocation;
import jadex.xml.stax.XMLReporter;



/**
 *  Reader for loading BDI XML models into OAV states.
 */
public class OAVBDIXMLReader
{
	//-------- attributes --------
	
	/** The singleton reader instance. */
	protected static AReader reader;
	protected static AWriter writer;
	
	/** The manager. */
	protected static TypeInfoPathManager manager;
	
	/** The handler. */
	protected static IObjectReaderHandler readerhandler;

	/** The writer handler. */
	protected static IObjectWriterHandler writerhandler;

	// Initialize reader instance.
	static
	{
		// Post processors.
		IPostProcessor expost = new ExpressionProcessor();
//		IPostProcessor pspost = new MultiPostProcessor(new IPostProcessor[]{
//			expost,
//			new ClassPostProcessor(OAVBDIMetaModel.providedservice_has_implementationname, OAVBDIMetaModel.providedservice_has_implementation)
//		});

		IPostProcessor tepost = new ClassPostProcessor(OAVBDIMetaModel.typedelement_has_classname, OAVBDIMetaModel.typedelement_has_class); 
		IPostProcessor scpost = new ClassPostProcessor(OAVBDIMetaModel.publish_has_classname, OAVBDIMetaModel.publish_has_class); 
//		IPostProcessor rspost = new ClassPostProcessor(OAVBDIMetaModel.requiredservice_has_classname, OAVBDIMetaModel.requiredservice_has_class); 
//		IPostProcessor pspost = new ClassPostProcessor(OAVBDIMetaModel.providedservice_has_classname, OAVBDIMetaModel.providedservice_has_class); 
//		IPostProcessor bopost = new ClassPostProcessor(OAVBDIMetaModel.body_has_classname, OAVBDIMetaModel.body_has_class); 
		IObjectStringConverter exconv = new ExpressionToStringConverter();
		
		IAttributeConverter exatconv = new AttributeConverter(null, exconv);
		
		Set typeinfos = new HashSet(ComponentXMLReader.getXMLMapping(null));

		final String uri = "http://jadex.sourceforge.net/jadex";
		
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

		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "capabilities"), new QName(uri, "capability")}), new ObjectInfo(OAVBDIMetaModel.capabilityref_type),
			null, null, new OAVObjectReaderHandler()));
		
		TypeInfo ti_performgoal = new TypeInfo(new XMLInfo(new QName(uri, "performgoal")), new ObjectInfo(OAVBDIMetaModel.performgoal_type),
			null, null, new OAVObjectReaderHandler());
		TypeInfo ti_performgoalref = new TypeInfo(new XMLInfo(new QName(uri, "performgoalref")), new ObjectInfo(OAVBDIMetaModel.goalreference_type),
			null, null, new OAVObjectReaderHandler());
		TypeInfo ti_achievegoal = new TypeInfo(new XMLInfo(new QName(uri, "achievegoal")), new ObjectInfo(OAVBDIMetaModel.achievegoal_type),
			null, null, new OAVObjectReaderHandler());
		TypeInfo ti_achievegoalref = new TypeInfo(new XMLInfo(new QName(uri, "achievegoalref")), new ObjectInfo(OAVBDIMetaModel.goalreference_type),
			null, null, new OAVObjectReaderHandler());
		TypeInfo ti_querygoal = new TypeInfo(new XMLInfo(new QName(uri, "querygoal")), new ObjectInfo(OAVBDIMetaModel.querygoal_type),
			null, null, new OAVObjectReaderHandler());
		TypeInfo ti_querygoalref = new TypeInfo(new XMLInfo(new QName(uri, "querygoalref")), new ObjectInfo(OAVBDIMetaModel.goalreference_type),
			null, null, new OAVObjectReaderHandler());
		TypeInfo ti_maintaingoal = new TypeInfo(new XMLInfo(new QName(uri, "maintaingoal")), new ObjectInfo(OAVBDIMetaModel.maintaingoal_type),
			null, null, new OAVObjectReaderHandler());
		TypeInfo ti_maintaingoalref = new TypeInfo(new XMLInfo(new QName(uri, "maintaingoalref")), new ObjectInfo(OAVBDIMetaModel.goalreference_type),
			null, null, new OAVObjectReaderHandler());
		TypeInfo ti_metagoal = new TypeInfo(new XMLInfo(new QName(uri, "metagoal")), new ObjectInfo(OAVBDIMetaModel.metagoal_type),
			null, null, new OAVObjectReaderHandler());
		TypeInfo ti_metagoalref = new TypeInfo(new XMLInfo(new QName(uri, "metagoalref")), new ObjectInfo(OAVBDIMetaModel.goalreference_type),
			null, null, new OAVObjectReaderHandler());
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
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_text, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.inhibits_has_ref))})));
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
		
		// Find type infos. hack???
		TypeInfo	comptype	= null;
		TypeInfo	configtype	= null;
		for(Iterator it=typeinfos.iterator(); (configtype==null || comptype==null) && it.hasNext(); )
		{
			TypeInfo	ti	= (TypeInfo)it.next();
			if(comptype==null && ti.getXMLInfo().getXMLPath().equals(new XMLInfo(new QName(uri, "componenttype")).getXMLPath()))
			{
				comptype	= ti;
			}
			if(configtype==null && ti.getXMLInfo().getXMLPath().equals(new XMLInfo(new QName(uri, "configuration")).getXMLPath()))
			{
				configtype	= ti;
			}
		}

		IObjectLinker	capalinker	= new IObjectLinker()
		{
			public void linkObject(Object object, Object parent, Object linkinfo, QName[] pathname, ReadContext context) throws Exception
			{
				// Exchange parent for OAV children of root object.
				Map	user	= (Map)context.getUserContext();
				IOAVState	state	= (IOAVState)user.get(OAVObjectReaderHandler.CONTEXT_STATE);
				if(state.isIdentifier(object))
				{
					parent = getOAVRoot(uri, context, user, state);
				}
				
				context.getTopStackElement().getReaderHandler().linkObject(object, parent, linkinfo, pathname, context);
			}
		};
		IPostProcessor	capaproc	= new IPostProcessor()
		{
			public Object postProcess(IContext context, Object object)
			{
				Map	user	= (Map)context.getUserContext();
				IOAVState	state	= (IOAVState)user.get(OAVObjectReaderHandler.CONTEXT_STATE);
				getOAVRoot(uri, (ReadContext)context, user, state);
				return object;
			}
			
			public int getPass()
			{
				return 0;
			}
		};

		IObjectLinker	configlinker	= new IObjectLinker()
		{
			public void linkObject(Object object, Object parent, Object linkinfo, QName[] pathname, ReadContext context) throws Exception
			{
				// Exchange parent for OAV children of configuration object.
				Map	user	= (Map)context.getUserContext();
				IOAVState	state	= (IOAVState)user.get(OAVObjectReaderHandler.CONTEXT_STATE);
				if(state.isIdentifier(object))
				{
					parent = getOAVConfiguration(uri, parent, context, user, state);
				}
				
				context.getTopStackElement().getReaderHandler().linkObject(object, parent, linkinfo, pathname, context);
			}
		};
		
		IPostProcessor	configproc	= new IPostProcessor()
		{
			public Object postProcess(IContext context, Object object)
			{
				Map	user	= (Map)context.getUserContext();
				IOAVState	state	= (IOAVState)user.get(OAVObjectReaderHandler.CONTEXT_STATE);
				getOAVConfiguration(uri, object, (ReadContext)context, user, state);
				return object;
			}
			
			public int getPass()
			{
				return 0;
			}
		};

		TypeInfo ti_capability = new TypeInfo(new XMLInfo(new QName(uri, "capability")), new ObjectInfo(null, capaproc), 
			new MappingInfo(comptype, null, null, 
				new AttributeInfo[]{
					new AttributeInfo(new AccessInfo(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"), null, AccessInfo.IGNORE_READWRITE))},  
				new SubobjectInfo[]{
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
			
			}), new LinkingInfo(capalinker));
		
		typeinfos.add(ti_capability);
		
		TypeInfo ti_expression = new TypeInfo(null, new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.expression_has_text), exatconv), 
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.expression_has_classname)),
			new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.expression_has_class, AccessInfo.IGNORE_WRITE)),
			}), null, new OAVObjectReaderHandler());
				
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "agent")), null, new MappingInfo(ti_capability)));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "belief")), new ObjectInfo(OAVBDIMetaModel.belief_type, tepost), 
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.typedelement_has_classname)),
				new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AccessInfo.IGNORE_WRITE))
			}), null, new OAVObjectReaderHandler()));	
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "beliefref")), new ObjectInfo(OAVBDIMetaModel.beliefreference_type),
			null, null, new OAVObjectReaderHandler()));
		
		TypeInfo ti_belset = new TypeInfo(new XMLInfo(new QName(uri, "beliefset")), new ObjectInfo(OAVBDIMetaModel.beliefset_type, tepost), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.typedelement_has_classname)),
			new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AccessInfo.IGNORE_WRITE))
			}, 
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "facts"), OAVBDIMetaModel.beliefset_has_factsexpression)),
			new SubobjectInfo(new AccessInfo(new QName(uri, "fact"), OAVBDIMetaModel.beliefset_has_facts))
			}), null, new OAVObjectReaderHandler());
		
		typeinfos.add(ti_belset);
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "beliefsetref")), new ObjectInfo(OAVBDIMetaModel.beliefsetreference_type),
			null, null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "fact")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(ti_expression)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "facts")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(ti_expression)));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "plan")), new ObjectInfo(OAVBDIMetaModel.plan_type), 
			new MappingInfo(null, OAVBDIMetaModel.modelelement_has_description, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "parameter"), OAVBDIMetaModel.parameterelement_has_parameters)),	
			new SubobjectInfo(new AccessInfo(new QName(uri, "parameterset"), OAVBDIMetaModel.parameterelement_has_parametersets))	
			}), null, new OAVObjectReaderHandler()));
		
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
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "trigger"), new QName(uri, "internalevent")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.triggerreference_has_ref))})));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "trigger"), new QName(uri, "messageevent")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.triggerreference_has_ref))})));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "trigger"), new QName(uri, "goalfinished")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.triggerreference_has_ref))})));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "trigger"), new QName(uri, "goal")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.triggerreference_has_ref))})));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "waitqueue")), new ObjectInfo(OAVBDIMetaModel.trigger_type)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "waitqueue"), new QName(uri, "internalevent")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.triggerreference_has_ref))})));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "waitqueue"), new QName(uri, "messageevent")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.triggerreference_has_ref))})));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "waitqueue"), new QName(uri, "goalfinished")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.triggerreference_has_ref))})));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "internalevent")), new ObjectInfo(OAVBDIMetaModel.internalevent_type),
			null, null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "internaleventref")), new ObjectInfo(OAVBDIMetaModel.internaleventreference_type),
			null, null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "messageevent")), new ObjectInfo(OAVBDIMetaModel.messageevent_type),
			null, null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "messageeventref")), new ObjectInfo(OAVBDIMetaModel.messageeventreference_type),
			null, null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "match")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
			new MappingInfo(ti_expression)));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "expression")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost),
			new MappingInfo(ti_expression), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "expressionref")), new ObjectInfo(OAVBDIMetaModel.expressionreference_type),
			null, null, new OAVObjectReaderHandler()));

		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "condition")), new ObjectInfo(OAVBDIMetaModel.condition_type, expost),
			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_text), null, new OAVObjectReaderHandler()));
		
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "requiredservice")), new ObjectInfo(OAVBDIMetaModel.requiredservice_type, rspost),
//			new MappingInfo(null, new AttributeInfo[]{
//				new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.requiredservice_has_classname)),
//				new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.requiredservice_has_class, AccessInfo.IGNORE_WRITE))
//			})));
//		
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "binding")), new ObjectInfo(OAVBDIMetaModel.binding_type)));
//		
//		TypeInfo ti_service = new TypeInfo(new XMLInfo(new QName(uri, "providedservice")), new ObjectInfo(OAVBDIMetaModel.providedservice_type, pspost),
//			new MappingInfo(null, new AttributeInfo[]{
//				new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.providedservice_has_classname)),
//				new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.providedservice_has_class, AccessInfo.IGNORE_WRITE))
//			}));
//		typeinfos.add(ti_service);
//		
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "implementation")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
//				new MappingInfo(ti_expression)));
//		
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "property")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost),
//			new MappingInfo(ti_expression)));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "achievegoal"), new QName(uri, "publish")}), new ObjectInfo(OAVBDIMetaModel.publish_type, scpost), 
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.publish_has_classname)),
					new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.publish_has_class, AccessInfo.IGNORE_WRITE))
				})));	
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "maintaingoal"), new QName(uri, "publish")}), new ObjectInfo(OAVBDIMetaModel.publish_type, scpost), 
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.publish_has_classname)),
					new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.publish_has_class, AccessInfo.IGNORE_WRITE))
				})));	
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "performgoal"), new QName(uri, "publish")}), new ObjectInfo(OAVBDIMetaModel.publish_type, scpost), 
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.publish_has_classname)),
					new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.publish_has_class, AccessInfo.IGNORE_WRITE))
				})));	
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "querygoal"), new QName(uri, "publish")}), new ObjectInfo(OAVBDIMetaModel.publish_type, scpost), 
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.publish_has_classname)),
					new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.publish_has_class, AccessInfo.IGNORE_WRITE))
				})));	
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "achievegoalref"), new QName(uri, "publish")}), new ObjectInfo(OAVBDIMetaModel.publish_type, scpost), 
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.publish_has_classname)),
					new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.publish_has_class, AccessInfo.IGNORE_WRITE))
				})));	
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "performgoalgoalref"), new QName(uri, "publish")}), new ObjectInfo(OAVBDIMetaModel.publish_type, scpost), 
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.publish_has_classname)),
					new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.publish_has_class, AccessInfo.IGNORE_WRITE))
				})));	
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "maintaingoalref"), new QName(uri, "publish")}), new ObjectInfo(OAVBDIMetaModel.publish_type, scpost), 
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.publish_has_classname)),
					new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.publish_has_class, AccessInfo.IGNORE_WRITE))
				})));	
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "querygoalref"), new QName(uri, "publish")}), new ObjectInfo(OAVBDIMetaModel.publish_type, scpost), 
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.publish_has_classname)),
					new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.publish_has_class, AccessInfo.IGNORE_WRITE))
				})));	
		
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
					
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "configurations")), null));
		
		SubobjectInfo[]	configsubs	= new SubobjectInfo[]
		{
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
		};
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "agent"), new QName(uri, "configurations"), new QName(uri, "configuration")}),
			new ObjectInfo(null, configproc), new MappingInfo(configtype, null, configsubs), new LinkingInfo(configlinker)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "capability"), new QName(uri, "configurations"), new QName(uri, "configuration")}),
			new ObjectInfo(null, configproc), new MappingInfo(configtype, null, configsubs), new LinkingInfo(configlinker)));
				
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialcapability")), new ObjectInfo(OAVBDIMetaModel.initialcapability_type),
			null, null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialbelief")), new ObjectInfo(OAVBDIMetaModel.configbelief_type),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.configbelief_has_ref))}), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialbeliefset")), new ObjectInfo(OAVBDIMetaModel.configbeliefset_type),
			new MappingInfo(ti_belset, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.configbeliefset_has_ref))}), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialgoal")), new ObjectInfo(OAVBDIMetaModel.configelement_type),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.configelement_has_ref))}), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialplan")), new ObjectInfo(OAVBDIMetaModel.configelement_type),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.configelement_has_ref))}), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialinternalevent")), new ObjectInfo(OAVBDIMetaModel.configelement_type),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.configelement_has_ref))}), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialmessageevent")), new ObjectInfo(OAVBDIMetaModel.configelement_type),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.configelement_has_ref))}), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "endgoal")), new ObjectInfo(OAVBDIMetaModel.configelement_type),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.configelement_has_ref))}), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "endplan")), new ObjectInfo(OAVBDIMetaModel.configelement_type),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.configelement_has_ref))}), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "endinternalevent")), new ObjectInfo(OAVBDIMetaModel.configelement_type),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.configelement_has_ref))}), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "endmessageevent")), new ObjectInfo(OAVBDIMetaModel.configelement_type),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.configelement_has_ref))}), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialgoal"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type),
			null, null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialgoal"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type),
			new MappingInfo(ti_paramset), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialplan"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type),
			null, null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialplan"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type),
			new MappingInfo(ti_paramset), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialinternalevent"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type),
			null, null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialinternalevent"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type),
			new MappingInfo(ti_paramset)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialmessageevent"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type),
			null, null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialmessageevent"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type),
			new MappingInfo(ti_paramset), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endgoal"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type),
			null, null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endgoal"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type),
			new MappingInfo(ti_paramset), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endplan"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type),
			null, null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endplan"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type),
			new MappingInfo(ti_paramset), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endinternalevent"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type),
			null, null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endinternalevent"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type),
			new MappingInfo(ti_paramset), null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endmessageevent"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type),
			null, null, new OAVObjectReaderHandler()));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endmessageevent"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type),
			new MappingInfo(ti_paramset), null, new OAVObjectReaderHandler()));
		
		// Different readers and writers should not work on the same typeinfos
		// because they may alter them (e.g. add additional array types).
		reader = XMLReaderFactory.getInstance().createReader(false, false, new XMLReporter()
		{
			public void report(String msg, String type, Object info, ILocation location) throws Exception
			{
//				System.out.println("XML error: "+msg+", "+type+", "+info+", "+location);
				IContext	context	= (IContext)AReader.READ_CONTEXT.get();
				reportError(context, msg);
			}
		});
		writer = XMLWriterFactory.getInstance().createWriter();
		
		manager = new TypeInfoPathManager(typeinfos);
		readerhandler = new BeanObjectReaderHandler(typeinfos);
		
		writerhandler = new OAVObjectWriterHandler(new HashSet(typeinfos));
	}
	
	/**
	 *  Get the reader instance.
	 */
	public static AReader getReader()
	{
		return reader;
	}
	
	/**
	 *  Get the writer instance.
	 */
	public static AWriter getWriter()
	{
		return writer;
	}
	
	/**
	 *  Get the manager.
	 *  @return the manager.
	 */
	public static TypeInfoPathManager getReaderManager()
	{
		return manager;
	}

	/**
	 *  Get the readerhandler.
	 *  @return the readerhandler.
	 */
	public static IObjectReaderHandler getReaderHandler()
	{
		return readerhandler;
	}

	/**
	 *  Report an error including the line and column.
	 */
	protected static void reportError(IContext context, String error)
	{
		Map	ouc	= (Map)context.getUserContext();
		MultiCollection	report	= (MultiCollection)ouc.get(ComponentXMLReader.CONTEXT_ENTRIES);
		String	pos;
		Tuple	stack	= new Tuple(((ReadContext)context).getStack());
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
	
	protected static Object getOAVRoot(String uri, ReadContext context, Map user, IOAVState state)
	{
		Object parent	= user.get(OAVBDIModelLoader.CONTEXT_OAVROOT);
		if(parent==null)
		{
			parent	= context.getStackElement(0).getTag().equals(new QName(uri, "agent"))
				? state.createObject(OAVBDIMetaModel.agent_type)
				: state.createObject(OAVBDIMetaModel.capability_type);
			user.put(OAVBDIModelLoader.CONTEXT_OAVROOT, parent);
			state.setAttributeValue(parent, OAVBDIMetaModel.modelelement_has_name, ((IModelInfo)context.getRootObject()).getName());
			state.setAttributeValue(parent, OAVBDIMetaModel.modelelement_has_description, ((IModelInfo)context.getRootObject()).getDescription());
		}
		return parent;
	}

	protected static Object getOAVConfiguration(final String uri, Object parent, ReadContext context, Map user, IOAVState state)
	{
		ConfigurationInfo	config	= (ConfigurationInfo)parent;
		parent	= user.get(config);
		if(parent==null)
		{
			parent	= state.createObject(OAVBDIMetaModel.configuration_type);
			user.put(config, parent);
			state.setAttributeValue(parent, OAVBDIMetaModel.modelelement_has_name, config.getName());
			state.setAttributeValue(parent, OAVBDIMetaModel.modelelement_has_description, config.getDescription());
			state.addAttributeValue(getOAVRoot(uri, context, user, state), OAVBDIMetaModel.capability_has_configurations, parent);
		}
		return parent;
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
			Map	ouc	= (Map)context.getUserContext();
			IOAVState state = (IOAVState)ouc.get(OAVObjectReaderHandler.CONTEXT_STATE);
			
			Object	ret	= null;
			String	value	= (String)state.getAttributeValue(object, OAVBDIMetaModel.expression_has_text);
			
			if(value!=null)
			{
				String lang = (String)state.getAttributeValue(object, OAVBDIMetaModel.expression_has_language);
				
				if(state.getType(object).isSubtype(OAVBDIMetaModel.condition_type))
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
								((IModelInfo)context.getRootObject()).getAllImports(), errors);
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
							ret = exp_parser.parseExpression(value, ((IModelInfo)context.getRootObject()).getAllImports(), null, state.getTypeModel().getClassLoader());
						}
						catch(Exception e)
						{
//							e.printStackTrace();
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
								((IModelInfo)context.getRootObject()).getAllImports(), errors);
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
	 *  Perform several post processors..
	 */
	public static class MultiPostProcessor	implements IPostProcessor
	{
		/** The post processors. */
		protected IPostProcessor[] postprocessors;
		
		/**
		 *  Create a new processor.
		 */
		public MultiPostProcessor(IPostProcessor[] postprocessors)
		{
			this.postprocessors = postprocessors;
		}
		
		/**
		 *  Post process.
		 */
		public Object postProcess(IContext context, Object object)
		{
			for(int i=0; i<postprocessors.length; i++)
			{
				postprocessors[i].postProcess(context, object);
			}
			return null;
		}
		
		/**
		 *  Get the pass.
		 */
		public int getPass()
		{
			return postprocessors[0].getPass();
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
			Map	ouc	= (Map)context.getUserContext();
			IOAVState state = (IOAVState)ouc.get(OAVObjectReaderHandler.CONTEXT_STATE);
			String	value	= (String)state.getAttributeValue(object, classnameattr);
			if(value!=null)
			{
				try
				{
					Class	clazz = SReflect.findClass(value, ((IModelInfo)context.getRootObject()).getAllImports(), state.getTypeModel().getClassLoader());
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
