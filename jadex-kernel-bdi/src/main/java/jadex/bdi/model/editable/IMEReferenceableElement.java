package jadex.bdi.model.editable;

import jadex.bdi.model.IMReferenceableElement;

/**
 * 
 */
public interface IMEReferenceableElement extends IMReferenceableElement, IMEElement
{
	/**
	 *  Set exported state.
	 *  @param exported The exported state. 
	 */
	public void setExported(String exported);
	
	/**
	 *  Set the assigntos.
	 *  @param assigntos The assign to elements. 
	 */
	public void setAssigntos(String[] assigntos);
}
