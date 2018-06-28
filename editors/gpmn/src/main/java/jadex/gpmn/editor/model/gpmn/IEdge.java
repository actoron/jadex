package jadex.gpmn.editor.model.gpmn;


/**
 *  An edge in the model.
 *
 */
public interface IEdge extends IElement
{
	/**
	 *  Gets the source.
	 *
	 *  @return The source
	 */
	public IElement getSource();

	/**
	 *  Sets the source.
	 *
	 *  @param source The source to set
	 */
	public void setSource(IElement source);

	/**
	 *  Gets the target.
	 *
	 *  @return The target
	 */
	public IElement getTarget();

	/**
	 *  Sets the target.
	 *
	 *  @param target The target to set
	 */
	public void setTarget(IElement target);
}
