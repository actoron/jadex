/**
 * 
 */
package jadex.tools.gpmn.diagram.tools;

import jadex.tools.gpmn.ActivationPlan;
import jadex.tools.gpmn.diagram.edit.parts.ActivationPlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PlanEdgeEditPart;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.core.edithelpers.CreateElementRequestAdapter;
import org.eclipse.gmf.runtime.diagram.core.util.ViewType;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IPrimaryEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.handles.ConnectionHandle;
import org.eclipse.gmf.runtime.diagram.ui.internal.tools.ConnectionHandleTool;
import org.eclipse.gmf.runtime.diagram.ui.providers.internal.DiagramViewProvider;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateUnspecifiedTypeConnectionRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequestFactory;
import org.eclipse.gmf.runtime.diagram.ui.requests.RequestConstants;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest.ViewAndElementDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.type.DiagramNotationType;
import org.eclipse.gmf.runtime.diagram.ui.util.INotationType;
import org.eclipse.gmf.runtime.diagram.ui.view.factories.DiagramViewFactory;
import org.eclipse.gmf.runtime.diagram.ui.view.factories.NoteViewFactory;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.emf.type.core.ElementTypeRegistry;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.ui.services.modelingassistant.ModelingAssistantService;
import org.eclipse.gmf.runtime.notation.DiagramLinkStyle;
import org.eclipse.gmf.runtime.notation.Location;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.quickaccess.ViewProvider;

/**
 * Disable direct label edit at the end of the creation of the connection.
 * Update the sequential order of subgoal connections and hide label if 
 * sequential flag of source is false.
 * <p>Code partly taken from STP BPMN modeler</p>
 *  
 * @author Claas
 *
 */
@SuppressWarnings("restriction")
public class ConnectionHandleToolEx extends ConnectionHandleTool
{
	//private static Command startcommand = null;

	public ConnectionHandleToolEx(ConnectionHandle connectionHandle)
	{
		super(connectionHandle);
	}
	
	/**
	 * Override to filter the rel-types provided by the assistant service that
	 * are the "attach-note" type of connection.
	 * 
	 * @see org.eclipse.gef.tools.TargetingTool#createTargetRequest()
	 */
	@SuppressWarnings("unchecked")//$NON-NLS-1$
	protected Request createTargetRequest()
	{
		List<IElementType> relTypes = null;
		ConnectionHandle connHandle = getConnectionHandle();
		if (connHandle.isIncoming())
		{
			// reversed direction.
			relTypes = ModelingAssistantService.getInstance()
					.getRelTypesOnTarget(connHandle.getOwner());
		}
		else
		{
			relTypes = ModelingAssistantService.getInstance()
					.getRelTypesOnSource(connHandle.getOwner());
			if (connHandle.getOwner() instanceof GoalEditPart)
				relTypes.add(GpmnElementTypes.ActivationEdge_4001);
			/*System.out.println(connHandle.getOwner().getClass().getCanonicalName());
			System.out.println(Arrays.toString(relTypes.toArray()));
			System.out.println(Arrays.toString(relTypes.toArray()));*/
		}
		
		// remove generated  note attachments
		for (ListIterator<IElementType> it = relTypes.listIterator(); it
				.hasNext();)
		{
			IElementType elem = it.next();
			if (elem instanceof INotationType)
			{
				//DiagramNotationType.NOTE_ATTACHMENT;
				//((DiagramEditPart) connHandle.getOwner().getParent()).getPrimaryView().getDiagram().get
				//DiagramViewFactory.
				//it.remove();
			}
		}
		
		
		CreateUnspecifiedTypeConnectionRequest request = new CreateUnspecifiedTypeConnectionRequest(
				relTypes, useModelingAssistantService(), getPreferencesHint());
		
		if (connHandle.isIncoming())
		{
			request.setDirectionReversed(true);
		}
		return request;
	}
	
	@Override
	protected Command getCommand()
	{
		if ((getConnectionHandle().getOwner() instanceof GoalEditPart) || (getTargetEditPart() instanceof GoalEditPart))
		{
			EditPart src = getConnectionHandle().getOwner();
			EditPart tgt = getTargetEditPart();
			if (getConnectionHandle().isIncoming())
			{
				EditPart tmp = src;
				src = tgt;
				tgt = tmp;
			}
			
			if (src instanceof GoalEditPart && tgt instanceof ShapeEditPart)
				return ActivationEdgeCreationTool.createGoalConnectionCommand((GoalEditPart) src, (ShapeEditPart) tgt);
		}
		return super.getCommand();
	}
	
	/**
	 * When creating a CreateUnspecifiedTypeConnectionRequest Should the
	 * modeling assistant be used? 
	 * 
	 * @return false
	 */
	protected boolean useModelingAssistantService()
	{
		return false;
	}
	
	/**
	 * Modified so that only the shapes are put in direct mode when being
	 * created with a connection.
	 */
	@Override
	protected void selectAddedObject(EditPartViewer viewer, Collection objects)
	{
		// use final arrays because we need them in anonymous runnable
		final EditPart[] shapeEP = new EditPart[1];
		final EditPart[] connectionEP = new EditPart[1];
		
		List<IPrimaryEditPart> editparts = new ArrayList<IPrimaryEditPart>();
		for (Object object : objects)
		{
			if (object instanceof IAdaptable)
			{
				Object editPart = viewer.getEditPartRegistry().get(
						((IAdaptable) object).getAdapter(View.class));
				
				if (editPart instanceof IPrimaryEditPart)
				{
					editparts.add((IPrimaryEditPart) editPart);
				}
				
				// put a shape into direct edit mode.
				if (editPart instanceof ShapeEditPart)
				{
					shapeEP[0] = (ShapeEditPart) editPart;
				}
				
				// update the connection label.
				if (editPart instanceof ConnectionEditPart)
				{
					connectionEP[0] = (ConnectionEditPart) editPart;
				}
			}
			
		}

//		if (connectionEP[0] != null)
//		{
//			if (connectionEP[0] instanceof SubGoalEdgeEditPart)
//			{
//				//SubGoalEdgeEditPart edgeEditPart = (SubGoalEdgeEditPart) connectionEP[0];
//				EditPart sourceEditPart = ((SubGoalEdgeEditPart) connectionEP[0]).getSource();
//				if (sourceEditPart != null && sourceEditPart instanceof GoalEditPartSupport)
//				{
//					Goal goal = (Goal) sourceEditPart.getModel();
//					
//					// toggle connection label edit
//					boolean isSequential = goal.isSequential();
//					if (!isSequential)
//					{
//						final ToggleConnectionLabelsRequest toggleRequest = new ToggleConnectionLabelsRequest(
//								!isSequential);
//						
//						Display.getCurrent().asyncExec(new Runnable()
//						{
//							@Override
//							public void run()
//							{
//								connectionEP[0].performRequest(toggleRequest);
//								//connectionEP[0].performRequest(new Request(RequestConstants.REQ_SELECTION));
//							}
//						});
//					}
//					
//				}
//				
//			}
//		}
		
		if (shapeEP[0] != null)
		{
			viewer.setSelection(new StructuredSelection(editparts));

			// automatically put the first shape into edit-mode
			Display.getCurrent().asyncExec(new Runnable()
			{
				@Override
				public void run()
				{
					// request direct edit for ShapeEditPart if it is active
					if (shapeEP[0].isActive())
					{
						shapeEP[0].performRequest(new Request(
								RequestConstants.REQ_DIRECT_EDIT));
					}
				}
			});
		}
		
		
	}
	
	

//	/**
//	 * Filter the source types depend on model
//	 * @param typelist
//	 * @return typelist filtered 
//	 */
//	protected List<IElementType> filterSourceTypes(List<IElementType> typelist, EditPart targetEditPart)
//	{
//
//		Object model = targetEditPart.getModel();
//		
//		if (model instanceof Plan)
//		{
//			// sub plan of plan is not allowed
//			typelist.remove(GpmnElementTypes.PlanEdge_4003);
//		}
//		if (model instanceof SubProcessGoal)
//		{
//			// SubPorocessGoals don't have outgoing connections
//			typelist.clear();
//		}
//		
//		return typelist;
//	}
//	
//	/**
//	 * Filter the target types depend on model
//	 * @param typelist
//	 * @return typelist filtered 
//	 */
//	protected List<IElementType> filterTargetTypes(List<IElementType> typelist, EditPart sourceEditPart)
//	{
//		//IGraphicalEditPart host = (IGraphicalEditPart) getHost();
//		//Object model = host.getPrimaryView().getElement();
//		
//		Object model = sourceEditPart.getModel();
//		
//		if (model instanceof Plan)
//		{
//			// sub plan of plan is not allowed
//			typelist.remove(GpmnElementTypes.SubGoalEdge_4002);
//		}
//		if (model instanceof SubProcessGoal)
//		{
//			// remove plan connections
//			typelist.remove(GpmnElementTypes.PlanEdge_4003);
//		}
//		if (model instanceof Goal)
//		{
//			// remove plan connections
//			typelist.remove(GpmnElementTypes.PlanEdge_4003);
//		}
//		
//		return typelist;
//	}
	
	/*public void printCompoundCommand(CompoundCommand cmd, String prefix)
	{
		for (Iterator it = cmd.getCommands().iterator(); it.hasNext(); )
		{
			Command c = (Command) it.next();
			System.out.print(prefix);
			System.out.print(c);
			System.out.print(" ");
			System.out.println(c.canExecute());
			if (c instanceof CompoundCommand)
				printCompoundCommand((CompoundCommand) c, prefix + " ");
		}
	}*/
}
