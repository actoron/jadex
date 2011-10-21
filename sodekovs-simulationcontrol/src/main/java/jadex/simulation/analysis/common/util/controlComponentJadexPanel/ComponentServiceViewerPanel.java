package jadex.simulation.analysis.common.util.controlComponentJadexPanel;

import jadex.base.gui.componentviewer.DefaultComponentServiceViewerPanel;
import jadex.commons.Properties;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import javax.swing.JComponent;

/**
 *  Extended DefaultComponentServiceViewerPanel
 *  @author haubeck
 */
public class ComponentServiceViewerPanel extends DefaultComponentServiceViewerPanel
{

	@Override
	public IFuture getProperties()
	{
		return new Future(null);
	}

	/**
	 *  The componentP to be shown in the gui.
	 *  @return	The componentP to be displayed.
	 */
	public JComponent getComponent()
	{
		return panel;
	}
	
	@Override
	public IFuture setProperties(Properties ps)
	{
		return new Future(null);
	}
	
	
}
