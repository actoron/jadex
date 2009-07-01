package jadex.bpmn.model;

import jadex.commons.SReflect;

/**
 * 
 */
public class MActivity extends MVertex
{

	
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
