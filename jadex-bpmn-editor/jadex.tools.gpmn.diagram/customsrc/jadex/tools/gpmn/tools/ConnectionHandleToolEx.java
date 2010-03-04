/**
 * 
 */
package jadex.tools.gpmn.tools;

import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.Plan;
import jadex.tools.gpmn.SubProcessGoal;
import jadex.tools.gpmn.diagram.edit.parts.GoalEditPartSupport;
import jadex.tools.gpmn.diagram.edit.parts.SubGoalEdgeEditPart;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IPrimaryEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.handles.ConnectionHandle;
import org.eclipse.gmf.runtime.diagram.ui.internal.tools.ConnectionHandleTool;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateUnspecifiedTypeConnectionRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.RequestConstants;
import org.eclipse.gmf.runtime.diagram.ui.requests.ToggleConnectionLabelsRequest;
import org.eclipse.gmf.runtime.diagram.ui.util.INotationType;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.ui.services.modelingassistant.ModelingAssistantService;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;

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
		}
		
		// remove generated  note attachments
		for (ListIterator<IElementType> it = relTypes.listIterator(); it
				.hasNext();)
		{
			IElementType elem = it.next();
			if (elem instanceof INotationType)
			{
				it.remove();
			}
		}

		
		CreateUnspecifiedTypeConnectionRequest request = new CreateUnspecifiedTypeConnectionRequest(
				relTypes, useModelingAssistantService(), getPreferencesHint())
		{

			@Override
			public void setTargetEditPart(EditPart part)
			{
				// doesn't work
				//filterSourceTypes(getElementTypes(), part);
				super.setTargetEditPart(part);
			}
			
			@Override
			public void setSourceEditPart(EditPart part)
			{
				// doesn't work
				//filterTargetTypes(getElementTypes(), part);
				super.setSourceEditPart(part);
			}
			
		};

		if (connHandle.isIncoming())
		{
			request.setDirectionReversed(true);
		}
		
		return request;
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
		
		List editparts = new ArrayList();
		for (Object object : objects)
		{
			if (object instanceof IAdaptable)
			{
				Object editPart = viewer.getEditPartRegistry().get(
						((IAdaptable) object).getAdapter(View.class));
				
				if (editPart instanceof IPrimaryEditPart)
				{
					editparts.add(editPart);
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
	
	
	
	
	
	
	
	/**
	 * Filter the source types depend on model
	 * @param typelist
	 * @return typelist filtered 
	 */
	protected List<IElementType> filterSourceTypes(List<IElementType> typelist, EditPart targetEditPart)
	{
		//IGraphicalEditPart host = (IGraphicalEditPart) getHost();
		//Object model = host.getPrimaryView().getElement();
		
		Object model = targetEditPart.getModel();
		
		if (model instanceof Plan)
		{
			// sub plan of plan is not allowed
			typelist.remove(GpmnElementTypes.PlanEdge_4003);
		}
		if (model instanceof SubProcessGoal)
		{
			// SubPorocessGoals don't have outgoing connections
			typelist.clear();
		}
		
		return typelist;
	}
	
	/**
	 * Filter the target types depend on model
	 * @param typelist
	 * @return typelist filtered 
	 */
	protected List<IElementType> filterTargetTypes(List<IElementType> typelist, EditPart sourceEditPart)
	{
		//IGraphicalEditPart host = (IGraphicalEditPart) getHost();
		//Object model = host.getPrimaryView().getElement();
		
		Object model = sourceEditPart.getModel();
		
		if (model instanceof Plan)
		{
			// sub plan of plan is not allowed
			typelist.remove(GpmnElementTypes.SubGoalEdge_4002);
		}
		if (model instanceof SubProcessGoal)
		{
			// remove plan connections
			typelist.remove(GpmnElementTypes.PlanEdge_4003);
		}
		if (model instanceof Goal)
		{
			// remove plan connections
			typelist.remove(GpmnElementTypes.PlanEdge_4003);
		}
		
		return typelist;
	}

}
