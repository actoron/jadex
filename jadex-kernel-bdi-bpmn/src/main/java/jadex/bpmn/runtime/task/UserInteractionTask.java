package jadex.bpmn.runtime.task;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.SReflect;
import jadex.commons.concurrent.IResultListener;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

/**
 *  Opens a dialog for the task and lets the user enter
 *  result parameters.
 */
public class UserInteractionTask implements ITask
{
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param instance	The process instance executing the task.
	 *  @listener	To be notified, when the task has completed.
	 */
	public void execute(final ITaskContext context, IProcessInstance instance, final IResultListener listener)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JComponent	message;
				MActivity	task	= context.getModelElement();
				List	parameters	= task.getParameters();
				
				if(parameters!=null && !parameters.isEmpty())
				{
					Insets	insets	= new Insets(2,2,2,2);
					message	= new JPanel(new GridBagLayout());
					message.add(new JLabel("Please enter values for task "+context.getModelElement().getName()),
						new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					
					for(int i=0; i<parameters.size(); i++)
					{
						final MParameter	param	= (MParameter) parameters.get(i);
						Object	value	= context.getParameterValue(param.getName());
						JComponent	comp;
						if(SReflect.getWrappedType(param.getClazz()).equals(Boolean.class))
						{
							final JCheckBox	cb	= new JCheckBox();
							cb.setSelected(value instanceof Boolean && ((Boolean)value).booleanValue());
							if(param.getDirection().equals(MParameter.DIRECTION_IN))
							{
								cb.setEnabled(false);
							}
							else
							{
								cb.addActionListener(new ActionListener()
								{
									public void actionPerformed(ActionEvent e)
									{
										context.setParameterValue(param.getName(), new Boolean(cb.isSelected()));
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
								tf.addKeyListener(new KeyAdapter()
								{
									public void keyTyped(KeyEvent e)
									{
										try
										{
											String	text	= tf.getText();
											// Todo: access thread context for imports etc.!?
											IParsedExpression	pex	= new JavaCCExpressionParser().parseExpression(text, null, null, null);
											context.setParameterValue(param.getName(), pex.getValue(null));
										}
										catch(Exception ex)
										{
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
				}
				final JOptionPane	pane	= new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
				pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
				
				final JDialog	dialog	= new JDialog((JFrame)null, context.getModelElement().getName());
				dialog.getContentPane().setLayout(new BorderLayout());
				dialog.getContentPane().add(pane, BorderLayout.CENTER);

		        dialog.addWindowListener(new WindowAdapter()
		        {
		            public void windowClosing(WindowEvent we)
		            {
		                pane.setValue(null);
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
								listener.exceptionOccurred(e);		                	
			                }
			                else
			                {
								listener.resultAvailable(null);
			                }
		            	}
		            }
		        });

				dialog.pack();
				dialog.setLocationRelativeTo(null);
				dialog.setVisible(true);
			}
		});
	}
}
