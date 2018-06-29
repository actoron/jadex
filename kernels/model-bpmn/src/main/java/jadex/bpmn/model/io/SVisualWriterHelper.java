package jadex.bpmn.model.io;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintStream;
import java.util.Collection;

public class SVisualWriterHelper
{
	/**
	 *  Writes the start of the visual section.
	 *  @param out The stream.
	 */
	public static final void beginVisualSection(PrintStream out)
	{
		out.println(SBpmnModelWriter.getIndent(1) + "<bpmndi:BPMNDiagram>");
		out.println(SBpmnModelWriter.getIndent(2) + "<bpmndi:BPMNPlane>");
	}
	
	/**
	 *  Writes a visual BPMN shape.
	 * 
	 * 	@param bpmnid ID of the BPMN element.
	 * 	@param bounds Bounds of the shape.
	 * 	@param altbounds Alternate bounds of the shape.
	 * 	@param intparams Parameters marked as internal.
	 *	@param expanded Flag if shape is expanded (can be null if not applicable)
	 * 	@param out The stream.
	 */
	public static final void writeBpmnShape(String bpmnid, Rectangle2D bounds, Rectangle2D altbounds, Collection<String> intparams, Boolean expanded, PrintStream out)
	{
		out.print(SBpmnModelWriter.getIndent(3));
		out.print("<bpmndi:BPMNShape bpmnElement=\"");
		out.print(bpmnid);
		if (expanded != null)// || node instanceof VExternalSubProcess)
		{
			out.print("\" isExpanded=\"");
			out.print(expanded.toString());
		}
		out.println("\">");
		
		if (bounds != null)
		{
			out.print(SBpmnModelWriter.getIndent(4));
			out.print("<dc:Bounds height=\"");
			out.print(bounds.getHeight());
			out.print("\" width=\"");
			out.print(bounds.getWidth());
			out.print("\" x=\"");
			out.print(bounds.getX());
			out.print("\" y=\"");
			out.print(bounds.getY());
			out.println("\"/>");
		}
		
		if (altbounds != null)
		{
			out.print(SBpmnModelWriter.getIndent(4));
			out.print("<dc:Bounds height=\"");
			out.print(altbounds.getHeight());
			out.print("\" width=\"");
			out.print(altbounds.getWidth());
			out.print("\" x=\"");
			out.print(altbounds.getX());
			out.print("\" y=\"");
			out.print(altbounds.getY());
			out.println("\"/>");
		}
		
		if (intparams != null && intparams.size() > 0)
		{
			out.print(SBpmnModelWriter.getIndent(4));
			out.println("<di:extension>");
			for (String intparam : intparams)
			{
				out.print(SBpmnModelWriter.getIndent(5));
				out.print("<jadexvisual:internalParameter>");
				out.print(intparam);
				out.println("</jadexvisual:internalParameter>");
			}
			out.print(SBpmnModelWriter.getIndent(4));
			out.println("</di:extension>");
		}
		
		//FIXME: Necessary?
//		out.print(SBpmnModelWriter.getIndent(4));
//		out.println("<bpmndi:BPMNLabel/>");
		
		out.print(SBpmnModelWriter.getIndent(3));
		out.println("</bpmndi:BPMNShape>");
	}
	
	/**
	 *  Writes a visual edge.
	 * 	
	 * 	@param bpmnid ID of the BPMN edge element.
	 * 	@param dataedge Flag whether this is a data edge.
	 * 	@param points Control points, if any.
	 * 	@param out The stream
	 */
	public static final void writeEdge(String bpmnid, boolean dataedge, Collection<Point2D> points, PrintStream out)
	{
		String ns = "bpmndi";
		String tagname = "BPMNEdge";
		String refname = "bpmnElement";
		String typestring = "";
		if (dataedge)
		{
			ns = "di";
			tagname = "Edge";
			refname = "jadexElement";
			typestring = " type=\"data\"";
		}
		
		out.print(SBpmnModelWriter.getIndent(3));
		out.print("<" + ns + ":" + tagname + typestring + " " + refname + "=\"");
		out.print(bpmnid);
		out.println("\">");
		
		if (points != null)
		{
			for (Point2D point : points)
			{
				out.print(SBpmnModelWriter.getIndent(4));
				out.print("<di:waypoint x=\"");
				out.print(point.getX());
				out.print("\" y=\"");
				out.print(point.getY());
				out.println("\"/>");
			}
		}
		
//		System.out.println(vmodel.getView().getState(edge).getLabelBounds());
		//FIXME: Necessary?
//		out.print(SBpmnModelWriter.getIndent(4));
//		out.println("<bpmndi:BPMNLabel/>");
		
		out.print(SBpmnModelWriter.getIndent(3));
		out.println("</" + ns + ":" + tagname + ">");
	}
	
	/**
	 *  Writes the end of the visual section.
	 *  @param out The stream.
	 */
	public static final void endVisualSection(PrintStream out)
	{
		out.println(SBpmnModelWriter.getIndent(2) + "</bpmndi:BPMNPlane>");
		out.println(SBpmnModelWriter.getIndent(1) + "</bpmndi:BPMNDiagram>");
	}
}
