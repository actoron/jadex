package jadex.base.gui.modeltree;

import javax.swing.JTree;
import javax.swing.SwingUtilities;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IMultiKernelListener;

/**
 *  Listener for Kernel Updates.
 */
public class TreePanelKernelListener implements IMultiKernelListener
{
	/** Refresh Operation */
	protected Runnable refresh;
	
	/** Listener ID */
	protected String id;
	
	/**
	 *  Creates a new Panel Kernel Listener.
	 *  @param id ID of the listener.
	 *  @param tree The GUI tree.
	 *  @param mffmic The file filter.
	 */
	public TreePanelKernelListener(String id, final JTree tree, final ModelFileFilterMenuItemConstructor mffmic)
	{
		this.id = id;
		refresh = new Runnable()
		{
			public void run()
			{
//				mffmic.getSupportedComponentTypes().addResultListener(new SwingDefaultResultListener()
//				{
//					public void customResultAvailable(Object result)
//					{
						((ITreeNode)tree.getModel().getRoot()).refresh(true);
//					}
//				});
			}
		};
	}
	
	/**
	 *  Called when new component types become available.
	 *  @param types Added component types.
	 */
	public void componentTypesRemoved(String[] types)
	{
//		System.out.println("types removed: "+SUtil.arrayToString(types));
		SwingUtilities.invokeLater(refresh);
	}
	
	/**
	 *  Called when component types become unavailable.
	 *  @param types Removed component types.
	 */
	public void componentTypesAdded(String[] types)
	{
//		System.out.println("types added: "+SUtil.arrayToString(types));
		SwingUtilities.invokeLater(refresh);
	}
	
	/**
	 *  Gets the ID.
	 *  @return The ID.
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 *  Generates a hash code.
	 *  @return A hash code.
	 */
	public int hashCode()
	{
		return id.hashCode();
	}
	
	/**
	 *  Returns whether an object is equal.
	 *  @return True, if the object is equal.
	 */
	public boolean equals(Object obj)
	{
		return (obj instanceof TreePanelKernelListener && ((TreePanelKernelListener) obj).getId().equals(id));
	}
}
