/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.stp.bpmn.Activity;
import org.eclipse.stp.bpmn.ActivityType;
import org.eclipse.stp.bpmn.diagram.edit.parts.Activity2EditPart;
import org.eclipse.stp.bpmn.diagram.edit.parts.ActivityEditPart;

/**
 * @author Claas
 *
 */
public class JadexUserTaskActivityFilter implements IFilter
{

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
	 */
	@Override
	public boolean select(Object toTest)
	{
		if (toTest instanceof ActivityEditPart || toTest instanceof Activity2EditPart)
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
