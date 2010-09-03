package jadex.tools.generic;

import jadex.base.fipa.IDF;
import jadex.base.gui.componentviewer.dfservice.DFBrowserPanel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SGUI;
import jadex.commons.service.IService;

import javax.swing.Icon;


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
		DFBrowserPanel ret = new DFBrowserPanel();
		ret.init(getJCC(), service);
		return new Future(ret);
	}
	
	/**
	 *  Get the icon.
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("df_sel"): icons.getIcon("df");
	}
}
