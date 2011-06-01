package jadex.wfms.client.standard.parametergui;

import jadex.wfms.client.standard.SNumberUtils;

import java.awt.GridBagConstraints;

import javax.swing.JTextField;

public class NumericParameterPanel extends AbstractParameterPanel
{
	private Class parameterType; 
	
	private JTextField parameterField;
	
	public NumericParameterPanel(String parameterName, Class parameterType, Number initialValue, boolean readOnly)
	{
		super(parameterName, readOnly);
		parameterField = new JTextField();
		parameterField.setEditable(!readOnly);
		if (initialValue != null)
			parameterField.setText(String.valueOf(initialValue));
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.weightx = 1;
		add(parameterField, g);
		this.parameterType = parameterType;
	}
	
	public boolean isParameterValueValid()
	{
		try
		{
			SNumberUtils.parseNumber(parameterType, parameterField.getText());
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
	
	public boolean requiresLabel()
	{
		return true;
	}
	
	public Object getParameterValue()
	{
		return SNumberUtils.parseNumber(parameterType, parameterField.getText());
	}
}
