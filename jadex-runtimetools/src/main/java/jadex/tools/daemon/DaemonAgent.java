package jadex.tools.daemon;

import jadex.bridge.IArgument;
import jadex.commons.SUtil;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

import javax.swing.SwingUtilities;

/**
 * 
 */
public class DaemonAgent extends MicroAgent
{
	//-------- attributes --------
	
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		addService(new DaemonAgentService(getExternalAccess()));
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				DaemonPanel.createGui(getExternalAccess());
			}
		});
	}
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent offers the daemon service.", null, 
			new IArgument[]{}//new Argument("infos", "Initial information records.", "InformationEntry[]")}
			, null, null, SUtil.createHashMap(new String[]{"componentviewer.viewerclass"}, new Object[]{"jadex.tools.daemon.DaemonViewerPanel"}));
	}

}
