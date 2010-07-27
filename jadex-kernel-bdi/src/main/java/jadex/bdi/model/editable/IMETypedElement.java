package jadex.bdi.model.editable;

import jadex.bdi.model.IMTypedElement;

/**
 * 
 */
public interface IMETypedElement extends IMEReferenceableElement, IMTypedElement
{
	/**
	 *  Set the clazz.
	 *  @param clazz The clazz. 
	 */
	public void setClazz(Class clazz);
	
//	/**
//	 *  Set the class name.
//	 *  @param name The class name. 
//	 */
//	public void setClassname(String name);
	
	/**
	 *  Set the update rate.
	 *  @param updaterate The update rate.
	 */
	public void setUpdateRate(long updaterate);
	
	/**
	 *  Set the evaluation mode.
	 *  @param mode The evaluation mode.
	 */
	public void setEvaluationMode(String mode);
}
