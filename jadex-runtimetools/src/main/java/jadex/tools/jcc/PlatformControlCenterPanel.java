package jadex.tools.jcc;

import jadex.base.gui.JadexLogoButton;
import jadex.base.gui.plugin.IControlCenterPlugin;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
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
	
	/** The tool count. */
	protected int	toolcnt;
	
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
	 *  @param template Conta
	 */
	protected void changeToolBar(JComponent[] template, IControlCenterPlugin selplugin)
	{
 		// Setup the tool bar.
		if(toolbar==null)
		{
			toolbar	= new JToolBar("Main Toolbar");
			this.add(BorderLayout.NORTH, toolbar);
	        // Add standard entries (after gap).
	        toolbar.add(Box.createGlue());
	        toolcnt++;
	        toolbar.addSeparator();
	        toolcnt++;
	        
	        //ButtonGroup bg = new ButtonGroup();
	        IControlCenterPlugin[]	plugins	= controlcenter.getPlugins();
	        for(int i=0; i<plugins.length; i++)
	        {
	            final IControlCenterPlugin plugin = plugins[i];
	        	//final JToggleButton button = new JToggleButton(new PluginAction(plugins[i]));
	        	final JButton button = new JButton(new PluginAction(plugins[i]));
	        	Icon ic = plugin.getToolIcon(selplugin.getName().equals(plugins[i].getName()));
	    	    if(ic!=null)
	    	    	button.setIcon(ic);
	    	    else
	    	    	button.setText(plugins[i].getName());
	    	    button.setText("A");
	            button.putClientProperty("plugin", plugins[i]);
	            button.setBorder(null);
	            button.setText(null);
	            button.setMinimumSize(BUTTON_DIM);
	            button.setHorizontalAlignment(SwingConstants.CENTER);
	            button.setVerticalAlignment(SwingConstants.CENTER);
	            button.setToolTipText(plugins[i].getName());
	            button.getModel().addItemListener(new ItemListener()
	            {
	            	public void itemStateChanged(ItemEvent e)
	            	{
	            		//System.out.println(plugin.getName()+" :"+button.isSelected());
	            		button.setIcon(plugin.getToolIcon(button.isSelected()));
	            	}
	            });
//	            if(plugins[i].getHelpID()!=null)
//	            	SHelp.setupHelp(button, plugins[i].getHelpID());
	            
	            //bg.add(button);
	     	    toolbar.add(button);
	            toolcnt++;
	        }
	        toolbar.addSeparator();
	        toolcnt++;
	        toolbar.add(new JadexLogoButton(toolbar));
	        toolcnt++;
		}
		else
		{
			while(toolbar.getComponentCount()>toolcnt)
			{
//				Component	comp	= toolbar.getComponent(0);
				toolbar.remove(0);
				//if(lasttoolbar!=null)
				//	lasttoolbar.add(comp);
			}
		}

        for(int i=0; template!=null && i<template.length; i++)
            toolbar.add(template[i], i);
        //lasttoolbar	= template;
        
        // Select plugins
        for(int i=0; i<toolbar.getComponentCount(); i++)
        {
        	JComponent comp = (JComponent)toolbar.getComponent(i);
        	if(comp.getClientProperty("plugin")!=null)
        	{
        		IControlCenterPlugin pl = (IControlCenterPlugin)comp.getClientProperty("plugin");
        		((JButton)comp).setIcon(pl.getToolIcon(pl.equals(selplugin)));
        		//((JToggleButton)comp).setSelected(pluginname.equals(comp.getClientProperty("pluginname")));	
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
					JComponent[] tool = plugin.getToolBar();
					controlcenter.getControlCenter().getWindow().setJMenuBar(
						controlcenter.getControlCenter().getWindow().createMenuBar(menu));
					changeToolBar(tool, plugin);
					
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
						changeToolBar(null, plugin);
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
		
		return IFuture.DONE;
	}
	
	//-------- toolbar actions --------
	

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
