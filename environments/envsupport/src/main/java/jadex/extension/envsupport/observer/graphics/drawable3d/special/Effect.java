package jadex.extension.envsupport.observer.graphics.drawable3d.special;

import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.javaparser.IParsedExpression;



public class Effect extends Primitive3d {
	
	protected String predefinedId = "";
	protected boolean predefined = false;
	protected float startsize;
	protected float endsize;
	
	
	

	public Effect(String predefinedId) {
		super();
		this.predefinedId = predefinedId;
	}
	
	/**
	 * Constructor for Predefined use
	 * @param position
	 * @param predefinedId
	 * @param exp
	 */

	public Effect(Object position, String predefinedId, double startsize, double endsize, IParsedExpression exp) {
		super(Primitive3d.PRIMITIVE_TYPE_EFFECT, position, new Vector3Double(0.0), new Vector3Double(1.0), exp);
		if(!predefinedId.equals(""))
		{
			this.predefined = true;
			this.predefinedId = predefinedId;
		}

		this.startsize = (float) startsize;
		this.endsize = (float) endsize;
	}

	/**
	 * @return the predefinedId
	 */
	public String getPredefinedId() {
		return predefinedId;
	}

	/**
	 * @param predefinedId the predefinedId to set
	 */
	public void setPredefinedId(String predefinedId) {
		this.predefinedId = predefinedId;
	}

	/**
	 * @return the startsize
	 */
	public float getStartsize() {
		return startsize;
	}

	/**
	 * @param startsize the startsize to set
	 */
	public void setStartsize(float startsize) {
		this.startsize = startsize;
	}

	/**
	 * @return the endsize
	 */
	public float getEndsize() {
		return endsize;
	}

	/**
	 * @param endsize the endsize to set
	 */
	public void setEndsize(float endsize) {
		this.endsize = endsize;
	}

	/**
	 * @return the predefined
	 */
	public boolean isPredefined() {
		return predefined;
	}

	/**
	 * @param predefined the predefined to set
	 */
	public void setPredefined(boolean predefined) {
		this.predefined = predefined;
	}
	
	

}
