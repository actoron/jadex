package jadex.editor.bpmn.diagram.edit.policies;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.core.commands.SetPropertyCommand;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.PropertyHandlerEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.requests.ChangePropertyValueRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.RequestConstants;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.notation.View;

public class JadexImplEditPolicy extends PropertyHandlerEditPolicy implements EditPolicy
{
	public Command getCommand(Request request)
	{
		if (request.getType().equals(RequestConstants.REQ_PROPERTY_CHANGE))
		{
			EditPart part = getTargetEditPart(request);
			if (part == null)
			{
				System.out.println("Line 36: part == null");
				return null;
			}
			if (part instanceof IGraphicalEditPart)
			{
				final EObject object = ((IGraphicalEditPart) part)
						.resolveSemanticElement();
				if (object instanceof EModelElement)
				{
					EAnnotation ann = ((EModelElement) object)
							.getEAnnotation("test" /*JadexProptertyConstants.JADEX_ACTIVITY_ANNOTATION*/);
					if (ann == null)
					{
						System.out.println("Line 44: ann == null " );
						return super.getCommand(request);
					}
				}
			}

			View view = ((IGraphicalEditPart) part).getNotationView();
			ChangePropertyValueRequest cpvr = (ChangePropertyValueRequest) request;
			if (view != null)
			{
				if (ViewUtil.isPropertySupported(view, cpvr.getPropertyID()))
				{
					System.out.println("return my command");
					return new ICommandProxy(new SetPropertyCommand(
							getEditingDomain(), new EObjectAdapter(view), cpvr
									.getPropertyID(), cpvr.getPropertyName(),
							((ChangePropertyValueRequest) request).getValue()));
				}
				else
				{
					System.err.println("WÜRG .... --> false :-(");
				}
			}

			
		}
		System.err.println("UUUUUUUUUUUUUUUUUUPPPSSSSS ..... -->" + request.getType());
		return null;
		
	}
}
