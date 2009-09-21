package jadex.wfms.simulation;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import jadex.bpmn.examples.wfms.WfmsLauncher;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IClientService;
import jadex.wfms.simulation.gui.SimulationWindow;

public class ClientSimulator implements IClient
{
	private IClientService clientService;
	
	private SimulationWindow simWindow;
	
	private String userName;
	
	public ClientSimulator(IClientService clientService)
	{
		this(clientService, "TestUser");
	}
	
	public ClientSimulator(IClientService clientService, String userName)
	{
		this.clientService = clientService;
		simWindow = new SimulationWindow();
		
		simWindow.addWindowListener(new WindowAdapter()
		{
			public void windowClosed(WindowEvent e)
			{
				System.exit(0);
			}
		});
		
		setupActions();
	}
	
	public IClientService getClientService()
	{
		return clientService;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public void startSimulation()
	{
		
	}
	
	private void setupActions()
	{
		simWindow.setOpenAction(new AbstractAction()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				Set modelNames = clientService.getModelNames(ClientSimulator.this);
				String modelName = simWindow.showProcessPickerDialog(modelNames);
				ClientProcessMetaModel model = new ClientProcessMetaModel();
				try
				{
					model.setRootModel(clientService.getProcessDefinitionService(ClientSimulator.this).getProcessModel(ClientSimulator.this, modelName));
					simWindow.setProcessTreeModel(model);
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
					simWindow.showMessage(JOptionPane.ERROR_MESSAGE, "Cannot open the process", "Opening the process failed.");
				}
			}
		});
		
		simWindow.setCloseAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				simWindow.setProcessTreeModel(null);
			}
		});
	}
}
