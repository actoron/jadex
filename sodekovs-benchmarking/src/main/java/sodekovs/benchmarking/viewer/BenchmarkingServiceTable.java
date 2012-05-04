package sodekovs.benchmarking.viewer;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.df.IProperty;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.gui.jtable.TableSorter;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import sodekovs.util.model.benchmarking.description.IBenchmarkingDescription;

/**
 * Table showing the df component services.
 */
public class BenchmarkingServiceTable extends JTable// JScrollPane
{
	/**
	 * Constructor.
	 */
	public BenchmarkingServiceTable() {
		super(new TableSorter(new BenchmarkingTableModel()));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableSorter sorter = (TableSorter) getModel();
		sorter.setTableHeader(getTableHeader());
		setDefaultRenderer(IComponentIdentifier.class, new ComponentIdentifierRenderer());
		setDefaultRenderer(String[].class, new StringArrayRenderer());
		setDefaultRenderer(IProperty[].class, new PropertyArrayRenderer());
	}

	// /**
	// * Get the selected component/service description.
	// * @return The currently selected service/component description.
	// */
	// public Object[] getSelectedServices()
	// {
	// Object[] ret = new Object[]{null, null};
	// int sel = getSelectedRow();
	// if(sel>=0)
	// {
	// TableSorter sorter = (TableSorter)getModel();
	// BenchmarkingTableModel model = (BenchmarkingTableModel)sorter.getTableModel();
	// sel = sorter.modelIndex(sel);
	// ret = new Object[]{model.getServiceDescription(sel)};
	// }
	// return ret;
	// }

	/**
	 * Get the selected service description.
	 * 
	 * @return The currently selected history description.
	 */
	public IBenchmarkingDescription getSelectedService() {
		IBenchmarkingDescription ret = null;
		int sel = getSelectedRow();

		// Hack: somehow sel is sometimes -1 : avoid nullpointer-exception with this hack.
		if (sel < 0) {
			sel = 0;
		}

		// if(sel>=0)
		// {
		TableSorter sorter = (TableSorter) getModel();
		BenchmarkingTableModel model = (BenchmarkingTableModel) sorter.getTableModel();
		sel = sorter.modelIndex(sel);
		ret = model.getServiceDescription(sel);
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
	public void setComponentDescriptions(IBenchmarkingDescription[] benchDesc) {
		TableSorter sorter = (TableSorter) getModel();
		BenchmarkingTableModel model = (BenchmarkingTableModel) sorter.getTableModel();
		// model.setComponentDescriptions(ad);
		model.setBenchmarkingDescriptions(benchDesc);
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
