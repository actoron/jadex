package jadex.tools.gpmn.diagram.parsers;

import jadex.tools.gpmn.ActivationEdge;
import jadex.tools.gpmn.ActivationPlan;
import jadex.tools.gpmn.diagram.edit.commands.ModifyActivationEdgeOrder;
import jadex.tools.gpmn.diagram.tools.SGpmnUtilities;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.NodeListener;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.common.ui.services.parser.IParser;
import org.eclipse.gmf.runtime.common.ui.services.parser.IParserEditStatus;
import org.eclipse.gmf.runtime.common.ui.services.parser.ParserEditStatus;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;

public class VirtualOrderParser implements IParser
{
	private DiagramEditPart diagramEditPart;
	
	public VirtualOrderParser(DiagramEditPart diagramEditPart)
	{
		//diagramEditPart.getDiagramView().
		this.diagramEditPart = diagramEditPart;
	}

	@Override
	public IContentAssistProcessor getCompletionProcessor(IAdaptable element)
	{
		return null;
	}

	@Override
	public String getEditString(IAdaptable element, int flags)
	{
		EObject eElement = (EObject) element.getAdapter(EObject.class);
		return getPrintString(element, flags);
		//return "3";
	}

	@Override
	public ICommand getParseCommand(IAdaptable element, String newString,
			int flags)
	{
		//LabelDirectEditPolicy ldep = (LabelDirectEditPolicy) element.getAdapter(LabelDirectEditPolicy.class);
		//System.err.println(((Node) element.getAdapter(Node.class)).getElement());
		final Node vaeLabel = (Node) element.getAdapter(View.class);
		final Edge vaEdge = (Edge) vaeLabel.eContainer();
		final int value = Integer.parseInt(newString);
		
		Node aeNode =  (Node) ((Edge) vaEdge.getTargetEdges().get(0)).getSource();
		Edge aEdge = null;
		for (Object edge : aeNode.getSourceEdges())
			if ((edge instanceof Edge) && (((Edge) edge).getTarget().equals(vaEdge.getTarget())))
			{
				aEdge = (Edge) edge;
				break;
			}
		ICommand cmd = new ModifyActivationEdgeOrder(diagramEditPart, aEdge, value);
		
		//SGpmnUtilities.resolveEditPart(diagramEditPart, object)
		
		//System.out.println("Requested Parse Command " + element.getClass() + " " + newString);
		//System.out.println("Requested Parse Command " + ldep.getClass());
		// TODO Auto-generated method stub
		
		//DiagramEditPart diagramEditPart = null;//(DiagramEditPart) ldep.getHost();
		
		/*ICommand cmd = new AbstractTransactionalCommand((TransactionalEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(vaEdge.getDiagram()),
				"Apply Edge Order.",
				Arrays.asList(new Object[] {WorkspaceSynchronizer.getFile(vaEdge.getDiagram().eResource())}))
		{
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				Node aeNode =  (Node) ((Edge) vaEdge.getTargetEdges().get(0)).getSource();
				for (Object edge : aeNode.getSourceEdges())
					if ((edge instanceof Edge) && (((Edge) edge).getTarget().equals(vaEdge.getTarget())))
					{
						System.err.println("Setting order " + value);
						((ActivationEdge) ((Edge) edge).getElement()).setOrder(value);
						break;
					}
				return CommandResult.newOKCommandResult();
			}
		};*/
		return cmd;
	}

	@Override
	public String getPrintString(IAdaptable element, int flags)
	{
		//System.out.println("GetPrintString: " + String.valueOf(element));
		ActivationEdge edge = (ActivationEdge) element.getAdapter(EObject.class);
		if (edge == null)
			return "";
		//System.out.println("GetPrintStringEdge: " + edge);
		return String.valueOf(edge.getOrder());
	}

	@Override
	public boolean isAffectingEvent(Object event, int flags)
	{
		//System.out.print("EVENT: ");
		//System.out.println(event);
		return false;
	}

	@Override
	public IParserEditStatus isValidEditString(IAdaptable element,
			String editString)
	{
		try
		{
			Integer.parseInt(editString);
		}
		catch (NumberFormatException e)
		{
			return ParserEditStatus.UNEDITABLE_STATUS;
		}
		return ParserEditStatus.EDITABLE_STATUS;
	}
}
