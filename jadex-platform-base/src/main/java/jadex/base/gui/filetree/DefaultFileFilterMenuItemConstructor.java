package jadex.base.gui.filetree;

import jadex.base.gui.IPropertiesProvider;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.IMenuItemConstructor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.TreeModel;

/**
 *  Dynamically create a new menu item structure for starting components.
 */
public class DefaultFileFilterMenuItemConstructor implements IMenuItemConstructor, IPropertiesProvider
{
	public static final String[] STANDARD_TYPES = new String[]{".zip", ".jar", ".doc", ".xls", ".ppt"};
	
	/** Constant for select all menu item. */
	public static final String SELECT_ALL = "all";
	
	/** The file types. */
	protected String[] types;
	
	/** The root node. */
	protected TreeModel treemodel;
	
	/** The menu. */
	protected JMenu menu;
	
	/** The supported file types to menu items. */
	protected Map filetypes;
	
	/**
	 *  Create a new filter menu item constructor.
	 */
	public DefaultFileFilterMenuItemConstructor(AsyncTreeModel treemodel)
	{
		this(null, treemodel);
	}
		
	/**
	 *  Create a new filter menu item constructor.
	 */
	public DefaultFileFilterMenuItemConstructor(String[] types, final AsyncTreeModel treemodel)
	{
		this.types = types==null? STANDARD_TYPES: types;
		this.treemodel = treemodel;
		
		menu = new JMenu("File Filter");
		filetypes = new HashMap();
		JCheckBoxMenuItem all = new JCheckBoxMenuItem();
		menu.add(all);
		menu.addSeparator();
		filetypes.put(SELECT_ALL, all);
		
		all.setAction(new AbstractAction("All files")
		{
			public void actionPerformed(ActionEvent e)
			{
				for(int i=2; i<menu.getItemCount(); i++)
				{
					JMenuItem item = (JMenuItem)menu.getItem(i);
					if(item!=null)
						item.setEnabled(!isAll());
				}
				((ITreeNode)treemodel.getRoot()).refresh(true);
			}
		});
		
		// Init menu
		getMenuItem();
	}
	
	/**
	 * 
	 */
	public boolean isAll()
	{
		return ((JCheckBoxMenuItem)filetypes.get(SELECT_ALL)).isSelected();
	}
	
	/**
	 *  Get all selected 
	 */
	public List getSelectedComponentTypes()
	{
		List ret = new ArrayList();
		
		for(Iterator it=filetypes.keySet().iterator(); it.hasNext(); )
		{
			String key = (String)it.next();
			Object val = filetypes.get(key);
			if(val instanceof JCheckBoxMenuItem)
			{
				JCheckBoxMenuItem cb = (JCheckBoxMenuItem)val;
				if(cb.isSelected())
				{
					ret.add(key);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Select a set of menu items.
	 */
	public void setSelectedComponentTypes(Set selected)
	{
		for(Iterator it=filetypes.keySet().iterator(); it.hasNext(); )
		{
			String key = (String)it.next();
			
			Object val = filetypes.get(key);
			if(val instanceof JCheckBoxMenuItem)
			{
				JCheckBoxMenuItem cb = (JCheckBoxMenuItem)val;
				cb.setSelected(selected.contains(key));
			}
		}
	}
	
	/**
	 *  Get or create a new menu item (struture).
	 *  @return The menu item (structure).
	 */
	public JMenuItem getMenuItem()
	{
		if(isEnabled())
		{
			Set supported = new HashSet();
			supported.add(SELECT_ALL);
					
			// add new file types
			for(int i=0; i<types.length; i++)
			{
				supported.add(types[i]);
				if(!filetypes.containsKey(types[i]))
				{
					final JCheckBoxMenuItem ff = new JCheckBoxMenuItem(types[i], true);
					try
					{
						File file = new File("icon"+types[i]);  
						FileSystemView view = FileSystemView.getFileSystemView();      
						Icon icon = view.getSystemIcon(file);   
						ff.setIcon(icon);
						menu.add(ff);
					}
					catch(Exception e)
					{
					}
					ff.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							((ITreeNode)treemodel.getRoot()).refresh(true);
						}
					});
					filetypes.put(types[i], ff);
				}
			}
			
			// remove obsolete filetypes
			for(Iterator it=filetypes.keySet().iterator(); it.hasNext(); )
			{
				Object next = it.next();
				if(!supported.contains(next))
				{
					JMenuItem rem = (JMenuItem)filetypes.get(next);
					menu.remove(rem);
					it.remove();
				}
			}
		}
		
		return isEnabled()? menu: null;
	}
	
	/**
	 *  Write current state into properties.
	 */
	public IFuture getProperties()
	{
		final Future ret = new Future();
		Properties	filterprops	= new Properties(null, "filter", null);
		List ctypes = getSelectedComponentTypes();
		for(int i=0; i<ctypes.size(); i++)
		{
			String ctype = (String)ctypes.get(i);
			filterprops.addProperty(new Property(ctype, "true"));
		}
		ret.setResult(filterprops);
		
		return ret;
	}

	/**
	 *  Update tool from given properties.
	 */
	public IFuture setProperties(final Properties props)
	{
		if(props!=null)
		{
			Property[] mps = props.getProperties();
			Set selected = new HashSet();
			for(int i=0; i<mps.length; i++)
			{
				if(Boolean.parseBoolean(mps[i].getValue())) 
					selected.add(mps[i].getType());
			}
			setSelectedComponentTypes(selected);
		}
		return new Future();
	}

	/**
	 *  Test if action is available in current context.
	 *  @return True, if available.
	 */
	public boolean isEnabled()
	{
		return true;
	}
}