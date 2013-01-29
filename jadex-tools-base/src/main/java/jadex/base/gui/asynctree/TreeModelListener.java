package jadex.base.gui.asynctree;

public interface TreeModelListener
{

	void treeStructureChanged(AsyncTreeModelEvent treeModelEvent);

	void treeNodesChanged(AsyncTreeModelEvent treeModelEvent);

	void treeNodesRemoved(AsyncTreeModelEvent treeModelEvent);

	void treeNodesInserted(AsyncTreeModelEvent treeModelEvent);

}
