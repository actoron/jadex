package jadex.base.gui.componenttree;

import jadex.bridge.IComponentDescription;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.gui.PropertiesPanel;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *  Panel for showing component properties.
 */
public class ComponentProperties	extends	PropertiesPanel
{
	//-------- constructors --------
	
	/**
	 *  Create new component properties panel.
	 */
	public ComponentProperties()
	{
		super(" Component Properties ");

		createTextField("Name");
		
		addFullLineComponent("Addresses_label", new JLabel("Addresses"));
		addFullLineComponent("Addresses", SGUI.createReadOnlyTable());
		
		createTextField("Type");
		createTextField("Model name");
		createTextField("Ownership");
		createTextField("State");
		createTextField("Processing state");
		
		createCheckBox("Master");
		createCheckBox("Daemon");
		createCheckBox("Auto shutdown");
	}
	
	//-------- methods --------
	
	/**
	 *  Set the description.
	 */
	public void	setDescription(IComponentDescription desc)
	{
		getTextField("Name").setText(desc.getName().getName());
		getTextField("Type").setText(desc.getType());
		getTextField("Model name").setText(desc.getModelName());
		getTextField("Ownership").setText(desc.getOwnership());
		getTextField("State").setText(desc.getState());
		getTextField("Processing state").setText(desc.getProcessingState());
		getCheckBox("Master").setSelected(desc.isMaster());
		getCheckBox("Daemon").setSelected(desc.isDaemon());
		getCheckBox("Auto shutdown").setSelected(desc.isAutoShutdown());
		
		JTable	list	= (JTable)getComponent("Addresses");
		String[]	addresses	= desc.getName().getAddresses();
		DefaultTableModel	dtm	= new DefaultTableModel();
		dtm.addColumn("Addresses", addresses!=null?addresses:SUtil.EMPTY_STRING_ARRAY);
		list.setModel(dtm);
	}
}
