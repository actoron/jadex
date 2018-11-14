package jadex.bpmn.editor.model.visual;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.editor.gui.SHelper;
import jadex.bpmn.editor.gui.controllers.GraphOperationsController;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MDataEdge;
import jadex.bpmn.model.MIdElement;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MMessagingEdge;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.model.io.IPostProcessingVisualModelReader;

/**
 *  Reader for the visual BPMN model.
 *
 */
public class BpmnVisualModelReader implements IPostProcessingVisualModelReader
{
	/** The visual BPMN graph. */
	protected BpmnGraph graph;
	
	/** The visual elements. */
	protected Map<String, VElement> vmap;
	
	/** Map of unconnected children */
	protected Map<String, List<VNode>> childmap;
	
	/**
	 *  Creates a visual model reader.
	 *  
	 *  @param graph The visual graph.
	 */
	public BpmnVisualModelReader(BpmnGraph graph)
	{
		this.graph = graph;
		this.vmap = new HashMap<String, VElement>();
		childmap = new HashMap<String, List<VNode>>();
	}
	
	/**
	 *  Reads a visual element.
	 *  
	 *  @param tag The XML tag.
	 *  @param attrs The attributes.
	 *  @param laneparents The parents of lanes.
	 *  @param emap Map of BPMN elements by ID.
	 *  @param buffer Buffer map.
	 */
	public void readElement(QName tag, Map<String, String> attrs, Map<String, String> laneparents, Map<String, MIdElement> emap, Map<String, Object> buffer)
	{
		/*if (ve != null)
		{
			graph.getView().clear(ve, true, false);
			graph.getView().invalidate(ve);
			//Object[] selcells = graph.getSelectionModel().getCells();
			//graph.getSelectionModel().removeCells(selcells);
			graph.getView().validate();
			//graph.setSelectionCells(selcells);
		}*/
	}
	
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
	public void processBpmnShape(String bpmnid, MIdElement e, Boolean expanded, Rectangle2D bounds, Rectangle2D altbounds, Set<String> internalparameters, String eventparentid, String subprocessparentid, String laneparentid)
	{
		VNamedNode vnode = null;
		
		if (e instanceof MSubProcess)
		{
			if (((MSubProcess) e).hasProperty("file") ||
				((MSubProcess) e).hasProperty("filename"))
			{
				vnode = createExternalSuboprocess();
			}
			else
			{
				vnode = createSuboprocess();
			}
		}
		else if (e instanceof MActivity)
		{
			vnode = createActivity();
		}
		else if (e instanceof MPool)
		{
			vnode = createPool();
		}
		else if (e instanceof MLane)
		{
			vnode = createLane();
		}
		
		if (internalparameters != null && vnode instanceof VActivity)
		{
			((VActivity)vnode).setInternalParameters(internalparameters);
		}
		
		if (vnode == null)
		{
			Logger.getLogger(BpmnEditor.APP_NAME).log(Level.WARNING, "Visual element found for unknown Object ID " + bpmnid);
		}
		else
		{
			if (expanded != null)
			{
				if (vnode instanceof VSubProcess)
				{
					((VSubProcess) vnode).setPseudoFolded(!expanded);
				}
				else
				{
					vnode.setCollapsed(!expanded);
				}
			}
			
			mxGeometry geo = null;
			if(bounds != null)
			{
				geo = new mxGeometry(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
			}
			
			mxGeometry oldgeo = vnode.getGeometry();
			if(geo != null)
			{
				if (altbounds != null)
				{
					geo.setAlternateBounds(new mxRectangle(altbounds.getX(), altbounds.getY(), altbounds.getWidth(), altbounds.getHeight()));
				}
				vnode.setGeometry(geo);
			}
			
			if(e instanceof MActivity)
			{
				MActivity act = (MActivity) e;
				VNode parent = null;
				
				if (act.isEventHandler())
				{
					// Geometry is handled by layout manager.
					vnode.setGeometry(oldgeo);
					parent = (VNode)vmap.get(eventparentid);
				}
				
				if (parent == null)
				{
					parent = (VNode)vmap.get(subprocessparentid);
				}
				
				if (parent == null)
				{
					parent = act.getLane() != null? (VNode) vmap.get(act.getLane().getId()) : (VNode) vmap.get(act.getPool().getId());
				}
				
				if (parent != null)
				{
//					graph.getModel().beginUpdate();
//					vnode.setParent(parent);
//					cells.add(vnode);
					graph.addCell(vnode, parent);
//					graph.getModel().endUpdate();
				}
				else
				{
					List<VNode> children = childmap.get(act.getPool().getId());
					if (children == null)
					{
						children = new ArrayList<VNode>();
						childmap.put(act.getPool().getId(), children);
					}
					children.add(vnode);
				}
				vnode.setBpmnElement(e);
			}
			else if (e instanceof MPool)
			{
//				graph.getModel().beginUpdate();
				graph.addCell(vnode);
//				cells.add(vnode);
//				graph.getModel().endUpdate();
				
				List<VNode> children = childmap.remove(e.getId());
				if (children != null)
				{
					for (VNode child : children)
					{
//						graph.getModel().beginUpdate();
//						child.setParent(vnode);
						graph.addCell(child, vnode);
//						cells.add(child);
//						graph.getModel().endUpdate();
					}
				}
				vnode.setBpmnElement(e);
			}
			else if (e instanceof MLane)
			{
				VPool parent = (VPool) vmap.get(laneparentid);
				
				if (parent != null)
				{
//					graph.getModel().beginUpdate();
//					vnode.setParent(parent);
//					cells.add(vnode);
					graph.addCell(vnode, parent);
//					graph.getModel().endUpdate();
				}
				else
				{
					List<VNode> children = childmap.get(laneparentid);
					if (children == null)
					{
						children = new ArrayList<VNode>();
						childmap.put(laneparentid, children);
					}
					children.add(vnode);
				}
				vnode.setBpmnElement(e);
			}
			
			vmap.put(bpmnid, vnode);
		}
	}
	
	/**
	 *  Process the visual part of a standard BPMN edge.
	 *  
	 *  @param bpmnid The referenced ID of the edge.
	 *  @param medge The semantic edge, if found.
	 *  @param waypoints The way points of the edge.
	 */
	public void processBpmnEdge(String bpmnid, MIdElement medge, List<Point2D> waypoints)
	{
		if (medge != null)
		{
			VEdge vedge = null;
			if (medge instanceof MSequenceEdge)
			{
				MSequenceEdge mseqedge = (MSequenceEdge) medge;
				vedge = createSequenceEdge();
				vedge.setSource(vmap.get(mseqedge.getSource().getId()));
				vedge.setTarget(vmap.get(mseqedge.getTarget().getId()));
			}
			else if (medge instanceof MMessagingEdge)
			{
				MMessagingEdge mmedge = (MMessagingEdge) medge;
				vedge = createMessagingEdge();
				vedge.setSource(vmap.get(mmedge.getSource().getId()));
				vedge.setTarget(vmap.get(mmedge.getTarget().getId()));
			}
			else
			{
				throw new RuntimeException("Unknown edge found: " + medge.getId());
			}
			vedge.setBpmnElement(medge);
			
			mxGeometry geo = vedge.getGeometry() != null? vedge.getGeometry() : new mxGeometry();
			if (waypoints != null)
			{
				List<mxPoint> mxpoints = new ArrayList<mxPoint>();
				for (Point2D point : waypoints)
				{
					mxpoints.add(new mxPoint(point));
				}
				
				geo.setPoints(mxpoints);
				vedge.setGeometry(geo);
			}
//			graph.getModel().beginUpdate();
//			vedge.setParent(vedge.getEdgeParent());
//			cells.add(vedge);
			if (vedge instanceof VMessagingEdge)
			{
				graph.addCell(vedge, graph.getCurrentRoot());
			}
			else
			{
				graph.addCell(vedge, vedge.getEdgeParent());
			}
//			graph.addCell(vedge);
//			graph.getModel().endUpdate();
		}
		else
		{
			Logger.getLogger(BpmnEditor.APP_NAME).log(Level.WARNING, "Visual element found for unknown BPMN edge ID " + bpmnid);
		}
	}
	
	/**
	 *  Process the visual part of a generic (non-standard) edge.
	 * 	
	 * 	@param type Type of the edge, if found.
	 *	@param waypoints The way points of the edge.
	 * 	@param attrs XML attributes for the edge.
	 * 	@param emap Map from element IDs to semantic elements.
	 */
	public void processGenericEdge(String type, List<Point2D> waypoints, Map<String, String> attrs, Map<String, MIdElement> emap)
	{
		if("data".equals(type))
		{
			String id = attrs.get("jadexElement");
			MDataEdge dedge = (MDataEdge) emap.get(id);
			if(dedge != null)
			{
				VDataEdge vedge = createDataEdge();
				VActivity sact = (VActivity) vmap.get(dedge.getSource().getId());
				VActivity tact = (VActivity) vmap.get(dedge.getTarget().getId());
				if (SHelper.isVisualEvent(sact))
				{
					vedge.setSource(sact);
				}
				else
				{
					vedge.setSource(sact.getOutputParameterPort(dedge.getSourceParameter()));
				}
				if (SHelper.isVisualEvent(tact))
				{
					vedge.setTarget(tact);
				}
				else
				{
					vedge.setTarget(tact.getInputParameterPort(dedge.getTargetParameter()));
				}
				vedge.setBpmnElement(dedge);
				
				if (waypoints != null)
				{
					List<mxPoint> mxpoints = new ArrayList<mxPoint>();
					for (Point2D point : waypoints)
					{
						mxpoints.add(new mxPoint(point));
					}
					
					mxGeometry geo = vedge.getGeometry() != null? vedge.getGeometry() : new mxGeometry();
					geo.setPoints(mxpoints);
					geo.setRelative(false);
					vedge.setGeometry(geo);
				}
				
//				graph.getModel().beginUpdate();
//				graph.addCell(vedge, vedge.getSource().getParent().getParent());
//				graph.addCell(vedge);
//				vedge.setParent(vedge.getEdgeParent());
//				cells.add(vedge);
				
				graph.addCell(vedge, vedge.getEdgeParent());
				
//				graph.getModel().endUpdate();
			}
			else
			{
				Logger.getLogger(BpmnEditor.APP_NAME).log(Level.WARNING, "Visual element found for unknown data edge ID " + id);
			}
			
		}
	}
	
	/**
	 *  Performs the post-process.
	 */
	public void postProcess()
	{
		for (Map.Entry<String, VElement> entry : vmap.entrySet())
		{
			if (entry.getValue() instanceof VSubProcess &&
				((VSubProcess) entry.getValue()).isPseudoFolded())
			{
				GraphOperationsController.pseudoCollapse((VSubProcess) entry.getValue());
			}
		}
	}
	
	/**
	 * 
	 */
	public VExternalSubProcess createExternalSuboprocess()
	{
		return new VExternalSubProcess(graph);
	}
	
	/**
	 * 
	 */
	public VSubProcess createSuboprocess()
	{
		return new VSubProcess(graph);
	}
	
	/**
	 * 
	 */
	public VActivity createActivity()
	{
		return new VActivity(graph);
	}
	
	/**
	 * 
	 */
	public VPool createPool()
	{
		return new VPool(graph);
	}
	
	/**
	 * 
	 */
	public VLane createLane()
	{
		return new VLane(graph);
	}
	
	/**
	 * 
	 */
	public VSequenceEdge createSequenceEdge()
	{
		return new VSequenceEdge(graph);
	}
	
	/**
	 * 
	 */
	public VMessagingEdge createMessagingEdge()
	{
		return new VMessagingEdge(graph);
	}
	
	/**
	 * 
	 */
	public VDataEdge createDataEdge()
	{
		return new VDataEdge(graph);
	}
}
