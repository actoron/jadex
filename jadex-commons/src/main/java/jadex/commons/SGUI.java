package jadex.commons;


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;


/**
 *  Static helper class with useful gui related methods.
 */
public class SGUI
{
	//-------- constants --------
	
	/** This property can be set on components to be automatically adjusted to equal sizes. */
	public static final String	AUTO_ADJUST	= "auto-adjust";
	
	//-------- methods --------

	/**
	 *  Create a menu bar, given a list of actions.
	 *  @param actions	The actions (null is mapped to separator).
	 *  @return A menu bar with a menu containing the actions.
	 */
	public static JMenuBar	createMenuBar(Action[] actions)
	{
		JMenuBar	menubar	= new JMenuBar();
		JMenu	menu	= new JMenu("Actions");
		menubar.add(menu);
		for(int i=0; i<actions.length; i++)
		{
			if(actions[i]==null)
			{
				menu.addSeparator();
			}
			else
			{
				menu.add(new JMenuItem(actions[i]));
			}
		}

		return menubar;
	}

	/**
	 *  Create a tool bar, given a list of actions.
	 *  @param name	The name of the toolbar.
	 *  @param actions	The actions (null is mapped to separator).
	 *  @return A tool bar containing the actions.
	 */
	public static JToolBar	createToolBar(String name, Action[] actions)
	{
		// Create toolbar.
		JToolBar	toolbar	= new JToolBar(name);
		toolbar.addSeparator();
		for(int i=0; i<actions.length; i++)
		{
			if(actions[i]==null)
			{
				toolbar.addSeparator();
			}
			else
			{
				JButton	button	= new JButton(actions[i]);
		        button.setToolTipText((String)actions[i].getValue(Action.NAME));
		        button.setText("");
		        button.setActionCommand((String)actions[i].getValue(Action.NAME));
		        button.setRequestFocusEnabled(false);
		        button.setMargin(new Insets(1, 1, 1, 1));
				toolbar.add(button);
			}
		}

		return toolbar;
	}

	/**
	 *  Create an action.
	 *  @param name	The name.
	 *  @param icon	The path to the icon.
	 *  @param listener	The action listener.
	 *  @return The action.
	 */
	public static Action	createAction(String name, Icon icon,
		final ActionListener listener)
	{
		return new AbstractAction(name, icon)
		{
			public void	actionPerformed(ActionEvent ae)
			{
				listener.actionPerformed(ae);
			}
		};
	}

	/**
	 *  Utility method that creates a UIDefaults.LazyValue that creates
	 *  an ImageIcon for the specified <code>gifFile</code> filename.
	 */
	// Hack!!! Required, because LookAndFeel.makeIcon returns an IconUIResource,
	// but only ImageIcons can be greyed out. grrr...
	public static Object	makeIcon(final Class baseclass, final String imgloc)
	{
		return new UIDefaults.LazyValue()
		{
			public Object	createValue(UIDefaults table)
			{
				URL	url	= baseclass.getResource(imgloc);
				if(url==null)
					throw new RuntimeException("Cannot load image: "+imgloc);
				return new ImageIcon(url);
			}
		};
	}

	/**
	 *  Render an object on a grid.
	 *  @param g	The graphics object.
	 *  @param comp	The object to render.
	 *  @param cellw	The cell width.
	 *  @param cellh	The cell height.
	 */
	public static void	renderObject(Graphics g, Component comp, double cellw,
		double cellh, int x, int y, int gridwidth)
	{
		Rectangle	bounds	= new Rectangle(
			(int)(cellw*x)+gridwidth,
			(int)(cellh*y)+gridwidth,
			(int)(cellw*(x+1)) - (int)(cellw*x) - gridwidth*2,
			(int)(cellh*(y+1)) - (int)(cellh*y) - gridwidth*2);

		// Paint component into component.
		comp.setBounds(bounds);
		g.translate(bounds.x, bounds.y);
		comp.paint(g);
		g.translate(-bounds.x, -bounds.y);
	}

	/**
	 *  Calculate the middle position of a window relativ to
	 */
	public static Point calculateMiddlePosition(Window win)
	{
		return calculateMiddlePosition(null, win);
	}

	/**
	 *  Calculate the middle position of a window relativ to
	 */
	public static Point calculateMiddlePosition(Window outer, Window win)
	{
		int rx;
		int ry;
		if(outer!=null && outer.isVisible())
		{
			Point p = outer.getLocationOnScreen();
			rx = (int)(p.getX()+outer.getWidth()/2);
			ry = (int)(p.getY()+outer.getHeight()/2);

		}
		else
		{
			Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
			rx = (int)size.getWidth() / 2;
			ry = (int)size.getHeight() / 2;
		}

		int x = rx - win.getWidth()/2;
		int y = ry - win.getHeight()/2;
		return new Point(x, y);
	}

	/**
	 *  Get the window parent if any.
	 *  @param comp The component.
	 *  @return The window if any.
	 */
	public static Window getWindowParent(Component comp)
	{
		while(comp!=null && !(comp instanceof Window))
		{
			comp	= comp.getParent();
		}
		return (Window)comp;
	}
	
	
	/**
	 *  Show an non-model message dialog.
	 */
	public static void showMessageDialog(Component parent, Object message, String title, int msgtype)
	{
		final JOptionPane pane = new JOptionPane(message, msgtype);
		final JDialog dialog = pane.createDialog(parent, title);
		dialog.setModal(false);

		pane.selectInitialValue();
		dialog.setVisible(true);
		
		// todo: find out why only called on JCC exit.
		dialog.addWindowListener(new WindowAdapter()
		{
			public void windowClosed(WindowEvent e)
			{
				dialog.removeWindowListener(this);
				dialog.dispose();
			}
		});
	}

	/**
	 *  Adjust components to equal sizes according to their
	 *  miminum, maximum, and preferred sizes.
	 */
	public static void	adjustComponentSizes(JComponent[] components)
	{
		int minimumwidth	= 0;
		int minimumheight	= 0;
		int maximumwidth	= 0;
		int maximumheight	= 0;
		int preferredwidth	= 0;
		int preferredheight	= 0;

		for(int i=0; i<components.length; i++)
		{
			Dimension	minimum	= components[i].getMinimumSize();
			Dimension	maximum	= components[i].getMaximumSize();
			Dimension	preferred	= components[i].getPreferredSize();
			minimumwidth = Math.max(minimumwidth, minimum.width);
			minimumheight = Math.max(minimumheight, minimum.height);
			maximumwidth = Math.max(maximumwidth, maximum.width);
			maximumheight = Math.max(maximumheight, maximum.height);
			preferredwidth = Math.max(preferredwidth, preferred.width);
			preferredheight = Math.max(preferredheight, preferred.height);
		}
		
		Dimension	minimum	= new Dimension(minimumwidth, minimumheight);
		Dimension	maximum	= new Dimension(maximumwidth, maximumheight);
		Dimension	preferred	= new Dimension(preferredwidth, preferredheight);
		
		for(int i=0; i<components.length; i++)
		{
			components[i].setMinimumSize(minimum);
			components[i].setMaximumSize(maximum);
			components[i].setPreferredSize(preferred);
		}
	}

	/**
	 *  Adjust all marked components to equal sizes according to their
	 *  miminum, maximum, and preferred sizes.
	 *  The mark is given by setting the {@link #AUTO_ADJUST} property
	 *  to an arbitrary value.
	 */
	public static void	adjustComponentSizes(Container parent)
	{
		java.util.List	components	= new LinkedList();
		java.util.List	adjustables	= new ArrayList();
		components.add(parent);
		while(components.size()>0)
		{
			Object	comp	= components.remove(0);
			
			// Add adjustables to adjustable list.
			if(comp instanceof JComponent)
			{
				JComponent jcomp	= (JComponent)comp;
				if(jcomp.getClientProperty(AUTO_ADJUST)!=null)
				{
					adjustables.add(jcomp);
				}
			}
			
			// Add children to traversal list.
			if(comp instanceof Container)
			{
				Container	container	= (Container)comp;
				for(int i=0; i<container.getComponentCount(); i++)
					components.add(container.getComponent(i));
			}
		}

		if(adjustables.size()>1)
		{
			JComponent[]	jcomps	= (JComponent[])adjustables.toArray(new JComponent[adjustables.size()]);
			adjustComponentSizes(jcomps);
		}
	}
	
	/**
	 *  Create a table that displays its contents using nto editable text fields. 
	 */
	public static JTable	createReadOnlyTable()
	{
		final JTextField	editor	= new JTextField();
		editor.setEditable(false);

		JTable	table	= new JTable();
		table.setBackground(editor.getBackground());
		table.setBorder(new LineBorder(table.getGridColor()));
//		table.setShowGrid(false);
//		final TableCellRenderer	defrenderer	= table.getDefaultRenderer(Object.class);
//		table.setDefaultRenderer(Object.class, new TableCellRenderer()
//		{
//			public Component getTableCellRendererComponent(JTable table, Object value,
//					boolean isSelected, boolean hasFocus, int row, int column)
//			{
//				Component	ret	= defrenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//				Dimension	dim	= ret.getPreferredSize();
//				ret.setBounds(new Rectangle(0, 0, dim.width, dim.height));
//				return ret;
//			}
//		});
		table.setDefaultEditor(Object.class, new TableCellEditor()
		{
			public boolean stopCellEditing()
			{
				return true;
			}
			
			public boolean shouldSelectCell(EventObject anEvent)
			{
				return true;
			}
			
			public void removeCellEditorListener(CellEditorListener l)
			{
			}
			
			public boolean isCellEditable(EventObject anEvent)
			{
				return true;
			}
			
			public Object getCellEditorValue()
			{
				return null;
			}
			
			public void cancelCellEditing()
			{
			}
			
			public void addCellEditorListener(CellEditorListener l)
			{
			}
			
			public Component getTableCellEditorComponent(JTable table, Object value,
					boolean isSelected, int row, int column)
			{
				editor.setText(""+value);
				Dimension	dim	= editor.getPreferredSize();
				editor.setBounds(new Rectangle(0, 0, dim.width, dim.height));
				return editor;
			}
		});
		return table;
	}
}
