package jadex.wfms.client.standard;

import jadex.bridge.IComponentChangeEvent;
import jadex.commons.collection.Tree;
import jadex.commons.collection.TreeNode;

import java.util.Map;

import javax.swing.JPanel;

public class ProcessMonitoringComponent extends JPanel
{
	protected Map<String, Tree> eventTrees;
	
	public ProcessMonitoringComponent()
	{
		
	}
	
	public void addEvent(IComponentChangeEvent event)
	{
		if (IComponentChangeEvent.SOURCE_CATEGORY_COMPONENT.equals(event.getSourceCategory()))
		{
			if (IComponentChangeEvent.EVENT_TYPE_CREATION.equals(event.getEventType()))
			{
				TreeNode root = new TreeNode(new ObjectLifeCycle(event));
				Tree tree = new Tree(root);
				eventTrees.put(event.getSourceName(), tree);
			}
			else if (IComponentChangeEvent.EVENT_TYPE_DISPOSAL.equals(event.getEventType()))
			{
				Tree tree = eventTrees.get(event.getSourceName());
				((ObjectLifeCycle)tree.getRootNode().getData()).setDisposalEvent(event);
			}
		}
		else
		{
		}
		if (event.getParent() == null)
		{
		}
	}
	
	protected static class ObjectLifeCycle
	{
		protected IComponentChangeEvent creationEvent;
		protected IComponentChangeEvent disposalEvent;
		
		public ObjectLifeCycle(IComponentChangeEvent creationEvent)
		{
			this.creationEvent = creationEvent;
		}
		
		public void setDisposalEvent(IComponentChangeEvent disposalEvent)
		{
			this.disposalEvent = disposalEvent;
		}
		
		public IComponentChangeEvent getCreationEvent()
		{
			return creationEvent;
		}
		
		public IComponentChangeEvent getDisposalEvent()
		{
			return disposalEvent;
		}
		
		public long getStartTime()
		{
			return creationEvent.getTime();
		}
		
		public long getEndTime()
		{
			if (creationEvent != null)
				return creationEvent.getTime();
			return System.currentTimeMillis();
		}
	}
}
