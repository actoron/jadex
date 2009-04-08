package jadex.adapter.base.envsupport.observer.gui;

import jadex.adapter.base.envsupport.observer.graphics.IViewport;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;
import jadex.bridge.ILibraryService;
import jadex.commons.SGUI;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;

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
	
	/** The viewport */
	private IViewport viewport;
	
	/** Creates the main window.
	 * 
	 *  @param title title of the window
	 *  @param simView view of the simulation
	 */
	public ObserverCenterWindow(String title, ILibraryService libService, boolean opengl)
	{
		super(title);
		
		viewport = createViewport(libService, opengl);
		
		menubar = new JMenuBar();
		menubar.add(new JMenu("Test"));
		setJMenuBar(menubar);
		
		toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
		getContentPane().add(toolbar, BorderLayout.NORTH);
		
		mainpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		mainpane.setDividerLocation(200 + mainpane.getInsets().left);
		getContentPane().add(mainpane, BorderLayout.CENTER);
		
		mainpane.setLeftComponent(new JPanel());
		
		mainpane.setRightComponent(viewport.getCanvas());

		setResizable(true);
		setBackground(null);
		pack();
		setSize(600, 400);
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);
	}
	
	/**
	 * Returns the viewport.
	 * @return the viewport
	 */
	public IViewport getViewport()
	{
		return viewport;
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
	public void setPluginView(Component view)
	{
		mainpane.setLeftComponent(view);
	}
	
	private IViewport createViewport(ILibraryService libService, boolean opengl)
	{
		final JFrame frame = new JFrame("");
		frame.setLayout(new BorderLayout());
		frame.setUndecorated(true);
		frame.pack();
		frame.setSize(1, 1);
		
		if (opengl)
		{
			// Try OpenGL...
			try
			{
				
				ViewportJOGL vp = new ViewportJOGL(libService);
				frame.add(vp.getCanvas());
				frame.setVisible(true);
				if (!((ViewportJOGL) vp).isValid())
				{
					System.err.println("OpenGL support insufficient, using Java2D fallback...");
					opengl = false;
				}
			}
			catch (RuntimeException e0)
			{
				System.err.println("OpenGL initialization failed, using Java2D fallback...");
				opengl = false;
			}
			catch (Error e1)
			{
				System.err.println("OpenGL initialization failed, using Java2D fallback...");
				opengl = false;
			}
		}
		frame.dispose();
		
		IViewport viewport = null;
		if (opengl)
		{
			viewport = new ViewportJOGL(libService);
		}
		else
		{
			viewport = new ViewportJ2D(libService);
		}
		return viewport;
	}
}
