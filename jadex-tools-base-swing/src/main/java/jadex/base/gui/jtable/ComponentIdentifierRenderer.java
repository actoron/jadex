package jadex.base.gui.jtable;

import java.awt.Component;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;


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
	 * @param platform	CID of the local platform, if any. Used to mark local platfor and check for known addresses in tool tip.
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

	public String getTooltipText(IComponentIdentifier cid)
	{
		String tooltip = "<b>" + cid.getName() + "</b>";
		if(platform!=null)
		{
			ITransportAddressService	tas	= SServiceProvider.getLocalService(platform, ITransportAddressService.class);
			IFuture<List<TransportAddress>>	fut	= tas.getAddresses(cid.getRoot());
			String[] addresses	= fut.isDone() ? fut.get().toArray(new String[0]) : null;
			
			if(addresses!=null)
			{
				for(int i=0; i<addresses.length; i++)
				{
					tooltip += "<br>" + addresses[i];
				}
			}
			else
			{
				tooltip += "<br>" + "no addresses found";
			}
			tooltip	= "<html>" + tooltip + "</html>";
		}
		return tooltip;
	}
}
