package jadex.bpmn.model;

import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.xml.BeanAttributeInfo;
import jadex.commons.xml.IBeanObjectCreator;
import jadex.commons.xml.IPostProcessor;
import jadex.commons.xml.LinkInfo;
import jadex.commons.xml.TypeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *  Java representation of a bpmn model for xml description.
 */
public class MBpmnDiagram extends MIdElement
{
	//-------- attributes --------
	
	/** The pools. */
	protected List pools;
	
	/** The artifacts. */
	protected List artifacts;
	
	/** The messages. */
//	protected List messages;
	
	/** The cached edges of the model. */
	protected Map alledges;
	
	/** The name of the model. */
	protected String	name;
	
	//-------- methods --------

	/**
	 * 
	 */
	public List getPools()
	{
		return pools;
	}
	
	/**
	 * 
	 */
	public void addPool(MPool pool)
	{
		if(pools==null)
			pools = new ArrayList();
		pools.add(pool);
	}
	
	/**
	 * 
	 */
	public void removePool(MPool pool)
	{
		if(pools!=null)
			pools.remove(pool);
	}
	
	/**
	 * 
	 */
	public List getArtifacts()
	{
		return artifacts;
	}
	
	/**
	 * 
	 */
	public void addArtifact(MArtifact artifact)
	{
		if(artifacts==null)
			artifacts = new ArrayList();
		artifacts.add(artifact);
	}
	
	/**
	 * 
	 */
	public void removePool(MArtifact artifact)
	{
		if(artifacts!=null)
			artifacts.remove(artifact);
	}
	
	//-------- helper methods --------
	
	/**
	 * 
	 */
	public Map getAllEdges()
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
	 * 
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
	 * 
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
	
//	/**
//	 *  Get a string representation of this AGR space type.
//	 *  @return A string representation of this AGR space type.
//	 */
//	public String	toString()
//	{
//		StringBuffer	sbuf	= new StringBuffer();
//		sbuf.append(SReflect.getInnerClassName(getClass()));
//		sbuf.append("(name=");
//		sbuf.append(getName());
//		sbuf.append(", dimensions=");
//		sbuf.append(getDimensions());
//		sbuf.append(", agent action types=");
//		sbuf.append(getMEnvAgentActionTypes());
//		sbuf.append(", space action types=");
//		sbuf.append(getMEnvSpaceActionTypes());
//		sbuf.append(", class=");
//		sbuf.append(getClazz());
//		sbuf.append(")");
//		return sbuf.toString();
//	}
	
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
	 *  Get all start events of the model.
	 *  @return A non-empty List of start events or null, if none.
	 */
	public List getStartEvents()
	{
		List	ret	= null;
		for(int i=0; pools!=null && i<pools.size(); i++)
		{
			MPool	pool	= (MPool) pools.get(i);
			List	tmp	= pool.getStartEvents();
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
		
		types.add(new TypeInfo("BpmnDiagram", MBpmnDiagram.class));
		
		types.add(new TypeInfo("pools", MPool.class, null, null,
			SUtil.createHashMap(new String[]{"associations"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("associationsDescription")}), null));
		
		types.add(new TypeInfo("artifacts", MArtifact.class));
		
		types.add(new TypeInfo("associations", MAssociation.class, null, null, 
			null, new AssociationPostProcessor()));
		
		types.add(new TypeInfo("lanes", MLane.class, null, null,
			SUtil.createHashMap(new String[]{"activities"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("activitiesDescription")}), null));
		
		types.add(new TypeInfo("eventHandlers", MActivity.class, null, null,
			SUtil.createHashMap(new String[]{"outgoingEdges", "incomingEdges"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("outgoingEdgesDescription"),
			new BeanAttributeInfo("incomingEdgesDescription")}), new VertexPostProcessor()));
		
		types.add(new TypeInfo("vertices", MActivity.class, null, null,
			SUtil.createHashMap(new String[]{"outgoingEdges", "incomingEdges", "lanes", "associations"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("outgoingEdgesDescription"),
			new BeanAttributeInfo("incomingEdgesDescription"),
			new BeanAttributeInfo("laneDescription"),
			new BeanAttributeInfo("associationsDescription")}), new VertexPostProcessor(),
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("Activity");
				}
			}));
		
		types.add(new TypeInfo("vertices", MSubProcess.class, null, null,
			SUtil.createHashMap(new String[]{"outgoingEdges", "incomingEdges", "lanes", "associations"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("outgoingEdgesDescription"),
			new BeanAttributeInfo("incomingEdgesDescription"),
			new BeanAttributeInfo("laneDescription"),
			new BeanAttributeInfo("associationsDescription")}), new VertexPostProcessor(),
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("SubProcess");
				}
			}));
		
		types.add(new TypeInfo("sequenceEdges", MSequenceEdge.class));
		
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

		// pool
		linkinfos.add(new LinkInfo("vertices", new BeanAttributeInfo("activity")));
		linkinfos.add(new LinkInfo("sequenceEdges", new BeanAttributeInfo("sequenceEdge")));
		linkinfos.add(new LinkInfo("lanes", new BeanAttributeInfo("lane")));
		
		// subprocesses
		linkinfos.add(new LinkInfo("eventHandlers", new BeanAttributeInfo("eventHandler")));

		// artifacts
		linkinfos.add(new LinkInfo("associations", new BeanAttributeInfo("association")));
		
		return linkinfos;
	}
	
	/**
	 *  Vertex post processor.
	 */
	static class VertexPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Load class.
		 */
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			MBpmnDiagram dia = (MBpmnDiagram)root;
			MActivity act = (MActivity)object;
			Map edges = dia.getAllEdges();
			
			String indesc = act.getIncomingEdgesDescription();
			if(indesc!=null)
			{
				StringTokenizer stok = new StringTokenizer(indesc);
				while(stok.hasMoreElements())
				{
					String edgeid = stok.nextToken(); 
					MSequenceEdge edge = (MSequenceEdge)edges.get(edgeid);
					act.addIncomingEdge(edge);
					edge.setTarget(act);
				}
			}
			
			String outdesc = act.getOutgoingEdgesDescription();
			if(outdesc!=null)
			{
				StringTokenizer stok = new StringTokenizer(outdesc);
				while(stok.hasMoreElements())
				{
					String edgeid = stok.nextToken(); 
					MSequenceEdge edge = (MSequenceEdge)edges.get(edgeid);
					act.addOutgoingEdge(edge);
					edge.setSource(act);
				}
			}
		}
		
		/**
		 *  Test if this post processor can be executed in first pass.
		 *  @return True if can be executed on first pass.
		 */
		public boolean isFirstPass()
		{
			return false;
		}
	}
	
	/**
	 *  Association post processor.
	 */
	static class AssociationPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Load class.
		 */
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			MBpmnDiagram dia = (MBpmnDiagram)root;
			MAssociation asso = (MAssociation)object;
			
//			MIdElement source = findSource(asso);
//			MIdElement target = findTarget(asso);
//			
//			asso.setSource(source);
//			asso.setTarget(target);
		}
		
		/**
		 *  Test if this post processor can be executed in first pass.
		 *  @return True if can be executed on first pass.
		 */
		public boolean isFirstPass()
		{
			return false;
		}
	}

}
