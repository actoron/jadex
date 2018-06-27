package jadex.wfms.simulation;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MParameter;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.ICacheableModel;
import jadex.commons.collection.Tree;
import jadex.commons.collection.TreeNode;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.gpmn.model.MBpmnPlan;
import jadex.gpmn.model.MGpmnModel;
import jadex.gpmn.model.MSubprocess;
import jadex.wfms.client.task.WorkitemTask;
import jadex.wfms.simulation.stateset.ParameterStateSetFactory;

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
	
	public IFuture setRootModel(int i,ClientSimulator sim, String processName, ICacheableModel model)
	{
		mainProcessName = processName;
		processes = new HashMap();
		tasks = new ArrayList();
		root = new ModelTreeNode();
		processes.put(resolveProcessName(model), root);
		
		final Future ret = new Future();
		buildTree(sim, model).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				TreeNode tmpRoot = (TreeNode) result;
				root.setChildren(tmpRoot.getChildren());
				root.setData(tmpRoot.getData());
				
				ret.setResult(null);
			} 
		});
		
		return ret;
	}
	
	public IFuture buildTree(final ClientSimulator sim, final ICacheableModel processModel)
	{
		final Future ret = new Future();
		final TreeNode node = new ModelTreeNode();
		node.setData(processModel);
		getSubProcessModels(sim, processModel).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				List subProcesses = (List) result;
				// Remove subprocesses that are already known
				for (Iterator it = subProcesses.iterator(); it.hasNext(); )
				{
					ICacheableModel subModel = (ICacheableModel) it.next();
					if (processes.containsKey(resolveProcessName(subModel)))
					{
						it.remove();
						ModelTreeNode linkNode = new ModelTreeNode();
						linkNode.setData(processes.get(resolveProcessName(subModel)));
						System.out.println("LinkNode: " + resolveProcessName(subModel) + " top model:" + resolveProcessName(processModel));
						node.addChild(linkNode);
					}
				}
				
				// Add tasks
				Set tasks = getDataTasks(processModel);
				for (Iterator it = tasks.iterator(); it.hasNext(); )
				{
					MActivity task = (MActivity) it.next();
					TreeNode taskNode = getTaskNode(task);
					node.addChild(taskNode);
					ClientMetaProcessModel.this.tasks.add(taskNode);
				}
				
				// Add subprocesses
				CollectionResultListener spCollector = new CollectionResultListener(subProcesses.size(), false, new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						Collection coll = (Collection) result;
						for (Iterator it = coll.iterator(); it.hasNext(); )
						{
							TreeNode subTree = (TreeNode) it.next();
							ModelTreeNode tmpNode = new ModelTreeNode();
							processes.put(resolveProcessName((ICacheableModel)subTree.getData()), tmpNode);
							tmpNode.setChildren(subTree.getChildren());
							tmpNode.setData(subTree.getData());
							node.addChild(tmpNode);
						}
						
						ret.setResult(node);
					}
				});
				
				for (Iterator it = subProcesses.iterator(); it.hasNext(); )
				{
					ICacheableModel subModel = (ICacheableModel) it.next();
					buildTree(sim, subModel).addResultListener(spCollector);
				}
			}
		});
		return ret;
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
	
	private IFuture getSubProcessModels(final ClientSimulator sim, ICacheableModel processModel)
	{
		final Future ret = new Future();
		if (processModel instanceof MGpmnModel)
		{
			
			final MGpmnModel gpmnModel = (MGpmnModel) processModel;
			CollectionResultListener planCollector = new CollectionResultListener(gpmnModel.getBpmnPlans().size(), false, new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					final List models = new LinkedList();
					Collection coll = (Collection) result;
					for (Iterator it = coll.iterator(); it.hasNext(); )
					{
						MBpmnModel bpmnModel = (MBpmnModel) it.next();
						if (bpmnModel != null)
							models.add(bpmnModel);
					}
					
					List<MSubprocess> bpmnsp = new ArrayList<MSubprocess>();
					for (Iterator it = gpmnModel.getSubprocesses().values().iterator(); it.hasNext(); )
					{
						MSubprocess process = (MSubprocess) it.next();
						if (process.getProcessReference().endsWith(".bpmn"))
							bpmnsp.add(process);
					}
					CollectionResultListener spCollector = new CollectionResultListener(bpmnsp.size(), false, new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							Collection coll = (Collection) result;
							for (Iterator it = coll.iterator(); it.hasNext(); )
							{
								MBpmnModel bpmnModel = (MBpmnModel) it.next();
								if (bpmnModel != null)
									models.add(bpmnModel);
							}
							
							ret.setResult(models);
						}
					});
					
					for (Iterator<MSubprocess> it = bpmnsp.iterator(); it.hasNext(); )
						sim.loadModelFromPath(it.next().getProcessReference()).addResultListener(spCollector);
				}
			});
			for (Iterator it = gpmnModel.getBpmnPlans().values().iterator(); it.hasNext(); )
			{
				MBpmnPlan plan = (MBpmnPlan) it.next();
				sim.loadModelFromPath(plan.getPlanref()).addResultListener(planCollector);
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
	
	public static final String resolveProcessName(ICacheableModel model)
	{
		String ret = null;
		IModelInfo modelinfo = null;
		if (model instanceof MBpmnModel)
			modelinfo = ((MBpmnModel) model).getModelInfo();
		else if (model instanceof MGpmnModel)
			modelinfo = ((MGpmnModel) model).getModelInfo();
		
		if (modelinfo != null)
		{
			ret = modelinfo.getName();
			if (ret == null)
			{
				ret = modelinfo.getFilename();
				ret = ret.substring(Math.max(ret.lastIndexOf('/'), ret.lastIndexOf(File.separator)) + 1);
			}
		}
		
		return ret;
	}
}


