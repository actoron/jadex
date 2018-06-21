package jadex.wfms.client.standard.parametergui;

import jadex.wfms.parametertypes.ListChoice;

import java.awt.GridBagConstraints;

import javax.swing.JComboBox;

public class ListChoiceParameterPanel extends AbstractParameterPanel
{
	private JComboBox parameterBox;
	
	private ListChoice parameterValue;
	
	public ListChoiceParameterPanel(String parameterName, ListChoice initialValue, boolean readOnly)
	{
		super(parameterName, readOnly);
		if (initialValue == null)
			throw new RuntimeException("Uninitialized ListChoice: " + parameterName);
		
		parameterValue = initialValue;
		parameterBox = new JComboBox(parameterValue.getChoices());
		if (initialValue.getSelection() != null)
			parameterBox.setSelectedItem(initialValue.getSelection());
		parameterBox.setEditable(false);
		
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.weightx = 1;
		add(parameterBox, g);
	}
	
	public boolean isParameterValueValid()
	{
		if (parameterBox.getSelectedItem() == null)
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
		parameterValue.setSelection((String) parameterBox.getSelectedItem());
		return parameterValue;
	}
}
