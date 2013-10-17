package jadex.bpmn.editor.model.visual;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

/**
 *  Generator for auto-generating the visual model of a BPMN.
 *
 */
public class BpmnVisualModelGenerator
{
	protected MBpmnModel mmodel;
	
	public BpmnVisualModelGenerator(MBpmnModel mmodel)
	{
		this.mmodel = mmodel;
	}
	
	public void generateModel(BpmnGraph graph)
	{
		List<MPool> pools = mmodel.getPools();
		
		Map<String, mxICell> elements = new HashMap<String, mxICell>();
		List<MSequenceEdge> seqedges = new ArrayList<MSequenceEdge>();
		
		for (MPool mpool : pools)
		{
			VPool vpool = new VPool(graph);
			vpool.setGeometry(new mxGeometry(0, 0, BpmnStylesheetColor.DEFAULT_POOL_WIDTH, BpmnStylesheetColor.DEFAULT_POOL_HEIGHT));
			graph.getModel().beginUpdate();
			graph.addCell(vpool);
			graph.getModel().endUpdate();
			vpool.setBpmnElement(mpool);
			
			List<MActivity> activities = mpool.getActivities();
			if (activities != null && activities.size() > 0)
			{
				for (MActivity mactivity : activities)
				{
//					VActivity vactivity = new VActivity(graph);
//					graph.getModel().beginUpdate();
//					graph.addCell(vactivity, vpool);
//					graph.getModel().endUpdate();
//					vactivity.setBpmnElement(mactivity);
//					setActivityGeometry(vactivity);
//					elements.put(mactivity.getId(), vactivity);
					if (mactivity.getOutgoingSequenceEdges() != null)
					{
						seqedges.addAll(mactivity.getOutgoingSequenceEdges());
					}
					elements.put(mactivity.getId(), generateActivity(graph, mactivity, vpool, elements, seqedges));
				}
			}
			
			List<MLane> lanes = mpool.getLanes();
			if (lanes != null && lanes.size() > 0)
			{
				for (MLane mlane : lanes)
				{
					VLane vlane = new VLane(graph);
					graph.getModel().beginUpdate();
					graph.addCell(vlane, vpool);
					graph.getModel().endUpdate();
					vlane.setBpmnElement(mlane);
					
					activities = mlane.getActivities();
					if (activities != null && activities.size() > 0)
					{
						for (MActivity mactivity : activities)
						{
//							VActivity vactivity = new VActivity(graph);
//							graph.getModel().beginUpdate();
//							graph.addCell(vactivity, vpool);
//							graph.getModel().endUpdate();
//							vactivity.setBpmnElement(mactivity);
//							elements.put(mactivity.getId(), vactivity);
//							if (mactivity.getOutgoingSequenceEdges() != null)
//							{
//								seqedges.addAll(mactivity.getOutgoingSequenceEdges());
//							}
							elements.put(mactivity.getId(), generateActivity(graph, mactivity, vlane, elements, seqedges));
						}
					}
				}
			}
			
//			List<MSequenceEdge> seqedges = mpool.getSequenceEdges();
			
		}
		
		if (seqedges != null && seqedges.size() > 0)
		{
			for (MSequenceEdge medge : seqedges)
			{
				VSequenceEdge vedge = new VSequenceEdge(graph, VSequenceEdge.class.getSimpleName());
				vedge.setSource(elements.get(medge.getSource().getId()));
				vedge.setTarget(elements.get(medge.getTarget().getId()));
				vedge.setBpmnElement(medge);
				graph.getModel().beginUpdate();
//				graph.addCell(vedge, elements.get(medge.getSource().getId()).getParent());
				graph.addCell(vedge, vedge.getEdgeParent());
				graph.getModel().endUpdate();
			}
		}
	}
	
	protected VActivity generateActivity(BpmnGraph graph, MActivity mactivity, mxICell parent, Map<String, mxICell> elements, List<MSequenceEdge> sseqedges)
	{
		VActivity vactivity = null;
		if (mactivity instanceof MSubProcess)
		{
			if (mactivity.hasProperty("file") ||
				mactivity.hasProperty("filename"))
			{
				vactivity = new VExternalSubProcess(graph);
				
				graph.getModel().beginUpdate();
				graph.addCell(vactivity, parent);
				graph.getModel().endUpdate();
				
				vactivity.setBpmnElement(mactivity);
			}
			else
			{
				vactivity = new VSubProcess(graph);
				
				graph.getModel().beginUpdate();
				graph.addCell(vactivity, parent);
				graph.getModel().endUpdate();
				
				vactivity.setBpmnElement(mactivity);
				
				List<MActivity> activities = ((MSubProcess) mactivity).getActivities();
				if (activities != null && activities.size() > 0)
				{
					for (MActivity activity : activities)
					{
						if (activity.getOutgoingSequenceEdges() != null)
						{
							sseqedges.addAll(activity.getOutgoingSequenceEdges());
						}
						elements.put(activity.getId(), generateActivity(graph, activity, vactivity, elements, sseqedges));
					}
				}
			}
		}
		else
		{
			vactivity = new VActivity(graph);
			
			graph.getModel().beginUpdate();
			graph.addCell(vactivity, parent);
			graph.getModel().endUpdate();
			
			vactivity.setBpmnElement(mactivity);
			
			if (mactivity.getOutgoingSequenceEdges() != null)
			{
				sseqedges.addAll(mactivity.getOutgoingSequenceEdges());
			}
		}
		
		List<MActivity> evthandlers = mactivity.getEventHandlers();
		if (evthandlers != null && evthandlers.size() > 0)
		{
			for (MActivity evthandler : evthandlers)
			{
//				if (evthandler.getOutgoingSequenceEdges() != null && sseqedges != null)
//				{
//					sseqedges.addAll(evthandler.getOutgoingSequenceEdges());
//				}
				elements.put(evthandler.getId(), generateActivity(graph, evthandler, vactivity, elements, sseqedges));
			}
		}
		
		setActivityGeometry(vactivity);
		
		return vactivity;
	}
	
	public void setActivityGeometry(VActivity vactivity)
	{
		MActivity mactivity = (MActivity) vactivity.getBpmnElement();
		Dimension ds = BpmnStylesheetColor.DEFAULT_ACTIVITY_SIZES.containsKey(mactivity.getActivityType()) ?
				   BpmnStylesheetColor.DEFAULT_ACTIVITY_SIZES.get(mactivity.getActivityType()) :
				   BpmnStylesheetColor.DEFAULT_ACTIVITY_SIZES.get(vactivity.getStyle());
		mxGeometry geo = new mxGeometry(0, 0, ds.width, ds.height);
		vactivity.setGeometry(geo);
		
		if (BpmnStylesheetColor.COLLAPSED_SIZES.containsKey(vactivity.getStyle()) ||
			BpmnStylesheetColor.COLLAPSED_SIZES.containsKey(mactivity.getActivityType()))
		{
			Dimension ads = (Dimension) (BpmnStylesheetColor.COLLAPSED_SIZES.get(vactivity.getStyle()) != null?
				BpmnStylesheetColor.COLLAPSED_SIZES.get(vactivity.getStyle()) :
				BpmnStylesheetColor.COLLAPSED_SIZES.get(mactivity.getActivityType()));
			vactivity.getGeometry().setAlternateBounds(new mxGeometry(0, 0, ads.width, ads.height));
		}
	}
}
