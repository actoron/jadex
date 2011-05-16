package jadex.simulation.analysis.buildingBlocks.dataEngineering.viewer;

import jadex.bridge.service.IService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.simulation.analysis.buildingBlocks.dataEngineering.IEngineerDataObjectService;
import jadex.simulation.analysis.common.services.defaultView.DefaultServiceViewerPanel;
import jadex.tools.generic.AbstractServicePlugin;

import javax.swing.Icon;

/**
 *  Used to show the general analysis service view
 */
public class EngineerDataObjectServicePlugin extends AbstractServicePlugin
{
	//-------- constants --------

	static
	{
		icons.put("service", SGUI.makeIcon(EngineerDataObjectServicePlugin.class, "/jadex/tools/common/images/configure.png"));
	}

	//-------- methods --------
	
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public Class getServiceType()
	{
		return IEngineerDataObjectService.class;
	}
	
	/**
	 *  Create the service panel.
	 */
	public IFuture createServicePanel(IService service)
	{
		DefaultServiceViewerPanel view= new DefaultServiceViewerPanel();
		view.init(getJCC(), service);
		return new Future(null);
	}
	
	/**
	 *  Get the icon.
	 */
	public Icon getToolIcon(boolean selected)
	{
		return icons.getIcon("service");
	}
}
