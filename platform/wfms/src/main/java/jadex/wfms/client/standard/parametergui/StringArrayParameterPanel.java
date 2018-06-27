package jadex.wfms.client.standard.parametergui;

import jadex.wfms.guicomponents.SGuiHelper;
import jadex.wfms.guicomponents.StringTable;
import jadex.wfms.guicomponents.StringTable.DefaultStringTableModel;

import java.util.Map;

public class StringArrayParameterPanel extends AbstractParameterPanel
{
	private StringTable parameterTable;
	
	public StringArrayParameterPanel(final String parameterName, String[] initialValue, final Map metaProperties, final boolean readOnly)
	{
		super(parameterName, readOnly);
		
		DefaultStringTableModel tableModel = new DefaultStringTableModel(SGuiHelper.beautifyName(parameterName, metaProperties), readOnly);

		if (initialValue != null)
			for (int i = 0; i < initialValue.length; ++i)
				tableModel.addString(initialValue[i]);
		parameterTable = new StringTable(tableModel, StringTable.TEXT_BUTTONS);
		
		add(parameterTable);
	}
	
	public boolean isParameterValueValid()
	{
		return true;
	}
	
	public boolean requiresLabel()
	{
		return false;
	}
	
	public Object getParameterValue()
	{
		return ((DefaultStringTableModel) parameterTable.getModel()).getStringsAsArray();
	}
}
