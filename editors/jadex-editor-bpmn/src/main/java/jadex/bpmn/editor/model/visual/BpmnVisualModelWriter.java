package jadex.bpmn.editor.model.visual;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.io.IBpmnVisualModelWriter;
import jadex.bpmn.model.io.SVisualWriterHelper;

/**
 *  A writer for the visual part of BPMN models.
 *
 */
public class BpmnVisualModelWriter implements IBpmnVisualModelWriter
{
	/** The visual model. */
	protected BpmnGraph vmodel;
	
	/**
	 *  Creates a visual model writer.
	 *  
	 *  @param vmodel The visual model.
	 */
	public BpmnVisualModelWriter(BpmnGraph vmodel)
	{
		this.vmodel = vmodel;
	}
	
	/**
	 *  Writes the visual model.
	 *  
	 *  @param out The output.
	 *  @param vmodel The visual model.
	 */
	public void writeVisualModel(PrintStream out)
	{
		SVisualWriterHelper.beginVisualSection(out);
		
		mxCell parent = (mxCell) vmodel.getDefaultParent();
		List<VNode> nodes = new ArrayList<VNode>();
		List<VEdge> edges = new ArrayList<VEdge>();
		getVisualElements(parent, nodes, edges);
		
		for (VNode node : nodes)
		{
			String bpmnid = node.getBpmnElement().getId();
			Boolean expanded = null;
			if (node instanceof VSubProcess)// || node instanceof VExternalSubProcess)
			{
				expanded = !((VSubProcess) node).isPseudoFolded();
			}
			
			Rectangle2D bounds = null;
			Rectangle2D altbounds = null;
			if (!(node instanceof VActivity && ((MActivity) ((VActivity) node).getBpmnElement()).isEventHandler()))
			{
				mxGeometry geo = node.getGeometry();
				bounds = new Rectangle2D.Double(geo.getX(), geo.getY(), geo.getWidth(), geo.getHeight());
				
				mxRectangle alt = geo.getAlternateBounds();
				if (alt != null)
				{
					altbounds = new Rectangle2D.Double(alt.getX(), alt.getY(), alt.getWidth(), alt.getHeight());
				}
			}
			
			Set<String> intparams = null;
			if (node instanceof VActivity)
			{
				intparams = ((VActivity) node).getInternalParameters();
			}
			
			SVisualWriterHelper.writeBpmnShape(bpmnid, bounds, altbounds, intparams, expanded, out);
		}
		
		for (VEdge edge : edges)
		{
			boolean dataedge = false;
			if (edge instanceof VDataEdge)
			{
				dataedge = true;
			}
			
			String bpmnid = edge.getBpmnElement().getId();
			
			List<Point2D> points = null;
			mxGeometry geo = edge.getGeometry();
			if (geo != null)
			{
				List<mxPoint> mxpoints = edge.getGeometry().getPoints();
				if (mxpoints != null)
				{
					for (mxPoint mxpoint : mxpoints)
					{
						if (points == null)
						{
							points = new ArrayList<Point2D>();
						}
						Point2D point = new Point2D.Double(mxpoint.getX(), mxpoint.getY());
						points.add(point);
					}
				}
			}
			
			SVisualWriterHelper.writeEdge(bpmnid, dataedge, points, out);
		}
		
		SVisualWriterHelper.endVisualSection(out);
	}
	
	/**
	 *  Recursively finds the visual elements.
	 *  
	 *  @param parent The parent node.
	 *  @param nodes List of thre nodes.
	 *  @param edges List of the edges.
	 */
	protected static final void getVisualElements(mxCell parent, List<VNode> nodes, List<VEdge> edges)
	{
		if (parent instanceof VNode)
		{
			if (!(parent instanceof VInParameter) &&
				!(parent instanceof VOutParameter))
			{
				nodes.add((VNode) parent);
			}
		}
		else if (parent instanceof VEdge)
		{
			edges.add((VEdge) parent);
		}
		
		for (int i = 0; i < parent.getChildCount(); ++i)
		{
			getVisualElements((mxCell) parent.getChildAt(i), nodes, edges);
		}
	}
}
