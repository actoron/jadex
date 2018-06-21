package jadex.tools.comanalyzer.table;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.UIDefaults;

import jadex.commons.SUtil;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.jtable.ResizeableTableHeader;
import jadex.tools.comanalyzer.ComanalyzerPlugin;
import jadex.tools.comanalyzer.ToolCanvas;
import jadex.tools.comanalyzer.ToolTab;


/**
 * The tooltab for displaying messages in a table.
 */
public class TablePanel extends ToolTab
{
	//-------- constants --------

	/** Icon paths */
	private static final String COMANALYZER_IMAGES = "/jadex/tools/comanalyzer/images/";


	/** The image icons. */
	protected static final UIDefaults defaults = new UIDefaults(new Object[]{"resize", SGUI.makeIcon(TablePanel.class, COMANALYZER_IMAGES + "resize.png"), "scrolllock",
			SGUI.makeIcon(TablePanel.class, COMANALYZER_IMAGES + "scrolllock.png"), "autoscroll", SGUI.makeIcon(TablePanel.class, COMANALYZER_IMAGES + "autoscroll.png"),});

	//-------- attributes --------

	/** The container for the table */
	protected TableCanvas panelcan;

	//-------- constructors --------

	/**
	 * Create the tabel panel.
	 */
	public TablePanel(ComanalyzerPlugin plugin)
	{
		super(plugin, "Table", null);

		// Initialize message table.
		panelcan = new TableCanvas(this);

		// Initialize tool bar.
		this.setLayout(new BorderLayout());
		this.add(BorderLayout.NORTH, SGUI.createToolBar("Tabel Panel Options", getActions()));
		this.add(BorderLayout.CENTER, panelcan);

	}

	// -------- ToolTab methods --------

	/**
	 * Returns the TableCanvas
	 * 
	 * @see jadex.tools.comanalyzer.ToolTab#getCanvas()
	 */
	public ToolCanvas getCanvas()
	{
		return panelcan;
	}

	/**
	 * Get the (menu/toolbar) actions.
	 */
	public Action[] getActions()
	{
		if(actions == null)
		{
			List actionlist = SUtil.arrayToList(super.getActions());
			actionlist.add(null); // seperator
			actionlist.add(RESIZE_COLUMNS);
			actionlist.add(SCROLL_LOCK);

			actions = (Action[])actionlist.toArray((new Action[actionlist.size()]));
		}

		return actions;
	}

	// -------- Actions --------

	/** Resize the column width to fit content. */
	protected final AbstractAction RESIZE_COLUMNS = new AbstractAction("Resize Column Widths to Fit Contents", defaults.getIcon("resize"))
	{
		public void actionPerformed(ActionEvent ae)
		{
			((ResizeableTableHeader)panelcan.table.getTableHeader()).resizeAllColumns();
		}
	};

	/** Toggle scroll lock */
	protected final AbstractAction SCROLL_LOCK = new AbstractAction("Scroll Lock", defaults.getIcon("scrolllock"))
	{
		public void actionPerformed(ActionEvent ae)
		{
			panelcan.setAutoScroll(!panelcan.isAutoScroll());
			SCROLL_LOCK.putValue(Action.SHORT_DESCRIPTION, panelcan.isAutoScroll() ? "Scroll Lock" : "Auto Scroll");
			SCROLL_LOCK.putValue(Action.SMALL_ICON, panelcan.isAutoScroll() ? defaults.getIcon("scrolllock") : defaults.getIcon("autoscroll"));
		}
	};
}
