package jadex.tools.dfbrowser;

import javax.swing.Icon;

import jadex.bridge.service.IService;
import jadex.bridge.service.types.df.IDF;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.tools.generic.AbstractServicePlugin;


/**
 *  The df service plugin is used to wrap the df panel as JCC plugin.
 */
public class DFServicePlugin extends AbstractServicePlugin
{
	//-------- constants --------

	static
	{
		icons.put("df", SGUI.makeIcon(DFServicePlugin.class, "/jadex/tools/common/images/new_dfbrowser.png"));
		icons.put("df_sel", SGUI.makeIcon(DFServicePlugin.class, "/jadex/tools/common/images/new_dfbrowser_sel.png"));
	}

	//-------- methods --------
	
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public Class getServiceType()
	{
		return IDF.class;
	}
	
	/**
	 *  Create the service panel.
	 */
	public IFuture createServicePanel(IService service)
	{
		final Future ret = new Future();
		final DFBrowserPanel brp = new DFBrowserPanel();
		brp.init(getJCC(), service).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ret.setResult(brp);
			}
		});
		return ret;
	}
	
	/**
	 *  Get the icon.
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("df_sel"): icons.getIcon("df");
	}
}
