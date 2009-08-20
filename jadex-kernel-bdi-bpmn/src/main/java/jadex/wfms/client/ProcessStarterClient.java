package jadex.wfms.client;

import jadex.wfms.IWfms;
import jadex.wfms.service.IAdminService;
import jadex.wfms.service.IWfmsClientService;

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
	private IWfmsClientService clientService;
	private IAdminService adminService;
	
	private JList processList;
	
	public ProcessStarterClient(IWfms wfms)
	{
		setLayout(new GridBagLayout());
		setTitle("BPMN Process Starter");
		this.clientService = (IWfmsClientService) wfms.getService(IWfmsClientService.class);
		this.adminService = (IAdminService) wfms.getService(IAdminService.class);
		
		processList = new JList(new DefaultListModel());
		for (Iterator it = clientService.getBpmnModelNames().iterator(); it.hasNext(); )
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
		
		JButton button = new JButton("Start");
		button.addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				String name = (String) processList.getSelectedValue();
				if (name != null)
					clientService.startBpmnProcess(name);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.SOUTH;
		add(button, c);
		
		button = new JButton("Add Bpmn-Process...");
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
														  		   "NewProcessName");
				String path = (String) JOptionPane.showInputDialog(ProcessStarterClient.this,
				  		   										   "Input new process path:",
				  		   										   "New Process Path",
				  		   										   JOptionPane.PLAIN_MESSAGE,
				  		   										   null,
				  		   										   null,
																   "");
				adminService.addBpmnModel(name, path);
				((DefaultListModel) processList.getModel()).clear();
				for (Iterator it = clientService.getBpmnModelNames().iterator(); it.hasNext(); )
					((DefaultListModel) processList.getModel()).addElement(it.next());
			}
		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.SOUTH;
		add(button, c);
		
		pack();
		setSize(300, 300);
		setVisible(true);
	}
	
	public String getUserName()
	{
		return "TestUser";
	}
}
