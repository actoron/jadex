package jadex.wfms.client.standard;

import jadex.wfms.client.IClientActivity;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class AdminActivitiesComponent extends JPanel
{
	private static final String TERMINATE_MENU_TEXT = "Terminate Activity";
	
	public JTree activitiesTree;
	
	public JMenuItem terminateItem;
	
	public AdminActivitiesComponent()
	{
		super (new GridBagLayout());
		
		final JPopupMenu contextMenu = new JPopupMenu();
		terminateItem = new JMenuItem(TERMINATE_MENU_TEXT);
		contextMenu.add(terminateItem);
		
		activitiesTree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode()));
		activitiesTree.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if ((e.getButton() == MouseEvent.BUTTON3) &&
					(e.getClickCount() == 1))
				{
					TreePath path = activitiesTree.getPathForLocation(e.getX(), e.getY());
					
					if (path == null)
						return;
					
					activitiesTree.setSelectionPath(path);
					
					if (!(((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof IClientActivity))
						return;
					
					contextMenu.show(activitiesTree, e.getX(), e.getY());
				}
			}
		});
		activitiesTree.setRootVisible(false);
		
		JScrollPane scrollPane = new JScrollPane(activitiesTree);
		GridBagConstraints g = new GridBagConstraints();
		g.weightx = 1.0;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.BOTH;
		add(scrollPane, g);
	}
	
	public void setUserActivities(Map userActivities)
	{
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		DefaultTreeModel model = (DefaultTreeModel) activitiesTree.getModel();
		activitiesTree.setRootVisible(false);
		
		for (Iterator it = userActivities.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry userEntry = (Map.Entry) it.next();
			DefaultMutableTreeNode userNode = new DefaultMutableTreeNode(userEntry.getKey());
			root.add(userNode);
			Set activities = (Set) userEntry.getValue();
			for (Iterator it2 = activities.iterator(); it2.hasNext(); )
			{
				IClientActivity activity = (IClientActivity) it2.next();
				userNode.add(new DefaultMutableTreeNode(activity));
			}
		}
		
		model.setRoot(root);
		int r = 0;
	    while (r < activitiesTree.getRowCount())
	    	activitiesTree.expandRow(r++);
	}
	
	public void addUserActivity(String userName, IClientActivity activity)
	{
		DefaultTreeModel model = (DefaultTreeModel) activitiesTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		DefaultMutableTreeNode userNode = getChildNode(root, userName);
		if (userNode == null)
		{
			userNode = new DefaultMutableTreeNode(userName);
			root.add(userNode);
			model.nodeStructureChanged(root);
		}
		
		boolean expand = activitiesTree.isExpanded(new TreePath(model.getPathToRoot(userNode)));
		if (userNode.getChildCount() == 0)
			expand = true;
		
		userNode.add(new DefaultMutableTreeNode(activity));
		model.nodeStructureChanged(userNode);
		
		if (expand)
			activitiesTree.expandPath(new TreePath(model.getPathToRoot(userNode)));
	}
	
	public void removeUserActivity(String userName, IClientActivity activity)
	{
		DefaultTreeModel model = (DefaultTreeModel) activitiesTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		DefaultMutableTreeNode userNode = getChildNode(root, userName);
		if (userNode != null)
		{
			DefaultMutableTreeNode activityNode = getChildNode(userNode, activity);
			userNode.remove(activityNode);
			model.nodeStructureChanged(userNode);
		}
	}
	
	public void setTerminateAction(Action action)
	{
		terminateItem.setAction(action);
		terminateItem.setText(TERMINATE_MENU_TEXT);
	}
	
	public IClientActivity getSelectedActivity()
	{
		IClientActivity ret = null;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) activitiesTree.getLastSelectedPathComponent();
		if ((node != null) && (node.getUserObject() instanceof IClientActivity))
			ret = (IClientActivity) node.getUserObject();
		
		return ret;
	}
	
	private DefaultMutableTreeNode getChildNode(DefaultMutableTreeNode parent, Object userObject)
	{
		int i = 0;
		while (i < parent.getChildCount())
		{
			if (((DefaultMutableTreeNode) parent.getChildAt(i)).getUserObject().equals(userObject))
				return ((DefaultMutableTreeNode) parent.getChildAt(i));
			++i;
		}
		return null;
	}
}
