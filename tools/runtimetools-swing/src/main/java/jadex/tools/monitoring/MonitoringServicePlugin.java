package jadex.tools.monitoring;

import javax.swing.Icon;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.monitoring.IMonitoringService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.tools.generic.AbstractServicePlugin;

/**
 *  The security service plugin is used to wrap the security panel as JCC plugin.
 */
public class MonitoringServicePlugin extends AbstractServicePlugin
{
	//-------- constants --------

	static
	{
		icons.put("security", SGUI.makeIcon(MonitoringServicePlugin.class, "/jadex/tools/common/images/security.png"));
		icons.put("security_sel", SGUI.makeIcon(MonitoringServicePlugin.class, "/jadex/tools/common/images/security_sel.png"));
	}

	//-------- methods --------
	
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public Class<?> getServiceType()
	{
		return IMonitoringService.class;
	}
	
	/**
	 *  Create the service panel.
	 */
	public IFuture<IAbstractViewerPanel> createServicePanel(IService service)
	{
		final Future<IAbstractViewerPanel> ret = new Future<IAbstractViewerPanel>();
		final MonitoringPanel ss = new MonitoringPanel();
		ss.init(getJCC(), service).addResultListener(new ExceptionDelegationResultListener<Void, IAbstractViewerPanel>(ret)
		{
			public void customResultAvailable(Void result)
			{
				ret.setResult(ss);
			}
		});
		return ret;
	}
	
	/**
	 *  Get the icon.
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("security_sel"): icons.getIcon("security");
	}
}
