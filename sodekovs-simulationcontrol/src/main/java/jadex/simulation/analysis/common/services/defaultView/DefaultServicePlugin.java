package jadex.simulation.analysis.common.services.defaultView;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.service.IService;
import jadex.simulation.analysis.buildingBlocks.analysisProcess.lowLevelAnalysis.view.LowLevelAnalysisServicePlugin;
import jadex.simulation.analysis.common.services.IAnalysisService;
import jadex.tools.generic.AbstractServicePlugin;

import javax.swing.Icon;

/**
 *  Used to show a service view as JCC plugin
 */
public abstract class DefaultServicePlugin extends AbstractServicePlugin
{
	//-------- constants --------

	static
	{
		icons.put("service", SGUI.makeIcon(LowLevelAnalysisServicePlugin.class, "/jadex/tools/common/images/configure.png"));
	}

	//-------- methods --------
	
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public Class getServiceType()
	{
		return IAnalysisService.class;
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
