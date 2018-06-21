package jadex.base.gui.asynctree;

public class AsyncTreeModelEvent
{

	private Object[] path;
	private AsyncTreeModel model;
	private Object[] children;
	private int[] indices;

	public AsyncTreeModelEvent(AsyncTreeModel asyncTreeModel, Object[] path)
	{
		this.model = asyncTreeModel;
		this.path = path;
	}

	public AsyncTreeModelEvent(AsyncTreeModel asyncTreeModel, Object[] path, int[] indices, Object[] children)
	{
		this.model = asyncTreeModel;
		this.path = path;
		this.indices = indices;
		this.children = children;
	}

	public Object[] getPath()
	{
		return path;
	}

	public AsyncTreeModel getModel()
	{
		return model;
	}

	public Object[] getChildren()
	{
		return children;
	}

	public int[] getIndices()
	{
		return indices;
	}

	

}
