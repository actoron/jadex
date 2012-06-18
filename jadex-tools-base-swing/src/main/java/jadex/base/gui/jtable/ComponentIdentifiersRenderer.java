package jadex.base.gui.jtable;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SReflect;

import java.awt.Component;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * A renderer for AgentIdentifiers. This class is used to display the
 * receivers entry in the table. Each receiver is displayed with its addresses.
 */
public class ComponentIdentifiersRenderer extends DefaultTableCellRenderer
{
	/**
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
	 * Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
		IComponentIdentifier aid;
		Iterator it = null;
		if(SReflect.isIterable(value))
		{
			it = SReflect.getIterator(value);
			aid = (IComponentIdentifier)it.next();
		}
		else
		{
			aid = (IComponentIdentifier)value;
		}
		
		String content = aid.getName();
		String tooltip = "<b>" + aid.getName() + "</b>";
		String[] addresses = aid.getAddresses();
		for(int i = 0;i<addresses.length; i++)
		{
			tooltip += "<br>" + addresses[i];
		}
			
		if(it!=null)
		{
			while(it.hasNext())
			{
				aid = (IComponentIdentifier)it.next();
				content += ", " + aid.getName();
				tooltip += "<br><b>" + aid.getName() + "</b>";
				addresses = aid.getAddresses();
				for(int j = 0; j < addresses.length; j++)
				{
					tooltip += "<br>" + addresses[j];
				}
			}
		}
		
		setText(content);
		setToolTipText("<html>" + tooltip + "</html>");
		return this;
	}
}
