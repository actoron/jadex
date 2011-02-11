package jadex.simulation.analysis.buildingBlocks.simulation.viewer;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.service.IService;
import jadex.commons.service.library.ILibraryService;

import javax.swing.Icon;

/**
 *  Used to show the service view as JCC plugin
 */
public class ExecuteExperimentServicePlugin extends AbstractServicePlugin
{
	//-------- constants --------

	static
	{
		icons.put("service", SGUI.makeIcon(ExecuteExperimentServicePlugin.class, "/jadex/tools/common/images/configure.png"));
	}

	//-------- methods --------
	
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public Class getServiceType()
	{
		return ILibraryService.class;
	}
	
	/**
	 *  Create the service panel.
	 */
	public IFuture createServicePanel(IService service)
	{
		BasicExecuteExperimentServiceView view= new BasicExecuteExperimentServiceView();
		view.init(getJCC(), service);
		return new Future(view);
	}
	
	/**
	 *  Get the icon.
	 */
	public Icon getToolIcon(boolean selected)
	{
		return icons.getIcon("service");
	}
}
