package jadex.gpmn.model;

import jadex.bpmn.model.MNamedIdElement;
import jadex.commons.SReflect;

import java.util.ArrayList;
import java.util.List;

/**
 *  Base class for all kinds of goals.
 */
public class MProcessElement extends MNamedIdElement
{
	//-------- attributes --------
	
	/** The outgoing sequence edges descriptions. */
	protected List outgoingsequenceedgesdescriptions;
	
	/** The incoming sequence edges descriptions. */
	protected List incomingsequenceedgesdescriptions;
	
	
	/** The outgoing sequence edges. */
	protected List outseqedges;
	
	/** The incoming sequence edges. */
	protected List inseqedges;

	/** The type. */
	protected String type;
	
	//-------- methods --------
	
	/**
	 *  Get the xml outgoing sequence edges desription.
	 *  @return The outgoing sequence edges description.
	 */
	public List getOutgoingSequenceEdgesDescriptions()
	{
		return this.outgoingsequenceedgesdescriptions;
	}

	/**
	 *  Set the xml outgoing edges desription.
	 *  @param outgoingedges The outgoing edges to set.
	 */
	public void addOutgoingSequenceEdgesDescription(String outgoingedges)
	{
		if(outgoingsequenceedgesdescriptions==null)
			outgoingsequenceedgesdescriptions = new ArrayList();
		outgoingsequenceedgesdescriptions.add(outgoingedges);
	}
	
	/**
	 *  Get the xml incoming edges description.
	 *  @return The incoming edges description.
	 */
	public List getIncomingSequenceEdgesDescriptions()
	{
		return this.incomingsequenceedgesdescriptions;
	}

	/**
	 *  Set the xml incoming edges description.
	 *  @param incomingedges The incoming edges to set.
	 */
	public void addIncomingSequenceEdgesDescription(String incomingedge)
	{
		if(incomingsequenceedgesdescriptions==null)
			incomingsequenceedgesdescriptions = new ArrayList();
		incomingsequenceedgesdescriptions.add(incomingedge);
	}
	
	/**
	 *  Get the outgoing sequence edges.
	 *  @return The outgoing edges.
	 */
	public List getOutgoingSequenceEdges()
	{
		return outseqedges;
	}

	/**
	 *  Add an outgoing edge.
	 *  @param edge The edge.
	 */
	public void addOutgoingSequenceEdge(MSequenceEdge edge)
	{
		if(outseqedges==null)
			outseqedges = new ArrayList();
		outseqedges.add(edge);
	}
	
	/**
	 *  Remove an outgoing edge.
	 *  @param edge The edge.
	 */
	public void removeOutgoingSequenceEdge(MSequenceEdge edge)
	{
		if(outseqedges!=null)
			outseqedges.remove(edge);
	}
	
	/**
	 *  Get the incoming edges.
	 *  @return The incoming edges.
	 */
	public List getIncomingSequenceEdges()
	{
		return inseqedges;
	}
	
	/**
	 *  Add an incoming edge.
	 *  @param edge The edge.
	 */
	public void addIncomingSequenceEdge(MSequenceEdge edge)
	{
		if(inseqedges==null)
			inseqedges = new ArrayList();
		inseqedges.add(edge);
	}
	
	/**
	 *  Remove an incoming edge.
	 *  @param edge The edge.
	 */
	public void removeIncomingSequenceEdge(MSequenceEdge edge)
	{
		if(inseqedges!=null)
			inseqedges.remove(edge);
	}
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Create a string representation of this activity.
	 *  @return A string representation of this activity.
	 */
	public String	toString()
	{		
		StringBuffer buf = new StringBuffer();
		buf.append(SReflect.getInnerClassName(this.getClass()));
		buf.append("(name=");
		buf.append(getName());
		buf.append(", type=");
		buf.append(getType());
		buf.append(")");
		return buf.toString();
	}
}
