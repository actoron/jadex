package jadex.tools.dfbrowser;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import jadex.base.gui.jtable.ComponentIdentifierRenderer;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.df.IProperty;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.gui.jtable.TableSorter;

/**
 *  Table showing the df component services.
 */
public class DFServiceTable extends JTable//JScrollPane
{
	/**
	 * Constructor.
	 */
	public DFServiceTable()
	{
		super(new TableSorter(new ServiceTableModel()));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableSorter sorter = (TableSorter)getModel();
		sorter.setTableHeader(getTableHeader());
		setDefaultRenderer(IComponentIdentifier.class, new ComponentIdentifierRenderer(null));
		setDefaultRenderer(String[].class, new StringArrayRenderer());
		setDefaultRenderer(IProperty[].class, new PropertyArrayRenderer());
	}

	/**
	 *  Get the selected component/service description.
	 *  @return The currently selected service/component description.
	 */
	public Object[] getSelectedServices()
	{
		Object[] ret = new Object[]{null, null};
		int sel = getSelectedRow();
		if(sel>=0)
		{
			TableSorter sorter = (TableSorter)getModel();
			ServiceTableModel model = (ServiceTableModel)sorter.getTableModel();
			sel = sorter.modelIndex(sel);
			ret = new Object[]{model.getServiceDescription(sel), model.getComponentDescription(sel)};
		}
		return ret;
	}
	
	/**
	 * Sets descriptions for this element
	 * @param ad
	 */
	public void setComponentDescriptions(IDFComponentDescription[] ad)
	{
		TableSorter sorter = (TableSorter)getModel();
		ServiceTableModel model = (ServiceTableModel)sorter.getTableModel();
		model.setComponentDescriptions(ad);
	}

	/**
	 * @param componentDescription
	 */
	public void setComponentDescription(IDFComponentDescription ad)
	{
		TableSorter sorter = (TableSorter)getModel();
		ServiceTableModel model = (ServiceTableModel)sorter.getTableModel();
		model.setComponentDescription(ad);
	}

	/**
	 *  Get the properties.
	 *  @param props The properties.
	 */
	public Properties	getProperties()
	{
		Properties	props	= new Properties();
		TableColumnModel cm = getColumnModel();
		for(int i=0; i<cm.getColumnCount(); i++)
		{
			TableColumn column = cm.getColumn(i);
			props.addProperty(new Property("columnwidth", Integer.toString(column.getWidth())));
		}
		return props;
	}

	/**
	 *  Set the properties.
	 *  @param props The properties.
	 */
	public void setProperties(Properties props)
	{
		Property[]	columnprops	= props.getProperties("columnwidth");
		TableColumnModel cm = getColumnModel();
		for(int i=0; i<cm.getColumnCount() && i<columnprops.length; i++)
		{
			cm.getColumn(i).setPreferredWidth(Integer.parseInt(columnprops[i].getValue()));
		}
	}
}
