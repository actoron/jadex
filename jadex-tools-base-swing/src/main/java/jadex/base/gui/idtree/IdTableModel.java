package jadex.base.gui.idtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *  Table model for list of objects.
 */
public class IdTableModel<T, E>	extends DefaultTableModel
{
	/** The known (id->ob). */
	protected Map<T, E> obs;

	/** The column names. */
	protected String[] columns;
	
	/** The column types. */
	protected Class<?>[] coltypes;
	
	/** The selected elems. */
	protected List<E> sels;
	
	/** The selection model. */
	protected ListSelectionModel selmodel;
	
	/** The table. */
	protected JTable table;
	
	/**
	 *  Create a new table model.
	 */
	public IdTableModel(String[] columns, Class<?>[] coltypes, JTable table)
	{
		this.columns = columns;
		this.coltypes = coltypes;
		this.table = table;
		this.obs = new LinkedHashMap<T, E>();
	}
	
	/**
	 *  Get the column count.
	 *  @return The number of columns.
	 */
	public int getColumnCount()
	{
		return columns.length;
	}
	
	/**
	 *  Get the column name.
	 *  @param i The index.
	 */
	public String getColumnName(int i)
	{
		return columns[i];
	}
	
	/**
	 *  Get the column class.
	 *  @return The column class.
	 */
	public Class<?> getColumnClass(int i)
	{
		return coltypes!=null? coltypes[i]: String.class;
	}
	
	/**
	 *  Get the row count.
	 *  @return The row count.
	 */
	public int getRowCount()
	{
		return obs==null? 0: obs.size();
	}
	
	/**
	 *  Get the value at each row.
	 */
	public Object getValueAt(int row, int column)
	{
		T[] ids	= obs.keySet().toArray((T[])new Object[obs.size()]);
		E obj = obs.get(ids[row]);
		return getValueAt(obj, column);
	}
	
	/**
	 *  Get the cell value.
	 *  Override to get specific value out of the object.
	 */
	public Object getValueAt(E obj, int column)
	{
		return obj;
	}
	
	/**
	 *  Get the obs.
	 *  @return The obs.
	 */
	public List<E> getValues()
	{
		return new ArrayList<E>(obs.values());
	}

	/**
	 *  Set the obs.
	 *  @param obs The obs to set.
	 */
	public void setObs(Map<T, E> obs)
	{
		this.obs = obs;
	}

	/**
	 *  Test if cell is editable (default is false).
	 */
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}
	
	/**
	 *  Get an object for an id.
	 *  @param id The id.
	 *  @return The object.
	 */
	public E getObject(String id)
	{
		return obs.get(id);
	}
	
	/**
	 *  Get the objects.
	 */
	public Collection<E> getObjects()
	{
		return obs==null? Collections.EMPTY_LIST: obs.values();
	}
	
	/**
	 *  Add a new object.
	 * @param id The id.
	 * @param obj The object.
	 */
	public void addObject(T id, E obj)
	{
		saveUserSelection();
		
		obs.put(id, obj);
		fireTableRowsInserted(obs.size()-1, obs.size()-1);
	
		restoreUserSelection();
		
		refresh();
	}
	
	/**
	 *  Remove an object.
	 *  @param id The id.
	 */
	public void removeObject(T id)
	{
		saveUserSelection();
		
		Iterator<T> it = obs.keySet().iterator();
		int row = -1;
		for(int i=0; it.hasNext(); i++)
		{
			T key = it.next();
			if(key.equals(id))
			{
				row = i;
			}
		}
		if(row!=-1)
		{
			obs.remove(id);
			fireTableRowsDeleted(row, row);
		}
		
		restoreUserSelection();
		
		refresh();
	}
	
	/**
	 *  Remove all objects.
	 */
	public void removeAll()
	{
		table.clearSelection();
		
		int size = obs.size();
		obs.clear();
		
		if(size>0)
			fireTableRowsDeleted(0, size-1);
		
		refresh();
	}
	
	public void setValueAt(Object val, int row, int column)
	{
	}
	
	public void addTableModelListener(TableModelListener l)
	{
	}
	
	public void removeTableModelListener(TableModelListener l)
	{
	}
			
	/**
	 *  Save the current user selection 
	 */
	protected void saveUserSelection()
	{
		if(table!=null)
		{
			int[] rows = table.getSelectedRows();
			E[] selos = (E[])getObjects().toArray(new Object[0]);
			
			sels = new ArrayList<E>();
			for(int i=0; i<rows.length; i++)
			{
				sels.add(selos[rows[i]]);
			}
		}
	}
	
	/**
	 *  Restore the current user selection.
	 */
	protected void restoreUserSelection()
	{
		if(table!=null)
		{
			table.clearSelection();
			
			if(sels!=null && !sels.isEmpty())
			{
				List<E> obs = new ArrayList<E>();
				obs.addAll(getObjects());
	
				for(E obj: sels)
				{
					int idx = obs.indexOf(obj);
					if(idx>=0)
					{
						table.addRowSelectionInterval(idx, idx);
					}
				}
			}
		}
	}
	
	boolean dorefresh;
	
	public void refresh()
	{
		if(!dorefresh)
		{
			dorefresh	= true;
			
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					dorefresh	= false;
					fireTableDataChanged();
					table.getParent().invalidate();
					table.getParent().doLayout();
					table.repaint();
				}
			});
		}
	}
}
