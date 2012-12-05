package jadex.bpmn.editor.model.visual;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MIdElement;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.model.io.IBpmnVisualModelReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;

/**
 *  Reader for the visual BPMN model.
 *
 */
public class BpmnVisualModelReader implements IBpmnVisualModelReader
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
		if ("Bounds".equals(tag.getLocalPart()))
		{
			if (buffer.containsKey("bounds"))
			{
				mxRectangle alt = new mxRectangle();
				alt.setWidth(Double.parseDouble(attrs.get("width")));
				alt.setHeight(Double.parseDouble(attrs.get("height")));
				alt.setX(Double.parseDouble(attrs.get("x")));
				alt.setY(Double.parseDouble(attrs.get("y")));
				mxGeometry geo = (mxGeometry) buffer.get("bounds");
				geo.setAlternateBounds(alt);
			}
			else
			{
				mxGeometry geo = new mxGeometry();
				geo.setWidth(Double.parseDouble(attrs.get("width")));
				geo.setHeight(Double.parseDouble(attrs.get("height")));
				geo.setX(Double.parseDouble(attrs.get("x")));
				geo.setY(Double.parseDouble(attrs.get("y")));
				buffer.put("bounds", geo);
			}
		}
		else if ("BPMNShape".equals(tag.getLocalPart()))
		{
			String bpmnid = attrs.get("bpmnElement");
			MIdElement e = emap.get(bpmnid);
			VNamedNode vnode = null;
			
			if (e instanceof MSubProcess)
			{
				if (((MSubProcess) e).hasPropertyValue("file"))
				{
					vnode = new VExternalSubProcess(graph);
				}
				else
				{
					vnode = new VSubProcess(graph);
				}
			}
			else if (e instanceof MActivity)
			{
				vnode = new VActivity(graph);
			}
			else if (e instanceof MPool)
			{
				vnode = new VPool(graph);
			}
			else if (e instanceof MLane)
			{
				vnode = new VLane(graph);
			}
			
			if (vnode == null)
			{
				System.err.println("Unknown Element ID: " + bpmnid);
				return;
			}
			
			String exp = attrs.get("isExpanded");
			if (exp != null)
			{
				vnode.setCollapsed(!Boolean.parseBoolean(exp));
			}
			
			mxGeometry geo = (mxGeometry) buffer.remove("bounds");
			if (geo != null)
			{
				vnode.setGeometry(geo);
			}
			
			if (e instanceof MActivity)
			{
				MActivity act = (MActivity) e;
				VNode parent = null;
				
				if (act.isEventHandler())
				{
					Map<String, String> ehpm = (Map<String, String>) buffer.get("eventhandlerparentmap");
					parent = (VNode) vmap.get(ehpm.get(act.getId()));
				}
				
				if (parent == null)
				{
					Map<String, MSubProcess> spem = (Map<String, MSubProcess>) buffer.get("subprocesselementmap");
					if (spem.containsKey(act.getId()))
					{
						parent = (VNode) vmap.get(spem.get(act.getId()).getId());
					}
				}
				
				if (parent == null)
				{
					parent = act.getLane() != null? (VNode) vmap.get(act.getLane().getId()) : (VNode) vmap.get(act.getPool().getId());
				}
				
				if (parent != null)
				{
					graph.getModel().beginUpdate();
					graph.addCell(vnode, parent);
					graph.getModel().endUpdate();
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
				graph.getModel().beginUpdate();
				graph.addCell(vnode);
				graph.getModel().endUpdate();
				
				List<VNode> children = childmap.remove(e.getId());
				if (children != null)
				{
					for (VNode child : children)
					{
						graph.getModel().beginUpdate();
						graph.addCell(child, vnode);
						graph.getModel().endUpdate();
					}
				}
				vnode.setBpmnElement(e);
			}
			else if (e instanceof MLane)
			{
				VPool parent = (VPool) vmap.get(laneparents.get(e.getId()));
				
				if (parent != null)
				{
					graph.getModel().beginUpdate();
					graph.addCell(vnode, parent);
					graph.getModel().endUpdate();
				}
				else
				{
					List<VNode> children = childmap.get(laneparents.get(e.getId()));
					if (children == null)
					{
						children = new ArrayList<VNode>();
						childmap.put(laneparents.get(e.getId()), children);
					}
					children.add(vnode);
				}
				vnode.setBpmnElement(e);
			}
			
			vmap.put(bpmnid, vnode);
		}
		else if ("BPMNEdge".equals(tag.getLocalPart()))
		{
			String bpmnid = attrs.get("bpmnElement");
			MIdElement medge = emap.get(bpmnid);
			
			VEdge vedge = null;
			if (medge instanceof MSequenceEdge)
			{
				MSequenceEdge mseqedge = (MSequenceEdge) medge;
				vedge = new VSequenceEdge(graph, VSequenceEdge.class.getSimpleName());
				vedge.setSource(vmap.get(mseqedge.getSource().getId()));
				vedge.setTarget(vmap.get(mseqedge.getTarget().getId()));
			}
			vedge.setBpmnElement(medge);
			
			List<mxPoint> waypoints = (List<mxPoint>) buffer.remove("waypoints");
			if (waypoints != null)
			{
				mxGeometry geo = vedge.getGeometry() != null? vedge.getGeometry() : new mxGeometry();
				geo.setPoints(waypoints);
				geo.setRelative(false);
				vedge.setGeometry(geo);
			}
			
			graph.getModel().beginUpdate();
			graph.addCell(vedge, vedge.getSource().getParent());
			graph.getModel().endUpdate();
			
		}
		else if ("waypoint".equals(tag.getLocalPart()))
		{
			List<mxPoint> waypoints = (List<mxPoint>) buffer.get("waypoints");
			if (waypoints == null)
			{
				waypoints = new ArrayList<mxPoint>();
				buffer.put("waypoints", waypoints);
			}
			
			mxPoint point = new mxPoint();
			point.setX(Double.parseDouble(attrs.get("x")));
			point.setY(Double.parseDouble(attrs.get("y")));
			waypoints.add(point);
		}
		
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
}
