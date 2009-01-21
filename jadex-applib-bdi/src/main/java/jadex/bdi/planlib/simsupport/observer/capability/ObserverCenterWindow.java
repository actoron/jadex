package jadex.bdi.planlib.simsupport.observer.capability;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.MenuBar;
import java.lang.reflect.InvocationTargetException;

import jadex.bdi.planlib.simsupport.common.graphics.IViewport;
import jadex.commons.SGUI;
import jadex.commons.SUtil;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

/** Default GUI main window.
 */
public class ObserverCenterWindow extends JFrame
{
	/** The menubar
	 */
	private JMenuBar menuBar_;
	
	/** The toolbar
	 */
	private JToolBar toolBar_;
	
	/** The main pane.
	 */
	private JSplitPane mainPane_;
	
	/** Creates the main window.
	 * 
	 *  @param title title of the window
	 *  @param simView view of the simulation
	 */
	public ObserverCenterWindow(String title, final Canvas simView)
	{
		super(title);
		
		menuBar_ = new JMenuBar();
		menuBar_.add(new JMenu("Test"));
		setJMenuBar(menuBar_);
		
		toolBar_ = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
		getContentPane().add(toolBar_, BorderLayout.NORTH);
		
		mainPane_ = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		mainPane_.setDividerLocation(200 + mainPane_.getInsets().left);
		getContentPane().add(mainPane_, BorderLayout.CENTER);
		
		mainPane_.setLeftComponent(new JPanel());
		
//		simView.setMinimumSize(new Dimension(1, 1));	// Not in Java 1.4
		mainPane_.setRightComponent(simView);

		setResizable(true);
		setBackground(null);
		pack();
		setSize(600, 400);
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);
	}
	
	/** Adds a menu
	 *  
	 *  @param menu the menu
	 */
	public void addMenu(JMenu menu)
	{
		menuBar_.add(menu);
	}
	
	/** Removes a menu
	 *  
	 *  @param menu the menu
	 */
	public void removeMenu(JMenu menu)
	{
		menuBar_.remove(menu);
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
		toolBar_.add(button);
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
		toolBar_.add(button);
	}
	
	/** Sets the plugin view.
	 *  
	 *  @param view the view
	 */
	public void setPluginView(Component view)
	{
		mainPane_.setLeftComponent(view);
	}
}
