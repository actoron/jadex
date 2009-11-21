package jadex.wfms.simulation;

import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWfmsListener;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.ProcessFinishedEvent;
import jadex.wfms.client.WorkitemQueueChangeEvent;
import jadex.wfms.service.IClientService;
import jadex.wfms.simulation.gui.SimulationWindow;
import jadex.wfms.simulation.stateholder.ProcessStateController;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ClientSimulator implements IClient
{
	private IClientService clientService;
	
	private SimulationWindow simWindow;
	
	private String userName;
	
	private ProcessStateController activeStateController;
	
	private ClientProcessMetaModel clientProcessMetaModel;
	
	public ClientSimulator(IClientService clientService)
	{
		this(clientService, "TestUser");
	}
	
	public ClientSimulator(IClientService clientSrv, String userName)
	{
		this.activeStateController = null;
		this.clientService = clientSrv;
		this.userName = userName;
		clientService.authenticate(this);
		simWindow = new SimulationWindow();
		
		setupActions();
		
		this.clientService.getMonitoringService(this).addLogHandler(this, new Handler()
		{
			
			public void publish(final LogRecord record)
			{
				if (EventQueue.isDispatchThread())
					simWindow.addLogMessage(record.getMessage());
				else
				{
					EventQueue.invokeLater(new Runnable()
					{
					
						public void run()
						{
							simWindow.addLogMessage(record.getMessage());
						}
					});
				}
			}
			
			public void flush()
			{
			}
			
			public void close() throws SecurityException
			{
			}
		});
		
		clientService.addWfmsListener(new IWfmsListener()
		{
			
			public void workitemRemoved(WorkitemQueueChangeEvent event)
			{
			}
			
			public void workitemAdded(WorkitemQueueChangeEvent event)
			{
				System.out.println("New workitem" + event.getWorkitem().getName());
				int type = event.getWorkitem().getType();
				IClientActivity activity = clientService.beginActivity(ClientSimulator.this, event.getWorkitem());
				if (type == IWorkitem.TEXT_INFO_WORKITEM_TYPE)
				{
					simWindow.addLogMessage("Processing Info Activity: " + activity.getName());
				}
				else if (type == IWorkitem.DATA_FETCH_WORKITEM_TYPE)
				{
					Map parameterStates = activeStateController.getActivityState(activity.getName());
					activity.setParameterValues(parameterStates);
				}
				
				clientService.finishActivity(ClientSimulator.this, activity);
			}
			
			public void processFinished(ProcessFinishedEvent event)
			{
				if ((activeStateController == null) || (activeStateController.finalState()))
				{
					EventQueue.invokeLater(new Runnable()
					{
						public void run()
						{
							simWindow.enableStop(false);
							simWindow.enableStart(true);
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
			
			public IClient getClient()
			{
				return null;
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
	
	private void updateStatusBar()
	{
		if (clientProcessMetaModel != null)
		{
			long stateCount = clientProcessMetaModel.createProcessStateController().getStateCount();
			if (stateCount > 0)
				simWindow.enableStart(true);
			else
				simWindow.enableStart(false);
			simWindow.setStatusBar("Process States: " + String.valueOf(stateCount));
		}
		else
			simWindow.setStatusBar(" ");
	}
	
	private void setupActions()
	{
		simWindow.setOpenAction(new AbstractAction()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				Set modelNames = clientService.getModelNames(ClientSimulator.this);
				String modelName = simWindow.showProcessPickerDialog(modelNames);
				System.out.println(modelName);
				if (modelName == null)
					return;
				ClientProcessMetaModel model = new ClientProcessMetaModel();
				try
				{
					model.setRootModel(modelName, clientService.getProcessDefinitionService(ClientSimulator.this).getProcessModel(ClientSimulator.this, modelName));
					simWindow.setProcessTreeModel(model);
					model.addStateChangeListener(new ChangeListener()
					{
						public void stateChanged(ChangeEvent e)
						{
							updateStatusBar();
						}
					});
					clientProcessMetaModel = model;
					updateStatusBar();
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
				clientProcessMetaModel = null;
			}
		});
		
		simWindow.setStartAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				simWindow.enableStart(false);
				simWindow.enableStop(true);
				activeStateController = clientProcessMetaModel.createProcessStateController();
				simWindow.addLogMessage("Starting Simulation");
				simWindow.addLogMessage("Setting process state: " + activeStateController.toString());
				clientService.startProcess(ClientSimulator.this, clientProcessMetaModel.getMainProcessName());
			}
		});
	}
	private Timer timer;
}
