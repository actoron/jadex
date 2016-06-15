package jadex.bpmn.editor.model.legacy;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.stream.XMLStreamException;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MAnnotation;
import jadex.bpmn.model.MAnnotationDetail;
import jadex.bpmn.model.MArtifact;
import jadex.bpmn.model.MAssociation;
import jadex.bpmn.model.MAssociationTarget;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MContextVariable;
import jadex.bpmn.model.MEdge;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MMessagingEdge;
import jadex.bpmn.model.MNamedIdElement;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.model.MTask;
import jadex.bridge.AbstractErrorReportBuilder;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IErrorReport;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.commons.IFilter;
import jadex.commons.ResourceInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.collection.IndexMap;
import jadex.commons.collection.MultiCollection;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeInfo;
import jadex.xml.IContext;
import jadex.xml.IPostProcessor;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.StackElement;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanAccessInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.bean.IBeanObjectCreator;
import jadex.xml.reader.AReadContext;
import jadex.xml.reader.AReader;
import jadex.xml.reader.IObjectReaderHandler;
import jadex.xml.reader.XMLReaderFactory;
import jadex.xml.stax.ILocation;
import jadex.xml.stax.QName;
import jadex.xml.stax.XMLReporter;

/**
 *  Reader for loading Bpmn XML models into a Java representation states.
 */
public class BpmnXMLReader
{
	//-------- constants --------
	
	/** Key for error entries in read context. */
	public static final String CONTEXT_ENTRIES = "entries";
	
	/** Key for sequence edges in read context. */
	public static final String SEQUENCE_EDGES = "sequenceedges";
	
	// Copied from jadex.tools.bpmn.editor.properties.AbstractJadexPropertySection
	
	/** 
	 * String delimiter for list elements <p>
	 * <p><code>0x241F</code> (9247) SYMBOL FOR UNIT SEPARATOR</p>
	 */
	public static final String LIST_ELEMENT_DELIMITER = "\u241F"; // "<*>";
	
	
	/** 
	 * String delimiter for element attributes  <p>
	 * <p><code>0x240B</code> (9227) SYMBOL FOR VERTICAL TABULATION</p>
	 */
	public static final String LIST_ELEMENT_ATTRIBUTE_DELIMITER = "\u240B"; //"#|#";

	/** The manager. */
	protected static TypeInfoPathManager manager;
	
	/** The handler. */
	protected static IObjectReaderHandler handler;
	
	//-------- attributes --------
	
	/** The singleton reader instance. */
	protected static AReader	reader;
	
	public static IPostProcessor configpp = new IPostProcessor()
	{
		public Object postProcess(IContext context, Object object)
		{
			ConfigurationInfo app = (ConfigurationInfo)object;
			IModelInfo mapp = (IModelInfo)context.getRootObject();
			
			UnparsedExpression[] margs = app.getArguments();
			for(int i=0; i<margs.length; i++)
			{
				try
				{
					Argument arg = (Argument)mapp.getArgument(margs[i].getName());
					if(arg==null)
						throw new RuntimeException("Overridden argument not declared in component type: "+margs[i].getName());
					
//					Object val = overridenarg.getParsedValue().getValue(null);
//					arg.setDefaultValue(app.getName(), val);
				}
				catch(RuntimeException e)
				{
					Tuple	se	= new Tuple(((AReadContext)context).getStack());
					MultiCollection<Tuple, String>	report	= (MultiCollection<Tuple, String>)context.getUserContext();
					report.add(se, e.toString());
				}
			}
			
			return null;
		}
		
		public int getPass()
		{
			return 0;
		}
	};
	
	//-------- methods --------
	
	// Initialize reader instance.
	static
	{
		reader = XMLReaderFactory.getInstance().createReader(false, false, new XMLReporter()
		{
			public void report(String msg, String type, Object info, ILocation location) throws XMLStreamException
			{
//				System.out.println("XML error: "+msg+", "+type+", "+info+", "+location);
//				Thread.dumpStack();
				IContext	context	= (IContext)(info instanceof IContext ? info : AReader.READ_CONTEXT.get());
				Tuple	stack	= new Tuple(info instanceof StackElement[] ? (StackElement[])info : ((AReadContext)context).getStack());
				
				Map	user	= (Map)context.getUserContext();
				MultiCollection<Tuple, String>	report	= (MultiCollection<Tuple, String>)user.get(CONTEXT_ENTRIES);
				String	pos;
				if(stack.getEntities().length>0)
				{
					StackElement	se	= (StackElement)stack.get(stack.getEntities().length-1);
					pos	= " (line "+se.getLocation().getLineNumber()+", column "+se.getLocation().getColumnNumber()+")";
				}
				else
				{
					pos	= " (line 0, column 0)";			
				}
				report.add(stack, msg+pos);
			}
		});
		
		manager = new TypeInfoPathManager(getXMLMapping());
		handler = new BeanObjectReaderHandler(getXMLMapping());
	}
	
	/**
	 *  Get the reader instance.
	 * /
	public static Reader	getReader()
	{
		return reader;
	}*/
	
	/**
	 *  Read properties from xml.
	 *  @param info	The resource info.
	 *  @param classloader The classloader.
 	 */
	public static MBpmnModel read(ResourceInfo rinfo, ClassLoader classloader, IResourceIdentifier rid, IComponentIdentifier root) throws Exception
	{
		Map	user	= new HashMap();
		MultiCollection<Tuple, String>	report	= new MultiCollection<Tuple, String>(new IndexMap().getAsMap(), LinkedHashSet.class);
		user.put(CONTEXT_ENTRIES, report);
		user.put(SEQUENCE_EDGES, new HashMap<String, MSequenceEdge>());
		MBpmnModel ret = (MBpmnModel)reader.read(manager, handler, rinfo.getInputStream(), classloader, user);
		cleanupModel(ret);
		ret.setFilename(rinfo.getFilename());
		ret.setLastModified(rinfo.getLastModified());
//		ret.setClassloader(classloader);
		String name = new File(rinfo.getFilename()).getName();
		name = name.substring(0, name.length()-5);
		ret.setName(name);
		
		if(rid==null)
		{
			String src = SUtil.getCodeSource(rinfo.getFilename(), ((ModelInfo)ret.getModelInfo()).getPackage());
			URL url = SUtil.toURL(src);
			rid = new ResourceIdentifier(new LocalResourceIdentifier(root, url), null);
		}
		ret.setResourceIdentifier(rid);
		
//		ret.initModelInfo();
//		((ModelInfo) ret.getModelInfo()).getProperties().remove("debugger.breakpoints");
		rinfo.getInputStream().close();
		
		if(!((ModelInfo)ret.getModelInfo()).checkName())
		{
			report.add(new Tuple(new Object[]{new StackElement(new QName("BpmnDiagram"), ret)}), "Name '"+ret.getModelInfo().getName()+"' does not match file name '"+ret.getModelInfo().getFilename()+"'.");				
		}
		if(!((ModelInfo)ret.getModelInfo()).checkPackage())
		{
			report.add(new Tuple(new Object[]{new StackElement(new QName("BpmnDiagram"), ret)}), "Package '"+ret.getModelInfo().getPackage()+"' does not match file name '"+ret.getModelInfo().getFilename()+"'.");				
		}

		if(report.size()>0)
		{
//			System.out.println("Error loading model: "+rinfo.getFilename()+" "+report);
			((ModelInfo)ret.getModelInfo()).setReport(buildReport(ret.getModelInfo().getFullName(), ret.getModelInfo().getFilename(), report));
		}
		
		return ret;
	}
	
	/**
     *  Build the error report.
     */
    public static IErrorReport buildReport(String modelname, String filename, MultiCollection<Tuple, String> entries)
    {
        return new AbstractErrorReportBuilder(modelname, filename,
            new String[]{"Component", "Configuration"}, entries, null)
        {
            public boolean isInCategory(Object obj, String category)
            {
                return "Component".equals(category) && obj instanceof SubcomponentTypeInfo
                    || "Configuration".equals(category) && obj instanceof ConfigurationInfo;
            }

            public Object getPathElementObject(Object element)
            {
                return ((StackElement)element).getObject();
            }

            public String getObjectName(Object obj)
            {
                String    name    = null;
                String    type    = obj!=null ? SReflect.getInnerClassName(obj.getClass()) : null;
                if(obj instanceof SubcomponentTypeInfo)
                {
                    name    = ((SubcomponentTypeInfo)obj).getName();
                }
                else if(obj instanceof ConfigurationInfo)
                {
                    name    = ((ConfigurationInfo)obj).getName();
                    type    = "Configuration";
                }
                else if(obj instanceof UnparsedExpression)
                {
                    name    = ((UnparsedExpression)obj).getName();
                }
//                else if(obj instanceof MExpressionType)
//                {
//                    IParsedExpression    pexp    = ((MExpressionType)obj).getParsedValue();
//                    String    exp    = pexp!=null ? pexp.getExpressionText() : null;
//                    name    = exp!=null ? ""+exp : null;
//                }

//                if(type!=null && type.startsWith("M") && type.endsWith("Type"))
//                {
//                    type    = type.substring(1, type.length()-4);
//                }
                if(type!=null && type.endsWith("Info"))
                {
                    type    = type.substring(0, type.length()-4);
                }

                return type!=null ? name!=null ? type+" "+name : type : name!=null ? name : "";
            }
        }.buildErrorReport();
    }

	/**
	 * 
	 */
    protected static void cleanupModel(MBpmnModel model)
    {
    	// Clean model from duplicate activities
		Map<String, MActivity> acts = model.getAllActivities();
		if(acts!=null)
		{
			for(MActivity act: (Collection<MActivity>)acts.values())
			{
				if(act instanceof MSubProcess)
				{
					MSubProcess subp = (MSubProcess)act;
					List<MActivity> sacts = subp.getActivities();
					if(sacts!=null)
					{
						for(MActivity sact: sacts)
						{
							if(sact.getPool()!=null)
							{
								sact.getPool().removeActivity(sact);
								System.out.println("Removed act from pool: "+sact);
							}
							if(sact.getLane()!=null)
							{
								sact.getLane().removeActivity(sact);
								System.out.println("Removed act from lane: "+sact);
							}
						}
					}
				}
			}
		}
		
		List<MPool> pools = model.getPools();
		if(pools!=null)
		{
			for(MPool pool: pools)
			{
				List<MLane> lanes = pool.getLanes();
				if (lanes != null)
				{
					for(MLane lane: lanes)
					{
						List<MActivity> lacts = lane.getActivities();
						if(lacts!=null)
						{
							for(MActivity lact: lacts)
							{
								lact.getPool().removeActivity(lact);
								System.out.println("Removed act from pool: "+lact);
							}
						}
					}				
				}
			}
		}
		
		Map<String, MEdge> edges = model.getAllEdges();
		for (Map.Entry<String, MEdge> entry : edges.entrySet())
		{
			if (entry.getValue().getSource() == null ||
				entry.getValue().getTarget() == null ||
				entry.getValue().getId() == null)
			{
				if (entry.getValue() instanceof MSequenceEdge)
				{
					if (entry.getValue().getSource() != null)
						entry.getValue().getSource().removeOutgoingSequenceEdge((MSequenceEdge) entry.getValue());
					if (entry.getValue().getTarget() != null)
						entry.getValue().getTarget().removeIncomingSequenceEdge((MSequenceEdge) entry.getValue());
				}
				else if (entry.getValue() instanceof MMessagingEdge)
				{
					if (entry.getValue().getSource() != null)
						entry.getValue().getSource().removeOutgoingMessagingEdge((MMessagingEdge) entry.getValue());
					
					if (entry.getValue().getTarget() != null)
						entry.getValue().getTarget().removeIncomingMessagingEdge((MMessagingEdge) entry.getValue());
					
					// Deep scan for really messed-up models.
					if (acts != null)
					{
						for (Map.Entry<String, MActivity> aentry : acts.entrySet())
						{
							aentry.getValue().removeIncomingMessagingEdge((MMessagingEdge) entry.getValue());
							aentry.getValue().removeOutgoingMessagingEdge((MMessagingEdge) entry.getValue());;
						}
					}
//					
					model.removeMessagingEdge((MMessagingEdge) entry.getValue());
				}
				System.out.println("Removing stray edge with ID " + String.valueOf(entry.getValue().getId()) +
						   		   ", source: " + String.valueOf(entry.getValue().getSource()) +
						   		   ", target: " + String.valueOf(entry.getValue().getTarget()));
			}
		}
		model.clearCaches();
    }
    
	/**
	 *  Get the XML mapping.
	 */
	public static Set getXMLMapping()
	{
		Set types = new HashSet();
		
		String uri = "http://stp.eclipse.org/bpmn";
		String xmiuri = "http://www.omg.org/XMI";
		
		TypeInfo	diatype	= new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "BpmnDiagram")}), new ObjectInfo(MBpmnModel.class, new BpmnModelPostProcessor()), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"), AccessInfo.IGNORE_READWRITE)),
			new AttributeInfo(new AccessInfo(new QName("http://www.omg.org/XMI", "version"), null, AccessInfo.IGNORE_READWRITE)),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("pools", "pool")),
			new SubobjectInfo(new AccessInfo("artifacts", "artifact")),
			new SubobjectInfo(new AccessInfo("messages", "messagingEdge")),
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
		}));
		diatype.setReaderHandler(new BeanObjectReaderHandler());
		types.add(diatype);
		
		types.add(new TypeInfo(new XMLInfo("eAnnotations"), new ObjectInfo(MAnnotation.class), 
			new MappingInfo(null, new BeanAccessInfo[]{
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("details", "detail")),
			})));
		
		types.add(new TypeInfo(new XMLInfo("details"), new ObjectInfo(MAnnotationDetail.class)));
		
		types.add(new TypeInfo(new XMLInfo("pools"), new ObjectInfo(MPool.class, new PoolPostProcessor()),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", "description")),
			new AttributeInfo(new AccessInfo("associations", "associationsDescription")),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE))
			}, 
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("vertices", "activity")),
//			new SubobjectInfo(new AccessInfo("sequenceEdges", "sequenceEdge")),
			new SubobjectInfo(new AccessInfo("lanes", "lane")),
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("artifacts"), new ObjectInfo(MArtifact.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", "description")),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("associations", "association")),
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("associations"), new ObjectInfo(MAssociation.class, new AssociationPostProcessor()), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new XMLInfo("eAnnotations"), new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("lanes"), new ObjectInfo(MLane.class, new LanePostProcessor()),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", "description")), 
			new AttributeInfo(new AccessInfo("activities", "activitiesDescription")),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new XMLInfo("eAnnotations"), new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("eventHandlers"), new ObjectInfo(MActivity.class, new EventHandlerPostProcessor()),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", "description")),
			new AttributeInfo(new AccessInfo("outgoingEdges", "outgoingSequenceEdgesDescription")),
			new AttributeInfo(new AccessInfo("incomingEdges", "incomingSequenceEdgesDescription")),
			new AttributeInfo(new AccessInfo("lanes", "laneDescription")),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE)),
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new XMLInfo("eAnnotations"), new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("vertices", 
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("Activity");
				}
			}), 
//			new ObjectInfo(MActivity.class, new ActivityPostProcessor()),
			new ObjectInfo(new IBeanObjectCreator()
			{
				public Object createObject(IContext context, Map<String, String> rawattributes)
						throws Exception
				{
					MActivity ret = null;
					Object at = rawattributes.get("activityType");
					if (at == null || MTask.TASK.equals(at))
					{
						ret = new MTask();
					}
					else
					{
						ret = new MActivity();
					}
					return ret;
				}
			}, new ActivityPostProcessor()),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", "description")),
			new AttributeInfo(new AccessInfo("outgoingEdges", "outgoingSequenceEdgesDescription")),
			new AttributeInfo(new AccessInfo("incomingEdges", "incomingSequenceEdgesDescription")),
			new AttributeInfo(new AccessInfo("lanes", "laneDescription")),
			new AttributeInfo(new AccessInfo("associations", "associationsDescription")),
//			new AttributeInfo(new AccessInfo("activityType", "activityType", null, MBpmnModel.TASK)),
			new AttributeInfo(new AccessInfo("activityType", "activityType", null, MTask.TASK)),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("incomingMessages", "incomingMessageDescription")),
			new SubobjectInfo(new AccessInfo("outgoingMessages", "outgoingMessageDescription")),
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("vertices", 
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("SubProcess");
				}
			}),
			new ObjectInfo(MSubProcess.class, new ActivityPostProcessor()),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", "description")),
			new AttributeInfo(new AccessInfo("outgoingEdges", "outgoingSequenceEdgesDescription")),
			new AttributeInfo(new AccessInfo("incomingEdges", "incomingSequenceEdgesDescription")),
			new AttributeInfo(new AccessInfo("lanes", "laneDescription")),
			new AttributeInfo(new AccessInfo("associations", "associationsDescription")),
			new AttributeInfo(new AccessInfo("activityType", "activityType", null, MBpmnModel.SUBPROCESS)),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE))	
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("incomingMessages", "incomingMessageDescription")),
			new SubobjectInfo(new AccessInfo("outgoingMessages", "outgoingMessageDescription")),
			new SubobjectInfo(new AccessInfo("eventHandlers", "eventHandler")),
			new SubobjectInfo(new AccessInfo("vertices", "Activity")),
//			new SubobjectInfo(new AccessInfo("sequenceEdges", "sequenceEdge")),
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("sequenceEdges"), new ObjectInfo(MSequenceEdge.class, new SequenceEdgePostProcessor()),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", "description")), 
			new AttributeInfo(new AccessInfo("associations", "associationsDescription")),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE)),
			new AttributeInfo(new AccessInfo("conditionType", null, AccessInfo.IGNORE_READWRITE)),
			new AttributeInfo(new AccessInfo("isDefault", "default"))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new XMLInfo("eAnnotations"), new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("messagingEdges"), new ObjectInfo(MMessagingEdge.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", "description")), 
			new AttributeInfo(new AccessInfo("associations", "associationsDescription")),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE)),
			}, 
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("incomingMessages"), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo(new QName(xmiuri, "type"), null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("href", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE)),
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));

		types.add(new TypeInfo(new XMLInfo("outgoingMessages"), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo(new QName(xmiuri, "type"), null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("href", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE)),
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("messages"), new ObjectInfo(MMessagingEdge.class), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("source", "sourceDescription")),
			new AttributeInfo(new AccessInfo("target", "targetDescription")),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE)),
			}, 
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));
		
		return types;
	}
	
	/**
	 *  Activity post processor.
	 */
	static class ActivityPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public Object postProcess(IContext context, Object object)
		{
			MBpmnModel dia = (MBpmnModel)context.getRootObject();
			MActivity act = (MActivity)object;
			
//			System.out.println("Act: "+act.getName()+" "+act.getDescription());

			// Make edge connections.
//			Map edges = dia.getAllSequenceEdges();
			Map<String, MSequenceEdge> edges = (Map<String, MSequenceEdge>) ((Map)context.getUserContext()).get(SEQUENCE_EDGES);
			String indesc = act.getIncomingSequenceEdgesDescription();
			if(indesc!=null)
			{
				StringTokenizer stok = new StringTokenizer(indesc);
				while(stok.hasMoreElements())
				{
					String edgeid = stok.nextToken(); 
					MSequenceEdge edge = (MSequenceEdge)edges.get(edgeid);
					act.addIncomingSequenceEdge(edge);
					edge.setTarget(act);
				}
			}
			
			String outdesc = act.getOutgoingSequenceEdgesDescription();
			if(outdesc!=null)
			{
				StringTokenizer stok = new StringTokenizer(outdesc);
				while(stok.hasMoreElements())
				{
					String edgeid = stok.nextToken(); 
					MSequenceEdge edge = (MSequenceEdge)edges.get(edgeid);
					act.addOutgoingSequenceEdge(edge);
					edge.setSource(act);
				}
			}
			
			// Make message connections.
			// todo: message - pool connections
			Map allmessages = dia.getAllMessagingEdges();
			List inmsgs = act.getIncomingMessagesDescriptions();
			if(inmsgs!=null)
			{
				for(int i=0; i<inmsgs.size(); i++)
				{
					Map msgdesc = (Map)inmsgs.get(i);
					String id = ((String)msgdesc.get("href")).substring(1);
					MMessagingEdge msg = (MMessagingEdge)allmessages.get(id);
					if(msg==null)
						throw new RuntimeException("Could not find message: "+id);
					
					act.addIncomingMessagingEdge(msg);
					msg.setTarget(act);
				}
			}
			
			List outmsgs = act.getOutgoingMessagesDescriptions();
			if(outmsgs!=null)
			{
				for(int i=0; i<outmsgs.size(); i++)
				{
					Map msgdesc = (Map)outmsgs.get(i);
					String id = ((String)msgdesc.get("href")).substring(1);
					MMessagingEdge msg = (MMessagingEdge)allmessages.get(id);
					if(msg==null)
						throw new RuntimeException("Could not find message: "+id);
					
					act.addOutgoingMessagingEdge(msg);
					msg.setTarget(act);
					
					// Set mode to throwing. Hack!!! BPMN editor does not set isThrowing property when using message edges.
					//act.setPropertyValue("isThrowing", Boolean.TRUE);
					
					act.setThrowing(true);
				}
			}
			
			if(act.getDescription()!=null)
			{
				// first line: name
				// lines with = in it: properties or class
				// lines starting with in/out/inout: parameters
				
				StringTokenizer	stok = new StringTokenizer(act.getDescription(), "\r\n");
				JavaCCExpressionParser parser = new JavaCCExpressionParser();
				
				while(stok.hasMoreTokens())
				{
					String prop = stok.nextToken().trim();
//					System.out.println("prop: "+prop);
					int	idx	= prop.indexOf("=");
					if(prop.startsWith("in") || prop.startsWith("out"))
					{
						// parameter
						StringTokenizer stok2 = new StringTokenizer(prop, " \t=");
						String paramdir = stok2.nextToken();
						String paramclazzname = stok2.nextToken();
						
						ClassInfo paramclazz = new ClassInfo(paramclazzname);
//						Class paramclazz = SReflect.findClass0(paramclazzname, dia.getModelInfo().getAllImports(), context.getClassLoader());
//						if(paramclazz==null)
//							throw new RuntimeException("Parameter class not found in imports: "+dia+", "+act+", "+paramclazzname);//+", "+SUtil.arrayToString(dia.getAllImports()));
						
						String paramname = stok2.nextToken();
						
//						IParsedExpression paramexp = null;
						UnparsedExpression paramexp = null;
						if(stok2.hasMoreTokens())
						{
							String proptext = prop.substring(idx+1).trim();
//							paramexp = parser.parseExpression(proptext, dia.getModelInfo().getAllImports(), null, context.getClassLoader());
							paramexp = new UnparsedExpression(paramname, paramclazzname, proptext, null);
							SJavaParser.parseExpression(paramexp, dia.getModelInfo().getAllImports(), context.getClassLoader());
						}
						
						MParameter param = new MParameter(paramdir, paramclazz, paramname, paramexp);
						act.addParameter(param);
					}
					else if(idx!=-1)
					{
						// property or class
						String propname = prop.substring(0, idx).trim();
						String proptext = prop.substring(idx+1).trim();
						if(propname.equals("class"))
						{
							// Compatibility hack: strip ".class" from value, if present.
							if(proptext.endsWith(".class"))
							{
								proptext	= proptext.substring(0, proptext.length()-6);
							}
//							try
//							{
//								Class<?>	clazz	= SReflect.findClass(proptext, dia.getModelInfo().getAllImports(), context.getClassLoader());
//								act.setClazz(clazz);
//							}
//							catch(ClassNotFoundException cnfe)
//							{
//								throw new RuntimeException(cnfe);
//							}
							act.setClazz(new ClassInfo(proptext));
						}
						else if(act	instanceof MSubProcess && propname.equals("parallel"))
						{
//							IParsedExpression propval = parser.parseExpression(proptext, dia.getModelInfo().getAllImports(), null, context.getClassLoader());
//							((MSubProcess)act).setSubprocessType(((Boolean)propval.getValue(null)).booleanValue() ? MSubProcess.SUBPROCESSTYPE_PARALLEL : MSubProcess.SUBPROCESSTYPE_NONE);
							((MSubProcess)act).setSubprocessType(Boolean.TRUE.equals(Boolean.parseBoolean(proptext))? MSubProcess.SUBPROCESSTYPE_PARALLEL : MSubProcess.SUBPROCESSTYPE_NONE);
						}
						else
						{
//							IParsedExpression propval = parser.parseExpression(proptext, dia.getModelInfo().getAllImports(), null, context.getClassLoader());
							UnparsedExpression propval = new UnparsedExpression(propname, Object.class, proptext, null);
							act.setPropertyValue(propname, propval);
						}
					}
					else
					{
						// line without "=" is name
						act.setName(prop);
					}
				}
			}
			
			// Read annotations from Jadex bpmn tool.
			
			List annos = act.getAnnotations();
			if(annos!=null)
			{
				JavaCCExpressionParser parser = new JavaCCExpressionParser();
				for(int i=0; i<annos.size(); i++)
				{
					MAnnotation anno = (MAnnotation)annos.get(i);
					
					// new jadex parameter handling - we accept ALL "_parameters_table"
					if(anno.getSource().toLowerCase().endsWith("_parameters_table"))
					{
						MultiColumnTableEx table = parseBpmnMultiColumTable(anno.getDetails(), annos);
						
						for(int row = 0; row < table.size(); row++)
						{
							// normal activity parameter has 4 values
							if(table.get(row).size() == 4)
							{
								String dir = (String) table.get(row).getColumnValueAt(0); 		// direction
								String name = (String) table.get(row).getColumnValueAt(1);		// name
								String clazzname = (String) table.get(row).getColumnValueAt(2);	// class
								String val = !"".equals((String) table.get(row).getColumnValueAt(3)) ? (String) table.get(row).getColumnValueAt(3) : null;		// value
								
								UnparsedExpression exp = null;
								if(val!=null && val.length()>0)
								{
//									exp = parser.parseExpression(val, dia.getModelInfo().getAllImports(), null, context.getClassLoader());
									exp = new UnparsedExpression(name, clazzname, val, null);
									SJavaParser.parseExpression(exp, dia.getModelInfo().getAllImports(), context.getClassLoader());
								}
								MParameter param = new MParameter(dir, new ClassInfo(clazzname), name, exp);
								act.addParameter(param);
							}
							
							// Parameters of event handlers have 2 elements = are treated as properties?! 
							// TODO: rename parameters to properties in editor and write converter?
							else if (table.get(row).size() == 2)
							{
								String name =  (String) table.get(row).getColumnValueAt(0);
								String val = !"".equals((String) table.get(row).getColumnValueAt(1)) ? (String) table.get(row).getColumnValueAt(1) : null;
								
								// context variable
//								IParsedExpression exp = null;
//								if(val!=null && val.length()>0)
//								{
//									exp = parser.parseExpression(val, dia.getModelInfo().getAllImports(), null, context.getClassLoader());
//								}
								UnparsedExpression exp = new UnparsedExpression(name, Object.class, val, null);
								act.setPropertyValue(name, exp);
							}
							
							else
							{
								throw new RuntimeException("Parameter specification error: "+table.get(row).size()+" "+Arrays.toString(table.get(row).getColumnValues()));
							}
								
						}
						
						// next annotation
						continue;
					}
					// new jadex properties handling
					// we accept ALL "_properties_table"
					else if(anno.getSource().toLowerCase().endsWith("_properties_table"))
					{
						MultiColumnTableEx table = parseBpmnMultiColumTable(anno.getDetails(), annos);
						
						for(int row = 0; row < table.size(); row++)
						{
							// normal property has 2 values
//							assert table.get(row).size() == 2;
			
							String name =  (String) table.get(row).getColumnValueAt(0);
							String val = !"".equals((String) table.get(row).getColumnValueAt(1)) ? (String) table.get(row).getColumnValueAt(1) : null;
							
							// context variable
//							IParsedExpression exp = null;
//							if(val!=null && val.length()>0)
//							{
//								exp = parser.parseExpression(val, dia.getModelInfo().getAllImports(), null, context.getClassLoader());
//							}
							UnparsedExpression exp = new UnparsedExpression(name, Object.class, val, null);
							act.setPropertyValue(name, exp);
//							System.out.println("Parameter/property: "+name+" "+exp);
						}
						
						// next annotation
						continue;
					}
					
					if (!anno.getSource().toLowerCase().endsWith("table"))
					{
						List details = anno.getDetails();
						if (details != null)
						{
							for (int j = 0; j < details.size(); j++)
							{
								MAnnotationDetail detail = (MAnnotationDetail) details.get(j);

								String key = detail.getKey();
								String value = detail.getValue();

								// TODO: remove old parameter handling
								if ("parameters".equals(key))
								{
									StringTokenizer stok = new StringTokenizer(value, LIST_ELEMENT_DELIMITER);

									while (stok.hasMoreTokens())
									{
										String paramtext = stok.nextToken();
										StringTokenizer stok2 = new StringTokenizer(paramtext, LIST_ELEMENT_ATTRIBUTE_DELIMITER);

										// Parameters of normal activities have 4 elements
										int tokcnt = stok2.countTokens();
										if (tokcnt == 3 || tokcnt == 4)
										{
											String dir = stok2.nextToken();
											String name = stok2.nextToken();
											String clazzname = stok2.nextToken();
											String val = stok2.hasMoreTokens() ? stok2.nextToken() : null;
											
											
//											try
//											{
//												Class clazz = SReflect.findClass(clazzname, dia.getModelInfo().getAllImports(),
//													context.getClassLoader());
												UnparsedExpression exp = null;
												if(val != null && val.length() > 0)
												{
//													exp = parser.parseExpression(val, dia.getModelInfo().getAllImports(),
//														null, context.getClassLoader());
													exp = new UnparsedExpression(name, clazzname, val, null);
												}
												MParameter param = new MParameter(dir, new ClassInfo(clazzname), name, exp);
												act.addParameter(param);
												// System.out.println("Parameter: "+param);
//											}
//											catch (ClassNotFoundException cnfe)
//											{
//												throw new RuntimeException(cnfe);
//											}
										}

										// Parameters of event handlers have 2 elements = are treated as properties?!
										else if (tokcnt == 2)
										{
											String name = stok2.nextToken();
											String val = stok2.nextToken();

											// context variable
//											IParsedExpression exp = null;
//											if (val != null && val.length() > 0)
//											{
//												exp = parser.parseExpression(val, dia.getModelInfo().getAllImports(),
//													null, context.getClassLoader());
//											}
											UnparsedExpression exp = new UnparsedExpression(name, Object.class, val, null);
											act.setPropertyValue(name, exp);
											// System.out.println("Parameter/property: "+name+" "+exp);
										}
										else
										{
											throw new RuntimeException("Parameter specification error: "
												+ stok2.countTokens()+ " " + paramtext);
										}
									}
								}
								else
								// property
								{
									// Skip empty string (cannot be parsed to anything), for parsing empty string "" need to be used
									if (!"".equals(value))
									{
										if (key.equals("class"))
										{
											// Compatibility hack: strip ".class" from value, if present.
											if (value.endsWith(".class"))
											{
												value = value.substring(0,
														value.length() - 6);
											}
//											try
											{
//												Class clazz = SReflect.findClass(value, dia.getModelInfo().getAllImports(),
//													context.getClassLoader());
												act.setClazz(new ClassInfo(value));
											}
//											catch (ClassNotFoundException cnfe)
											{
//												throw new RuntimeException(cnfe);
											}
										}
										else if (act instanceof MSubProcess
												&& "parallel".equals(key.toLowerCase()))
										{
//											IParsedExpression propval = parser.parseExpression(value,
//												dia.getModelInfo().getAllImports(), null, context.getClassLoader());
//											((MSubProcess)act).setSubprocessType(((Boolean) propval.getValue(null)).booleanValue() 
//												? MSubProcess.SUBPROCESSTYPE_PARALLEL
//												: MSubProcess.SUBPROCESSTYPE_NONE);
											((MSubProcess)act).setSubprocessType(Boolean.TRUE.equals(Boolean.parseBoolean(value))? MSubProcess.SUBPROCESSTYPE_PARALLEL : MSubProcess.SUBPROCESSTYPE_NONE);
										}
										else
										{
//											IParsedExpression propval = parser.parseExpression(value,
//												dia.getModelInfo().getAllImports(), null, context.getClassLoader());
											UnparsedExpression propval = new UnparsedExpression(key, Object.class, value, null);
											act.setPropertyValue(key, propval);
										}
									}
								}
							}
						}
					}
				}
			}
			
			try
			{
				if (act.hasProperty("isThrowing") && Boolean.TRUE.equals(act.getParsedPropertyValue("isThrowing")))
				{
					act.setThrowing(true);
				}
			}
			catch (Exception e)
			{
			}
			
			return null;
		}
		
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 2;
		}
		
		
	}
	
	/**
	 *  Event handler post processor.
	 */
	static class EventHandlerPostProcessor extends ActivityPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public Object postProcess(IContext context, Object object)
		{
			Object ret = super.postProcess(context, object);
			if(ret==null)
			{
				((MActivity)object).setEventHandler(true);
			}
			else
			{
				((MActivity)ret).setEventHandler(true);
			}
			return ret;
		}
	}
	
	/**
	 *  Pool post processor.
	 */
	static class PoolPostProcessor	extends NamePropertyPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public Object postProcess(IContext context, Object object)
		{
			super.postProcess(context, object);
			
			// Set pool of activities.
			MPool pool = (MPool)object;
			setSubActivities(pool, pool);
			
			return null;
		}
		
		/**
		 *  Associate also subactivities with outer pool.
		 */
		protected void setSubActivities(MAssociationTarget parent, MPool pool)
		{
			List activities = parent instanceof MSubProcess? getAllActivities((MSubProcess)parent): ((MPool)parent).getActivities();
			if(activities != null)
			{
				for(Iterator it = activities.iterator(); it.hasNext(); )
				{
					MActivity activity = (MActivity)it.next();
					activity.setPool(pool);
					if(activity instanceof MSubProcess)
						setSubActivities((MSubProcess)activity, pool);
				}
			}
		}
		
		/**
		 *  Get all activities of a subprocess.
		 */
		public List getAllActivities(MSubProcess proc)
		{
			List ret = new ArrayList();
			if(proc.getActivities()!=null)
				ret.addAll(proc.getActivities());
			if(proc.getEventHandlers()!=null)
				ret.addAll(proc.getEventHandlers());
			return ret;
		}
	}
	
	
	/**
	 *  Lane post processor.
	 */
	static class LanePostProcessor	extends NamePropertyPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public Object postProcess(IContext context, Object object)
		{
			super.postProcess(context, object);
			
			// Resolve activities
			MLane	lane	= (MLane)object;
			String	actdesc	= lane.getActivitiesDescription();
			if(actdesc!=null)
			{
				MBpmnModel dia = (MBpmnModel)context.getRootObject();
				Map	activities	= dia.getAllActivities();
				
				StringTokenizer stok = new StringTokenizer(actdesc);
				while(stok.hasMoreElements())
				{
					String actid = stok.nextToken(); 
					MActivity activity = (MActivity)activities.get(actid);
					lane.addActivity(activity);
					activity.setLane(lane);
				}
			}
			
			return null;
		}
	}
	
	/**
	 *  Association post processor.
	 */
	static class AssociationPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Set source and target of association.
		 */
		public Object postProcess(IContext context, Object object)
		{
			MBpmnModel dia = (MBpmnModel)context.getRootObject();
			MAssociation asso = (MAssociation)object;
			
			MArtifact source = (MArtifact)dia.getAllAssociationSources().get(asso.getId());
			MAssociationTarget target = (MAssociationTarget)dia.getAllAssociationTargets().get(asso.getId());
			
			if(source==null)
				throw new RuntimeException("Could not find association source: "+source);
			if(target==null)
				throw new RuntimeException("Could not find association target: "+target);
			
			asso.setSource(source);
			asso.setTarget(target);
			
			source.addAssociation(asso);
			target.addAssociation(asso);
		
			return null;
		}
	
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 2;
		}
	}
	
	/**
	 *  Sequence edge post processor.
	 */
	static class SequenceEdgePostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public Object postProcess(IContext context, Object object)
		{
			MBpmnModel dia = (MBpmnModel)context.getRootObject();
			MSequenceEdge edge = (MSequenceEdge)object;
			((Map<String, MSequenceEdge>) ((Map) context.getUserContext()).get(SEQUENCE_EDGES)).put(edge.getId(), edge);
//			JavaCCExpressionParser parser = new JavaCCExpressionParser();

			// Read annotations from Jadex bpmn tool.
			
			List annos = edge.getAnnotations();
			if(annos!=null)
			{
				for(int i=0; i<annos.size(); i++)
				{
					MAnnotation anno = (MAnnotation)annos.get(i);
					
					// new mappings handling - we accept ALL "_mappings_table since a mapping is a mapping :-)
					// todo: enhance mappings with index?
					if(anno.getSource().toLowerCase().endsWith("_mappings_table"))
					{
						MultiColumnTableEx table = parseBpmnMultiColumTable(anno.getDetails(), annos);
						
						for(int row = 0; row < table.size(); row++)
						{
//							try
//							{
//								// normal mapping has 2 values
//								assert table.get(row).size() == 2;
//							}
//							catch (AssertionError e)
//							{
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
							
							String propname = (String) table.get(row).getColumnValueAt(0);
							String proptext = (String) table.get(row).getColumnValueAt(1);

//							IParsedExpression exp = parser.parseExpression(proptext, dia.getModelInfo().getAllImports(), null, context.getClassLoader());
//							IParsedExpression iexp	= null;
							UnparsedExpression exp = new UnparsedExpression(propname, Object.class, proptext, null);
							UnparsedExpression iexp	= null;

							if(propname.endsWith("]") && propname.indexOf("[")!=-1)
							{
								String	itext	= propname.substring(propname.indexOf("[")+1, propname.length()-1);
								propname	= propname.substring(0, propname.indexOf("["));
//								iexp	= parser.parseExpression(itext, dia.getModelInfo().getAllImports(), null, context.getClassLoader());
								iexp	= new UnparsedExpression(propname, Object.class, itext, null);
							}

							edge.addParameterMapping(propname, exp, iexp);

						}
						
						// next annotation
						continue;
					}
					
					if (!anno.getSource().toLowerCase().endsWith("table"))
					{
						List details = anno.getDetails();
						if (details != null)
						{
							for(int j = 0; j < details.size(); j++)
							{
								MAnnotationDetail detail = (MAnnotationDetail)details.get(j);

								String key = detail.getKey();
								String value = detail.getValue();

								// TODO: remove old mappings handling
								if ("mappings".equals(key))
								{
									StringTokenizer stok = new StringTokenizer(value, LIST_ELEMENT_DELIMITER);
									while (stok.hasMoreTokens())
									{
										String maptext = stok.nextToken();

										StringTokenizer stok2 = new StringTokenizer(maptext, LIST_ELEMENT_ATTRIBUTE_DELIMITER);
										if (stok2.countTokens() == 2)
										{
											String propname = stok2.nextToken();
											String proptext = stok2.nextToken();

//											IParsedExpression exp = parser.parseExpression(proptext, dia.getModelInfo().getAllImports(),
//												null, context.getClassLoader());
//											IParsedExpression iexp = null;
											UnparsedExpression exp = new UnparsedExpression(propname, Object.class, proptext, null);
											UnparsedExpression iexp	= null;

											if (propname.endsWith("]") && propname.indexOf("[") != -1)
											{
												String itext = propname.substring(propname.indexOf("[") + 1,propname.length() - 1);
												propname = propname.substring(0,propname.indexOf("["));
												iexp	= new UnparsedExpression(propname, Object.class, itext, null);
//												iexp = parser.parseExpression(itext, dia.getModelInfo().getAllImports(), null, context.getClassLoader());
											}

											edge.addParameterMapping(propname, exp, iexp);
										}
										// System.out.println("Mapping: "+propname+" "+exp);
									}
								}
								else
								// todo: remove old mappings handling until here

								if ("condition".equals(key) && value != null && value.length() > 0)
								{
//									IParsedExpression cond = parser.parseExpression(value, dia.getModelInfo().getAllImports(), null, context.getClassLoader());
									UnparsedExpression cond = new UnparsedExpression(null, (Class<?>) null, value, null);
									edge.setCondition(cond);

									// System.out.println("Condition: "+key+" "+value);
								}
							}
						}
					}
				}
			}
			
			// Only interpret description when no annotations were found.
			
			else if(edge.getDescription()!=null)
			{
				// first line: name
				// second line: condition
				// lines with = in it: parameters
				
				try
				{
					StringTokenizer	stok = new StringTokenizer(edge.getDescription(), "\r\n");
					String lineone = null;
					String linetwo = null;
					while(stok.hasMoreTokens())
					{
						String prop = stok.nextToken();
						int	idx	= prop.indexOf("=");
						boolean	comp	= idx>0 && (prop.charAt(idx-1)=='!' || prop.charAt(idx-1)=='<' || prop.charAt(idx-1)=='>');
						boolean	eq	= idx!=-1 && idx<prop.length()-1 && prop.charAt(idx+1)=='=';
						boolean	assignment	= idx!=-1 && !comp && !eq;
						if(assignment)
						{
							String	propname = prop.substring(0, idx).trim();
							String	proptext = prop.substring(idx+1).trim();
//							IParsedExpression exp = parser.parseExpression(proptext, dia.getModelInfo().getAllImports(), null, context.getClassLoader());
//							IParsedExpression iexp	= null;
							UnparsedExpression exp = new UnparsedExpression(propname, Object.class, proptext, null);
							UnparsedExpression iexp	= null;
	
							if(propname.endsWith("]") && propname.indexOf("[")!=-1)
							{
								String	itext	= propname.substring(propname.indexOf("[")+1, propname.length()-1);
								propname	= propname.substring(0, propname.indexOf("["));
//								iexp	= parser.parseExpression(itext, dia.getModelInfo().getAllImports(), null, context.getClassLoader());
								iexp	= new UnparsedExpression(propname, Object.class, itext, null);
							}
	
							edge.addParameterMapping(propname, exp, iexp);
						}
						else
						{
							// last line without "=" is assumed to be condition
							if(lineone==null)
								lineone = prop;
							else
								linetwo = prop;
						}
					}
					
					if(lineone!=null && linetwo!=null)
					{
						edge.setName(lineone);
//						IParsedExpression cond = parser.parseExpression(linetwo, 
//							dia.getModelInfo().getAllImports(), null, context.getClassLoader());
						UnparsedExpression cond = new UnparsedExpression(null, (Class<?>) null, linetwo, null);
						edge.setCondition(cond);
					}
					else if(lineone!=null)
					{
//						IParsedExpression cond = parser.parseExpression(lineone, 
//							dia.getModelInfo().getAllImports(), null, context.getClassLoader());
						UnparsedExpression cond = new UnparsedExpression(null, (Class<?>) null, lineone, null);
						edge.setCondition(cond);
					}
				}
				catch(Exception e)
				{
					// nop, maybe just comment
				}
			}
			
			return IPostProcessor.DISCARD_OBJECT;
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
	 *  Named element post processor.
	 *  Can parse the name and an aribitrary number of properties.
	 */
	static class NamePropertyPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public Object postProcess(IContext context, Object object)
		{
			MBpmnModel dia = (MBpmnModel)context.getRootObject();
			MNamedIdElement namedelem = (MNamedIdElement)object;
			JavaCCExpressionParser parser = new JavaCCExpressionParser();

			if(namedelem.getDescription()!=null)
			{
				// first line: name
				// lines with = in it: properties
				
				StringTokenizer	stok = new StringTokenizer(namedelem.getDescription(), "\r\n");
				while(stok.hasMoreTokens())
				{
					String prop = stok.nextToken();
					int	idx	= prop.indexOf("=");
					if(idx!=-1)
					{
						if (namedelem instanceof MActivity)
						{
							String propname = prop.substring(0, idx).trim();
							String proptext = prop.substring(idx+1).trim();
							try
							{
								IParsedExpression propval = parser.parseExpression(proptext, dia.getModelInfo().getAllImports(), 
									null, context.getClassLoader());
								((MActivity) namedelem).setPropertyValue(propname, propval);
							}
							catch(RuntimeException e)
							{
								throw new RuntimeException("Error parsing property: "+dia+", "+propname+", "+proptext, e);
							}
						}	
					}
					else
					{
						// line without "=" is name
						namedelem.setName(prop);
					}
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
			return 2;
		}
	}
	
	/**
	 *  Bpmn Model post processor.
	 */
	static class BpmnModelPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public Object postProcess(IContext context, Object object)
		{
			MBpmnModel model = (MBpmnModel)context.getRootObject();
			ModelInfo mi = (ModelInfo)model.getModelInfo();
			JavaCCExpressionParser parser = new JavaCCExpressionParser();

			Map configurations = new HashMap();
			
			// Handle the annotations of the model (Jadex BPMN Editor).
			List annos = model.getAnnotations();
			if(annos!=null)
			{
				// new jadex import handling
				
				// Parse imports/package first (editor saves annotations in arbitrary order, grrr.)
				for(int i=0; i<annos.size(); i++)
				{
					MAnnotation anno = (MAnnotation)annos.get(i);
					
					if(anno.getSource().toLowerCase().endsWith("_imports_table")) 
					{
						MultiColumnTableEx table = parseBpmnMultiColumTable(anno.getDetails(), annos);

						for(int row = 0; row < table.size(); row++) 
						{
							String imp = (String) table.get(row).getColumnValueAt(0);
							if(imp.length() > 0)
								model.addImport(imp);
						}

						// we have found the imports
						break;
					}
				}
				
				// Parse imports/package first (editor saves annotations in arbitrary order, grrr.)
				for(int i=0; i<annos.size(); i++)
				{
					MAnnotation anno = (MAnnotation)annos.get(i);

					if(!anno.getSource().toLowerCase().endsWith("table"))
					{
						List details = anno.getDetails();
						if(details != null)
						{
							for(int j = 0; j < details.size(); j++)
							{
								MAnnotationDetail detail = (MAnnotationDetail)details.get(j);
								String key = detail.getKey().toLowerCase();
								String value = detail.getValue();
								
								// TODO: remove old imports handling
								if("imports".equals(key))
								{
									StringTokenizer stok = new StringTokenizer(value, LIST_ELEMENT_DELIMITER);
									while(stok.hasMoreElements())
									{
										String imp = stok.nextToken().trim();
										if(imp.length() > 0)
											model.addImport(imp);
									}
									// System.out.println("Imports: "+SUtil.arrayToString(imps));
								}
								else 
								// todo: remove old imports until here
									
								if("package".equals(key))
								{
									model.setPackage(value);
									// System.out.println("Package: "+value);
								}
							}
						}
					}
				}
				
				for(int i=0; i<annos.size(); i++)
				{
					MAnnotation anno = (MAnnotation)annos.get(i);
					if(anno.getSource().toLowerCase().endsWith("_configurations_table"))
					{
						MultiColumnTableEx table = parseBpmnMultiColumTable(anno.getDetails(), annos);
						for(int row = 0; row < table.size(); row++)
						{
							String id = table.getCellValue(row, 0);
							String name = table.getCellValue(row, 1);
							String poollane = table.getCellValue(row, 2);
							ConfigurationInfo ci = new ConfigurationInfo(name);
							mi.addConfiguration(ci);
							configurations.put(id, ci);
//							if(poollane!=null && poollane.length()>0)
//								model.addPoolLane(name, poollane);
						}
						break;
					}
				}

				Map bindings = new HashMap();
				for(int i=0; i<annos.size(); i++)
				{
					MAnnotation anno = (MAnnotation)annos.get(i);
					
					if(anno.getSource().toLowerCase().endsWith("_bindings_table")) 
					{
						MultiColumnTableEx table = parseBpmnMultiColumTable(anno.getDetails(), annos);

						for(int row = 0; row < table.size(); row++) 
						{
							String name = table.getCellValue(row, 0);
							String scope = table.getCellValue(row, 1);
							String compname = table.getCellValue(row, 2);
							String comptype = table.getCellValue(row, 3);
							String proxytype = table.getCellValue(row, 4);
							String dynamictxt = table.getCellValue(row, 5);
							String createtxt = table.getCellValue(row, 6);
							String recovertxt = table.getCellValue(row, 7);
//							String creationtype = table.getCellValue(row, 8); todo

							boolean dynamic = new Boolean(dynamictxt).booleanValue();
							boolean create = new Boolean(createtxt).booleanValue();
							boolean recover = new Boolean(recovertxt).booleanValue();
							
							// todo: interceptors, proxytype
							
							RequiredServiceBinding binding = new RequiredServiceBinding(null, compname, comptype, dynamic, 
									scope, create, recover, null, proxytype==null || proxytype.length()==0? 
										BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED: proxytype, null);
							bindings.put(name, binding);
						}

						break;
					}
				}
				
				for(int i=0; i<annos.size(); i++)
				{
					MAnnotation anno = (MAnnotation)annos.get(i);
					
					// new jadex arguments handling
					if(anno.getSource().toLowerCase().endsWith("_arguments_table"))
					{
						MultiColumnTableEx table = parseBpmnMultiColumTable(anno.getDetails(), annos);
						
						for(int row = 0; row < table.size(); row++)
						{
							String name = table.getCellValue(row, 0);
							String isarg = table.getCellValue(row, 1);
							String isres = table.getCellValue(row, 2);
							String desc = table.getCellValue(row, 3);
							String typename = table.getCellValue(row, 4);
							String val = table.getCellValue(row, 5).length()>0? table.getCellValue(row, 5): null;
							
							boolean argi = isarg!=null && Boolean.parseBoolean(isarg);
							boolean resu = isres!=null && Boolean.parseBoolean(isres);
							
							Map	inivals	= null;
							if(table.getRowSize()>6)
							{
								String complexref = table.getCellValue(row, 6);
								Map vals = table.getComplexValue(complexref);
								if(vals!=null)
								{
									for(Iterator it=vals.keySet().iterator(); it.hasNext(); )
									{
										String configid = (String)it.next();
										String valtext = (String)vals.get(configid);
										ConfigurationInfo ci = (ConfigurationInfo)configurations.get(configid);
										if(ci!=null && valtext!=null && valtext.length()>0)
										{
											if(argi)
											{
												ci.addArgument(new UnparsedExpression(name, typename, valtext, null));
											}
											if(resu)
											{
												ci.addResult(new UnparsedExpression(name, typename, valtext, null));
											}
											if(!argi && !resu)
											{
												if(inivals==null)
													inivals	= new HashMap();
												inivals.put(ci.getName(), parser.parseExpression(val, model.getModelInfo().getAllImports(), null, context.getClassLoader()));
											}
										}
									}
								}
							}
							
							if(argi)
							{
								model.addArgument(new Argument(name, desc, typename, val));
							}
							if(resu)
							{
								model.addResult(new Argument(name, desc, typename, val));
							}
							if(!argi && !resu)
							{
								//UnparsedExpression exp = null;
								MContextVariable cv = new MContextVariable();
								cv.setName(name);
								cv.setClazz(new ClassInfo(typename));
								
								if(val!=null && val.length()>0)
								{
//									exp = parser.parseExpression(val, model.getModelInfo().getAllImports(), null, context.getClassLoader());
									//exp = new UnparsedExpression(name, typename, val, null);
									cv.setValue(val);
								}
								//model.addContextVariable(name, new ClassInfo(SReflect.findClass0(typename, model.getModelInfo().getAllImports(), context.getClassLoader())), exp, inivals);
								model.addContextVariable(cv);
							}
						
//							System.out.println("Argument: "+arg);
						}
					} 
					
					// new jadex properties handling
					else if(anno.getSource().toLowerCase().endsWith("_properties_table"))
					{
						MultiColumnTableEx table = parseBpmnMultiColumTable(anno.getDetails(), annos);
						for(int row=0; row<table.size(); row++)
						{
							// normal property has 3 values
							String name = null;
							String value = null;
							String typename	= null;
							name = table.getCellValue(row, 0);
							if(table.get(row).size()==2)
							{
								value = table.getCellValue(row, 1);
							}
							else if(table.get(row).size()==3)
							{
								typename = table.getCellValue(row, 1);
								value = table.getCellValue(row, 2);
							}
							if(value!=null && value.length()>0)
							{
								model.addProperty(name, new UnparsedExpression(name, typename, value, null));
							}
						}
					}
					else if(anno.getSource().toLowerCase().endsWith("_providedservices_table"))
					{
						MultiColumnTableEx table = parseBpmnMultiColumTable(anno.getDetails(), annos);
						for(int row=0; row<table.size(); row++)
						{
							String name = table.getCellValue(row, 0);
							String typename = table.getCellValue(row, 1);
							String proxytype = table.getCellValue(row, 2);
							String implname = table.getCellValue(row, 3);
							if("".equals(implname))
								implname	= null;

//							Class impltype = implname!=null ? SReflect.findClass0(implname, mi.getAllImports(), context.getClassLoader()) : null;
//							Class type = SReflect.findClass0(typename, mi.getAllImports(), context.getClassLoader());
							ClassInfo type = new ClassInfo(typename);
//							if(type==null)
//							{
//								try
//								{
//									((AReadContext)context).getReporter().report("Type not found: "+typename, null, null, null);
//								}
//								catch(Exception e)
//								{
//									throw new RuntimeException(e);
//								}
//							}
							RequiredServiceBinding binding = implname!=null ? (RequiredServiceBinding)bindings.get(implname) : null;
							ProvidedServiceImplementation psim	= null;
							if(binding!=null)
							{
								// todo: interceptors
//								psim = new ProvidedServiceImplementation(impltype, null, proxytype, binding, null);
								psim = new ProvidedServiceImplementation();
								psim.setClazz(implname != null? new ClassInfo(implname) : null);
								psim.setProxytype(proxytype);
								psim.setBinding(binding);
							}
							else
							{
								// todo: interceptors
//								psim = new ProvidedServiceImplementation(impltype, impltype==null? implname: null, proxytype, null, null);
								psim = new ProvidedServiceImplementation();
//								psim.setClazz(implname != null? new ClassInfo(implname) : null);
								psim.setValue(implname);
								//TODO? Expression?
								psim.setProxytype(proxytype);
								psim.setBinding(binding);
							}
							
							// todo: support scope, publish and now also properties
							ProvidedServiceInfo psi = new ProvidedServiceInfo(name, type, psim, null, null, null);
//							ProvidedServiceInfo psi = new ProvidedServiceInfo(name, type, null, null);
							mi.addProvidedService(psi);
							
							if(table.getRowSize()>4)
							{
								String initialref = table.getCellValue(row, 4);
								Map vals = table.getComplexValue(initialref);
								for(Iterator it=vals.keySet().iterator(); it.hasNext(); )
								{
									String configid = (String)it.next();
									implname = (String)vals.get(configid);
									if("".equals(implname))
										implname	= null;
									ConfigurationInfo ci = (ConfigurationInfo)configurations.get(configid);
									if(ci!=null)
									{
//										impltype = implname!=null ? SReflect.findClass0(implname, mi.getAllImports(), context.getClassLoader()) : null;
										binding = implname!=null ? (RequiredServiceBinding)bindings.get(implname) : null;
//										if(binding!=null)
//										{
//											// todo: interceptors
//											psim = new ProvidedServiceImplementation(impltype, null, proxytype, binding, null);
//										}
//										else
//										{
//											// todo: interceptors
//											psim = new ProvidedServiceImplementation(impltype, impltype==null? implname: null, proxytype, null, null);
//										}
										if(binding!=null)
										{
											// todo: interceptors
//											psim = new ProvidedServiceImplementation(impltype, null, proxytype, binding, null);
											psim = new ProvidedServiceImplementation();
											psim.setClazz(implname != null? new ClassInfo(implname) : null);
											psim.setProxytype(proxytype);
											psim.setBinding(binding);
										}
										else
										{
											// todo: interceptors
//											psim = new ProvidedServiceImplementation(impltype, impltype==null? implname: null, proxytype, null, null);
											psim = new ProvidedServiceImplementation();
//											psim.setClazz(implname != null? new ClassInfo(implname) : null);
											psim.setValue(implname);
											//TODO? Expression?
											psim.setProxytype(proxytype);
											psim.setBinding(binding);
										}
										// todo: support scope, publish and now also properties
										ci.addProvidedService(new ProvidedServiceInfo(name, type, psim, null, null, null));
//										ci.addProvidedService(new ProvidedServiceInfo(name, type, null, null));
									}
								}
							}
						}
					}
					else if(anno.getSource().toLowerCase().endsWith("_requiredservices_table"))
					{
						MultiColumnTableEx table = parseBpmnMultiColumTable(anno.getDetails(), annos);
						for(int row=0; row<table.size(); row++)
						{
							String name = table.getCellValue(row, 0);
							String typename = table.getCellValue(row, 1);
							String multi = table.getCellValue(row, 2);
							String bindingname = table.getCellValue(row, 3);
							// todo:
							String mtypename = null;//table.getCellValue(row, 4);
							if("".equals(bindingname))
								bindingname	= null;
//							Class<?> type = SReflect.findClass0(typename, mi.getAllImports(), context.getClassLoader());
//							Class<?> mtype = mtypename==null? null: SReflect.findClass0(mtypename, mi.getAllImports(), context.getClassLoader());
							boolean multiple = new Boolean(multi).booleanValue();
							
							RequiredServiceInfo rsi;
							if(bindingname!=null)
							{
								RequiredServiceBinding binding = (RequiredServiceBinding)bindings.get(bindingname);
								if(binding==null)
									throw new RuntimeException("Unknown binding: "+bindingname);
								rsi = new RequiredServiceInfo(name, new ClassInfo(typename), multiple, new ClassInfo(mtypename), binding, null, null);
							}
							else
							{
//								rsi = new RequiredServiceInfo(name, type);
								rsi = new RequiredServiceInfo(name, new ClassInfo(typename), false, new ClassInfo(mtypename), new RequiredServiceBinding(name, RequiredServiceInfo.SCOPE_APPLICATION), null, null);
								rsi.setMultiple(multiple);
							}
							mi.addRequiredService(rsi);
							
							if(table.getRowSize()>4)
							{
								String initialref = table.getCellValue(row, 4);
								Map vals = table.getComplexValue(initialref);
								for(Iterator it=vals.keySet().iterator(); it.hasNext(); )
								{
									String configid = (String)it.next();
									bindingname = (String)vals.get(configid);
									if("".equals(bindingname))
										bindingname	= null;
									ConfigurationInfo ci = (ConfigurationInfo)configurations.get(configid);
									if(ci!=null)
									{
										if(bindingname!=null)
										{
											RequiredServiceBinding binding = (RequiredServiceBinding)bindings.get(bindingname);
											if(binding==null)
												throw new RuntimeException("Unknown binding: "+bindingname);
											rsi = new RequiredServiceInfo(name, new ClassInfo(typename), multiple, new ClassInfo(mtypename), binding, null, null);
										}
										else
										{
//											rsi = new RequiredServiceInfo(name, new ClassInfo(typename));
											rsi = new RequiredServiceInfo(name, new ClassInfo(typename), false, new ClassInfo(mtypename), new RequiredServiceBinding(name, RequiredServiceInfo.SCOPE_APPLICATION), null, null);
											rsi.setMultiple(multiple);
										}
										ci.addRequiredService(rsi);
									}
								}
							}
						}
					}
					else if(anno.getSource().toLowerCase().endsWith("_subcomponents_table"))
					{
						MultiColumnTableEx table = parseBpmnMultiColumTable(anno.getDetails(), annos);
						for(int row=0; row<table.size(); row++)
						{
							String name = table.getCellValue(row, 0);
							String filename = table.getCellValue(row, 1);
							String instnameref = table.getCellValue(row, 2);
							String numref = table.getCellValue(row, 3);
							String argref = table.getCellValue(row, 4);
							
							SubcomponentTypeInfo suco = new SubcomponentTypeInfo(name, filename);
							mi.addSubcomponentType(suco);
							
							if(table.getRowSize()>5)
							{
								Map instnames = table.getComplexValue(instnameref);
								Map nums = table.getComplexValue(numref);
								Map args = table.getComplexValue(argref);
								
								for(Iterator it=configurations.keySet().iterator(); it.hasNext(); )
								{
									String configid = (String)it.next();
									ConfigurationInfo ci = (ConfigurationInfo)configurations.get(configid);
									String instname = (String)instnames.get(configid);
									String number = (String)nums.get(configid);
									
									String argstext = (String)args.get(configid);
									
									if((instname!=null && instname.length()>0) || (number!=null && number.length()>0))
									{
										ComponentInstanceInfo cii = new ComponentInstanceInfo(instname!=null && instname.length()>0? instname: null, 
											name!=null && name.length()>0? name: null, null, number!=null && number.length()>0? number: null);
										cii.setArgumentsExpression(new UnparsedExpression(null, Map.class, argstext, null));
										ci.addComponentInstance(cii);
									}
								}
							}
						}
					}
					else if(!anno.getSource().toLowerCase().endsWith("table") && !anno.getSource().toLowerCase().endsWith("complex"))
					{
						// handle other annotation details here
					
						List details = anno.getDetails();
						if(details!=null)
						{
							for(int j=0; j<details.size(); j++)
							{
								MAnnotationDetail detail = (MAnnotationDetail)details.get(j);
								
								String key = detail.getKey().toLowerCase();
								String value = detail.getValue();
								
								if("editor_version".equals(key))
								{
									// currently ignored
									// todo: maybe add a version attribute to the jadex model?
								}
								else if("description".equals(key))
								{
									mi.setDescription(value);
								}
								else if("configuration".equals(key))
								{
									// ignore? The selected configuration for next editor opening
								}
								else if("master".equals(key))
								{
									mi.setMaster(new Boolean(value));
								}
								else if("daemon".equals(key))
								{
									mi.setDaemon(new Boolean(value));
								}
								else if("autoshutdown".equals(key))
								{
									mi.setAutoShutdown(new Boolean(value));
								}
								else if("suspend".equals(key))
								{
									mi.setSuspend(new Boolean(value));
								}
								else if("keep alive".equals(key))
								{
									model.setKeepAlive(new Boolean(value).booleanValue());
								}
								else if("parameters".equals(key))
								{
									throw new RuntimeException("parameters no longer separately");
								}
								
								// TODO: remove old arguments handling
								else if("arguments".equals(key))
								{
									StringTokenizer stok = new StringTokenizer(value, LIST_ELEMENT_DELIMITER);
									while(stok.hasMoreTokens())
									{
										String argtext = stok.nextToken();
										StringTokenizer stok2 = new StringTokenizer(argtext, LIST_ELEMENT_ATTRIBUTE_DELIMITER);
										String name = stok2.nextToken();
										String isarg = stok2.nextToken();
										String isres = stok2.nextToken();
										String desc = stok2.nextToken();
										String typename = stok2.nextToken();
										String val = stok2.hasMoreTokens()? stok2.nextToken(): null;
										UnparsedExpression exp = null;
	
//										// context variable
//										Class clazz = SReflect.findClass0(typename, model.getAllImports(), context.getClassLoader());
//										if(clazz!=null)
//										{
//											
//											if(val!=null && val.length()>0)
//											{
//												exp = parser.parseExpression(val, model.getAllImports(), null, context.getClassLoader());
//											}
//											model.addContextVariable(name, clazz, exp);
//	//										System.out.println("Context variable: "+name);
//										}
										
										IArgument arg	= new Argument(name, desc, typename, val);
										
										boolean argi = isarg!=null && Boolean.parseBoolean(isarg);
										boolean resu = isres!=null && Boolean.parseBoolean(isres);
										if(argi)
										{
											model.addArgument(arg);
										}
										if(resu)
										{
											model.addResult(arg);
										}
										if(!argi && !resu)
										{
											//model.addContextVariable(name, new ClassInfo(arg.getClazz().getType(getClass().getClassLoader(), model.getModelInfo().getAllImports())), exp, null);
											MContextVariable cv = new MContextVariable();
											cv.setName(name);
											cv.setClazz(new ClassInfo(arg.getClazz().getTypeName()));
											model.addContextVariable(cv);
										}
	//									System.out.println("Argument: "+arg);
									}
								}
								else if("results".equals(key))
								{
									throw new RuntimeException("results no longer separately");
								}
								
								// TODO: remove old properties handling
								else if("properties".equals(key))
								{
									StringTokenizer stok = new StringTokenizer(value, LIST_ELEMENT_DELIMITER);
									while(stok.hasMoreTokens())
									{
										String proptext = stok.nextToken();
										StringTokenizer stok2 = new StringTokenizer(proptext, LIST_ELEMENT_ATTRIBUTE_DELIMITER);
										String name = stok2.nextToken();
										String val = stok2.hasMoreTokens()? stok2.nextToken(): null;
//										IParsedExpression exp = null;
	
										if(val!=null && val.length()>0)
										{
//											exp = parser.parseExpression(val, mi.getAllImports(), null, context.getClassLoader());
											UnparsedExpression exp = new UnparsedExpression(name, Object.class, val, null);
											try
											{
//												Object	propval	= exp!=null ? exp.getValue(null) : null;
												model.addProperty(name, exp);
											}
											catch(RuntimeException e)
											{
												e.printStackTrace();
											}
										}
									}
								}
								else if(!"package".equals(key) && !"imports".equals(key))
								{
									throw new RuntimeException("Error parsing annotation: "+key+", "+value);
								}
							}
						}
					}
				}
			}

			// Read information from artifact text (normal stp modeller)
			List arts = model.getArtifacts();
			if(arts!=null)
			{
				if(arts.size()>1)
					throw new RuntimeException("Diagram must have one artifact for imports/package");
				
				String desc = ((MArtifact)arts.get(0)).getDescription();
				StringTokenizer	stok = new StringTokenizer(desc, "\r\n");
				while(stok.hasMoreTokens())
				{
					String	prop	= stok.nextToken().trim();
					if(prop.endsWith(";"))
						prop = prop.substring(0, prop.length()-1);
					
					if(prop.startsWith("package"))
					{
						String packagename = prop.substring(prop.indexOf("package")+8).trim();
						model.setPackage(packagename);
					}
					else if(prop.startsWith("import"))
					{
						String imp = prop.substring(prop.indexOf("imports")+7).trim();
						model.addImport(imp);
					}
					else if(prop.startsWith("argument"))
					{
						String argstr = prop.substring(prop.indexOf("argument")+8).trim();
						
						try
						{
							IArgument arg = (IArgument)parser.parseExpression(argstr, model.getModelInfo().getAllImports(), null, 
								context.getClassLoader()).getValue(null);							
							model.addArgument(arg);
							// Hack!!! Add context variable for argument too.
//							model.addContextVariable(arg.getName(), SReflect.findClass(arg.getTypename(), model.getAllImports(), context.getClassLoader()), null);
						}
						catch(Exception e)
						{
//							e.printStackTrace();
							throw new RuntimeException("Error parsing argument: "+model+", "+argstr, e);
						}
					}
					else if(prop.startsWith("result"))
					{
						String resstr = prop.substring(prop.indexOf("result")+6).trim();
						
						try
						{
							IArgument res = (IArgument)parser.parseExpression(resstr, mi.getAllImports(), null, 
								context.getClassLoader()).getValue(null);
							model.addResult(res);
							// Hack!!! Add context variable for result too.
//							model.addContextVariable(res.getName(), SReflect.findClass(res.getTypename(), model.getAllImports(), context.getClassLoader()), null);
						}
						catch(Exception e)
						{
							throw new RuntimeException("Error parsing result: "+model+", "+resstr, e);
						}
					}
					else
					{
						// context variable
						
						String	init	= null;
						int	idx	= prop.indexOf("=");
						if(idx!=-1)
						{
							init	= prop.substring(idx+1);
							prop	= prop.substring(0, idx);
						}
						StringTokenizer stok2 = new StringTokenizer(prop, " \t");
						if(stok2.countTokens()==2)
						{
							String clazzname = stok2.nextToken();
//							Class clazz = SReflect.findClass0(clazzname, mi.getAllImports(), context.getClassLoader());
//							if(clazz!=null)
//							{
								String name = stok2.nextToken();
								//UnparsedExpression exp = null;
								MContextVariable cv = new MContextVariable();
								cv.setName(name);
								cv.setClazz(new ClassInfo(clazzname));
								if(init!=null)
								{
//									exp = parser.parseExpression(init, mi.getAllImports(), null, context.getClassLoader());
									//exp = new UnparsedExpression(name, clazz, init, null);
									cv.setValue(init);
								}
								
								//model.addContextVariable(name, new ClassInfo(clazz), exp, null);
								model.addContextVariable(cv);
//							}
						}
					}
				}
			}	
			
			// Create configurations for each pool.
			
			// todo: currently each pool is a config
			// one could allow arbitrary definitions of configs
//			Argument[] args = (Argument[])model.getModelInfo().getArguments();
//			List pools = model.getPools();
//			for(int i=0; i<pools.size(); i++)
//			{
//				MPool pool = (MPool)pools.get(i);
//				for(int j=0; j<args.length; )
//				MConfiguration config = new MConfiguration();
//				config.addPool(pool);
//				config.setName(pool.getName());
//				model.addC
//			}
			
			return null;
		}
		
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 1;
		}
		
//		/**
//		 * 
//		 */
//		protected Object parseValue(Class clazz, String val, JavaCCExpressionParser parser, )
//		{
//			Object ret = null;
//			try
//			{
//				if(clazz!=null)
//				{
//					if(val!=null && val.length()>0)
//					{
//						exp = parser.parseExpression(val, model.getModelInfo().getAllImports(), null, context.getClassLoader());
//						argval	= exp!=null ? exp.getValue(null) : null;
//					}
//				}
//			}
//			catch(RuntimeException e)
//			{
//				// Hack!!! initial value for context variable might not be accessible statically.
//			}
//		}
	}
	
	// ---- Helper method for various post IPostProcessor ----
	
//	/**
//	 * Parse a list of annotation details into an BpmnMultiColumnTable
//	 * @param details
//	 * @return BpmnMultiColumTable from details
//	 */
//	public static BpmnMultiColumTable parseBpmnMultiColumTable(List details)
//	{
//		BpmnMultiColumTable table = null;
//		// initialize the table
//		for(int j=0; j<details.size() && table == null; j++)
//		{
//			MAnnotationDetail detail = (MAnnotationDetail)details.get(j);
//			
//			String key = detail.getKey().toLowerCase();
//			String value = detail.getValue();
//			
//			if("dimension".equals(key))
//			{
//				table = new BpmnMultiColumTable(value);
//			}
//		}
//		
//		assert table != null;
//		
//		for(int j = 0; j < details.size(); j++) 
//		{
//			MAnnotationDetail detail = (MAnnotationDetail)details.get(j);
//
//			String key = detail.getKey().toLowerCase();
//			String value = detail.getValue();
//
//			// todo: fixme
//			if("dimension".equals(key) || "uniquecolumnindex".equals(key) || "complexcolumns".equals(key)) 
//			{
//				continue;
//			}
//			
//			table.setCellValue(key, value);
//		}
//		
//		return table;
//
//	}
	
	/**
	 * Parse a list of annotation details into an MultiColumnTableEx
	 * @param details
	 * @return BpmnMultiColumTable from details
	 */
	public static MultiColumnTableEx parseBpmnMultiColumTable(List details, List annos)
	{
		return MultiColumnTableEx.parseEAnnotationTable(details, annos);
	}
	
//	/**
//	 * Parse a list of annotation details into an MultiColumnTableEx
//	 * @param details
//	 * @return BpmnMultiColumTable from details
//	 */
//	public static MultiColumnTableEx parseBpmnMultiColumTable(List details)
//	{
//		return MultiColumnTableEx.parseEAnnotationTable(details, null);
//	}
	
	
}

////---- BpmnMultiColumTable ----
//
//class BpmnMultiColumTable
//{
//	/** The table dimension [x, y] */
//	private int[] dimension;
//	
//	/** The table data */
//	private String[][] data;
//	
//
//	/**
//	 * Create a BpmnMultiColumnTable with given dimension
//	 * @param tableDimension
//	 */
//	public BpmnMultiColumTable(String tableDimension) 
//	{
//		this.dimension = parseCellIndex(tableDimension);
//		this.data = new String[dimension[0]][dimension[1]];
//	}
//	
//	/**
//	 * Parse a cell index string to an int[] for 2-dimensional table array
//	 * @param index
//	 * @return int[]{row, column} 
//	 */
//	protected int[] parseCellIndex(String index)
//	{
//		assert index != null;
//		String[] xy = index.split(":");
//		if (xy.length == 2)
//		{
//			try {
//				return new int[] { Integer.parseInt(xy[0]),
//						Integer.parseInt(xy[1]) };
//			} catch (NumberFormatException nfe) { /*ignore*/ }
//		}
//		
//		return new int[]{-1, -1};
//		
//	}
//	
//	/**
//	 * Set the value at given index in data[][]
//	 * @param cellIndex
//	 * @param value
//	 */
//	public void setCellValue(String cellIndex, String value)
//	{
//		int[] index = parseCellIndex(cellIndex);
//		assert index[0] != -1 && index[1] != -1;
//		assert index[0] < data.length;
//		assert index[1] < data[index[0]].length;
//		data[index[0]][index[1]] = value;
//	}
//	
//	/**
//	 * Get the value at given index
//	 * @param cellIndex
//	 * @return the value at index from data[][] or null
//	 */
//	public String getCellValue(String cellIndex)
//	{
//		int[] index = parseCellIndex(cellIndex);
//		assert index[0] != -1 && index[1] != -1;
//		assert index[0] < data.length;
//		assert index[1] < data[index[0]].length;
//		return data[index[0]][index[1]];
//	}
//	
//	/**
//	 *  Get the call data.
//	 */
//	public String getCellValue(int x, int y)
//	{
//		return data[x][y];
//	}
//	
//	/**
//	 *  Get the dimension.
//	 */
//	public int getDimension(int i)
//	{
//		return dimension[i];
//	}
//
//	/**
//	 *  Get row.
//	 */
//	public String[] getRow(int x)
//	{
//		return data[x];
//	}
//}
