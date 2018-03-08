package jadex.commons.gui.jtreetable;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;

import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;


/**
 *  The node type represents the generic properties
 *  of tree table nodes.
 */
public class TreeTableNodeType
{
	//-------- attributes --------

	/** The supertype (if any). */
	protected TreeTableNodeType	supertype;

	/** The type name. */
	protected String	name;

	/** The icon. */
	// Should support different icons for open/closed/leaf???
	protected Icon[]	icons;

	/** The columns (internal names of properties). */
	protected String[]	columns;

	/** The display names of the columns. */
	protected String[]	columnnames;

	/** The editability of the columns. */
	protected boolean[]	editable;

	/** The excluded columns of this type. */
	protected boolean[]	excludes;

	/** The validators for editable columns (if any). */
//	protected IValidator[]	validators;

	/** The supported popup actions. */
	protected List	actions;

	//-------- constructors --------

	/**
	 *  Create a treetable node type inheriting from some other type.
	 *  @param name	The type name.
	 *  @param icons	The icons.
	 *  @param supertype	The supertype.
	 */
	public TreeTableNodeType(String name, Icon icons[], TreeTableNodeType supertype)
	{
		this(name, icons, null, null);
		this.supertype	= supertype;
	}

	/**
	 *  Create a treetable node type.
	 */
	public TreeTableNodeType(String name, Icon[] icons, String[] columns,
		String[] columnnames)
	{
		this.name	= name;
		this.icons	= icons;
		this.columns	= columns;
		this.columnnames	= columnnames;
		this.actions	= new ArrayList();
	}

	//-------- attribute accessors --------

	/**
	 *  Get the supertype.
	 *  @return The supertype.
	 */
	public TreeTableNodeType	getSupertype()
	{
		return this.supertype;
	}

	/**
	 *  Get the type name.
	 *  @return The name of the node type.
	 */
	public String	getName()
	{
		return this.name;
	}
	
	/**
	 *  Add an icon.
	 *  @param icon The icon for the node.
	 */
	public void	addIcon(Icon icon)
	{
		Icon[] tmp = new Icon[icons!=null? icons.length+1: 1];
		for(int i=0; icons!=null && i<icons.length; i++)
			tmp[i] = icons[i];
		tmp[tmp.length-1] = icon;
		this.icons = tmp;
	}

	/**
	 *  Get the icon.
	 *  @return The icon for the node.
	 */
	public Icon	getIcon(Object value)
	{
		return selectIcon(value)==null && supertype!=null
			? supertype.selectIcon(value)
			: selectIcon(value);
	}
	
	/**
	 *  Select an icon.
	 *  @param value The selection criterium.
	 *  @return The icon to be used.
	 */
	public Icon selectIcon(Object value)
	{
		return icons!=null? icons[0]: null;
	}

	/**
	 *  Get the columns (internal property names).
	 *  @return The columns.
	 */
	public String[]	getColumns()
	{
		return columns==null && supertype!=null
			? supertype.getColumns()
			: columns;
	}

	/**
	 *  Get the column names (display names).
	 *  @return The column namess.
	 */
	public String[]	getColumnNames()
	{
		return columnnames==null && supertype!=null
			? supertype.getColumnNames()
			: columnnames;
	}

	/**
	 *  Get the editability of the column.
	 *  @param column	The column to check for editability.
	 *  @return The editablility.
	 */
	public boolean	isColumnEditable(int column)
	{
		return editable==null && supertype!=null
			? supertype.isColumnEditable(column)
			: editable!=null && editable.length>column && editable[column];
	}

	/**
	 *  Check if nodes of this type should have a value for the given column.
	 *  @param column	The column to check for.
	 *  @return True, if there is no value for the given column.
	 */
	public boolean	isColumnExcluded(int column)
	{
		return excludes==null && supertype!=null
			? supertype.isColumnExcluded(column)
			: excludes!=null && excludes.length>column && excludes[column];
	}

	/**
	 *  Exclude a column.
	 *  @param column	The column to exclude.
	 */
	public void	addExclude(String column)
	{
		String[] columns	= getColumns();
		if(excludes==null)
		{
			this.excludes	= new boolean[columns.length];
		}
		for(int i=0; i<columns.length; i++)
		{
			if(columns[i].equals(column))
			{
				excludes[i]	= true;
				break;
			}
		}
	}

	/**
	 *  Make a column editable.
	 *  @param column	The column to be editable.
	 */
	public void	setEditable(String column)
	{
		String[] columns	= getColumns();
		if(editable==null)
		{
			this.editable	= new boolean[columns.length];
		}
		for(int i=0; i<columns.length; i++)
		{
			if(columns[i].equals(column))
			{
				editable[i]	= true;
				break;
			}
		}
	}

	/**
	 *  Add a popup action.
	 *  @param action	The action.
	 *  @param name	The (display) name of the action.
	 */
	public void	addPopupAction(Action action)
	{
		actions.add(action);
	}

	/**
	 *  Get all popup actions (also from supertypes).
	 *  @return The popup actions.
	 */
	public Action[]	getPopupActions()
	{
		Action[] all = (Action[])actions.toArray(new Action[actions.size()]);
		if(supertype!=null)
		{
			all	= (Action[])SUtil.joinArrays(supertype.getPopupActions(), all);
		}
		
		List ret = SCollection.createArrayList();
		for(int i=0; i<all.length; i++)
		{
			if(all[i].isEnabled())
			{
				//System.out.println("Enabled: "+all[i]);
				ret.add(all[i]);
			}
		}
		
		return (Action[])ret.toArray(new Action[ret.size()]);
	}
	
//	/**
//	 *  Get the validator for a column (if any).
//	 */
//	public IValidator	getValidator(int column)
//	{
//		return (validators!=null && validators.length>column && validators[column]!=null)
//			? validators[column]
//			: supertype!=null ? supertype.getValidator(column) : null;
//	}
//	
//	/**
//	 *  Set the validator for a given column.
//	 */
//	public void	setValidator(String column, IValidator validator)
//	{
//		String[] columns	= getColumns();
//		if(validators==null)
//		{
//			this.validators	= new IValidator[columns.length];
//		}
//		for(int i=0; i<columns.length; i++)
//		{
//			if(columns[i].equals(column))
//			{
//				validators[i]	= validator;
//				break;
//			}
//		}		
//	}
}
