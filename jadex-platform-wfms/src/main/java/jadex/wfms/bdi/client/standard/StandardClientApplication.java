package jadex.wfms.bdi.client.standard;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IExternalAccess;
import jadex.commons.SGUI;
import jadex.wfms.bdi.client.standard.parametergui.ActivityComponent;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.parametertypes.Document;

public class StandardClientApplication
{
	private static final String WINDOW_TITLE = "Workflow Client Application";
	
	private static final String WORKITEM_LIST_TAB_NAME = "Workitem List";
	
	private static final String PROCESS_MODEL_TAB_NAME = "Process Models";
	
	private static final JPanel EMPTY_PANEL = new JPanel();
	
	private IBDIExternalAccess agent;
	
	private JFrame mainFrame;
	
	private JSplitPane mainSplitPane;
	
	private WorkitemListComponent wlComponent;
	
	private ProcessModelComponent pmComponent;
	
	boolean connected;
	
	public StandardClientApplication(IExternalAccess appAgent)
	{
		connected = false;
		this.agent = (IBDIExternalAccess) appAgent;
		
		agent.addAgentListener(new IAgentListener()
		{
			public void agentTerminating(AgentEvent ae)
			{
			}
			
			public void agentTerminated(AgentEvent ae)
			{
				EventQueue.invokeLater(new Runnable()
				{
					
					public void run()
					{
						mainFrame.dispose();
					}
				});
			}
		});
		
		EventQueue.invokeLater(new Runnable()
		{
			
			public void run()
			{
				
				mainFrame = new JFrame(WINDOW_TITLE);
				mainFrame.addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						if (connected)
							disconnect();
						agent.killAgent();
						mainFrame.dispose();
					}
				});
				
				mainSplitPane = new JSplitPane();
				mainSplitPane.setOneTouchExpandable(true);
				mainFrame.getContentPane().add(mainSplitPane);
				mainSplitPane.setRightComponent(new JTabbedPane());
				
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
				
				while (!connected)
				{
					LoginDialog loginDialog = new LoginDialog(mainFrame);
					loginDialog.setLocation(SGUI.calculateMiddlePosition(loginDialog));
					loginDialog.setVisible(true);
					try
					{
						connect(loginDialog.getUserName(), loginDialog.getPassword());
						connected = true;
					}
					catch (GoalFailureException e)
					{
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
					
					if (mainSplitPane.getRightComponent() instanceof ActivityComponent)
					{
						ActivityComponent currentAc = (ActivityComponent) mainSplitPane.getRightComponent();
						//TODO: Cancel activity
						mainSplitPane.setRightComponent(EMPTY_PANEL);
					}
					
					IClientActivity activity = (IClientActivity) beginActivity.getParameter("activity").getValue();
					ActivityComponent ac = createActivityComponent(activity);
					
					if (mainSplitPane.getRightComponent().equals(EMPTY_PANEL))
					{
						mainSplitPane.setRightComponent(ac);
					}
					else if (mainSplitPane.getRightComponent() instanceof JTabbedPane)
					{
						JTabbedPane tabPane = (JTabbedPane) mainSplitPane.getRightComponent();
						tabPane.add(ac, ac.getActivity().getName());
						tabPane.setSelectedComponent(ac);
					}
					
					
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
	
	private void disconnect()
	{
		try
		{
			IGoal disconnect = agent.createGoal("clientcap.disconnect");
			agent.dispatchTopLevelGoalAndWait(disconnect);
		}
		catch (GoalFailureException e)
		{
		}
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
	
	private ActivityComponent createActivityComponent(IClientActivity activity)
	{
		final ActivityComponent ac = new ActivityComponent(activity);
		ac.setCancelAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				cancelActivity(ac);
			}
		});
		
		ac.setSuspendAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				suspendActivity(ac);
			}
		});
		
		ac.setFinishAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (!ac.isReadyForFinish())
					return;
				
				IGoal finishGoal = agent.createGoal("clientcap.finish_activity");
				IClientActivity activity = ac.getActivity();
				
				activity.setMultipleParameterValues(ac.getParameterValues());
				
				finishGoal.getParameter("activity").setValue(activity);
				
				try
				{
					agent.dispatchTopLevelGoalAndWait(finishGoal);
				}
				catch (GoalFailureException e1)
				{
					JOptionPane.showMessageDialog(mainFrame, "Failed finishing activity.");
					return;
				}
				
				removeActivityComponent(ac);
			}
		});
		
		return ac;
	}
	
	private boolean suspendActivity(ActivityComponent ac)
	{
		IClientActivity activity = ac.getActivity();
		Map parameterValues = ac.getParameterValues();
		activity.setMultipleParameterValues(parameterValues);
		
		return cancelActivity(ac);
	}
	
	private boolean cancelActivity(ActivityComponent ac)
	{
		IGoal cancelGoal = agent.createGoal("clientcap.cancel_activity");
		IClientActivity activity = ac.getActivity();
		cancelGoal.getParameter("activity").setValue(activity);
		
		try
		{
			agent.dispatchTopLevelGoalAndWait(cancelGoal);
		}
		catch (GoalFailureException e)
		{
			JOptionPane.showMessageDialog(mainFrame, "Activity cancelation failed.");
			return false;
		}
		
		removeActivityComponent(ac);
		
		return true;
	}
	
	private void removeActivityComponent(ActivityComponent ac)
	{
		if (mainSplitPane.getRightComponent() instanceof ActivityComponent)
		{
			mainSplitPane.setRightComponent(EMPTY_PANEL);
		}
		else
		{
			JTabbedPane tabPane = (JTabbedPane) mainSplitPane.getRightComponent();
			tabPane.remove(ac);
		}
	}
}

