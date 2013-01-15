package jadex.android.controlcenter.componentViewer.tree;

public interface TreeModelListener
{

	void treeStructureChanged(TreeModelEvent treeModelEvent);

	void treeNodesChanged(TreeModelEvent treeModelEvent);

	void treeNodesRemoved(TreeModelEvent treeModelEvent);

	void treeNodesInserted(TreeModelEvent treeModelEvent);

}
