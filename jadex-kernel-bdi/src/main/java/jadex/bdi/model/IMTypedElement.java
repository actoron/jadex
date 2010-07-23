package jadex.bdi.model;


/**
 *  Interface for typed elements.
 */
public interface IMTypedElement extends IMReferenceableElement
{
	/**
	 *  Get the clazz.
	 *  @return The clazz. 
	 */
	public Class getClazz();
	
	/**
	 *  Get the class name.
	 *  @return The class name. 
	 */
	public String getClassname();
	
	/**
	 *  Get the update rate.
	 *  @return The update rate.
	 */
	public long getUpdateRate();
	
	/**
	 *  Get the evaluation mode.
	 *  @return The evaluation mode.
	 */
	public String getEvaluationMode();
}
