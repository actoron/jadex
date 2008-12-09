package jadex.tools.common.modeltree;

import javax.swing.JComponent;

import jadex.commons.concurrent.IExecutable;

/**
 *  A task to update some aspect of a node.
 */
public abstract class NodeTask implements IExecutable
{
	//-------- attributes --------
	
	/** The node functionality that invoked the task. */
	protected DefaultNodeFunctionality	nof;
	
	/** The node. */
	protected IExplorerTreeNode	node;
	
	/** The task priority (0-1). */
	protected double	priority;
	
	/** The status text (if any). */
	protected String	statustext;
	
	/** The status component (if any). */
	protected JComponent	statuscomp;
	
	//-------- constructors --------
	
	/**
	 *  Create a node task.
	 */
	public NodeTask(DefaultNodeFunctionality nof, IExplorerTreeNode node,
		double priority, String statustext, JComponent statuscomp)
	{
		this.nof	= nof;
		this.node	= node;
		this.priority	= priority;
		this.statustext	= statustext;
		this.statuscomp	= statuscomp;
	}
	
	//-------- methods --------
	
	/**
	 *  Perform the task.
	 */
	public abstract void	performTask();
	
	/**
	 *  Get the priority.
	 */
	public double	getPriority()
	{
		return priority;
	}
	
	/**
	 *  Get the status component.
	 */
	public JComponent	getStatusComponent()
	{
		return statuscomp;
	}
	
	//-------- IExecutable interface --------
	
	/**
	 *  Execute the task.
	 */
	public boolean execute()
	{
		// Perform task only, when node still in tree.
		if(nof.isValidChild(node))
		{
			if(statustext!=null)
			{
				String	tip	= node.getToolTipText();
				if(tip!=null)
					nof.getJCC().setStatusText(statustext+tip);
			}

			try
			{
				performTask();
			}
			catch(RuntimeException e)
			{
				e.printStackTrace();
			}
		}
		nof.nodeTaskFinished(this);
		return false;
	}
}
