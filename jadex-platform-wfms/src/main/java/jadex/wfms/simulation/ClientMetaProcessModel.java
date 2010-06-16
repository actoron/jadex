package jadex.wfms.simulation;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MParameter;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.collection.Tree;
import jadex.commons.collection.TreeNode;
import jadex.gpmn.model2.MBpmnPlan;
import jadex.gpmn.model2.MGpmnModel;
import jadex.gpmn.model2.MSubprocess;
import jadex.wfms.client.task.WorkitemTask;
import jadex.wfms.simulation.stateholder.ParameterStateSetFactory;

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

public class ClientMetaProcessModel extends Tree implements TreeModel
{
	private String mainProcessName;
	
	private Map processes;
	
	private List tasks;
	
	public ClientMetaProcessModel()
	{
	}
	
	public void setRootModel(ClientSimulator sim, String processName, ILoadableComponentModel model) throws Exception
	{
		mainProcessName = processName;
		processes = new HashMap();
		tasks = new ArrayList();
		root = new ModelTreeNode();
		processes.put(resolveProcessName(model), root);
		TreeNode tmpRoot = buildTree(sim, model);
		root.setChildren(tmpRoot.getChildren());
		root.setData(tmpRoot.getData());
	}
	
	public TreeNode buildTree(ClientSimulator sim, ILoadableComponentModel processModel) throws Exception
	{
		TreeNode node = new ModelTreeNode();
		node.setData(processModel);
		List subProcesses = getSubProcessModels(sim, processModel);
		
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
			TreeNode subTree = buildTree(sim, subModel);
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
	
	public Scenario createScenario(String name)
	{
		Map taskMap = new HashMap();
		for (Iterator it = tasks.iterator(); it.hasNext(); )
		{
			ModelTreeNode task = (ModelTreeNode) it.next();
			Map paramMap = new HashMap();
			for (Iterator it2 = task.getChildren().iterator(); it2.hasNext(); )
			{
				MParameter param = (MParameter) ((ModelTreeNode) it2.next()).getData();
				paramMap.put(param.getName(), ParameterStateSetFactory.createStateHolder(param));
			}
			
			taskMap.put(((MActivity) task.getData()).getName(), paramMap);
		}
		
		return new Scenario(name, taskMap);
	}
	
	//TODO: PSController
	/*public ProcessStateController createProcessStateController()
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
	}*/
	
	/*public List getParameterSets()
	{
		List pSets = new ArrayList();
		for (Iterator it = tasks.iterator(); it.hasNext(); )
		{
			ModelTreeNode taskNode = (ModelTreeNode) it.next();
			if (taskNode.getNumberOfChildren() > 0)
			{
				List parameterNodes = taskNode.getChildren();
				for (Iterator it2 = parameterNodes.iterator(); it2.hasNext(); )
				{
					ModelTreeNode paramNode = (ModelTreeNode) it2.next();
					pSets.add((IParameterStateSet) paramNode.getData());
				}
			}
		}
		return pSets;
	}*/
	
	private TreeNode getTaskNode(MActivity task)
	{
		TreeNode node = new ModelTreeNode();
		node.setData(task);
		List outParams = task.getParameters(new String[]{MParameter.DIRECTION_INOUT, MParameter.DIRECTION_OUT});
		for (Iterator it = outParams.iterator(); it.hasNext(); )
		{
			MParameter param = (MParameter) it.next();
			if (!param.getName().startsWith("IGNORE_"))
				node.addChild(new ModelTreeNode(param));
		}
		return node;
	}
	
	private List getSubProcessModels(ClientSimulator sim, ILoadableComponentModel processModel) throws Exception
	{
		List ret = new LinkedList();
		//ret.add(processModel);
		if (processModel instanceof MGpmnModel)
		{
			
			MGpmnModel gpmnModel = (MGpmnModel) processModel;
			for (Iterator it = gpmnModel.getBpmnPlans().values().iterator(); it.hasNext(); )
			{
				MBpmnPlan plan = (MBpmnPlan) it.next();
				MBpmnModel bpmnModel = (MBpmnModel) sim.loadModelFromPath(plan.getPlanref());
				if (bpmnModel != null)
					ret.add(bpmnModel);
			}
			
			for (Iterator it = gpmnModel.getSubprocesses().values().iterator(); it.hasNext(); )
			{
				String processref = ((MSubprocess) it.next()).getProcessReference();
				if (processref.endsWith(".bpmn"))
				{
					MBpmnModel bpmnModel = (MBpmnModel) sim.loadModelFromPath(processref);
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
	
	private Set getDataTasks(Object processModel)
	{
		Set ret = new HashSet();
		
		if (processModel instanceof MBpmnModel)
		{
			MBpmnModel bpmnModel = (MBpmnModel) processModel;
			Collection activities = (bpmnModel.getAllActivities().values());
			for (Iterator it = activities.iterator(); it.hasNext(); )
			{
				MActivity activity = (MActivity) it.next();
				//ReflectNode classNode = ((ReflectNode) activity.getPropertyValue("class"));
				Class clazz = activity.getClazz();
				if (WorkitemTask.class.equals(clazz))
				{
					// TODO: Check for "in"-only parameters
					if ((activity.getParameters() != null) && (activity.getParameters().size() > 0))
					{
						for (Iterator it2 = activity.getParameters(new String[]{MParameter.DIRECTION_INOUT, MParameter.DIRECTION_OUT}).iterator(); it2.hasNext(); )
						{
							MParameter param = (MParameter) it2.next();
							if (!param.getName().startsWith("IGNORE_"))
							{
								ret.add(activity);
								break;
							}
						}
					}
				}
			}
		}
		
		return ret;
	}
	
	public String getMainProcessName()
	{
		return mainProcessName;
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
	
	/**
	 * Adds a change listener for state set changes.
	 * @param listener the listener
	 */
	/*public void addStateChangeListener(ChangeListener listener)
	{
		for (Iterator it = parameterSets.iterator(); it.hasNext(); )
		{
			IParameterStateSet stateSet = (IParameterStateSet) it.next();
			stateSet.addStateChangeListener(listener);
		}
	}*/
	
	/**
	 * Removes a change listener for state set changes.
	 * @param listener the listener
	 */
	/*public void removeStateChangeListener(ChangeListener listener)
	{
		for (Iterator it = parameterSets.iterator(); it.hasNext(); )
		{
			IParameterStateSet stateSet = (IParameterStateSet) it.next();
			stateSet.removeStateChangeListener(listener);
		}
	}*/
	
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


