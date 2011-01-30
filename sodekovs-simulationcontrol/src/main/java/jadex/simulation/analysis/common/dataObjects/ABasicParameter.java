package jadex.simulation.analysis.common.dataObjects;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ABasicParameter implements IAParameter {

	private Boolean variable;
	private String name;
	private Object value;
	private Set<IAContraint> contraints = new HashSet<IAContraint>();
	private Class type;
	private Boolean result;

	public ABasicParameter(String name, Object value, Class type, Boolean variable, Boolean result) {
		this.name = name;
		this.type = type;
		this.variable = variable;
		this.result = result;
		setValue(value);
	}

	public ABasicParameter(String name, Object value, Class type, Boolean variable, Boolean result, Set<IAContraint> contraints) {
		this(name, value, type, variable, result);
		this.contraints = contraints;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public Boolean isFeasable() {
		Boolean ret = true;
		for (IAContraint contraint : contraints) {
			if (!contraint.isValid())
				ret = false;
		}
		return ret;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;

	}

	@Override
	public boolean equals(Object obj) {
		Boolean result = false;
		if (obj instanceof ABasicParameter) {
			ABasicParameter par = (ABasicParameter) obj;
			if (this.getName().equals(par.getName()))
				result = true;
		}
		return result;
	}

	@Override
	public Boolean isVariable() {
		return variable;
	}

	@Override
	public Class getClazz() {
		return type;
	}

	@Override
	public JComponent getView(final Boolean option) {
		final JComponent result = new JPanel(new GridBagLayout());
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Insets insets = new Insets(2, 2, 2, 2);
				int x = 0;

				JLabel label = new JLabel(getName());
				label.setPreferredSize(new Dimension(200, 30));
				result.add(label,
						new GridBagConstraints(x, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				x++;
				
				if (option)
				{
				String name;
				if (isResult())
				{
					name = "Verwenden";
				}
				else
				{
					name = "Variabel";
				}
				final JCheckBox paraVariableBox = new JCheckBox(name);
				
				paraVariableBox.setSelected(isVariable());
				paraVariableBox.setPreferredSize(new Dimension(100, 30));
				paraVariableBox.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						setVariable(paraVariableBox.isSelected());

					}
				});
				result.add(paraVariableBox,
						new GridBagConstraints(x, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				x++;
				}

				if (!isResult())
				{
					final JTextField field = new JTextField(getValue().toString());
					field.setEditable(true);
					field.setPreferredSize(new Dimension(200, 30));
					result.add(field,
					new GridBagConstraints(x, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					field.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							setValue(field.getText());
							// TODO: Nicht immer String
						}
					});
					x++;
				}
				else if (isResult() && getValue() != null)
				{
					final JTextField field = new JTextField(getValue().toString());
					field.setEditable(false);
					field.setPreferredSize(new Dimension(200, 30));
					result.add(field,
					new GridBagConstraints(x, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					x++;
				}

				JTextField paraClassField = new JTextField(getClazz().toString().substring(getClazz().toString().lastIndexOf(".") + 1));
				paraClassField.setEditable(false);
				paraClassField.setPreferredSize(new Dimension(200, 30));
				result.add(paraClassField,
				new GridBagConstraints(x, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				x++;
				result.updateUI();
				result.validate();
			}
		});

		return result;
	}

	@Override
	public Boolean isResult() {
		return result;
	}

	private void setVariable(Boolean variable) {
		this.variable = variable;
	}
}
