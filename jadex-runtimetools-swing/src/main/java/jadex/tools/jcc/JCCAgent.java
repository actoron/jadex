package jadex.tools.jcc;

import jadex.base.gui.componentviewer.ComponentViewerPlugin;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;
import jadex.tools.awareness.AwarenessComponentPlugin;
import jadex.tools.chat.ChatPlugin;
import jadex.tools.debugger.DebuggerPlugin;
import jadex.tools.security.SecurityServicePlugin;
import jadex.tools.simcenter.SimulationServicePlugin;
import jadex.tools.starter.StarterPlugin;
import jadex.tools.testcenter.TestCenterPlugin;

/**
 *  Micro component for opening the JCC gui.
 */
@Description("Micro component for opening the JCC gui.")
@Arguments(@Argument(name="saveonexit", clazz=boolean.class, defaultvalue="true", 
	description="Save settings on exit?"))
public class JCCAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The saveonexit argument. */
	@AgentArgument
	protected boolean	saveonexit;
	
	/** The control center. */
	protected ControlCenter	cc;
	
	//-------- micro agent methods --------
	
	/**
	 *  Open the gui on agent startup.
	 */
	public IFuture<Void>	agentCreated()
	{
		Future<Void>	ret	= new Future<Void>();
		this.cc	= new ControlCenter();
		cc.init(getExternalAccess(),
			new String[]
			{
				StarterPlugin.class.getName(),
				ChatPlugin.class.getName(),
//				StarterServicePlugin.class.getName(),
//				DFServicePlugin.class.getName(),
//				ConversationPlugin.class.getName(),
//				"jadex.tools.comanalyzer.ComanalyzerPlugin",
				TestCenterPlugin.class.getName(),
//				JadexdocPlugin.class.getName(),
				SimulationServicePlugin.class.getName(),
				DebuggerPlugin.class.getName(),
//				RuleProfilerPlugin.class.getName(),
//				LibraryServicePlugin.class.getName(),
				AwarenessComponentPlugin.class.getName(),
				ComponentViewerPlugin.class.getName(),
				SecurityServicePlugin.class.getName()
//				DeployerPlugin.class.getName()
			},
		saveonexit).addResultListener(createResultListener(new DelegationResultListener<Void>(ret)));
		return ret;
	}
	
	/**
	 *  Close the gui on agent shutdown.
	 */
	public IFuture<Void>	agentKilled()
	{
//		System.out.println("JCC agent killed");
		Future<Void>	ret	= new Future<Void>();
		cc.shutdown().addResultListener(createResultListener(new DelegationResultListener<Void>(ret)));
//		ret.addResultListener(new IResultListener()
//		{
//			public void resultAvailable(Object result)
//			{
//				System.out.println("r1");
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("r2");
//			}
//		});
		return ret;
	}
	
	/**
	 *  Get the control center.
	 */
	// Used for test case.
	public ControlCenter	getControlCenter()
	{
		return cc;
	}
}
