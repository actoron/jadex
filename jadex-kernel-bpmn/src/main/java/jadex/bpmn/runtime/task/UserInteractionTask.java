package jadex.bpmn.runtime.task;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.commons.SReflect;
import jadex.commons.collection.IndexMap;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.transformation.annotations.Classname;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
	public IFuture<Void> execute(final ITaskContext context, final IInternalAccess instance)
	{
		final Future<Void> ret = new Future<Void>();
		
//		final IComponentListener	lis	= new TerminationAdapter()
//		{
//			public void componentTerminated()
//			{
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						if(dialog!=null)
//						{
//							dialog.setVisible(false);
//						}
//					}
//				});
//			}
//		};
//		instance.addComponentListener(lis);

		final ISubscriptionIntermediateFuture<IMonitoringEvent> sub = instance.subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false);
		sub.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
		{
			public void intermediateResultAvailable(IMonitoringEvent result)
			{
				if(dialog!=null)
				{
					dialog.setVisible(false);
				}
			}
		}));
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				final JOptionPane	pane;
				JComponent	message;
				MActivity	task	= context.getModelElement();
				IndexMap<String, MParameter>	parameters	= task.getParameters();
				
				if(parameters!=null && !parameters.isEmpty())
				{
					Insets	insets	= new Insets(2,2,2,2);
					message	= new JPanel(new GridBagLayout());
					message.add(new JLabel("Please enter values for task "+context.getModelElement().getName()),
						new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					
					pane	= new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
					pane.setValue(JOptionPane.UNINITIALIZED_VALUE);

					int i=0;
					for(Iterator it=parameters.values().iterator(); it.hasNext(); i++)
					{
						final MParameter param = (MParameter)it.next();
						Object	value	= context.getParameterValue(param.getName());
						JComponent	comp;
						if(SReflect.getWrappedType(param.getClazz().getType(instance.getClassLoader(), instance.getModel().getAllImports())).equals(Boolean.class))
						{
							final JCheckBox	cb	= new JCheckBox();
							cb.setSelected(value instanceof Boolean && ((Boolean)value).booleanValue());
							if(param.getDirection().equals(MParameter.DIRECTION_IN))
							{
								cb.setEnabled(false);
							}
							else
							{
						        pane.addPropertyChangeListener(new PropertyChangeListener()
						        {
						            public void propertyChange(PropertyChangeEvent event)
						            {
						            	if(pane.getValue()!=JOptionPane.UNINITIALIZED_VALUE)
						            	{
						            		context.setParameterValue(param.getName(), new Boolean(cb.isSelected()));
						            	}
						            }
								});
							}
							comp	= cb;
						}
						else
						{
							final JTextField	tf	= new JTextField(value!=null ? ""+value : "");
							if(param.getDirection().equals(MParameter.DIRECTION_IN))
							{
								tf.setEditable(false);
							}
							else
							{
						        pane.addPropertyChangeListener(new PropertyChangeListener()
						        {
						            public void propertyChange(PropertyChangeEvent event)
						            {
						            	if(pane.getValue()!=JOptionPane.UNINITIALIZED_VALUE)
						            	{
											String	text	= tf.getText();
											try
											{
												// Todo: access thread context for imports etc.!?
												IParsedExpression	pex	= new JavaCCExpressionParser().parseExpression(text, null, null, null);
												context.setParameterValue(param.getName(), pex.getValue(null));
											}
											catch(Exception ex)
											{
												// Hack!!! Fallback: if no expression entered for string, use value directly.
												if(param.getClazz().equals(String.class))
												{
													context.setParameterValue(param.getName(), text);
												}
												else
												{
													ex.printStackTrace();
												}
											}
						            	}
									}
								});
							}
							comp	= tf;
						}
						message.add(new JLabel(param.getName()),
							new GridBagConstraints(0, i+1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
						message.add(comp,
							new GridBagConstraints(1, i+1, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					}
				}
				else
				{
					message = new JLabel("Please perform task "+context.getModelElement().getName());
					pane	= new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
					pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
				}
				
				dialog = new JDialog((JFrame)null, context.getModelElement().getName());
				dialog.getContentPane().setLayout(new BorderLayout());
				dialog.getContentPane().add(pane, BorderLayout.CENTER);

		        dialog.addWindowListener(new WindowAdapter()
		        {
		            public void windowClosing(WindowEvent we)
		            {
		                pane.setValue(null);
		                
		                instance.getExternalAccess().scheduleStep(new IComponentStep<Void>()
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
//								listener.exceptionOccurred(UserInteractionTask.this, e);		                	
								ret.setException(e);
			                }
			                else
			                {
			                	ret.setResult(null);
//								listener.resultAvailable(UserInteractionTask.this, null);
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
				dialog.dispose();
				ret.setResult(null);
			}
		});
		return ret;
	}
	
//	//-------- static methods --------
//	
//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static TaskMetaInfo getMetaInfo()
//	{
//		String desc = "The user interaction task can be used for fetching in parameter values " +
//			"via an interactive user interface dialog. The task automatically uses all declared" +
//			"in parameters.";
//		
//		return new TaskMetaInfo(desc, (ParameterMetaInfo[])null); 
//	}
}
