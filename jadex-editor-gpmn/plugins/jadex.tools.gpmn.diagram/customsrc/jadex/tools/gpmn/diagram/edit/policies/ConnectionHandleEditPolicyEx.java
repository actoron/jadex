package jadex.tools.gpmn.diagram.edit.policies;

import jadex.tools.gpmn.diagram.handles.ConnectionHandleEx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Tool;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.ConnectionHandleEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.DiagramAssistantEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.handles.ConnectionHandleLocator;
import org.eclipse.gmf.runtime.diagram.ui.handles.ConnectionHandle.HandleDirection;
import org.eclipse.gmf.runtime.diagram.ui.l10n.DiagramUIMessages;
import org.eclipse.gmf.runtime.diagram.ui.preferences.IPreferenceConstants;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.ui.services.modelingassistant.ModelingAssistantService;

/**
 * Derived from
 * org.eclipse.gmf.runtime.diagram.ui.editpolicies.ConnectionHandleEditPolicy.
 */
public class ConnectionHandleEditPolicyEx extends DiagramAssistantEditPolicy
{

	/**
	 * Listens to the owner figure being moved so the handles can be removed
	 * when this occurs.
	 */
	private class OwnerMovedListener implements FigureListener
	{

		/**
		 * @see org.eclipse.draw2d.FigureListener#figureMoved(org.eclipse.draw2d.IFigure)
		 */
		public void figureMoved(IFigure source)
		{
			hideDiagramAssistant();
		}
	}

	/** listener for owner shape movement */
	private OwnerMovedListener ownerMovedListener = new OwnerMovedListener();

	/** list of connection handles currently being displayed */
	private List handles = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.editpolicies.DiagramAssistantEditPolicy
	 * #isDiagramAssistant(java.lang.Object)
	 */
	protected boolean isDiagramAssistant(Object object)
	{
		return object instanceof ConnectionHandleEx;
	}

	/**
	 * Gets the two connection handle figures to be added to this shape if they
	 * support user gestures.
	 * 
	 * @return a list of <code>ConnectionHandleEx</code> objects
	 */
	protected List getHandleFigures()
	{
		List list = new ArrayList(2);

		String tooltip;
		tooltip = buildTooltip(HandleDirection.INCOMING);
		if (tooltip != null)
		{
			list.add(new ConnectionHandleEx((IGraphicalEditPart) getHost(),
					HandleDirection.INCOMING, tooltip));
		}

		tooltip = buildTooltip(HandleDirection.OUTGOING);
		if (tooltip != null)
		{
			list.add(new ConnectionHandleEx((IGraphicalEditPart) getHost(),
					HandleDirection.OUTGOING, tooltip));
		}

		return list;
	}

	/**
	 * By default relies on the GMF ModelingAssistantService to return the
	 * appropriate types This can be overridden by extension of the modeler.
	 * 
	 * @return The list of element types that could be target of a new
	 *         relationship
	 */
	@SuppressWarnings("unchecked")//$NON-NLS-1$
	protected List<IElementType> getTargetElementTypes()
	{
		return ModelingAssistantService.getInstance().getRelTypesOnTarget(
				getHost());
	}

	/**
	 * By default relies on the GMF ModelingAssistantService to return the
	 * appropriate types This can be overridden by extension of the modeler.
	 * 
	 * @return The list of element types that could be source of a new
	 *         relationship
	 */
	@SuppressWarnings("unchecked")//$NON-NLS-1$
	protected List<IElementType> getSourceElementTypes()
	{
		return ModelingAssistantService.getInstance().getRelTypesOnSource(
				getHost());
	}

	/**
	 * By default relies on the GMF ModelingAssistantService to return the
	 * appropriate types This can be overridden by extension of the modeler.
	 * 
	 * @return The list of element types that could be target of a new
	 *         relationship
	 */
	@SuppressWarnings("unchecked")//$NON-NLS-1$
	protected List<IElementType> getTargetElementTypesSRE()
	{
		return ModelingAssistantService.getInstance()
				.getRelTypesForSREOnTarget(getHost());
	}

	/**
	 * By default relies on the GMF ModelingAssistantService to return the
	 * appropriate types This can be overridden by extension of the modeler.
	 * 
	 * @return The list of element types that could be source of a new
	 *         relationship
	 */
	@SuppressWarnings("unchecked")//$NON-NLS-1$
	protected List<IElementType> getSourceElementTypesSRE()
	{
		return ModelingAssistantService.getInstance()
				.getRelTypesForSREOnSource(getHost());
	}

	/**
	 * Builds the applicable tooltip string based on whether the Modeling
	 * Assistant Service supports handle gestures on this element. If no
	 * gestures are supported, the tooltip returned will be null.
	 * 
	 * @param direction
	 *            the handle direction.
	 * @return tooltip the tooltip string; if null, the handle should be not be
	 *         displayed
	 */
	protected String buildTooltip(HandleDirection direction)
	{

		boolean supportsCreation = (direction == HandleDirection.OUTGOING) ? !getSourceElementTypes()
				.isEmpty()
				: !getTargetElementTypes().isEmpty();

		boolean supportsSRE = (direction == HandleDirection.OUTGOING) ? !getSourceElementTypesSRE()
				.isEmpty()
				: !getTargetElementTypesSRE().isEmpty();

		if (supportsSRE)
		{
			if (supportsCreation)
			{
				return DiagramUIMessages.ConnectionHandle_ToolTip_ShowRelatedElementsAndCreateRelationship;
			}
			else
			{
				return DiagramUIMessages.ConnectionHandle_ToolTip_ShowRelatedElementsOnly;
			}
		}
		else if (supportsCreation)
		{
			return DiagramUIMessages.ConnectionHandle_ToolTip_CreateRelationshipOnly;
		}
		return null;
	}

	public void activate()
	{
		super.activate();

		((IGraphicalEditPart) getHost()).getFigure().addFigureListener(
				ownerMovedListener);
	}

	public void deactivate()
	{
		((IGraphicalEditPart) getHost()).getFigure().removeFigureListener(
				ownerMovedListener);

		super.deactivate();
	}

	protected void showDiagramAssistant(Point referencePoint)
	{
		if (referencePoint == null)
		{
			referencePoint = getHostFigure().getBounds().getBottom();
		}

		handles = getHandleFigures();
		if (handles == null)
		{
			return;
		}

		ConnectionHandleLocator locator = getConnectionHandleLocator(referencePoint);
		IFigure layer = getLayer(LayerConstants.HANDLE_LAYER);
		for (Iterator iter = handles.iterator(); iter.hasNext();)
		{
			ConnectionHandleEx handle = (ConnectionHandleEx) iter.next();

			handle.setLocator(locator);
			locator.addHandle(handle);

			handle.addMouseMotionListener(this);
			layer.add(handle);

			// Register this figure with it's host editpart so mouse events
			// will be propagated to it's host.
			getHost().getViewer().getVisualPartMap().put(handle, getHost());
		}

		if (!shouldAvoidHidingDiagramAssistant())
		{
			// dismiss the handles after a delay
			hideDiagramAssistantAfterDelay(getDisappearanceDelay());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.editpolicies.DiagramAssistantEditPolicy
	 * #getPreferenceName()
	 */
	String getPreferenceName()
	{
		return IPreferenceConstants.PREF_SHOW_CONNECTION_HANDLES;
	}

	/**
	 * Removes the connection handles.
	 */
	protected void hideDiagramAssistant()
	{
		if (handles == null)
		{
			return;
		}
		IFigure layer = getLayer(LayerConstants.HANDLE_LAYER);
		for (Iterator iter = handles.iterator(); iter.hasNext();)
		{
			IFigure handle = (IFigure) iter.next();
			handle.removeMouseMotionListener(this);
			layer.remove(handle);
			getHost().getViewer().getVisualPartMap().remove(handle);
		}
		handles = null;
	}

	private boolean isSelectionToolActive()
	{
		// getViewer calls getParent so check for null
		if (getHost().getParent() != null)
		{
			Tool theTool = getHost().getViewer().getEditDomain()
					.getActiveTool();
			if ((theTool != null) && theTool instanceof SelectionTool)
			{
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.editpolicies.DiagramAssistantEditPolicy
	 * #shouldShowDiagramAssistant()
	 */
	protected boolean shouldShowDiagramAssistant()
	{
		if (!super.shouldShowDiagramAssistant())
		{
			return false;
		}
		if (handles != null || !isSelectionToolActive())
		{
			return false;
		}
		return true;
	}

	/**
	 * get the connection handle locator using the host and the passed reference
	 * point
	 * 
	 * @param referencePoint
	 * @return <code>ConnectionHandleLocator</code>
	 */
	protected ConnectionHandleLocator getConnectionHandleLocator(
			Point referencePoint)
	{
		return new ConnectionHandleLocator(getHostFigure(), referencePoint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.editpolicies.DiagramAssistantEditPolicy
	 * #isDiagramAssistantShowing()
	 */
	protected boolean isDiagramAssistantShowing()
	{
		return handles != null;
	}

	protected String getDiagramAssistantID()
	{
		return ConnectionHandleEditPolicy.class.getName();
	}

	/**
	 * Add a connection handle to the edit policy.
	 * 
	 * @param aHandle
	 *            the connection handle.
	 * @since 1.2
	 */
	public void addHandle(ConnectionHandleEx aHandle)
	{
		handles.add(aHandle);
	}
}
