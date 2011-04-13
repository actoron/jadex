package jadex.benchmarking.viewer;

import jadex.base.gui.componentviewer.ComponentViewerPlugin;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.tools.awareness.AwarenessComponentPlugin;
import jadex.tools.comanalyzer.ComanalyzerPlugin;
import jadex.tools.convcenter.ConversationPlugin;
import jadex.tools.debugger.DebuggerPlugin;
import jadex.tools.deployer.DeployerPlugin;
import jadex.tools.dfbrowser.DFServicePlugin;
import jadex.tools.jcc.ControlCenter;
import jadex.tools.libtool.LibraryServicePlugin;
import jadex.tools.simcenter.SimulationServicePlugin;
import jadex.tools.starter.StarterPlugin;
import jadex.tools.testcenter.TestCenterPlugin;

/**
 *  Micro component for opening the JCC gui.
 */
@Description("Micro component for opening the JCC gui.")
public class JCCAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The control center. */
	protected ControlCenter	cc;
	
	//-------- micro agent methods --------
	
	/**
	 *  Open the gui on agent startup.
	 */
	public IFuture	agentCreated()
	{
		Future	ret	= new Future();
		this.cc	= new ControlCenter();
		cc.init(getExternalAccess(),
			new String[]{
				StarterPlugin.class.getName(),
//				StarterServicePlugin.class.getName(),
				DFServicePlugin.class.getName(),
				ConversationPlugin.class.getName(),
				ComanalyzerPlugin.class.getName(),
				TestCenterPlugin.class.getName(),
//				JadexdocPlugin.class.getName(),
				SimulationServicePlugin.class.getName(),
				DebuggerPlugin.class.getName(),
//				RuleProfilerPlugin.class.getName(),
				LibraryServicePlugin.class.getName(),
				AwarenessComponentPlugin.class.getName(),
				ComponentViewerPlugin.class.getName(),
				BenchmarkingPlugin.class.getName(),
				DeployerPlugin.class.getName()
			}
		).addResultListener(createResultListener(new DelegationResultListener(ret)));
		return ret;
	}
	
	/**
	 *  Close the gui on agent shutdown.
	 */
	public IFuture	agentKilled()
	{
		Future	ret	= new Future();
		cc.shutdown().addResultListener(createResultListener(new DelegationResultListener(ret)));
		return ret;
	}
}
