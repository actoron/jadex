package jadex.wfms.simulation;

import jadex.bpmn.examples.wfms.WfmsLauncher;
import jadex.wfms.IWfms;
import jadex.wfms.service.IClientService;

public class SimLauncher
{
	public static void main(String[] args) throws Exception
	{
		IWfms wfms = WfmsLauncher.launchWfms(args);
		(new Simulator((IClientService) wfms.getService(IClientService.class))).test();
	}

}
