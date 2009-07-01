package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MPool extends MNamedIdElement
{
	//-------- attributes --------
	
	/** The associations. */
	// protected List associations;
	
	/** The vertices. */
	protected List vertices;
	
	/** The sequence edges. */
	protected List sequenceedges;
	
	/** The pools. */
	protected List lanes;
	
	/** The messages. */
//	protected List messages;
	
	/** The type. */
	protected String type;
	
	//-------- constructors --------
	
	//-------- methods --------
	
	/**
	 * 
	 */
	public List getVertices()
	{
		return vertices;
	}
	
	/**
	 *  Get all start events of the pool.
	 *  @return A non-empty List of start events or null, if none.
	 */
	public List getStartEvents()
	{
		List	ret	= null;
		for(int i=0; vertices!=null && i<vertices.size(); i++)
		{
			MVertex	vertex	= (MVertex) vertices.get(i);
			// Todo: use constants
			// Todo: other start event types.
			if(vertex.getActivityType().equals("EventStartEmpty"))
			{
				if(ret==null)
					ret	= new ArrayList();
				ret.add(vertex);
			}
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public void addVertex(MVertex vertex)
	{
		if(vertices==null)
			vertices = new ArrayList();
		vertices.add(vertex);
	}
	
	/**
	 * 
	 */
	public void removeVertex(MVertex vertex)
	{
		if(vertices!=null)
			vertices.remove(vertex);
	}
	
	/**
	 * 
	 */
	public List getSequenceEdges()
	{
		return sequenceedges;
	}
	
	/**
	 * 
	 */
	public void addSequenceEdge(MSequenceEdge edge)
	{
		if(sequenceedges==null)
			sequenceedges = new ArrayList();
		sequenceedges.add(edge);
	}
	
	/**
	 * 
	 */
	public void removeSequenceEdge(MSequenceEdge edge)
	{
		if(sequenceedges!=null)
			sequenceedges.remove(edge);
	}
	
	/**
	 * 
	 */
	public List getLanes()
	{
		return lanes;
	}
	
	/**
	 * 
	 */
	public void addLane(MLane lane)
	{
		if(lanes==null)
			lanes = new ArrayList();
		lanes.add(lane);
	}
	
	/**
	 * 
	 */
	public void removeLane(MLane lane)
	{
		if(lanes!=null)
			lanes.remove(lane);
	}
	
	/**
	 * @return the type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}
}
