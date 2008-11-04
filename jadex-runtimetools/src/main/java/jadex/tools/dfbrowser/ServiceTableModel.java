package jadex.tools.dfbrowser;

import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.IDFServiceDescription;
import jadex.adapter.base.fipa.IProperty;
import jadex.bridge.IAgentIdentifier;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 *
 */
class ServiceTableModel extends AbstractTableModel
{
	IDFAgentDescription[] ad;

	IDFServiceDescription[] sd;

	/**
	 * @return 8
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return 8;
	}

	/**
	 * @param ads
	 */
	public void setAgentDescriptions(IDFAgentDescription[] ads)
	{
		ArrayList ad_list = new ArrayList();
		ArrayList svd_list = new ArrayList();

		for(int a = 0; a < ads.length; a++)
		{
			IDFAgentDescription ad = ads[a];
			IDFServiceDescription[] sd = ads[a].getServices();
			for(int s = 0; s < sd.length; s++)
			{
				ad_list.add(ad);
				svd_list.add(sd[s]);
			}
		}
		this.ad = (IDFAgentDescription[])ad_list.toArray(new IDFAgentDescription[ad_list.size()]);
		this.sd = (IDFServiceDescription[])svd_list.toArray(new IDFServiceDescription[svd_list.size()]);

		fireTableDataChanged();
	}

	/**
	 * @param ad
	 */
	public void setAgentDescription(IDFAgentDescription ad)
	{
		IDFServiceDescription[] sd = ad.getServices();
		IDFAgentDescription[] aid = new IDFAgentDescription[sd.length];
		for(int s = 0; s < sd.length; s++)
		{
			aid[s] = ad;
		}
		this.ad = aid;
		this.sd = sd;

		fireTableDataChanged();
	}

	/**
	 * @return all agent subscriptions length
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount()
	{
		return sd != null ? sd.length : 0;
	}

	/**
	 * @param rowIndex
	 * @param columnIndex
	 * @return the values of this table
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(sd == null || rowIndex < 0 || rowIndex >= sd.length)
		{
			return null;
		}
		switch(columnIndex)
		{
			case 0:
				return sd[rowIndex].getName();
			case 1:
				return sd[rowIndex].getType();
			case 2:
				return sd[rowIndex].getOwnership();
			case 3:
				return ad[rowIndex].getName();
			case 4:
				return sd[rowIndex].getOntologies();
			case 5:
				return sd[rowIndex].getLanguages();
			case 6:
				return sd[rowIndex].getProtocols();
			case 7:
				return sd[rowIndex].getProperties();
		}

		return null;
	}

	/**
	 * @param columnIndex
	 * @return the name of a columnt
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int columnIndex)
	{
		switch(columnIndex)
		{
			case 0:
				return "Name";
			case 1:
				return "Type";
			case 2:
				return "Ownership";
			case 3:
				return "Agent";
			case 4:
				return "Ontologies";
			case 5:
				return "Languages";
			case 6:
				return "Protocols";
			case 7:
				return "Properties";
		}
		return null;
	}

	/**
	 * @param columnIndex
	 * @return the class of a column
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int columnIndex)
	{
		switch(columnIndex)
		{
			case 0:
				return String.class;
			case 1:
				return String.class;
			case 2:
				return String.class;
			case 3:
				return IAgentIdentifier.class;
			case 4:
				return String[].class;
			case 5:
				return String[].class;
			case 6:
				return String[].class;
			case 7:
				return IProperty[].class;
		}
		return null;
	}

	/**
	 * @param i
	 * @return the service description at row i
	 */
	public IDFServiceDescription getServiceDescription(int i)
	{
		return sd == null || i < 0 || i >= sd.length ? null : sd[i];
	}

	/**
	 * @param i
	 * @return the aid of the service at row i
	 */
	public IDFAgentDescription getAgentDescription(int i)
	{
		return ad == null || i < 0 || i >= ad.length ? null : ad[i];
	}

}
