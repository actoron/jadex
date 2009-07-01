package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MSubProcess extends MActivity
{
	//-------- attributes --------
	
	/** The vertices. */
	protected List vertices;
	
	/** The sequence edges. */
	protected List sequenceedges;
	
	/** The event handlers. */
	protected List eventhandlers;
	
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
	public List getEventHandlers()
	{
		return eventhandlers;
	}
	
	/**
	 * 
	 */
	public void addEventHandler(MActivity eventhandler)
	{
		if(eventhandlers==null)
			eventhandlers = new ArrayList();
		eventhandlers.add(eventhandler);
	}
	
	/**
	 * 
	 */
	public void removeEventHandler(MActivity eventhandler)
	{
		if(eventhandlers!=null)
			eventhandlers.remove(eventhandler);
	}
}
