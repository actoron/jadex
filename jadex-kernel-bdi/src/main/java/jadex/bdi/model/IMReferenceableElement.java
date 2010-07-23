package jadex.bdi.model;

/**
 *  Interface for referenceable model elements.
 */
public interface IMReferenceableElement extends IMElement
{
	/**
	 *  Test if the element is exported.
	 *  @return True if exported. 
	 */
	public String isExported();
	
	/**
	 *  Test if the element is exported.
	 *  @return True if exported. 
	 */
	public String[] getAssigntos();
}
