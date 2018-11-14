package jadex.bpmn.runtime.task;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MIdElement;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.IndexMap;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.transformation.annotations.Classname;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

/**
 *  Opens a dialog for the task and lets the user enter
 *  result parameters.
 */
@Task(description="The user interaction task can be used for fetching in parameter values " +
	"via an interactive user interface dialog. The task automatically uses all declared " +
	"in parameters.")
public class UserInteractionTask implements ITask
{
	//-------- attributes --------
	
	/** The dialog. */
	protected JDialog	dialog;

	//-------- ITask interface --------
	
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param instance	The process instance executing the task.
	 *  @listener	To be notified, when the task has completed.
	 */
	public IFuture<Void> execute(final ITaskContext context, IInternalAccess instance)
	{
		final Future<Void> ret = new Future<Void>();
		
		final ISubscriptionIntermediateFuture<IMonitoringEvent> sub = instance.getFeature0(IMonitoringComponentFeature.class).subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.FINE);
		sub.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
		{
			public void intermediateResultAvailable(IMonitoringEvent result)
			{
				if(dialog!=null)
				{
					dialog.setVisible(false);
				}
			}
			
			public void	commandAvailable(Object command)
			{
				// ignore timer updates
			}
		}));
		
		final IExternalAccess	exta	= instance.getExternalAccess();
		MActivity	task	= context.getModelElement();
		final String	taskname	= task.getName();

		// Simple pseudo struct for parameters.
		final int NAME	= 0;
		final int TYPE	= 1;
		final int VALUE	= 2;
		final int DIRECTION	= 3;
		final int NEWVALUE	= 4;
		
		IndexMap<String, MParameter> parameters	= task.getParameters();
		MIdElement pa = task;
		MBpmnModel model = context.getBpmnModel();
		while(pa!=null && (parameters==null || parameters.size()==0))
		{
			pa = model.getParent(pa);
			if(pa instanceof MActivity)
			{
				parameters = ((MActivity)pa).getParameters();
			}
		}
		
		final List<Object[]> lparameters = parameters!=null && parameters.size()>0? extractParams(context, instance, parameters): null;
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				final JOptionPane	pane;
				JComponent	message;
				
				if(lparameters!=null && !lparameters.isEmpty())
				{
					Insets	insets	= new Insets(2,2,2,2);
					message	= new JPanel(new GridBagLayout());
					message.add(new JLabel("Please enter values for task "+taskname),
						new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					
					pane	= new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
					pane.setValue(JOptionPane.UNINITIALIZED_VALUE);

					int i=0;
					for(final Object[] param: lparameters)
					{
						JComponent	comp;
						if(SReflect.getWrappedType((Class<?>)param[TYPE]).equals(Boolean.class))
						{
							final JCheckBox	cb	= new JCheckBox();
							cb.setSelected(param[VALUE] instanceof Boolean && ((Boolean)param[VALUE]).booleanValue());
							if(param[DIRECTION].equals(MParameter.DIRECTION_IN))
							{
								cb.setEnabled(false);
							}
							else
							{
								cb.addItemListener(new ItemListener()
								{
									public void itemStateChanged(ItemEvent e)
									{
										param[NEWVALUE]	= cb.isSelected();
									}
								});
							}
							comp	= cb;
						}
						else
						{
							final JTextField	tf	= new JTextField(param[VALUE]!=null ? ""+param[VALUE] : "");
							if(param[DIRECTION].equals(MParameter.DIRECTION_IN))
							{
								tf.setEditable(false);
							}
							else
							{
								tf.addKeyListener(new KeyAdapter()
								{
									public void keyReleased(KeyEvent e)
									{
										param[NEWVALUE]	= tf.getText();
//										System.out.println("setting: "+tf.getText());
									}
								});
							}
							comp	= tf;
						}
						message.add(new JLabel((String)param[NAME]),
							new GridBagConstraints(0, i+1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
						message.add(comp,
							new GridBagConstraints(1, i+1, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
						i++;
					}
				}
				else
				{
					message = new JLabel("Please perform task "+taskname);
					pane	= new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
					pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
				}
				
				dialog = new JDialog((JFrame)null, taskname);
				dialog.getContentPane().setLayout(new BorderLayout());
				dialog.getContentPane().add(pane, BorderLayout.CENTER);

		        dialog.addWindowListener(new WindowAdapter()
		        {
		            public void windowClosing(WindowEvent we)
		            {
		                pane.setValue(null);
		                
		                exta.scheduleStep(new IComponentStep<Void>()
						{
		                	@Classname("rem")
							public IFuture<Void> execute(IInternalAccess ia)
							{
		                		sub.terminate();
//								ia.removeComponentListener(lis);
								return IFuture.DONE;
							}
						});
		            }
		        });

		        pane.addPropertyChangeListener(new PropertyChangeListener()
		        {
		            public void propertyChange(PropertyChangeEvent event)
		            {
		            	if(pane.getValue()!=JOptionPane.UNINITIALIZED_VALUE)
		            	{
			            	// Close window, if button was pressed.
			                if(pane.getValue()!=null)
			                {
			                    dialog.setVisible(false);
			                }
		                
			                if(pane.getValue()==null || ((Integer)pane.getValue()).intValue()==JOptionPane.CANCEL_OPTION)
			                {
								Exception	e	= new RuntimeException("Task not completed");
								e.fillInStackTrace();
								ret.setExceptionIfUndone(e);
			                }
			                else
			                {
			                	// Write changed value, if any.
			                	if(lparameters!=null)
			                	{
				                	for(Object[] param: lparameters)
				                	{
				                		if(!SUtil.equals(param[VALUE], param[NEWVALUE]))
				                		{
				                			if(param[NEWVALUE] instanceof String)
				                			{
												try
												{
													// Todo: access thread context for imports etc.!?
													IParsedExpression	pex	= new JavaCCExpressionParser().parseExpression((String)param[NEWVALUE], null, null, null);
	//												System.out.println("setPVal: "+param[NAME]+" "+pex.getValue(null));
													context.setParameterValue((String)param[NAME], pex.getValue(null));
												}
												catch(Exception ex)
												{
													// Hack!!! Fallback: if no expression entered for string, use value directly.
													if(param[TYPE].equals(String.class))
													{
														context.setParameterValue((String)param[NAME], param[NEWVALUE]);
													}
													else
													{
														ex.printStackTrace();
													}
												}
				                			}
				                			else
				                			{
												context.setParameterValue((String)param[NAME], param[NEWVALUE]);
				                			}
				                		}
				                	}
			                	}
			                	
			                	ret.setResultIfUndone(null);
			                }
		            	}
		            }
		        });

				dialog.pack();
				dialog.setLocationRelativeTo(null);
				dialog.setVisible(true);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture<Void> cancel(final IInternalAccess instance)
	{
		final Future<Void> ret = new Future<Void>();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(dialog!=null)
				{
					dialog.dispose();
				}
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 * 
	 */
	protected List<Object[]> extractParams(ITaskContext context, IInternalAccess instance, IndexMap<String, MParameter> parameters)
	{
		final List<Object[]> lparameters = new ArrayList<Object[]>();
		
		for(MParameter param: parameters.values())
		{
			Object	value	= context.getParameterValue(param.getName());
			Class<?>	clazz	= param.getClazz().getType(instance.getClassLoader(), instance.getModel().getAllImports());
			lparameters.add(new Object[]
			{
				param.getName(),
				clazz,
				value,
				param.getDirection(),
				value
			});
		}
		
		return lparameters;
	}
}
