package jadex.gpmn.editor.model.visual;

import jadex.gpmn.editor.model.gpmn.IElement;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

public abstract class VElement extends mxCell
{
	public VElement(Object value, mxGeometry geometry, String style)
	{
		super(value, geometry, style);
	}
	
	/**
	 *  Returns the business model element.
	 *  @return The business model element.
	 */
	public abstract IElement getElement();
	
	/**
	 *  Tests if this element can be folded (style sheet must allow folding).
	 *  
	 *  @return True, if folding is allowed.
	 */
	public boolean isFoldable()
	{
		return false;
	}
}
