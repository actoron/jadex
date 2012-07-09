package jadex.base.gui.componenttree;

import jadex.bridge.ILocalResourceIdentifier;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.SUtil;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;

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
		createTextField("Resource Identifier");
		createTextField("(global / local)");
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
		String gid = desc.getResourceIdentifier().getGlobalIdentifier().getResourceId();
		ILocalResourceIdentifier lid = desc.getResourceIdentifier().getLocalIdentifier();
		getTextField("Resource Identifier").setText(gid==null? "n/a": gid);
		getTextField("(global / local)").setText(lid==null? "n/a": lid.toString());
//		getTextField("Processing state").setText(desc.getProcessingState());
		getCheckBox("Master").setSelected(desc.getMaster()==null? false: desc.getMaster().booleanValue());
		getCheckBox("Daemon").setSelected(desc.getDaemon()==null? false: desc.getDaemon().booleanValue());
		getCheckBox("Auto shutdown").setSelected(desc.getAutoShutdown()==null? false: desc.getAutoShutdown().booleanValue());
		
		JTable	list	= (JTable)getComponent("Addresses");
		String[]	addresses	= desc.getName().getAddresses();
		DefaultTableModel	dtm	= new DefaultTableModel();
		dtm.addColumn("Addresses", addresses!=null?addresses:SUtil.EMPTY_STRING_ARRAY);
		list.setModel(dtm);
	}
}
