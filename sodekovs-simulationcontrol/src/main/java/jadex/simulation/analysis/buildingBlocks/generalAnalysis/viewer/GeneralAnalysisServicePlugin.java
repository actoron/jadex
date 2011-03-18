package jadex.simulation.analysis.buildingBlocks.generalAnalysis.viewer;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.bridge.service.IService;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.IGeneralAnalysisService;
import jadex.tools.generic.AbstractServicePlugin;

import javax.swing.Icon;

/**
 *  Used to show the general analysis service view
 */
public class GeneralAnalysisServicePlugin extends AbstractServicePlugin
{
	//-------- constants --------

	static
	{
		icons.put("service", SGUI.makeIcon(GeneralAnalysisServicePlugin.class, "/jadex/tools/common/images/configure.png"));
	}

	//-------- methods --------
	
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public Class getServiceType()
	{
		return IGeneralAnalysisService.class;
	}
	
	/**
	 *  Create the service panel.
	 */
	public IFuture createServicePanel(IService service)
	{
		GeneralAnalysisServiceViewerPanel view= new GeneralAnalysisServiceViewerPanel();
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
