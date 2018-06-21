package jadex.tools.libtool;

import javax.swing.Icon;

import jadex.bridge.service.IService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.tools.generic.AbstractServicePlugin;

/**
 *  The library service plugin is used to wrap the library panel as JCC plugin.
 */
public class LibraryServicePlugin extends AbstractServicePlugin
{
	//-------- constants --------

	static
	{
		icons.put("library", SGUI.makeIcon(LibraryServicePlugin.class, "/jadex/tools/common/images/libcenter.png"));
		icons.put("library_sel", SGUI.makeIcon(LibraryServicePlugin.class, "/jadex/tools/common/images/libcenter_sel.png"));
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
		final Future ret = new Future();
		final LibServiceBrowser lib = new LibServiceBrowser();
		lib.init(getJCC(), service).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ret.setResult(lib);
			}
		});
		return ret;
	}
	
	/**
	 *  Get the icon.
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("library_sel"): icons.getIcon("library");
	}
}
