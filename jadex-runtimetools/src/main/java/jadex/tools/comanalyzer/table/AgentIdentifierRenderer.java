package jadex.tools.comanalyzer.table;

import jadex.bridge.IAgentIdentifier;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * A renderer for AgentIdentifiers. This class is used to display the receiver
 * entry in the table. The receiver is displayed with its addresses.
 */
class AgentIdentifierRenderer extends DefaultTableCellRenderer
{

	/**
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
	 * Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
		IAgentIdentifier aid = (IAgentIdentifier)value;
		if(aid == null)
			return this;
		setText(aid.getName());
		String[] addresses = aid.getAddresses();
		String tooltip = "<b>" + aid.getName() + "</b>";
		for(int i = 0; i < addresses.length; i++)
		{
			tooltip += "<br>" + addresses[i];
		}
		setToolTipText("<html>" + tooltip + "</html>");
		return this;
	}
}
