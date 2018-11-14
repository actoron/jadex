package jadex.gpmn.editor.model.visual;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

import jadex.gpmn.editor.gui.GuiConstants;
import jadex.gpmn.editor.model.gpmn.IActivationEdge;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.IEdge;
import jadex.gpmn.editor.model.gpmn.IElement;
import jadex.gpmn.editor.model.gpmn.IPlanEdge;
import jadex.gpmn.editor.model.gpmn.ISuppressionEdge;

/**
 *  Visual edge.
 *
 */
public class VEdge extends VElement
{
	/**
	 *  Creates a new visual representation of a node.
	 *  
	 *  @param iedge The underlying node this visual represents.
	 *  @param style The style.
	 */
	public VEdge(VElement source, VElement target, IEdge iedge)
	{
		super(iedge, null, null);
		setValue(iedge);
		mxGeometry geo = new mxGeometry();
		geo.setRelative(true);
		setGeometry(geo);
		
		super.setSource(source);
		super.setTarget(target);
		setEdge(true);
		setVisible(true);
	}
	
	/**
	 *  Returns the business model element.
	 *  @return The business model element.
	 */
	public IElement getElement()
	{
		return getEdge();
	}
	
	/**
	 *  Returns the style.
	 */
	public String getStyle()
	{
		String ret = super.getStyle();
		if (getSource() instanceof VPlan && ((VPlan) getSource()).getPlan() instanceof IActivationPlan)
		{
			ret = ret + ((IActivationPlan) ((VPlan) getSource()).getPlan()).getMode();
		}
		return ret;
	}
	
	/**
	 *  Get the business model edge.
	 *  @return The edge.
	 */
	public IEdge getEdge()
	{
		return (IEdge) super.getValue();
	}
	
	public void setSource(mxICell source)
	{
		if (source == null)
		{
			return;
		}
		
		IElement nelem = null;
		if (source instanceof VNode)
		{
			nelem = ((VNode) source).getNode();
		}
		else if (source instanceof VEdge)
		{
			nelem = ((VEdge) source).getEdge();
		}
		
		if (getEdge() != null)
		{
			getEdge().setSource(nelem);
		}
		
		super.setSource(source);
	}
	
	public void setTarget(mxICell target)
	{
		if (target == null)
		{
			return;
		}
		
		IElement nelem = null;
		if (target instanceof VNode)
		{
			nelem = ((VNode) target).getNode();
		}
		else if (target instanceof VEdge)
		{
			nelem = ((VEdge) target).getEdge();
		}
		
		if (getEdge() != null)
		{
			getEdge().setTarget(nelem);
		}
		
		super.setTarget(target);
	}
	
	/**
	 *  Sets the value.
	 *  @param value The value.
	 */
	public void setValue(Object value)
	{
		if (value instanceof String && getEdge() instanceof IActivationEdge)
		{
			try
			{
				int neworder = Integer.parseInt((String) value);
				((IActivationEdge) getEdge()).setOrder(neworder);
			}
			catch (NumberFormatException e)
			{
			}
		}
		else if (value instanceof IEdge)
		{
			IEdge iedge = (IEdge) value;
			String style = null;
			if (iedge instanceof IPlanEdge)
			{
				style = GuiConstants.PLAN_EDGE_STYLE;
			}
			else if (iedge instanceof IActivationEdge)
			{
				style = GuiConstants.ACTIVATION_EDGE_STYLE;
			}
			else if (iedge instanceof ISuppressionEdge)
			{
				style = GuiConstants.SUPPRESSION_EDGE_STYLE;
			}
			setStyle(style);
			super.setValue(value);
		}
		else
		{
			super.setValue(value);
		}
	}
	
	/**
	 *  Gets the value.
	 *  @return The value.
	 */
	public Object getValue()
	{
		if (super.getValue() instanceof IActivationEdge)
		{
			return ((IActivationEdge) getEdge()).getOrder();
		}
		else
		{
			return super.getValue();
		}
	}
	
	public void setSourceValue(mxICell source)
	{
		super.setSource(source);
	}
	
	public void setTargetValue(mxICell target)
	{
		super.setTarget(target);
	}
}
