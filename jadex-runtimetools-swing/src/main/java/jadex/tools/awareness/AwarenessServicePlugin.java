package jadex.tools.awareness;

import javax.swing.Icon;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.tools.generic.AbstractServicePlugin;

/**
 *  The security service plugin is used to wrap the security panel as JCC plugin.
 */
public class AwarenessServicePlugin extends AbstractServicePlugin
{
	//-------- constants --------

	static
	{
		icons.put("awareness", SGUI.makeIcon(AwarenessComponentPlugin.class, "/jadex/tools/common/images/awareness.png"));
		icons.put("awareness_sel", SGUI.makeIcon(AwarenessComponentPlugin.class, "/jadex/tools/common/images/awareness_sel.png"));
	}

	//-------- methods --------
	
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public Class<?> getServiceType()
	{
		return IAwarenessManagementService.class;
	}
	
	/**
	 *  Create the service panel.
	 */
	public IFuture<IAbstractViewerPanel> createServicePanel(IService service)
	{
		final Future<IAbstractViewerPanel> ret = new Future<IAbstractViewerPanel>();
		final AwarenessAgentPanel awap = new AwarenessAgentPanel();
		awap.init(getJCC(), service).addResultListener(new ExceptionDelegationResultListener<Void, IAbstractViewerPanel>(ret)
		{
			public void customResultAvailable(Void result)
			{
				ret.setResult(awap);
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
