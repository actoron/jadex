package jadex.bpmn.editor.model.visual;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.io.IBpmnVisualModelWriter;
import jadex.bpmn.model.io.SBpmnModelWriter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;

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
	public void writeVisualMode(PrintStream out)
	{
		out.println(SBpmnModelWriter.getIndent(1) + "<bpmndi:BPMNDiagram>");
		out.println(SBpmnModelWriter.getIndent(2) + "<bpmndi:BPMNPlane>");
		
		mxCell parent = (mxCell) vmodel.getDefaultParent();
		List<VNode> nodes = new ArrayList<VNode>();
		List<VEdge> edges = new ArrayList<VEdge>();
		getVisualElements(parent, nodes, edges);
		
		for (VNode node : nodes)
		{
			out.print(SBpmnModelWriter.getIndent(3));
			out.print("<bpmndi:BPMNShape bpmnElement=\"");
			out.print(node.getBpmnElement().getId());
			if (node instanceof VSubProcess)// || node instanceof VExternalSubProcess)
			{
				out.print("\" isExpanded=\"");
				out.print(!node.isCollapsed());
			}
			out.println("\">");
			
			if (!(node instanceof VActivity && ((MActivity) ((VActivity) node).getBpmnElement()).isEventHandler()))
			{
				mxGeometry geo = node.getGeometry();
				out.print(SBpmnModelWriter.getIndent(4));
				out.print("<dc:Bounds height=\"");
				out.print(geo.getHeight());
				out.print("\" width=\"");
				out.print(geo.getWidth());
				out.print("\" x=\"");
				out.print(geo.getX());
				out.print("\" y=\"");
				out.print(geo.getY());
				out.println("\"/>");
			
			
				mxRectangle alt = geo.getAlternateBounds();
				if (alt != null)
				{
					out.print(SBpmnModelWriter.getIndent(4));
					out.print("<dc:Bounds height=\"");
					out.print(alt.getHeight());
					out.print("\" width=\"");
					out.print(alt.getWidth());
					out.print("\" x=\"");
					out.print(alt.getX());
					out.print("\" y=\"");
					out.print(alt.getY());
					out.println("\"/>");
				}
			}
			
			//FIXME: Necessary?
//			out.print(SBpmnModelWriter.getIndent(4));
//			out.println("<bpmndi:BPMNLabel/>");
			
			out.print(SBpmnModelWriter.getIndent(3));
			out.println("</bpmndi:BPMNShape>");
		}
		
		for (VEdge edge : edges)
		{
			String ns = "bpmndi";
			String tagname = "BPMNEdge";
			String refname = "bpmnElement";
			String typestring = "";
			if (edge instanceof VDataEdge)
			{
				ns = "di";
				tagname = "Edge";
				refname = "jadexElement";
				typestring = " type=\"data\"";
			}
			
			out.print(SBpmnModelWriter.getIndent(3));
			out.print("<" + ns + ":" + tagname + typestring + " " + refname + "=\"");
			out.print(edge.getBpmnElement().getId());
			out.println("\">");
			
			mxGeometry geo = edge.getGeometry();
			if (geo != null)
			{
				List<mxPoint> points = edge.getGeometry().getPoints();
				if (points != null)
				{
					for (mxPoint point : points)
					{
						out.print(SBpmnModelWriter.getIndent(4));
						out.print("<di:waypoint x=\"");
						out.print(point.getX());
						out.print("\" y=\"");
						out.print(point.getY());
						out.println("\"/>");
					}
				}
			}
			
			//FIXME: Necessary?
//			out.print(SBpmnModelWriter.getIndent(4));
//			out.println("<bpmndi:BPMNLabel/>");
			
			out.print(SBpmnModelWriter.getIndent(3));
			out.println("</" + ns + ":" + tagname + ">");
		}
		
		out.println(SBpmnModelWriter.getIndent(2) + "</bpmndi:BPMNPlane>");
		out.println(SBpmnModelWriter.getIndent(1) + "</bpmndi:BPMNDiagram>");
	}
	
	/**
	 *  Recursively finds the visual elements.
	 *  
	 *  @param parent The parent node.
	 *  @param nodes List of the nodes.
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
