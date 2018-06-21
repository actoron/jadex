package jadex.wfms.client.standard.parametergui;

import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;

public class BooleanParameterPanel extends AbstractParameterPanel
{
	private JCheckBox parameterBox;
	
	public BooleanParameterPanel(String parameterName, Boolean initialValue, boolean readOnly)
	{
		super(parameterName, readOnly);
		parameterBox = new JCheckBox();
		parameterBox.setEnabled(!readOnly);
		if (initialValue != null)
			parameterBox.setSelected(initialValue.booleanValue());
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.weightx = 1;
		add(parameterBox, g);
	}
	
	public boolean isParameterValueValid()
	{
		return true;
	}
	
	public boolean requiresLabel()
	{
		return true;
	}
	
	public Object getParameterValue()
	{
		return new Boolean(parameterBox.isSelected());
	}
}
