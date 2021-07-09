package jadex.commons.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import jadex.bridge.service.types.simulation.SSimulation;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.Future;


/**
 *  Static helper class with useful gui related methods.
 */
public class SGUI
{
	//-------- constants --------
	
	/** This property can be set on components to be automatically adjusted to equal sizes. */
	public static final String	AUTO_ADJUST	= "auto-adjust";
	
	/**
	 *  IDs of potentially broken UIs that override behavior like setBackground().
	 */
	protected static final Set<String> BROKEN_UI_IDS = new HashSet<String>();
	static
	{
		BROKEN_UI_IDS.add("GTK");
	}
	
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
	 *  Create a dialog with a specific content panel.
	 */
	public static boolean createDialog(String title, JComponent content, JComponent comp)
	{
		return createDialog(title, content, comp, false);
	}
	
	/**
	 *  Create a dialog with a specific content panel.
	 */
	public static boolean createDialog(String title, JComponent content, JComponent comp, boolean info)
	{
		final JDialog dia = new JDialog((JFrame)null, title, true);
		
		JButton bok = new JButton("OK");
		JPanel ps = new JPanel(new GridBagLayout());
		ps.add(bok, new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.SOUTHEAST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2), 0, 0));
		final boolean[] ok = new boolean[1];
		bok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ok[0] = true;
				dia.dispose();
			}
		});
		
		if(!info)
		{
			JButton bcancel = new JButton("Cancel");
			bok.setMinimumSize(bcancel.getMinimumSize());
			bok.setPreferredSize(bcancel.getPreferredSize());
			ps.add(bcancel, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
			bcancel.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					dia.dispose();
				}
			});
		}
		
		dia.getContentPane().add(content, BorderLayout.CENTER);
		dia.getContentPane().add(ps, BorderLayout.SOUTH);
		
		dia.pack();
		dia.setLocation(SGUI.calculateMiddlePosition(comp!=null? SGUI.getWindowParent(comp): null, dia));
		dia.setVisible(true);
		
		return ok[0];
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
	 *  Display an error dialog.
	 * 
	 *  @param errortitle The title to use for an error dialog (required).
	 *  @param errormessage An optional error message displayed before the exception.
	 *  @param exception The exception (if any).
	 */
	public static void showError(Component parent, String errortitle, String errormessage, final Exception exception)
	{
//		if(exception!=null)
//		{
//			Thread.dumpStack();
//			exception.printStackTrace();
//		}
		
		final String	text;
		String	exmsg	= exception==null ? null : exception.getMessage();
		if(errormessage==null && exmsg==null)
		{
			text	= errortitle;
		}
		else if(errormessage!=null && exmsg==null)
		{
			text	= errormessage;
		}
		else if(errormessage==null && exmsg!=null)
		{
			text	= exmsg;
		}
		else// if(errormessage!=null && exmsg!=null)
		{
			text = errormessage + "\n" + exmsg;
		}
		Object	message	= SUtil.wrapText(text);
		if(exception!=null)
		{
			final JPanel	panel	= new JPanel(new BorderLayout());
			final JButton	details	= new JButton("Show Details")
			{
				public Insets getInsets()
				{
					return new Insets(1, 1, 1, 1);
				}
			};
			details.addActionListener(new ActionListener()
			{
				JComponent	area	= null;
				boolean shown	= false;
				public void actionPerformed(ActionEvent e)
				{
					if(shown)
					{
						panel.remove(area);
						details.setText("Show Details");
					}
					else
					{
						if(area==null)
						{
							StringWriter	sw	= new StringWriter();
							exception.printStackTrace(new PrintWriter(sw));
							area	= new JScrollPane(new JTextArea(sw.toString(), 10, 40));
						}
						panel.add(area, BorderLayout.CENTER);
						details.setText("Hide Details");
					}
					SGUI.getWindowParent(panel).pack();
					shown	= !shown;
				}
			});
			JPanel	but	= new JPanel(new FlowLayout(FlowLayout.RIGHT));
			but.add(details);
			JTextArea	msg	= new JTextArea(message.toString());
			msg.setEditable(false);
			
			// Java Bug #7100524 Workaround:
			// This is needed to avoid throwing an obscure exception later 
			msg.setDropTarget(null);
			
			msg.setCursor(null);  
			msg.setOpaque(false);  
//					msg.setFocusable(false);
			panel.add(msg, BorderLayout.NORTH);
			panel.add(but, BorderLayout.SOUTH);
			message	= panel;
			
			// Make dialogs resizable
			panel.addHierarchyListener(new HierarchyListener()
			{
				public void hierarchyChanged(HierarchyEvent e)
				{
					Window window = SwingUtilities.getWindowAncestor(panel);
					if(window instanceof Dialog)
	                {
	                    Dialog dialog = (Dialog)window;
	                    if(!dialog.isResizable()) 
	                    {
	                        dialog.setResizable(true);
	                    }
	                }
					
				}
			});
		}
		
		JOptionPane.showMessageDialog(parent, message, errortitle, JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 *  Adjust components to equal sizes according to their
	 *  miminum, maximum, and preferred sizes.
	 *  
	 *  All components must be JComponents, this performs automatic array conversion.
	 */
	public static void	adjustComponentSizes(Component[] components)
	{
		JComponent[] jcomponents = new JComponent[components.length];
		System.arraycopy(components, 0, jcomponents, 0, components.length);
		adjustComponentSizes(jcomponents);
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
	 *  Adjust components to equal sizes according to their
	 *  miminum, maximum, and preferred sizes horizontally.
	 */
	public static void	adjustComponentHorizontalSizes(JComponent[] components)
	{
		int minimumwidth	= 0;
		int maximumwidth	= 0;
		int preferredwidth	= 0;

		for(int i=0; i<components.length; i++)
		{
			Dimension	minimum	= components[i].getMinimumSize();
			Dimension	maximum	= components[i].getMaximumSize();
			Dimension	preferred	= components[i].getPreferredSize();
			minimumwidth = Math.max(minimumwidth, minimum.width);
			maximumwidth = Math.max(maximumwidth, maximum.width);
			preferredwidth = Math.max(preferredwidth, preferred.width);
		}
		
		for(int i=0; i<components.length; i++)
		{
			Dimension minimum	= components[i].getMinimumSize();
			Dimension maximum	= components[i].getMaximumSize();
			Dimension preferred	= components[i].getPreferredSize();
			minimum		= new Dimension(minimumwidth, minimum.height);
			maximum		= new Dimension(maximumwidth, maximum.height);
			preferred	= new Dimension(preferredwidth, preferred.height);
			components[i].setMinimumSize(minimum);
			components[i].setMaximumSize(maximum);
			components[i].setPreferredSize(preferred);
		}
	}
	
	/**
	 *  Adjust components to equal sizes according to their
	 *  miminum, maximum, and preferred sizes vertically.
	 */
	public static void	adjustComponentVerticalSizes(JComponent[] components)
	{
		int minimumheight	= 0;
		int maximumheight	= 0;
		int preferredheight	= 0;

		for(int i=0; i<components.length; i++)
		{
			Dimension	minimum	= components[i].getMinimumSize();
			Dimension	maximum	= components[i].getMaximumSize();
			Dimension	preferred	= components[i].getPreferredSize();
			minimumheight = Math.max(minimumheight, minimum.height);
			maximumheight = Math.max(maximumheight, maximum.height);
			preferredheight = Math.max(preferredheight, preferred.height);
		}
		
		for(int i=0; i<components.length; i++)
		{
			Dimension minimum	= components[i].getMinimumSize();
			Dimension maximum	= components[i].getMaximumSize();
			Dimension preferred	= components[i].getPreferredSize();
			minimum		= new Dimension(minimum.width, minimumheight);
			maximum		= new Dimension(maximum.width, maximumheight);
			preferred	= new Dimension(preferred.width, preferredheight);
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
	 *  Sets the minimum size along with the preferred size.
	 *  Negative values will remain the same.
	 *  
	 *  @param c Component.
	 *  @param w Width.
	 *  @param h Height.
	 */
	public static void setMinimumSize(JComponent c, int w, int h)
	{
		Dimension ms = c.getMinimumSize();
		Dimension ps = c.getPreferredSize();
		
		c.setMinimumSize(new Dimension(w < 0 ? ms.width : w, h < 0 ? ms.height : h));
		c.setPreferredSize(new Dimension(w < 0 ? ps.width : w, h < 0 ? ps.height : h));
	}
	
	/**
	 *  Creates a simple vertical arrangement using group layout with gaps.
	 *  Automatically adds the components.
	 *  
	 *  @param container The container.
	 *  @param components The components.
	 */
	public static void createVerticalGroupLayout(Container container, JComponent[] components, boolean fixedsize)
	{
		GroupLayout l = new GroupLayout(container);
		l.setAutoCreateContainerGaps(true);
		l.setAutoCreateGaps(true);
		container.setLayout(l);
		
		SequentialGroup rhgroup = l.createSequentialGroup();
		ParallelGroup hgroup = l.createParallelGroup();
		rhgroup.addGroup(hgroup);
		
		SequentialGroup vgroup = l.createSequentialGroup();
		
		for (int i = 0; i < components.length; ++i)
		{
			container.add(components[i]);
			
			if (fixedsize)
			{
				hgroup.addComponent(components[i], GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
				vgroup.addComponent(components[i], GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
			}
			else
			{
				hgroup.addComponent(components[i]);
				vgroup.addComponent(components[i]);
			}
		}
		
		if (fixedsize)
			SGUI.adjustComponentHorizontalSizes(components);
		
		l.setHorizontalGroup(rhgroup);
		l.setVerticalGroup(vgroup);
	}
	
	/**
	 *  Creates a simple vertical arrangement using group layout with gaps.
	 *  Automatically adds the components.
	 *  
	 *  @param container The container.
	 *  @param components The components.
	 */
	public static void createHorizontalGroupLayout(Container container, JComponent[] components, boolean fixedsize)
	{
		GroupLayout l = new GroupLayout(container);
		l.setAutoCreateContainerGaps(true);
		l.setAutoCreateGaps(true);
		container.setLayout(l);
		
		SequentialGroup hgroup = l.createSequentialGroup();
		
		SequentialGroup rvgroup = l.createSequentialGroup();
		ParallelGroup vgroup = l.createParallelGroup();
		rvgroup.addGroup(vgroup);
		
		for (int i = 0; i < components.length; ++i)
		{
			container.add(components[i]);
			
			if (fixedsize)
			{
				hgroup.addComponent(components[i], GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
				vgroup.addComponent(components[i], GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
			}
			else
			{
				hgroup.addComponent(components[i]);
				vgroup.addComponent(components[i]);
			}
		}
		
		if (fixedsize)
			SGUI.adjustComponentHorizontalSizes(components);
		
		l.setHorizontalGroup(hgroup);
		l.setVerticalGroup(rvgroup);
	}
	
	/**
	 *  Creates a simple edgeless, vertical arrangement using group layout.
	 *  Automatically adds the components.
	 *  
	 *  @param container The container.
	 *  @param components The components.
	 */
	public static void createEdgelessHorizontalGroupLayout(Container container, JComponent[] components, boolean fixedsize)
	{
		createEdgelessHorizontalGroupLayout(container, components, fixedsize, false);
	}
	
	/**
	 *  Creates a simple edgeless, vertical arrangement using group layout.
	 *  Automatically adds the components.
	 *  
	 *  @param container The container.
	 *  @param components The components.
	 */
	public static void createEdgelessHorizontalGroupLayout(Container container, JComponent[] components, boolean fixedsize, boolean rightalign)
	{
		GroupLayout l = new GroupLayout(container);
		l.setAutoCreateContainerGaps(false);
		l.setAutoCreateGaps(true);
		container.setLayout(l);
		
		SequentialGroup hgroup = l.createSequentialGroup();
		if (rightalign)
			hgroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE);
		
		SequentialGroup rvgroup = l.createSequentialGroup();
		ParallelGroup vgroup = l.createParallelGroup();
		rvgroup.addGroup(vgroup);
		
		for (int i = 0; i < components.length; ++i)
		{
			container.add(components[i]);
			
			if (fixedsize)
			{
				hgroup.addComponent(components[i], GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
				vgroup.addComponent(components[i], GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
			}
			else
			{
				hgroup.addComponent(components[i]);
				vgroup.addComponent(components[i]);
			}
		}
		
		if (!rightalign)
			hgroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE);
		
		if (fixedsize)
			SGUI.adjustComponentHorizontalSizes(components);
		
		l.setHorizontalGroup(hgroup);
		l.setVerticalGroup(rvgroup);
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
	
	/**
	 *  Get the proportional split location.
	 */
	public static double getProportionalDividerLocation(JSplitPane pane)
	{
		double full = pane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT? pane.getSize().getWidth(): pane.getSize().getHeight();
		double ret = ((double)pane.getDividerLocation())/full;
		return ret;
	}
	
	/**
	 *  Color conversion method for "web-style" color definitions.
	 *  This method is needed because CSS.stringToColor() is
	 *  protected and StyleSheet.stringToColor() may be unavailable
	 *  without a GUI.
	 */
	public static Color stringToColor(String str)
	{
		// Note: Unlike CSS.stringToColor, this method is missing "rgb(1.0, 1.0, 1.0)"-style definitions.
		if (str.length() == 0 || str.equalsIgnoreCase("Black"))
			str = "#000000";
		else if(str.equalsIgnoreCase("Silver"))
			str = "#C0C0C0";
		else if(str.equalsIgnoreCase("Gray"))
			str = "#808080";
		else if(str.equalsIgnoreCase("White"))
			str = "#FFFFFF";
		else if(str.equalsIgnoreCase("Maroon"))
			str = "#800000";
		else if(str.equalsIgnoreCase("Red"))
			str = "#FF0000";
		else if(str.equalsIgnoreCase("Purple"))
			str = "#800080";
		else if(str.equalsIgnoreCase("Fuchsia"))
			str = "#FF00FF";
		else if(str.equalsIgnoreCase("Green"))
			str = "#008000";
		else if(str.equalsIgnoreCase("Lime"))
			str = "#00FF00";
		else if(str.equalsIgnoreCase("Olive"))
			str = "#808000";
		else if(str.equalsIgnoreCase("Yellow"))
			str = "#FFFF00";
		else if(str.equalsIgnoreCase("Navy"))
			str = "#000080";
		else if(str.equalsIgnoreCase("Blue"))
			str = "#0000FF";
		else if(str.equalsIgnoreCase("Teal"))
			str = "#008080";
		else if(str.equalsIgnoreCase("Aqua"))
			str = "#00FFFF";
		
		Color ret = null;
		if(str.startsWith("#"))
		{
			String b = str.substring(5, 7);
			String g = str.substring(3, 5);
			String r = str.substring(1, 3);
			if (str.length()==9)
			{
				String alpha = str.substring(7);
				ret = new Color(Integer.parseInt(r, 16),
						Integer.parseInt(g, 16),
						Integer.parseInt(b, 16),
						Integer.parseInt(alpha, 16));
			}
			else
			{
				ret = new Color(Integer.parseInt(r, 16),
						  Integer.parseInt(g, 16),
						  Integer.parseInt(b, 16));
			}
		}
		
		return ret;
	}
	
	/**
	 * Convert a color to an html representation.
	 * 
	 * @param color The color.
	 * @return The html string representing the color.
	 */
	public static String colorToHTML(Color color)
	{
		return "#" + Integer.toHexString(color.getRed())
				+ Integer.toHexString(color.getGreen())
				+ Integer.toHexString(color.getBlue());
	}

	/**
	 * Convert a font to an html representation.
	 * 
	 * @param font The font.
	 * @return The html string representing the font.
	 */
	public static String fontToHTML(Font font)
	{
		String style;
		if(font.isBold())
		{
			style = font.isItalic() ? "bolditalic " : "bold ";
		}
		else
		{
			style = font.isItalic() ? "italic " : "";
		}
		return style + font.getSize() + " " + font.getName();
	}
	
	/**
	 *  Scale an image.
	 *  @param scr The src image.
	 *  @param w The width.
	 *  @param h The height.
	 *  @param type The type (Image.SCALE_XYZ).
	 */
	public static final BufferedImage scaleImage(Image src, int w, int h, int type)
	{
		Image img = src.getScaledInstance(w, h, type);
		BufferedImage ret = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR_PRE);
		Graphics g = ret.createGraphics();
		g.drawImage(img, 0, 0, new Color(0, 0, 0, 0), null);
		g.dispose();
		return ret;
	}
	
	/**
	 *  Converts a buffered image to a new image type.
	 *  E.g. BufferedImage.TYPE_4BYTE_ABGR_PRE.
	 *  
	 *  @param original Original image.
	 *  @param type New type.
	 *  @return Converted image.
	 */
	public static final BufferedImage convertBufferedImageType(BufferedImage original, int type)
	{
		BufferedImage ret = new BufferedImage(original.getWidth(), original.getHeight(), type);
		Graphics g = ret.createGraphics();
		g.drawImage(original, 0, 0, null);
		g.dispose();
		return ret;
	}
	
	/**
	 *  Gets the index of the selected button in a button group.
	 *  
	 *  @param bg The button group.
	 *  @return The selected index.
	 */
	public static int getSelectedButton(ButtonGroup bg)
	{
		int ret = -1;
		Enumeration<AbstractButton> enumer = bg.getElements();
		int i = 0;
		while (enumer.hasMoreElements())
		{
			if (enumer.nextElement().isSelected())
			{
				ret = i;
				break;
			}
			++i;
		}
		return ret;
	}
	
	/**
	 *  Sets the enabled state of all buttons in a button group.
	 *  
	 *  @param bg The button group.
	 *  @param enabled True, if enabled
	 */
	public static void setAllEnabled(ButtonGroup bg, boolean enabled)
	{
		Enumeration<AbstractButton> enumer = bg.getElements();
		while (enumer.hasMoreElements())
		{
			enumer.nextElement().setEnabled(enabled);
		}
	}
	
	/**
	 *  Scales a buffered image.
	 *  
	 *  @param original Original image.
	 *  @param w New width.
	 *  @param h New height.
	 *  @return Scaled image.
	 */
	public static final BufferedImage scaleBufferedImage(BufferedImage original, int w, int h)
	{
		Image scaled = original.getScaledInstance(w, h, Image.SCALE_AREA_AVERAGING);
		BufferedImage ret = new BufferedImage(w, h, original.getType());
		Graphics2D g = ret.createGraphics();
		g.drawImage(scaled, 0, 0, null);
		g.dispose();
		return ret;
	}
	
	/**
	 * Use SwingUtilities to put the given runnable in the AWT Event queue.
	 * 
	 * @param runnable
	 */
	public static void invokeLater(Runnable runnable) 
	{
		SwingUtilities.invokeLater(runnable);
	}
	
	/**
	 *  Invoke on swing thread and sync with simulation if any.
	 */
	public static void invokeLaterSimBlock(Runnable runnable) 
	{
		if(!SReflect.HAS_GUI || SwingUtilities.isEventDispatchThread())
		{
			runnable.run();
		}
		else
		{
			Future<Void>	adblock	= SSimulation.block();
			SwingUtilities.invokeLater(() ->
			{
				try
				{
					runnable.run();
				}
				finally
				{
					if(adblock!=null)
						adblock.setResult(null);
				}
			});
		}
	}

	
	/**
	 *  Shortcut method.
	 *  Text extraction from the Java "Document" class used by JTextArea is braindead,
	 *  it throws checked exception, lots of parameters: Bloat, bloat, bloat.
	 *  
	 *  @param doc The text area.
	 *  @return The extracted String.
	 */
	public static final String getText(JTextArea area)
	{
		Document doc = area.getDocument();
		String ret = null;
        
		try
        {
            ret = doc.getText(0, doc.getLength());
            if (ret.length() == 0)
    		{
    			ret = null;
    		}
        }
        catch (BadLocationException e)
        {
        }
        
        return ret;
	}
	
	/**
	 * 	Shortcut method.
	 *  Setting text the Java "Document" class used by JTextArea is braindead,
	 *  it throws non-RuntimeException, multiple invocations, many parameters:
	 *  Bloat, bloat, bloat.
	 *  
	 *  @param doc The document.
	 *  @return The extracted String.
	 */
	public static final void setText(JTextArea area, String text)
	{
		Document doc = area.getDocument();
		try
		{
			doc.remove(0, doc.getLength());
			doc.insertString(0, text, null);
		}
		catch (BadLocationException e)
		{
		}
	}
	
	/**
	 *  Adds a copy & paste menu to a text component.
	 *  
	 *  @param textcomponent The text component.
	 */
	public static void addCopyPasteMenu(final JTextComponent textcomponent)
	{
		textcomponent.addMouseListener(new TextCopyPasteMouseListener(textcomponent));
	}
	
	/**
	 *  Removes copy & paste menu from a text component.
	 *  
	 *  @param textcomponent The text component.
	 */
	public static void removeCopyPasteMenu(final JTextComponent textcomponent)
	{
		for (MouseListener lis : textcomponent.getMouseListeners())
		{
			if (lis instanceof TextCopyPasteMouseListener)
			{
				textcomponent.removeMouseListener(lis);
				break;
			}
		}
	}
	
	/**
	 *  Converts a color to RGBA array.
	 */
	public static double[] colorToRgba(Color color)
	{
		double[] ret = new double[4];
		ret[0] = color.getRed() / 255.0;
		ret[1] = color.getGreen() / 255.0;
		ret[2] = color.getBlue() / 255.0;
		ret[3] = color.getAlpha() / 255.0;
		return ret;
	}
	
	/**
	 *  Converts a RGBA array to color.
	 */
	public static Color rgbaToColor(double[] rgba)
	{
		assert rgba != null && rgba.length == 4;
		return new Color((float) rgba[0], (float) rgba[1], (float) rgba[2], (float) rgba[3]);
	}
	
//	/** Lookup table for divider locations (split->Integer).*/
//	protected static Map	locations;
//
//	/**
//	 *  Set a split location.
//	 *  Delays the call until the component is valid.
//	 */
//	public static void setDividerLocation(final JSplitPane split, int loc)
//	{
//		assert SwingUtilities.isEventDispatchThread();
//		
//		// Set direct.
//		if(split.isValid())
//		{
//			System.out.println("setDividerLocation: "+loc+", @"+split.hashCode());
//			split.setDividerLocation(loc);
//			if(locations!=null)
//			{
//				locations.remove(split);
//				if(locations.isEmpty())
//				{
//					locations	= null;
//				}
//			}
//		}
//		
//		// Wait until valid.
//		else
//		{
//			// Already queued
//			if(locations!=null && locations.containsKey(split))
//			{
////				System.out.println("setDividerLocation updated: "+loc+", @"+split.hashCode());
//				locations.put(split, Integer.valueOf(loc));
//			}
//			
//			// First time call.
//			else
//			{
//				split.validate();
////				System.out.println("setDividerLocation queued: "+loc+", @"+split.hashCode());
//				if(locations==null)
//					locations	= new HashMap();
//				locations.put(split, Integer.valueOf(loc));
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						if(locations!=null && locations.containsKey(split))
//						{
//							int	loc	= ((Integer)locations.remove(split)).intValue();
//							if(locations.isEmpty())
//							{
//								locations	= null;
//							}
//							setDividerLocation(split, loc);
//						}
//					}
//				});
//			}
//		}
//	}
	
	/**
	 *  Listener for copy-paste menu.
	 *
	 */
	public static class TextCopyPasteMouseListener extends MouseAdapter
	{
		/** The text component. */
		protected JTextComponent textcomponent;
		
		/**
		 *  Creates the listener.
		 */
		public TextCopyPasteMouseListener(JTextComponent textcomponent)
		{
			this.textcomponent = textcomponent;
		}
		
		/**
		 *  Called when mouse pressed.
		 */
		public void mousePressed(MouseEvent e)
		{
			if (e.isPopupTrigger())
			{
				JCopyPasteContextMenu popup = new JCopyPasteContextMenu(textcomponent);
				popup.show(textcomponent, e.getX(), e.getY());
			}
		}
	}
}
