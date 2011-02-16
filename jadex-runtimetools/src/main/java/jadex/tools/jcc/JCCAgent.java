package jadex.tools.jcc;

import jadex.base.gui.componentviewer.ComponentViewerPlugin;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IMessageService;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.commons.future.IFuture;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.library.ILibraryService;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.tools.comanalyzer.ComanalyzerPlugin;
import jadex.tools.convcenter.ConversationPlugin;
import jadex.tools.debugger.DebuggerPlugin;
import jadex.tools.deployer.DeployerPlugin;
import jadex.tools.dfbrowser.DFServicePlugin;
import jadex.tools.generic.AwarenessComponentPlugin;
import jadex.tools.libtool.LibraryServicePlugin;
import jadex.tools.simcenter.SimCenterPlugin;
import jadex.tools.starter.StarterServicePlugin;
import jadex.tools.testcenter.TestCenterPlugin;

/**
 *  Micro component for opening the JCC gui.
 */
@Description("Micro component for opening the JCC gui.")
@RequiredServices(
{
	@RequiredService(name="cms", type=IComponentManagementService.class, scope=RequiredServiceInfo.SCOPE_PLATFORM),
	@RequiredService(name="rms", type=IRemoteServiceManagementService.class, scope=RequiredServiceInfo.SCOPE_PLATFORM),
	@RequiredService(name="libservice", type=ILibraryService.class, scope=RequiredServiceInfo.SCOPE_PLATFORM),
	@RequiredService(name="messageservice", type=IMessageService.class, scope=RequiredServiceInfo.SCOPE_PLATFORM)
})
public class JCCAgent extends MicroAgent
{
	/**
	 *  Open the gui on agent startup.
	 */
	public IFuture	agentCreated()
	{
		new ControlCenter(getExternalAccess(),
//			StarterPlugin.class.getName()
			StarterServicePlugin.class.getName()	
			+ " "+DFServicePlugin.class.getName()
			+ " "+ConversationPlugin.class.getName()
			+ " "+ComanalyzerPlugin.class.getName()
			+ " "+TestCenterPlugin.class.getName()
			// + " "+JadexdocPlugin.class.getName()
			+ " "+SimCenterPlugin.class.getName()
			+ " "+DebuggerPlugin.class.getName()
			// + " "+RuleProfilerPlugin.class.getName()
			+ " "+LibraryServicePlugin.class.getName()
			+ " "+AwarenessComponentPlugin.class.getName()
			+ " "+ComponentViewerPlugin.class.getName()
			+ " "+DeployerPlugin.class.getName()
//			+ " "+StarterServicePlugin.class.getName()		
		);
		return IFuture.DONE;
	}
}
