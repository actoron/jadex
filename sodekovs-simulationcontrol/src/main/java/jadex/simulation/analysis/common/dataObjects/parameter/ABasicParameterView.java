package jadex.simulation.analysis.common.dataObjects.parameter;

import jadex.commons.gui.JValidatorTextField;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.simulation.analysis.common.dataObjects.ADataObjectView;
import jadex.simulation.analysis.common.dataObjects.IADataView;
import jadex.simulation.analysis.common.dataObjects.Factories.ADataViewFactory;
import jadex.simulation.analysis.common.events.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.common.util.ParserClassValidator;
import jadex.simulation.analysis.common.util.SAnalysisClassLoader;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ABasicParameterView extends ADataObjectView implements IADataView
{
	private ABasicParameter parameter;

	private JLabel paraNameValue;
	private JTextField innerTypValue;
	private JComponent valueComp;
	private JCheckBox paraVariableBox;
	private JPanel freePanel;
	private JCheckBox valueBoolean;
	private JValidatorTextField valueField;

	public ABasicParameterView(ABasicParameter parameter)
	{
		super(parameter);
		component = new JPanel(new GridBagLayout());
		this.parameter = (ABasicParameter) parameter;
		init();
	}

	private void init()
	{
		Insets insets = new Insets(2, 2, 2, 2);
		int gridY = 0;

		// Parameter Type
		JLabel paraType = new JLabel("Parametertyp:");
		paraType.setPreferredSize(new Dimension(150, 25));
		component.add(paraType, new GridBagConstraints(0, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

		JLabel paraTypeValue = new JLabel("ABasicParameter");
		paraTypeValue.setPreferredSize(new Dimension(250, 25));
		component.add(paraTypeValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
		gridY++;
		paraTypeValue.setToolTipText("Typ des Parameters");

		// Parameter Name
		JLabel paraName = new JLabel("Parametername:");
		paraName.setPreferredSize(new Dimension(150, 25));
		component.add(paraName, new GridBagConstraints(0, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

		paraNameValue = new JLabel(parameter.getName());
		paraNameValue.setPreferredSize(new Dimension(250, 25));
		component.add(paraNameValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
		gridY++;
		paraNameValue.setToolTipText("Name des Parameters");

		// Parameter Innere Tpye
		JLabel innereTyp = new JLabel("Klasseausprägung:");
		innereTyp.setPreferredSize(new Dimension(150, 25));
		component.add(innereTyp, new GridBagConstraints(0, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

		innerTypValue = new JTextField(parameter.getValueClass().toString());
		innerTypValue.setEditable(false);
		innerTypValue.setPreferredSize(new Dimension(250, 25));
		component.add(innerTypValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
		innerTypValue.setToolTipText("Klasse die der Parameter hält");
		gridY++;

		JLabel valueLabel = new JLabel("Aktueller Wert:");
		valueLabel.setPreferredSize(new Dimension(150, 25));
		component.add(valueLabel, new GridBagConstraints(0, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

		if (parameter.getValueClass().equals(Boolean.class))
		{
			valueBoolean = new JCheckBox("");
			valueBoolean.setSelected((Boolean) parameter.getValue());
			valueBoolean.setPreferredSize(new Dimension(250, 25));
			valueBoolean.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					synchronized (mutex)
					{
						parameter.setValue(valueBoolean.isSelected());
					}
				}
			});
			valueComp = valueBoolean;
		}
		else
		{
			valueField = new JValidatorTextField(parameter.getValue().toString());
			valueField.setValidator(new ParserClassValidator(SAnalysisClassLoader.getClassLoader(), parameter.getValueClass()));
			valueField.setPreferredSize(new Dimension(250, 25));
			valueField.addFocusListener(new FocusListener()
			{
				@Override
				public void focusLost(FocusEvent e)
				{
					validateField(valueField.getText());
				}

				@Override
				public void focusGained(FocusEvent e)
				{}
			});
			valueField.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					synchronized (mutex)
					{
						validateField(valueField.getText());
					}
				}
			});
			valueComp = valueField;
			valueComp.setEnabled(parameter.isEditable());
			valueComp.setToolTipText("Wert des Parameters");
		}
		component.add(valueComp, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
		gridY++;

		// Verwendung
		JLabel value = new JLabel("Verwendung:");
		value.setPreferredSize(new Dimension(150, 25));
		component.add(value, new GridBagConstraints(0, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

		paraVariableBox = new JCheckBox("");
		paraVariableBox.setSelected(parameter.isUsage());
		paraVariableBox.setPreferredSize(new Dimension(250, 25));
		paraVariableBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				synchronized (mutex)
				{
					parameter.setUsage(paraVariableBox.isSelected());
				}
			}
		});
		paraVariableBox.setEnabled(parameter.isEditable());
		component.add(paraVariableBox, new GridBagConstraints(1, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
		paraVariableBox.setToolTipText("Gibt an ob der Parameter im weiteren Verlauf der Analyse veränderbar sein soll");
		gridY++;

		freePanel = new JPanel();
		component.add(freePanel, new GridBagConstraints(0, gridY, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

		component.setPreferredSize(new Dimension(500, 300));
		
		component.updateUI();
		component.validate();
	}

	@Override
	public void dataEventOccur(ADataEvent event)
	{
		synchronized (mutex)
		{
			String command = event.getCommand();
			if (command.equals(AConstants.PARAMETER_VALUE))
			{
				if (valueComp instanceof JCheckBox)
				{
					JCheckBox box = (JCheckBox) valueComp;
					box.setSelected((Boolean) parameter.getValue());
				}
				else
				{
					JValidatorTextField textField = (JValidatorTextField) valueComp;
					textField.setText(parameter.getValue().toString());
				}
			}

			if (command.equals(AConstants.DATA_EDITABLE))
			{
				paraVariableBox.setEnabled(parameter.isEditable());
				valueComp.setEnabled(parameter.isEditable());
			}

			if (command.equals(AConstants.PARAMETER_USAGE))
			{
				paraVariableBox.setSelected(parameter.isUsage());
			}

			component.revalidate();
			component.repaint();
		}
	}

	private void validateField(String text)
	{
		synchronized (mutex)
		{
			if (text.length() > 0)
			{
				if (parameter.getValueClass().equals(String.class))
				{
					parameter.setValue(text);
				}
				else
				{
					try
					{
						String[] imports = { "jadex.simulation.analysis.common.dataObject.*", "jadex.simulation.analysis.common.dataObject.parameter.*" };
						IParsedExpression pex = new JavaCCExpressionParser().parseExpression(text, imports, null, SAnalysisClassLoader.getClassLoader());
						Object value = pex.getValue(null);
						if (value.getClass().equals(parameter.getValueClass()))
						{
							if (!(parameter.getValue().equals(value)))
							{
								parameter.setValue(value);
							}

						}
						else
						{
							throw new RuntimeException();
						}
					}
					catch (Exception ex)
					{
						JOptionPane.showMessageDialog(SwingUtilities.getRoot(component), "Aktueller Wert (" + parameter.getValueClass() + ") ist nicht zulässig!");
					}
				}
			}
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
		para.setUsage(false);
		para.setValue(10.0);
		para.setEditable(false);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 300);
		frame.add(ADataViewFactory.createView(para).getComponent());
		frame.setVisible(true);
	}
}
