package jadex.bpmn.model;

import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.ICacheableModel;
import jadex.commons.SReflect;
import jadex.commons.SUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


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
	
	/** The process trigger name. Used to put the trigger event in the process arguments. */
	public static final String TRIGGER = "_process_trigger";
	
	//-------- attributes --------
	
	/** The pools. */
	protected List pools;
	
	/** The artifacts. */
	protected List artifacts;
	
	/** The messages. */
	protected List messages;
			
	//-------- init structures --------
	
	/** The cached edges of the model. */
	protected Map alledges;

	/** The cached activities of the model. */
	protected Map allactivities;

	/** The association sources. */
	protected Map associationsources;
	
	/** The association targets. */
	protected Map associationtargets;
	
	/** The messaging edges. */
	protected Map allmessagingedges;
	
	//-------- added structures --------

	/** The context variables (name -> [class, initexpression]). */
	protected Map<String, MContextVariable> variabls;
	
	/** The pool/names in configurations. */
	protected Map configpoollanes;
	
	/** The keep alive flag that allows processes to stay after end event. */
	protected boolean keepalive;
	
	//-------- model management --------
	
	/** The last modified date. */
	protected long lastmodified;
	
	/** The last check date. */
	protected long lastchecked;
	
	/** The model info. */
	protected ModelInfo modelinfo;
	
	
	/** The classloader. */
	protected ClassLoader classloader;
	
	//-------- methods --------

	/**
	 *  Create a new model.
	 */
	public MBpmnModel()
	{
		this.modelinfo = new ModelInfo();
		//FIXME: Hack!
		modelinfo.setType("BPMN Process");
	}
	
	/**
	 *  Init the model info.
	 */
	public void initModelInfo()
	{
		List names = new ArrayList();
		for(Iterator it=getAllActivities().values().iterator(); it.hasNext(); )
		{
			names.add(((MActivity)it.next()).getBreakpointId());
		}
		addProperty("debugger.breakpoints", names);
		
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
	}
	
	/**
	 *  Add method info.
	 */
	public static void addMethodInfos(Map props, String type, String[] names)
	{
		Object ex = props.get(type);
		if(ex!=null)
		{
			List newex = new ArrayList();
			for(Iterator it=SReflect.getIterator(ex); it.hasNext(); )
			{
				newex.add(it.next());
			}
			for(int i=0; i<names.length; i++)
			{
				newex.add(names[i]);
			}
		}
		else
		{
			props.put(type, names);
		}
	}
	
	/**
	 *  Get the pools.
	 *  @return The pools.
	 */
	public List getPools()
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
			pools = new ArrayList();
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
	public List getArtifacts()
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
			artifacts = new ArrayList();
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
	public List getMessagingEdges()
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
			messages = new ArrayList();
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
	 *  Get all message edges.
	 *  @return The message edges (id -> edge).
	 */
	public Map getAllMessagingEdges()
	{
		if(this.allmessagingedges==null)
		{
			this.allmessagingedges = new HashMap();
			
			List messages = getMessagingEdges();
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
	public Map getAllSequenceEdges()
	{
		if(this.alledges==null)
		{
			this.alledges = new HashMap();
			// todo: hierarchical search also in lanes of pools?!
			
			List pools = getPools();
			if(pools!=null)
			{
				for(int i=0; i<pools.size(); i++)
				{
					MPool tmp = (MPool)pools.get(i);
					addEdges(tmp.getSequenceEdges(), alledges);
					
					List acts = tmp.getActivities();
					if(acts!=null)
					{
						for(int j=0; j<acts.size(); j++)
						{
							if(acts.get(j) instanceof MSubProcess)
							{
								getAllEdges((MSubProcess)acts.get(j), alledges);
							}
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
	public Map getAllActivities()
	{
		if(this.allactivities==null)
		{
			this.allactivities = new HashMap();
			
			List pools = getPools();
			if(pools!=null)
			{
				for(int i=0; i<pools.size(); i++)
				{
					MPool tmp = (MPool)pools.get(i);
					List acts = tmp.getActivities();
					if(acts!=null)
					{
						for(int j=0; j<acts.size(); j++)
						{
							MActivity mact = (MActivity)acts.get(j);
							allactivities.put(mact.getId(), acts.get(j));
							if(mact instanceof MSubProcess)
							{
								addAllSubActivities((MSubProcess)mact, allactivities);
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
	public void addAllSubActivities(MSubProcess proc, Map activities)
	{
		List acts = proc.getActivities();
		if(acts!=null)
		{
			for(int i=0; i<acts.size(); i++)
			{
				MActivity mact = (MActivity)acts.get(i);
				allactivities.put(mact.getId(), acts.get(i));
				if(mact instanceof MSubProcess)
				{
					addAllSubActivities((MSubProcess)mact, activities);
				}
			}
		}
		List	handlers	= proc.getEventHandlers();
		if(handlers!=null)
		{
			for(int i=0; i<handlers.size(); i++)
			{
				MActivity mact = (MActivity)handlers.get(i);
				allactivities.put(mact.getId(), handlers.get(i));
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
	protected void getAllEdges(MSubProcess sub, Map edges)
	{
		addEdges(sub.getSequenceEdges(), edges);
		
		List acts = sub.getActivities();
		if(acts!=null)
		{
			for(int j=0; j<acts.size(); j++)
			{
				if(acts.get(j) instanceof MSubProcess)
				{
					getAllEdges((MSubProcess)acts.get(j), edges);
				}
			}
		}
	}

	/**
	 *  Add edges to the result map.
	 *  @param tmp The list of edges.
	 *  @param edges The result map (id -> edge).
	 */
	protected void addEdges(List tmp, Map edges)
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
	public Map getAllAssociationTargets()
	{
		if(this.associationtargets==null)
		{
			this.associationtargets = new HashMap();
			
			// Add pools
			List pools = getPools();
			if(pools!=null)
			{
				for(int i=0; i<pools.size(); i++)
				{
					MPool pool = (MPool)pools.get(i);
					addAssociations(pool.getAssociationsDescription(), pool, associationtargets);
					
					// Add lanes
					List lanes = pool.getLanes();
					if(lanes!=null)
					{
						for(int j=0; j<lanes.size(); j++)
						{
							MLane lane = (MLane)lanes.get(j);
							addAssociations(lane.getAssociationsDescription(), lane, associationtargets);
						}
					}
					
					// Add activities
					List acts = pool.getActivities();
					if(acts!=null)
					{
						for(int j=0; j<acts.size(); j++)
						{
							MActivity act = (MActivity)acts.get(j);
							addActivityTargets(act);
						}
					}
				}
			}
			
			// Add edges
			Map edges = getAllSequenceEdges();
			for(Iterator it=edges.values().iterator(); it.hasNext(); )
			{
				MSequenceEdge edge = (MSequenceEdge)it.next();
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
			List acts = ((MSubProcess)act).getActivities();
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
	protected boolean addAssociations(String assosdesc, MIdElement target,  Map targets)
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
	public Map getAllAssociationSources()
	{
		if(this.associationsources==null)
		{
			this.associationsources = new HashMap();
		
			addArtifacts(getArtifacts(), associationsources);
		
			List pools = getPools();
			if(pools!=null)
			{
				for(int i=0; i<pools.size(); i++)
				{
					MPool pool = (MPool)pools.get(i);
					addArtifacts(pool.getArtifacts(), associationsources);
					
					// Search subprocesses
					List acts = pool.getActivities();
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
	protected void addSubProcesses(MSubProcess subproc, Map sources)
	{
		List artifacts = subproc.getArtifacts();
		addArtifacts(artifacts, sources);
		
		List acts = subproc.getActivities();
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
	 *  Add artifacts.
	 *  @param artifacts The list of artifacts.
	 *  @param sources The sources result map (association id -> art).
	 */
	protected MArtifact addArtifacts(List artifacts, Map sources)
	{
		MArtifact ret = null;
		
		if(artifacts!=null)
		{
			for(int i=0; i<artifacts.size() && ret==null; i++)
			{
				MArtifact art = (MArtifact)artifacts.get(i);
				List assos = art.getAssociations();
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
	public List<MActivity> getStartActivities(String poolname, String lanename)
	{
		List ret	= null;
		for(int i=0; pools!=null && i<pools.size(); i++)
		{
			MPool pool = (MPool)pools.get(i);
			if(poolname==null || poolname.equals(pool.getName()))
			{
				List tmp = pool.getStartActivities();
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
							ret = new ArrayList();
						}
						for(int j=0; j<tmp.size(); j++)
						{
							MActivity act = (MActivity)tmp.get(j);
							if(lanename.equals(act.getLane()))
							{
								ret.add(act);
							}
						}
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Add a pool/lane activation for a configurations.
	 *  @param config The configuration name.
	 *  @param poollane The poollane name (dot separated).
	 */
	public void addPoolLane(String config, String poollane)
	{
		if(configpoollanes==null)
			configpoollanes = new HashMap();
		configpoollanes.put(config, poollane);
	}
	
	/**
	 *  Get the pool lane.
	 *  @param config The configurations.
	 */
	public String getPoolLane(String config)
	{
		return configpoollanes==null? null: (String)configpoollanes.get(config);
	}
	
	/**
	 *  Removes a pool/lane activation for a configurations.
	 *  @param config The configuration name.
	 *  @return The poollane name (dot separated).
	 */
	public String removePoolLane(String config)
	{
		return configpoollanes != null? (String) configpoollanes.remove(config): null;
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
				if(activity.getIncomingSequenceEdges()==null || activity.getIncomingSequenceEdges().isEmpty())
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
		if(variabls==null)
			variabls	= new HashMap<String, MContextVariable>();
		
		variabls.put(variable.getName(), variable);
	}

	/**
	 *  Remove a context variable declaration.
	 *  @param name	The variable name.
	 */
	public void removeContextVariable(String name)
	{
		if(variabls!=null)
		{
			variabls.remove(name);
			
			if(variabls.isEmpty())
			{
				variabls	= null;
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
		return variabls!=null ? new ArrayList<MContextVariable>(variabls.values()) : Collections.EMPTY_LIST;
	}

	/**
	 *  Get a declared context variable.
	 *  @param name	The variable name.
	 *  @return The variable.
	 */
	public MContextVariable getContextVariable(String name)
	{
		return variabls != null? variabls.get(name) : null;
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
			variabls.put(exp.getName(), (MContextVariable) exp);
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
		// Todo: more in configuration than just pools/lanes?
		String[]	ret;
		List	pools	= getPools();
		if(pools!=null)
		{
			List	aret	= new ArrayList();
			if(pools.size()>1)
			{
				aret.add("All");
			}
			
			for(int i=0; i<pools.size(); i++)
			{
				MPool	pool	= (MPool)pools.get(i);
				aret.add(pool.getName());
				
				List	lanes	= pool.getLanes();
				if(lanes!=null)
				{
					for(int j=0; j<lanes.size(); j++)
					{
						MLane	lane	= (MLane)lanes.get(j);
						String	name	= lane.getName();
						while(lane.getLane()!=null)
						{
							lane	= lane.getLane();
							name	= lane.getName() + "." + name;
						}
						
						aret.add(pool.getName()+"."+name);
					}
					ret	= (String[])aret.toArray(new String[aret.size()]);
				}
			}			
			ret	= (String[])aret.toArray(new String[aret.size()]);
		}
		else
		{
			ret	= SUtil.EMPTY_STRING_ARRAY;
		}
		
		return ret;
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
		return classloader;
	}

	/**
	 *  Set the classloader.
	 *  @param classloader The classloader to set.
	 */
	public void setClassLoader(ClassLoader classloader)
	{
		this.classloader = classloader;
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
}
