package jadex.wfms.bdi.client.basic;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IExternalAccess;
import jadex.commons.SGUI;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;

public class BasicClientApplication
{
	private static final String WINDOW_TITLE = "Workflow Client Application";
	
	private static final String WORKITEM_LIST_TAB_NAME = "Workitem List";
	
	private static final String PROCESS_MODEL_TAB_NAME = "Process Model Center";
	
	private static final JPanel EMPTY_PANEL = new JPanel();
	
	private IBDIExternalAccess agent;
	
	private JFrame mainFrame;
	
	private JSplitPane mainSplitPane;
	
	private WorkitemListComponent wlComponent;
	
	private ProcessModelComponent pmComponent;
	
	public BasicClientApplication(IExternalAccess appAgent)
	{
		this.agent = (IBDIExternalAccess) appAgent;
		
		EventQueue.invokeLater(new Runnable()
		{
			
			public void run()
			{
				
				mainFrame = new JFrame(WINDOW_TITLE);
				mainFrame.addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						agent.killAgent();
						mainFrame.dispose();
					}
				});
				
				mainSplitPane = new JSplitPane();
				mainSplitPane.setOneTouchExpandable(true);
				mainFrame.getContentPane().add(mainSplitPane);
				mainSplitPane.setRightComponent(EMPTY_PANEL);
				
				JTabbedPane leftTabPane = new JTabbedPane();
				mainSplitPane.setLeftComponent(leftTabPane);
				
				wlComponent = new WorkitemListComponent();
				leftTabPane.add(WORKITEM_LIST_TAB_NAME, wlComponent);
				
				pmComponent = new ProcessModelComponent();
				leftTabPane.add(PROCESS_MODEL_TAB_NAME, pmComponent);
				
				mainFrame.pack();
				mainFrame.setSize(800, 550);
				mainFrame.setLocation(SGUI.calculateMiddlePosition(mainFrame));
				mainFrame.setVisible(true);
				mainSplitPane.setDividerLocation(0.4);
				
				setAgentActions();
				
				boolean connected = false;
				while (!connected)
				{
					LoginDialog loginDialog = new LoginDialog(mainFrame);
					loginDialog.setLocation(SGUI.calculateMiddlePosition(loginDialog));
					loginDialog.setVisible(true);
					connected = true;
					try
					{
						connect(loginDialog.getUserName(), loginDialog.getPassword());
					}
					catch (GoalFailureException e)
					{
						connected = false;
					}
				}
				
				initializeWorkitemList();
				updateProcessModels();
				setActions();
			}
		});
		
	}
	
	private void setActions()
	{
		pmComponent.setStartAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				String processName = pmComponent.getSelectedModelName();
				if (processName != null)
				{
					IGoal startProcess = agent.createGoal("clientcap.start_process");
					startProcess.getParameter("process_name").setValue(processName);
					try
					{
						agent.dispatchTopLevelGoalAndWait(startProcess);
					}
					catch (GoalFailureException e1)
					{
						JOptionPane.showMessageDialog(mainFrame, "Process Start failed.");
					}
				}
			}
		});
		
		wlComponent.setBeginActivityAction(new AbstractAction()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				IWorkitem wi = wlComponent.getSelectedWorkitem();
				if (wi != null)
				{
					IGoal beginActivity = agent.createGoal("clientcap.begin_activity");
					beginActivity.getParameter("workitem").setValue(wi);
					try
					{
						agent.dispatchTopLevelGoalAndWait(beginActivity);
					}
					catch (GoalFailureException e1)
					{
						JOptionPane.showMessageDialog(mainFrame, "Start of activity failed.");
					}
					
					IClientActivity activity = (IClientActivity) beginActivity.getParameter("activity").getValue();
					
					ActivityComponent ac = new ActivityComponent(activity);
					
					mainSplitPane.setRightComponent(ac);
				}
			}
		});
	}
	
	private void setAgentActions()
	{
		Action wiAdded = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				IWorkitem wi = (IWorkitem) e.getSource();
				wlComponent.addWorkitem(wi);
			}
		};
		agent.getBeliefbase().getBelief("add_workitem_controller").setFact(wiAdded);
		
		Action wiRemoved = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				IWorkitem wi = (IWorkitem) e.getSource();
				wlComponent.removeWorkitem(wi);
			}
		};
		agent.getBeliefbase().getBelief("remove_workitem_controller").setFact(wiRemoved);
	}
	
	private void connect(String userName, Object authToken)
	{
		IGoal connect = agent.createGoal("clientcap.connect");
		connect.getParameter("user_name").setValue(userName);
		connect.getParameter("auth_token").setValue(authToken);
		agent.dispatchTopLevelGoalAndWait(connect);
	}
	
	private void initializeWorkitemList()
	{
		IGoal subscribe = agent.createGoal("clientcap.start_workitem_subscription");
		agent.dispatchTopLevelGoalAndWait(subscribe);
		
		IGoal requestListGoal = agent.createGoal("clientcap.request_workitem_list");
		agent.dispatchTopLevelGoalAndWait(requestListGoal);
		Set workitemList = (Set) requestListGoal.getParameter("workitem_list").getValue();
		wlComponent.setWorkitems(workitemList);
	}
	
	private void updateProcessModels()
	{
		IGoal modelNameGoal = agent.createGoal("clientcap.request_model_names");
		agent.dispatchTopLevelGoalAndWait(modelNameGoal);
		Set modelNames = (Set) modelNameGoal.getParameter("model_names").getValue();
		pmComponent.setProcessModelNames(modelNames);
	}
}

