package jadex.tools.gpmn.diagram.tools;

import jadex.tools.gpmn.diagram.edit.parts.VirtualActivationEdgeEditPart;
import jadex.tools.gpmn.diagram.ui.figures.VirtualActivationEdgeFigure;

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.type.DiagramNotationType;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.gef.ui.internal.tools.SelectConnectionEditPartTracker;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;

@SuppressWarnings("restriction")
public class VirtualActivationEdgeSelectToolEx extends SelectConnectionEditPartTracker
{
	public VirtualActivationEdgeSelectToolEx(ConnectionEditPart owner)
	{
		super(owner);
	}
	
	@Override
	protected boolean handleButtonDown(int button)
	{
		PrecisionPoint loc = new PrecisionPoint(getLocation());
		//getEdgeFigure().translateToAbsolute(loc);
		
		PrecisionPoint center = getEdgeFigure().getPreciseBendPointCenter();
		getEdgeFigure().translateToAbsolute(center);
		// TODO: This is definitely incorrect but SWT.BUTTON1 is the wrong mask.
		if ((button & 1) != 0)
		{
			if (center.getDistance(loc) <= VirtualActivationEdgeFigure.OVAL_RADIUS + 1.0)
			{
				getEdge().getDiagramEditDomain().getDiagramCommandStack().execute(getExpandCommand((DiagramEditPart) getEdge().getRoot().getContents(),
						getPlanFromVirtualEdge((Edge) getEdge().getNotationView())));
				return true;
			}
		}
		
		return super.handleButtonDown(button);
	}
	
	/*public static Command getExpandCommand(DiagramEditPart diagramEditPart, Node activationPlanNode)
	{
		Edge vaeEdge = null;
		for (Iterator it = activationPlanNode.getSourceEdges().iterator(); it.hasNext(); )
		{
			Edge edge = (Edge) it.next();
			if (edge.getType().equals(DiagramNotationType.NOTE_ATTACHMENT.getSemanticHint()))
			{
				vaeEdge = (Edge) edge.getTarget();
				break;
			}
		}
		
		return getExpandCommand((VirtualActivationEdgeEditPart) SGpmnUtilities.resolveEditPart(diagramEditPart, activationPlanNode));
	}*/
	
	public static Command getExpandCommand(final DiagramEditPart diagramEditPart, final Node activationPlanNode)
	{
		//final DiagramEditPart diagramEditPart = (DiagramEditPart) vaePart.getRoot().getContents();
		
		Command cmd = new ICommandProxy(new AbstractTransactionalCommand((TransactionalEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(diagramEditPart.resolveSemanticElement()),
				"Create Virtual Edges.",
				Arrays.asList(new Object[] {WorkspaceSynchronizer.getFile(diagramEditPart.resolveSemanticElement().eResource())}))
		{
			@SuppressWarnings("unchecked")
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				
				Edge[] sourceEdges =  (Edge[]) activationPlanNode.getSourceEdges().toArray(new Edge[0]);
				for (int i = 0; i < sourceEdges.length; ++i)
				{
					if (sourceEdges[i].getType().equals(DiagramNotationType.NOTE_ATTACHMENT.getSemanticHint()))
					{
						ViewUtil.destroy(sourceEdges[i].getTarget());
						ViewUtil.destroy(sourceEdges[i]);
					}
				}
				
				activationPlanNode.setVisible(true);
				
				return CommandResult.newOKCommandResult();
			}
			
			public boolean canExecute()
			{
				return true;
			};
		});
		
		return cmd;
	}
	
	public static final Node getPlanFromVirtualEdge(Edge vaeEdge)
	{
		return (Node) ((Edge) vaeEdge.getTargetEdges().get(0)).getSource();
	}
	
	private VirtualActivationEdgeEditPart getEdge()
	{
		return (VirtualActivationEdgeEditPart) getSourceEditPart();
	}
	
	private VirtualActivationEdgeFigure getEdgeFigure()
	{
		return (VirtualActivationEdgeFigure) getEdge().getFigure();
	}
}
