/**
 * 
 */
package jadex.editor.bpmn.diagram.edit.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.stp.bpmn.diagram.edit.parts.BpmnEditPartFactory;
import org.eclipse.stp.bpmn.diagram.part.BpmnVisualIDRegistry;

/**
 * @author Claas
 * 
 */
public class JadexEditPartFactory extends BpmnEditPartFactory
{

	/**
	 * Creates a special text annotation edit part
	 */
	public EditPart createEditPart(EditPart context, Object model)
	{
		if (model instanceof View)
		{
			final View view = (View) model;
			int viewVisualID = BpmnVisualIDRegistry.getVisualID(view);
			switch (viewVisualID)
			{
				// use default EP until preference editor
				//case SequenceEdgeEditPart.VISUAL_ID:
				//	return new SequenceEdgeEditPartWithCondition(view);

				case WrappingLabelEditPart.VISUAL_ID:
					return new WrappingLabelEditPart(view);

			}

		}
		return super.createEditPart(context, model);
	}

}
