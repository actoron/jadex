package jadex.tools.dfbrowser;

import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.IProperty;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.Properties;
import jadex.bridge.Property;
import jadex.tools.common.TableSorter;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *  Table showing the df agent services.
 */
public class DFServiceTable extends JTable//JScrollPane
{
	/**
	 * Constructor for DFAgentTable.
	 */
	public DFServiceTable()
	{
		super(new TableSorter(new ServiceTableModel()));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableSorter sorter = (TableSorter)getModel();
		sorter.setTableHeader(getTableHeader());
		setDefaultRenderer(IAgentIdentifier.class, new AgentIdentifierRenderer());
		setDefaultRenderer(String[].class, new StringArrayRenderer());
		setDefaultRenderer(IProperty[].class, new PropertyArrayRenderer());
	}

	/**
	 *  Get the selected agent/service description.
	 *  @return The currently selected service/agent description.
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
			ret = new Object[]{model.getServiceDescription(sel), model.getAgentDescription(sel)};
		}
		return ret;
	}
	
	/**
	 * Sets Agent descriptions for this element
	 * @param ad
	 */
	public void setAgentDescriptions(IDFAgentDescription[] ad)
	{
		TableSorter sorter = (TableSorter)getModel();
		ServiceTableModel model = (ServiceTableModel)sorter.getTableModel();
		model.setAgentDescriptions(ad);
	}

	/**
	 * @param agentDescription
	 */
	public void setAgentDescription(IDFAgentDescription ad)
	{
		TableSorter sorter = (TableSorter)getModel();
		ServiceTableModel model = (ServiceTableModel)sorter.getTableModel();
		model.setAgentDescription(ad);
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
