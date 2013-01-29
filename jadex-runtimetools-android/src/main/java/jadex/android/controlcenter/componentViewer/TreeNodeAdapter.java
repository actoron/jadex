package jadex.android.controlcenter.componentViewer;

import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.INodeListener;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.asynctree.AsyncTreeModelEvent;
import jadex.base.gui.asynctree.TreeModelListener;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

public class TreeNodeAdapter extends BaseAdapter
{

	private AsyncTreeModel model;
	private Context context;
	private ITreeNode currentNode;
	private Handler uiHandler;

	public TreeNodeAdapter(Context context, AsyncTreeModel model)
	{
		this.context = context;
		this.model = model;
		this.currentNode = model.getRoot();
		this.uiHandler = new Handler();
		
		model.addNodeListener(new INodeListener()
		{
			
			@Override
			public void nodeRemoved(ITreeNode node)
			{
				System.out.println("node removed: "+ node.toString());
			}
			
			@Override
			public void nodeAdded(ITreeNode node)
			{
				System.out.println("node added: " +node.toString());
			}
		});
		
		model.addTreeModelListener(new TreeModelListener()
		{
			
			@Override
			public void treeStructureChanged(AsyncTreeModelEvent treeModelEvent)
			{
				System.out.println("treeStructureChanged: " +treeModelEvent.toString());				
			}
			
			@Override
			public void treeNodesRemoved(AsyncTreeModelEvent treeModelEvent)
			{
				System.out.println("treeNodesRemoved: " +treeModelEvent.toString());				
			}
			
			@Override
			public void treeNodesInserted(AsyncTreeModelEvent treeModelEvent)
			{
				System.out.println("treeNodesInserted: " +treeModelEvent.toString());	
			}
			
			@Override
			public void treeNodesChanged(AsyncTreeModelEvent treeModelEvent)
			{
				System.out.println("treeNodesChanged: " +treeModelEvent.toString());
				uiHandler.post(new Runnable()
				{
					
					@Override
					public void run()
					{
						notifyDataSetChanged();
					}
				});
			}
		});
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ITreeNode node = getItem(position);
		TreeNodeView treeNodeView;
		if (convertView == null)
		{
			treeNodeView = new TreeNodeView(context);
		} else
		{
			treeNodeView = (TreeNodeView) convertView;
		}

		treeNodeView.setName(node.toString());

		return treeNodeView;
	}
	
	public int getCount()
	{
		return currentNode.getChildCount();
	}

	public ITreeNode getItem(int position)
	{
		return model.getChild(currentNode, position);
	}

	public long getItemId(int position)
	{
		return position;
	}

//	public boolean hasStableIds()
//	{
//		return false;
//	}
//
//	public int getItemViewType(int position)
//	{
//		return 0;
//	}
//
//	public int getViewTypeCount()
//	{
//		return 1;
//	}

//	public boolean isEmpty()
//	{
//		return currentNode.isLeaf();
//	}
//
//	public boolean areAllItemsEnabled()
//	{
//		return false;
//	}

	public boolean isEnabled(int position)
	{
		boolean leaf = model.getChild(currentNode, position).isLeaf();
		return true;
	}
	
	
	public OnItemClickListener onTreeNodeClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			ITreeNode item = getItem(position);
			setCurrentNode(item);
		}
		
	};
	
	protected void setCurrentNode(ITreeNode item)
	{
		currentNode = item;
		notifyDataSetChanged();
	}

	public ITreeNode getCurrentNode() {
		return currentNode;
	}
	
	

}
