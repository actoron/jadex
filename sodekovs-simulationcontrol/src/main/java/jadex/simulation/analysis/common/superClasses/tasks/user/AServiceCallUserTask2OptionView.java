package jadex.simulation.analysis.common.superClasses.tasks.user;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.parameter.ASummaryParameter;
import jadex.simulation.analysis.common.superClasses.tasks.ATaskView;
import jadex.simulation.analysis.common.superClasses.tasks.IATask;
import jadex.simulation.analysis.common.superClasses.tasks.IATaskView;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * A tasks with a user interaction. OK-CANCEL Option
 * @author 5Haubeck
 *
 */
public class AServiceCallUserTask2OptionView extends ATaskView implements IATaskView
{
	protected JTextField paraTypeValue;
	protected ASummaryParameter paraS;

	public AServiceCallUserTask2OptionView(IATask taskObject)
	{
		super(taskObject);
	}

	public IFuture startGUI(final ASummaryParameter para, final Double value)
	{
		final Future ret = new Future();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				paraS = para;
				Insets insets = new Insets(1, 1, 1, 1);
				JLabel paraType = new JLabel("Sicherheitswert (bei +-10%)");
				paraType.setPreferredSize(new Dimension(200, 20));
				component.add(paraType, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				paraTypeValue = new JTextField(value.toString()+ "%");
				paraTypeValue.setEditable(false);
				paraTypeValue.setPreferredSize(new Dimension(400, 20));
				component.add(paraTypeValue, new GridBagConstraints(1, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				component.add(para.getView().getComponent(),  new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				
				
				final JButton button = new JButton("Weitere Ausführungen");
				button.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
				{
					ret.setResult(Boolean.TRUE);
					button.setEnabled(false);
					try
					{
						parent.setClosed(true);
					}
					catch (PropertyVetoException e1){}
			}
				});
				final JButton button2 = new JButton("Ausreichend");
				button2.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
				{
					ret.setResult(Boolean.FALSE);
					button.setEnabled(false);
					try
					{
						parent.setClosed(true);
					}
					catch (PropertyVetoException e1){}
			}
				});

				button.setPreferredSize(new Dimension(300, 20));
				button2.setPreferredSize(new Dimension(300, 20));

				component.add(button, new GridBagConstraints(0, 2, 1, GridBagConstraints.REMAINDER, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
				component.add(button2, new GridBagConstraints(1, 2, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
				}
		});
		return ret;
	}

	public void addServiceGUI(JComponent component, GridBagConstraints constrain)
	{
		this.component.add(component, constrain);
	}
}
