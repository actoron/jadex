package jadex.wfms.client;

import jadex.wfms.IWfms;
import jadex.wfms.service.IProcessDefinitionService;
import jadex.wfms.service.IClientService;

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
	
	private JList bpmnProcessList;
	
	private JList gpmnProcessList;
	
	public ProcessStarterClient(IClientService clntService)
	{
		setLayout(new GridBagLayout());
		setTitle("BPMN Process Starter");
		this.clientService = clntService;
		
		bpmnProcessList = new JList(new DefaultListModel());
		for (Iterator it = clientService.getProcessDefinitionService(this).getBpmnModelNames(this).iterator(); it.hasNext(); )
			((DefaultListModel) bpmnProcessList.getModel()).addElement(it.next());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		add(bpmnProcessList, c);
		
		gpmnProcessList = new JList(new DefaultListModel());
		for (Iterator it = clientService.getProcessDefinitionService(this).getGpmnModelNames(this).iterator(); it.hasNext(); )
			((DefaultListModel) gpmnProcessList.getModel()).addElement(it.next());
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		add(gpmnProcessList, c);
		
		JButton button = new JButton("Add BPMN Process...");
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
														  		   "New BPMN Process");
				String path = (String) JOptionPane.showInputDialog(ProcessStarterClient.this,
				  		   										   "Input new process path:",
				  		   										   "New Process Path",
				  		   										   JOptionPane.PLAIN_MESSAGE,
				  		   										   null,
				  		   										   null,
																   "");
//				adminService.addProcessModel(ProcessStarterClient.this, name, path);
				clientService.getProcessDefinitionService(ProcessStarterClient.this).addBpmnModel(ProcessStarterClient.this, path);
				((DefaultListModel) bpmnProcessList.getModel()).clear();
				for (Iterator it = clientService.getProcessDefinitionService(ProcessStarterClient.this).getBpmnModelNames(ProcessStarterClient.this).iterator(); it.hasNext(); )
					((DefaultListModel) bpmnProcessList.getModel()).addElement(it.next());
			}
		});
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		add(button, c);
		
		button = new JButton("Start BPMN Process");
		button.addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				String name = (String) bpmnProcessList.getSelectedValue();
				if (name != null)
					clientService.startBpmnProcess(ProcessStarterClient.this, name);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		add(button, c);
		
		
		button = new JButton("Add GPMN Process...");
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
				  		   										   "New GPMN Process");
				String path = (String) JOptionPane.showInputDialog(ProcessStarterClient.this,
							   									   "Input new process path:",
							   									   "New Process Path",
							   									   JOptionPane.PLAIN_MESSAGE,
							   									   null,
							   									   null,
							   									   "");
//				adminService.addProcessModel(ProcessStarterClient.this, name, path);
				clientService.getProcessDefinitionService(ProcessStarterClient.this).addGpmnModel(ProcessStarterClient.this, path);
				((DefaultListModel) gpmnProcessList.getModel()).clear();
				for (Iterator it = clientService.getProcessDefinitionService(ProcessStarterClient.this).getGpmnModelNames(ProcessStarterClient.this).iterator(); it.hasNext(); )
					((DefaultListModel) gpmnProcessList.getModel()).addElement(it.next());
			}
		});
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		add(button, c);
		
		button = new JButton("Start GPMN Process");
		button.addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				String name = (String) gpmnProcessList.getSelectedValue();
				if (name != null)
					clientService.startGpmnProcess(ProcessStarterClient.this, name);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 1;
		c.weightx = 0;
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
