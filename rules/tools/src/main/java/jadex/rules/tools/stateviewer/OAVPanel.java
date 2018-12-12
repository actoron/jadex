package jadex.rules.tools.stateviewer;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import jadex.commons.gui.TreeExpansionHandler;
import jadex.rules.state.IOAVState;
import jadex.rules.tools.stateviewer.OAVTreeModel.OAVTreeCellRenderer;
import jadex.rules.tools.stateviewer.OAVTreeModel.ObjectInspectorNode;
import jadex.rules.tools.stateviewer.OAVTreeModel.ObjectNode;
import jadex.rules.tools.stateviewer.OAVTreeModel.RootNode;

/**
 *  A panel displaying an OAV state.
 */
public class OAVPanel extends JPanel
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;
	
	/** The tree model. */
	protected OAVTreeModel	model;
	
	//-------- constructors --------
	
	/**
	 *  Create a panel for an OAV state.
	 *  @param state	The OAV state.
	 */
	public OAVPanel(IOAVState state)
	{
		super(new BorderLayout());
		this.state = state;
		this.model = new OAVTreeModel(state);
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
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);

		// Open first tree entry when only one exists (Hack?)
		if(model.getChildCount(model.getRoot())==1)
		{
			Object	node	= ((RootNode)model.getRoot()).getChildren().get(0);
			Object[]	obs	= null;	 
			if(node instanceof ObjectNode)
			{
				obs = ((ObjectNode)node).getPath();
			}
			else if(node instanceof ObjectInspectorNode)
			{
				obs = ((ObjectInspectorNode)node).getPath();
			}

			if(obs!=null)
			{
				tree.expandPath(new TreePath(obs));
			}
		}
		
		new TreeExpansionHandler(tree);
		tree.setCellRenderer(new OAVTreeCellRenderer());
		
		this.add(new JScrollPane(tree), BorderLayout.CENTER);
	}
	
	//-------- methods --------
	
	/**
	 *  Dispose the panel and remove all listeners.
	 */
	public void	dispose()
	{
		model.dispose();
	}
	
	//-------- static part --------
	
	/**
	 *  Create a frame for an OAV state.
	 *  @param title	The title for the frame.
	 *  @param state	The OAV state.
	 *  @param obj	The OAV root object.
	 *  @return	The frame.
	 */
	public static JFrame	createOAVFrame(String title, IOAVState state)
	{
		JFrame	frame	= new JFrame(title);
		frame.getContentPane().add(new OAVPanel(state), BorderLayout.CENTER);
		frame.setSize(600, 400);		
		return frame;
	}
}
