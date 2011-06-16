package jadex.wfms.client.standard;

import jadex.bridge.IComponentChangeEvent;
import jadex.commons.collection.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessEventTree
{
	protected Map<String, List<TreeNode>> eventmap;
	
	public ProcessEventTree()
	{
		this.eventmap = new HashMap<String, List<TreeNode>>();
	}
	
	/**
	 *  Adds an event to the tree.
	 *  
	 */
	/*public void addEvent(IComponentChangeEvent event)
	{
		if (event.getSourceCategory().equals(IComponentChangeEvent.SOURCE_CATEGORY_COMPONENT))
		{
			List<TreeNode> tnlist = eventmap.get(event.getSourceName());
			if (tnlist == null)
			{
				tnlist = new ArrayList<TreeNode>();
				eventmap.put(event.getSourceName(), tnlist);
			}
			
			TreeNode pn = null;
			if ( == null)
			int pos = findProcessNodeIndex(tnlist, event);
			
			eventlist.add(e)
		}
	}
	
	protected static final int findProcessNodeIndex(List<TreeNode> tnlist, IComponentChangeEvent event)
	{
		TreeNode center = tnlist.get(tnlist.size() / 2);
		long centertime = ((IComponentChangeEvent) ((List<IComponentChangeEvent>) center.getData()).get(0)).getTime();
		if (event.getTime() < centertime)
		{
			if (tnlist.size() == 1)
				return 0;
			return findProcessNodeIndex(tnlist.subList(0, tnlist.size()), event);
		}
		return findProcessNodeIndex(tnlist.subList(tnlist.size() / 2, tnlist.size()), event) + tnlist.size() / 2;
	}*/
}
