package jadex.tools.jcc;

import jadex.base.gui.JadexLogoButton;
import jadex.base.gui.plugin.IControlCenterPlugin;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 *  Panel for administering one chosen platform.
 */
public class PlatformControlCenterPanel extends JPanel	implements IPropertiesProvider
{
	//-------- constants --------
	
	/**	The dimension for tool bar buttons. */
	protected static final Dimension BUTTON_DIM = new Dimension(32, 32);
	
	//-------- attributes --------
	
	/** The control center. */
	protected PlatformControlCenter controlcenter;

	/** The current perspective. */
	protected IControlCenterPlugin currentperspective;

	/** The layout. */
	protected CardLayout clayout;

	/** The content. */
    protected JPanel	content;

	/** The tool bar. */
	protected JToolBar toolbar;
	
	/** A split pane for the main panel and the console. */
	protected JSplitPanel sp;
	
	/** The console. */
	protected ConsolePanel console;
	
	/** Map for console heights (plugin name -> height). */
	protected Map consoleheights; 
	
	/** Remember local console enabled state, as it is not stored for remote platforms. */
	protected boolean	consoleenabled;
	
	//-------- constructors --------
	
	/**
	 *  Create a new control center window.
	 */
	public PlatformControlCenterPanel(PlatformControlCenter controlcenter)
	{
		super(new BorderLayout());
		
		this.controlcenter = controlcenter;
		this.consoleheights = new HashMap();
	
		clayout = new CardLayout();
		content = new JPanel(clayout);
	
		this.console = new ConsolePanel(controlcenter.getPlatformAccess(), controlcenter.getJCCAccess());
		console.setConsoleEnabled(false);
		this.sp = new JSplitPanel(JSplitPane.VERTICAL_SPLIT);
		sp.setOneTouchExpandable(true);
		sp.setDividerLocation(0.0);
//		sp.setDividerLocation(200);
		content.setMinimumSize(new Dimension(0,0));
		console.setMinimumSize(new Dimension(0,0));
		sp.add(content);
		sp.add(console);
		this.add("Center", sp);
		sp.setResizeWeight(1.0);
	}

	/**
	 *  Create a toolbar containing the given tools (if any).
	 */
	protected void updateToolBar(IControlCenterPlugin selplugin)
	{
 		// Setup the tool bar.
		if(selplugin==null)
			selplugin = currentperspective;
			
		if(toolbar==null)
		{
			toolbar	= new JToolBar("Main Toolbar");
			JButton jlb = new JadexLogoButton(toolbar);
			JPanel tp = new JPanel(new BorderLayout());
			JPanel tmp = new JPanel();
			tmp.add(jlb);
			tp.add(toolbar, BorderLayout.CENTER);
			tp.add(tmp, BorderLayout.EAST);
			this.add(BorderLayout.NORTH, tp);
	       
			// Add standard entries (after gap).
	        toolbar.add(Box.createGlue());
	        toolbar.addSeparator();
	        
	        //ButtonGroup bg = new ButtonGroup();
//	        IControlCenterPlugin[]	plugins	= controlcenter.getPlugins();
//	        for(int i=0; i<plugins.length; i++)
//	        {
//	            addPlugin(plugins[i], selplugin);
//	        }
//	        toolbar.addSeparator();
//	        toolbar.add(new JadexLogoButton(toolbar));
	        
	        final JPopupMenu popup = new JPopupMenu();
	        toolbar.addMouseListener(new MouseAdapter()
            {
	        	public void mousePressed(MouseEvent e)
	        	{
	        		mouseClicked(e);
	        	}
	        	public void mouseReleased(MouseEvent e)
	        	{
	        		mouseClicked(e);
	        	}
	        	public void mouseClicked(MouseEvent e)
	            {
	             	if(e.isPopupTrigger())
	             	{
	             		popup.removeAll();
	                	IControlCenterPlugin[] pls = controlcenter.getToolbarPlugins(false);
	                	for(int i=0; i<pls.length; i++)
	                	{
	                		final IControlCenterPlugin pl = pls[i];
	                		popup.add(new JMenuItem(new AbstractAction(pl.getName(), pl.getToolIcon(false)) 
	        	            {
	        	                public void actionPerformed(ActionEvent e) 
	        	                {
	        	                	controlcenter.setPluginVisible(pl, true);
	        	                	updateToolBar(null);
	        	                }
	        	            }));
	                	}
	                    popup.show(e.getComponent(), e.getX(), e.getY());
	             	}
	            }
            });
	        toolbar.add(popup);
		}
		
		// Remove leading tool specific buttons
		if(selplugin!=null)
		{
			List<JComponent> torem = new ArrayList<JComponent>();
			for(int i=0; i<toolbar.getComponentCount(); i++)
			{
				JComponent comp	= (JComponent)toolbar.getComponent(i);
				if(comp instanceof JButton && comp.getClientProperty("plugin")==null)
	        	{
					torem.add(comp);
	        	}
			}
			for(JComponent com: torem)
			{
				toolbar.remove(com);
			}
			
			JComponent[] template = selplugin.getToolBar();
	        for(int i=0; template!=null && i<template.length; i++)
	        {
	            toolbar.add(template[i], i);
	        }
		}

        Set<IControlCenterPlugin> shown = new HashSet<IControlCenterPlugin>();
        for(int i=0; i<toolbar.getComponentCount(); i++)
        {
        	JComponent comp = (JComponent)toolbar.getComponent(i);
        	if(comp.getClientProperty("plugin")!=null)
        	{
        		IControlCenterPlugin pl = (IControlCenterPlugin)comp.getClientProperty("plugin");
        		shown.add(pl);
        	}
        }
        
        // Remove all plugin buttons
        List<JComponent> torem = new ArrayList<JComponent>();
		for(int i=0; i<toolbar.getComponentCount(); i++)
		{
			JComponent comp	= (JComponent)toolbar.getComponent(i);
			if(comp instanceof JButton && comp.getClientProperty("plugin")!=null)
        	{
				torem.add(comp);
        	}
		}
		for(JComponent com: torem)
		{
			toolbar.remove(com);
		}
        
        // Make visble plugins
        IControlCenterPlugin[] pls = controlcenter.getToolbarPlugins(true);
        List<IControlCenterPlugin> toshow = SUtil.arrayToList(pls);
        for(IControlCenterPlugin pl: toshow)
        {
        	addPlugin(pl, selplugin);
        }
        
//        boolean selectnew = false;
        
        // Select plugin and remove invisible ones
//      List<JComponent> torem = new ArrayList<JComponent>();
//        for(int i=0; i<toolbar.getComponentCount(); i++)
//        {
//        	JComponent comp = (JComponent)toolbar.getComponent(i);
//        	if(comp.getClientProperty("plugin")!=null)
//        	{
//        		IControlCenterPlugin pl = (IControlCenterPlugin)comp.getClientProperty("plugin");
//        		boolean sel = pl.equals(selplugin); 
//        		((JButton)comp).setIcon(pl.getToolIcon(sel));
//        		//((JToggleButton)comp).setSelected(pluginname.equals(comp.getClientProperty("pluginname")));	
//        		if(!controlcenter.isPluginVisible(pl))
//        		{
//        			torem.add(comp);
//        			if(selplugin.equals(pl))
//        				selectnew = true;
//        		}
//        		else
//        		{
//        			toshow.remove(pl);
//        		}
//        	}
//        }
//        for(JComponent com: torem)
//		{
//			toolbar.remove(com);
//		}

//        for(Iterator<IControlCenterPlugin> it=toshow.iterator(); it.hasNext(); )
//        {
//        	IControlCenterPlugin pl = it.next();
//        	addPlugin(pl, null);
//        }
        
//        // Check ordering of buttons, must correspond to plugins
//        for(int i=0; i<pls.length; i++)
//        {
//        	
//        	
//        	JComponent comp = (JComponent)toolbar.getComponent(i);
//        	if(comp.getClientProperty("plugin")!=null)
//        	{
//        		IControlCenterPlugin pl = (IControlCenterPlugin)comp.getClientProperty("plugin");
//        	}
//        }
        
        // Just select first (if any) if selected plugin was removed 
        if(selplugin!=null && !SUtil.arrayToSet(pls).contains(selplugin))
        {
        	for(int i=0; i<toolbar.getComponentCount(); i++)
        	{	
        		JComponent comp = (JComponent)toolbar.getComponent(i);
        		if(comp.getClientProperty("plugin")!=null)
        		{
        			IControlCenterPlugin pl = (IControlCenterPlugin)comp.getClientProperty("plugin");
        			setPerspective(pl);
        			break;
        		}
        	}
        }
        
        toolbar.validate();
        toolbar.repaint();
        
        // If toolbar has been dropped out -> pack the window (hack???).
        Container	root	= toolbar;
        while(root.getParent()!=null && !(root instanceof Window))
        	root	= root.getParent();
        if(root instanceof Window && !(root instanceof JFrame))
        {
        	((Window)root).pack();
        }
    }
	
	/**
	 * 
	 */
	protected void addPlugin(final IControlCenterPlugin pl, IControlCenterPlugin selplugin)
	{
		final JButton button = new JButton(new PluginAction(pl));
    	Icon ic = pl.getToolIcon(selplugin!=null? selplugin.getName().equals(pl.getName()): false);
	    if(ic!=null)
	    	button.setIcon(ic);
	    else
	    	button.setText(pl.getName());
	    button.setText("A");
        button.putClientProperty("plugin", pl);
        button.setBorder(null);
        button.setText(null);
        button.setMinimumSize(BUTTON_DIM);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setToolTipText(pl.getName());
        button.getModel().addItemListener(new ItemListener()
        {
        	public void itemStateChanged(ItemEvent e)
        	{
        		//System.out.println(plugin.getName()+" :"+button.isSelected());
        		button.setIcon(pl.getToolIcon(button.isSelected()));
        	}
        });
        final JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem(new AbstractAction("Hide tool") 
        {
            public void actionPerformed(ActionEvent e) 
            {
            	controlcenter.setPluginVisible(pl, false);
            	updateToolBar(null);
            }
        }));
        popup.add(new JMenuItem(new AbstractAction("Move left") 
        {
            public void actionPerformed(ActionEvent e) 
            {
            	controlcenter.moveLeftPlugin(pl);
            	updateToolBar(null);
            }
        }));
        popup.add(new JMenuItem(new AbstractAction("Move right") 
        {
            public void actionPerformed(ActionEvent e) 
            {
            	controlcenter.moveRightPlugin(pl);
            	updateToolBar(null);
            }
        }));
        button.addMouseListener(new MouseAdapter()
        {
        	public void mousePressed(MouseEvent e)
        	{
        		mouseClicked(e);
        	}
        	public void mouseReleased(MouseEvent e)
        	{
        		mouseClicked(e);
        	}
            public void mouseClicked(MouseEvent e)
            {
            	if(e.isPopupTrigger())
            	{
            		popup.show(e.getComponent(), e.getX(), e.getY());
            	}
            }
        });

//        if(plugins[i].getHelpID()!=null)
//        	SHelp.setupHelp(button, plugins[i].getHelpID());
        
 	    toolbar.add(button);
	}
	
	
	/**
	 * This method may only be called from the swing thread
	 */
	// return future only used for testing
	public IFuture<Void>	setPerspective(final IControlCenterPlugin plugin)
	{
//		System.out.println("setPerspective start: "+plugin);
		final Future	ret	= new Future();
//		ret.addResultListener(new DefaultResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
//				System.out.println("setPerspective end: "+plugin);
//			}
//		});
		controlcenter.getControlCenter().getWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		controlcenter.activatePlugin(plugin).addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				IControlCenterPlugin	oldperspective	= currentperspective;
	            currentperspective = plugin;

	            // Save console height of old perspective
	    		if(oldperspective!=plugin)
	    			consoleheights.put(oldperspective.getName()+".console.height", new Double(getConsoleHeight()));
	    		// Set console height of new perspective
	    		Double ch = (Double)consoleheights.get(currentperspective.getName()+".console.height");
	    		//System.out.println("Found: "+ch);
	            if(ch!=null)
	            	setConsoleHeight(ch.doubleValue());
	            else
	            	setConsoleHeight(1.0);
	            //System.out.println(consoleheights+" "+ch);
	            
	            try
				{
					// Get menu and toolbar before setting to avoid inconsistent state on error in plugin.
					JMenu[] menu = plugin.getMenuBar();
//					JComponent[] tool = plugin.getToolBar();
					controlcenter.getControlCenter().getWindow().setJMenuBar(
						controlcenter.getControlCenter().getWindow().createMenuBar(menu));
					updateToolBar(plugin);
					
					if(!Arrays.asList(content.getComponents()).contains(plugin.getView()))
					{
						content.add(plugin.getView(), plugin.getName());
//						if(controlcenter.props.getSubproperty(plugin.getName())!=null)
//						{
//							System.out.println("plugins: "+controlcenter.props.getSubproperty(plugin.getName()));
//							plugin.setProperties(controlcenter.props.getSubproperty(plugin.getName()))
//								.addResultListener(new DefaultResultListener()
//								{
//									public void resultAvailable(Object result)
//									{
//									}
//								});
//						}
					}
					
					clayout.show(content, plugin.getName());
					controlcenter.getControlCenter().getWindow().validate();
					controlcenter.getControlCenter().getWindow().repaint();
					ret.setResult(null);
				}
				catch(RuntimeException e)
				{
					System.err.println("Error in plugin " + plugin.getName());
					e.printStackTrace();

					// Restore perspective.
					if(oldperspective != null)
					{
						setPerspective(oldperspective);
					}
					// When no perspective:
					// Fallback to empty plugin.
					else
					{
						controlcenter.getControlCenter().getWindow().setJMenuBar(
							controlcenter.getControlCenter().getWindow().createMenuBar(null));
						updateToolBar(plugin);
						clayout.show(content, plugin.getName());
						controlcenter.getControlCenter().getWindow().validate();
						controlcenter.getControlCenter().getWindow().repaint();
					}
					ret.setResult(null);
				}
				
				controlcenter.getControlCenter().getWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			public void customExceptionOccurred(Exception exception)
			{
				controlcenter.getControlCenter().getWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				controlcenter.displayError("Plugin Error", "Plugin could not be activated: "+plugin.getName(), exception);
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Show the console.
	 *  (Code simulates a one touch expandable click programmatically,
	 *  see BasicSplitPaneDivider.OneTouchActionHandler)
	 */
	public void showConsole(boolean show)
	{
		boolean shown = isConsoleShown();

		//System.out.println(show+" "+shown);
		
		Insets  insets = sp.getInsets();
		int lastloc = sp.getLastDividerLocation();
	    int currentloc = sp.getUI().getDividerLocation(sp);
		int newloc = currentloc;
		BasicSplitPaneDivider divider = ((BasicSplitPaneUI)sp.getUI()).getDivider();

		if(show && !shown) 
		{
			if(currentloc >= (sp.getHeight() - insets.bottom - divider.getHeight())) 
			{
				int maxloc = sp.getMaximumDividerLocation();
				newloc = lastloc<maxloc? lastloc: maxloc*2/3;
            }
		}
		else if(!show && shown)
		{
		    newloc = sp.getMaximumDividerLocation();
		}

		if(currentloc != newloc) 
		{
			sp.setDividerLocation(newloc);
			sp.setLastDividerLocation(currentloc);
		}
	}
	
	/**
	 *  Test if console is shown.
	 */
	public boolean isConsoleShown()
	{
		return getConsoleHeight() != 0;
	}
	
	/**
	 *  Set the console height.
	 *  @param height The console height.
	 */
	public void setConsoleHeight(final double height)
	{
		// Delay setting when not yet displayed.
//		if(sp.getMaximumDividerLocation()<height)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					sp.setDividerLocation(height);
//					sp.setDividerLocation(sp.getMaximumDividerLocation() - height);
				}
			});
		}
//		else
//		{
//			sp.setDividerLocation(sp.getMaximumDividerLocation() - height);
//		}
	}
	
	/**
	 *  Get the console height.
	 *  @return The console height.
	 */
	public double getConsoleHeight()
	{
		return sp.getProportionalDividerLocation();//sp.getMaximumDividerLocation() - sp.getDividerLocation();
	}
	
	/**
	 *  Name is used as tab title.
	 */
	public String	getName()
	{
		return controlcenter.getPlatformAccess().getComponentIdentifier().getName();
	}
	
	//-------- IPropertiesProvider interface --------
	
	/**
	 *  Write current state into properties.
	 */
	public IFuture<Properties> getProperties()
	{
		Properties	props	= new Properties();
		if(currentperspective!=null)
			props.addProperty(new Property("perspective", currentperspective.getName()));
		props.addProperty(new Property("consoleenabled", consoleenabled ? "true" : "false"));
		consoleheights.put(currentperspective.getName()+".console.height", new Double(getConsoleHeight()));
		props.addProperty(new Property("consoleheights", JavaWriter.objectToXML(consoleheights, getClass().getClassLoader())));
		return new Future<Properties>(props);
	}
	
	/**
	 *  Update from given properties.
	 */
	public IFuture<Void> setProperties(Properties props)
	{
		Property	prop	= props.getProperty("perspective");
		if(prop!=null)
		{
			IControlCenterPlugin	plugin	= controlcenter.getPluginForName(prop.getValue());
			if(plugin!=null)
				currentperspective	= plugin;
		}
		
		prop	= props.getProperty("consoleheights");
		if(prop!=null)
		{
			try
			{
				consoleheights	= (Map)JavaReader.objectFromXML(prop.getValue(), getClass().getClassLoader());
			}
			catch(RuntimeException e)
			{
				System.err.println("Cannot load console settings: "+e.getClass().getName());
//				return new Future(e);	// Todo: Propagate exception?
			}
		}
	
		// If no perspective selected use first plugin.
		if(currentperspective==null)
		{
			currentperspective	= controlcenter.getPlugins()[0];
		}
		// Set perspective in any case to make new console heights take effect, if any.
		setPerspective(currentperspective);
		
		// Only enable console automatically when on local platform.
		if(controlcenter.getJCCAccess().getComponentIdentifier().getPlatformName()
			.equals(controlcenter.getPlatformAccess().getComponentIdentifier().getPlatformName()))
		{
			consoleenabled	= props.getBooleanProperty("consoleenabled");
			console.setConsoleEnabled(consoleenabled);
		}
		
		updateToolBar(null);
		
		return IFuture.DONE;
	}
	
	//-------- toolbar actions --------
	

	/**
	 *  Get the currentperspective.
	 *  @return The currentperspective.
	 */
	public IControlCenterPlugin getCurrentPerspective()
	{
		return currentperspective;
	}

	/**
	 *  Toolbar action for activating a plugin.
	 */
	class PluginAction extends AbstractAction
	{
		final IControlCenterPlugin plugin;

		/**
		 * Constructor for PluginAction.
		 * @param plugin
		 */
		public PluginAction(IControlCenterPlugin plugin)
		{
			super(plugin.getName());
			this.plugin = plugin;
		}

		/**
		 * @param e
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e)
		{
			setPerspective(plugin);
		}

	}
}
