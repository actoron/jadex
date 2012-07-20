package sodekovs.benchmarking.viewer;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bridge.service.IService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.tools.generic.AbstractServicePlugin;

import javax.swing.Icon;

import sodekovs.benchmarking.services.IBenchmarkingManagementService;


/**
 *  The benchmarking service plugin is used to wrap the benchmarking panel as JCC plugin.
 */
public class BenchmarkingPlugin extends AbstractServicePlugin
{
	//-------- constants --------

	static
	{
		icons.put("benchmark", SGUI.makeIcon(BenchmarkingPlugin.class, "/sodekovs/benchmarking/viewer/images/benchmark2.png"));
		icons.put("benchmark_sel", SGUI.makeIcon(BenchmarkingPlugin.class, "/sodekovs/benchmarking/viewer/images/benchmark_sel.png"));
	}

	//-------- methods --------
	
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public Class getServiceType()
	{
		return IBenchmarkingManagementService.class;
	}
	
	/**
	 *  Create the service panel.
	 */
	public IFuture<IAbstractViewerPanel> createServicePanel(IService service)
	{
//		System.out.println("Starting Benchmarking-Plugin?");
		final Future<IAbstractViewerPanel> ret = new Future<IAbstractViewerPanel>();
		final BenchmarkingPanel brp = new BenchmarkingPanel();
		brp.init(getJCC(), service).addResultListener(new ExceptionDelegationResultListener<Void, IAbstractViewerPanel>(ret)
		{
			public void customResultAvailable(Void result)
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
		return selected? icons.getIcon("benchmark_sel"): icons.getIcon("benchmark");
	}
}
