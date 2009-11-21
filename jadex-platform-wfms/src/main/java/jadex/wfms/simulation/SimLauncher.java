package jadex.wfms.simulation;

import jadex.wfms.WfmsLauncher;

import java.awt.EventQueue;

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
