package sodekovs.swing.jcc.plugins.benchmarking;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.df.IProperty;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.gui.jtable.TableSorter;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import sodekovs.util.model.benchmarking.description.IHistoricDataDescription;

/**
 * Table showing historic data found in a db.
 */
public class HistoricDataTable extends JTable// JScrollPane
{
	/**
	 * Constructor.
	 */
	public HistoricDataTable() {
		super(new TableSorter(new HistoricDataTableModel()));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableSorter sorter = (TableSorter) getModel();
		sorter.setTableHeader(getTableHeader());
		setDefaultRenderer(IComponentIdentifier.class, new ComponentIdentifierRenderer());
		setDefaultRenderer(String[].class, new StringArrayRenderer());
		setDefaultRenderer(IProperty[].class, new PropertyArrayRenderer());
	}

	/**
	 * Get the selected history description.
	 * 
	 * @return The currently selected history description.
	 */
	public IHistoricDataDescription getSelectedHistoricDataDescription() {
		// Object[] ret = new Object[]{null, null};
		IHistoricDataDescription ret = null;
		int sel = getSelectedRow();

		// Hack: somehow sel is sometimes -1 : avoid nullpointer-exception with this hack.
		if (sel < 0) {
			sel = 0;
		}

		// if(sel>=0)
		// {
		TableSorter sorter = (TableSorter) getModel();
		HistoricDataTableModel model = (HistoricDataTableModel) sorter.getTableModel();
		sel = sorter.modelIndex(sel);
		ret = model.getHistoricDataDescription(sel);
		// }else if(sel == -1){
		// sel=0;
		// System.out.println("YYYYYYYYYYYYYYES");
		// TableSorter sorter = (TableSorter)getModel();
		// HistoricDataTableModel model = (HistoricDataTableModel)sorter.getTableModel();
		// sel = sorter.modelIndex(sel);
		// ret = model.getHistoricDataDescription(sel);
		// }
		return ret;
	}

	/**
	 * Sets descriptions for this element
	 * 
	 * @param ad
	 */
	public void setComponentDescriptions(IHistoricDataDescription[] histDataDesc) {
		TableSorter sorter = (TableSorter) getModel();
		HistoricDataTableModel model = (HistoricDataTableModel) sorter.getTableModel();
		// model.setComponentDescriptions(ad);
		model.setHistoricDataDescriptions(histDataDesc);
	}

	// /**
	// * @param componentDescription
	// */
	// public void setComponentDescription(IBenchmarkingDescription ad)
	// {
	// TableSorter sorter = (TableSorter)getModel();
	// BenchmarkingTableModel model = (BenchmarkingTableModel)sorter.getTableModel();
	// // model.setComponentDescription(ad);
	// }

	/**
	 * Get the properties.
	 * 
	 * @param props
	 *            The properties.
	 */
	public Properties getProperties() {
		Properties props = new Properties();
		TableColumnModel cm = getColumnModel();
		for (int i = 0; i < cm.getColumnCount(); i++) {
			TableColumn column = cm.getColumn(i);
			props.addProperty(new Property("columnwidth", Integer.toString(column.getWidth())));
		}
		return props;
	}

	/**
	 * Set the properties.
	 * 
	 * @param props
	 *            The properties.
	 */
	public void setProperties(Properties props) {
		Property[] columnprops = props.getProperties("columnwidth");
		TableColumnModel cm = getColumnModel();
		for (int i = 0; i < cm.getColumnCount() && i < columnprops.length; i++) {
			cm.getColumn(i).setPreferredWidth(Integer.parseInt(columnprops[i].getValue()));
		}
	}
}
