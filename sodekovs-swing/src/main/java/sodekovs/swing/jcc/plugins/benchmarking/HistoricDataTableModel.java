package sodekovs.swing.jcc.plugins.benchmarking;


import javax.swing.table.AbstractTableModel;

import sodekovs.util.model.benchmarking.description.IHistoricDataDescription;

/**
 *
 */
class HistoricDataTableModel extends AbstractTableModel
{

	IHistoricDataDescription[] histDataDesc;

	/**
	 * @return 3
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return 3;
	}

	/**
	 * @param ads
	 */
	public void setHistoricDataDescriptions(IHistoricDataDescription[] histDataDesc)
	{
//		ArrayList ad_list = new ArrayList();
//		ArrayList svd_list = new ArrayList();
//
//		for(int a = 0; a < ads.length; a++)
//		{
//			IDFComponentDescription ad = ads[a];
//			IDFServiceDescription[] sd = ads[a].getServices();
//			for(int s = 0; s < sd.length; s++)
//			{
//				ad_list.add(ad);
//				svd_list.add(sd[s]);
//			}
//		}
//		this.ad = (IDFComponentDescription[])ad_list.toArray(new IDFComponentDescription[ad_list.size()]);
		this.histDataDesc = histDataDesc;

		fireTableDataChanged();
	}

//	/**
//	 * @param ad
//	 */
//	public void setComponentDescription(IDFComponentDescription ad)
//	{
//		IDFServiceDescription[] sd = ad.getServices();
//		IDFComponentDescription[] aid = new IDFComponentDescription[sd.length];
//		for(int s = 0; s < sd.length; s++)
//		{
//			aid[s] = ad;
//		}
//		this.ad = aid;
//		this.bechnDesc = sd;
//
//		fireTableDataChanged();
//	}

	/**
	 * @return all component subscriptions length
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount()
	{
		return histDataDesc != null ? histDataDesc.length : 0;
	}

	/**
	 * @param rowIndex
	 * @param columnIndex
	 * @return the values of this table
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(histDataDesc == null || rowIndex < 0 || rowIndex >= histDataDesc.length)
		{
			return null;
		}
		switch(columnIndex)
		{
			case 0:
				return histDataDesc[rowIndex].getName();
			case 1:
				return histDataDesc[rowIndex].getType();
			case 2:
				return histDataDesc[rowIndex].getTimestamp();
//			case 3:
//				return histDataDesc[rowIndex].getStatus();
//			case 4:
//				return bechnDesc[rowIndex].getOntologies();
//			case 5:
//				return bechnDesc[rowIndex].getLanguages();
//			case 6:
//				return bechnDesc[rowIndex].getProtocols();
//			case 7:
//				return bechnDesc[rowIndex].getProperties();
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
				return "Timestamp";
//			case 3:
//				return "Status";
//			case 4:
//				return "Ontologies";
//			case 5:
//				return "Languages";
//			case 6:
//				return "Protocols";
//			case 7:
//				return "Properties";
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
//			case 3:
//				return String.class;
//			case 4:
//				return String[].class;
//			case 5:
//				return String[].class;
//			case 6:
//				return String[].class;
//			case 7:
//				return IProperty[].class;
		}
		return null;
	}

	/**
	 * @param i
	 * @return the service description at row i
	 */
	public IHistoricDataDescription getHistoricDataDescription(int i)
	{
		return histDataDesc == null || i < 0 || i >= histDataDesc.length ? null : histDataDesc[i];
	}

//	/**
//	 * @param i
//	 * @return the aid of the service at row i
//	 */
//	public IDFComponentDescription getComponentDescription(int i)
//	{
//		return ad == null || i < 0 || i >= ad.length ? null : ad[i];
//	}

}
