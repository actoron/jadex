package jadex.wfms.client;

import jadex.wfms.IWfms;
import jadex.wfms.service.client.IClientService;
import jadex.wfms.service.definition.IProcessDefinitionService;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;

public class ProcessStarterClient extends JFrame implements IClient
{
	private IClientService clientService;
	
	private JList processList;
	
	public ProcessStarterClient(IClientService clntService)
	{
		setLayout(new GridBagLayout());
		setTitle("BPMN Process Starter");
		this.clientService = clntService;
		
		processList = new JList(new DefaultListModel());
		for (Iterator it = clientService.getProcessDefinitionService(this).getProcessModelNames(this).iterator(); it.hasNext(); )
			((DefaultListModel) processList.getModel()).addElement(it.next());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		add(processList, c);
		
		JButton button = new JButton("Add Process...");
		button.addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				String name = (String) JOptionPane.showInputDialog(ProcessStarterClient.this,
														  		   "Input new process name:",
														  		   "New Process Name",
														  		   JOptionPane.PLAIN_MESSAGE,
														  		   null,
														  		   null,
														  		   "New Process Path");
				String path = (String) JOptionPane.showInputDialog(ProcessStarterClient.this,
				  		   										   "Input new process path:",
				  		   										   "New Process Path",
				  		   										   JOptionPane.PLAIN_MESSAGE,
				  		   										   null,
				  		   										   null,
																   "");
//				adminService.addProcessModel(ProcessStarterClient.this, name, path);
				clientService.getProcessDefinitionService(ProcessStarterClient.this).addProcessModel(ProcessStarterClient.this, path);
				((DefaultListModel) processList.getModel()).clear();
				for (Iterator it = clientService.getProcessDefinitionService(ProcessStarterClient.this).getProcessModelNames(ProcessStarterClient.this).iterator(); it.hasNext(); )
					((DefaultListModel) processList.getModel()).addElement(it.next());
			}
		});
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		add(button, c);
		
		button = new JButton("Start Process");
		button.addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				String name = (String) processList.getSelectedValue();
				if (name != null)
					clientService.startProcess(ProcessStarterClient.this, name);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		add(button, c);
		
		pack();
		setSize(700, 300);
		setVisible(true);
	}
	
	public String getUserName()
	{
		return "TestUser";
	}
}
