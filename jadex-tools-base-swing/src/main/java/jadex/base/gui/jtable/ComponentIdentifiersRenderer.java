package jadex.base.gui.jtable;

import java.awt.Component;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.commons.SReflect;


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
		IComponentIdentifier cid;
		Iterator it = null;
		if(SReflect.isIterable(value))
		{
			it = SReflect.getIterator(value);
			cid = (IComponentIdentifier)it.next();
		}
		else
		{
			cid = (IComponentIdentifier)value;
		}
		
		String content = cid.getName();
		String tooltip = "<b>" + cid.getName() + "</b>";
		String[] addresses = cid instanceof ITransportComponentIdentifier ? ((ITransportComponentIdentifier)cid).getAddresses() : null;
		for(int i=0; addresses!=null && i<addresses.length; i++)
		{
			tooltip += "<br>" + addresses[i];
		}
			
		if(it!=null)
		{
			while(it.hasNext())
			{
				cid = (IComponentIdentifier)it.next();
				content += ", " + cid.getName();
				tooltip += "<br><b>" + cid.getName() + "</b>";
				addresses = cid instanceof ITransportComponentIdentifier ? ((ITransportComponentIdentifier)cid).getAddresses() : null;
				for(int j=0; addresses!=null && j<addresses.length; j++)
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
