package jadex.tools.comanalyzer.table;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import jadex.base.gui.jtable.ComponentIdentifierRenderer;
import jadex.base.gui.jtable.ComponentIdentifiersRenderer;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;
import jadex.commons.gui.jtable.DateTimeRenderer;
import jadex.commons.gui.jtable.ResizeableTableHeader;
import jadex.commons.gui.jtable.TableSorter;
import jadex.commons.gui.jtable.VisibilityTableColumnModel;
import jadex.tools.comanalyzer.Component;
import jadex.tools.comanalyzer.Message;
import jadex.tools.comanalyzer.MessageFilterMenu;
import jadex.tools.comanalyzer.ToolCanvas;
import jadex.tools.comanalyzer.ToolTab;


/**
 * The container for the table.
 */
public class TableCanvas extends ToolCanvas
{

	// -------- attributes --------

	/** The autoscroll for the table */
	protected boolean autoScroll = true;

	/** The table model for sorting the actual model */
	protected TableSorter sorter;

	/** The table model that maintains the messages */
	protected MessageTableModel model;

	/** The table. */
	protected JTable table;

	// -------- constructors --------

	/**
	 * Constructor for the container of the message table.
	 * @param tooltab The tooltab.
	 */
	public TableCanvas(ToolTab tooltab)
	{
		super(tooltab);

		model = new MessageTableModel();
		model.addTableModelListener(new TableChangedListener());

		sorter = new TableSorter(model);
		table = new JTable(sorter);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setBackground(UIManager.getColor("List.background"));
		table.setPreferredScrollableViewportSize(new Dimension(800, 70));
		table.setDefaultRenderer(IComponentIdentifier.class, new ComponentIdentifierRenderer(tooltab.getPlugin().getJCC().getJCCAccess().getComponentIdentifier().getRoot()));
		// setDefaultRenderer(Object.class, new ContentRenderer());
		table.setDefaultRenderer(IComponentIdentifier[].class, new ComponentIdentifiersRenderer());
		table.setDefaultRenderer(Date.class, new DateTimeRenderer());
		table.addMouseListener(new TableMouseListener(table));

		// Initialize visibility of columns and add mouselistener
		VisibilityTableColumnModel columnmodel = new VisibilityTableColumnModel();
		table.setColumnModel(columnmodel);
		table.createDefaultColumnsFromModel();
		// Make first column unhideable
		columnmodel.setColumnChangeable(columnmodel.getColumn(0), false);

		// Make headers resizable.
		ResizeableTableHeader header = new ResizeableTableHeader(columnmodel);
		header.setTable(table);
		header.setAutoResizingEnabled(false); // default
		header.setIncludeHeaderWidth(false); // default
		// Set the preffered, minimum and maximum column widths
		header.setAllColumnWidths(145, -1, -1);
		header.setColumnWidths(columnmodel.getColumn(0), 30, -1, -1);

		// apply header to table and sorter
		table.setTableHeader(header);
		sorter.setTableHeader(header);

		// must be set after sorter.setTableHeader() !!!
		columnmodel.addMouseListener(table);

		JScrollPane main = new JScrollPane(table);
		main.getViewport().setBackground(UIManager.getColor("List.background"));

		this.setLayout(new BorderLayout());
		this.add(BorderLayout.CENTER, main);
	}

	// -------- ToolCanvas methods --------

	/**
	 * Update a message by adding it, if the message can be displayed or
	 * removing it if present.
	 * 
	 * @param message The message to add.
	 * @param isPresent <code>true</code> if removal is skipped. (Can be
	 * applied to new messages)
	 */
	public void updateMessage(Message message, boolean isPresent)
	{
		if(message.getEndpoints() != null)
		{
			if(!model.containsMessage(message))
			{
				addMessage(message);
			}
		}
		else if(isPresent)
		{
			removeMessage(message);
		}

	}

	/**
	 * Removes a message.
	 * 
	 * @param message The message to remove.
	 */
	public void removeMessage(Message message)
	{
		model.removeMessage(message);
	}

	/**
	 * Updates an agent by adding it, if the agent can be displayed or removing
	 * it if present.
	 * 
	 * @param agent The agent to add.
	 * @param isPresent <code>true</code> if removal is skipped. (Can be
	 * applied to new agents)
	 */
	public void updateComponent(Component agent, boolean update)
	{
		// the table dont have a representation for agents
	}

	/**
	 * Removes an agent.
	 * 
	 * @param agent The agent to remove.
	 */
	public void removeComponent(Component agent)
	{
		// the table dont have a representation for agents
	}

	/**
	 * Repaint the canvas.
	 */
	public void repaintCanvas()
	{
		// the table dont need a special repaint
	}

	/**
	 * Clear the table by removing all messages.
	 */
	public void clear()
	{
		model.removeAllMessages();
	}

	// -------- TableCanvas methods --------

	/**
	 * Adds a message.
	 * @param message The message to add.
	 */
	public void addMessage(Message message)
	{
//		System.out.println("adding: "+message);
		model.addMessage(message);
	}

	/**
	 * @return Returns <code>true</code> if autoscroll is on.
	 */
	public boolean isAutoScroll()
	{
		return autoScroll;
	}

	/**
	 * @param autoScroll Set the autoscroll.
	 */
	public void setAutoScroll(boolean autoScroll)
	{
		this.autoScroll = autoScroll;
	}

	/**
	 * Get the selected messages.
	 * 
	 * @return The array of selected meessages
	 */
	public Message[] getSelectedMessages()
	{
		Message[] ret = null;

		int count = table.getSelectedRowCount();
		if(count > 0)
		{
			// TableSorter sorter = (TableSorter) getModel();
			// MessageTableModel model = (MessageTableModel)
			// sorter.getTableModel();

			ArrayList msgs = new ArrayList();
			int[] rows = table.getSelectedRows();
			for(int i = 0; i < rows.length; i++)
			{
				msgs.add(model.getMessage(sorter.modelIndex(rows[i])));
			}
			ret = (Message[])msgs.toArray(new Message[msgs.size()]);
		}

		return ret;
	}

	// -------- inner classes --------

	/**
	 * Listener to receive the tableChanged event. Here one can adjust the
	 * viewport location to make autoscroll possible.
	 */
	private final class TableChangedListener implements TableModelListener
	{
		public void tableChanged(TableModelEvent e)
		{
			if(autoScroll && e.getType() == TableModelEvent.INSERT)
			{
				// must use invokeLater hier to get the very last row !!!
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
						table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, -1, true));
//					}
//				});
			}
		}
	}

	/**
	 * A mouselistener for the table providing the messagefiltermenu on
	 * rightclick and the elementdetailview on doubleclick
	 */
	private class TableMouseListener extends MouseAdapter
	{

		/** The table */
		protected JTable table;

		/**
		 * Create tjhe listener
		 * 
		 * @param table The table to listen to.
		 */
		public TableMouseListener(JTable table)
		{
			this.table = table;
		}

		/**
		 * Use mouseRelesased for popup trigger.
		 */
		public void mouseReleased(MouseEvent e)
		{
			if(e.getButton() == MouseEvent.BUTTON3)
			{
				// get the coordinates of the mouse click
				Point p = e.getPoint();
				// get the row index that contains that coordinate
				int selectedRow = table.rowAtPoint(p);

				if(selectedRow >= 0)
				{
					final MessageFilterMenu mpopup;

					// check if selectedRow is in selection
					int[] rows = table.getSelectedRows();
					List rowlist = SUtil.arrayToList(rows);

					if(rowlist.size() > 1 && rowlist.contains(Integer.valueOf(selectedRow)))
					{
						mpopup = new MessageFilterMenu(tooltab.getPlugin(), getSelectedMessages());
					}
					else
					{
						// select row
						ListSelectionModel selection = table.getSelectionModel();
						selection.setSelectionInterval(selectedRow, selectedRow);

						Message message = model.getMessage((sorter.modelIndex(selectedRow)));
						mpopup = new MessageFilterMenu(tooltab.getPlugin(), message);
					}

					mpopup.show(e.getComponent(), e.getX(), e.getY());
				}

			}
		}

		/**
		 * Use mouseClicked for doubleclick.
		 */
		public void mouseClicked(final MouseEvent e)
		{

			if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
			{
				int selectedRow = table.getSelectedRow();
				if(selectedRow >= 0)
				{
					Message message = model.getMessage((sorter.modelIndex(selectedRow)));

					tooltab.getToolPanel().showElementDetails(message.getParameters());

				}

			}

		}
	}

}
