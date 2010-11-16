package jadex.tools.gpmn.diagram.tools;

import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GoalType;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.core.edithelpers.CreateElementRequestAdapter;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequestFactory;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest.ViewAndElementDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.tools.UnspecifiedTypeCreationTool;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.notation.Location;
import org.eclipse.gmf.runtime.notation.Node;

public abstract class AbstractGoalCreationTool extends UnspecifiedTypeCreationTool
{
	public AbstractGoalCreationTool()
	{
		super(Arrays.asList(new Object[] {GpmnElementTypes.Goal_2004}));
	}
	
	@Override
	protected Command getCommand()
	{
		final DiagramEditPart diagramEditPart = (DiagramEditPart) getCurrentViewer().getRootEditPart().getChildren().get(0);
		Command cmd = new ICommandProxy(new AbstractTransactionalCommand((TransactionalEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(diagramEditPart.resolveSemanticElement()),
				"Create "+getGoalType().getName(),
				Arrays.asList(new Object[] {WorkspaceSynchronizer.getFile(diagramEditPart.resolveSemanticElement().eResource())}))
		{
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				CreateViewRequest req = CreateViewRequestFactory.getCreateShapeRequest(GpmnElementTypes.Goal_2004, diagramEditPart.getDiagramPreferencesHint());
				diagramEditPart.getCommand(req).execute();
				diagramEditPart.refresh();
				Goal goal = (Goal) (((CreateElementRequestAdapter) ((ViewAndElementDescriptor) ((List) req.getNewObject()).get(0)).getElementAdapter()).resolve());
				
				goal.setGoalType(getGoalType());
				diagramEditPart.refresh();
				Point p = getLocation();
				IGraphicalEditPart goalEditPart = (IGraphicalEditPart) SGpmnUtilities.resolveEditPart(diagramEditPart, goal);
				Node goalNode = (Node) goalEditPart.getModel();
				diagramEditPart.getFigure().translateToRelative(p);
				int w2 = Math.max(goalEditPart.getFigure().getSize().width, goalEditPart.getFigure().getMinimumSize().width) / 2;
				int h2 = Math.max(goalEditPart.getFigure().getSize().height, goalEditPart.getFigure().getMinimumSize().height) / 2;
				((Location) goalNode.getLayoutConstraint()).setX(p.x - w2);
				((Location) goalNode.getLayoutConstraint()).setY(p.y - h2);
				
				return null;
			}
		});
		
		return cmd;
	}
	
	public abstract GoalType getGoalType();
	
	@Override
	public IElementType getElementType()
	{
		return GpmnElementTypes.Goal_2004;
	}
}
