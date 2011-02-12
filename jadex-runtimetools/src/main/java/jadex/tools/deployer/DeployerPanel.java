package jadex.tools.deployer;

import jadex.base.gui.asynctree.INodeHandler;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.plugin.IControlCenter;
import jadex.base.service.deployment.FileData;
import jadex.base.service.deployment.IDeploymentService;
import jadex.commons.future.SwingDefaultResultListener;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.tree.TreePath;

/**
 *  Panel for showing a file transfer view composed of two
 *  panels with a file tree.
 */
public class DeployerPanel extends JPanel
{
	//-------- attributes --------
	
	/** The control center. */
	protected IControlCenter jcc;
	
	//-------- constructors --------
	
	/**
	 *  Create a new deloyer panel.
	 */
	public DeployerPanel(final IControlCenter jcc)
	{
		this.jcc = jcc;
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		setLayout(new BorderLayout());
		add(split, BorderLayout.CENTER);

		// Local view on the left
		DeployerNodeHandler nh1 = new DeployerNodeHandler();
		DeployerNodeHandler nh2 = new DeployerNodeHandler();
		DeployerServiceSelectorPanel p1 = new DeployerServiceSelectorPanel(jcc.getExternalAccess(), IDeploymentService.class, nh1);
		DeployerServiceSelectorPanel p2 = new DeployerServiceSelectorPanel(jcc.getExternalAccess(), IDeploymentService.class, nh2);
		nh1.setFirstPanel(p1);
		nh1.setSecondPanel(p2);
		nh2.setFirstPanel(p2);
		nh2.setSecondPanel(p1);
		
		split.add(p1);
		split.add(p2);
		
		split.setOneTouchExpandable(true);
		split.setDividerLocation(0.5);
	}
	
	//-------- helper classes --------
	
	/**
	 *  The deployer node handler that combines
	 *  both file trees via commands.
	 */
	class DeployerNodeHandler implements INodeHandler
	{
		//-------- attributes --------
		
		/** The first panel. */
		protected DeployerServiceSelectorPanel first;
		
		/** The second panel. */
		protected DeployerServiceSelectorPanel second;

		AbstractAction copy = new AbstractAction("Copy file")
		{
			public void actionPerformed(ActionEvent e)
			{
				if(first!=null && second!=null)
				{
					final String sel_1 = first.getSelectedPath();
					final String sel_2 = second.getSelectedPath();
					IDeploymentService ds = second.getDeploymentService();
					if(sel_1!=null && sel_2!=null)
					{
						File source = new File(sel_1);
						ds.putFile(new FileData(source), sel_2)
							.addResultListener(new SwingDefaultResultListener()
						{
							public void customResultAvailable(Object result)
							{
								second.refreshTreePaths(null);
								jcc.setStatusText("Copied: "+sel_1+" to: "+sel_2);
							}
							
							public void customExceptionOccurred(Exception exception)
							{
								jcc.setStatusText("Copy error: "+sel_1+" to: "+sel_2+" exception: "+exception);
							}
						});
					}
				}
			}
			
			public boolean isEnabled()
			{
				return first!=null && second!=null && first.getSelectedPath()!=null && second.getSelectedPath()!=null;
			}
		};
		
		AbstractAction del = new AbstractAction("Delete file")
		{
			public void actionPerformed(ActionEvent e)
			{
				if(first!=null)
				{
					final TreePath tp = first.getSelectedTreePath();
					final String sel = first.getSelectedPath();
					if(sel!=null)
					{
						IDeploymentService ds = first.getDeploymentService();
						ds.deleteFile(sel).addResultListener(new SwingDefaultResultListener()
						{
							public void customResultAvailable(Object result)
							{
								first.refreshTreePaths(new TreePath[]{tp.getParentPath()});
								jcc.setStatusText("Deleted: "+sel);
							}
							public void customExceptionOccurred(Exception exception) 
							{
								jcc.setStatusText("Could not delete: "+sel);
							}
						});
					}
				}
			}
			
			public boolean isEnabled()
			{
				return first!=null && first.getSelectedPath()!=null;
			}
		};
	
		//-------- methods --------

		/**
		 *  Get the overlay for a node if any.
		 */
		public Icon	getOverlay(ITreeNode node)
		{
			return null;
		}

		/**
		 *  Get the popup actions available for all of the given nodes, if any.
		 */
		public Action[]	getPopupActions(ITreeNode[] nodes)
		{
			return new Action[]{copy, del};
		}

		/**
		 *  Get the default action to be performed after a double click.
		 */
		public Action getDefaultAction(ITreeNode node)
		{
			return null;
		}
		
		/**
		 *  Set the first panel.
		 */
		public void setFirstPanel(DeployerServiceSelectorPanel first)
		{
			 this.first = first;
		}
		
		/**
		 *  Set the first panel.
		 */
		public void setSecondPanel(DeployerServiceSelectorPanel second)
		{
			 this.second = second;
		}
	}
}
