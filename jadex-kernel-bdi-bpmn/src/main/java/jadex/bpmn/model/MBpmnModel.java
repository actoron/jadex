package jadex.bpmn.model;

import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.xml.BeanAttributeInfo;
import jadex.commons.xml.IPostProcessor;
import jadex.commons.xml.LinkInfo;
import jadex.commons.xml.TypeInfo;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *  Java representation of a bpmn model for xml description.
 */
public class MBpmnModel extends MIdElement
{
	//-------- attributes --------
	
	/** The pools. */
	protected List pools;
	
	/** The artifacts. */
	protected List artifacts;
	
	/** The messages. */
	protected List messages;
	
	/** The name of the model. */
	protected String name;
	
	//-------- init structures --------
	
	/** The cached edges of the model. */
	protected Map alledges;

	/** The association sources. */
	protected Map associationsources;
	
	/** The association targets. */
	protected Map associationtargets;
	
	/** The messaging edges. */
	protected Map allmessagingedges;
	
	//-------- added structures --------
	
	/** The package. */
	protected String packagename;
	
	/** The imports. */
	protected String[] imports;
	
	//-------- methods --------

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
	protected Map getAllAssociationTargets()
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
	protected Map getAllAssociationSources()
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
		return name;
	}
	
	/**
	 *  Set the name of the model.
	 *  @param name	The name to set.
	 */
	public void	setName(String name)
	{
		this.name	= name;
	}
	
	/**
	 *  Get all start activities of the model.
	 *  @return A non-empty List of start activities or null, if none.
	 */
	public List getStartActivities()
	{
		List	ret	= null;
		for(int i=0; pools!=null && i<pools.size(); i++)
		{
			MPool	pool	= (MPool) pools.get(i);
			List	tmp	= pool.getStartActivities();
			if(tmp!=null)
			{
				if(ret!=null)
					ret.addAll(tmp);
				else
					ret	= tmp;
			}
		}
		
		return ret;
	}

	/**
	 *  Get all imports.
	 *  @return The imports.
	 */
	public String[] getAllImports()
	{
		return imports;
	}
	
	/**
	 *  Set the imports.
	 *  @param imports The imports.
	 */
	public void setImports(String[] imports)
	{
		this.imports = imports;
	}
	
	/**
	 *  Get the package name.
	 *  @return The package name.
	 */
	public String getPackage()
	{
		return packagename;
	}
	
	/**
	 *  Set the package name.
	 *  @param packagename The package name to set.
	 */
	public void setPackage(String packagename)
	{
		this.packagename = packagename;
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
	 *  Get the XML mapping.
	 */
	public static Set getXMLMapping()
	{
		Set types = new HashSet();
		
		types.add(new TypeInfo("BpmnDiagram", MBpmnModel.class, null, null, 
			null, new BpmnModelPostProcessor()));
		
		types.add(new TypeInfo("pools", MPool.class, null, null,
			SUtil.createHashMap(new String[]{"name", "associations"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("description"),
			new BeanAttributeInfo("associationsDescription")}), new NamePropertyPostProcessor()));
		
		types.add(new TypeInfo("artifacts", MArtifact.class, null, null,
			SUtil.createHashMap(new String[]{"name"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("description")}), null));
		
		types.add(new TypeInfo("associations", MAssociation.class, null, null, 
			null, new AssociationPostProcessor()));
		
		types.add(new TypeInfo("lanes", MLane.class, null, null,
			SUtil.createHashMap(new String[]{"name", "activities"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("description"),
			new BeanAttributeInfo("activitiesDescription")}), new NamePropertyPostProcessor()));
		
		types.add(new TypeInfo("eventHandlers", MActivity.class, null, null,
			SUtil.createHashMap(new String[]{"outgoingEdges", "incomingEdges"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("outgoingSequenceEdgesDescription"),
			new BeanAttributeInfo("incomingSequenceEdgesDescription")}), new ActivityPostProcessor()));
		
		types.add(new TypeInfo("vertices", MActivity.class, null, null,
			SUtil.createHashMap(new String[]{"name", "outgoingEdges", "incomingEdges", "lanes", "associations", "activityType"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("description"),
			new BeanAttributeInfo("outgoingSequenceEdgesDescription"),
			new BeanAttributeInfo("incomingSequenceEdgesDescription"),
			new BeanAttributeInfo("laneDescription"),
			new BeanAttributeInfo("associationsDescription"),
			new BeanAttributeInfo("activityType", null, null, "Task")}),
			new ActivityPostProcessor(),
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("Activity");
				}
			}));
		
		types.add(new TypeInfo("vertices", MSubProcess.class, null, null,
			SUtil.createHashMap(new String[]{"name", "outgoingEdges", "incomingEdges", "lanes", "associations"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("description"),
			new BeanAttributeInfo("outgoingSequenceEdgesDescription"),
			new BeanAttributeInfo("incomingSequenceEdgesDescription"),
			new BeanAttributeInfo("laneDescription"),
			new BeanAttributeInfo("associationsDescription")}), new ActivityPostProcessor(),
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("SubProcess");
				}
			}));
		
		types.add(new TypeInfo("sequenceEdges", MSequenceEdge.class, null, null,
			SUtil.createHashMap(new String[]{"name", "associations"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("description"), 
			new BeanAttributeInfo("associationsDescription")}), new SequenceEdgePostProcessor()));
		
		types.add(new TypeInfo("messagingEdges", MMessagingEdge.class, null, null,
			SUtil.createHashMap(new String[]{"name", "associations"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("description"), 
			new BeanAttributeInfo("associationsDescription")}), null));
		
		types.add(new TypeInfo("incomingMessages", HashMap.class, null, null, 
			SUtil.createHashMap(new String[]{"type", "href"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, "")}), null));

		types.add(new TypeInfo("outgoingMessages", HashMap.class, null, null, 
			SUtil.createHashMap(new String[]{"type", "href"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, "")}), null));
		
		types.add(new TypeInfo("messages", MMessagingEdge.class, null, null, 
			SUtil.createHashMap(new String[]{"source", "target"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("sourceDescription"),
			new BeanAttributeInfo("targetDescription")}), null));
		
		return types;
	}
	
	/**
	 *  Get the XML link infos.
	 */
	public static Set getXMLLinkInfos()
	{
		Set linkinfos = new HashSet();

		// bpmn diagram
		linkinfos.add(new LinkInfo("pools", new BeanAttributeInfo("pool")));
		linkinfos.add(new LinkInfo("artifacts", new BeanAttributeInfo("artifact")));
		linkinfos.add(new LinkInfo("messages", new BeanAttributeInfo("messagingEdge")));

		// pool
		linkinfos.add(new LinkInfo("vertices", new BeanAttributeInfo("activity")));
		linkinfos.add(new LinkInfo("sequenceEdges", new BeanAttributeInfo("sequenceEdge")));
		linkinfos.add(new LinkInfo("lanes", new BeanAttributeInfo("lane")));
		
		// activities
		linkinfos.add(new LinkInfo("incomingMessages", new BeanAttributeInfo("incomingMessageDescription")));
		linkinfos.add(new LinkInfo("outgoingMessages", new BeanAttributeInfo("outgoingMessageDescription")));
		
		// subprocesses
		linkinfos.add(new LinkInfo("eventHandlers", new BeanAttributeInfo("eventHandler")));

		// artifacts
		linkinfos.add(new LinkInfo("associations", new BeanAttributeInfo("association")));
		
		return linkinfos;
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
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			MBpmnModel dia = (MBpmnModel)root;
			MActivity act = (MActivity)object;

			// Make edge connections.
			Map edges = dia.getAllSequenceEdges();
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
				}
			}
			
			if(act.getDescription()!=null)
			{
				// first line: name
				// lines with = in it: properties
				// lines starting with in/out/inout: parameters
				
				StringTokenizer	stok = new StringTokenizer(act.getDescription(), "\r\n");
				JavaCCExpressionParser parser = new JavaCCExpressionParser();
				
				while(stok.hasMoreTokens())
				{
					String prop = stok.nextToken().trim();
					int	idx	= prop.indexOf("=");
					if(prop.startsWith("in") || prop.startsWith("out"))
					{
						// parameter
						StringTokenizer stok2 = new StringTokenizer(prop, " =");
						String paramdir = stok2.nextToken();
						String paramclazzname = stok2.nextToken();
						Class paramclazz = paramclazzname==null? null: SReflect.findClass0(paramclazzname, dia.getAllImports(), classloader);
						String paramname = stok2.nextToken();
						IParsedExpression paramexp = null;
						if(stok2.hasMoreTokens())
						{
							String proptext = prop.substring(idx+1).trim();
							paramexp = parser.parseExpression(proptext, dia.getAllImports(), null, classloader);
						}
						MParameter param = new MParameter(paramdir, paramclazz, paramname, paramexp);
						act.addParameter(param);
					}
					else if(idx!=-1)
					{
						// property
						String propname = prop.substring(0, idx).trim();
						String proptext = prop.substring(idx+1).trim();
						Object propval = parser.parseExpression(proptext, dia.getAllImports(), null, classloader).getValue(null);
						act.setPropertyValue(propname, propval);
					}
					else
					{
						// line without "=" is name
						act.setName(prop);
					}
				}
			}
		}
		
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 3;
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
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			MBpmnModel dia = (MBpmnModel)root;
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
		}
	
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 3;
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
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			MBpmnModel dia = (MBpmnModel)root;
			MSequenceEdge edge = (MSequenceEdge)object;
			JavaCCExpressionParser parser = new JavaCCExpressionParser();

			if(edge.getDescription()!=null)
			{
				// first line: name
				// second line: condition
				// lines with = in it: parameters
				
				StringTokenizer	stok = new StringTokenizer(edge.getDescription(), "\r\n");
				String lineone = null;
				String linetwo = null;
				while(stok.hasMoreTokens())
				{
					
					String prop = stok.nextToken();
					int	idx	= prop.indexOf("=");
					if(idx!=-1)
					{
						String	propname = prop.substring(0, idx).trim();
						String	proptext = prop.substring(idx+1).trim();
						IParsedExpression exp = parser.parseExpression(proptext, dia.getAllImports(), null, classloader);
						edge.addParameterMapping(propname, exp);
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
					IParsedExpression cond = parser.parseExpression(linetwo, dia.getAllImports(), null, classloader);
					edge.setCondition(cond);
				}
				else if(lineone!=null)
				{
					IParsedExpression cond = parser.parseExpression(lineone, dia.getAllImports(), null, classloader);
					edge.setCondition(cond);
				}
			}
		}
		
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 3;
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
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			MBpmnModel dia = (MBpmnModel)root;
			MNamedIdElement namedelem = (MNamedIdElement)object;
			JavaCCExpressionParser parser = new JavaCCExpressionParser();

			if(namedelem.getDescription()!=null)
			{
				// first line: name
				// lines with = in it: properties
				
				StringTokenizer	stok = new StringTokenizer(namedelem.getDescription(), "\r\n");
				String name = null;
				while(stok.hasMoreTokens())
				{
					String prop = stok.nextToken();
					int	idx	= prop.indexOf("=");
					if(idx!=-1)
					{
						String propname = prop.substring(0, idx).trim();
						String proptext = prop.substring(idx+1).trim();
						Object propval = parser.parseExpression(proptext, dia.getAllImports(), null, classloader).getValue(null);
						namedelem.setPropertyValue(propname, propval);
					}
					else
					{
						// line without "=" is name
						namedelem.setName(name);
					}
				}
			}
		}
		
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 3;
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
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			MBpmnModel model = (MBpmnModel)root;

			List arts = model.getArtifacts();
			if(arts!=null)
			{
				if(arts.size()>1)
					throw new RuntimeException("Diagram must have one artifact for imports/package");
				
				String desc = ((MArtifact)arts.get(0)).getDescription();
				StringTokenizer	stok = new StringTokenizer(desc, "\r\n");
				List imports = new ArrayList();
				while(stok.hasMoreTokens())
				{
					String	prop	= stok.nextToken().trim();
					if(prop.startsWith("package"))
					{
						String packagename = prop.substring(prop.indexOf("package")+8).trim();
						if(packagename.endsWith(";"))
							packagename = packagename.substring(0, packagename.length()-1);
						model.setPackage(packagename);
					}
					else if(prop.startsWith("import"))
					{
						String imp = prop.substring(prop.indexOf("imports")+7).trim();
						if(imp.endsWith(";"))
							imp = imp.substring(0, imp.length()-1);
						imports.add(imp);
					}
				}
				if(model.getPackage()!=null)
					imports.add(model.getPackage()+".*");
				model.setImports((String[])imports.toArray(new String[imports.size()]));
			}
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
	 *  Get all start activities form the supplied set of activities.
	 *  Start activities are those without incoming edges. 
	 *  @return A non-empty List of start activities or null, if none.
	 */
	public static List	getStartActivities(List activities)
	{
		List	ret	= null;
		for(Iterator it=activities.iterator(); it.hasNext(); )
		{
			MActivity	activity	= (MActivity) it.next();
			if(activity.getIncomingSequenceEdges()==null || activity.getIncomingSequenceEdges().isEmpty())
			{
				if(ret==null)
				{
					ret	= new ArrayList();
				}
				ret.add(activity);
			}
		}
		
		return ret;
	}
}
