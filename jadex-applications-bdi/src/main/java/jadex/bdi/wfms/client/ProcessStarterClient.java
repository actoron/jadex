package jadex.bdi.wfms.client;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.wfms.ontology.RequestAuth;
import jadex.bridge.IExternalAccess;
import jadex.wfms.service.IClientService;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;

public class ProcessStarterClient extends JFrame
{
	private IBDIExternalAccess agent;
	
	private JList processList;
	
	public ProcessStarterClient(IExternalAccess agent)//IClientService clntService)
	{
		setLayout(new GridBagLayout());
		setTitle("Process Starter");
		this.agent = (IBDIExternalAccess) agent;
		//this.clientService = clntService;
		//clientService.authenticate(this);
		processList = new JList(new DefaultListModel());
		//for (Iterator it = clientService.getProcessDefinitionService(this).getProcessModelNames(this).iterator(); it.hasNext(); )
		//	((DefaultListModel) processList.getModel()).addElement(it.next());
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
				/*String name = (String) JOptionPane.showInputDialog(ProcessStarterClient.this,
														  		   "Input new process name:",
														  		   "New Process Name",
														  		   JOptionPane.PLAIN_MESSAGE,
														  		   null,
														  		   null,
														  		   "New Process Path");*/
				String path = (String) JOptionPane.showInputDialog(ProcessStarterClient.this,
				  		   										   "Enter new process path:",
				  		   										   "New Process Path",
				  		   										   JOptionPane.PLAIN_MESSAGE,
				  		   										   null,
				  		   										   null,
																   "");
//				adminService.addProcessModel(ProcessStarterClient.this, name, path);
				//clientService.getProcessDefinitionService(ProcessStarterClient.this).addProcessModel(ProcessStarterClient.this, path);
				addProcess(path);
				((DefaultListModel) processList.getModel()).clear();
				refreshProcessList();
				//for (Iterator it = clientService.getProcessDefinitionService(ProcessStarterClient.this).getProcessModelNames(ProcessStarterClient.this).iterator(); it.hasNext(); )
					//((DefaultListModel) processList.getModel()).addElement(it.next());
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
					startProcess(name);
					//clientService.startProcess(ProcessStarterClient.this, name);
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
		
		EventQueue.invokeLater(new Runnable()
		{
			
			public void run()
			{
				connect("TestUser");
				refreshProcessList();
			}
		});
	}
	
	private void connect(String userName)
	{
		IGoal connect = agent.createGoal("clientcap.connect");
		connect.getParameter("user_name").setValue("TestUser");
		agent.dispatchTopLevelGoalAndWait(connect);
	}
	
	private void refreshProcessList()
	{
		IGoal modelNameReq = agent.createGoal("clientcap.request_model_names");
		try
		{
			agent.dispatchTopLevelGoalAndWait(modelNameReq);
		}
		catch (GoalFailureException e)
		{
			e.printStackTrace();
			return;
		}
		Set modelNames = (Set) modelNameReq.getParameter("model_names").getValue();
		for (Iterator it = modelNames.iterator(); it.hasNext(); )
			((DefaultListModel) processList.getModel()).addElement(it.next());
	}
	
	private void startProcess(String name)
	{
		IGoal start = agent.createGoal("clientcap.start_process");
		start.getParameter("process_name").setValue(name);
		agent.dispatchTopLevelGoalAndWait(start);
	}
	
	private void addProcess(String path)
	{
		IGoal add = agent.createGoal("clientcap.add_process");
		add.getParameter("process_path").setValue(path);
		try
		{
			agent.dispatchTopLevelGoalAndWait(add);
		}
		catch (GoalFailureException e)
		{
			JOptionPane.showMessageDialog(this, "Error while adding the process.");
		}
	}
}
