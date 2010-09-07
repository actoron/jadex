/**
 * 
 */
package jadex.tools.gpmn.diagram.part;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditDomain;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.ui.IEditorPart;

/**
 * GPMN specific DiagramEditDomain<p>
 * Primary used to remove some generated model elements from palette and popup bar
 * 
 * @author Claas
 *
 */
public class GpmnDiagramEditDomain extends DiagramEditDomain
{

	/**
     * List of generated element types to be removed from palette and popup bar
     * @generated NOT
     */
    private Set<IElementType> removedElementTypes =  new HashSet<IElementType>();
	
	/**
	 * @param editorPart
	 */
	public GpmnDiagramEditDomain(IEditorPart editorPart)
	{
		super(editorPart);
	}
	
	/**
     * 
     * @return the element types that are removed from the domain.
     * @generated NOT
     */
	public Set<IElementType> getRemovedElementTypes() {
		return removedElementTypes;
	}

	/**
	 * Sets the generated element types that should removed from the domain.
	 * @param removedElementTypes
	 * @generated NOT
	 */
	public void setRemovedElementTypes(Set<IElementType> removedElementTypes) {
		this.removedElementTypes = removedElementTypes;
	}

}
