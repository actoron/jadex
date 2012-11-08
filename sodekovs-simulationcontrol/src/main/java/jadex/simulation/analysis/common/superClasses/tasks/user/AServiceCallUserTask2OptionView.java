package jadex.simulation.analysis.common.superClasses.tasks.user;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.superClasses.tasks.ATaskView;
import jadex.simulation.analysis.common.superClasses.tasks.IATask;
import jadex.simulation.analysis.common.superClasses.tasks.IATaskView;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

	public AServiceCallUserTask2OptionView(IATask taskObject)
	{
		super(taskObject);
	}

	public IFuture startGUI(final String[][] val)
	{
		final Future ret = new Future();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Insets insets = new Insets(1, 1, 1, 1);
				JPanel comp = new JPanel(new GridBagLayout());
				Integer count = 0;
				for (String[] strings : val)
				{
					JLabel label1 = new JLabel(strings[0]);
					JLabel label2 = new JLabel(strings[1]);
					comp.add(label1, new GridBagConstraints(0, count, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					comp.add(label2, new GridBagConstraints(1, count, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					count++;
				}
				component.add(comp,  new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				
				
				final JButton button = new JButton("Ok");
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
				final JButton button2 = new JButton("Cancel");
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

				component.add(button, new GridBagConstraints(0, 1, 1, GridBagConstraints.REMAINDER, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
				component.add(button2, new GridBagConstraints(1, 1, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
				}
		});
		return ret;
	}
}
