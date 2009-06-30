package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MVertex extends MNamedIdElement
{
	//-------- attributes --------
	
	/** The outgoing edges. */
	protected String outgoingedges;
	
	/** The incoming edges. */
	protected String incomingedges;
	
	/** The outgoing edges. */
	protected List outedges;
	
	/** The incoming edges. */
	protected List inedges;
	
	/** The activity type. */
	protected String activitytype;
	
	/** The type. */
	protected String type;
	
	//-------- constructors --------
	
	//-------- methods --------

	/**
	 * @return the outgoingedges
	 */
	public String getOutgoingEdgesDescription()
	{
		return this.outgoingedges;
	}

	/**
	 * @param outgoingedges the outgoingedges to set
	 */
	public void setOutgoingEdgesDescription(String outgoingedges)
	{
		this.outgoingedges = outgoingedges;
	}
	
	/**
	 * @return the incomingedges
	 */
	public String getIncomingEdgesDescription()
	{
		return this.incomingedges;
	}

	/**
	 * @param incomingedges the incomingedges to set
	 */
	public void setIncomingEdgesDescription(String incomingedges)
	{
		this.incomingedges = incomingedges;
	}
	
	/**
	 * 
	 */
	public List getOutgoingEdges()
	{
		return outedges;
	}
	
	/**
	 * @return the incomingedges
	 */
	public String getIncomingedges()
	{
		return this.incomingedges;
	}

	/**
	 * @param incomingedges the incomingedges to set
	 */
	public void setIncomingedges(String incomingedges)
	{
		this.incomingedges = incomingedges;
	}

	/**
	 * 
	 */
	public void addOutgoingEdge(MSequenceEdge edge)
	{
		if(outedges==null)
			outedges = new ArrayList();
		outedges.add(edge);
	}
	
	/**
	 * 
	 */
	public void removeOutgoingEdge(MSequenceEdge edge)
	{
		if(outedges!=null)
			outedges.remove(edge);
	}
	
	/**
	 * 
	 */
	public List getIncomingEdges()
	{
		return inedges;
	}
	
	/**
	 * 
	 */
	public void addIncomingEdge(MSequenceEdge edge)
	{
		if(inedges==null)
			inedges = new ArrayList();
		inedges.add(edge);
	}
	
	/**
	 * 
	 */
	public void removeIncomingEdge(MSequenceEdge edge)
	{
		if(inedges!=null)
			inedges.remove(edge);
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

	/**
	 * @return the activitytype
	 */
	public String getActivityType()
	{
		return this.activitytype;
	}

	/**
	 * @param activitytype the activitytype to set
	 */
	public void setActivityType(String activitytype)
	{
		this.activitytype = activitytype;
	}
	
}
