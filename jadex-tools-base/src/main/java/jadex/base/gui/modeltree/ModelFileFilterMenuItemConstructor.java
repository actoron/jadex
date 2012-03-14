package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.IMenuItemConstructor;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.swing.tree.TreeModel;

/**
 *  Dynamically create a new menu item structure for starting components.
 */
public class ModelFileFilterMenuItemConstructor implements IMenuItemConstructor, IPropertiesProvider
{
	//-------- attributes --------
	
	/** Constant for select all menu item. */
	public static final String SELECT_ALL = "all";
	
	/** The root node. */
	protected TreeModel treemodel;
	
	/** The external access. */
	protected IExternalAccess exta;
	
	/** The menu. */
	protected JMenu menu;
	
	/** The supported file types to menu items. */
	protected Map filetypes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new filter menu item constructor.
	 */
	public ModelFileFilterMenuItemConstructor(final AsyncTreeModel treemodel, IExternalAccess exta)
	{
		this.treemodel = treemodel;
		this.exta = exta;
		
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
	
	//-------- methods --------
	
	/**
	 *  Test if all is selected.
	 *  @return True, if all.
	 */
	public boolean isAll()
	{
		return ((JCheckBoxMenuItem)filetypes.get(SELECT_ALL)).isSelected();
	}
	
	/**
	 *  Get all selected component types.
	 *  @return A list of component types.
	 */
	public List<String> getSelectedComponentTypes()
	{
		List<String> ret = new ArrayList<String>();
		
//		if(!isAll())
		{
			for(Iterator it=filetypes.keySet().iterator(); it.hasNext(); )
			{
				String key = (String)it.next();
//				if(!SELECT_ALL.equals(key))
				{
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
	 *  Returns the supported component types.
	 *  @return The supported component types.
	 */
	public IFuture getSupportedComponentTypes()
	{
		final Future ret = new Future();
		SServiceProvider.getServices(exta.getServiceProvider(), 
			IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				Collection facts = (Collection)result;
				
				Set supported = new HashSet();
				supported.add(SELECT_ALL);
				if(facts!=null)
				{
					for(Iterator it=facts.iterator(); it.hasNext(); )
					{
						IComponentFactory fac = (IComponentFactory)it.next();
						
						String[] fts = fac.getComponentTypes();
						
						// add new file types
						for(int i=0; i<fts.length; i++)
						{
							supported.add(fts[i]);
							if(!filetypes.containsKey(fts[i]))
							{
								final JCheckBoxMenuItem ff = new JCheckBoxMenuItem(fts[i], true);
								fac.getComponentTypeIcon(fts[i]).addResultListener(new DefaultResultListener()
								{
									public void resultAvailable(Object result)
									{
										ff.setIcon((Icon)result);
									}
								});
								
								menu.add(ff);
								ff.addActionListener(new ActionListener()
								{
									public void actionPerformed(ActionEvent e)
									{
										((ITreeNode)treemodel.getRoot()).refresh(true);
									}
								});
								filetypes.put(fts[i], ff);
							}
						}
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
				ret.setResult(filetypes.keySet());
			}
		});
		return ret;
	}
	
	/**
	 *  Get or create a new menu item (struture).
	 *  @return The menu item (structure).
	 */
	public JMenuItem getMenuItem()
	{
		if(isEnabled())
		{
			SServiceProvider.getServices(exta.getServiceProvider(), 
				IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new SwingDefaultResultListener()
			{
				public void customResultAvailable(Object result)
				{
					Collection facts = (Collection)result;
					
					Set supported = new HashSet();
					supported.add(SELECT_ALL);
					if(facts!=null)
					{
						for(Iterator it=facts.iterator(); it.hasNext(); )
						{
							IComponentFactory fac = (IComponentFactory)it.next();
							
							String[] fts = fac.getComponentTypes();
							
							// add new file types
							for(int i=0; i<fts.length; i++)
							{
								supported.add(fts[i]);
								if(!filetypes.containsKey(fts[i]))
								{
									final JCheckBoxMenuItem ff = new JCheckBoxMenuItem(fts[i], true);
									fac.getComponentTypeIcon(fts[i]).addResultListener(new DefaultResultListener()
									{
										public void resultAvailable(Object result)
										{
											ff.setIcon((Icon)result);
										}
									});
									
									menu.add(ff);
									ff.addActionListener(new ActionListener()
									{
										public void actionPerformed(ActionEvent e)
										{
											((ITreeNode)treemodel.getRoot()).refresh(true);
										}
									});
									filetypes.put(fts[i], ff);
								}
							}
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
			});
		}
		
		return isEnabled()? menu: null;
	}

	/**
	 *  Test if action is available in current context.
	 *  @return True, if available.
	 */
	public boolean isEnabled()
	{
		return true;
	}
	
	/**
	 *  Write current state into properties.
	 */
	public IFuture<Properties> getProperties()
	{
		final Future<Properties> ret = new Future<Properties>();
		Properties	filterprops	= new Properties();
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
	public IFuture<Void> setProperties(final Properties props)
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
		return IFuture.DONE;
	}
	
	
}