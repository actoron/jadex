package jadex.bdi.examples.booktrading.common;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;
import jadex.bdi.runtime.IBDIExternalAccess;

import javax.swing.JComponent;

/**
 *  Panel for the customer view.
 */
public class GuiViewerPanel extends AbstractComponentViewerPanel
{
	//-------- methods --------
	
	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent()
	{
		return new GuiPanel((IBDIExternalAccess)component);
	}
}