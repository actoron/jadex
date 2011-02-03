package jadex.simulation.analysis.common.dataObjects.parameter;

import jadex.simulation.analysis.common.dataObjects.ABasicDataObject;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ABasicParameter extends ABasicDataObject implements IAParameter
{
	private String name = "defaultName";
	private Object value = null;
	private Class type = Object.class;

	public ABasicParameter(String name)
	{
		super();
		setName(name);
	}

	public ABasicParameter(String name, Class type)
	{
		this(name);
		synchronized (mutex)
		{
			this.type = type;
		}
	}

	public ABasicParameter(String name, Object value)
	{
		this(name);
		setValue(value);
	}

	public ABasicParameter(String name, Class type, Object value)
	{
		this(name, type);
		setValue(value);
	}

	// ----- Interface IAParameter -----

	// Name

	@Override
	public void setName(String name)
	{
		synchronized (mutex)
		{
			this.name = name;
		}
	}

	@Override
	public String getName()
	{
		return name;
	}

	// Type

	@Override
	public Class getValueClass()
	{
		return type;
	}

	@Override
	public void setValueClass(Class type)
	{
		synchronized (type)
		{
			this.type = type;
		}
	}

	// feasable

	@Override
	public boolean isFeasable()
	{
		return true;
	}

	// value

	@Override
	public Object getValue()
	{
		return value;
	}

	@Override
	public void setValue(Object value)
	{
		synchronized (mutex)
		{
			this.value = value;
		}

	}

	// ----- override ABasicDataObject -----

	@Override
	public JComponent getView()
	{
		synchronized (mutex)
		{
			final JComponent component = new JPanel(new GridBagLayout());
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					Insets insets = new Insets(2, 2, 2, 2);
					int gridX = 0;

					JLabel label = new JLabel(getName());
					label.setPreferredSize(new Dimension(200, 25));
					component.add(label, new GridBagConstraints(gridX, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					gridX++;

					JTextField paraClassField = new JTextField(getValueClass().toString());
					paraClassField.setEditable(false);
					paraClassField.setPreferredSize(new Dimension(200, 25));
					component.add(paraClassField, new GridBagConstraints(gridX, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					gridX++;

					final JCheckBox paraVariableBox = new JCheckBox("Verwendung");
					paraVariableBox.setSelected(isEditable());
					paraVariableBox.setPreferredSize(new Dimension(200, 25));
					paraVariableBox.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							setEditable(paraVariableBox.isSelected());
						}
					});
					component.add(paraVariableBox, new GridBagConstraints(gridX, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					gridX++;

					// TODO: Boolean
					final JTextField field = new JTextField(getValue().toString());
					field.setEditable(isEditable());
					field.setPreferredSize(new Dimension(200, 25));
					component.add(field, new GridBagConstraints(gridX, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					gridX++;

					JPanel freePanel = new JPanel();
					component.add(freePanel, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

					component.updateUI();
					component.validate();
				}
			});

			return component;
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ABasicParameter)
		{
			ABasicParameter parameter = (ABasicParameter) obj;
			if (this.getName().equalsIgnoreCase(parameter.getName()))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getName().hashCode();
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
		frame.setSize(600, 300);
		frame.add(para.getView());
		frame.setVisible(true);
	}
}
