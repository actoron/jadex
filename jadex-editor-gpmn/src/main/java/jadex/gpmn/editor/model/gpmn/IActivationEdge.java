package jadex.gpmn.editor.model.gpmn;

public interface IActivationEdge extends IEdge
{
	/**
	 *  Gets the order for sequential activation.
	 *
	 *  @return The order.
	 */
	public int getOrder();
	
	/**
	 *  Sets the order for sequential activation.
	 *
	 *  @param order The order.
	 */
	public void setOrder(int order);
}
