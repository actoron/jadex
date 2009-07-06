package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import jadex.commons.SReflect;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

/**
 * 
 */
public class MActivity extends MNamedIdElement implements IAssociationTarget
{
	//-------- attributes --------
	
	/** The lane description. */
	protected String lanedescription;

	/** The associations description. */
	protected String associationsdescription;

	/** The outgoing edges description. */
	protected String outgoingedgesdescription;
	
	/** The incoming edges description. */
	protected String incomingedgesdescription;
	
	/** The incoming messages description. */
	protected List incomingmessagesdescriptions;
	
	/** The outgoing messages description. */
	protected List outgoingmessagesdescriptions;

	
	/** The outgoing edges. */
	protected List outedges;
	
	/** The incoming edges. */
	protected List inedges;

	/** The outgoing message edges. */
	protected List outmsgedges;
	
	/** The incoming message edges. */
	protected List inmsgedges;
	
	/** The associations. */
	protected List associations;
		
	/** The type. */
	protected String type;
	
	/** The activity type. */
	protected String activitytype;

	/** The looping flag. */
	protected boolean looping;
	
	/** The properties. */
	protected Map properties;
	
	//-------- methods --------
	
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
	 * @return the outgoingedges
	 */
	public String getOutgoingEdgesDescription()
	{
		return this.outgoingedgesdescription;
	}

	/**
	 * @param outgoingedges the outgoingedges to set
	 */
	public void setOutgoingEdgesDescription(String outgoingedges)
	{
		this.outgoingedgesdescription = outgoingedges;
	}
	
	/**
	 * @return the incomingedges
	 */
	public String getIncomingEdgesDescription()
	{
		return this.incomingedgesdescription;
	}

	/**
	 * @param incomingedges the incomingedges to set
	 */
	public void setIncomingEdgesDescription(String incomingedges)
	{
		this.incomingedgesdescription = incomingedges;
	}
	
	/**
	 * 
	 */
	public List getOutgoingMessagesDescriptions()
	{
		return outgoingmessagesdescriptions;
	}

	/**
	 * 
	 */
	public void addOutgoingMessageDescription(Object desc)
	{
		if(outgoingmessagesdescriptions==null)
			outgoingmessagesdescriptions = new ArrayList();
		outgoingmessagesdescriptions.add(desc);
	}
	
	/**
	 * 
	 */
	public void removeOutgoingMessageDescription(Object desc)
	{
		if(outgoingmessagesdescriptions!=null)
			outgoingmessagesdescriptions.remove(desc);
	}
	
	/**
	 * 
	 */
	public List getIncomingMessagesDescriptions()
	{
		return incomingmessagesdescriptions;
	}

	/**
	 * 
	 */
	public void addIncomingMessageDescription(Object desc)
	{
		if(incomingmessagesdescriptions==null)
			incomingmessagesdescriptions = new ArrayList();
		incomingmessagesdescriptions.add(desc);
	}
	
	/**
	 * 
	 */
	public void removeIncomingMessageDescription(Object desc)
	{
		if(incomingmessagesdescriptions!=null)
			incomingmessagesdescriptions.remove(desc);
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
	 * 
	 */
	public List getOutgoingMessagingEdges()
	{
		return outmsgedges;
	}

	/**
	 * 
	 */
	public void addOutgoingMessagingEdge(MMessagingEdge edge)
	{
		if(outmsgedges==null)
			outmsgedges = new ArrayList();
		outmsgedges.add(edge);
	}
	
	/**
	 * 
	 */
	public void removeOutgoingMessagingEdge(MMessagingEdge edge)
	{
		if(outmsgedges!=null)
			outmsgedges.remove(edge);
	}
	
	/**
	 * 
	 */
	public List getIncomingMessagingEdges()
	{
		return inmsgedges;
	}
	
	/**
	 * 
	 */
	public void addIncomingMessagingEdge(MMessagingEdge edge)
	{
		if(inmsgedges==null)
			inmsgedges = new ArrayList();
		inmsgedges.add(edge);
	}
	
	/**
	 * 
	 */
	public void removeIncomingMessagingEdge(MMessagingEdge edge)
	{
		if(inmsgedges!=null)
			inmsgedges.remove(edge);
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
	
	/**
	 * 
	 */
	public List getAssociations()
	{
		return associations;
	}

	/**
	 * 
	 */
	public void addAssociation(MAssociation association)
	{
		if(associations==null)
			associations = new ArrayList();
		associations.add(association);
	}
	
	/**
	 * 
	 */
	public void removeAssociation(MAssociation association)
	{
		if(associations!=null)
			associations.remove(association);
	}
	
	/**
	 * @return the looping
	 */
	public boolean isLooping()
	{
		return this.looping;
	}

	/**
	 * @param looping the looping to set
	 */
	public void setLooping(boolean looping)
	{
		this.looping = looping;
	}
	
	/**
	 *  Get a declared value from the model.
	 *  @param name The name.
	 */
	public void setPropertyValue(String name, Object value)
	{
		if(properties==null)
			properties = new HashMap();
		properties.put(name, value);
	}
	
	/**
	 *  Get a property value from the model.
	 *  @param name The name.
	 */
	public Object getPropertyValue(String name)
	{
		Object ret = null;
		if(properties!=null)
			ret = properties.get(name);
		return ret;
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
