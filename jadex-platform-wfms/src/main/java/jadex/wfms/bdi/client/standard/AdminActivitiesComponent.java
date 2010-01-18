package jadex.wfms.bdi.client.standard;

import jadex.wfms.client.IClientActivity;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class AdminActivitiesComponent extends JPanel
{
	public JTree activitiesTree;
	
	public AdminActivitiesComponent()
	{
		super (new GridBagLayout());
		
		activitiesTree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode()));
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
		
		userNode.add(new DefaultMutableTreeNode(activity));
		model.nodeStructureChanged(userNode);
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
	
	private DefaultMutableTreeNode getChildNode(DefaultMutableTreeNode parent, Object userObject)
	{
		int i = 0;
		while (i < parent.getChildCount())
		{
			if (((DefaultMutableTreeNode) parent.getChildAt(i)).getUserObject().equals(userObject))
				return ((DefaultMutableTreeNode) parent.getChildAt(i));
		}
		return null;
	}
}
