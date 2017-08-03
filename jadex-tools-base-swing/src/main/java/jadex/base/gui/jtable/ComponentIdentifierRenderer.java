package jadex.base.gui.jtable;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.commons.SUtil;


/**
 * A renderer for AgentIdentifiers. This class is used to display the receiver
 * entry in the table. The receiver is displayed with its addresses.
 */
public class ComponentIdentifierRenderer extends DefaultTableCellRenderer
{
	/** The local platform. */
	protected IComponentIdentifier platform;
	
	/**
	 * Create a new ComponentIdentifierRenderer.
	 */
	public ComponentIdentifierRenderer()
	{
	}
	
	/**
	 * Create a new ComponentIdentifierRenderer.
	 */
	public ComponentIdentifierRenderer(IComponentIdentifier platform)
	{
		this.platform = platform.getRoot();
	}
	
	/**
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
	 * Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
		IComponentIdentifier cid = (IComponentIdentifier)value;
		if(cid!=null)
		{
			setText(SUtil.equals(cid, platform)? "local": cid.getName());
			setToolTipText(getTooltipText(cid));
		}
		return this;
	}

	public static String getTooltipText(IComponentIdentifier cid)
	{
		String[] addresses = cid instanceof ITransportComponentIdentifier ? ((ITransportComponentIdentifier)cid).getAddresses() : null;
		String tooltip = "<b>" + cid.getName() + "</b>";
		if(addresses!=null)
		{
			for(int i=0; i<addresses.length; i++)
			{
				tooltip += "<br>" + addresses[i];
			}
		}
		tooltip	= "<html>" + tooltip + "</html>";
		return tooltip;
	}
}
