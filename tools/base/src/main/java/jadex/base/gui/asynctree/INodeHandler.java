package jadex.base.gui.asynctree;

/**
 * Node handlers provide additional information for nodes such as icon overlays
 * and popup actions.
 */
public interface INodeHandler
{
	/**
	 * Get the overlay for a node if any.
	 */
	public byte[] getOverlay(ITreeNode node);

}
