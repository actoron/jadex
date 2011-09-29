package jadex.base.gui.componenttree;

import jadex.bridge.service.ProvidedServiceInfo;
import jadex.commons.SReflect;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.jtable.ResizeableTableHeader;

import java.awt.BorderLayout;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *  Panel for showing service properties.
 */
public class ProvidedServiceInfoProperties	extends	PropertiesPanel
{
	//-------- constructors --------
	
	/**
	 *  Create new service properties panel.
	 */
	public ProvidedServiceInfoProperties()
	{
		super(" Service Properties ");

		createTextField("Name");
		createTextField("Type");
		
		// todo:
//		createTextField("Implementation");  
		
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
	public void	setService(ProvidedServiceInfo service)
	{
		getTextField("Name").setText(service.getName());
		getTextField("Type").setText(service.getTypeName());
//		getTextField("Implementation").setText();
		
		try
		{
			JTable	list	= (JTable)getComponent("Methods").getComponent(0);
			Method[] methods	= service.getType(null).getMethods();
			String[] returntypes	= new String[methods.length]; 
			String[] names	= new String[methods.length]; 
			String[] parameters	= new String[methods.length];
			for(int i=0; i<methods.length; i++)
			{
	//			returntypes[i] = SReflect.getUnqualifiedClassName(methods[i].getReturnType());
				returntypes[i] = SReflect.getUnqualifiedTypeName(methods[i].getGenericReturnType().toString());
				names[i] = methods[i].getName();
	//			Class[]	params	= methods[i].getParameterTypes();
				Type[]	params	= methods[i].getGenericParameterTypes();
				String pstring	= "";
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
