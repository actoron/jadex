package jadex.simulation.analysis.common.data.parameter;

import jadex.commons.gui.JValidatorTextField;
import jadex.simulation.analysis.common.data.ADataObjectView;
import jadex.simulation.analysis.common.data.IADataView;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.events.data.ADataEvent;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 * View of ABasicParameter
 * 
 * @author 5Haubeck
 */
public class ABasicParameterView extends ADataObjectView implements IADataView
{
	private ABasicParameter parameter;
	private ABasicParameterController controller;
	protected JComponent componentP;

	private JTextField paraNameValue;
	private JTextField innerTypValue;
	protected JComponent valueComp;
	protected JPanel freePanel;
	private JCheckBox valueBoolean;
	private JValidatorTextField valueField;
	protected JTextField paraTypeValue;
	protected JLabel valueLabel;

	public ABasicParameterView(ABasicParameter parameter)
	{
		super(parameter);
		componentP = new JPanel(new GridBagLayout());
		component = new JScrollPane(componentP);
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
					paraType.setPreferredSize(new Dimension(150, 20));
					componentP.add(paraType, new GridBagConstraints(0, gridY, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

					paraTypeValue = new JTextField("ABasicParameter");
					paraTypeValue.setEditable(false);
					paraTypeValue.setPreferredSize(new Dimension(400, 20));
					componentP.add(paraTypeValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					gridY++;
					paraTypeValue.setToolTipText("Typ des Parameters");

					// Parameter Innere Tpye
					JLabel innereTyp = new JLabel("Klasseausprägung");
					innereTyp.setPreferredSize(new Dimension(150, 20));
					componentP.add(innereTyp, new GridBagConstraints(0, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

					innerTypValue = new JTextField(parameter.getValueClass().toString());
					innerTypValue.setEditable(parameter.isEditable());
					innerTypValue.setPreferredSize(new Dimension(400, 20));
					componentP.add(innerTypValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					innerTypValue.setToolTipText("Klasse die der Parameter hält");
					gridY++;

					// Parameter Name
					JLabel paraName = new JLabel("Parametername");
					paraName.setPreferredSize(new Dimension(150, 20));
					componentP.add(paraName, new GridBagConstraints(0, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

					paraNameValue = new JTextField(parameter.getName());
					paraNameValue.setEditable(parameter.isEditable());
					paraNameValue.setPreferredSize(new Dimension(400, 20));
					componentP.add(paraNameValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
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
								// controller.setName(paraNameValue.getText());
							}
						});
					paraNameValue.setToolTipText("Name des Parameters");
					gridY++;

					componentP.add(new JPanel(), new GridBagConstraints(0, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					gridY++;

					valueLabel = new JLabel("Aktueller Wert");
					valueLabel.setPreferredSize(new Dimension(150, 20));
					componentP.add(valueLabel, new GridBagConstraints(0, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

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
						controller.setValue(Boolean.toString(valueBoolean.isSelected()));
					}
				});
				valueBoolean.setEnabled(parameter.isValueEditable());
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
						controller.setValue(valueField.getText());
					}
				});
				valueField.setEnabled(parameter.isValueEditable());
				valueComp = valueField;

				valueComp.setToolTipText("Wert des Parameters");
			}
			componentP.add(valueComp, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
			gridY++;

			freePanel = new JPanel();
			componentP.add(freePanel, new GridBagConstraints(0, gridY, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

			componentP.setPreferredSize(new Dimension(500, 250));

			componentP.updateUI();
			componentP.validate();
		}
			});
	}

	@Override
	public void update(final IAEvent event)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
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
				// paraVariableBox.setEnabled(parameter.isEditable());
				paraNameValue.setEditable(parameter.isEditable());
				innerTypValue.setEditable(parameter.isEditable());
			}

			if (command.equals(AConstants.PARAMETER_EDITABLE))
			{
				if (valueComp instanceof JCheckBox)
				{
					((JCheckBox) valueComp).setEnabled(parameter.isValueEditable());
				}
				else
				{
					((JTextField) valueComp).setEditable(parameter.isValueEditable());
				}

			}
			componentP.revalidate();
			componentP.repaint();
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
		// para.setUsage(false);
		para.setValue(10.0);
		// para.setValueEditable(true);
		para.setEditable(true);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(550, 250);
		frame.add(para.getView().getComponent());
		frame.setVisible(true);

	}
}
