package jadex.tools.dfbrowser;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.UIDefaults;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import jadex.base.gui.jtable.ComponentIdentifierRenderer;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.df.IDFServiceDescription;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.jtable.TableSorter;

/**
 *  This class serves for displaying component descriptions.
 */
public class DFComponentTable extends JTable
{
	static final IDFComponentDescription[] EMPTY = new IDFComponentDescription[0];
	
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"remove_component", SGUI.makeIcon(DFComponentTable.class, "/jadex/tools/common/images/new_remove_service.png"),
	});
	
	//-------- attributes --------
	
	/** The popup menu. */
	protected JPopupMenu popup;

	/** The panel. */
	protected DFBrowserPanel panel;

	//-------- constructors --------
	
	/**
	 * Constructor.
	 */
	public DFComponentTable(DFBrowserPanel panel)
	{
		super(new TableSorter(new ComponentTableModel()));
		this.panel	= panel;
		TableSorter sorter = (TableSorter)getModel();
		
		sorter.setTableHeader(getTableHeader());
		setPreferredScrollableViewportSize(new Dimension(800, 70));
		setDefaultRenderer(IComponentIdentifier.class, new ComponentIdentifierRenderer(null));
		setDefaultRenderer(String[].class, new StringArrayRenderer());
		setDefaultRenderer(IDFServiceDescription[].class, new ServiceDescriptionArrayRenderer());
		setDefaultRenderer(Date.class, new LeaseTimeRenderer());
		
		addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(e.isPopupTrigger())
				{
					popup.show(DFComponentTable.this, e.getX(), e.getY());
				}
			}

			public void mousePressed(MouseEvent e)
			{
				if(e.isPopupTrigger())
				{
					popup.show(DFComponentTable.this, e.getX(), e.getY());
				}
			}

			public void mouseReleased(MouseEvent e)
			{
				if(e.isPopupTrigger())
				{
					popup.show(DFComponentTable.this, e.getX(), e.getY());
				}
			}
		});

		popup = new JPopupMenu();
		addMenuItems(popup);
	}

	/**
	 *  Add the menu items.
	 *  @param menu The menu.
	 */
	protected void addMenuItems(JPopupMenu menu)
	{
		menu.add(new JMenuItem(new AbstractAction("Remove component description", icons.getIcon("remove_component"))
		{
			public void actionPerformed(ActionEvent e)
			{
				int selectedRow = getSelectedRow();
				if(selectedRow >= 0)
				{
					TableSorter sorter = (TableSorter)getModel();
					ComponentTableModel model = (ComponentTableModel)sorter.getTableModel();
					panel.removeComponentRegistration(model.getComponentDescription(sorter.modelIndex(selectedRow)));
				}
			}

		}));
	}

	/**
	 *  Get the selected components.
	 *  @return the descriptions of selected components
	 */
	public IDFComponentDescription[] getSelectedComponents()
	{
		IDFComponentDescription[] ret = EMPTY;
		
		int count = getSelectedRowCount();
		if(count>0)
		{
			TableSorter sorter = (TableSorter)getModel();
			ComponentTableModel model = (ComponentTableModel)sorter.getTableModel();
		
			ArrayList sa = new ArrayList();
			int[] rows = getSelectedRows();
			for(int i = 0; i < rows.length; i++)
			{
				sa.add(model.getComponentDescription(sorter.modelIndex(rows[i])));
			}
			ret = (IDFComponentDescription[])sa.toArray(new IDFComponentDescription[sa.size()]);
		}

		return ret;
	}

	/**
	 *  Sets descriptions for this element.
	 *  @param ad The component description.
	 */
	public void setComponentDescriptions(IDFComponentDescription[] ad)
	{
		TableSorter sorter = (TableSorter)getModel();
		ComponentTableModel model = (ComponentTableModel)sorter.getTableModel();
		model.setComponentDescriptions(ad);
	}

	/**
	 *  Get the properties.
	 *  @param props The properties.
	 */
	public Properties	getProperties()
	{
		Properties	props	= new Properties();
		TableColumnModel cm = getColumnModel();
		for(int i=0; i<cm.getColumnCount(); i++)
		{
			TableColumn column = cm.getColumn(i);
			props.addProperty(new Property("columnwidth", Integer.toString(column.getWidth())));
		}
		return props;
	}

	/**
	 *  Set the properties.
	 *  @param props The properties.
	 */
	public void setProperties(Properties props)
	{
		Property[]	columnprops	= props.getProperties("columnwidth");
		TableColumnModel cm = getColumnModel();
		for(int i=0; i<cm.getColumnCount() && i<columnprops.length; i++)
		{
			cm.getColumn(i).setPreferredWidth(Integer.parseInt(columnprops[i].getValue()));
		}
	}
}
