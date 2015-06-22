package jadex.bdiv3x;

import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.model.IBDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MBody;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MCapabilityReference;
import jadex.bdiv3.model.MCondition;
import jadex.bdiv3.model.MConfigBeliefElement;
import jadex.bdiv3.model.MConfigParameterElement;
import jadex.bdiv3.model.MConfiguration;
import jadex.bdiv3.model.MDeliberation;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MElementRef;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MInternalEvent;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.model.MMessageEvent.Direction;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.model.MParameter.EvaluationMode;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MPlanParameter;
import jadex.bdiv3.model.MProcessableElement;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bdiv3.model.MTrigger;
import jadex.bdiv3.model.SBDIModel;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.component.ComponentXMLReader;
import jadex.rules.eca.EventType;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeConverter;
import jadex.xml.AttributeInfo;
import jadex.xml.IContext;
import jadex.xml.IPostProcessor;
import jadex.xml.LinkingInfo;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.StackElement;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.IBeanObjectCreator;
import jadex.xml.reader.AReadContext;
import jadex.xml.reader.IObjectLinker;
import jadex.xml.stax.QName;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *  Reader for loading component XML models into a Java representation states.
 */
public class BDIXMLReader extends ComponentXMLReader
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
	
	public static final IStringObjectConverter excludeconv = new IStringObjectConverter()
	{
		public Object convertString(String val, Object context) throws Exception
		{
			return MProcessableElement.ExcludeMode.getExcludeMode(val);
		}
	};
	
	public static final IObjectStringConverter reexcludeconv = new IObjectStringConverter()
	{
		public String convertObject(Object val, Object context)
		{
			return ((ExcludeMode)val).toString();
		}
	};
	
	public static final IStringObjectConverter evamodeconv = new IStringObjectConverter()
	{
		public Object convertString(String val, Object context) throws Exception
		{
			return MParameter.EvaluationMode.getEvaluationMode(val);
		}
	};
	
	public static final IObjectStringConverter reevamodeconv = new IObjectStringConverter()
	{
		public String convertObject(Object val, Object context)
		{
			return ((EvaluationMode)val).toString();
		}
	};
	
	/** The loader constant. */
	public static final String	CONTEXT_LOADER	= "context_loader";
	
	/** The loader for sub capabilities. */
	protected BDIXModelLoader	loader;
	
	//-------- constructors --------
	
	/**
	 *  Create a new reader.
	 */
	public BDIXMLReader(BDIXModelLoader loader)
	{
		super(getXMLMapping());
		this.loader	= loader;
	}
	
	//-------- methods --------
	
	/**
	 *  Add loader to context.
	 */
	public Map<String,Object> createContext()
	{
		Map<String,Object>	ret	= super.createContext();
		ret.put(CONTEXT_LOADER, loader);
		return ret;
	}
	
	/**
	 *  Get the type of loaded models.
	 */
	protected String	getModelType(String filename)
	{
		return filename.endsWith(BDIXModelLoader.FILE_EXTENSION_AGENT) ? BDIXComponentFactory.FILETYPE_AGENT : BDIXComponentFactory.FILETYPE_CAPABILITY;
	}

	/**
	 *  Get the XML mapping.
	 */
	public static Set<TypeInfo> getXMLMapping()
	{
		Set<TypeInfo> typeinfos = ComponentXMLReader.getXMLMapping(null);
		
		final String uri = "http://jadex.sourceforge.net/jadex";
		
		// Post processors.
		final IPostProcessor expost = new ExpressionProcessor();

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
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "capabilities"), new QName(uri, "capability")}), new ObjectInfo(MCapabilityReference.class)));
		
		IObjectLinker condlinker = new IObjectLinker()
		{
			public void linkObject(Object object, Object parent, Object linkinfo, QName[] pathname, AReadContext context) throws Exception
			{
				if(object instanceof MCondition)
				{
					MGoal mgoal = (MGoal)parent;
					String condtype = pathname[pathname.length-1].getLocalPart();
					condtype = condtype.substring(0, condtype.length()-9);
					mgoal.addCondition(condtype, (MCondition)object);
				}
				else
				{
					context.getDefaultHandler().linkObject(object, parent, linkinfo, pathname, context);
				}
			}
		};
		
		TypeInfo ti_performgoal = new TypeInfo(new XMLInfo(new QName(uri, "performgoal")), new ObjectInfo(MGoal.class, new IPostProcessor()
		{
			public Object postProcess(IContext context, Object object)
			{
				// set or-success to false for perform goals
				MGoal mgoal = (MGoal)object;
				mgoal.setOrSuccess(false);
				return object;
			}
			
			public int getPass()
			{
				return 0;
			}
		}),
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("recalculate", "rebuild")), 
				new AttributeInfo(new AccessInfo("exclude", "excludeMode"), new AttributeConverter(excludeconv, reexcludeconv))
			}, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo(new QName(uri, "parameterset"), "parameter")),
			}), new LinkingInfo(condlinker));
		
		
		TypeInfo ti_achievegoal = new TypeInfo(new XMLInfo(new QName(uri, "achievegoal")), new ObjectInfo(MGoal.class),
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("recalculate", "rebuild")), 
				new AttributeInfo(new AccessInfo("exclude", "excludeMode"), new AttributeConverter(excludeconv, reexcludeconv))
			}, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo(new QName(uri, "parameterset"), "parameter")),
			}), new LinkingInfo(condlinker));
			
		TypeInfo ti_querygoal = new TypeInfo(new XMLInfo(new QName(uri, "querygoal")), new ObjectInfo(MGoal.class, new IPostProcessor()
		{
			public Object postProcess(IContext context, Object object)
			{
				// create the implicit target condition
				MGoal mgoal = (MGoal)object;
				MCondition mcond = new MCondition(new UnparsedExpression("targetexp", "jadex.bdiv3.runtime.impl.RGoal.isQueryGoalFinished($goal)"));
				List<EventType> events = new ArrayList<EventType>();
				for(MParameter mparam: mgoal.getParameters())
				{
					jadex.bdiv3.model.MParameter.Direction dir = mparam.getDirection();
					if(MParameter.Direction.OUT.equals(dir) || MParameter.Direction.INOUT.equals(dir))
					{
						// todo: capa not null
						BDIAgentFeature.addParameterEvents(mgoal, null, events, mparam.getName(), null);
					}
				}
				mcond.setEvents(events);
				
				mgoal.addCondition(MGoal.CONDITION_TARGET, mcond);
				return object;
			}
			
			public int getPass()
			{
				return 0;
			}
		}),
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("recalculate", "rebuild")), 
				new AttributeInfo(new AccessInfo("exclude", "excludeMode"), new AttributeConverter(excludeconv, reexcludeconv))
			}, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo(new QName(uri, "parameterset"), "parameter")),
			}), new LinkingInfo(condlinker));
		
		TypeInfo ti_maintaingoal = new TypeInfo(new XMLInfo(new QName(uri, "maintaingoal")), new ObjectInfo(MGoal.class),
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("recalculate", "rebuild")), 
				new AttributeInfo(new AccessInfo("exclude", "excludeMode"), new AttributeConverter(excludeconv, reexcludeconv))
			}, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo(new QName(uri, "parameterset"), "parameter")),
			}), new LinkingInfo(condlinker));
		
		TypeInfo ti_metagoal = new TypeInfo(new XMLInfo(new QName(uri, "metagoal")), new ObjectInfo(MGoal.class, new GoalMetaProc(true)),
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("recalculate", "rebuild")), 
				new AttributeInfo(new AccessInfo("exclude", "excludeMode"), new AttributeConverter(excludeconv, reexcludeconv))
			}, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo(new QName(uri, "parameterset"), "parameter")),
				new SubobjectInfo(new AccessInfo(new QName[]{new QName(uri, "trigger"), new QName(uri, "goal")}, "triggerGoal"))
			}), new LinkingInfo(condlinker));
		
		// reset to not create MTrigger for metagoal goal triggers (are added as trigger goals)
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "metagoal"), new QName(uri, "trigger")}), null)); 
		
		typeinfos.add(ti_performgoal);
		typeinfos.add(ti_achievegoal);
		typeinfos.add(ti_querygoal);
		typeinfos.add(ti_maintaingoal);
		typeinfos.add(ti_metagoal);
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "performgoalref")), new ObjectInfo(MElementRef.class), null, null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "achievegoalref")), new ObjectInfo(MElementRef.class), null, null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "querygoalref")), new ObjectInfo(MElementRef.class), null, null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "maintaingoalref")), new ObjectInfo(MElementRef.class), null, null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "metagoalgoalref")), new ObjectInfo(MElementRef.class), null, null));
		
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

		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "deliberation")), new ObjectInfo(MDeliberation.class),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("cardinalityone", "cardinalityOne"))}, 
			new SubobjectInfo[]{new SubobjectInfo(new XMLInfo(new QName(uri, "inhibits")), new AccessInfo("inhibits", "inhibitionExpression"))})));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "inhibits")}), new ObjectInfo(UnparsedExpression.class, null),
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv)),
				new AttributeInfo(new AccessInfo("ref", "name")),
				new AttributeInfo(new AccessInfo("inhibit", null, AccessInfo.IGNORE_READ)),
			}, null)));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "unique")), new ObjectInfo(new IBeanObjectCreator()
		{
			public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
			{
				return Boolean.TRUE;
			}
		})));
		
		// Find type infos. hack???
		TypeInfo	comptype	= null;
		TypeInfo	configtype	= null;
		for(Iterator<TypeInfo> it=typeinfos.iterator(); (configtype==null || comptype==null) && it.hasNext(); )
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
				if(object instanceof MBelief || object instanceof MGoal || object instanceof MPlan || object instanceof MMessageEvent || object instanceof MInternalEvent 
					|| object instanceof MCapabilityReference || object instanceof MElementRef
					|| (object instanceof UnparsedExpression && pathname[pathname.length-1].getLocalPart().equals("expression")) // hack for bdi expressions
					|| (object instanceof MCondition && pathname[pathname.length-1].getLocalPart().equals("condition")))
				{
					parent	= ((BDIXModel)parent).getCapability();
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
					BDIXModel	model	= (BDIXModel)context.getStackElement(pathname.length-2).getObject();
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
		
		IPostProcessor	capaproc	= new IPostProcessor()
		{
			public Object postProcess(IContext context, Object object)
			{
				BDIXModel model = (BDIXModel)context.getRootObject();
				MCapability mcapa = model.getCapability();
				
				if(mcapa.getGoalPublications()!=null)
				{
					for(Map.Entry<ClassInfo, List<Tuple2<MGoal, String>>> entry: mcapa.getGoalPublications().entrySet())
					{
						ClassInfo key = entry.getKey();
						List<Tuple2<MGoal, String>> vals = entry.getValue();
						Map<String, String> goalnames = new LinkedHashMap<String, String>();
						for(Tuple2<MGoal, String> val: vals)
						{
							goalnames.put(val.getSecondEntity(), val.getFirstEntity().getName());
						}
		//				System.out.println("found goal publish: "+key);
						
						StringBuffer buf = new StringBuffer();
						buf.append("jadex.bdiv3.runtime.impl.GoalDelegationHandler.createServiceImplementation($component, ");
						buf.append(key.getTypeName()+".class, ");
						buf.append("new String[]{");
						for(Iterator<String> it2=goalnames.keySet().iterator(); it2.hasNext(); )
						{
							buf.append("\"").append(it2.next()).append("\"");
							if(it2.hasNext())
								buf.append(", ");
						}
						buf.append("}, ");
						buf.append("new String[]{");
						for(Iterator<String> it2=goalnames.keySet().iterator(); it2.hasNext(); )
						{
							buf.append("\"").append(goalnames.get(it2.next())).append("\"");
							if(it2.hasNext())
								buf.append(", ");
						}
						buf.append("}");
						buf.append(")");
						
		//				System.out.println("service creation expression: "+buf.toString());
						
						ProvidedServiceImplementation psi = new ProvidedServiceImplementation(null, buf.toString(), 
							BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED, null, null);
						
						// todo: allow specifying scope
						model.addProvidedService(new ProvidedServiceInfo(null, key, psi, null, null, null));
					}
				}
				
				// Load subcapabilities.
				Map<String, IBDIModel>	subcaps	= new LinkedHashMap<String, IBDIModel>();
				for(MCapabilityReference subcap: model.getCapability().getCapabilities())
				{
					try
					{
						IResourceIdentifier	rid	= (IResourceIdentifier)((Map<String,Object>)context.getUserContext()).get(CONTEXT_RID);
						IComponentIdentifier	root	= (IComponentIdentifier)((Map<String,Object>)context.getUserContext()).get(CONTEXT_ROOT);
						BDIXModelLoader	loader	= (BDIXModelLoader)((Map<String,Object>)context.getUserContext()).get(CONTEXT_LOADER);
						BDIXModel	cmodel	= (BDIXModel)loader.loadCapabilityModel(subcap.getFile(), model.getAllImports(),
							rid, context.getClassLoader(), new Object[]{rid, root}).getModelInfo();
						subcaps.put(subcap.getName(), cmodel);
					}
					catch(Exception e)
					{
						throw new RuntimeException(e);
					}
				}
				SBDIModel.mergeSubcapabilities(model, subcaps);
				
				return null;
			}
			
			public int getPass()
			{
				return 1;
			}
		};
		
		TypeInfo ti_capability = new TypeInfo(new XMLInfo(new QName(uri, "capability")), new ObjectInfo(BDIXModel.class, capaproc), 
			new MappingInfo(comptype, null, null, 
				new AttributeInfo[]{
					new AttributeInfo(new AccessInfo(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"), null, AccessInfo.IGNORE_READWRITE))},  
				new SubobjectInfo[]{
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "beliefs"), new QName(uri, "beliefref")}), new AccessInfo(new QName(uri, "beliefref"), "elementRef")),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "beliefs"), new QName(uri, "beliefsetref")}), new AccessInfo(new QName(uri, "beliefsetref"), "elementRef")),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "beliefs"), new QName(uri, "belief")}), new AccessInfo(new QName(uri, "belief"), "belief")),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "beliefs"), new QName(uri, "beliefset")}), new AccessInfo(new QName(uri, "beliefset"), "belief")),
		
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "performgoal")}), new AccessInfo(new QName(uri, "performgoal"), "goal"), null, false, ti_performgoal.getObjectInfo()),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "achievegoal")}), new AccessInfo(new QName(uri, "achievegoal"), "goal"), null, false, ti_achievegoal.getObjectInfo()),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "querygoal")}), new AccessInfo(new QName(uri, "querygoal"), "goal"), null, false, ti_querygoal.getObjectInfo()),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "maintaingoal")}), new AccessInfo(new QName(uri, "maintaingoal"), "goal"), null, false, ti_maintaingoal.getObjectInfo()),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "metagoal")}), new AccessInfo(new QName(uri, "metagoal"), "goal"), null, false, ti_metagoal.getObjectInfo()),
					
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "performgoalref")}), new AccessInfo(new QName(uri, "performgoalref"), "elementRef")),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "achievegoalref")}), new AccessInfo(new QName(uri, "achievegoalref"), "elementRef")),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "querygoalref")}), new AccessInfo(new QName(uri, "querygoalref"), "elementRef")),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "maintaingoalref")}), new AccessInfo(new QName(uri, "maintaingoalref"), "elementRef")),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "goals"), new QName(uri, "metagoalref")}), new AccessInfo(new QName(uri, "metagoalref"), "elementRef")),
		
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "plans"), new QName(uri, "plan")}), new AccessInfo(new QName(uri, "plan"), "plan")),
		
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "messageevent")}), new AccessInfo(new QName(uri, "messageevent"), "messageEvent")),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "messageeventref")}), new AccessInfo(new QName(uri, "messageeventref"), "messageEvent")),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "internalevent")}), new AccessInfo(new QName(uri, "internalevent"), "internalEvent")),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "events"), new QName(uri, "internaleventref")}), new AccessInfo(new QName(uri, "internaleventref"), "internalEvent")),
		
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "expressions"), new QName(uri, "expression")}), new AccessInfo(new QName(uri, "expression"), "expression")),
					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "expressions"), new QName(uri, "expressionref")}), new AccessInfo(new QName(uri, "expressionref"), "expression")),
//					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "expressions"), new QName(uri, "expression")}), new AccessInfo(new QName(uri, "expression"), "expression")),
//					new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "expressions"), new QName(uri, "expressionref")}), new AccessInfo(new QName(uri, "expressionref"), "expression")),
			
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
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "belief")), new ObjectInfo(MBelief.class, 
			new IPostProcessor()
			{
				public Object postProcess(IContext context, Object object)
				{
					MBelief mbel = (MBelief)object;
					mbel.setMulti(false);
					return mbel;
				}
				
				public int getPass()
				{
					return 0;
				}
			}),
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv)),
				new AttributeInfo(new AccessInfo("argument", "exported")),
				new AttributeInfo(new AccessInfo("evaluationmode", "evaluationMode"), new AttributeConverter(evamodeconv, reevamodeconv))
			}, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo(new QName(uri, "fact"), "defaultFact"))
			}), null));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "beliefset")), new ObjectInfo(MBelief.class, 
			new IPostProcessor()
			{
				public Object postProcess(IContext context, Object object)
				{
					MBelief mbel = (MBelief)object;
					mbel.setMulti(true);
					return mbel;
				}
				
				public int getPass()
				{
					return 0;
				}
			}), 
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv)),
				new AttributeInfo(new AccessInfo("argument", "exported")),
				new AttributeInfo(new AccessInfo("evaluationmode", "evaluationMode"), new AttributeConverter(evamodeconv, reevamodeconv)),
			}, new SubobjectInfo[]{
				// because there is only MBelief the facts expression is stored as default fact
				// and multiple facts are added to a list
				new SubobjectInfo(new AccessInfo(new QName(uri, "fact"), "defaultFacts")),
				new SubobjectInfo(new AccessInfo(new QName(uri, "facts"), "defaultFact"))
			}), null));//, new OAVObjectReaderHandler()));	
		
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "beliefref")), new ObjectInfo(MElementRef.class),	null, null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "beliefsetref")), new ObjectInfo(MElementRef.class),	null, null));
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
				new SubobjectInfo(new AccessInfo(new QName(uri, "parameterset"), "parameter")),
				new SubobjectInfo(new AccessInfo(new QName(uri, "contextcondition"), "contextCondition"))	
			}), null));
//		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "body")), new ObjectInfo(MBody.class), 
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv)),
				new AttributeInfo(new AccessInfo("impl", "clazz"), new AttributeConverter(classconv, reclassconv)),	// Todo: ignore on write?
				new AttributeInfo(new AccessInfo("service", "serviceName")),
				new AttributeInfo(new AccessInfo("method", "serviceMethodName"))
//			new AttributeInfo(new AccessInfo("impl", OAVBDIMetaModel.body_has_impl))
			}, null)));//, bopost));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "precondition")), new ObjectInfo(UnparsedExpression.class, expost),
			new MappingInfo(null, null, "value")));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "contextcondition")), new ObjectInfo(OAVBDIMetaModel.condition_type, expost), 
//			new MappingInfo(ti_expression)));
			
		IPostProcessor mtrpp = new IPostProcessor()
		{
			public Object postProcess(IContext context, Object object)
			{
				BDIXModel model = (BDIXModel)context.getRootObject();
				MCapability mcapa = model.getCapability();
				
				MTrigger mtr = (MTrigger)object;
				
				List<String> names = mtr.getMessageNames();
				if(names!=null)
				{
					for(String name: names)
					{
						// todo: capa scoping?!
						name	= name.replace(".", MElement.CAPABILITY_SEPARATOR);
						if(mcapa.getMessageEvent(name)==null)
							throw new RuntimeException("Message event not found: "+name);
						mtr.addMessageEvent(mcapa.getMessageEvent(name));
					}
				}
				
				names = mtr.getInternalEventNames();
				if(names!=null)
				{
					for(String name: names)
					{
						// todo: capa scoping?!
						name	= name.replace(".", MElement.CAPABILITY_SEPARATOR);
						if(mcapa.getInternalEvent(name)==null)
							throw new RuntimeException("Internal event not found: "+name);
						mtr.addInternalEvent(mcapa.getInternalEvent(name));
					}
				}
				
				names = mtr.getGoalNames();
				if(names!=null)
				{
					for(String name: names)
					{
						// todo: capa scoping?!
						name	= name.replace(".", MElement.CAPABILITY_SEPARATOR);
						if(mcapa.getGoal(name)==null)
							throw new RuntimeException("Goal not found: "+name);
						mtr.addGoal(mcapa.getGoal(name));
					}
				}
				
				names = mtr.getGoalFinishedNames();
				if(names!=null)
				{
					for(String name: names)
					{
						// todo: capa scoping?!
						name	= name.replace(".", MElement.CAPABILITY_SEPARATOR);
						if(mcapa.getGoal(name)==null)
							throw new RuntimeException("GoalFinished not found: "+name);
						mtr.addGoalFinished(mcapa.getGoal(name));
					}
				}
				
				return null;
			}
			
			public int getPass()
			{
				return 1;
			}
		};
		
		AttributeInfo[]	trigger_attributes	= new AttributeInfo[]
		{
			new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "factchanged"), new QName("ref")}, "factChanged")),
			new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "factadded"), new QName("ref")}, "factAdded")),
			new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "factremoved"), new QName("ref")}, "factRemoved")),
			new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "internalevent"), new QName("ref")}, "internalEventName")),
			new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "messageevent"), new QName("ref")}, "messageName")),
			new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "goal"), new QName("ref")}, "goalName")),
			new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "goalfinished"), new QName("ref")}, "goalFinishedName")),
			new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "factchanged"), new QName("cref")}, "factChanged")),
			new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "factadded"), new QName("cref")}, "factAdded")),
			new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "factremoved"), new QName("cref")}, "factRemoved")),
			new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "internalevent"), new QName("cref")}, "internalEventName")),
			new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "messageevent"), new QName("cref")}, "messageName")),
			new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "goal"), new QName("cref")}, "goalName")),
			new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "goalfinished"), new QName("cref")}, "goalFinishedName"))
		};
		
		// Special handling to add match-expressions directly to the trigger.
		LinkingInfo	matchlinker	= new LinkingInfo(new IObjectLinker()
		{
			public void linkObject(Object object, Object parent, Object linkinfo, QName[] pathname, AReadContext context) throws Exception
			{
				if(context.getStackElement(context.getStackSize()-1).getTag().equals(new QName(uri, "match")))
				{
					MTrigger mtrig = (MTrigger)context.getStackElement(context.getStackSize()-3).getObject();
					String	ref	= (String)context.getStackElement(context.getStackSize()-2).getRawAttributes().get("ref");
					if(ref==null)
					{
						ref	= (String)context.getStackElement(context.getStackSize()-2).getRawAttributes().get("cref");
					}
					mtrig.addGoalMatchExpression(ref, (UnparsedExpression)object);	// Todo: support match expression on other elements as allowed in schema 
				}
				else if(context.getTopStackElement().getTag().equals(new QName(uri, "factadded")))
				{
					MTrigger mtrig = (MTrigger)context.getStackElement(context.getStackSize()-2).getObject();
					String	name	= ((String)object).replace(".", MElement.CAPABILITY_SEPARATOR);
					mtrig.addFactAdded(name);
				}
				else if(context.getTopStackElement().getTag().equals(new QName(uri, "factremoved")))
				{
					MTrigger mtrig = (MTrigger)context.getStackElement(context.getStackSize()-2).getObject();
					String	name	= ((String)object).replace(".", MElement.CAPABILITY_SEPARATOR);
					mtrig.addFactRemoved(name);
				}
				else if(context.getTopStackElement().getTag().equals(new QName(uri, "factchanged")))
				{
					MTrigger mtrig = (MTrigger)context.getStackElement(context.getStackSize()-2).getObject();
					String	name	= ((String)object).replace(".", MElement.CAPABILITY_SEPARATOR);
					mtrig.addFactChangeds(name);
				}
				else
				{
					context.getTopStackElement().getReaderHandler().linkObject(object, parent, linkinfo, pathname, context);
				}
			}
		});
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "trigger")), new ObjectInfo(MTrigger.class, mtrpp), 
			new MappingInfo(null, trigger_attributes), matchlinker));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "waitqueue")), new ObjectInfo(MTrigger.class, mtrpp), 
			new MappingInfo(null, trigger_attributes), matchlinker));

		// Ignore <goal>, etc. tags inside trigger/waitqueue
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "trigger"), new QName(uri, "internalevent")}), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "trigger"), new QName(uri, "messageevent")}), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "trigger"), new QName(uri, "goal")}), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "waitqueue"), new QName(uri, "internalevent")}), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "waitqueue"), new QName(uri, "messageevent")}), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "waitqueue"), new QName(uri, "goal")}), null));

		IBeanObjectCreator boc = new IBeanObjectCreator()
		{
			public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
			{
				return rawattributes.get("ref");
			}
		};
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "factadded")), new ObjectInfo(boc), new MappingInfo(null, null, "value", 
			new AttributeInfo[]{new AttributeInfo(new AccessInfo("ref", null, AccessInfo.IGNORE_READ))}), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "factremoved")), new ObjectInfo(boc), new MappingInfo(null, null, "value", 
			new AttributeInfo[]{new AttributeInfo(new AccessInfo("ref", null, AccessInfo.IGNORE_READ))}), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "factchanged")), new ObjectInfo(boc), new MappingInfo(null, null, "value", 
			new AttributeInfo[]{new AttributeInfo(new AccessInfo("ref", null, AccessInfo.IGNORE_READ))}), null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "metagoal"), new QName(uri, "trigger"), new QName(uri, "goal")}), new ObjectInfo(boc), new MappingInfo(null, null, "value", 
			new AttributeInfo[]{new AttributeInfo(new AccessInfo("ref", null, AccessInfo.IGNORE_READ))}), null));
		
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "factremoved")), new ObjectInfo(boc), null, null));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "factchanged")), new ObjectInfo(boc), null, null));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "internalevent")), new ObjectInfo(MInternalEvent.class),
			null, null));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "internaleventref")), new ObjectInfo(OAVBDIMetaModel.internaleventreference_type),
//			null, null, new OAVObjectReaderHandler()));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "messageevent")), new ObjectInfo(MMessageEvent.class),
			new MappingInfo(null, null, null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("type", "type"), new AttributeConverter(msgtypeconv, remsgtypeconv)),
				new AttributeInfo(new AccessInfo("direction", "direction"), new AttributeConverter(dirconv, redirconv))},
				new SubobjectInfo[]{
					new SubobjectInfo(new XMLInfo(new QName(uri, "match")), new AccessInfo("match", "matchExpression")),
//					new SubobjectInfo(new XMLInfo(new QName(uri, "parameter")), new AccessInfo("value", "defaultValue")),
//					new SubobjectInfo(new AccessInfo(new QName(uri, "parameter"), new )),	
//					new SubobjectInfo(new AccessInfo(new QName(uri, "parameterset"), OAVBDIMetaModel.parameterelement_has_parametersets))	
				}),
			null));
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

		// Exchange expression with condition for trigger.
		IPostProcessor condexpost = new IPostProcessor()
		{
			public Object postProcess(IContext context, Object object)
			{
				expost.postProcess(context, object);
				MCondition cond = new MCondition();
				cond.setExpression((UnparsedExpression)object);
				
				AReadContext ar	= (AReadContext)context;
				MElement pe	= null;
				for(StackElement se: ar.getStack())
				{
					if(se.getObject() instanceof MGoal || se.getObject() instanceof MPlan)
					{
						pe	= (MElement)se.getObject();
					}
				}				
				cond.initEvents(pe);
				
				String bels = ar.getTopStackElement().getRawAttributes()==null? null: ar.getTopStackElement().getRawAttributes().get("beliefs");
				if(bels!=null)
				{
					StringTokenizer stok = new StringTokenizer(bels, ",");
					while(stok.hasMoreElements())
					{
						String tok = stok.nextToken();
						cond.addEvent(new EventType(ChangeEvent.BELIEFCHANGED, tok));
						cond.addEvent(new EventType(ChangeEvent.FACTCHANGED, tok));
						cond.addEvent(new EventType(ChangeEvent.FACTADDED, tok));
						cond.addEvent(new EventType(ChangeEvent.FACTREMOVED, tok));
					}
				}
				String params = ar.getTopStackElement().getRawAttributes()==null? null: ar.getTopStackElement().getRawAttributes().get("parameters");
				if(params!=null)
				{
					StringTokenizer stok = new StringTokenizer(params, ",");
					while(stok.hasMoreElements())
					{
						String tok = stok.nextToken();
						cond.addEvent(new EventType(ChangeEvent.PARAMETERCHANGED, pe.getName(), tok));
						cond.addEvent(new EventType(ChangeEvent.VALUECHANGED, pe.getName(), tok));
						cond.addEvent(new EventType(ChangeEvent.VALUEADDED, pe.getName(), tok));
						cond.addEvent(new EventType(ChangeEvent.VALUEREMOVED, pe.getName(), tok));
					}
				}
				String rawevs = ar.getTopStackElement().getRawAttributes()==null? null: ar.getTopStackElement().getRawAttributes().get("rawevents");
				if(rawevs!=null)
				{
					StringTokenizer stok = new StringTokenizer(rawevs, ",");
					while(stok.hasMoreElements())
					{
						String tok = stok.nextToken();
						cond.addEvent(new EventType(tok));
					}
				}
				
				return cond;
			}
			
			public int getPass()
			{
				return 0;
			}
		};
		AttributeInfo[]	condattrs	= new AttributeInfo[]
		{
			new AttributeInfo(new AccessInfo("beliefs", null, AccessInfo.IGNORE_READ)),
			new AttributeInfo(new AccessInfo("parameters", null, AccessInfo.IGNORE_READ))
		};
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "condition")), new ObjectInfo(UnparsedExpression.class, condexpost),
			new MappingInfo(null, null, "value", condattrs)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "creationcondition")), new ObjectInfo(UnparsedExpression.class, condexpost),
			new MappingInfo(null, null, "value", condattrs)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "dropcondition")), new ObjectInfo(UnparsedExpression.class, condexpost),
			new MappingInfo(null, null, "value", condattrs)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "targetcondition")), new ObjectInfo(UnparsedExpression.class, condexpost),
			new MappingInfo(null, null, "value", condattrs)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "maintaincondition")), new ObjectInfo(UnparsedExpression.class, condexpost),
			new MappingInfo(null, null, "value", condattrs)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "recurcondition")), new ObjectInfo(UnparsedExpression.class, condexpost),
			new MappingInfo(null, null, "value", condattrs)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "contextcondition")), new ObjectInfo(UnparsedExpression.class, condexpost),
			new MappingInfo(null, null, "value", condattrs)));
		
		IPostProcessor pubproc = new IPostProcessor()
		{
			public Object postProcess(IContext context, Object object)
			{
				try
				{
					BDIXModel model = (BDIXModel)context.getRootObject();
					MCapability mcapa = model.getCapability();
					AReadContext ar = (AReadContext)context;
					Map<String, String> rawattrs = ar.getTopStackElement().getRawAttributes();
					String service = rawattrs.get("class");
					String method = rawattrs.get("method");
					MGoal mgoal = (MGoal)ar.getStackElement(ar.getStackSize()-2).getObject();
					
					Class<?> iface = SReflect.findClass(service, model.getAllImports(), ar.getClassLoader());
					ClassInfo ci = new ClassInfo(iface.getName());
					
					// Just use first method if no name is given
					if(method==null)
						method = iface.getDeclaredMethods()[0].getName();
					
					mcapa.addGoalPublication(ci, mgoal, method);
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
				}
				return null;
			}
			
			public int getPass()
			{
				return 0;
			}
		};
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "achievegoal"), new QName(uri, "publish")}), new ObjectInfo(null, pubproc), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", null, AccessInfo.IGNORE_READ)),
			new AttributeInfo(new AccessInfo("method", null, AccessInfo.IGNORE_READ))
		})));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "performgoal"), new QName(uri, "publish")}), new ObjectInfo(null, pubproc), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", null, AccessInfo.IGNORE_READ)),
			new AttributeInfo(new AccessInfo("method", null, AccessInfo.IGNORE_READ))
		})));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "querygoal"), new QName(uri, "publish")}), new ObjectInfo(null, pubproc), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", null, AccessInfo.IGNORE_READ)),
			new AttributeInfo(new AccessInfo("method", null, AccessInfo.IGNORE_READ))
		})));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "maintaingoal"), new QName(uri, "publish")}), new ObjectInfo(null, pubproc), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", null, AccessInfo.IGNORE_READ)),
			new AttributeInfo(new AccessInfo("method", null, AccessInfo.IGNORE_READ))
		})));
		
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
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "parameter")), new ObjectInfo(MParameter.class, 
			new ParamMultiProc(false)), 
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv)),
				new AttributeInfo(new AccessInfo("direction"), new AttributeConverter(pdirconv, repdirconv)),
				new AttributeInfo(new AccessInfo("updaterate", "updateRate")),
				new AttributeInfo(new AccessInfo("evaluationmode", "evaluationMode"), new AttributeConverter(evamodeconv, reevamodeconv)),
				new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "servicemapping"), new QName("ref")}, "serviceMapping"))
			}, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo(new QName(uri, "value"), "defaultValue")),
				new SubobjectInfo(new AccessInfo(new QName(uri, "bindingoptions"), "bindingOptions"))
			}
		)));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "parameterset")), new ObjectInfo(MParameter.class, 
			new ParamMultiProc(true)), 
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv)),
				new AttributeInfo(new AccessInfo("direction"), new AttributeConverter(pdirconv, repdirconv)),
				new AttributeInfo(new AccessInfo("updaterate", "updateRate")),
				new AttributeInfo(new AccessInfo("evaluationmode", "evaluationMode"), new AttributeConverter(evamodeconv, reevamodeconv)),
//				new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "messageeventmapping"), new QName("ref")}, "messageEventMapping")),
//				new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "goalmapping"), new QName("ref")}, "goalMapping")),
				new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "servicemapping"), new QName("ref")}, "serviceMapping"))
			}, new SubobjectInfo[]{
				// because there is only MParameter the values expression is stored as default value
				// and multiple facts are added to a list
				new SubobjectInfo(new AccessInfo(new QName(uri, "value"), "defaultValues")),
				new SubobjectInfo(new AccessInfo(new QName(uri, "values"), "defaultValue"))
			}), null));//, new OAVObjectReaderHandler()));	
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "plan"), new QName(uri, "parameter")}), new ObjectInfo(MPlanParameter.class, 
			new ParamMultiProc(false)), 
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv)),
				new AttributeInfo(new AccessInfo("direction"), new AttributeConverter(pdirconv, repdirconv)),
				new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "messageeventmapping"), new QName("ref")}, "messageEventMapping")),
				new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "goalmapping"), new QName("ref")}, "goalMapping")),
				new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "servicemapping"), new QName("ref")}, "serviceMapping"))
			}, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo(new QName(uri, "value"), "defaultValue")),
				new SubobjectInfo(new AccessInfo(new QName(uri, "bindingoptions"), "bindingOptions"))
			})));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "plan"), new QName(uri, "parameterset")}), new ObjectInfo(MPlanParameter.class, 
			new ParamMultiProc(true)), 
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv)),
				new AttributeInfo(new AccessInfo("direction"), new AttributeConverter(pdirconv, repdirconv)),
				new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "messageeventmapping"), new QName("ref")}, "messageEventMapping")),
				new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "goalmapping"), new QName("ref")}, "goalMapping")),
				new AttributeInfo(new AccessInfo(new QName[]{new QName(uri, "servicemapping"), new QName("ref")}, "serviceMapping"))
			}, new SubobjectInfo[]{
				// because there is only MParameter the values expression is stored as default value
				// and multiple facts are added to a list
				new SubobjectInfo(new AccessInfo(new QName(uri, "value"), "defaultValues")),
				new SubobjectInfo(new AccessInfo(new QName(uri, "values"), "defaultValue"))
			})));
		
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "plan"), new QName(uri, "parameterset")}), new ObjectInfo(OAVBDIMetaModel.planparameterset_type, tepost), 
//			new MappingInfo(null, new AttributeInfo[]{
//			new AttributeInfo(new AccessInfo("class", OAVBDIMetaModel.typedelement_has_classname)),
//			new AttributeInfo(new AccessInfo((String)null, OAVBDIMetaModel.typedelement_has_class, AccessInfo.IGNORE_WRITE))
//			},
//			new SubobjectInfo[]{
//			new SubobjectInfo(new AccessInfo(new QName(uri, "values"), OAVBDIMetaModel.parameterset_has_valuesexpression)),	
//			new SubobjectInfo(new AccessInfo(new QName(uri, "value"), OAVBDIMetaModel.parameterset_has_values))	
//			})));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "value")}), new ObjectInfo(UnparsedExpression.class, expost),
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
			}, null)));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "fact")}), new ObjectInfo(UnparsedExpression.class, expost),
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
			}, null)));

		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "facts")}), new ObjectInfo(UnparsedExpression.class, expost),
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
			}, null)));

		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "match")}), new ObjectInfo(UnparsedExpression.class, expost), 
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
			}, null)));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "expression")}), new ObjectInfo(UnparsedExpression.class, expost),
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
			}, null)));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "bindingoptions")}), new ObjectInfo(UnparsedExpression.class, expost),
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

//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialbelief"), new QName(uri, "fact")}), new ObjectInfo(String.class)));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialbeliefset"), new QName(uri, "fact")}), new ObjectInfo(String.class)));
//		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialbeliefset"), new QName(uri, "facts")}), new ObjectInfo(String.class)));
		
		MappingInfo	configelementmapping	= new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("ref", "name")),
			new AttributeInfo(new AccessInfo("cref", "name"))
		});
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialbelief")), new ObjectInfo(MConfigBeliefElement.class), configelementmapping, null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialbeliefset")), new ObjectInfo(MConfigBeliefElement.class), configelementmapping, null));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialgoal")), new ObjectInfo(MConfigParameterElement.class), configelementmapping, null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialplan")), new ObjectInfo(MConfigParameterElement.class), configelementmapping, null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialinternalevent")), new ObjectInfo(MConfigParameterElement.class), configelementmapping, null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "initialmessageevent")), new ObjectInfo(MConfigParameterElement.class), configelementmapping, null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "endgoal")), new ObjectInfo(MConfigParameterElement.class), configelementmapping, null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "endplan")), new ObjectInfo(MConfigParameterElement.class), configelementmapping, null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "endinternalevent")), new ObjectInfo(MConfigParameterElement.class), configelementmapping, null));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "endmessageevent")), new ObjectInfo(MConfigParameterElement.class), configelementmapping, null));
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialgoal"), new QName(uri, "parameter")}), new ObjectInfo(null)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialgoal"), new QName(uri, "parameterset")}), new ObjectInfo(null)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialplan"), new QName(uri, "parameter")}), new ObjectInfo(null)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialplan"), new QName(uri, "parameterset")}), new ObjectInfo(null)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialinternalevent"), new QName(uri, "parameter")}), new ObjectInfo(null)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialinternalevent"), new QName(uri, "parameterset")}), new ObjectInfo(null)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialmessageevent"), new QName(uri, "parameter")}), new ObjectInfo(null)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "initialmessageevent"), new QName(uri, "parameterset")}), new ObjectInfo(null)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endgoal"), new QName(uri, "parameter")}), new ObjectInfo(null)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endgoal"), new QName(uri, "parameterset")}), new ObjectInfo(null)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endplan"), new QName(uri, "parameter")}), new ObjectInfo(null)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endplan"), new QName(uri, "parameterset")}), new ObjectInfo(null)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endinternalevent"), new QName(uri, "parameter")}), new ObjectInfo(null)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endinternalevent"), new QName(uri, "parameterset")}), new ObjectInfo(null)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endmessageevent"), new QName(uri, "parameter")}), new ObjectInfo(null)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "endmessageevent"), new QName(uri, "parameterset")}), new ObjectInfo(null)));
		
		return typeinfos;
	}
	
	/**
	 * 
	 */
	public static class ParamMultiProc implements IPostProcessor
	{
		boolean multi;
		
		ParamMultiProc(boolean multi)
		{
			this.multi = multi;
		}
		
		public Object postProcess(IContext context, Object object)
		{
			MParameter mparam = (MParameter)object;
			mparam.setMulti(multi);
			return mparam;
		}
		
		public int getPass()
		{
			return 0;
		}
	}
	
	/**
	 * 
	 */
	public static class GoalMetaProc implements IPostProcessor
	{
		boolean meta;
		
		GoalMetaProc(boolean meta)
		{
			this.meta = meta;
		}
		
		public Object postProcess(IContext context, Object object)
		{
			MGoal mgoal = (MGoal)object;
			mgoal.setMetagoal(meta);
			return mgoal;
		}
		
		public int getPass()
		{
			return 0;
		}
	}
}