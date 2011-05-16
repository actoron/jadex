package jadex.simulation.analysis.common.dataObjects.parameter;

import jadex.commons.gui.JValidatorTextField;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.simulation.analysis.common.dataObjects.ADataObjectView;
import jadex.simulation.analysis.common.dataObjects.IADataView;
import jadex.simulation.analysis.common.dataObjects.factories.ADataViewFactory;
import jadex.simulation.analysis.common.events.data.ADataEvent;
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
	private ABasicParameterController controller;

	private JTextField paraNameValue;
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
		controller = new ABasicParameterController(parameter, this);
		init();
	}

	private void init()
	{
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Insets insets = new Insets(1, 1, 1, 1);
				int gridY = 0;

				// Parameter Type
				JLabel paraType = new JLabel("Parametertyp");
				paraType.setPreferredSize(new Dimension(150,20));
				component.add(paraType, new GridBagConstraints(0, gridY, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JTextField paraTypeValue = new JTextField("ABasicParameter");
				paraTypeValue.setEditable(false);
				paraTypeValue.setPreferredSize(new Dimension(400, 20));
				component.add(paraTypeValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				gridY++;
				paraTypeValue.setToolTipText("Typ des Parameters");

				// Parameter Innere Tpye
				JLabel innereTyp = new JLabel("Klasseausprägung");
				innereTyp.setPreferredSize(new Dimension(150, 20));
				component.add(innereTyp, new GridBagConstraints(0, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				innerTypValue = new JTextField(parameter.getValueClass().toString());
				innerTypValue.setEditable(parameter.isEditable());
				innerTypValue.setPreferredSize(new Dimension(400, 20));
				component.add(innerTypValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				innerTypValue.setToolTipText("Klasse die der Parameter hält");
				gridY++;
				
				// Parameter Name
				JLabel paraName = new JLabel("Parametername");
				paraName.setPreferredSize(new Dimension(150, 20));
				component.add(paraName, new GridBagConstraints(0, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				paraNameValue = new JTextField(parameter.getName());
				paraNameValue.setEditable(parameter.isEditable());
				paraNameValue.setPreferredSize(new Dimension(400, 20));
				component.add(paraNameValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				paraNameValue.addFocusListener(new FocusListener()
				{
					
					@Override
					public void focusLost(FocusEvent e)
					{
						controller.setName(paraNameValue.getText());
					}
					
					@Override
					public void focusGained(FocusEvent e)
					{
						controller.setName(paraNameValue.getText());
					}
				});
				
				gridY++;
				paraNameValue.setToolTipText("Name des Parameters");

				JLabel valueLabel = new JLabel("Aktueller Wert");
				valueLabel.setPreferredSize(new Dimension(150, 20));
				component.add(valueLabel, new GridBagConstraints(0, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				if (parameter.getValueClass().equals(Boolean.class))
		{
			valueBoolean = new JCheckBox("");
			valueBoolean.setSelected((Boolean) parameter.getValue());
			valueBoolean.setPreferredSize(new Dimension(400, 20));
			valueBoolean.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					synchronized (mutex)
					{
						controller.setValue(Boolean.toString(valueBoolean.isSelected()));
					}
				}
			});
			valueComp = valueBoolean;
		}
		else
		{
			valueField = new JValidatorTextField(parameter.getValue().toString());
			valueField.setValidator(new ParserClassValidator(SAnalysisClassLoader.getClassLoader(), parameter.getValueClass()));
			valueField.setPreferredSize(new Dimension(400, 20));
			valueField.addFocusListener(new FocusListener()
			{
				@Override
				public void focusLost(FocusEvent e)
				{
					controller.setValue(valueField.getText());
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
						controller.setValue(valueField.getText());
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
			JLabel value = new JLabel("Verwendung");
			value.setPreferredSize(new Dimension(150, 20));
			component.add(value, new GridBagConstraints(0, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

			paraVariableBox = new JCheckBox("");
			paraVariableBox.setSelected(parameter.isUsage());
			paraVariableBox.setPreferredSize(new Dimension(350, 20));
			paraVariableBox.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
			{
				synchronized (mutex)
				{
					controller.setUsage(new Boolean(paraVariableBox.isSelected()));
				}
			}
			});
			paraVariableBox.setEnabled(parameter.isEditable());
			component.add(paraVariableBox, new GridBagConstraints(1, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
			paraVariableBox.setToolTipText("Gibt an ob der Parameter im weiteren Verlauf der Analyse veränderbar sein soll");
			gridY++;

			freePanel = new JPanel();
			component.add(freePanel, new GridBagConstraints(0, gridY, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

			component.setPreferredSize(new Dimension(550, 300));

			component.updateUI();
			component.validate();
		}
		});
	}

	@Override
	public void dataEventOccur(final ADataEvent event)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
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
				paraNameValue.setEnabled(parameter.isEditable());
				innerTypValue.setEnabled(parameter.isEditable());
			}

			if (command.equals(AConstants.PARAMETER_USAGE))
			{
				paraVariableBox.setSelected(parameter.isUsage());
			}

			component.revalidate();
			component.repaint();
		}
	}
		});
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
		frame.setSize(550, 300);
		frame.add(ADataViewFactory.createView(para).getComponent());
		frame.setVisible(true);
	}
}
