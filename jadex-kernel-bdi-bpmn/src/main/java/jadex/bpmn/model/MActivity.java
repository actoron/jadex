package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

import jadex.commons.SReflect;

/**
 * 
 */
public class MActivity extends MNamedIdElement
{
	//-------- attributes --------
	
	/** The lane description. */
	protected String lanedescription;

	/** The associations description. */
	protected String associationsdescription;

	
	/** The outgoing edges. */
	protected String outgoingedges;
	
	/** The incoming edges. */
	protected String incomingedges;
	
	/** The outgoing edges. */
	protected List outedges;
	
	/** The incoming edges. */
	protected List inedges;
		
	/** The type. */
	protected String type;
	
	/** The activity type. */
	protected String activitytype;
		
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
	 * @return the lanedescription
	 */
	public String getLaneDescription()
	{
		return this.lanedescription;
	}

	/**
	 * @param lanedescription the lanedescription to set
	 */
	public void setLaneDescription(String lanedescription)
	{
		this.lanedescription = lanedescription;
	}
	
	/**
	 * @return the associationsdescription
	 */
	public String getAssociationsDescription()
	{
		return this.associationsdescription;
	}

	/**
	 * @param associationsdescription the associationsdescription to set
	 */
	public void setAssociationsDescription(String associationsdescription)
	{
		this.associationsdescription = associationsdescription;
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
		buf.append(", activityType=");
		buf.append(getActivityType());
		buf.append(")");
		return buf.toString();
	}
}
