
package jadex.tools.gpmn.diagram.handles;

import jadex.tools.gpmn.diagram.tools.ConnectionHandleToolEx;

import org.eclipse.gef.DragTracker;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.handles.ConnectionHandle;

/**
 * Handles the images for subgoal and message connections
 */
public class ConnectionHandleEx extends ConnectionHandle
{

	public ConnectionHandleEx(IGraphicalEditPart ownerEditPart,
			HandleDirection relationshipDirection, String tooltip)
	{
		super(ownerEditPart, relationshipDirection, tooltip);
	}
	
	/**
	 * The extended tool returned here will not go into direct-edit 
	 * at the end of the creation.
	 * 
	 * @see org.eclipse.gef.handles.AbstractHandle#createDragTracker()
	 */
	@Override
	protected DragTracker createDragTracker()
	{
		return new ConnectionHandleToolEx(this);
	}

}
