package jadex.bpmn.model.io;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bpmn.model.MIdElement;

/**
 *  Interface for writer of the visual part of BPMN models.
 *
 */
public interface IBpmnVisualModelReader
{
	/**
	 *  Reads a visual element.
	 *  
	 *  @param tag The XML tag.
	 *  @param attrs The attributes.
	 *  @param laneparents The parents of lanes.
	 *  @param emap Map of BPMN elements by ID.
	 *  @param buffer Buffer map.
	 */
//	public abstract void readElement(QName tag,
//									 Map<String, String> attrs,
//									 Map<String, String> laneparents,
//									 Map<String, MIdElement> emap,
//									 Map<String, Object> buffer);
	
	/**
	 *  Process the visual part of a standard BPMN shape.
	 * 
	 * 	@param bpmnid The referenced ID of the shape.
	 * 	@param e The semantic shape, if found, null otherwise.
	 *  @param expanded Flag whether the shape should be collapsed (false), expanded (true) or default (null).
	 * 	@param bounds Bounds of the shape.
	 * 	@param altbounds Alternative bounds.
	 *  @param internalparameters Parameters that are considered to be internal and should not have input connectors.
	 * 	@param eventparentid The parent ID if the shape is an event with a parent.
	 * 	@param subprocessparentid The parent subprocess ID if the shape is part of a subprocess.
	 * 	@param laneparentid ID if the parent if the shape is a lane.
	 */
	public void processBpmnShape(String bpmnid, MIdElement e, Boolean expanded, Rectangle2D bounds, Rectangle2D altbounds,  Set<String> internalparameters, String eventparentid, String subprocessparentid, String laneparentid);

	/**
	 *  Process the visual part of a standard BPMN edge.
	 *  
	 *  @param bpmnid The referenced ID of the edge.
	 *  @param medge The semantic edge, if found.
	 *  @param waypoints The way points of the edge.
	 */
	public void processBpmnEdge(String bpmnid, MIdElement medge, List<Point2D> waypoints);
	
	/**
	 *  Process the visual part of a generic (non-standard) edge.
	 * 	
	 * 	@param type Type of the edge, if found.
	 *	@param waypoints The way points of the edge.
	 * 	@param attrs XML attributes for the edge.
	 * 	@param emap Map from element IDs to semantic elements.
	 */
	public void processGenericEdge(String type, List<Point2D> waypoints, Map<String, String> attrs, Map<String, MIdElement> emap);
}
