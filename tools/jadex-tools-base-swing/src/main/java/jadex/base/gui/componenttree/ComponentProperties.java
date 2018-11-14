package jadex.base.gui.componenttree;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jadex.bridge.ILocalResourceIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.SUtil;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;

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
		createTextField("Creator");
		createTextField("Resource Identifier");
		createTextField("(global / local)");
		createTextField("Ownership");
		createTextField("State");
		createTextField("Processing state");
		
		createCheckBox("Master");
		createCheckBox("Daemon");
		createCheckBox("Auto shutdown");
		createCheckBox("Synchronous");
		createCheckBox("Persistable");
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
		getTextField("Creator").setText(desc.getCreator()!=null? desc.getCreator().getName(): "n/a");
		getTextField("Ownership").setText(desc.getOwnership());
		getTextField("State").setText(desc.getState());
		String gid = desc.getResourceIdentifier().getGlobalIdentifier()!=null? desc.getResourceIdentifier().getGlobalIdentifier().getResourceId(): "n/a";
		ILocalResourceIdentifier lid = desc.getResourceIdentifier().getLocalIdentifier();
		getTextField("Resource Identifier").setText(gid==null? "n/a": gid);
		getTextField("(global / local)").setText(lid==null? "n/a": lid.toString());
//		getTextField("Processing state").setText(desc.getProcessingState());
		getCheckBox("Master").setSelected(desc.isMaster());
		getCheckBox("Daemon").setSelected(desc.isDaemon());
		getCheckBox("Auto shutdown").setSelected(desc.isAutoShutdown());
		getCheckBox("Synchronous").setSelected(desc.isSynchronous());
		getCheckBox("Persistable").setSelected(desc.isPersistable());
		
		JTable	list	= (JTable)getComponent("Addresses");
		String[]	addresses	= desc.getName() instanceof ITransportComponentIdentifier ? ((ITransportComponentIdentifier)desc.getName()).getAddresses() : null;
		DefaultTableModel	dtm	= new DefaultTableModel();
		dtm.addColumn("Addresses", addresses!=null ? addresses : SUtil.EMPTY_STRING_ARRAY);
		list.setModel(dtm);
	}
}
