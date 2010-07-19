package jadex.wfms.simulation;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IEAGoal;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MParameter;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SGUI;
import jadex.commons.ThreadSuspendable;
import jadex.commons.collection.TreeNode;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.gpmn.model2.MGpmnModel;
import jadex.wfms.GoalDispatchResultListener;
import jadex.wfms.SwingGoalDispatchResultListener;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.guicomponents.LoginDialog;
import jadex.wfms.guicomponents.SGuiHelper;
import jadex.wfms.simulation.gui.SimulationWindow;
import jadex.wfms.simulation.stateholder.AbstractNumericStateSet;
import jadex.wfms.simulation.stateholder.BooleanStateSet;
import jadex.wfms.simulation.stateholder.DocumentStateSet;
import jadex.wfms.simulation.stateholder.IParameterStateSet;
import jadex.wfms.simulation.stateholder.NumberRange;
import jadex.wfms.simulation.stateholder.ProcessStateController;
import jadex.wfms.simulation.stateholder.ResolvableListChoiceStateSet;
import jadex.wfms.simulation.stateholder.ResolvableMultiListChoiceStateSet;
import jadex.wfms.simulation.stateholder.StringArrayStateSet;
import jadex.wfms.simulation.stateholder.StringStateSet;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeCellRenderer;

public class ClientSimulator
{
	private IBDIExternalAccess agent;
	
	private SimulationWindow simWindow;
	
	private ProcessStateController activeStateController;
	
	private ClientMetaProcessModel clientMetaProcessModel;
	
	private DefaultTableModel scenarios;
	
	public ClientSimulator(final IBDIExternalAccess agent)
	{
		Runnable init = new Runnable()
		{
			
			public void run()
			{
				ClientSimulator.this.agent = agent;
				ClientSimulator.this.activeStateController = null;
				ClientSimulator.this.scenarios = new DefaultTableModel(new Object[] { "Scenarios" }, 0)
				{
					public void addRow(final Object[] rowData)
					{
						super.addRow(rowData);
						((Scenario) rowData[0]).addStateChangeListener(new ChangeListener()
						{
							public void stateChanged(ChangeEvent e)
							{
								int row = 0;
								while (row < getRowCount() && (!getValueAt(row, 0).equals(rowData[0])))
									++row;
								if (row < getRowCount())
									fireTableRowsUpdated(row, row);
							}
						});
					}
					
					public void removeRow(int row)
					{
						((Scenario) getValueAt(row, 0)).clearStateChangeListeners();
						super.removeRow(row);
					}
				};
				scenarios.addTableModelListener(new TableModelListener()
				{
					public void tableChanged(TableModelEvent e)
					{
						updateGui();
					}
				});
				simWindow = new SimulationWindow(scenarios);
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
								simWindow.dispose();
							}
						});
					}
				});
				
				login(showLoginDialog()).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						continueInit();
					}
				});
				
				
			}
		};
		
		EventQueue.invokeLater(init);
	}
	
	private void continueInit()
	{
		setupActions();
		
		
		//TODO: Add Log
		/*this.clientService.getMonitoringService(this).addLogListener(this, new ILogListener()
		{
			public void logMessage(final LogEvent event)
			{
				if (EventQueue.isDispatchThread())
					simWindow.addLogMessage(event.getMessage());
				else
				{
					EventQueue.invokeLater(new Runnable()
					{
					
						public void run()
						{
							simWindow.addLogMessage(event.getMessage());
						}
					});
				}
			}
			
			
		});*/
		
		/*this.clientService.getMonitoringService(this).addProcessListener(this, new IProcessListener()
		{
			public void processFinished(ProcessEvent event)
			{
				if ((activeStateController == null) || (activeStateController.finalState()))
				{
					EventQueue.invokeLater(new Runnable()
					{
						public void run()
						{
							simWindow.enableMenuItem(SimulationWindow.STOP_MENU_ITEM_NAME, false);
							simWindow.enableMenuItem(SimulationWindow.START_MENU_ITEM_NAME, true);
							activeStateController = null;
							simWindow.addLogMessage("Finished Simulation");
						}
					});
				}
				else
				{
					EventQueue.invokeLater(new Runnable()
					{
						public void run()
						{
							activeStateController.nextState();
							simWindow.addLogMessage("Setting new process state: " + activeStateController.toString());
							clientService.startProcess(ClientSimulator.this, clientProcessMetaModel.getMainProcessName());
						}
					});
				}
			}
		});*/
		
		agent.getBeliefbase().setBeliefFact("clientcap.process_finished_controller", new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if ((activeStateController == null) || (activeStateController.finalState()))
				{
					if (simWindow.isLastScenario())
						EventQueue.invokeLater(new Runnable()
						{
							public void run()
							{
								editMode();
								activeStateController = null;
								simWindow.addLogMessage("Finished Simulation");
							}
						});
					else
						EventQueue.invokeLater(new Runnable()
						{
							public void run()
							{
								simWindow.selectNextScenario();
								while (simWindow.getSelectedScenario().createProcessStateController().getStateCount() == 0)
									simWindow.selectNextScenario();
								simWindow.addLogMessage("Selecting scenario: " + simWindow.getSelectedScenario().getName());
								activeStateController = simWindow.getSelectedScenario().createProcessStateController();
								startProcess();
							}
						});
				}
				else
				{
					EventQueue.invokeLater(new Runnable()
					{
						public void run()
						{
							activeStateController.nextState();
							simWindow.addLogMessage("Setting new process state: " + activeStateController.toString());
							startProcess();
						}
					});
				}
			}
		});
		agent.createGoal("clientcap.start_process_event_subscription").addResultListener(new GoalDispatchResultListener(agent));
		
		agent.getBeliefbase().setBeliefFact("clientcap.add_workitem_controller", new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				final IWorkitem workitem = (IWorkitem) e.getSource();
				
				agent.createGoal("clientcap.begin_activity").addResultListener(new GoalDispatchResultListener(agent)
				{
					public void configureGoal(IEAGoal goal)
					{
						goal.setParameterValue("workitem",workitem);
					}
				});
			}
		});
		agent.createGoal("clientcap.start_workitem_subscription").addResultListener(new GoalDispatchResultListener(agent));
		
		agent.getBeliefbase().setBeliefFact("clientcap.add_activity_controller", new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				final IClientActivity activity = (IClientActivity) e.getSource();
				simWindow.addLogMessage("Processing Activity: " + activity.getName());
				Map parameterStates = activeStateController.getActivityState(activity.getName(), activity.getParameterValues());
				if (parameterStates != null)
					activity.setMultipleParameterValues(parameterStates);
				
				agent.createGoal("clientcap.finish_activity").addResultListener(new GoalDispatchResultListener(agent)
				{
					public void configureGoal(IEAGoal goal)
					{
						goal.setParameterValue("activity", activity);
					}
				});
			}
		});
		agent.createGoal("clientcap.start_activity_subscription").addResultListener(new SwingGoalDispatchResultListener(agent));
		
		simWindow.setCellRenderer(new DefaultTreeCellRenderer()
		{
			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
			{
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				if (value instanceof TreeNode)
				{
					Object data = ((TreeNode) value).getData();
					if (data instanceof MGpmnModel)
						setIcon(SimulationWindow.GPMN_ICON);
					else if (data instanceof MBpmnModel)
						setIcon(SimulationWindow.BPMN_ICON);
					else if (data instanceof MActivity)
						setIcon(SimulationWindow.TASK_ICON);
					else if (data instanceof MParameter)
					{
						setIcon(SimulationWindow.PARAM_ICON);
						MActivity task = (MActivity) ((ModelTreeNode)((ModelTreeNode)value).getParent()).getData();
						if ((simWindow.getSelectedScenario() == null) || simWindow.getSelectedScenario().getTaskParameter(task.getName(), ((MParameter) data).getName()).getStateCount() == 0)
							setForeground(new Color(128,128,128));
					}
					else if (data instanceof TreeNode)
						setForeground(new Color(128,128,128));
				}
				
				return this;
			}
		});
		
		updateGui();
	}
	
	protected IFuture loadModelFromPath(final String path)
	{
		final Future ret = new Future();
		agent.createGoal("clientcap.request_model").addResultListener(new GoalDispatchResultListener(agent)
		{
			public void configureGoal(IEAGoal goal)
			{
				goal.setParameterValue("model_name", path);
				goal.setParameterValue("model_name_path", Boolean.TRUE);
			}
			
			public void resultAvailable(Object source, Object result)
			{
				
			}
			
			public void goalResultsAvailable(Map parameters)
			{
				ret.setResult(parameters.get("model"));
			}
		});
		
		return ret;
	}
	
	private void updateGui()
	{
		if (simWindow.getSelectedScenario() != null)
		{
			simWindow.setStatusBar("Process States: " + String.valueOf(getStateCount()));
		}
		else
			simWindow.setStatusBar(" ");
		
		if (activeStateController == null)
			editMode();
		else
			simMode();
		
		simWindow.refreshParameterStates();
	}
	
	private LoginDialog showLoginDialog()
	{
		LoginDialog loginDialog = new LoginDialog(simWindow);
		loginDialog.setLocation(SGUI.calculateMiddlePosition(loginDialog));
		loginDialog.setVisible(true);
		return loginDialog;
	}
	
	private IFuture login(LoginDialog loginDialog)
	{
		final Future ret = new Future();
		final String username = loginDialog.getUserName();
		final String password = loginDialog.getPassword();
		agent.createGoal("clientcap.connect").addResultListener(new GoalDispatchResultListener(agent)
		{
			public void configureGoal(IEAGoal goal)
			{
				goal.setParameterValue("user_name", username);
				goal.setParameterValue("auth_token", password);
				ret.setResult(null);
			}
			
			public void goalExceptionOccurred(Exception exception)
			{
				System.err.println("Login failed");
			}
		});
		return ret;
	}
	
	private void setupActions()
	{
		simWindow.setMenuItemAction(SimulationWindow.OPEN_MENU_ITEM_NAME, new AbstractAction()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				agent.createGoal("clientcap.request_model_names").addResultListener(new SwingGoalDispatchResultListener(agent)
				{
					public void goalResultsAvailable(Map parameters)
					{
						Set modelNames = new TreeSet((Set) parameters.get("model_names"));
						final String modelName = simWindow.showProcessPickerDialog(modelNames);
						if (modelName == null)
							return;
						final ClientMetaProcessModel model = new ClientMetaProcessModel();
						
						agent.createGoal("clientcap.request_model").addResultListener(new SwingGoalDispatchResultListener(ClientSimulator.this.agent)
						{
							public void configureGoal(IEAGoal goal)
							{
								goal.setParameterValue("model_name", modelName);
							}
							
							public void goalResultsAvailable(Map parameters)
							{
								try
								{
									model.setRootModel(ClientSimulator.this, modelName, (ILoadableComponentModel) parameters.get("model"));
								}
								catch (Exception e)
								{
									goalExceptionOccurred(e);
								}
								simWindow.setProcessTreeModel(model);
								clientMetaProcessModel = model;
								while (scenarios.getRowCount() > 0)
									scenarios.removeRow(0);
								updateGui();
							}
							
							public void goalExceptionOccurred(Exception exception)
							{
								exception.printStackTrace();
								simWindow.showMessage(JOptionPane.ERROR_MESSAGE, "Cannot open the process", "Opening the process failed.");
							}
						});
					}
				});
			}
		});
		
		simWindow.setMenuItemAction(SimulationWindow.CLOSE_MENU_ITEM_NAME, new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				while (scenarios.getRowCount() > 0)
					scenarios.removeRow(0);
				simWindow.setProcessTreeModel(null);
				clientMetaProcessModel = null;
				updateGui();
			}
		});
		
		simWindow.setMenuItemAction(SimulationWindow.START_MENU_ITEM_NAME, new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				simMode();
				simWindow.selectFirstScenario();
				while (simWindow.getSelectedScenario().createProcessStateController().getStateCount() == 0)
					simWindow.selectNextScenario();
				activeStateController = simWindow.getSelectedScenario().createProcessStateController();
				simWindow.addLogMessage("Starting Simulation");
				simWindow.addLogMessage("Setting process state: " + activeStateController.toString());
				startProcess();
			}
		});
		
		simWindow.setMenuItemAction(SimulationWindow.ADD_SCENARIO_ITEM_NAME, new AbstractAction()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				String name = "Unnamed Scenario";
				if (hasScenario(name))
				{
					String base = name;
					long count = 1;
					do
						name = base + String.valueOf(count++);
					while (hasScenario(name));
				}
				name = JOptionPane.showInputDialog(simWindow, "Scenario Name", name);
				if (hasScenario(name))
					JOptionPane.showMessageDialog(simWindow,
						    "Duplicate Scenario Name: " + name,
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
				else
				{
					scenarios.addRow(new Object[] {clientMetaProcessModel.createScenario(name)});
					simWindow.setSelectedScenario((Scenario) scenarios.getValueAt(scenarios.getRowCount() - 1, 0));
					simWindow.refreshParameterStates();
				}
				editMode();
			}
		});
		
		simWindow.setMenuItemAction(SimulationWindow.RENAME_SCENARIO_ITEM_NAME, new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (simWindow.getSelectedScenario() == null)
					return;
				
				String name = JOptionPane.showInputDialog(simWindow, "Scenario Name", simWindow.getSelectedScenario().getName());
				if (hasScenario(name))
					JOptionPane.showMessageDialog(simWindow,
						    "Duplicate Scenario Name: " + name,
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
				else
					simWindow.getSelectedScenario().setName(name);
			}
		});
		
		simWindow.setMenuItemAction(SimulationWindow.REMOVE_SCENARIO_ITEM_NAME, new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (simWindow.getSelectedScenario() == null)
					return;
				int row = 0;
				while (!scenarios.getValueAt(row, 0).equals(simWindow.getSelectedScenario()))
						++row;
				scenarios.removeRow(row);
			}
		});
		
		simWindow.setMenuItemAction(SimulationWindow.AUTO_FILL_MENU_ITEM_NAME, new AbstractAction() {
			
			private Random random;
			
			public void actionPerformed(ActionEvent e)
			{
				if (random == null)
					random = new Random();
				if (simWindow.getSelectedScenario() != null)
				{
					List pSets = simWindow.getSelectedScenario().getParameterSets();
					for (Iterator it = pSets.iterator(); it.hasNext(); )
					{
						IParameterStateSet pSet = (IParameterStateSet) it.next();
						if (pSet instanceof BooleanStateSet)
						{
							if (((BooleanStateSet) pSet).hasState(Boolean.FALSE))
								((BooleanStateSet) pSet).addState(Boolean.TRUE);
							else
								((BooleanStateSet) pSet).addState(Boolean.FALSE);
						}
						else if (pSet instanceof AbstractNumericStateSet)
						{
							long number = Math.abs(random.nextLong()) % ((AbstractNumericStateSet) pSet).getUpperBound();
							((AbstractNumericStateSet) pSet).addRange(new NumberRange(number, number));
						}
						else if (pSet instanceof StringStateSet)
							((StringStateSet) pSet).addString("Quick Fill Test " + String.valueOf(random.nextLong()));
						else if (pSet instanceof DocumentStateSet)
							((DocumentStateSet) pSet).addRandom();
						else if (pSet instanceof StringArrayStateSet)
							((StringArrayStateSet) pSet).addString(new String[] {"Quick Fill Test", String.valueOf(random.nextLong())});
						else if (pSet instanceof ResolvableListChoiceStateSet)
							for (Iterator it2 = Arrays.asList(((ResolvableListChoiceStateSet) pSet).getChoices()).iterator(); it2.hasNext(); )
							{
								Object selection = it2.next();
								if (!((ResolvableListChoiceStateSet) pSet).hasSelection(selection));
								{
									((ResolvableListChoiceStateSet) pSet).addSelection(selection);
									break;
								}
							}
						else if (pSet instanceof ResolvableMultiListChoiceStateSet)
							((ResolvableMultiListChoiceStateSet) pSet).addSelectionSet(SGuiHelper.selectFromArray(((ResolvableMultiListChoiceStateSet)pSet).getChoices(), random.nextLong()));
					}
				}
			}
		});
	}
	
	private void editMode()
	{
		simWindow.enableMenuItem(SimulationWindow.STOP_MENU_ITEM_NAME, false);
		if (getStateCount() > 0)
			simWindow.enableMenuItem(SimulationWindow.START_MENU_ITEM_NAME, true);
		else
			simWindow.enableMenuItem(SimulationWindow.START_MENU_ITEM_NAME, false);
		
		boolean enableScenarioMgmt = clientMetaProcessModel != null;
		simWindow.enableMenuItem(SimulationWindow.ADD_SCENARIO_ITEM_NAME, enableScenarioMgmt);
		simWindow.enableMenuItem(SimulationWindow.RENAME_SCENARIO_ITEM_NAME, enableScenarioMgmt);
		simWindow.enableMenuItem(SimulationWindow.REMOVE_SCENARIO_ITEM_NAME, enableScenarioMgmt);
		
		simWindow.enableMenuItem(SimulationWindow.AUTO_FILL_MENU_ITEM_NAME, simWindow.getSelectedScenario() != null);
		
		simWindow.enableScenarioTable(true);
	}
	
	private void simMode()
	{
		simWindow.enableMenuItem(SimulationWindow.STOP_MENU_ITEM_NAME, true);
		simWindow.enableMenuItem(SimulationWindow.START_MENU_ITEM_NAME, false);
		simWindow.enableScenarioTable(false);
		
		simWindow.enableMenuItem(SimulationWindow.ADD_SCENARIO_ITEM_NAME, false);
		simWindow.enableMenuItem(SimulationWindow.RENAME_SCENARIO_ITEM_NAME, false);
		simWindow.enableMenuItem(SimulationWindow.REMOVE_SCENARIO_ITEM_NAME, false);
		simWindow.enableMenuItem(SimulationWindow.AUTO_FILL_MENU_ITEM_NAME, false);
	}
	
	private long getStateCount()
	{
		long stateCount = 0;
		for (int i = 0; i < scenarios.getRowCount(); ++i)
			stateCount += ((Scenario) scenarios.getValueAt(i, 0)).createProcessStateController().getStateCount();
		return stateCount;
	}
	
	private void startProcess()
	{
		agent.createGoal("clientcap.start_process").addResultListener(new SwingGoalDispatchResultListener(agent)
		{
			public void configureGoal(IEAGoal goal)
			{
				goal.setParameterValue("process_name", clientMetaProcessModel.getMainProcessName());
			}
		});
	}
	
	private boolean hasScenario(String name)
	{
		for (int i = 0; i < scenarios.getRowCount(); ++i)
			if (((Scenario) scenarios.getValueAt(i, 0)).getName().equals(name))
					return true;
		return false;
	}
}
