package jadex.bpmn.model;

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
	
	/** The messages. */
//	protected List messages;
	
	/** The name of the model. */
	protected String	name;
	
	//-------- constructors --------
	
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
		
		types.add(new TypeInfo("pools", MPool.class));
		
		IBeanObjectCreator oc = new IBeanObjectCreator()
		{
			public Object createObject(Object context, Map rawattributes,
				ClassLoader classloader) throws Exception
			{
				Object ret = null;
				String type = (String)rawattributes.get("type");
				if(type.endsWith("Activity"))
				{
					ret = new MActivity();
				}
//				else if(type.endsWith("SubProcess"))
//				{
//					ret = new MSubProcess();
//				}
				else
				{
					throw new RuntimeException("Unknown vertex type: "+type);
				}
				
				return ret;
			}
		};
		
		types.add(new TypeInfo("vertices", oc, null, null,
			SUtil.createHashMap(new String[]{"outgoingEdges", "incomingEdges"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("outgoingEdgesDescription"),
			new BeanAttributeInfo("incomingEdgesDescription")}), new VertexPostProcessor()));
		
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

		// pool
		linkinfos.add(new LinkInfo("vertices", new BeanAttributeInfo("vertex")));
		linkinfos.add(new LinkInfo("sequenceEdges", new BeanAttributeInfo("sequenceEdge")));
		
		return linkinfos;
	}
	
	/**
	 *  Get the XML mapping.
	 * /
	public static Set getXMLMapping()
	{
		Set types = new HashSet();
		
		types.add(new TypeInfo("BpmnDiagram", MBpmnDiagram.class, null, null,
			SUtil.createHashMap(new String[]{"id", "version"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("", null, "property"),
			new BeanAttributeInfo("", null, "property")
			}), null));
		
		types.add(new TypeInfo("pools", MultiCollection.class, null, null,
			SUtil.createHashMap(new String[]{"id", "name", "type"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, "")}), null));
		
		types.add(new TypeInfo("vertices", MultiCollection.class, null, null,
			SUtil.createHashMap(new String[]{"id", "name", "activityType", "type", "outgoingEdges", "incomingEdges"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, "")}), null));
		
		types.add(new TypeInfo("sequenceEdges", MultiCollection.class, null, null,
			SUtil.createHashMap(new String[]{"id", "name", "type"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, "")
			}), null));
		
		
		return types;
	}*/
	
	/**
	 *  Get the XML link infos.
	 * /
	public static Set getXMLLinkInfos()
	{
		Set linkinfos = new HashSet();

		// bpmn diagram
		linkinfos.add(new LinkInfo("pools", new BeanAttributeInfo("pools", null, "property")));

		// pool
		linkinfos.add(new LinkInfo("vertices", new BeanAttributeInfo("vertices", null, "")));
		linkinfos.add(new LinkInfo("sequenceEdges", new BeanAttributeInfo("vertices", null, "")));
		
		return linkinfos;
	}*/
	
	/**
	 *  Load class.
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
			MVertex v = (MVertex)object;

			// Search container of vertex
			// todo: hierarchical search in nested subprocesses and also in lanes of pools?!
			MPool container = null;
			List pools = dia.getPools();
			if(pools!=null)
			{
				for(int i=0; container==null && i<pools.size(); i++)
				{
					MPool tmp = (MPool)pools.get(i);
					if(tmp.getVertices().contains(v))
						container = tmp;
				}
			}
			if(container==null)
				throw new RuntimeException("Vertex container not found: "+v);
			List tmp = container.getSequenceEdges();
			Map edges = new HashMap();
			for(int i=0; i<tmp.size(); i++)
			{
				MSequenceEdge edge = (MSequenceEdge)tmp.get(i);
				edges.put(edge.getId(), edge);
			}
			
			String indesc = v.getIncomingEdgesDescription();
			if(indesc!=null)
			{
				StringTokenizer stok = new StringTokenizer(indesc);
				while(stok.hasMoreElements())
				{
					String edgeid = stok.nextToken(); 
					MSequenceEdge edge = (MSequenceEdge)edges.get(edgeid);
					v.addIncomingEdge(edge);
					edge.setTarget(v);
				}
			}
			
			String outdesc = v.getOutgoingEdgesDescription();
			if(outdesc!=null)
			{
				StringTokenizer stok = new StringTokenizer(outdesc);
				while(stok.hasMoreElements())
				{
					String edgeid = stok.nextToken(); 
					MSequenceEdge edge = (MSequenceEdge)edges.get(edgeid);
					v.addOutgoingEdge(edge);
					edge.setSource(v);
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

}
