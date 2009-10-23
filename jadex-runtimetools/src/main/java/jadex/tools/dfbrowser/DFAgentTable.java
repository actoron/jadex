package jadex.tools.dfbrowser;

import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.IDFServiceDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SGUI;
import jadex.commons.jtable.TableSorter;

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

/**
 *  This class serves for displaying agent descriptions.
 */
public class DFAgentTable extends JTable
{
	static final IDFAgentDescription[] EMPTY = new IDFAgentDescription[0];
	
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"remove_agent", SGUI.makeIcon(DFBrowserPlugin.class, "/jadex/tools/common/images/new_remove_service.png"),
	});
	
	//-------- attributes --------
	
	/** The popup menu. */
	protected JPopupMenu popup;

	/** The browser plugin. */
	protected DFBrowserPlugin browser;

	//-------- constructors --------
	
	/**
	 * Constructor for DFAgentTable.
	 */
	public DFAgentTable(final DFBrowserPlugin browser)
	{
		super(new TableSorter(new AgentTableModel()));
		this.browser = browser;
		TableSorter sorter = (TableSorter)getModel();
		
		sorter.setTableHeader(getTableHeader());
		setPreferredScrollableViewportSize(new Dimension(800, 70));
		setDefaultRenderer(IComponentIdentifier.class, new AgentIdentifierRenderer());
		setDefaultRenderer(String[].class, new StringArrayRenderer());
		setDefaultRenderer(IDFServiceDescription[].class, new ServiceDescriptionArrayRenderer());
		setDefaultRenderer(Date.class, new LeaseTimeRenderer());
		
		addMouseListener(new MouseAdapter()
		{
			/*public void mouseClicked(MouseEvent e)
			{
				int selectedRow = getSelectedRow();
				if(e.getClickCount() > 1 && selectedRow >= 0)
				{
					agentSelected(model.getAgentDescription(sorter.modelIndex(selectedRow)));
				}
			}*/

			public void mousePressed(MouseEvent e)
			{
				if(e.isPopupTrigger())
				{
					popup.show(DFAgentTable.this, e.getX(), e.getY());
				}
			}

			public void mouseReleased(MouseEvent e)
			{
				if(e.isPopupTrigger())
				{
					popup.show(DFAgentTable.this, e.getX(), e.getY());
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
		menu.add(new JMenuItem(new AbstractAction("Remove agent description", icons.getIcon("remove_agent"))
		{
			public void actionPerformed(ActionEvent e)
			{
				int selectedRow = getSelectedRow();
				if(selectedRow >= 0)
				{
					TableSorter sorter = (TableSorter)getModel();
					AgentTableModel model = (AgentTableModel)sorter.getTableModel();
					browser.removeAgentRegistration(model.getAgentDescription(sorter.modelIndex(selectedRow)));
				}
			}

		}));
		/*menu.add(new JMenuItem(new AbstractAction("Show services")
		{
			public void actionPerformed(ActionEvent e)
			{
				TableSorter sorter = (TableSorter)getModel();
				ServiceTableModel model = (ServiceTableModel)sorter.getTableModel();
				int selectedRow = getSelectedRow();
				if(selectedRow >= 0)
				{
					agentSelected(model.getAgentDescription(sorter.modelIndex(selectedRow)));
				}
			}
		}));*/
	}

	/**
	 *  Get the selected agents.
	 *  @return the descriptions of selected agents
	 */
	public IDFAgentDescription[] getSelectedAgents()
	{
		IDFAgentDescription[] ret = EMPTY;
		
		int count = getSelectedRowCount();
		if(count>0)
		{
			TableSorter sorter = (TableSorter)getModel();
			AgentTableModel model = (AgentTableModel)sorter.getTableModel();
		
			ArrayList sa = new ArrayList();
			int[] rows = getSelectedRows();
			for(int i = 0; i < rows.length; i++)
			{
				sa.add(model.getAgentDescription(sorter.modelIndex(rows[i])));
			}
			ret = (IDFAgentDescription[])sa.toArray(new IDFAgentDescription[sa.size()]);
		}

		return ret;
	}

	/**
	 *  Sets Agent descriptions for this element.
	 *  @param ad The agent description.
	 */
	public void setAgentDescriptions(IDFAgentDescription[] ad)
	{
		TableSorter sorter = (TableSorter)getModel();
		AgentTableModel model = (AgentTableModel)sorter.getTableModel();
		model.setAgentDescriptions(ad);
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
