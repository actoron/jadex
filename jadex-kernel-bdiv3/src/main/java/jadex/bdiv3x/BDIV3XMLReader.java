package jadex.bdiv3x;

import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MBody;
import jadex.bdiv3.model.MConfiguration;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.model.MMessageEvent.Direction;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MTrigger;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.component.ComponentXMLReader;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeConverter;
import jadex.xml.AttributeInfo;
import jadex.xml.LinkingInfo;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.reader.AReadContext;
import jadex.xml.reader.IObjectLinker;
import jadex.xml.stax.QName;

import java.util.Iterator;
import java.util.Set;

/**
 *  Reader for loading component XML models into a Java representation states.
 */
public class BDIV3XMLReader extends ComponentXMLReader
{
	public static final IStringObjectConverter msgtypeconv = new IStringObjectConverter()
	{
		public Object convertString(String val, Object context) throws Exception
		{
			return MessageType.getMessageType(val);
		}
	};
	
	public static final IObjectStringConverter remsgtypeconv = new IObjectStringConverter()
	{
		public String convertObject(Object val, Object context)
		{
			return ((MessageType)val).getName();
		}
	};
	
	public static final IStringObjectConverter dirconv = new IStringObjectConverter()
	{
		public Object convertString(String val, Object context) throws Exception
		{
			return Direction.getDirection(val);
		}
	};
	
	public static final IObjectStringConverter redirconv = new IObjectStringConverter()
	{
		public String convertObject(Object val, Object context)
		{
			return ((Direction)val).getString();
		}
	};
	
	public static final IStringObjectConverter pdirconv = new IStringObjectConverter()
	{
		public Object convertString(String val, Object context) throws Exception
		{
			return jadex.bdiv3.model.MParameter.Direction.getDirection(val);
		}
	};
	
	public static final IObjectStringConverter repdirconv = new IObjectStringConverter()
	{
		public String convertObject(Object val, Object context)
		{
			return ((jadex.bdiv3.model.MParameter.Direction)val).getString();
		}
	};
	
	//-------- constructors --------
	
	/**
	 *  Create a new reader.
	 */
	public BDIV3XMLReader()
	{
		super(getXMLMapping());
	}
	
	//-------- methods --------
	
	/**
	 *  Get the type of loaded models.
	 */
	protected String	getModelType(String filename)
	{
		return filename.endsWith(BDIV3XModelLoader.FILE_EXTENSION_AGENT) ? BDIV3XComponentFactory.FILETYPE_AGENT : BDIV3XComponentFactory.FILETYPE_CAPABILITY;
	}

	/**
	 *  Get the XML mapping.
	 */
	public static Set<TypeInfo> getXMLMapping()
	{
		Set<TypeInfo> typeinfos = ComponentXMLReader.getXMLMapping(null);
		
		String uri = "http://jadex.sourceforge.net/jadex";
		
//		// Post processors.
//		IPostProcessor expost = new ExpressionProcessor();
//
//		IObjectStringConverter exconv = new ExpressionToStringConverter();
//		
//		IAttributeConverter exatconv = new AttributeConverter(null, exconv);
//		
//		Set typeinfos = new HashSet(ComponentXMLReader.getXMLMapping(null));
//
//		final String uri = "http://jadex.sourceforge.net/jadex";
//		
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "exclude")),  new ObjectInfo(new IBeanObjectCreator()
//		{
//			public Object createObject(IContext context, Map rawattributes) throws Exception
//			{
//				return rawattributes.get("parameterref");
//			}
//			}), new MappingInfo(null, new AttributeInfo[]
//			{
//				// Using URI doesn't work (bug in reader?)
//				//new AttributeInfo(new AccessInfo(new QName(uri,"parameterref"), null, AccessInfo.IGNORE_READ))
//				new AttributeInfo(new AccessInfo("parameterref", null, AccessInfo.IGNORE_READ))
//			})));
//
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "capabilities"), new QName(uri, "capability")}), new ObjectInfo(OAVBDIMetaModel.capabilityref_type),
//			null, null, new OAVObjectReaderHandler()));
		
		TypeInfo ti_performgoal = new TypeInfo(new XMLInfo(new QName(uri, "performgoal")), new ObjectInfo(MGoal.class),
			null, null);//, new OAVObjectReaderHandler());
		TypeInfo ti_performgoalref = new TypeInfo(new XMLInfo(new QName(uri, "performgoalref")), new ObjectInfo(MGoal.class),
			null, null);//, new OAVObjectReaderHandler());
		TypeInfo ti_achievegoal = new TypeInfo(new XMLInfo(new QName(uri, "achievegoal")), new ObjectInfo(MGoal.class),
			null, null);//, new OAVObjectReaderHandler());
		TypeInfo ti_achievegoalref = new TypeInfo(new XMLInfo(new QName(uri, "achievegoalref")), new ObjectInfo(MGoal.class),
			null, null);//, new OAVObjectReaderHandler());
		TypeInfo ti_querygoal = new TypeInfo(new XMLInfo(new QName(uri, "querygoal")), new ObjectInfo(MGoal.class),
			null, null);//, new OAVObjectReaderHandler());
		TypeInfo ti_querygoalref = new TypeInfo(new XMLInfo(new QName(uri, "querygoalref")), new ObjectInfo(MGoal.class),
			null, null);//, new OAVObjectReaderHandler());
		TypeInfo ti_maintaingoal = new TypeInfo(new XMLInfo(new QName(uri, "maintaingoal")), new ObjectInfo(MGoal.class),
			null, null);//, new OAVObjectReaderHandler());
		TypeInfo ti_maintaingoalref = new TypeInfo(new XMLInfo(new QName(uri, "maintaingoalref")), new ObjectInfo(MGoal.class),
			null, null);//, new OAVObjectReaderHandler());
		TypeInfo ti_metagoal = new TypeInfo(new XMLInfo(new QName(uri, "metagoal")), new ObjectInfo(MGoal.class),
			null, null);//, new OAVObjectReaderHandler());
		TypeInfo ti_metagoalref = new TypeInfo(new XMLInfo(new QName(uri, "metagoalref")), new ObjectInfo(MGoal.class),
			null, null);//, new OAVObjectReaderHandler());
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
		
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "creationcondition")), new ObjectInfo(OAVBDIMetaModel.condition_type, expost), 
//			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_text)));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "dropcondition")), new ObjectInfo(OAVBDIMetaModel.condition_type, expost), 
//			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_text)));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "targetcondition")), new ObjectInfo(OAVBDIMetaModel.condition_type, expost), 
//			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_text)));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "maintaincondition")), new ObjectInfo(OAVBDIMetaModel.condition_type), 
//			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_text)));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "recurcondition")), new ObjectInfo(OAVBDIMetaModel.condition_type), 
//			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_text)));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "metagoal"), new QName(uri, "trigger")}), new ObjectInfo(OAVBDIMetaModel.metagoaltrigger_type)));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "inhibits")), new ObjectInfo(OAVBDIMetaModel.inhibits_type, expost), 
//			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_text, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.inhibits_has_ref))})));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "deliberation")), null));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "unique")), new ObjectInfo(new IBeanObjectCreator()
//			{
//				public Object createObject(IContext context, Map rawattributes) throws Exception
//				{
//					return Boolean.TRUE;
//				}
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
		
		// Link BDI elements to modelinfo.capability instead of modeinfo directly.
		IObjectLinker	capalinker	= new IObjectLinker()
		{
			public void linkObject(Object object, Object parent, Object linkinfo, QName[] pathname, AReadContext context) throws Exception
			{
				if(object instanceof MBelief || object instanceof MGoal || object instanceof MPlan || object instanceof MMessageEvent)
				{
					parent	= ((BDIV3XModel)parent).getCapability();
				}
				
				context.getTopStackElement().getReaderHandler().linkObject(object, parent, linkinfo, pathname, context);
			}
		};
		
//		IPostProcessor	capaproc	= new IPostProcessor()
//		{
//			public Object postProcess(IContext context, Object object)
//			{
//				Map	user	= (Map)context.getUserContext();
//				IOAVState	state	= (IOAVState)user.get(OAVObjectReaderHandler.CONTEXT_STATE);
//				getOAVRoot(uri, (AReadContext)context, user, state);
//				return object;
//			}
//			
//			public int getPass()
//			{
//				return 0;
//			}
//		};
//

		// Link BDI elements to modelinfo.capability.configuration(name) instead of configuration directly.
		IObjectLinker	configlinker	= new IObjectLinker()
		{
			public void linkObject(Object object, Object parent, Object linkinfo, QName[] pathname, AReadContext context) throws Exception
			{
				if(pathname[pathname.length-1].getLocalPart().startsWith("initial") || pathname[pathname.length-1].getLocalPart().startsWith("end"))
				{
					String	config	= ((ConfigurationInfo)parent).getName();
					BDIV3XModel	model	= (BDIV3XModel)context.getStackElement(pathname.length-2).getObject();
					parent	= model.getCapability().getConfiguration(config);
					if(parent==null)
					{
						MConfiguration	mconf	= new MConfiguration(config);
						model.getCapability().addConfiguration(mconf);
						parent	= mconf;
					}
				}
				
				context.getTopStackElement().getReaderHandler().linkObject(object, parent, linkinfo, pathname, context);
			}
		};
		
		TypeInfo ti_capability = new TypeInfo(new XMLInfo(new QName(uri, "capability")), new ObjectInfo(BDIV3XModel.class), 
			new MappingInfo(comptype, null, null, 
				new AttributeInfo[]{
					new AttributeInfo(new AccessInfo(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"), null, AccessInfo.IGNORE_READWRITE))},  
				new SubobjectInfo[]{
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "beliefs"), new QName(uri, "beliefref")}), new AccessInfo(new QName(uri, "beliefref"), "belief")),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "beliefs"), new QName(uri, "beliefsetref")}), new AccessInfo(new QName(uri, "beliefsetref"), "belief")),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "beliefs"), new QName(uri, "belief")}), new AccessInfo(new QName(uri, "belief"), "belief")),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "beliefs"), new QName(uri, "beliefset")}), new AccessInfo(new QName(uri, "beliefset"), "belief")),
		
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "performgoal")}), new AccessInfo(new QName(uri, "performgoal"), "goal"), null, false, ti_performgoal.getObjectInfo()),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "achievegoal")}), new AccessInfo(new QName(uri, "achievegoal"), "goal"), null, false, ti_achievegoal.getObjectInfo()),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "querygoal")}), new AccessInfo(new QName(uri, "querygoal"), "goal"), null, false, ti_querygoal.getObjectInfo()),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "maintaingoal")}), new AccessInfo(new QName(uri, "maintaingoal"), "goal"), null, false, ti_maintaingoal.getObjectInfo()),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "metagoal")}), new AccessInfo(new QName(uri, "metagoal"), "goal"), null, false, ti_metagoal.getObjectInfo()),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "performgoalref")}), new AccessInfo(new QName(uri, "performgoalref"), "goal"), null, false, ti_performgoalref.getObjectInfo()),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "achievegoalref")}), new AccessInfo(new QName(uri, "achievegoalref"), "goal"), null, false, ti_achievegoalref.getObjectInfo()),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "querygoalref")}), new AccessInfo(new QName(uri, "querygoalref"), "goal"), null, false, ti_querygoalref.getObjectInfo()),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "maintaingoalref")}), new AccessInfo(new QName(uri, "maintaingoalref"), "goal"), null, false, ti_maintaingoalref.getObjectInfo()),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "metagoalref")}), new AccessInfo(new QName(uri, "metagoalref"), "goal"), null, false, ti_metagoalref.getObjectInfo()),
		
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "plans"), new QName(uri, "plan")}), new AccessInfo(new QName(uri, "plan"), "plan")),
		
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "messageevent")}), new AccessInfo(new QName(uri, "messageevent"), "messageEvent")),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "messageeventref")}), new AccessInfo(new QName(uri, "messageeventref"), "event")),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "internaleventref")}), new AccessInfo(new QName(uri, "internaleventref"), "event")),
		
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "expressions"), new QName(uri, "expressionref")}), new AccessInfo(new QName(uri, "expressionref"), "expression")),
			
			}), new LinkingInfo(capalinker));
		
		typeinfos.add(ti_capability);
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "agent")), null, new MappingInfo(ti_capability)));
		
//		TypeInfo ti_expression = new TypeInfo(null, new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
//			new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.expression_has_text), exatconv), 
//			new AttributeInfo[]{
//			new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.expression_has_classname)),
//			new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.expression_has_class, AccessInfo.IGNORE_WRITE)),
//			}), null, new OAVObjectReaderHandler());
//				
//		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "belief")), new ObjectInfo(MBelief.class),//, tepost), 
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
			}), null));//, new OAVObjectReaderHandler()));	
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "beliefref")), new ObjectInfo(OAVBDIMetaModel.beliefreference_type),
//			null, null, new OAVObjectReaderHandler()));
//		
//		TypeInfo ti_belset = new TypeInfo(new XMLInfo(new QName(uri, "beliefset")), new ObjectInfo(OAVBDIMetaModel.beliefset_type, tepost), 
//			new MappingInfo(null, new AttributeInfo[]{
//			new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.typedelement_has_classname)),
//			new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AccessInfo.IGNORE_WRITE))
//			}, 
//			new SubobjectInfo[]{
//			new SubobjectInfo(new AccessInfo(new QName(uri, "facts"), OAVBDIMetaModel.beliefset_has_factsexpression)),
//			new SubobjectInfo(new AccessInfo(new QName(uri, "fact"), OAVBDIMetaModel.beliefset_has_facts))
//			}), null, new OAVObjectReaderHandler());
//		
//		typeinfos.add(ti_belset);
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "beliefsetref")), new ObjectInfo(OAVBDIMetaModel.beliefsetreference_type),
//			null, null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "fact")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
//			new MappingInfo(ti_expression)));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "facts")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
//			new MappingInfo(ti_expression)));
//		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "plan")), new ObjectInfo(MPlan.class), 
			new MappingInfo(null, "description", null, null,
			new SubobjectInfo[]{
//			new SubobjectInfo(new AccessInfo(new QName(uri, "parameter"), OAVBDIMetaModel.parameterelement_has_parameters)),	
//			new SubobjectInfo(new AccessInfo(new QName(uri, "parameterset"), OAVBDIMetaModel.parameterelement_has_parametersets))	
			}), null));//, new OAVObjectReaderHandler()));
//		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "body")), new ObjectInfo(MBody.class), 
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv)),
				new AttributeInfo(new AccessInfo("impl", "clazz"), new AttributeConverter(classconv, reclassconv))	// Todo: ignore on write?
//			new AttributeInfo(new AccessInfo("impl", OAVBDIMetaModel.body_has_impl))
			}, null)));//, bopost));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "precondition")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
//			new MappingInfo(ti_expression)));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "contextcondition")), new ObjectInfo(OAVBDIMetaModel.condition_type, expost), 
//			new MappingInfo(ti_expression)));
//			
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "trigger")), new ObjectInfo(MTrigger.class), 
			new MappingInfo(null, 
			new SubobjectInfo[]{
//			new SubobjectInfo(new AccessInfo(new QName(uri, "internalevent"), OAVBDIMetaModel.trigger_has_internalevents)),
//			new SubobjectInfo(new AccessInfo(new QName(uri, "messageevent"), OAVBDIMetaModel.trigger_has_messageevents)),
//			new SubobjectInfo(new AccessInfo(new QName(uri, "goalfinished"), OAVBDIMetaModel.trigger_has_goalfinisheds)),
//			new SubobjectInfo(new AccessInfo(new QName(uri, "factadded"), OAVBDIMetaModel.trigger_has_factaddeds)),
//			new SubobjectInfo(new AccessInfo(new QName(uri, "factremoved"), OAVBDIMetaModel.trigger_has_factremoveds)),
//			new SubobjectInfo(new AccessInfo(new QName(uri, "factchanged"), OAVBDIMetaModel.trigger_has_factchangeds)),
//			new SubobjectInfo(new AccessInfo(new QName(uri, "goal"), OAVBDIMetaModel.plantrigger_has_goals)),
//			new SubobjectInfo(new AccessInfo(new QName(uri, "condition"), OAVBDIMetaModel.plantrigger_has_condition))
			})));
//		
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "trigger"), new QName(uri, "internalevent")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type),
//			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.triggerreference_has_ref))})));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "trigger"), new QName(uri, "messageevent")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type),
//				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.triggerreference_has_ref))})));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "trigger"), new QName(uri, "goalfinished")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type),
//				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.triggerreference_has_ref))})));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "trigger"), new QName(uri, "goal")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type),
//				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.triggerreference_has_ref))})));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "waitqueue")), new ObjectInfo(OAVBDIMetaModel.trigger_type)));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "waitqueue"), new QName(uri, "internalevent")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type),
//				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.triggerreference_has_ref))})));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "waitqueue"), new QName(uri, "messageevent")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type),
//				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.triggerreference_has_ref))})));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "waitqueue"), new QName(uri, "goalfinished")}), new ObjectInfo(OAVBDIMetaModel.triggerreference_type),
//				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cref", OAVBDIMetaModel.triggerreference_has_ref))})));
//		
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "internalevent")), new ObjectInfo(OAVBDIMetaModel.internalevent_type),
//			null, null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "internaleventref")), new ObjectInfo(OAVBDIMetaModel.internaleventreference_type),
//			null, null, new OAVObjectReaderHandler()));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "messageevent")), new ObjectInfo(MMessageEvent.class),
			new MappingInfo(null, null, null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("type", "type"), new AttributeConverter(msgtypeconv, remsgtypeconv)),
				new AttributeInfo(new AccessInfo("direction", "direction"), new AttributeConverter(dirconv, redirconv))},
//				new SubobjectInfo[]{
//					new SubobjectInfo(new AccessInfo(new QName(uri, "parameter"), )),	
//					new SubobjectInfo(new AccessInfo(new QName(uri, "parameterset"), OAVBDIMetaModel.parameterelement_has_parametersets))	
//				}),
			null)));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "messageeventref")), new ObjectInfo(OAVBDIMetaModel.messageeventreference_type),
//			null, null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "match")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
//			new MappingInfo(ti_expression)));
//		
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "expression")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost),
//			new MappingInfo(ti_expression), null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "expressionref")), new ObjectInfo(OAVBDIMetaModel.expressionreference_type),
//			null, null, new OAVObjectReaderHandler()));
//
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "condition")), new ObjectInfo(OAVBDIMetaModel.condition_type, expost),
//			new MappingInfo(null, null, OAVBDIMetaModel.expression_has_text), null, new OAVObjectReaderHandler()));
//				
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "achievegoal"), new QName(uri, "publish")}), new ObjectInfo(OAVBDIMetaModel.publish_type, scpost), 
//				new MappingInfo(null, new AttributeInfo[]{
//					new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.publish_has_classname)),
//					new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.publish_has_class, AccessInfo.IGNORE_WRITE))
//				})));	
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "maintaingoal"), new QName(uri, "publish")}), new ObjectInfo(OAVBDIMetaModel.publish_type, scpost), 
//				new MappingInfo(null, new AttributeInfo[]{
//					new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.publish_has_classname)),
//					new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.publish_has_class, AccessInfo.IGNORE_WRITE))
//				})));	
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "performgoal"), new QName(uri, "publish")}), new ObjectInfo(OAVBDIMetaModel.publish_type, scpost), 
//				new MappingInfo(null, new AttributeInfo[]{
//					new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.publish_has_classname)),
//					new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.publish_has_class, AccessInfo.IGNORE_WRITE))
//				})));	
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "querygoal"), new QName(uri, "publish")}), new ObjectInfo(OAVBDIMetaModel.publish_type, scpost), 
//				new MappingInfo(null, new AttributeInfo[]{
//					new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.publish_has_classname)),
//					new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.publish_has_class, AccessInfo.IGNORE_WRITE))
//				})));	
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "achievegoalref"), new QName(uri, "publish")}), new ObjectInfo(OAVBDIMetaModel.publish_type, scpost), 
//				new MappingInfo(null, new AttributeInfo[]{
//					new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.publish_has_classname)),
//					new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.publish_has_class, AccessInfo.IGNORE_WRITE))
//				})));	
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "performgoalgoalref"), new QName(uri, "publish")}), new ObjectInfo(OAVBDIMetaModel.publish_type, scpost), 
//				new MappingInfo(null, new AttributeInfo[]{
//					new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.publish_has_classname)),
//					new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.publish_has_class, AccessInfo.IGNORE_WRITE))
//				})));	
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "maintaingoalref"), new QName(uri, "publish")}), new ObjectInfo(OAVBDIMetaModel.publish_type, scpost), 
//				new MappingInfo(null, new AttributeInfo[]{
//					new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.publish_has_classname)),
//					new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.publish_has_class, AccessInfo.IGNORE_WRITE))
//				})));	
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "querygoalref"), new QName(uri, "publish")}), new ObjectInfo(OAVBDIMetaModel.publish_type, scpost), 
//				new MappingInfo(null, new AttributeInfo[]{
//					new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.publish_has_classname)),
//					new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.publish_has_class, AccessInfo.IGNORE_WRITE))
//				})));	
//		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "parameter")), new ObjectInfo(MParameter.class), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv)),
			new AttributeInfo(new AccessInfo("direction"), new AttributeConverter(pdirconv, repdirconv))
			})));
		
//		TypeInfo ti_paramset = new TypeInfo(new XMLInfo(new QName(uri, "parameterset")), new ObjectInfo(MParameter.class), 
//			new MappingInfo(null, new AttributeInfo[]{
//			new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.typedelement_has_classname)),
//			new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AccessInfo.IGNORE_WRITE))
//			},
//			new SubobjectInfo[]{
//			new SubobjectInfo(new AccessInfo(new QName(uri, "values"), OAVBDIMetaModel.parameterset_has_valuesexpression)),	
//			new SubobjectInfo(new AccessInfo(new QName(uri, "value"), OAVBDIMetaModel.parameterset_has_values))	
//			}));
//		typeinfos.add(ti_paramset);
//		
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "plan"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.planparameter_type, tepost), 
//			new MappingInfo(null, new AttributeInfo[]{
//			new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.typedelement_has_classname)),
//			new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AccessInfo.IGNORE_WRITE))
//			})));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "plan"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.planparameterset_type, tepost), 
//			new MappingInfo(null, new AttributeInfo[]{
//			new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.typedelement_has_classname)),
//			new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AccessInfo.IGNORE_WRITE))
//			},
//			new SubobjectInfo[]{
//			new SubobjectInfo(new AccessInfo(new QName(uri, "values"), OAVBDIMetaModel.parameterset_has_valuesexpression)),	
//			new SubobjectInfo(new AccessInfo(new QName(uri, "value"), OAVBDIMetaModel.parameterset_has_values))	
//			})));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "value")}), new ObjectInfo(UnparsedExpression.class, null),//new ExpressionProcessor()), 
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
			}, null)));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "values")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
//			new MappingInfo(ti_expression)));
//
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "bindingoptions")), new ObjectInfo(OAVBDIMetaModel.expression_type, expost), 
//			new MappingInfo(ti_expression)));
//					
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "configurations")), null));
//		
		SubobjectInfo[]	configsubs	= new SubobjectInfo[]
		{
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "beliefs"), new QName(uri, "initialbelief")}), new AccessInfo("initialbelief", "initialBelief")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "beliefs"), new QName(uri, "initialbeliefset")}), new AccessInfo("initialbeliefset", "initialBelief")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "initialgoal")}), new AccessInfo("initialgoal", "initialGoal")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "endgoal")}), new AccessInfo("endgoal", "endGoal")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "plans"), new QName(uri, "initialplan")}), new AccessInfo("initialplan", "initialPlan")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "plans"), new QName(uri, "endplan")}), new AccessInfo("endplan", "endPlan")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "initialinternalevent")}), new AccessInfo("initialinternalevent", "initialEvent")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "initialmessageevent")}), new AccessInfo("initialmessageevent", "initialEvent")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "endinternalevent")}), new AccessInfo("endinternalevent", "endEvent")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "endmessageevent")}), new AccessInfo("endmessageevent", "endEvent"))
		};
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "agent"), new QName(uri, "configurations"), new QName(uri, "configuration")}),
			null, new MappingInfo(configtype, null, configsubs), new LinkingInfo(configlinker)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "capability"), new QName(uri, "configurations"), new QName(uri, "configuration")}),
			null, new MappingInfo(configtype, null, configsubs), new LinkingInfo(configlinker)));
//				
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialcapability")), new ObjectInfo(OAVBDIMetaModel.initialcapability_type),
//			null, null, new OAVObjectReaderHandler()));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialbelief")), new ObjectInfo(UnparsedExpression.class),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("ref", "name"))}), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialbeliefset")), new ObjectInfo(UnparsedExpression.class),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("ref", "name"))}), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialgoal")), new ObjectInfo(UnparsedExpression.class),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("ref", "name"))}), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialplan")), new ObjectInfo(UnparsedExpression.class),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("ref", "name"))}), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialinternalevent")), new ObjectInfo(UnparsedExpression.class),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("ref", "name"))}), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialmessageevent")), new ObjectInfo(UnparsedExpression.class),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("ref", "name"))}), null));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "endgoal")), new ObjectInfo(UnparsedExpression.class),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("ref", "name"))}), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "endplan")), new ObjectInfo(UnparsedExpression.class),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("ref", "name"))}), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "endinternalevent")), new ObjectInfo(UnparsedExpression.class),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("ref", "name"))}), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "endmessageevent")), new ObjectInfo(UnparsedExpression.class),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("ref", "name"))}), null));
		
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialgoal"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type),
//			null, null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialgoal"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type),
//			new MappingInfo(ti_paramset), null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialplan"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type),
//			null, null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialplan"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type),
//			new MappingInfo(ti_paramset), null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialinternalevent"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type),
//			null, null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialinternalevent"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type),
//			new MappingInfo(ti_paramset)));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialmessageevent"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type),
//			null, null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialmessageevent"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type),
//			new MappingInfo(ti_paramset), null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endgoal"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type),
//			null, null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endgoal"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type),
//			new MappingInfo(ti_paramset), null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endplan"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type),
//			null, null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endplan"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type),
//			new MappingInfo(ti_paramset), null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endinternalevent"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type),
//			null, null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endinternalevent"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type),
//			new MappingInfo(ti_paramset), null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endmessageevent"), new QName(uri, "parameter")}), new ObjectInfo(OAVBDIMetaModel.configparameter_type),
//			null, null, new OAVObjectReaderHandler()));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endmessageevent"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.configparameterset_type),
//			new MappingInfo(ti_paramset), null, new OAVObjectReaderHandler()));
		
		return typeinfos;
	}
	
}
