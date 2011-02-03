package jadex.simulation.analysis.common.dataObjects.parameter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class AConstraintParameter extends ABasicParameter implements IAConstraintParameter
{
	protected Set<IAConstraint> constraints;

	public AConstraintParameter(String name, Class type)
	{
		super(name, type);
	}

	public AConstraintParameter(String name, Class type, Object value)
	{
		this(name, type);
		setValue(value);
	}

	public AConstraintParameter(String name, Class type, Object value, Set<IAConstraint> constraints)
	{
		this(name, type, value);
		synchronized (mutex)
		{
			this.constraints = Collections.synchronizedSet(constraints);
		}
	}

	// ----- Interface IAConstraintParameter -----

	@Override
	public Set<IAConstraint> getConstraints()
	{
		return constraints;
	}

	@Override
	public void addConstraint(IAConstraint constraint)
	{
		synchronized (mutex)
		{
			constraints.add(constraint);
		}
	}

	@Override
	public void removeConstraint(IAConstraint constraint)
	{
		synchronized (mutex)
		{
			constraints.remove(constraint);
		}
	}

	@Override
	public void clearConstraints()
	{
		synchronized (mutex)
		{
			constraints.clear();
		}
	}

	@Override
	public boolean containsConstraint(IAConstraint constraint)
	{
		return constraints.contains(constraint);
	}

	@Override
	public boolean hasConstraints()
	{
		return constraints.isEmpty();
	}

	@Override
	public Integer numberOfConstaints()
	{
		return constraints.size();
	}

	// ----- Override ABasicParameter -----

	@Override
	public boolean isFeasable()
	{
		synchronized (mutex)
		{
			boolean ret = true;
			for (IAConstraint constraint : constraints)
			{
				if (constraint.isValid(getValue())) ret = false;
			}
			return ret;
		}
	}

	@Override
	public JComponent getView()
	{
		synchronized (mutex)
		{
			final JComponent component = super.getView();
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{

				}
			});
			return component;
		}
	}

	/**
	 * Test View
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		ABasicParameter para = new ABasicParameter("Parameter", Double.class, 5.0);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1024, 786);
		frame.add(para.getView());
		frame.setVisible(true);
	}
}
