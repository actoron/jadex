package jadex.base.gui.componenttree;

import java.awt.BorderLayout;
import java.lang.reflect.Method;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.SReflect;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.jtable.ResizeableTableHeader;

/**
 *  Panel for showing required service properties.
 */
public class RequiredServiceProperties	extends	PropertiesPanel
{
	//-------- constructors --------
	
	/**
	 *  Create new service properties panel.
	 */
	public RequiredServiceProperties()
	{
		super(" Service Properties ");

		createTextField("Name");
		createTextField("Type");
		createTextField("Multiple");
		createTextField("Binding");
		
		addFullLineComponent("Methods_label", new JLabel("Methods"));
		JTable	table	= SGUI.createReadOnlyTable();
		table.setTableHeader(new ResizeableTableHeader(table.getColumnModel()));
		JPanel	scroll	= new JPanel(new BorderLayout());
		scroll.add(table, BorderLayout.CENTER);
		scroll.add(table.getTableHeader(), BorderLayout.NORTH);
		addFullLineComponent("Methods", scroll);
	}
	
	//-------- methods --------
	
	/**
	 *  Set the service.
	 */
	public void	setService(RequiredServiceInfo info)
	{
//		IServiceIdentifier	sid	= service.getId();
		
		getTextField("Name").setText(info.getName());
		getTextField("Type").setText(info.getType().getTypeName());
		getTextField("Multiple").setText(""+info.isMultiple());
		RequiredServiceBinding bind = info.getDefaultBinding();
		StringBuffer buf = new StringBuffer();
		buf.append("scope="+bind.getScope());
//		buf.append(" dynamic="+bind.isDynamic());
//		buf.append(" create="+bind.isCreate());
//		buf.append(" recover="+bind.isRecover());
		if(bind.getComponentName()!=null)
			buf.append(" component name="+bind.getComponentName());
		if(bind.getComponentType()!=null)
			buf.append(" component type="+bind.getComponentType());
		getTextField("Binding").setText(buf.toString());
		
		try
		{
			// Todo: support methods also for remote components.
			JTable	list	= (JTable)getComponent("Methods").getComponent(0);
			Method[] methods	= info.getType().getType0().getMethods();	// NullPointerException for remote
			String[] returntypes	= new String[methods.length]; 
			String[] names	= new String[methods.length]; 
			String[] parameters	= new String[methods.length];
			for(int i=0; i<methods.length; i++)
			{
				returntypes[i] = SReflect.getUnqualifiedTypeName(methods[i].getGenericReturnType().toString());
	//			returntypes[i]	= SReflect.getUnqualifiedClassName(methods[i].getReturnType());
				names[i]	= methods[i].getName();
				Class<?>[]	params	= methods[i].getParameterTypes();
				String	pstring	= "";
				for(int j=0; j<params.length; j++)
				{
					if(j==0)
					{
	//					pstring	= SReflect.getUnqualifiedClassName(params[j]);
						pstring	= SReflect.getUnqualifiedTypeName(params[j].toString());
					}
					else
					{
	//					pstring	+= ", "+SReflect.getUnqualifiedClassName(params[j]);
						pstring	+= ", "+SReflect.getUnqualifiedTypeName(params[j].toString());
					}
				}
				parameters[i]	= pstring;
			}
			DefaultTableModel	dtm	= new DefaultTableModel();
			dtm.addColumn("Return Type", returntypes);
			dtm.addColumn("Method Name", names);
			dtm.addColumn("Parameters", parameters);
			list.setModel(dtm);
		}
		catch(NullPointerException e)
		{
			// Class not available
			JTable	list	= (JTable)getComponent("Methods").getComponent(0);
			DefaultTableModel	dtm	= new DefaultTableModel();
			dtm.addColumn("Not Available", new String[]{"Class file for service type not available locally.", "Methods cannot be displayed."});
			list.setModel(dtm);
		}
	}
}
