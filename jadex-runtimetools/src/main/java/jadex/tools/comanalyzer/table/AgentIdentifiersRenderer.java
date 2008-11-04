package jadex.tools.comanalyzer.table;

import jadex.bridge.IAgentIdentifier;
import jadex.commons.SReflect;

import java.awt.Component;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * A renderer for AgentIdentifiers. This class is used to display the
 * receivers entry in the table. Each receiver is displayed with its addresses.
 */
class AgentIdentifiersRenderer extends DefaultTableCellRenderer
{
	/**
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
	 * Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
		Iterator it = SReflect.getIterator(value);

		IAgentIdentifier aid = (IAgentIdentifier)it.next();
		String content = aid.getName();
		String tooltip = "<b>" + aid.getName() + "</b>";
		String[] addresses = aid.getAddresses();
		for(int i = 0;i<addresses.length; i++)
		{
			tooltip += "<br>" + addresses[i];
		}
		
		while(it.hasNext())
		{
			aid = (IAgentIdentifier)it.next();
			content += ", " + aid.getName();
			tooltip += "<br><b>" + aid.getName() + "</b>";
			addresses = aid.getAddresses();
			for(int j = 0; j < addresses.length; j++)
			{
				tooltip += "<br>" + addresses[j];
			}
		}
		setText(content);
		setToolTipText("<html>" + tooltip + "</html>");
		return this;
	}
}
