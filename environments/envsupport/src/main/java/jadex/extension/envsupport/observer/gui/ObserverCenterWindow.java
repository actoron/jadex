package jadex.extension.envsupport.observer.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;
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
import javax.swing.SwingUtilities;

import jadex.commons.gui.SGUI;


/**
 * Default GUI main window.
 */
public class ObserverCenterWindow extends JFrame
{

	/** The menubar. */
	private JMenuBar	menubar;

	/** The toolbar. */
	private JToolBar	toolbar;

	/** The main pane. */
	protected JPanel	mainpanel;

	/** The split pane. */
	private JSplitPane	splitpane;

	/** Known plugin views. */
	protected Set		knownpluginviews;

	protected boolean	disposed;

	protected boolean	fullscreen	= false;

	protected Dimension	olddim;

	protected Point		oldpos;

	protected Component	oldcomp;

	protected int		olddivider;
	
	protected KeyListener fullscreenlistener;
	
	/**
	 * Creates the main window.
	 * 
	 * @param title title of the window
	 * @param simView view of the simulation
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
					// menubar.add(new JMenu("Test"));
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
					
					fullscreenlistener = new KeyAdapter()
					{
					
						public void keyPressed(KeyEvent e)
						{
							if(e.getKeyCode() == KeyEvent.VK_F11)
							{
								makeFullscreen();
							}
						
						}
					};
					
//					KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
//					manager.addKeyEventDispatcher(new KeyEventDispatcher()
//					{
//						public boolean dispatchKeyEvent(KeyEvent e)
//						{
//							if(e.getKeyCode() == KeyEvent.VK_F11 && e.getID() == KeyEvent.KEY_PRESSED)
//							{
//								Object source = e.getSource();
//								System.out.println("window " + source.toString());
//				
//									makeFullscreen();
//
//							}
//
//							return false;
//						}
//					});

				}
			}
		};


		if(EventQueue.isDispatchThread())
		{
			runnable.run();
		}
		else
		{
			// try
//			{
				// EventQueue.invokeAndWait(runnable);
				EventQueue.invokeLater(runnable);
//			}
			// catch (InterruptedException e)
			// {
			// }
			// catch (InvocationTargetException e)
			// {
			// }
		}
	}

	/**
	 * Display window in fullscreen.
	 */
	public void makeFullscreen()
	{
		boolean fs = oldcomp == null;

		menubar.setVisible(!fs);
		splitpane.setVisible(!fs);
		toolbar.setVisible(!fs);
		// mainpanel.remove(splitpane);
		// mainpanel.remove(toolbar);

		final WindowListener[] wls = getWindowListeners();
		for(WindowListener wl : wls)
		{
			removeWindowListener(wl);
		}

		if(fs)
		{
			oldpos = getLocation();
			olddim = getSize();
			oldcomp = splitpane.getRightComponent();
			olddivider = splitpane.getDividerLocation();

			splitpane.remove(oldcomp);
			mainpanel.add(oldcomp, BorderLayout.CENTER);

			dispose();
			disposed = false;
			setUndecorated(true);
			setVisible(true);

			setLocation(0, 0);
			setSize(Toolkit.getDefaultToolkit().getScreenSize());
		}
		else
		{
			splitpane.setRightComponent(oldcomp);
			mainpanel.remove(oldcomp);
			mainpanel.add(splitpane, BorderLayout.CENTER);

			dispose();
			disposed = false;
			setUndecorated(false);
			setVisible(true);

			setLocation(oldpos);
			setSize(olddim);
			splitpane.setDividerLocation(olddivider);
			oldcomp = null;
		}

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				for(WindowListener wl : wls)
				{
					addWindowListener(wl);
				}
			}
		});
	}


	/**
	 * Dispose the frame
	 */
	public void dispose()
	{
//		System.out.println("observer dispose");
		disposed = true;
		super.dispose();
	}


	/**
	 * Adds a menu
	 * 
	 * @param menu the menu
	 */
	public void addMenu(JMenu menu)
	{
		menubar.add(menu);
	}

	/**
	 * Removes a menu
	 * 
	 * @param menu the menu
	 */
	public void removeMenu(JMenu menu)
	{
		menubar.remove(menu);
	}

	/**
	 * Adds a toolbar item
	 * 
	 * @param name name of the toolbar item
	 * @param action toolbar item action
	 */
	public void addToolbarItem(String name, Action action)
	{
		JButton button = new JButton(action);
		button.setText(name);
		toolbar.add(button);
	}

	/**
	 * Adds a toolbar item with icon.
	 * 
	 * @param name name of the toolbar item
	 * @param icon the icon of the item
	 * @param action toolbar item action
	 */
	public void addToolbarItem(String name, Icon icon, Action action)
	{
		JButton button = new JButton(action);
		button.setIcon(icon);
		button.setToolTipText(name);
		toolbar.add(button);
	}

	/**
	 * Sets the plugin view.
	 * 
	 * @param view the view
	 */
	public void setPluginView(String pluginname, Component view)
	{
		if(view==null)
			return;
		
		JPanel pluginpanel = (JPanel)splitpane.getLeftComponent();
		CardLayout cl = (CardLayout)(pluginpanel.getLayout());

		if(!knownpluginviews.contains(pluginname))
		{
			knownpluginviews.add(pluginname);
			pluginpanel.add(view, pluginname);
		}

		cl.show(pluginpanel, pluginname);
	}

	/**
	 * Sets the perspective view.
	 * 
	 * @param view the view
	 */
	public void setPerspectiveView(Component view)
	{
		int loc = splitpane.getDividerLocation();
		
		Component oldview = splitpane.getRightComponent();
		if(oldview != null)
		{
			oldview.removeKeyListener(fullscreenlistener);
		}
		
		splitpane.setRightComponent(view);
		
		view.addKeyListener(fullscreenlistener);
		
		splitpane.setDividerLocation(loc);
	}

	/**
	 * @return the fullscreen
	 */
	public boolean isFullscreen()
	{
		return fullscreen;
	}

	/**
	 * @param fullscreen the fullscreen to set
	 */
	public void setFullscreen(boolean fullscreen)
	{
		this.fullscreen = fullscreen;
	}

}
