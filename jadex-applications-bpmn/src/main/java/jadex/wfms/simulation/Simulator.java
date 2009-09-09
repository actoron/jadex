package jadex.wfms.simulation;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import jadex.wfms.client.IClient;
import jadex.wfms.service.IClientService;
import jadex.wfms.simulation.gui.SimulationWindow;

public class Simulator implements IClient
{
	private IClientService clientService;
	
	private SimulationWindow simWindow;
	
	private String userName;
	
	public Simulator(IClientService clientService)
	{
		this(clientService, "TestUser");
	}
	
	public Simulator(IClientService clientService, String userName)
	{
		this.clientService = clientService;
		simWindow = new SimulationWindow(this);
		
		simWindow.addWindowListener(new WindowAdapter()
		{
			public void windowClosed(WindowEvent e)
			{
				System.exit(0);
			}
		});
	}
	
	public IClientService getClientService()
	{
		return clientService;
	}
	
	public String getUserName()
	{
		return userName;
	}
}
