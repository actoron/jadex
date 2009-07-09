package jadex.tools.bpmn.editor;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.stp.bpmn.diagram.edit.parts.BpmnEditPartFactory;
import org.eclipse.stp.bpmn.diagram.edit.parts.TextAnnotation2EditPart;
import org.eclipse.stp.bpmn.diagram.edit.parts.TextAnnotationEditPart;
import org.eclipse.stp.bpmn.diagram.part.BpmnVisualIDRegistry;

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
			// For the case of the text annotation edit parts, we override the
			// default edit parts to change their default edit policies
			switch (viewVisualID)
			{
			case TextAnnotationEditPart.VISUAL_ID:
				return new TextAnnotationEditPart(view)
				{
					@Override
					protected void createDefaultEditPolicies()
					{
						super.createDefaultEditPolicies();
						installEditPolicy(EditPolicyRoles.PROPERTY_HANDLER_ROLE, new JadexImplEditPolicy());
					}
				};

			case TextAnnotation2EditPart.VISUAL_ID:
				return new TextAnnotationEditPart(view)
				{
					@Override
					protected void createDefaultEditPolicies()
					{
						super.createDefaultEditPolicies();
						installEditPolicy(EditPolicyRoles.PROPERTY_HANDLER_ROLE, new JadexImplEditPolicy());
					}
				};
			}
		}
		return super.createEditPart(context, model);
	}

}
