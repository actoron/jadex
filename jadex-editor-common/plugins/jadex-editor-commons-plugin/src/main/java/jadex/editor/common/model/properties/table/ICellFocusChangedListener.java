package jadex.editor.common.model.properties.table;

import org.eclipse.jface.viewers.ViewerCell;

/**
 * Hook Interface!
 * @author Claas
 */
public interface ICellFocusChangedListener 
{
	public void cellFocusChanged(ViewerCell newCell, ViewerCell oldCell);
}