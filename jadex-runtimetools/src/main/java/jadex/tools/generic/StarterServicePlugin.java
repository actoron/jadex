package jadex.tools.generic;

import jadex.bridge.IComponentManagementService;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SGUI;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.IService;
import jadex.tools.starter.StarterViewerPanel;

import javax.swing.Icon;

/**
 * 
 */
public class StarterServicePlugin extends AbstractServicePlugin
{
	//-------- constants --------

	static
	{
		icons.put("awareness", SGUI.makeIcon(StarterServicePlugin.class, "/jadex/tools/common/images/awareness.png"));
		icons.put("awareness_sel", SGUI.makeIcon(StarterServicePlugin.class, "/jadex/tools/common/images/awareness_sel.png"));
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
		return selected? icons.getIcon("awareness_sel"): icons.getIcon("awareness");
	}
}
