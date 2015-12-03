package jadex.base.gui.config;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.factory.SBootstrapLoader;

public class ArgumentTableModel extends AbstractTableModel
{
	//-------- constants --------
	
	/** The column names. */
	public static final String[]	COLUMN_NAMES	= new String[]
	{
		"Name", "Type", "Default Value", "Custom Value"
	};
	
	//-------- attributes --------
	
	/** The model info. */
	protected IModelInfo	mi;
	
	/** The edited arguments. */
	protected Map<String, String>	arguments;
	
	/** The currently selected configuration. */
	protected String	config;
	
	//-------- constructors --------
	
	/**
	 *  Create an argument table model.
	 */
	public ArgumentTableModel(IModelInfo mi)
	{
		this.mi	= mi;
		this.arguments	= new HashMap<String, String>();
	}
	
	//-------- TableModel interface --------
	
	/**
	 *  Get the number of columns.
	 */
	public int getColumnCount()
	{
		return COLUMN_NAMES.length;
	}

	/**
	 *  Get the name of a column.
	 */
	public String getColumnName(int column)
	{
		return COLUMN_NAMES[column];
	}

	/**
	 *  Get the number of rows.
	 */
	public int getRowCount()
	{
		return mi.getArguments().length;
	}
	
	/**
	 *  Get a table cell value.
	 */
	public Object getValueAt(int row, int column)
	{
		Object	ret;
		IArgument	arg	= mi.getArguments()[row];
		if(column==0)
		{
			ret	= arg.getName();
		}
		else if(column==1)
		{
			String	clazz	= arg.getClazz().getTypeName();
			if(clazz.indexOf(".")!=-1)
			{
				clazz	= clazz.substring(clazz.lastIndexOf(".")+1);
			}
			ret	= clazz;
		}
		else if(column==2)
		{
			ret	= SBootstrapLoader.getArgumentString(arg.getName(), mi, config);
		}
		else
		{
			ret	= arguments.get(arg.getName());
		}
				
//				if(ci.getType(getClass().getClassLoader())!=null && Boolean.class.equals(SReflect.getWrappedType(ci.getType())))
//				{
//					argprops.createCheckBox(args[i].getName()+" ("+ci.getTypeName()+")", "true".equals(argval), true, 0)
//						.setToolTipText(args[i].getDescription());								
//				}
//				else
//				{
//					argprops.createTextField(args[i].getName()+" ("+ci.getTypeName()+")", (String)argval, true, 0)
//						.setToolTipText(args[i].getDescription());
//				}
		return ret;
	}
	
	/**
	 *  Ask if cell may be edited.
	 */
	public boolean isCellEditable(int row, int column)
	{
		return column==3;
	}
	
	/**
	 *  Set a cell value.
	 */
	public void setValueAt(Object val, int row, int column)
	{
		arguments.put(mi.getArguments()[row].getName(), (String)val);
	}

	/**
	 *  Set the selected configuration.
	 */
	public void setConfiguration(String config)
	{
		this.config	= config;
		fireTableDataChanged();
	}
	
	/**
	 *  Get the edited arguments.
	 */
	public Map<String, String>	getArguments()
	{
		return arguments;
	}
}
