package jadex.bpmn.model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.commons.ICacheableModel;
import jadex.commons.IdGenerator;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.collection.BiHashMap;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.javaparser.SJavaParser;


/**
 *  Java representation of a bpmn model for xml description.
 */
public class MBpmnModel extends MAnnotationElement implements ICacheableModel//, ILoadableComponentModel
{
	//-------- constants --------
	
	/** Constant for task. */
	public static final String TASK = "Task";

	/** Constant for sub process. */
	public static final String SUBPROCESS = "SubProcess";
	
	/** Constant for gateway parallel. */
	public static final String GATEWAY_PARALLEL = "GatewayParallel";

	/** Constant for gateway data based exclusive. */
	public static final String GATEWAY_DATABASED_EXCLUSIVE = "GatewayDataBasedExclusive";

	/** Constant for gateway data based exclusive. */
	public static final String GATEWAY_DATABASED_INCLUSIVE = "GatewayDataBasedInclusive";
	
	
	/** Constant for event start empty. */
	public static final String EVENT_START_EMPTY = "EventStartEmpty";
	
	/** Constant for event start message. */
	public static final String EVENT_START_MESSAGE = "EventStartMessage";

	/** Constant for event start timer. */
	public static final String EVENT_START_TIMER = "EventStartTimer";

	/** Constant for event start rule. */
	public static final String EVENT_START_RULE = "EventStartRule";

	/** Constant for event start signal. */
	public static final String EVENT_START_SIGNAL = "EventStartSignal";
	
	/** Constant for event start multiple. */
	public static final String EVENT_START_MULTIPLE = "EventStartMultiple";
	
	
	/** Constant for event end empty. */
	public static final String EVENT_END_EMPTY = "EventEndEmpty";

	/** Constant for event end error. */
	public static final String EVENT_END_ERROR = "EventEndError";
	
	/** Constant for event end message. */
	public static final String EVENT_END_MESSAGE = "EventEndMessage";
		
	/** Constant for event end signal. */
	public static final String EVENT_END_SIGNAL = "EventEndSignal";
	
	/** Constant for event end compensation. */
	public static final String EVENT_END_COMPENSATION = "EventEndCompensation";
	
	/** Constant for event end cancellation. */
	public static final String EVENT_END_CANCEL = "EventEndCancel";
	
	/** Constant for event end cancellation. */
	public static final String EVENT_END_TERMINATE = "EventEndTerminate";
		
	
	/** Constant for event start empty. */
	public static final String EVENT_INTERMEDIATE_EMPTY = "EventIntermediateEmpty";
	
	/** Constant for event intermediate error. */
	public static final String EVENT_INTERMEDIATE_ERROR = "EventIntermediateError";
	
	/** Constant for event intermediate rule. */
	public static final String EVENT_INTERMEDIATE_RULE = "EventIntermediateRule";

	/** Constant for event intermediate signal. */
	public static final String EVENT_INTERMEDIATE_SIGNAL = "EventIntermediateSignal";
	
	/** Constant for event intermediate message. */
	public static final String EVENT_INTERMEDIATE_MESSAGE = "EventIntermediateMessage";
	
	/** Constant for event intermediate timer. */
	public static final String EVENT_INTERMEDIATE_TIMER = "EventIntermediateTimer";
	
	/** Constant for event intermediate compensation. */
	public static final String EVENT_INTERMEDIATE_COMPENSATION = "EventIntermediateCompensation";
	
	/** Constant for event intermediate cancellation. */
	public static final String EVENT_INTERMEDIATE_CANCEL = "EventIntermediateCancel";

	/** Constant for event intermediate multiple. */
	public static final String EVENT_INTERMEDIATE_MULTIPLE = "EventIntermediateMultiple";
	
	// Todo: add (or move from runtime handlers) constants for all properties supported in editor.
	
	/** Property name for error events. */
	public static final String PROPERTY_EVENT_ERROR = "exception";
	
	/** Property name for condition of rule events. */
	public static final String PROPERTY_EVENT_RULE_CONDITION = "condition";	
	
	/** Property name for event types of rule events. */
	public static final String PROPERTY_EVENT_RULE_EVENTTYPES = "eventtypes";	
	
	
	/** The signal event handler trigger parameter name. Used to put the trigger in the signal event arguments. */
	public static final String SIGNAL_EVENT_TRIGGER = "signal_trigger";
	
	/** The process trigger name. Used to put the trigger event in the process arguments. */
	public static final String TRIGGER = "_process_trigger";
		
	//-------- attributes --------
	
	/** The pools. */
	protected List<MPool> pools;
	
	/** The artifacts. */
	protected List<MArtifact> artifacts;
	
	/** The messages. */
	protected List<MMessagingEdge> messages;
	
	/** The outgoing data edges for results. */
	protected List<MDataEdge> resultdataedges;
	
	/** The incoming data edges for arguments. */
	protected List<MDataEdge> argdataedges;
			
	//-------- init structures --------
	
	/** The cached edges of the model. */
	protected Map<String, MSequenceEdge> alledges;

	/** The cached activities of the model. */
	protected Map<String, MActivity> allactivities;
	
	/** The cached event subprocess start events of the model. */
	protected Map<MSubProcess, List<MActivity>> eventsubprocessstartevents;
	
	/** The cached instance-matched events that require waiting. */
	protected List<MActivity> waitingevents;
	
	/** The cached type-matched start events of the model. */
	protected List<MActivity> typematchedstartevents;
	
	/** Parents of activities. */
	protected Map<MIdElement, MIdElement> parents;

	/** The association sources. */
	protected Map<String, MIdElement> associationsources;
	
	/** The association targets. */
	protected Map<String, MIdElement> associationtargets;
	
	/** The messaging edges. */
	protected Map<String, MMessagingEdge> allmessagingedges;
	
	/** The data edges. */
	protected Map<String, MDataEdge> alldataedges;
	
	//-------- added structures --------

	/** The context variables (name -> [class, initexpression]). */
	protected Map<String, MContextVariable> variables;
	
	/** The configurations (config name -> start elements). */
	protected Map<String, List<MNamedIdElement>> configurations;
	
	/** The keep alive flag that allows processes to stay after end event. */
	protected boolean keepalive;
	
	//-------- model management --------
	
	/** The last modified date. */
	protected long lastmodified;
	
	/** The last check date. */
	protected long lastchecked;
	
	/** The model info. */
	protected ModelInfo modelinfo;
	
	//-------- methods --------

	/**
	 *  Create a new model.
	 */
	public MBpmnModel()
	{
		this.modelinfo = new ModelInfo();
		modelinfo.internalSetRawModel(this);
	}
	
	/**
	 *  Init the model info.
	 */
	public void initModelInfo(ClassLoader cl)
	{
		List<String> names = new ArrayList<String>();
		for(Iterator<MActivity> it=getAllActivities().values().iterator(); it.hasNext(); )
		{
//			names.add(((MActivity)it.next()).getBreakpointId());
			names.add(((MActivity)it.next()).getId());
		}
		modelinfo.setBreakpoints((String[])names.toArray(new String[names.size()]));
//		addProperty("debugger.breakpoints", names);
		
//		modelinfo.setConfigurationNames(getConfigurations());
		
		String[] confignames = getConfigurations();
		if(confignames.length>0)
		{
			ConfigurationInfo[] cinfo = new ConfigurationInfo[confignames.length];
			for(int i=0; i<confignames.length; i++)
			{
				cinfo[i] = new ConfigurationInfo(confignames[i]);
				modelinfo.addConfiguration(cinfo[i]);
			}
//			modelinfo.setConfigurations(cinfo);
		}
		
//		if(imports!=null)
//			modelinfo.setImports((String[])imports.toArray(new String[imports.size()]));
		
		modelinfo.setStartable(true);
		
		final Map<MSubProcess, List<MActivity>> evtsubstarts = getEventSubProcessStartEventMapping();
		if(evtsubstarts!=null)
		{
			ProvidedServiceInfo[] psis = modelinfo.getProvidedServices();
			Set<Class<?>> haspsis = new HashSet<Class<?>>();
			if(psis!=null)
			{
				for(ProvidedServiceInfo psi: psis)
				{
					haspsis.add(psi.getType().getType(cl));
				}
			}
			
			for(Map.Entry<MSubProcess, List<MActivity>> entry: evtsubstarts.entrySet())
			{
				Class<?> iface = null;
				
				List<MActivity> macts = entry.getValue();
				for(MActivity mact: macts)
				{
					if(MBpmnModel.EVENT_START_MESSAGE.equals(mact.getActivityType()))
					{
						if(mact.hasPropertyValue(MActivity.IFACE))
						{
							if(iface==null)
							{
								UnparsedExpression uexp = mact.getPropertyValue(MActivity.IFACE);
								iface = (Class<?>)SJavaParser.parseExpression(uexp, getModelInfo().getAllImports(), cl).getValue(null);
							}
							
							if(iface!=null && !haspsis.contains(iface))
							{
								// found interface without provided service impl
								break;
							}
						}
					}
				}
				
				// todo: provided service scope
				if(iface!=null && !haspsis.contains(iface))
				{
					String exp = "java.lang.reflect.Proxy.newProxyInstance($component.getClassLoader()," 
						+ "new Class[]{"+iface.getName()+".class"
						+ "}, new jadex.bpmn.runtime.ProcessServiceInvocationHandler($component, \""+entry.getKey().getId()+"\"))";
					ProvidedServiceImplementation psim = new ProvidedServiceImplementation(null, exp, BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED, null, null);
					ProvidedServiceInfo psi = new ProvidedServiceInfo("internal_"+iface.getName(), iface, psim, null, null, null);
					modelinfo.addProvidedService(psi);
				}
			}
		}
	}
	
//	/**
//	 *  Add method info.
//	 */
//	public static void addMethodInfos(Map props, String type, String[] names)
//	{
//		Object ex = props.get(type);
//		if(ex!=null)
//		{
//			List newex = new ArrayList();
//			for(Iterator it=SReflect.getIterator(ex); it.hasNext(); )
//			{
//				newex.add(it.next());
//			}
//			for(int i=0; i<names.length; i++)
//			{
//				newex.add(names[i]);
//			}
//		}
//		else
//		{
//			props.put(type, names);
//		}
//	}
	
	/**
	 *  Get the pools.
	 *  @return The pools.
	 */
	public List<MPool> getPools()
	{
		return pools;
	}
	
	/**
	 *  Add a pool.
	 *  @param pool The pool. 
	 */
	public void addPool(MPool pool)
	{
		if(pools==null)
			pools = new ArrayList<MPool>();
		pools.add(pool);
	}
	
	/**
	 *  Remove a pool.
	 *  @param pool The pool.
	 */
	public void removePool(MPool pool)
	{
		if(pools!=null)
			pools.remove(pool);
	}
	
	/**
	 *  Get the pools.
	 *  @return The pools.
	 */
	public MPool getPool(String name)
	{
		MPool ret = name==null? (MPool)pools.get(0): null;
		for(int i=0; i<pools.size() && ret==null; i++)
		{
			MPool tmp = (MPool)pools.get(i);
			if(tmp.getName().equals(name))
				ret = tmp;
		}
		return ret;
	}
	
	/**
	 *  Get the artifacts.
	 *  @return The artifacts. 
	 */
	public List<MArtifact> getArtifacts()
	{
		return artifacts;
	}
	
	/**
	 *  Add an artifact.
	 *  @param artifact The artifact.  
	 */
	public void addArtifact(MArtifact artifact)
	{
		if(artifacts==null)
			artifacts = new ArrayList<MArtifact>();
		artifacts.add(artifact);
	}
	
	/**
	 *  Remove an artifact.
	 *  @param artifact The artifact.
	 */
	public void removeArtifact(MArtifact artifact)
	{
		if(artifacts!=null)
			artifacts.remove(artifact);
	}
	
	/**
	 *  Get the message edges.
	 *  @return The message edges.  
	 */
	public List<MMessagingEdge> getMessagingEdges()
	{
		return messages;
	}
	
	/**
	 *  Add a message edge.
	 *  @param message The message edfe.
	 */
	public void addMessagingEdge(MMessagingEdge message)
	{
		if(messages==null)
			messages = new ArrayList<MMessagingEdge>();
		messages.add(message);
	}
	
	/**
	 *  Remove a message edge.
	 *  @param message The message.
	 */
	public void removeMessagingEdge(MMessagingEdge message)
	{
		if(messages!=null)
			messages.remove(message);
	}
	
	//-------- helper init methods --------
	
	/**
	 *  Get all edges.
	 *  @return The edges (id -> edge).
	 */
	public Map<String, MEdge> getAllEdges()
	{
		Map<String, MEdge> edges = new HashMap<String, MEdge>();
		Map<String, MSequenceEdge> seqedges = getAllSequenceEdges();
		if (seqedges != null)
			edges.putAll(seqedges);
		Map<String, MMessagingEdge> mesedges = getAllMessagingEdges();
		if (mesedges != null)
			edges.putAll(mesedges);
		Map<String, MDataEdge> datedges = getAllDataEdges();
		if (datedges != null)
			edges.putAll(datedges);
		return edges;
	}
	
	/**
	 *  Get all data edges.
	 *  @return The data edges (id -> edge).
	 */
	public Map<String, MDataEdge> getAllDataEdges()
	{
		if(this.alldataedges==null)
		{
			Map<String, MActivity> acts = getAllActivities();
			for (MActivity act : acts.values())
			{
				List<MDataEdge> inde = act.getIncomingDataEdges();
				List<MDataEdge> outde = act.getOutgoingDataEdges();
				if (inde != null)
				{
					for (MDataEdge de : inde)
					{
						alldataedges.put(de.getId(), de);
					}
				}
				if (outde != null)
				{
					for (MDataEdge de : outde)
					{
						alldataedges.put(de.getId(), de);
					}
				}
			}
		}
		
		return alldataedges;
	}
	
	/**
	 *  Get all message edges.
	 *  @return The message edges (id -> edge).
	 */
	public Map<String, MMessagingEdge> getAllMessagingEdges()
	{
		if(this.allmessagingedges==null)
		{
			this.allmessagingedges = new HashMap<String, MMessagingEdge>();
			
			List<MMessagingEdge> messages = getMessagingEdges();
			if(messages!=null)
			{
				for(int i=0; i<messages.size(); i++)
				{
					MMessagingEdge msg = (MMessagingEdge)messages.get(i);
					allmessagingedges.put(msg.getId(), msg);
				}
			}
		}
		return allmessagingedges;
	}
	
	/**
	 *  Get all sequence edges.
	 *  @return The sequence edges (id -> edge).
	 */
	public Map<String, MSequenceEdge> getAllSequenceEdges()
	{
		if(this.alledges==null)
		{
			this.alledges = new HashMap<String, MSequenceEdge>();
			// todo: hierarchical search also in lanes of pools?!
			
			List<MPool> pools = getPools();
			if(pools!=null)
			{
				for(int i=0; i<pools.size(); i++)
				{
					MPool tmp = (MPool)pools.get(i);
					
					List<MActivity> acts = tmp.getActivities();
					if(acts!=null)
					{
						for(int j=0; j<acts.size(); j++)
						{
							getAllEdges(acts.get(j), alledges);
						}
					}
					
				}
			}
		}
		
		return alledges;
	}
	
	/**
	 *  Get all activities.
	 *  @return The activities (id -> activity).
	 */
	public Map<String, MActivity> getAllActivities()
	{
		if(this.allactivities==null)
		{
			this.allactivities = new HashMap<String, MActivity>();
			this.parents = new HashMap<MIdElement, MIdElement>();
			
			List<MPool> pools = getPools();
			if(pools!=null)
			{
				for(int i=0; i<pools.size(); i++)
				{
					MPool tmp = pools.get(i);
					
					List<MLane> lanes = tmp.getLanes();
					if (lanes != null)
					{
						for (MLane lane : lanes)
						{
							parents.put(lane, tmp);
						}
					}
					
					List<MActivity> acts = tmp.getActivities();
					if(acts!=null)
					{
						for(int j=0; j<acts.size(); j++)
						{
							MActivity mact = acts.get(j);
							MLane lane = mact.getLane();
							parents.put(mact, lane != null? lane : tmp);
							allactivities.put(mact.getId(), acts.get(j));
							if(mact instanceof MSubProcess)
							{
								addAllSubActivities((MSubProcess)mact, allactivities);
							}
							else
							{
								List<MActivity>	handlers = mact.getEventHandlers();
								if(handlers!=null)
								{
									for(int k=0; k<handlers.size(); k++)
									{
										MActivity mhact = (MActivity)handlers.get(k);
										allactivities.put(mhact.getId(), mhact);
										parents.put(mhact, mact);
									}
								}
							}
						}
					}
				}
			}
		}
		
		return allactivities;
	}
	
	/**
	 *  Add all subactivities.
	 */
	public void addAllSubActivities(MSubProcess proc, Map<String, MActivity> activities)
	{
		List<MActivity> acts = proc.getActivities();
		if(acts!=null)
		{
			for(int i=0; i<acts.size(); i++)
			{
				MActivity mact = (MActivity)acts.get(i);
				allactivities.put(mact.getId(), acts.get(i));
				parents.put(mact, proc);
				if(mact instanceof MSubProcess)
				{
					addAllSubActivities((MSubProcess)mact, activities);
				}
			}
		}
		List<MActivity>	handlers	= proc.getEventHandlers();
		if(handlers!=null)
		{
			for(int i=0; i<handlers.size(); i++)
			{
				MActivity mact = (MActivity)handlers.get(i);
				allactivities.put(mact.getId(), handlers.get(i));
				parents.put(handlers.get(i), mact);
				if(mact instanceof MSubProcess)
				{
					addAllSubActivities((MSubProcess)mact, activities);
				}
			}
		}
	}
	
	/**
	 *  Internal get all edges.
	 *  @param sub The subprocess.
	 *  @param edges The edges (results will be added to this).
	 */
	protected void getAllEdges(MActivity act, Map<String, MSequenceEdge> edges)
	{
//		addEdges(sub.getSequenceEdges(), edges);
		addEdges(act.getIncomingSequenceEdges(), edges);
		addEdges(act.getOutgoingSequenceEdges(), edges);
		
		if(act instanceof MSubProcess)
		{
			List<MActivity> acts = ((MSubProcess) act).getActivities();
			if(acts!=null)
			{
				for(int j=0; j<acts.size(); j++)
				{
					getAllEdges(acts.get(j), edges);
				}
			}
		}
	}

	/**
	 *  Add edges to the result map.
	 *  @param tmp The list of edges.
	 *  @param edges The result map (id -> edge).
	 */
	protected void addEdges(List<MSequenceEdge> tmp, Map<String, MSequenceEdge> edges)
	{
		if(tmp!=null)
		{
			for(int i=0; i<tmp.size(); i++)
			{
				MSequenceEdge edge = (MSequenceEdge)tmp.get(i);
				edges.put(edge.getId(), edge);
			}
		}
	}
	
	/**
	 *  Get all association targets.
	 *  @return A map of association targets (association id -> target).
	 */
	public Map<String, MIdElement> getAllAssociationTargets()
	{
		if(this.associationtargets==null)
		{
			this.associationtargets = new HashMap<String, MIdElement>();
			
			// Add pools
			List<MPool> pools = getPools();
			if(pools!=null)
			{
				for(int i=0; i<pools.size(); i++)
				{
					MPool pool = pools.get(i);
					addAssociations(pool.getAssociationsDescription(), pool, associationtargets);
					
					// Add lanes
					List<MLane> lanes = pool.getLanes();
					if(lanes!=null)
					{
						for(int j=0; j<lanes.size(); j++)
						{
							MLane lane = lanes.get(j);
							addAssociations(lane.getAssociationsDescription(), lane, associationtargets);
						}
					}
					
					// Add activities
					List<MActivity> acts = pool.getActivities();
					if(acts!=null)
					{
						for(int j=0; j<acts.size(); j++)
						{
							MActivity act = acts.get(j);
							addActivityTargets(act);
						}
					}
				}
			}
			
			// Add edges
			Map<String, MSequenceEdge> edges = getAllSequenceEdges();
			for(Iterator<MSequenceEdge> it=edges.values().iterator(); it.hasNext(); )
			{
				MSequenceEdge edge = it.next();
				addAssociations(edge.getAssociationsDescription(), edge, associationtargets);
			}
		}
		return associationtargets;
	}
	
	/**
	 *  Internal add activity targets.
	 *  @param act The activity.
	 */
	protected void addActivityTargets(MActivity act)
	{
		addAssociations(act.getAssociationsDescription(), act, associationtargets);
		if(act instanceof MSubProcess)
		{
			List<MActivity> acts = ((MSubProcess)act).getActivities();
			if(acts!=null)
			{
				for(int i=0; i<acts.size(); i++)
				{
					MActivity subact = (MActivity)acts.get(i);
					addActivityTargets(subact);
				}
			}
		}
	}
	
	/**
	 *  Internal add associations.
	 *  @param target The target.
	 *  @param targets The targets result map.
	 */
	protected boolean addAssociations(String assosdesc, MIdElement target,  Map<String, MIdElement> targets)
	{
		boolean ret = false;
		
//		String assosdesc = target.getAssociationsDescription();
		if(assosdesc!=null)
		{
			StringTokenizer stok = new StringTokenizer(assosdesc);
			while(stok.hasMoreElements() && !ret)
			{
				String assoid = stok.nextToken();
				targets.put(assoid, target);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get all association sources.
	 *  @return The map of association sources (association id -> source).
	 */
	public Map<String, MIdElement> getAllAssociationSources()
	{
		if(this.associationsources==null)
		{
			this.associationsources = new HashMap<String, MIdElement>();
		
			addArtifacts(getArtifacts(), associationsources);
		
			List<MPool> pools = getPools();
			if(pools!=null)
			{
				for(int i=0; i<pools.size(); i++)
				{
					MPool pool = pools.get(i);
					addArtifacts(pool.getArtifacts(), associationsources);
					
					// Search subprocesses
					List<MActivity> acts = pool.getActivities();
					if(acts!=null)
					{
						for(int j=0; j<acts.size(); j++)
						{
							Object act = acts.get(j);
							if(act instanceof MSubProcess)
							{
								addSubProcesses((MSubProcess)act, associationsources);
							}
						}
					}
				}
			}
		}
		return associationsources;
	}
	
	/**
	 *  Add sub processes.
	 *  @param subproc The sub process.
	 *  @param sources The sources result map.
	 */
	protected void addSubProcesses(MSubProcess subproc, Map<String, MIdElement> sources)
	{
		List<MArtifact> artifacts = subproc.getArtifacts();
		addArtifacts(artifacts, sources);
		
		List<MActivity> acts = subproc.getActivities();
		if(acts!=null)
		{
			for(int j=0; j<acts.size(); j++)
			{
				Object act = acts.get(j);
				if(act instanceof MSubProcess)
				{
					addSubProcesses(((MSubProcess)act), sources);
				}
			}
		}
	}
	
	/**
	 *  Add an outgoing edge.
	 *  @param edge The edge.
	 */
	public void addResultDataEdge(MDataEdge edge)
	{
		if(resultdataedges==null)
			resultdataedges = new ArrayList<MDataEdge>();
		resultdataedges.add(edge);
	}
	
	/**
	 *  Remove an outgoing edge.
	 *  @param edge The edge.
	 */
	public void removeResultDataEdge(MDataEdge edge)
	{
		if(resultdataedges!=null)
			resultdataedges.remove(edge);
	}
	
	/**
	 *  Add an incoming edge.
	 *  @param edge The edge.
	 */
	public void addArgumentDataEdge(MDataEdge edge)
	{
		if(argdataedges==null)
			argdataedges = new ArrayList<MDataEdge>();
		argdataedges.add(edge);
	}
	
	/**
	 *  Remove an outgoing edge.
	 *  @param edge The edge.
	 */
	public void removeArgumentDataEdge(MDataEdge edge)
	{
		if(argdataedges!=null)
			argdataedges.remove(edge);
	}
	
	/**
	 *  Get the incoming data edges.
	 *  @return the incoming data edges.
	 */
	public List<MDataEdge> getArgumentDataEdges()
	{
		return argdataedges;
	}
	
	/**
	 *  Set the incoming data edges.
	 *  @param indataedges The incoming data edges.
	 */
	public void setArgumentDataEdges(List<MDataEdge> indataedges)
	{
		this.argdataedges = indataedges;
	}
	
	/**
	 *  Get the outgoing data edges.
	 *  @return the outgoing data edges.
	 */
	public List<MDataEdge> getResultDataEdges()
	{
		return resultdataedges;
	}
	
	/**
	 *  Set the outgoing data edges.
	 *  @param outdataedges The outgoing data edges.
	 */
	public void setResultDataEdges(List<MDataEdge> outdataedges)
	{
		this.resultdataedges = outdataedges;
	}
	
	/**
	 *  Add artifacts.
	 *  @param artifacts The list of artifacts.
	 *  @param sources The sources result map (association id -> art).
	 */
	protected MArtifact addArtifacts(List<MArtifact> artifacts, Map<String, MIdElement> sources)
	{
		MArtifact ret = null;
		
		if(artifacts!=null)
		{
			for(int i=0; i<artifacts.size() && ret==null; i++)
			{
				MArtifact art = artifacts.get(i);
				List<MAssociation> assos = art.getAssociations();
				if(assos!=null)
				{
					for(int j=0; j<assos.size(); j++)
					{
						MAssociation asso = (MAssociation)assos.get(j);
						sources.put(asso.getId(), art);
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the name of the model.
	 *  @return The name of the model.
	 */
	public String	getName()
	{
		return modelinfo.getName();
	}
	
	/**
	 *  Set the name of the model.
	 *  @param name	The name to set.
	 */
	public void	setName(String name)
	{
		modelinfo.setName(name);
	}
	
	/**
	 *  Get the full model name (package.name)
	 *  @return The full name.
	 * /
	public String getFullName()
	{
		String pkg = getPackage();
		return pkg!=null && pkg.length()>0? pkg+"."+getName(): getName();
	}*/
	
	/**
	 *  Get all start activities of the model.
	 *  @return A non-empty List of start activities or null, if none.
	 */
	public List<MActivity> getStartActivities()
	{
		return getStartActivities(null, null);
	}
	
	/**
	 *  Get all start activities of the model.
	 *  @return A non-empty List of start activities or null, if none.
	 */
	public List<MActivity> getStartActivities(String poolname, String lanename)
	{
		List<MActivity> ret	= null;
		for(int i=0; pools!=null && i<pools.size(); i++)
		{
			MPool pool = (MPool)pools.get(i);
			if(poolname==null || poolname.equals(pool.getName()))
			{
				List<MActivity> tmp = pool.getStartActivities();
				if(tmp!=null)
				{
					if(lanename==null)
					{
						if(ret!=null)
						{
							ret.addAll(tmp);
						}
						else
						{
							ret	= tmp;
						}
					}
					else
					{
						if(ret==null)
						{
							ret = new ArrayList<MActivity>();
						}
						for(int j=0; j<tmp.size(); j++)
						{
							MActivity act = (MActivity)tmp.get(j);
							if(act.getLane()!=null && lanename.equals(act.getLane().getName()))
							{
								ret.add(act);
							}
						}
					}
				}
				List<MLane> lanes = pool.getLanes();
				if(lanes!=null)
				{
					for(MLane lane: lanes)
					{
						if(lanename==null || lanename.equals(lane.getName()))
						{
							tmp = lane.getStartActivities();
							
							if(ret!=null)
							{
								ret.addAll(tmp);
							}
							else
							{
								ret	= tmp;
							}
						}
					}
				}
			}
		}
		
		return ret;
	}
	
//	/**
//	 *  Add a pool/lane activation for a configurations.
//	 *  @param config The configuration name.
//	 *  @param poollane The poollane name (dot separated).
//	 */
//	public void addPoolLane(String config, String poollane)
//	{
//		if(configpoollanes==null)
//			configpoollanes = new HashMap();
//		configpoollanes.put(config, poollane);
//	}
//	
//	/**
//	 *  Get the pool lane.
//	 *  @param config The configurations.
//	 */
//	public String getPoolLane(String config)
//	{
//		return configpoollanes==null? null: (String)configpoollanes.get(config);
//	}
//	
//	/**
//	 *  Removes a pool/lane activation for a configurations.
//	 *  @param config The configuration name.
//	 *  @return The poollane name (dot separated).
//	 */
//	public String removePoolLane(String config)
//	{
//		return configpoollanes != null? (String) configpoollanes.remove(config): null;
//	}
	
	/**
	 *  Add a start element for a configurations.
	 *  @param config The configuration name.
	 *  @param element The start element name.
	 */
	public void addStartElement(String config, MNamedIdElement element)
	{
		if(configurations==null)
			configurations = new HashMap<String, List<MNamedIdElement>>();
		List<MNamedIdElement> elems = configurations.get(config);
		if(elems==null)
		{
			elems = new ArrayList<MNamedIdElement>();
			configurations.put(config, elems);
		}
		elems.add(element);
	}
	
	/**
	 *  Get the pool lane.
	 *  @param config The configurations.
	 */
	public List<MNamedIdElement> getStartElements(String config)
	{
		return configurations==null? null: (List<MNamedIdElement>)configurations.get(config);
	}
	
	/**
	 *  Removes a pool/lane activation for a configurations.
	 *  @param config The configuration name.
	 *  @return The poollane name (dot separated).
	 */
	public void removeStartElement(String config, MNamedIdElement element)
	{
		if(configurations!=null)
		{
			List<MNamedIdElement> elems = configurations.get(config);
			if(elems!=null)
			{
				elems.remove(element);
			}
		}
	}
	
	/**
	 *  Removes a pool/lane activation for a configurations.
	 *  @param config The configuration name.
	 *  @return The poollane name (dot separated).
	 */
	public void removeConfiguration(String config)
	{
		if(configurations!=null)
		{
			configurations.remove(config);
		}
	}

//	/**
//	 *  Get all imports.
//	 *  @return The imports.
//	 */
//	public String[] getAllImports()
//	{
//		List ret = new ArrayList();
//		if(modelinfo.getPackage()!=null)
//			ret.add(modelinfo.getPackage()+".*");
//		if(imports!=null)
//			ret.addAll(imports);
//		return (String[])ret.toArray(new String[ret.size()]);
//	}
	
	/**
	 *  Set the imports.
	 *  @param imports The imports.
	 * /
	public void setImports(String[] imports)
	{
		this.imports = imports;
	}*/
	
	/**
	 *  Add an import.
	 *  @param imp The import statement.
	 */
	public void addImport(String imp)
	{
		modelinfo.addImport(imp);
//		if(imports==null)
//			imports = new ArrayList();
//		this.imports.add(imp);
	}
	
	/**
	 *  Get the package name.
	 *  @return The package name.
	 * /
	public String getPackage()
	{
		return modelinfo.getPackage();
	}*/
	
	/**
	 *  Set the package name.
	 *  @param packagename The package name to set.
	 */
	public void setPackage(String packagename)
	{
		modelinfo.setPackage(packagename);
	}

	/**
	 *  Get a string representation of this AGR space type.
	 *  @return A string representation of this AGR space type.
	 */
	public String	toString()
	{
		StringBuffer	sbuf	= new StringBuffer();
		sbuf.append(SReflect.getInnerClassName(getClass()));
		sbuf.append("(name=");
		sbuf.append(getName());
		sbuf.append(")");
		return sbuf.toString();
	}
	
	//-------- static part --------
	
	/**
	 *  Get all start activities form the supplied set of activities.
	 *  Start activities are those without incoming edges. 
	 *  @return A non-empty List of start activities or null, if none.
	 */
	public static List<MActivity>	getStartActivities(List<MActivity> activities)
	{
		List<MActivity>	ret	= null;
		if (activities != null)
		{
			for(Iterator<MActivity> it=activities.iterator(); it.hasNext(); )
			{
				MActivity	activity	= it.next();
				if((activity.getIncomingSequenceEdges()==null ||
				    activity.getIncomingSequenceEdges().isEmpty()) &&
				    !((activity instanceof MSubProcess) &&
				    (MSubProcess.SUBPROCESSTYPE_EVENT.equals(((MSubProcess) activity).getSubprocessType()))))
				{
					if(ret==null)
					{
						ret	= new ArrayList<MActivity>();
					}
					ret.add(activity);
				}
			}
		}
		
		return ret;
	}

	/**
	 *  Add a context variable declaration.
	 *  @param variable The variable.
	 */
	public void addContextVariable(MContextVariable variable)
	{
		if(variables==null)
			variables	= new HashMap<String, MContextVariable>();
		
		variables.put(variable.getName(), variable);
	}

	/**
	 *  Remove a context variable declaration.
	 *  @param name	The variable name.
	 */
	public void removeContextVariable(String name)
	{
		if(variables!=null)
		{
			variables.remove(name);
			
			if(variables.isEmpty())
			{
				variables	= null;
			}
		}
	}
	
	/**
	 *  Remove a context variable declaration.
	 *  @param variable	The variable.
	 */
	public void removeContextVariable(MContextVariable variable)
	{
		removeContextVariable(variable.getName());
	}

	/**
	 *  Get the declared context variables.
	 *  @return A set of variable names.
	 */
	public List<MContextVariable> getContextVariables()
	{
		return variables!=null ? new ArrayList<MContextVariable>(variables.values()) : Collections.EMPTY_LIST;
	}

	/**
	 *  Get a declared context variable.
	 *  @param name	The variable name.
	 *  @return The variable.
	 */
	public MContextVariable getContextVariable(String name)
	{
		return variables != null? variables.get(name) : null;
	}

	/**
	 *  Get the initialization expression of a declared context variable.
	 *  @param name	The variable name.
	 *  @return The initialization expression (if any).
	 */
	public UnparsedExpression getContextVariableExpression(String name, String config)
	{
		MContextVariable variable = getContextVariable(name);
		return config != null? variable.getValue(config) : variable;
	}
	
	/**
	 *  Set the initialization expression of a declared context variable.
	 *  @param name	The variable name.
	 */
	public void setContextVariableExpression(String config, UnparsedExpression exp)
	{
		if (config == null && exp instanceof MContextVariable)
		{
			variables.put(exp.getName(), (MContextVariable) exp);
		}
		else if (config != null)
		{
			MContextVariable var =  getContextVariable(exp.getName());
			if (var != null)
			{
				var.setValue(config, exp);
			}
		}
	}

	/**
	 *  Get the filename.
	 *  @return The filename.
	 * /
	public String getFilename()
	{
		return modelinfo.getFilename();
	}*/

	/**
	 *  Set the filename.
	 *  @param filename The filename to set.
	 */
	public void setFilename(String filename)
	{
		modelinfo.setFilename(filename);
	}

	/**
	 *  Get the lastmodified date.
	 *  @return The lastmodified date.
	 */
	public long getLastModified()
	{
		return this.lastmodified;
	}

	/**
	 *  Set the lastmodified date.
	 *  @param lastmodified The lastmodified date to set.
	 */
	public void setLastModified(long lastmodified)
	{
		this.lastmodified = lastmodified;
	}

	/**
	 *  Get the last checked date.
	 *  @return The last checked date
	 */
	public long getLastChecked()
	{
		return this.lastchecked;
	}

	/**
	 *  Set the last checked date.
	 *  @param lastchecked The last checked date to set.
	 */
	public void setLastChecked(long lastchecked)
	{
		this.lastchecked = lastchecked;
	}
	
	/**
	 *  Get the configurations.
	 *  @return The configuration.
	 */
	protected String[] getConfigurations()
	{
		return configurations==null? new String[0]: configurations.keySet().toArray(new String[configurations.size()]);
				
//		// Todo: more in configuration than just pools/lanes?
//		String[]	ret;
//		List	pools	= getPools();
//		if(pools!=null)
//		{
//			List	aret	= new ArrayList();
//			if(pools.size()>1)
//			{
//				aret.add("All");
//			}
//			
//			for(int i=0; i<pools.size(); i++)
//			{
//				MPool	pool	= (MPool)pools.get(i);
//				aret.add(pool.getName());
//				
//				List	lanes	= pool.getLanes();
//				if(lanes!=null)
//				{
//					for(int j=0; j<lanes.size(); j++)
//					{
//						MLane	lane	= (MLane)lanes.get(j);
//						String	name	= lane.getName();
//						while(lane.getLane()!=null)
//						{
//							lane	= lane.getLane();
//							name	= lane.getName() + "." + name;
//						}
//						
//						aret.add(pool.getName()+"."+name);
//					}
//					ret	= (String[])aret.toArray(new String[aret.size()]);
//				}
//			}			
//			ret	= (String[])aret.toArray(new String[aret.size()]);
//		}
//		else
//		{
//			ret	= SUtil.EMPTY_STRING_ARRAY;
//		}
//		
//		return ret;
	}
	
	/**
	 *  Add an argument.
	 *  @param argument The argument.
	 */
	public void addArgument(IArgument argument)
	{
		modelinfo.addArgument(argument);
	}
	
	/**
	 *  Get the properties.
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools. 
	 *  @return The properties.
	 * /
	public Map	getProperties()
	{
		if(properties==null)
		{
			Map	props	= new HashMap();
			List	names	= new ArrayList();
			for(Iterator it=getAllActivities().values().iterator(); it.hasNext(); )
			{
				names.add(((MActivity)it.next()).getBreakpointId());
			}
			props.put("debugger.breakpoints", names);
			this.properties	= props;
		}
		return this.properties;
	}*/

	/**
	 *  Add a property.
	 */
	public void	addProperty(String name, Object value)
	{
		modelinfo.addProperty(name, value);
	}

	/**
	 *  Add a result.
	 *  @param result The result.
	 */
	public void addResult(IArgument result)
	{
		modelinfo.addResult(result);
	}
	
//	/**
//	 *  Set the classloader.
//	 *  @param classloader The classloader to set.
//	 */
//	public void setClassloader(ClassLoader classloader)
//	{
//		modelinfo.setClassloader(classloader);
//	}
	
	/**
	 *  Get the model info.
	 *  @return The model info.
	 */
	public IModelInfo getModelInfo()
	{
		return modelinfo;
	}

	/**
	 *  Get the keepalive.
	 *  @return the keepalive.
	 */
	public boolean isKeepAlive()
	{
		return keepalive;
	}

	/**
	 *  Set the keepalive.
	 *  @param keepalive The keepalive to set.
	 */
	public void setKeepAlive(boolean keepalive)
	{
		this.keepalive = keepalive;
	}

	/**
	 *  Get the classloader.
	 *  @return the classloader.
	 */
	public ClassLoader getClassLoader()
	{
		return modelinfo.getClassLoader();
	}

	/**
	 *  Set the classloader.
	 *  @param classloader The classloader to set.
	 */
	public void setClassLoader(ClassLoader classloader)
	{
		modelinfo.setClassloader(classloader);
	}
	
	/**
	 *  Set the resource identifier.
	 *  @param rid The resource identifier.
	 */
	public void setResourceIdentifier(IResourceIdentifier rid)
	{
		modelinfo.setResourceIdentifier(rid);
	}
	
	/**
	 *  Get the resource identifier.
	 *  @return The resource identifier.
	 */
	public IResourceIdentifier getResourceIdentifier()
	{
		return modelinfo.getResourceIdentifier();
	}
	
	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename()
	{
		return modelinfo.getFilename();
	}
	
	/**
	 *  Gets the parent of an element.
	 *  
	 *  @param element The element.
	 *  @return The parent.
	 */
	public MIdElement getParent(MIdElement element)
	{
		if (parents == null)
		{
			getAllActivities();
		}
		MIdElement ret = null;
		if (element instanceof MActivity ||
			element instanceof MLane)
		{
			ret = parents.get(element);
		}
		else if (element instanceof MEdge)
		{
			element = ((MEdge) element).getSource();
			ret = parents.get(element);
		}
		return ret;
	}
	
	/**
	 *  Returns all start events triggered based on type matching.
	 *  @return Events triggered based on type matching.
	 */
	public List<MActivity> getTypeMatchedStartEvents()
	{
		if(typematchedstartevents == null)
		{
			initMatchedStartEventCache();
		}
		return typematchedstartevents;
	}
	
	/**
	 *  Returns all start events in event subprocesses.
	 *  @return Start events in event subprocesses.
	 */
	public List<MActivity> getEventSubProcessStartEvents()
	{
		if(eventsubprocessstartevents == null)
		{
			initMatchedStartEventCache();
		}
		List<MActivity> ret = new ArrayList<MActivity>();
		for (List<MActivity> acts : eventsubprocessstartevents.values())
		{
			ret.addAll(acts);
		}
		return ret;
	}
	
	/**
	 *  Returns a mapping from event subprocesses to their start events.
	 *  @return The mapping
	 */
	public Map<MSubProcess, List<MActivity>> getEventSubProcessStartEventMapping()
	{
		if(eventsubprocessstartevents == null)
		{
			initMatchedStartEventCache();
		}
		return eventsubprocessstartevents;
	}
	
	/**
	 *  Returns all events waiting for outside triggers.
	 *  @return Events waiting for outside triggers.
	 */
	public List<MActivity> getWaitingEvents()
	{
		if(waitingevents == null)
		{
			initMatchedStartEventCache();
		}
		return waitingevents;
	}
	
	/**
	 *  Initializes the type and instance event trigger caches.
	 */
	protected void initMatchedStartEventCache()
	{
		eventsubprocessstartevents = new HashMap<MSubProcess, List<MActivity>>();
		typematchedstartevents = new ArrayList<MActivity>();
		waitingevents = new ArrayList<MActivity>();
		List<MActivity> starteventtriggers = new ArrayList<MActivity>();
		List<MSubProcess> subprocesses = new ArrayList<MSubProcess>();
		Map<String, MActivity> allactivities = getAllActivities();
		for (Map.Entry<String, MActivity> entry : allactivities.entrySet())
		{
			if (MBpmnModel.EVENT_START_RULE.equals(entry.getValue().getActivityType()) ||
				MBpmnModel.EVENT_START_TIMER.equals(entry.getValue().getActivityType()) ||
				MBpmnModel.EVENT_START_MESSAGE.equals(entry.getValue().getActivityType()))
			{
				starteventtriggers.add(entry.getValue());
			}
			else if (MBpmnModel.EVENT_INTERMEDIATE_RULE.equals(entry.getValue().getActivityType()) ||
					  MBpmnModel.EVENT_INTERMEDIATE_TIMER.equals(entry.getValue().getActivityType()) ||
					  MBpmnModel.EVENT_INTERMEDIATE_MESSAGE.equals(entry.getValue().getActivityType()))
			{
				waitingevents.add(entry.getValue());
			}
			else if (entry.getValue() instanceof MSubProcess &&
					  ((MSubProcess) entry.getValue()).getActivities() != null &&
					  !((MSubProcess) entry.getValue()).getActivities().isEmpty())
			{
				subprocesses.add((MSubProcess) entry.getValue());
			}
		}
		
		for (MActivity startevent : starteventtriggers)
		{
			boolean contained = false;
			for (MSubProcess subproc : subprocesses)
			{
				if (subproc.getActivities().contains(startevent))
				{
					contained = true;
					if (MSubProcess.SUBPROCESSTYPE_EVENT.equals(subproc.getSubprocessType()))
					{
//						eventsubprocessstartevents.put(subproc, startevent);
						List<MActivity> acts = eventsubprocessstartevents.get(subproc);
						if (acts == null)
						{
							acts = new ArrayList<MActivity>();
							eventsubprocessstartevents.put(subproc, acts);
						}
						acts.add(startevent);
					}
					else
					{
						waitingevents.add(startevent);
					}
					break;
				}
			}
			if (!contained)
			{
				typematchedstartevents.add(startevent);
			}
		}
	}
	
	/**
	 *  Clones a set of elements.
	 *  
	 * 	@param originals The original elements.
	 * 	@return Mapping of original IDs to cloned IDs, cloned elements.
	 */
	public Tuple2<BiHashMap<String,String>, List<MIdElement>> cloneElements(final Set<MIdElement> originals)
	{
		final BiHashMap<String, String> idmap = new BiHashMap<String, String>();
		final IdGenerator idgen = new IdGenerator();
		Traverser trav = new Traverser()
		{
			public Object doTraverse(Object object, Class<?> clazz, Map<Object, Object> traversed, List<ITraverseProcessor> processors, boolean clone, ClassLoader targetcl, Object context)
			{
				boolean istraversed = traversed.containsKey(object);
				Object ret = super.doTraverse(object, clazz, traversed, processors, clone, targetcl, context);
				if (!istraversed && object != ret && ret instanceof MIdElement)
				{
					String oldid = ((MIdElement) ret).getId();
					((MIdElement) ret).setId(idgen.generateId());
					idmap.put(oldid, ((MIdElement) ret).getId());
				}
				return ret;
			}
		};
		List<ITraverseProcessor> procs = new ArrayList<ITraverseProcessor>();
		procs.add(new ITraverseProcessor()
		{
			public Object process(Object object, Type type, List<ITraverseProcessor> processors, Traverser traverser, Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
			{
				return Traverser.IGNORE_RESULT;
			}
			
			public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
			{
				boolean ret = false;
				if (object instanceof MEdge)
				{
					MEdge medge = (MEdge) object;
					if (!isContainedInParentSet(originals, medge.getSource()) ||
						!isContainedInParentSet(originals, medge.getTarget()))
					{
						ret = true;
					}
				}
				return ret;
			}
		});
		procs.add(new ITraverseProcessor()
		{
			public Object process(Object object, Type type, List<ITraverseProcessor> processors, Traverser traverser, Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
			{
				return object;
			}
			
			public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
			{
				return object instanceof MPool ||
						object instanceof MLane ||
						(object instanceof MIdElement && !isContainedInParentSet(originals, (MIdElement) object));
			}
		});
		procs.addAll(Traverser.getDefaultProcessors());
		
		List<MIdElement> mclone = new ArrayList<MIdElement>(originals);
		mclone = (List<MIdElement>) trav.traverse(mclone, null, new IdentityHashMap<Object, Object>(), procs, true, null, null);
		
		clearCaches();
		return new Tuple2<BiHashMap<String,String>, List<MIdElement>>(idmap, mclone);
	}
	
	/**
	 *  Checks if an element or one of its parents is in a set.
	 *  
	 * 	@param mmap The set.
	 *	@param idelem The element
	 * 	@return True, if contained.
	 */
	public boolean isContainedInParentSet(Set<MIdElement> mmap, MIdElement idelem)
	{
		boolean ret = false;
		
		ret = mmap.contains(idelem);
		boolean running = true;
		while (!ret && running)
		{
			MIdElement parent = null;
			parent = this.getParent(idelem);
			if (parent == null)
			{
				running = false;
			}
			else
			{
				if (mmap.contains(parent))
				{
					ret = true;
				}
				else
				{
					idelem = parent;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get an activity by id.
	 *  @param id The id.
	 *  @return The activity.
	 */
	public MActivity getActivityById(String id)
	{
		return getAllActivities().get(id);
	}
	
	/**
	 *  Clears the model caches if stale.
	 */
	public void clearCaches()
	{
		allactivities = null;
		eventsubprocessstartevents = null;
		waitingevents = null;
		typematchedstartevents = null;
		parents = null;
		alledges = null;
		allmessagingedges = null;
		alldataedges = null;
	}
	
}
