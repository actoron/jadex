/**
 * 
 */
package jadex.editor.bpmn.editor.properties;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.stp.bpmn.Activity;
import org.eclipse.stp.bpmn.ActivityType;
import org.eclipse.stp.bpmn.diagram.edit.parts.Activity2EditPart;
import org.eclipse.stp.bpmn.diagram.edit.parts.ActivityEditPart;
import org.eclipse.stp.bpmn.diagram.edit.parts.SubProcessEditPart;
import org.eclipse.stp.bpmn.diagram.edit.parts.SubProcessNameEditPart;
import org.eclipse.stp.bpmn.diagram.edit.parts.SubProcessSubProcessBodyCompartmentEditPart;
import org.eclipse.stp.bpmn.diagram.edit.parts.SubProcessSubProcessBorderCompartmentEditPart;

/**
 * @author Claas
 *
 */
public class JadexUserTaskActivityFilter implements IFilter
{

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
	 */
	public boolean select(Object toTest)
	{
//		if(toTest instanceof SubProcessEditPart || toTest instanceof SubProcessNameEditPart
//			|| toTest instanceof SubProcessSubProcessBodyCompartmentEditPart || toTest instanceof SubProcessSubProcessBorderCompartmentEditPart)
//		{
//			return true;
//		}
		
		
		if(toTest instanceof ActivityEditPart || toTest instanceof Activity2EditPart)
		{
			EditPart part = (EditPart) toTest;
			if (part.getModel() instanceof View)
			{
				Activity model = (Activity) ((View) part.getModel()).getElement();
				return (model.getActivityType().getValue() == ActivityType.TASK);
			}
		}
		return false;
	}

}
