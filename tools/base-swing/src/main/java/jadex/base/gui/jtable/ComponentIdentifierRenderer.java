package jadex.base.gui.jtable;

import java.awt.Component;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.commons.SUtil;
import jadex.commons.collection.LRU;
import jadex.commons.collection.SortedList;
import jadex.commons.future.IResultListener;


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
		this.platform = platform!=null ? platform.getRoot() : null;
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
	
	private Map<IComponentIdentifier, String>	adrcache	= Collections.synchronizedMap(new LRU<IComponentIdentifier, String>());

	public String getTooltipText(final IComponentIdentifier cid)
	{
		String tooltip = "<b>" + cid.getName() + "</b>";
		if(platform!=null)
		{
			String	adrtip	= adrcache.get(cid);
			
			// On first access display info message.
			if(adrtip==null)
			{
				adrtip	= "<br> fetching addresses, please retry...";
				adrcache.put(cid, adrtip);
			}
			
			// Try fetching each time to also receive updates
			try
			{
				ITransportAddressService	tas	= platform.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( ITransportAddressService.class));
				tas.getAddresses(cid.getRoot()).addResultListener(new IResultListener<List<TransportAddress>>()
				{
					@Override
					public void resultAvailable(List<TransportAddress> addresses)
					{
						if(addresses!=null && !addresses.isEmpty())
						{
							SortedList<TransportAddress>	sl	= new SortedList<TransportAddress>(new Comparator<TransportAddress>()
							{
								public int compare(TransportAddress o1, TransportAddress o2)
								{
									return (o1.getTransportType() + "://" + o1.getAddress())
										.compareTo(o2.getTransportType() + "://" + o2.getAddress());
								};
							}, true);
							sl.addAll(addresses);
							String	adrtip	= "";
							for(TransportAddress adr: sl)
							{
								adrtip += "<br>" + adr.getTransportType() + "://" + adr.getAddress();
							}
							adrcache.put(cid, adrtip);
						}
						else
						{
							adrcache.put(cid, "<br>" + "no addresses found");
						}
					}

					@Override
					public void exceptionOccurred(Exception exception)
					{
						adrcache.put(cid, "<br> failed to fetch addresses: "+exception);
					}
				});
			}
			catch(Exception e)
			{
				adrtip	= "<br> failed to fetch addresses: "+e;
				adrcache.put(cid, adrtip);
			}
			
			// Display what we have until now
			tooltip	+= adrtip;
		}
		return "<html>" + tooltip + "</html>";
	}
}
