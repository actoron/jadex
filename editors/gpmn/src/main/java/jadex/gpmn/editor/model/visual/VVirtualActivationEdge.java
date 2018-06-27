package jadex.gpmn.editor.model.visual;

import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

import jadex.gpmn.editor.gui.GuiConstants;
import jadex.gpmn.editor.model.gpmn.IActivationEdge;
import jadex.gpmn.editor.model.gpmn.IEdge;
import jadex.gpmn.editor.model.gpmn.IElement;

/**
 *  A virtual activation edge not directly represent as business model edge.
 *
 */
public class VVirtualActivationEdge extends VEdge
{
	/** The activation plan. */
	protected VPlan plan;
	
	/** The activation edge. */
	protected VEdge edge;
	
	/** Virtual edge group. */
	protected List<VVirtualActivationEdge> edgegroup;
	
	public VVirtualActivationEdge(VElement source, VElement target, List<VVirtualActivationEdge> group, VPlan aplan)
	{
		super(source, target, null);
		
		this.plan = aplan;
		
		this.edgegroup = group;
		
		setStyle();
		
		insert(new VVEdgeMarker());
	}
	
	public void setStyle()
	{
		setStyle(GuiConstants.VIRTUAL_ACTIVATION_EDGE_STYLE + plan.getPlanMode());
	}
	
	public IElement getElement()
	{
		return plan.getPlan();
	}
	
	public IEdge getEdge()
	{
		return null;
	}
	
	/**
	 *  Gets the activation edge.
	 *  @return The activation edge.
	 */
	public VEdge getActivationEdge()
	{
		if (edge == null)
		{
			for (int i = 0; i < plan.getEdgeCount() && this.edge == null; ++i)
			{
				if (((VEdge) plan.getEdgeAt(i)).getTarget().equals(target))
				{
					this.edge = (VEdge) plan.getEdgeAt(i);
				}
			}
		}
		return edge;
	}
	
	/**
	 *  Gets the edge group.
	 *
	 *  @return The edge group.
	 */
	public List<VVirtualActivationEdge> getEdgeGroup()
	{
		return edgegroup;
	}

	/**
	 *  Gets the plan.
	 *
	 *  @return The plan.
	 */
	public VPlan getPlan()
	{
		return plan;
	}
	
	/**
	 *  Sets the plan.
	 *
	 *  @param plan The plan.
	 */
	public void setPlan(VPlan plan)
	{
		this.plan = plan;
	}

	/**
	 *  Override to match type marker.
	 */
	public void setGeometry(mxGeometry geometry)
	{
		super.setGeometry(geometry);
		VVEdgeMarker m = ((VVEdgeMarker) getChildAt(0));
		if (m != null)
			m.setGeometry();
	}
	
	/**
	 *  Sets the value.
	 *  @param value The value.
	 */
	public void setValue(Object value)
	{
		try
		{
			int neworder = Integer.parseInt((String) value);
			((IActivationEdge) getActivationEdge().getEdge()).setOrder(neworder);
		}
		catch (NumberFormatException e)
		{
		}
	}
	
	/**
	 *  Gets the value.
	 *  @return The value.
	 */
	public Object getValue()
	{
		return ((IActivationEdge) getActivationEdge().getEdge()).getOrder();
	}
	
	public class VVEdgeMarker extends mxCell
	{
		public VVEdgeMarker()
		{
			super("A", new mxGeometry(), GuiConstants.VIRTUAL_ACTIVATION_EDGE_MARKER_STYLE);
			
			setGeometry();
			setConnectable(false);
			setVisible(true);
			setVertex(true);
			//setCollapsed(true);
			
			setId(VVirtualActivationEdge.this.getId() + "_Marker");
		}
		
		public void setGeometry()
		{
			mxGeometry geo = new mxGeometry(0, 0, GuiConstants.VAE_MARKER_WIDTH,
												  GuiConstants.VAE_MARKER_HEIGHT);
			geo.setRelative(true);
			mxGeometry pg = VVirtualActivationEdge.this.getGeometry();
			//geo.setX(pg.getWidth() * 0.5 - GuiConstants.VAE_MARKER_WIDTH * 0.5);
			//geo.setY(pg.getHeight() * 0.5 - GuiConstants.VAE_MARKER_HEIGHT * 0.5);
			geo.setOffset(new mxPoint(pg.getWidth() * 0.5 - GuiConstants.VAE_MARKER_WIDTH * 0.5,
									  pg.getHeight() * 0.5 - GuiConstants.VAE_MARKER_HEIGHT * 0.5));
			//mxGeometry geo = new mxGeometry(pg.getCenterX(), pg.getCenterY(), GuiConstants.VAE_MARKER_WIDTH, GuiConstants.VAE_MARKER_HEIGHT);
			//System.out.println(pg.getCenterX());
			setGeometry(geo);
			
		}
	}
}
