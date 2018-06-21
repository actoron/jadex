package jadex.gpmn.editor.model.visual;

import com.mxgraph.model.mxGeometry;

import jadex.gpmn.editor.model.gpmn.IElement;
import jadex.gpmn.editor.model.gpmn.INode;
/**
 *  Visual representation of a node.
 *
 */
public abstract class VNode extends VElement
{
	/**
	 *  Creates a new visual representation of a node.
	 *  
	 *  @param gpmnnode The underlying node this visual represents.
	 *  @param geometry Geometry defining position and size.
	 */
	public VNode(Object value, mxGeometry geometry, String style)
	{
		super(value, geometry, style);
		setVertex(true);
		setConnectable(true);
		setVisible(true);
	}

	/**
	 *  Gets the x.
	 *
	 *  @return The x.
	 */
	public double getX()
	{
		return getGeometry().getX();
	}

	/**
	 *  Sets the x.
	 *
	 *  @param x The x.
	 */
	public void setX(double x)
	{
		getGeometry().setX(x);
	}

	/**
	 *  Gets the y.
	 *
	 *  @return The y.
	 */
	public double getY()
	{
		return getGeometry().getY();
	}

	/**
	 *  Sets the y.
	 *
	 *  @param y The y.
	 */
	public void setY(double y)
	{
		getGeometry().setY(y);
	}
	
	/**
	 *  Returns the business model element.
	 *  @return The business model element.
	 */
	public IElement getElement()
	{
		return getNode();
	}
	
	/**
	 *  Returns the business model node.
	 *  @return The node.
	 */
	public abstract INode getNode();
}
