package jadex.tools.gpmn.diagram.edit.commands;

import jadex.tools.gpmn.ActivationEdge;
import jadex.tools.gpmn.diagram.edit.parts.ActivationEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.VirtualActivationEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.VirtualActivationOrderEditPart;
import jadex.tools.gpmn.diagram.tools.SGpmnUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;

public class ModifyActivationEdgeOrder extends AbstractTransactionalCommand
{
	private Node apNode;
	private ActivationEdge aEdge;
	private int newOrder;
	private DiagramEditPart diagramEditPart;
	
	public ModifyActivationEdgeOrder(DiagramEditPart diagramEditPart, Edge aEdge, int newOrder)
	{
		super((TransactionalEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(diagramEditPart.getDiagramView()),
				"Modify Edge Order.",
				Arrays.asList(new Object[] {WorkspaceSynchronizer.getFile(diagramEditPart.getDiagramView().eResource())}));
		//this.apNode = apNode;
		this.apNode = (Node) aEdge.getSource();
		this.aEdge = (ActivationEdge) aEdge.getElement();
		this.newOrder = newOrder;
		this.diagramEditPart = diagramEditPart;
	}
	
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException
	{
		boolean sort = true;
		ArrayList<ActivationEdge> aEdges = new ArrayList<ActivationEdge>();
		for (Object edge : apNode.getSourceEdges())
			if ((edge instanceof Edge) && (((Edge) edge).getElement()) instanceof ActivationEdge)
			{
				ActivationEdge caEdge = (ActivationEdge) ((Edge) edge).getElement();
				if (caEdge.getOrder() == newOrder && !caEdge.equals(aEdge))
				{
					caEdge.setOrder(aEdge.getOrder());
					sort = false;
					break;
				}
				aEdges.add(caEdge);
			}
		
		aEdge.setOrder(newOrder);
		
		if (sort)
		{
			Collections.sort(aEdges, new Comparator<ActivationEdge>()
			{
				public int compare(ActivationEdge ae1, ActivationEdge ae2)
				{
					return ae1.getOrder() - ae2.getOrder();
				}
			});
			
			for (int i = 0; i < aEdges.size(); ++i)
				aEdges.get(i).setOrder(i + 1);
		}
		
		for (Object edge : apNode.getSourceEdges())
		{
			if (edge instanceof Edge)
			{
				if ((((Edge) edge).getElement()) instanceof ActivationEdge)
				{
					ActivationEdgeEditPart part = (ActivationEdgeEditPart) SGpmnUtilities.resolveEditPart(diagramEditPart, edge);
					if (part != null)
						((EditPart) part.getChildren().get(0)).refresh();
					
					if (((Edge) edge).getSourceEdges().size() == 1)
						((VirtualActivationOrderEditPart) SGpmnUtilities.resolveEditPart(diagramEditPart,((Edge) ((Edge) edge).getSourceEdges().get(0)).getTarget()).getChildren().get(0)).refresh();
				}
			}
		}
		
		return CommandResult.newOKCommandResult();
	}
}
