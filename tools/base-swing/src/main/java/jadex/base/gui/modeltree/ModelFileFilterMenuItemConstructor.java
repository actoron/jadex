package jadex.base.gui.modeltree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.tree.TreeModel;

import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.IMenuItemConstructor;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;
import jadex.commons.gui.future.SwingResultListener;

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
	protected Map<String, JCheckBoxMenuItem> filetypes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new filter menu item constructor.
	 */
	public ModelFileFilterMenuItemConstructor(final AsyncSwingTreeModel treemodel, IExternalAccess exta)
	{
		this.treemodel = treemodel;
		this.exta = exta;
		
		menu = new JMenu("File Filter");
		filetypes = new HashMap<String, JCheckBoxMenuItem>();
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
				((ISwingTreeNode)treemodel.getRoot()).refresh(true);
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
			for(Iterator<String> it=filetypes.keySet().iterator(); it.hasNext(); )
			{
				String key = it.next();
//				if(!SELECT_ALL.equals(key))
				{
					JCheckBoxMenuItem cb = filetypes.get(key);
					if(cb.isSelected())
					{
						ret.add(key);
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Select a set of menu items.
	 */
	public void setSelectedComponentTypes(Set<String> selected)
	{
		for(Iterator<String> it=filetypes.keySet().iterator(); it.hasNext(); )
		{
			String key = it.next();
			
			JCheckBoxMenuItem cb = filetypes.get(key);
			cb.setSelected(selected.contains(key));
		}
	}
	
	/**
	 *  Returns the supported component types.
	 *  @return The supported component types.
	 */
	public IFuture<Set<String>> getSupportedComponentTypes()
	{
		final Future<Set<String>> ret = new Future<Set<String>>();
		SServiceProvider.searchServices(exta, new ServiceQuery<>(IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new SwingExceptionDelegationResultListener<Collection<IComponentFactory>, Set<String>>(ret)
		{
			public void customResultAvailable(Collection<IComponentFactory> facts)
			{
				Set<String> supported = new HashSet<String>();
				supported.add(SELECT_ALL);
				if(facts!=null)
				{
					for(Iterator<IComponentFactory> it=facts.iterator(); it.hasNext(); )
					{
						IComponentFactory fac = it.next();
						
						String[] fts = fac.getComponentTypes();
						
						// add new file types
						for(int i=0; i<fts.length; i++)
						{
							supported.add(fts[i]);
							if(!filetypes.containsKey(fts[i]))
							{
								final JCheckBoxMenuItem ff = new JCheckBoxMenuItem(fts[i], true);
								fac.getComponentTypeIcon(fts[i]).addResultListener(new SwingResultListener<byte[]>(new IResultListener<byte[]>()
								{
									public void resultAvailable(byte[] img)
									{
										ff.setIcon(new ImageIcon(img));
									}
									
									public void exceptionOccurred(Exception exception)
									{
										// ignore...
									}
								}));
								
								menu.add(ff);
								ff.addActionListener(new ActionListener()
								{
									public void actionPerformed(ActionEvent e)
									{
										((ISwingTreeNode)treemodel.getRoot()).refresh(true);
									}
								});
								filetypes.put(fts[i], ff);
							}
						}
					}
				}
				
				// remove obsolete filetypes
				for(Iterator<String> it=filetypes.keySet().iterator(); it.hasNext(); )
				{
					String next = it.next();
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
			SServiceProvider.searchServices(exta, new ServiceQuery<>(IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM))
//				.addResultListener(new SwingResultListener<Collection<IComponentFactory>>(new IResultListener<Collection<IComponentFactory>>()
				.addResultListener(new SwingResultListener<Collection<IComponentFactory>>(new IResultListener<Collection<IComponentFactory>>()
			{
				public void resultAvailable(Collection<IComponentFactory> facts)
				{
					Set<String> supported = new HashSet<String>();
					supported.add(SELECT_ALL);
					if(facts!=null)
					{
						for(Iterator<IComponentFactory> it=facts.iterator(); it.hasNext(); )
						{
							Object o = it.next();
							if(!(o instanceof IComponentFactory))
							{
								System.out.println("debug: "+o);
								SUtil.arrayToString("interfaces:"+o.getClass().getInterfaces());
							}
							
							IComponentFactory fac = (IComponentFactory)o;//it.next();
							
							String[] fts = fac.getComponentTypes();
							
							// add new file types
							for(int i=0; i<fts.length; i++)
							{
								supported.add(fts[i]);
								if(!filetypes.containsKey(fts[i]))
								{
									final JCheckBoxMenuItem ff = new JCheckBoxMenuItem(fts[i], true);
									fac.getComponentTypeIcon(fts[i]).addResultListener(new SwingResultListener<byte[]>(new IResultListener<byte[]>()
									{
										public void resultAvailable(byte[] img)
										{
											if(ff==null)
												System.out.println("hhh");
											ff.setIcon(new ImageIcon(img));
										}
										
										public void exceptionOccurred(Exception exception)
										{
											// ignore...
										}
									}));
									
									menu.add(ff);
									ff.addActionListener(new ActionListener()
									{
										public void actionPerformed(ActionEvent e)
										{
											((ISwingTreeNode)treemodel.getRoot()).refresh(true);
										}
									});
									filetypes.put(fts[i], ff);
								}
							}
						}
					}
					
					// remove obsolete filetypes
					for(Iterator<String> it=filetypes.keySet().iterator(); it.hasNext(); )
					{
						String next = it.next();
						if(!supported.contains(next))
						{
							JMenuItem rem = (JMenuItem)filetypes.get(next);
							menu.remove(rem);
							it.remove();
						}
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// ignore...
				}
			}));
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
		List<String> ctypes = getSelectedComponentTypes();
		for(int i=0; i<ctypes.size(); i++)
		{
			String ctype = ctypes.get(i);
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
			Set<String> selected = new HashSet<String>();
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