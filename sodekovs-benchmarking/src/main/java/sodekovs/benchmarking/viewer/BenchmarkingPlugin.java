package sodekovs.benchmarking.viewer;

import jadex.bridge.service.IService;
import jadex.commons.future.DelegationResultListener;
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
	public IFuture createServicePanel(IService service)
	{
		final Future ret = new Future();
		final BenchmarkingPanel brp = new BenchmarkingPanel();
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
		return selected? icons.getIcon("benchmark_sel"): icons.getIcon("benchmark");
	}
}
