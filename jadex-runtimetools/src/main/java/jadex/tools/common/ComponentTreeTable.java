package jadex.tools.common;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IExternalAccess;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.jtable.ResizeableTableHeader;
import jadex.commons.jtable.VisibilityTableColumnModel;
import jadex.tools.common.jtreetable.DefaultTreeTableCellRenderer;
import jadex.tools.common.jtreetable.DefaultTreeTableModel;
import jadex.tools.common.jtreetable.DefaultTreeTableNode;
import jadex.tools.common.jtreetable.JTreeTable;
import jadex.tools.common.jtreetable.TreeTableNodeType;
import jadex.tools.common.jtreetable.TreeTablePopupListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *  The element tree shows elements in a tree view.
 */
public class ComponentTreeTable extends JScrollPane
{
	//-------- constants --------

	/** The container tree table node type. */
	public static String NODE_CONTAINER = "container_node";

	/** The element tree table node type. */
	public static String NODE_COMPONENT = "component_node";


	//-------- static part --------

	/** The image icons. */
	public static UIDefaults icons = new UIDefaults(new Object[]
	{
		// Node icons.
		NODE_COMPONENT, SGUI.makeIcon(ComponentTreeTable.class, "/jadex/tools/common/images/new_agent.png"), 
		NODE_CONTAINER, SGUI.makeIcon(ComponentTreeTable.class, "/jadex/tools/common/images/new_platform.png")
	});

	//-------- attributes --------

	/** The tree table node types. */
	protected Map nodetypes;

	/** The platform node containing the agents. */
	protected DefaultTreeTableNode	platform;

	/** The tree table nodes (cid->node). */
	protected Map nodes;

	/** The tree table. */
	protected JTreeTable treetable;
	
	/** The table header for auto adjusting of columns. */
	protected ResizeableTableHeader	header;

	//-------- constructors --------

	/**
	 *  Open the gui.
	 */
	public ComponentTreeTable(IExternalAccess component)//IServiceProvider container)
	{
		IExternalAccess root = component;
		while(root.getParent()!=null)
			root = root.getParent();
		
		// Initialize default node types (may be overriden from outside).
		this.nodetypes = new HashMap();
		addNodeType(new TreeTableNodeType(NODE_CONTAINER, new Icon[]{icons.getIcon(NODE_CONTAINER)}, new String[]{"name"}, new String[]{"Name"}));
		addNodeType(new ComponentTreeTableNodeType(root.getServiceProvider()));

		this.nodes = new HashMap();
		this.getViewport().setBackground(UIManager.getColor("List.background"));

		// Use custom font (larger).
		//Font	font	= UIManager.getFont("List.font");
		//		font	= font.deriveFont(font.getStyle()|Font.BOLD, font.getSize2D()*1.25f);
		//		font	= font.deriveFont(font.getSize2D()*1.25f);
		DefaultTreeTableCellRenderer renderer = new DefaultTreeTableCellRenderer();
		//renderer.setFont(font);

		// Setup tree table component.
		// hack: todo
		this.platform = new DefaultTreeTableNode(getNodeType(NODE_CONTAINER), root.getComponentIdentifier());
		nodes.put(root.getComponentIdentifier(), platform);
		this.treetable = new JTreeTable(new DefaultTreeTableModel(platform, getNodeType(NODE_COMPONENT).getColumnNames()));
		//treetable.setFont(font);
		//treetable.setRowHeight(treetable.getFontMetrics(font).getHeight());
		treetable.getTree().setShowsRootHandles(true);
		treetable.getTree().setCellRenderer(renderer);
		treetable.addMouseListener(new TreeTablePopupListener());
		treetable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		treetable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		treetable.setBackground(UIManager.getColor("List.background"));
		//treetable.table.setShowGrid(true);

		// Initialize visibility of columns and add mouselistener
		VisibilityTableColumnModel columnmodel = new VisibilityTableColumnModel();
		treetable.setColumnModel(columnmodel);
		treetable.createDefaultColumnsFromModel();
		columnmodel.addMouseListener(treetable);
		// Make first column unhideable
		columnmodel.setColumnChangeable(columnmodel.getColumn(0), false);

		// Add resizable header.
		this.header = new ResizeableTableHeader();
		header.setColumnModel(treetable.getColumnModel());
//		header.setAutoResizingEnabled(true); //default
		header.setIncludeHeaderWidth(false); //default
		treetable.setTableHeader(header);
		// Set the preferred, minimum and maximum column widths
//		header.setAllColumnWidths(100, -1, -1);
//		header.setColumnWidths(treetable.getColumnModel().getColumn(0), 100, 100, -1);

		//		// TreeExpansionHandler remembers expanded nodes.
		//		new TreeExpansionHandler(treetable.getTree());

		setViewportView(treetable);
	}

	//--------- methods --------

	/**
	 *  Adjust the column widths of the table to fit the current contents.
	 * /
	public void	adjustColumnWidths()
	{
		header.resizeAllColumns();
	}*/
	
	/**
	 *  Add an agent.
	 */
	public void addComponent(IComponentDescription desc)
	{
		DefaultTreeTableNode parent	= (DefaultTreeTableNode)(desc.getParent()!=null ? nodes.get(desc.getParent()) : null);
		
		if(parent!=null)
		{
			Map values = new HashMap();
			values.put("name", desc.getName().getName());
			values.put("type", desc.getType());
			values.put("state", desc.getState());
			//		values.put("ownership", description.getOwnership());
			String[] addresses = desc.getName().getAddresses();
			if(addresses.length > 0)
				values.put("address", addresses[0]);
		
			DefaultTreeTableNode	node	= new DefaultTreeTableNode(getNodeType(NODE_COMPONENT), desc, values);
			nodes.put(desc.getName(), node);
			parent.add(node);
	
			// Expand node on first add. (hack???)
			if(parent.getChildCount() == 1)
			{
				this.treetable.getTree().expandPath(new TreePath(parent.getPath()));
			}
		}
	}
	
	/**
	 *  Update an existing agent description.
	 */
	public void updateComponent(IComponentDescription desc)
	{
		DefaultTreeTableNode	parent	= (DefaultTreeTableNode)(desc.getParent()!=null ? nodes.get(desc.getParent()) : platform);

		Map values = new HashMap();
		values.put("name", desc.getName().getName());
		values.put("type", desc.getType());
		values.put("state", desc.getState());
		//values.put("ownership", description.getOwnership());
		String[] addresses = desc.getName().getAddresses();
		if(addresses.length > 0)
			values.put("address", addresses[0]);
		
		DefaultTreeTableNode node = parent.getChild(desc);
		if(node!=null)	// Might be removed in meantime.
		{
			node.setValues(values);
			node.setUserObject(desc);
		}
		
		//System.out.println("update for: "+node);
		
		// Reload not needed (and crumples column widths).
		//((DefaultTreeTableModel)treetable.getTree().getModel()).reload(node);
	}

	/**
	 *  Remove an agent.
	 *  @param description
	 */
	public void removeComponent(IComponentDescription description)
	{
		DefaultTreeTableNode	parent	= (DefaultTreeTableNode)(description.getParent()!=null ? nodes.get(description.getParent()) : platform);
		if(parent!=null)
		{	
			DefaultTreeTableNode child = parent.getChild(description);
			if(child != null)
			{
				nodes.remove(description.getName());
				parent.remove(parent.getIndex(child));
			}
		}
		// else event for already removed parent.
	}
	
	/**
	 *  Remove all components.
	 */
	public void removeComponents()
	{
		// todo: methods must be called on DefaultTreeTableNode
		// to get the model informed about the changes
		platform.removeAllChildren();
	}
	
	/**
	 *  Get all agents.
	 */
	public DefaultTreeTableNode[] getAllComponents()
	{
		List ret = SCollection.createArrayList();
		
		DefaultTreeTableNode root = (DefaultTreeTableNode)((DefaultTreeTableModel)treetable.getTree().getModel()).getRoot();
		List children = getAllChildren(root);
		for(int i=0; i<children.size(); i++)
		{
			DefaultTreeTableNode node = (DefaultTreeTableNode)children.get(i);
			if(node.getUserObject() instanceof IComponentDescription)
				ret.add(node);
		}
		
		return (DefaultTreeTableNode[])ret.toArray(new DefaultTreeTableNode[ret.size()]);
	}
	
	/**
	 *  Print out current column widths.
	 *  Used for debugging nasty swing bugs.
	 */
	protected void	printColumnWidths(String msg)
	{
		TableColumnModel	tcm	= treetable.getColumnModel();
		int[]	widths	= new int[tcm.getColumnCount()];
		for(int i=0; i<tcm.getColumnCount(); i++)
			widths[i]	= tcm.getColumn(i).getWidth();
		System.out.println(msg+", column widths: "+SUtil.arrayToString(widths));
	}
	
	/**
	 *  Get the column widths.
	 *  @param widths The widths.
	 */
	public int[] getColumnWidths()
	{
		TableColumnModel	tcm	= treetable.getColumnModel();
		int[]	widths	= new int[tcm.getColumnCount()];
		for(int i=0; i<tcm.getColumnCount(); i++)
			widths[i]	= tcm.getColumn(i).getWidth();
		return widths;
	}
	
	/**
	 *  Set the column width.
	 *  @param widths The widths.
	 */
	public void setColumnWidths(int[] widths)
	{
//		System.out.println("Widths: "+SUtil.arrayToString(widths));
		TableColumnModel	tcm	= treetable.getColumnModel();
		for(int i=0; i<tcm.getColumnCount() && i<widths.length; i++)
			tcm.getColumn(i).setPreferredWidth(widths[i]);
	}
	
	/**
	 * Load the properties.
	 */
	public void setProperties(Properties props)
	{
//		System.out.println("Starter set props: "+props);
		
		Property[] aps = props.getProperties();
		int[] widths = new int[aps.length];
		for(int i=0; i<aps.length; i++)
			widths[i] = Integer.parseInt(aps[i].getValue());
		setColumnWidths(widths);
	}

	/**
	 * Save the properties.
	 * @param props
	 */
	public Properties	getProperties()
	{
		Properties props = new Properties();
		int[] ws = getColumnWidths();
		for(int i=0; i<ws.length; i++)
			props.addProperty(new Property(""+i, ""+ws[i]));
		return props;
	}
	
	/**
	 *  Get all children from a root node.
	 *  @return A list of all children.
	 */
	protected List getAllChildren(DefaultTreeTableNode root)
	{
		List ret = SCollection.createArrayList();
		for(int i=0; root!=null && i<root.getChildCount(); i++)
		{
			DefaultTreeTableNode child = (DefaultTreeTableNode)root.getChildAt(i);
			ret.add(child);
			ret.addAll(getAllChildren(child));
		}
		
		return ret;
	}

	/**
	 *  Get a node type.
	 *  Can be used e.g. to add popup actions.
	 */
	public TreeTableNodeType getNodeType(String name)
	{
		return (TreeTableNodeType)nodetypes.get(name);
	}

	/**
	 *  Add a node type.
	 *  Can also be used e.g. to override the default node types.
	 */
	public void addNodeType(TreeTableNodeType type)
	{
		nodetypes.put(type.getName(), type);
	}

	/**
	 *  Get the tree table.
	 * @return the tree table object
	 */
	public JTreeTable getTreetable()
	{
		return treetable;
	}

	/** 
	 * @return the platform node
	 */
	public DefaultMutableTreeNode getPlatform()
	{
		return platform;
	}
	
}