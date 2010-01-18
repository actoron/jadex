/**
 * 
 */
package jadex.tools.bpmn.diagram.edit.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.LabelEditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.stp.bpmn.diagram.edit.parts.BpmnEditPartFactory;
import org.eclipse.stp.bpmn.diagram.edit.parts.SequenceEdgeEditPart;
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
				case SequenceEdgeEditPart.VISUAL_ID:
					return new SequenceEdgeEditPartWithCondition(view);

				case WrappingLabelEditPart.VISUAL_ID:
					return new WrappingLabelEditPart(view);

					
//				case TextAnnotationEditPart.VISUAL_ID:
//					// For the case of the text annotation edit parts, we
//					// override the
//					// default edit parts to change their default edit policies
//					// THIS IS CURENTLY ONLY A TEST !!
//					return new TextAnnotationEditPart(view)
//					{
//						@Override
//						protected void createDefaultEditPolicies()
//						{
//							super.createDefaultEditPolicies();
//							installEditPolicy(
//									EditPolicyRoles.PROPERTY_HANDLER_ROLE,
//									new JadexImplEditPolicy());
//						}
//					};
//
//				case TextAnnotation2EditPart.VISUAL_ID:
//					// For the case of the text annotation edit parts, we
//					// override the
//					// default edit parts to change their default edit policies
//					// THIS IS CURENTLY ONLY A TEST !!
//					return new TextAnnotationEditPart(view)
//					{
//						@Override
//						protected void createDefaultEditPolicies()
//						{
//							super.createDefaultEditPolicies();
//							installEditPolicy(
//									EditPolicyRoles.PROPERTY_HANDLER_ROLE,
//									new JadexImplEditPolicy());
//						}
//					};
			}

		}
		return super.createEditPart(context, model);
	}

}
