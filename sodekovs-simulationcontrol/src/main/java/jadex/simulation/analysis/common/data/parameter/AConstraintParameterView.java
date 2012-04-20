package jadex.simulation.analysis.common.data.parameter;

import jadex.commons.gui.JValidatorTextField;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.simulation.analysis.common.data.IADataView;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.events.data.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.common.util.ParserClassValidator;
import jadex.simulation.analysis.common.util.SAnalysisClassLoader;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * View of AConstraintParameter 
 * @author 5Haubeck
 *
 */
public class AConstraintParameterView extends ABasicParameterView implements IADataView
{
	private AConstraintParameter parameter;
	protected JValidatorTextField obenField_;
	protected JValidatorTextField untenField_;

	public AConstraintParameterView(AConstraintParameter parameter)
	{
		super(parameter);
		this.parameter = parameter;
		initConstraints();
	}

	private void initConstraints()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Insets insets = new Insets(1, 1, 1, 1);

				componentP.remove(freePanel);
				int gridY = componentP.getComponentCount() + 1;

				paraTypeValue.setText("AConstraintParameter");
				final ABorderConstraint constrain = (ABorderConstraint) parameter.getConstraints().iterator().next();

				JLabel oben = new JLabel("Obere Grenze:");
				final JValidatorTextField obenField = new JValidatorTextField(constrain.getUpperBorder().toString());
				obenField_ = obenField;
				obenField.setValidator(new ParserClassValidator(SAnalysisClassLoader.getClassLoader(), parameter.getValueClass()));
				obenField.addFocusListener(new FocusListener()
				{
					@Override
					public void focusLost(FocusEvent e)
					{
						setBound(constrain, obenField.getText(), true);
					}

					@Override
					public void focusGained(FocusEvent e)
					{}
				});
				obenField.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						setBound(constrain, obenField.getText(), true);
					}
				});

				// JPanel obenPanel = new JPanel(new GridBagLayout());
				oben.setPreferredSize(new Dimension(150, 20));
				obenField.setPreferredSize(new Dimension(250, 20));
				componentP.add(oben, new GridBagConstraints(0, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				componentP.add(obenField, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				gridY++;

				JLabel unten = new JLabel("Untere Grenze:");
				final JValidatorTextField untenField = new JValidatorTextField(constrain.getLowerBorder().toString());
				untenField_ = untenField;
				untenField.setValidator(new ParserClassValidator(SAnalysisClassLoader.getClassLoader(), parameter.getValueClass()));
				untenField.addFocusListener(new FocusListener()
				{
					@Override
					public void focusLost(FocusEvent e)
					{
						setBound(constrain, untenField.getText(), false);
					}

					@Override
					public void focusGained(FocusEvent e)
					{}
				});
				untenField.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						setBound(constrain, untenField.getText(), false);
					}
				});

				unten.setPreferredSize(new Dimension(150, 20));
				untenField.setPreferredSize(new Dimension(400, 20));
				componentP.add(unten, new GridBagConstraints(0, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				componentP.add(untenField, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				gridY++;

				componentP.add(freePanel, new GridBagConstraints(0, gridY, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

			}
		});
	}

	private void setBound(ABorderConstraint constrain, String text, Boolean up)
	{
		try
		{
			String[] imports = { "jadex.simulation.analysis.common.data.*", "jadex.simulation.analysis.common.data.parameter.*" };
			IParsedExpression pex = new JavaCCExpressionParser().parseExpression(text, imports, null, SAnalysisClassLoader.getClassLoader());
			Object value = pex.getValue(null);
			if (value.getClass().equals(Double.class))
			{
				Double dValue = (Double) value;
				if (up)
				{
					if (!(constrain.getUpperBorder() == dValue))
					{
						constrain.setUpperBorder(dValue);
					}
				}
				else
				{
					if (!(constrain.getLowerBorder() == dValue))
					{
						constrain.setLowerBorder(dValue);
					}
				}

			}
			else
			{
				throw new RuntimeException();
			}

		}
		catch (Exception ex)
		{
			// JOptionPane.showMessageDialog(SwingUtilities.getRoot(view.getComponent()), "Aktueller Wert (" + parameter.getValueClass() + ") ist nicht zulässig!");
		}
	}

	@Override
	public void update(final IAEvent event)
	{
		super.update(event);

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if (event.getCommand().equals(AConstants.DATA_EDITABLE))
					{
						untenField_.setEditable((Boolean) ((ADataEvent) event).getValue());
						obenField_.setEditable((Boolean) ((ADataEvent) event).getValue());
					}
				}
		});
	}

	public static void main(String[] args)
	{
		AConstraintParameter para = new AConstraintParameter("Parameter", 5.0);
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
