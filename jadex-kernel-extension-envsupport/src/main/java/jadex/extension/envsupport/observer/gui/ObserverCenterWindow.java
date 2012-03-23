package jadex.extension.envsupport.observer.gui;

import jadex.commons.gui.SGUI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.EventQueue;
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
	
	/** The menubar
	 */
	private JMenuBar menubar;
	
	/** The toolbar
	 */
	private JToolBar toolbar;
	
	/** The main pane.
	 */
	private JSplitPane mainpane;
	
	/** Known plugin views
	 */
	protected Set knownpluginviews;
	
	/** Creates the main window.
	 * 
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
				menubar = new JMenuBar();
//				menubar.add(new JMenu("Test"));
				setJMenuBar(menubar);

				toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
				getContentPane().add(toolbar, BorderLayout.NORTH);

				mainpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
				mainpane.setOneTouchExpandable(true);
				getContentPane().add(mainpane, BorderLayout.CENTER);

				JPanel pluginpanel = new JPanel(new CardLayout());
				mainpane.setLeftComponent(pluginpanel);
				
				JPanel perspectivepanel = new JPanel(new CardLayout());
				mainpane.setRightComponent(perspectivepanel);

				setResizable(true);
				setBackground(null);
				pack();
				setSize(800, 600);
				setLocation(SGUI.calculateMiddlePosition(ObserverCenterWindow.this));
				setVisible(true);
				mainpane.setDividerLocation(250);
			}
		};
		
		if (EventQueue.isDispatchThread())
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
		JPanel pluginpanel = (JPanel) mainpane.getLeftComponent();
		CardLayout cl = (CardLayout)(pluginpanel.getLayout());
		
		if (!knownpluginviews.contains(pluginname))
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
		int loc = mainpane.getDividerLocation();
		mainpane.setRightComponent(view);
		mainpane.setDividerLocation(loc);
	}
}
