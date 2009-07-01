package jadex.bpmn.model;

import jadex.commons.SReflect;

/**
 * 
 */
public class MActivity extends MVertex
{
	//-------- attributes --------
	
	/** The lane description. */
	protected String lanedescription;
	
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
