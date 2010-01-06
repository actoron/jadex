package jadex.wfms.bdi.client.standard.parametergui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class StringParameterPanel extends AbstractParameterPanel
{
	private JTextField parameterField;
	
	public StringParameterPanel(String parameterName, String initialValue, boolean readOnly)
	{
		super(parameterName, readOnly);
		parameterField = new JTextField();
		parameterField.setEditable(!readOnly);
		if (initialValue != null)
			parameterField.setText(initialValue);
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.weightx = 1;
		add(parameterField, g);
	}
	
	public boolean isParameterValueValid()
	{
		if (parameterField.getText().isEmpty())
			return false;
		return true;
	}
	
	public Object getParameterValue()
	{
		return parameterField.getText();
	}
}
