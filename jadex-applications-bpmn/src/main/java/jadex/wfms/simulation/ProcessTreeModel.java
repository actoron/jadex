package jadex.wfms.simulation;

import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.runtime.task.ShowClientInfoTask;
import jadex.commons.collection.Tree;
import jadex.commons.collection.TreeNode;
import jadex.gpmn.model.MGpmnModel;
import jadex.gpmn.model.MPlan;
import jadex.gpmn.model.MProcess;
import jadex.javaparser.javaccimpl.ReflectNode;
import jadex.wfms.IProcessModel;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class ProcessTreeModel extends Tree implements TreeModel
{
	private static final Set DATA_TASKS = new HashSet();
	static
	{
		DATA_TASKS.add(ShowClientInfoTask.class);
	}
	
	private BpmnModelLoader bpmnLoader;
	
	private Set processNames;
	
	public ProcessTreeModel()
	{
		bpmnLoader = new BpmnModelLoader();
	}
	
	public void setRootModel(IProcessModel model) throws Exception
	{
		processNames = new HashSet();
		processNames.add(model.getFilename());
		root = buildTree(model);
	}
	
	public TreeNode buildTree(IProcessModel processModel) throws Exception
	{
		TreeNode node = new ModelTreeNode();
		node.setData(processModel);
		List subProcesses = getSubProcessModels(processModel);
		
		// Remove subprocesses that are already known
		for (Iterator it = subProcesses.iterator(); it.hasNext(); )
		{
			IProcessModel subModel = (IProcessModel) it.next();
			System.out.println(processModel.getName() + " Submodel: " + subModel.getName() + " " + subModel.getFilename());
			if (processNames.contains(subModel.getFilename()))
				it.remove();
			else
				processNames.add(subModel.getFilename());
		}
		
		// Add subprocesses
		for (Iterator it = subProcesses.iterator(); it.hasNext(); )
		{
			IProcessModel subModel = (IProcessModel) it.next();
			node.addChild(buildTree(subModel));
		}
		
		// Add activities
		Set activities = getDataActivities(processModel);
		for (Iterator it = activities.iterator(); it.hasNext(); )
		{
			MActivity activity = (MActivity) it.next();
			node.addChild(getActivityNode(activity));
		}
		
		return node;
	}
	
	private TreeNode getActivityNode(MActivity activity)
	{
		TreeNode node = new ModelTreeNode();
		node.setData(activity);
		List outParams = activity.getOutParameters();
		for (Iterator it = outParams.iterator(); it.hasNext(); )
		{
			MParameter param = (MParameter) it.next();
			node.addChild(new ModelTreeNode(param));
		}
		return node;
	}
	
	private List getSubProcessModels(IProcessModel processModel) throws Exception
	{
		List ret = new LinkedList();
		ret.add(processModel);
		if (processModel instanceof MGpmnModel)
		{
			
			MGpmnModel gpmnModel = (MGpmnModel) processModel;
			for (Iterator it = gpmnModel.getProcesses().iterator(); it.hasNext(); )
			{
				MProcess proc = (MProcess) it.next();
				for(Iterator it2 = proc.getPlans().iterator(); it2.hasNext(); )
				{
					MPlan plan = (MPlan) it2.next();
					System.out.println("BPMN Plan: " + plan.getBpmnPlan());
					MBpmnModel bpmnModel = bpmnLoader.loadBpmnModel(plan.getBpmnPlan(), gpmnModel.getImports());
					if (bpmnModel != null)
						ret.add(bpmnModel);
				}
			}
		}
		else if (processModel instanceof MBpmnModel)
		{
			//TODO: Add subprocess search
		}
		
		return ret;
	}
	
	private Set getDataActivities(Object processModel)
	{
		Set ret = new HashSet();
		
		if (processModel instanceof MBpmnModel)
		{
			MBpmnModel bpmnModel = (MBpmnModel) processModel;
			Collection activities = (bpmnModel.getAllActivities().values());
			for (Iterator it = activities.iterator(); it.hasNext(); )
			{
				MActivity activity = (MActivity) it.next();
				ReflectNode classNode = ((ReflectNode) activity.getPropertyValue("class"));
				if (classNode != null)
				{
					Class activityClass = (Class) classNode.getConstantValue();
					System.out.println(activityClass);
					if (DATA_TASKS.contains(activityClass))
						ret.add(activity);
				}
			}
		}
		
		return ret;
	}
	
	private Set modelSetToNameSet(Set modelSet)
	{
		Set ret = new HashSet();
		for (Iterator it = modelSet.iterator(); it.hasNext(); )
		{
			IProcessModel model = (IProcessModel) it.next();
			ret.add(model.getName());
		}
		return ret;
	}
	
	// ------------------- TreeModel ------------------------
	
	public void addTreeModelListener(TreeModelListener l)
	{
	}
	
	public void removeTreeModelListener(TreeModelListener l)
	{
	}
	
	public Object getChild(Object parent, int index)
	{
		TreeNode parentNode = (TreeNode) parent;
		return parentNode.getChildren().get(index);
	}
	
	public int getChildCount(Object parent)
	{
		TreeNode parentNode = (TreeNode) parent;
		return parentNode.getNumberOfChildren();
	}
	
	public int getIndexOfChild(Object parent, Object child)
	{
		TreeNode parentNode = (TreeNode) parent;
		return parentNode.getChildren().indexOf(child);
	}
	
	public Object getRoot()
	{
		return root;
	}
	
	public boolean isLeaf(Object node)
	{
		TreeNode tNode = (TreeNode) node;
		return tNode.getChildren() == null? false : tNode.getChildren().isEmpty();
	}
	
	public void valueForPathChanged(TreePath path, Object newValue)
	{
	}
	
	private class ModelTreeNode extends TreeNode
	{
		public ModelTreeNode()
		{
			super();
		}
		
		public ModelTreeNode(Object data)
		{
			super(data);
		}
		
		public String toString()
		{
			if (data instanceof IProcessModel)
			{
				IProcessModel model = ((IProcessModel) data);
				String ret = model.getName();
				if (ret == null)
				{
					ret = model.getFilename();
					ret = ret.substring(ret.lastIndexOf(File.separator) + 1);
				}
				return ret;
			}
			else if (data instanceof MActivity)
				return ((MActivity) data).getName();
			else if (data instanceof MParameter)
				return ((MParameter) data).getName();
			else
				return String.valueOf(data);
		}
	}
}
