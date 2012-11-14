package jadex.bpmn.editor.model.visual;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.model.io.SBpmnModelWriter;
import jadex.bpmn.model.io.IBpmnVisualModelWriter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

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
			out.println("\">");
			
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
			
			//FIXME: Necessary?
//			out.print(SBpmnModelWriter.getIndent(4));
//			out.println("<bpmndi:BPMNLabel/>");
			
			out.print(SBpmnModelWriter.getIndent(3));
			out.println("</bpmndi:BPMNShape>");
		}
		
		for (VEdge edge : edges)
		{
			out.print(SBpmnModelWriter.getIndent(3));
			out.print("<bpmndi:BPMNEdge bpmnElement=\"");
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
			out.println("</bpmndi:BPMNEdge>");
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
			nodes.add((VNode) parent);
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
