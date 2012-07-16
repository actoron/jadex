package jadex.extension.envsupport.observer.gui;

import jadex.commons.gui.SGUI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

/** Default GUI main window.
 */
public class ObserverCenterWindow extends JFrame
{
	
	/** The menubar. */
	private JMenuBar menubar;
	
	/** The toolbar. */
	private JToolBar toolbar;
	
	/** The main pane. */
	protected JPanel mainpanel;
	
	/** The split pane. */
	private JSplitPane splitpane;
	
	/** Known plugin views. */
	protected Set knownpluginviews;
	
	protected boolean disposed;
	
	/** 
	 *  Creates the main window.
	 *  @param title title of the window
	 *  @param simView view of the simulation
	 */
	public ObserverCenterWindow(String title)
	{
		super(title);
		
		this.knownpluginviews = new HashSet();
		
		Runnable runnable = new Runnable()
		{
			public void run()
			{
				if(!disposed)
				{
					menubar = new JMenuBar();
	//				menubar.add(new JMenu("Test"));
					setJMenuBar(menubar);
	
					mainpanel = new JPanel(new BorderLayout());
					
					toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
					mainpanel.add(toolbar, BorderLayout.NORTH);
	
					splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
					splitpane.setOneTouchExpandable(true);
	
					JPanel pluginpanel = new JPanel(new CardLayout());
					splitpane.setLeftComponent(pluginpanel);
					
					JPanel perspectivepanel = new JPanel(new CardLayout());
					splitpane.setRightComponent(perspectivepanel);
					
					mainpanel.add(splitpane, BorderLayout.CENTER);
					getContentPane().add(mainpanel, BorderLayout.CENTER);
					
					setResizable(true);
					setBackground(null);
					pack();
					setSize(800, 600);
					setLocation(SGUI.calculateMiddlePosition(ObserverCenterWindow.this));
					setVisible(true);
					splitpane.setDividerLocation(250);
					
//					addMouseListener(new MouseAdapter()
//					{
//						public void mouseClicked(MouseEvent e)
//						{
//							System.out.println("mouse: "+e);
//    						
//    						menubar.setVisible(false);
//    						splitpane.setVisible(false);
//    						toolbar.setVisible(false);
//    						mainpanel.remove(splitpane);
////    						mainpanel.remove(toolbar);
//    						Component right = splitpane.getRightComponent();
//    						splitpane.remove(right);
//    						
//    						mainpanel.add(right, BorderLayout.CENTER);
//    						
////    						dispose();
////    						setUndecorated(true);
////    						setVisible(true);
//    						
//    						setLocation(0,0);
//    						setSize(Toolkit.getDefaultToolkit().getScreenSize());
//						}
//					});
					
//					KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
//			        manager.addKeyEventDispatcher(new KeyEventDispatcher()
//	        		{
//	        			public boolean dispatchKeyEvent(KeyEvent e)
//	        			{
//	        				if(e.getID() == KeyEvent.KEY_PRESSED)
//	        				{
//	        				}
//	        				return false;
//	        			}
//	        		});
				}
			}
		};
		
		if(EventQueue.isDispatchThread())
		{
			runnable.run();
		}
		else
		{
//			try
			{
//				EventQueue.invokeAndWait(runnable);
				EventQueue.invokeLater(runnable);
			}
//			catch (InterruptedException e)
//			{
//			}
//			catch (InvocationTargetException e)
//			{
//			}
		}
	}
	
	public void dispose()
	{
		disposed	= true;
		super.dispose();
	}
	
	/** Adds a menu
	 *  
	 *  @param menu the menu
	 */
	public void addMenu(JMenu menu)
	{
		menubar.add(menu);
	}
	
	/** Removes a menu
	 *  
	 *  @param menu the menu
	 */
	public void removeMenu(JMenu menu)
	{
		menubar.remove(menu);
	}
	
	/** Adds a toolbar item
	 *  
	 *  @param name name of the toolbar item
	 *  @param action toolbar item action
	 */
	public void addToolbarItem(String name, Action action)
	{
		JButton button = new JButton(action);
		button.setText(name);
		toolbar.add(button);
	}
	
	/** Adds a toolbar item with icon.
	 *  
	 *  @param name name of the toolbar item
	 *  @param icon the icon of the item
	 *  @param action toolbar item action
	 */
	public void addToolbarItem(String name, Icon icon, Action action)
	{
		JButton button = new JButton(action);
		button.setIcon(icon);
		button.setToolTipText(name);
		toolbar.add(button);
	}
	
	/** Sets the plugin view.
	 *  
	 *  @param view the view
	 */
	public void setPluginView(String pluginname, Component view)
	{
		JPanel pluginpanel = (JPanel) splitpane.getLeftComponent();
		CardLayout cl = (CardLayout)(pluginpanel.getLayout());
		
		if(!knownpluginviews.contains(pluginname))
		{
			knownpluginviews.add(pluginname);
			pluginpanel.add(view, pluginname);
		}
		
		cl.show(pluginpanel, pluginname);
	}
	
	/** Sets the perspective view.
	 *  
	 *  @param view the view
	 */
	public void setPerspectiveView(Component view)
	{
		int loc = splitpane.getDividerLocation();
		splitpane.setRightComponent(view);
		splitpane.setDividerLocation(loc);
	}
	
}
