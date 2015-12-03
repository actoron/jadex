package jadex.gpmn.editor.model.visual;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

import jadex.gpmn.editor.gui.GuiConstants;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.INode;
import jadex.gpmn.editor.model.gpmn.IPlan;
import jadex.gpmn.editor.model.gpmn.IRefPlan;

/**
 *  Visual plan node.
 *
 */
public class VPlan extends VNode implements IPlanModeProvider
{
	/**
	 *  Creates a new plan node.
	 *  
	 *  @param plan Business model plan.
	 *  @param position The position.
	 */
	public VPlan(IPlan plan, mxPoint position)
	{
		this(plan, position.getX(), position.getY());
	}
	
	/**
	 *  Creates a new plan node.
	 *  
	 *  @param plan Business model plan.
	 *  @param x The X-position.
	 *  @param y The Y-position.
	 */
	public VPlan(IPlan plan, double x, double y)
	{
		super(plan, new mxGeometry(x, y,
				 GuiConstants.DEFAULT_PLAN_WIDTH, 
				 GuiConstants.DEFAULT_PLAN_HEIGHT),
				 null);
		
		if (plan instanceof IRefPlan)
		{
			setStyle(GuiConstants.REF_PLAN_STYLE);
		}
		else if (plan instanceof IActivationPlan)
		{
			setStyle(GuiConstants.ACTIVATION_PLAN_STYLE);
			insert(new SequentialMarker(this));
		}
		
		insert(new VPlanType());
	}
	
	/**
	 *  Returns the business model plan.
	 *  @return The plan.
	 */
	public IPlan getPlan()
	{
		return (IPlan) super.getValue();
	}
	
	/**
	 *  Returns the business model node.
	 *  @return The node.
	 */
	public INode getNode()
	{
		return getPlan();
	}
	
	/**
	 *  Sets the value.
	 *  @param value The value.
	 */
	public void setValue(Object value)
	{
		if (value instanceof String)
		{
			getPlan().setName((String) value);
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
		return getPlan().getName();
	}
	
	/**
	 *  Tests if this element is collapsible (stylesheet must allow collapsing).
	 *  
	 *  @return True, if collapsing is allowed.
	 */
	public boolean isFoldable()
	{
		boolean ret = false;
		if (getPlan() instanceof IActivationPlan)
		{
			ret = getPlan().getSourceEdges().size() > 0 && getPlan().getTargetEdges().size() > 0;
		}
		return ret;
	}
	
	/**
	 *  Override to match type marker.
	 */
	public void setGeometry(mxGeometry geometry)
	{
		super.setGeometry(geometry);
		
		SequentialMarker sm = null;
		VPlanType gt = null;
		for (int i = 0; i < getChildCount(); ++i)
		{
			if (getChildAt(i) instanceof VPlanType)
			{
				gt = (VPlanType) getChildAt(i);
			}
			else if (getChildAt(i) instanceof SequentialMarker)
			{
				sm = (SequentialMarker) getChildAt(i);
			}
		}
		
		if (sm != null)
			sm.setGeometry();
		if (gt != null)
			gt.setGeometry();
	}
	
	/**
	 *  Provides the mode of the associated plan.
	 *  @return Sequential, Parallel or null if undetermined.
	 */
	public String getPlanMode()
	{
		String mode = null;
		if (getPlan() instanceof IActivationPlan)
		{
			mode = ((IActivationPlan) getPlan()).getMode();
		}
		return mode;
	}
	
	/**
	 *  Plan type marker.
	 *
	 */
	public class VPlanType extends mxCell
	{
		protected double width = 0.0;
		
		public VPlanType()
		{
			super(null, new mxGeometry(), GuiConstants.PLAN_TYPE_STYLE);
			
			String val = null;
			if (getPlan() instanceof IActivationPlan)
			{
				val = "Activate";
				width = GuiConstants.PLAN_ACTIVATION_MARKER_WIDTH;
			}
			else if (getPlan() instanceof IRefPlan)
			{
				val = "Java";
				width = GuiConstants.PLAN_REF_MARKER_WIDTH;
			}
			
			setValue(val);
			
			setGeometry();
			setConnectable(false);
			setVisible(true);
			setVertex(true);
			
			setId(VPlan.this.getId() + "_Type");
		}
		
		public void setGeometry()
		{
			mxGeometry geo = new mxGeometry(0, 0, width, GuiConstants.PLAN_MARKER_HEIGHT);
			geo.setRelative(true);
			double gw = VPlan.this.getGeometry().getWidth();
			geo.setOffset(new mxPoint((gw - width) / 2, 0));
			setGeometry(geo);
		}
	}
}
