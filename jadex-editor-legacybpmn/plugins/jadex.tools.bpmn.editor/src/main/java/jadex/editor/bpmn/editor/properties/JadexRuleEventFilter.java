package jadex.editor.bpmn.editor.properties;


import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.stp.bpmn.Activity;
import org.eclipse.stp.bpmn.ActivityType;
import org.eclipse.stp.bpmn.diagram.edit.parts.Activity2EditPart;
import org.eclipse.stp.bpmn.diagram.edit.parts.ActivityEditPart;

/**
 * 
 */
public class JadexRuleEventFilter implements IFilter
{
	public boolean select(Object toTest)
	{
		boolean ret = false;
		if (toTest instanceof ActivityEditPart || toTest instanceof Activity2EditPart)
		{
			EditPart part = (EditPart) toTest;
			if (part.getModel() instanceof View)
			{
				Activity model = (Activity) ((View) part.getModel()).getElement();
				ret = ActivityType.EVENT_INTERMEDIATE_RULE_LITERAL.equals(model.getActivityType())
					|| ActivityType.EVENT_START_RULE_LITERAL.equals(model.getActivityType());
			}
		}
		
		return ret;
	}

}
