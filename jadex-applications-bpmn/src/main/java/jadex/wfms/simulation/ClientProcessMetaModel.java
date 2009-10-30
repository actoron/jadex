package jadex.wfms.simulation;

import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MParameter;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.collection.Tree;
import jadex.commons.collection.TreeNode;
import jadex.gpmn.model.MGpmnModel;
import jadex.gpmn.model.MPlan;
import jadex.javaparser.javaccimpl.ReflectNode;
import jadex.wfms.client.task.FetchDataTask;
import jadex.wfms.simulation.stateholder.ActivityStateController;
import jadex.wfms.simulation.stateholder.IParameterStateSet;
import jadex.wfms.simulation.stateholder.ParameterStateSetFactory;
import jadex.wfms.simulation.stateholder.ProcessStateController;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class ClientProcessMetaModel extends Tree implements TreeModel
{
	private static final Set DATA_TASKS = new HashSet();
	static
	{
		DATA_TASKS.add(FetchDataTask.class);
	}
	
	private BpmnModelLoader bpmnLoader;
	
	private Map processes;
	
	private List tasks;
	
	public ClientProcessMetaModel()
	{
		bpmnLoader = new BpmnModelLoader();
	}
	
	public void setRootModel(ILoadableComponentModel model) throws Exception
	{
		processes = new HashMap();
		tasks = new ArrayList();
		root = new ModelTreeNode();
		processes.put(resolveProcessName(model), root);
		TreeNode tmpRoot = buildTree(model);
		root.setChildren(tmpRoot.getChildren());
		root.setData(tmpRoot.getData());
	}
	
	public TreeNode buildTree(ILoadableComponentModel processModel) throws Exception
	{
		TreeNode node = new ModelTreeNode();
		node.setData(processModel);
		List subProcesses = getSubProcessModels(processModel);
		
		// Remove subprocesses that are already known
		for (Iterator it = subProcesses.iterator(); it.hasNext(); )
		{
			ILoadableComponentModel subModel = (ILoadableComponentModel) it.next();
			if (processes.containsKey(resolveProcessName(subModel)))
			{
				it.remove();
				ModelTreeNode linkNode = new ModelTreeNode();
				linkNode.setData(processes.get(resolveProcessName(subModel)));
				System.out.println("LinkNode: " + resolveProcessName(subModel) + " top model:" + resolveProcessName(processModel));
				node.addChild(linkNode);
			}
		}
		
		// Add subprocesses
		for (Iterator it = subProcesses.iterator(); it.hasNext(); )
		{
			ILoadableComponentModel subModel = (ILoadableComponentModel) it.next();
			ModelTreeNode tmpNode = new ModelTreeNode();
			processes.put(resolveProcessName(subModel), tmpNode);
			TreeNode subTree = buildTree(subModel);
			tmpNode.setChildren(subTree.getChildren());
			tmpNode.setData(subTree.getData());
			node.addChild(tmpNode);
		}
		
		// Add tasks
		Set tasks = getDataTasks(processModel);
		for (Iterator it = tasks.iterator(); it.hasNext(); )
		{
			MActivity task = (MActivity) it.next();
			TreeNode taskNode = getTaskNode(task);
			node.addChild(taskNode);
			this.tasks.add(taskNode);
		}
		
		return node;
	}
	
	public ProcessStateController createProcessStateController()
	{
		ProcessStateController pController = new ProcessStateController();
		
		for (Iterator it = tasks.iterator(); it.hasNext(); )
		{
			ModelTreeNode taskNode = (ModelTreeNode) it.next();
			if (taskNode.getNumberOfChildren() > 0)
			{
				MActivity task = (MActivity) taskNode.getData();
				ActivityStateController aController = new ActivityStateController(task.getName());
				
				List parameterNodes = taskNode.getChildren();
				for (Iterator it2 = parameterNodes.iterator(); it2.hasNext(); )
				{
					ModelTreeNode paramNode = (ModelTreeNode) it2.next();
					aController.addStateSet((IParameterStateSet) paramNode.getData());
				}
				
				pController.addActivityController(aController);
			}
		}
		
		return pController;
	}
	
	private TreeNode getTaskNode(MActivity task)
	{
		TreeNode node = new ModelTreeNode();
		node.setData(task);
		List outParams = task.getOutParameters();
		for (Iterator it = outParams.iterator(); it.hasNext(); )
		{
			MParameter param = (MParameter) it.next();
			node.addChild(new ModelTreeNode(ParameterStateSetFactory.createStateHolder(param)));
		}
		return node;
	}
	
	private List getSubProcessModels(ILoadableComponentModel processModel) throws Exception
	{
		List ret = new LinkedList();
		//ret.add(processModel);
		if (processModel instanceof MGpmnModel)
		{
			
			MGpmnModel gpmnModel = (MGpmnModel) processModel;
//			for(Iterator it = gpmnModel.getProcesses().iterator(); it.hasNext(); )
//			{
//				MProcess proc = (MProcess) it.next();
				for(Iterator it2 = gpmnModel.getPlans().iterator(); it2.hasNext(); )
				{
					MPlan plan = (MPlan) it2.next();
					MBpmnModel bpmnModel = bpmnLoader.loadBpmnModel(plan.getBpmnPlan(), gpmnModel.getImports());
					if (bpmnModel != null)
						ret.add(bpmnModel);
				}
//			}
		}
		else if (processModel instanceof MBpmnModel)
		{
			//TODO: Add subprocess search
		}
		
		return ret;
	}
	
	private Set getDataTasks(Object processModel)
	{
		Set ret = new HashSet();
		
		if (processModel instanceof MBpmnModel)
		{
			MBpmnModel bpmnModel = (MBpmnModel) processModel;
			Collection activities = (bpmnModel.getAllActivities().values());
			System.out.println("Activities: " + activities.toString());
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
	
	public static final String resolveProcessName(ILoadableComponentModel model)
	{
		String ret = model.getName();
		if (ret == null)
		{
			ret = model.getFilename();
			ret = ret.substring(Math.max(ret.lastIndexOf('/'), ret.lastIndexOf(File.separator)) + 1);
		}
		return ret;
	}
}


