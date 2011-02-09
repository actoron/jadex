package jadex.tools.generic;

import jadex.bridge.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.service.IService;
import jadex.tools.starter.StarterViewerPanel;

import javax.swing.Icon;

/**
 *  Plugin for starting components.
 */
public class StarterServicePlugin extends AbstractServicePlugin
{
	//-------- constants --------

	static
	{
		icons.put("starter", SGUI.makeIcon(StarterServicePlugin.class, "/jadex/tools/common/images/new_starter.png"));
		icons.put("starter_sel", SGUI.makeIcon(StarterServicePlugin.class, "/jadex/tools/common/images/new_starter_sel.png"));
	}

	//-------- methods --------
	
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public Class getServiceType()
	{
		return IComponentManagementService.class;
	}
	
	/**
	 *  Create the component panel.
	 */
	public IFuture createServicePanel(IService service)
	{
		final Future ret = new Future();
		final StarterViewerPanel stp = new StarterViewerPanel();
		stp.init(getJCC(), service).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ret.setResult(stp);
			}
		});
		return ret;
	}
	
	/**
	 *  Get the icon.
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("starter_sel"): icons.getIcon("starter");
	}

}
