package jadex.base.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import jadex.base.gui.ObjectTreeModel.ObjectTreeCellRenderer;
import jadex.commons.gui.TreeExpansionHandler;

/**
 *  Panel for inspecting Java objects.
 */
public class ObjectInspectorPanel  extends JPanel
{
	/**
	 *  Create a panel for an OAV state.
	 *  @param state	The OAV state.
	 */
	public ObjectInspectorPanel(Object root)
	{
		super(new BorderLayout());
		final ObjectTreeModel model = new ObjectTreeModel(root);
		final JTree tree = new JTree(model);
		model.addTreeModelListener(new TreeModelListener()
		{
			public void treeStructureChanged(TreeModelEvent e)
			{
			}
			
			public void treeNodesRemoved(TreeModelEvent e)
			{
			}
			
			public void treeNodesInserted(TreeModelEvent e)
			{
				// In case the first child is added to root expansion must be called
				// JTree bug: http://forums.java.net/jive/thread.jspa?threadID=17914
				Object[] treepath = e.getPath();
				if(treepath!=null && treepath.length==1 && e.getChildren().length==1)
				{
					tree.expandPath(new TreePath(model.getRoot()));
				}
			}
			
			public void treeNodesChanged(TreeModelEvent e)
			{
			}
		});
		tree.setRootVisible(true);
		tree.setShowsRootHandles(true);
	
		// Open first tree entry when only one exists (Hack?)
//		if(model.getChildCount(model.getRoot())==1)
//		{
//			ObjectInspectorNode	node = (ObjectInspectorNode)model.getRoot();
//			Object[] obs =  node.getPath();
//	
//			if(obs!=null)
//			{
//				tree.expandPath(new TreePath(obs));
//			}
//		}
		
		new TreeExpansionHandler(tree);
		tree.setCellRenderer(new ObjectTreeCellRenderer());
		
		this.add(new JScrollPane(tree), BorderLayout.CENTER);
	}

	//-------- static part --------
	
	/**
	 *  Create a frame for an OAV state.
	 *  @param title	The title for the frame.
	 *  @param state	The OAV state.
	 *  @param obj	The OAV root object.
	 *  @return	The frame.
	 */
	public static JFrame createObjectFrame(String title, Object root)
	{
		JFrame	frame	= new JFrame(title);
		frame.getContentPane().add(new ObjectInspectorPanel(root), BorderLayout.CENTER);
		frame.setSize(600, 400);		
		return frame;
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		JFrame f = createObjectFrame("test", new JFrame());
		f.setVisible(true);
	}
}
