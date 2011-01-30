package jadex.simulation.analysis.common.dataObjects;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class AParameterCollection extends HashMap<String, IAParameter> implements IAParameterCollection {

	@Override
	public Boolean isFeasable() {
		Boolean result = true;
		for (IAParameter para : values()) {
			if (!para.isFeasable())
				result = false;
		}
		return result;
	}

	@Override
	public void add(IAParameter parameter) {
		put(parameter.getName(), parameter);
	}

	@Override
	public JComponent getView(final Boolean option) {
		final IAParameterCollection me = this;
		final JComponent result = new JPanel(new GridBagLayout());
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				int x =1;
				Insets insets = new Insets(2, 2, 2, 2);
				for (IAParameter outputParameter : me.values()) 
				{
					JComponent comp = outputParameter.getView(option);
					result.add(comp, new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					x++;
				}
				result.updateUI();
				result.validate();
			}
		});

		return result;
	}
}
