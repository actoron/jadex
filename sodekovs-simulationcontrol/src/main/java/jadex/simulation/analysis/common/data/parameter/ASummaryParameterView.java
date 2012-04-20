package jadex.simulation.analysis.common.data.parameter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import jadex.simulation.analysis.common.data.IADataView;

/**
 * View of ASummaryParameter
 * @author 5Haubeck
 */
public class ASummaryParameterView extends ABasicParameterView implements IADataView
{
	private ASummaryParameter parameter;

	public ASummaryParameterView(ASummaryParameter parameter)
	{
		super(parameter);
		this.parameter = parameter;
		initSummary();
	}

	private void initSummary()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Insets insets = new Insets(1, 1, 1, 1);

				componentP.remove(freePanel);
				componentP.remove(valueComp);
				componentP.remove(valueLabel);

				int gridY = componentP.getComponentCount() + 1;
				paraTypeValue.setText("ASummaryParameter");
				
				JLabel nLabel = new JLabel("Beobachtungen:");
				nLabel.setPreferredSize(new Dimension(150,20));
				componentP.add(nLabel, new GridBagConstraints(0, gridY, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JTextField nValue = new JTextField(parameter.getN().toString());
				nValue.setEditable(false);
				nValue.setPreferredSize(new Dimension(400, 20));
				componentP.add(nValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				gridY++;
				
				JLabel meanLabel = new JLabel("Durchschnitt:");
				meanLabel.setPreferredSize(new Dimension(150,20));
				componentP.add(meanLabel, new GridBagConstraints(0, gridY, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JTextField meanValue = new JTextField(parameter.getMeanValue().toString());
				meanValue.setEditable(false);
				meanValue.setPreferredSize(new Dimension(400, 20));
				componentP.add(meanValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				gridY++;
				
				JLabel varLabel = new JLabel("Varianz:");
				varLabel.setPreferredSize(new Dimension(150,20));
				componentP.add(varLabel, new GridBagConstraints(0, gridY, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JTextField varValue = new JTextField(parameter.getVarianceValue().toString());
				varValue.setEditable(false);
				varValue.setPreferredSize(new Dimension(400, 20));
				componentP.add(varValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				gridY++;
				
				JLabel stdLabel = new JLabel("Stndardabweichung:");
				stdLabel.setPreferredSize(new Dimension(150,20));
				componentP.add(stdLabel, new GridBagConstraints(0, gridY, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JTextField stdValue = new JTextField(parameter.getStandardDeviationValue().toString());
				stdValue.setEditable(false);
				stdValue.setPreferredSize(new Dimension(400, 20));
				componentP.add(stdValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				gridY++;
				
				JLabel minLabel = new JLabel("Minimum:");
				minLabel.setPreferredSize(new Dimension(150,20));
				componentP.add(minLabel, new GridBagConstraints(0, gridY, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JTextField minValue = new JTextField(parameter.getMinValue().toString());
				minValue.setEditable(false);
				minValue.setPreferredSize(new Dimension(400, 20));
				componentP.add(minValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				gridY++;
				
				JLabel maxLabel = new JLabel("Maximum:");
				maxLabel.setPreferredSize(new Dimension(150,20));
				componentP.add(maxLabel, new GridBagConstraints(0, gridY, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JTextField maxValue = new JTextField(parameter.getMaxValue().toString());
				maxValue.setEditable(false);
				maxValue.setPreferredSize(new Dimension(400, 20));
				componentP.add(maxValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				gridY++;
				
				JLabel sumLabel = new JLabel("Summe:");
				sumLabel.setPreferredSize(new Dimension(150,20));
				componentP.add(sumLabel, new GridBagConstraints(0, gridY, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JTextField sumValue = new JTextField(parameter.getSumValue().toString());
				sumValue.setEditable(false);
				sumValue.setPreferredSize(new Dimension(400, 20));
				componentP.add(sumValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				gridY++;
				
//				JLabel lastLabel = new JLabel("Letzter Wert:");
//				lastLabel.setPreferredSize(new Dimension(150,20));
//				componentP.add(lastLabel, new GridBagConstraints(0, gridY, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

//				JTextField lastValue = new JTextField(parameter.getLastValue().toString());
//				lastValue.setEditable(false);
//				lastValue.setPreferredSize(new Dimension(400, 20));
//				componentP.add(lastValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
//				gridY++;
				
				componentP.add(freePanel, new GridBagConstraints(0, gridY, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

			}
		});

	}
	
	public static void main(String[] args)
	{
		ASummaryParameter para = new ASummaryParameter("Parameter");
		para.setEditable(true);
		para.addValue(100.0);
		para.addValue(50.0);
		para.addValue(60.0);
		para.addValue(80.0);
		para.addValue(120.0);
		para.addValue(20.0);
		para.addValue(20.0);
		para.addValue(110.0);
		para.addValue(20.0);
		para.addValue(10.0);
		

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(550, 350);
		frame.add(para.getView().getComponent());
		frame.setVisible(true);
		
	}

}
