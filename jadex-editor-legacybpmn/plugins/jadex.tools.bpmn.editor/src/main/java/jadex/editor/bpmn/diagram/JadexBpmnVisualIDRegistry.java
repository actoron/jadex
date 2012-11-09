/**
 * 
 */
package jadex.editor.bpmn.diagram;

import jadex.editor.bpmn.diagram.edit.parts.WrappingLabelEditPart;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.stp.bpmn.diagram.edit.parts.BpmnDiagramEditPart;
import org.eclipse.stp.bpmn.diagram.edit.parts.SequenceEdgeEditPart;
import org.eclipse.stp.bpmn.diagram.part.BpmnVisualIDRegistry;

/**
 * @author Claas
 *
 */
public class JadexBpmnVisualIDRegistry extends BpmnVisualIDRegistry
{

	/**
	 * @generated NOT
	 */
	public static int getNodeVisualID(View containerView,
			EObject domainElement, EClass domainElementMetaclass,
			String semanticHint)
	{
		// copied from BpmnVisualIDRegistry
		String containerModelID = getModelID(containerView);
		if (!BpmnDiagramEditPart.MODEL_ID.equals(containerModelID))
		{
			return -1;
		}
		int containerVisualID;
		if (BpmnDiagramEditPart.MODEL_ID.equals(containerModelID))
		{
			containerVisualID = getVisualID(containerView);
		}
		else
		{
			if (containerView instanceof Diagram)
			{
				containerVisualID = BpmnDiagramEditPart.VISUAL_ID;
			}
			else
			{
				return -1;
			}
		}
		int nodeVisualID = semanticHint != null ? getVisualID(semanticHint)
				: -1;
		
		// add WrappingLabelEditPart to switch
		switch (containerVisualID)
		{
			case SequenceEdgeEditPart.VISUAL_ID:
				if (WrappingLabelEditPart.VISUAL_ID == nodeVisualID)
				{
					return WrappingLabelEditPart.VISUAL_ID;
				}
				break;
		}
		
		// fall through
		return BpmnVisualIDRegistry.getNodeVisualID(containerView,
				domainElement, domainElementMetaclass, semanticHint);
	}
}
