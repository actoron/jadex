package jadex.android.controlcenter.componentViewer.tree;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.IComponentDescription;

/**
 *  Interface for all active component tree nodes.
 */
public interface IActiveComponentTreeNode extends IAndroidTreeNode
{
	/**
	 *  Get the component description.
	 */
	public IComponentDescription	getDescription();
	
//	/**
//	 *  Get the component id.
//	 */
//	public IComponentIdentifier getId();
}
