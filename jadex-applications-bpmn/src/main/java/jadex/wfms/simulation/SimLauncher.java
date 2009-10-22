package jadex.wfms.simulation;

import java.awt.EventQueue;

import jadex.bpmn.examples.wfms.WfmsLauncher;
import jadex.wfms.IWfms;
import jadex.wfms.service.client.IClientService;

public class SimLauncher
{
	public static void main(String[] args) throws Exception
	{
		final WfmsLauncher launcher = new WfmsLauncher();
		launcher.launchBasicWfms(args);
		EventQueue.invokeLater(new Runnable()
		{
			
			public void run()
			{
				new ClientSimulator(launcher.getClientService());
			}
		});
	}

}
