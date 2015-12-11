package jadex.bpmn.editor.model.visual;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.commons.Tuple3;

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
		
		Map<String, Tuple3<Integer, mxICell, mxICell>> elements = new HashMap<String, Tuple3<Integer, mxICell, mxICell>>();
		List<MSequenceEdge> seqedges = new ArrayList<MSequenceEdge>();
		
		for(MPool mpool : pools)
		{
			VPool vpool = new VPool(graph);
			vpool.setGeometry(new mxGeometry(0, 0, BpmnStylesheetColor.DEFAULT_POOL_WIDTH, BpmnStylesheetColor.DEFAULT_POOL_HEIGHT));
			graph.getModel().beginUpdate();
			graph.addCell(vpool);
			graph.getModel().endUpdate();
			vpool.setBpmnElement(mpool);
			
			List<MActivity> activities = mpool.getActivities();
			if(activities != null && activities.size() > 0)
			{
				for(MActivity mactivity : activities)
				{
					if(mactivity.getOutgoingSequenceEdges() != null)
					{
						seqedges.addAll(mactivity.getOutgoingSequenceEdges());
					}
					genActivity(graph, mactivity, seqedges, elements, vpool, 0);
				}
			}
			
			List<MLane> lanes = mpool.getLanes();
			if(lanes != null && lanes.size() > 0)
			{
				int cnt = 0;
				for(MLane mlane : lanes)
				{
					cnt++;
					VLane vlane = new VLane(graph);
					graph.getModel().beginUpdate();
					graph.addCell(vlane, vpool);
					graph.getModel().endUpdate();
					vlane.setBpmnElement(mlane);
					vlane.setGeometry(new mxGeometry(0, 0, BpmnStylesheetColor.DEFAULT_POOL_WIDTH-cnt*10, BpmnStylesheetColor.DEFAULT_POOL_HEIGHT-cnt*10));
					
					activities = mlane.getActivities();
					if(activities != null && activities.size() > 0)
					{
						for (MActivity mactivity : activities)
						{
							genActivity(graph, mactivity, seqedges, elements, vlane, 1);
						}
					}
				}
			}
		}
		
		if(elements != null && elements.size() > 0)
		{
			for(Tuple3<Integer, mxICell, mxICell> tup: elements.values())
			{
				graph.getModel().beginUpdate();
				graph.addCell(tup.getSecondEntity(), tup.getThirdEntity());
				graph.getModel().endUpdate();
				setActivityGeometry((VActivity)tup.getSecondEntity());
			}
		}
		
		if(seqedges != null && seqedges.size() > 0)
		{
			for(MSequenceEdge medge : seqedges)
			{
				VSequenceEdge vedge = new VSequenceEdge(graph);
				vedge.setSource(elements.get(medge.getSource().getId()).getSecondEntity());
				vedge.setTarget(elements.get(medge.getTarget().getId()).getSecondEntity());
				vedge.setBpmnElement(medge);
				graph.getModel().beginUpdate();
//				graph.addCell(vedge, elements.get(medge.getSource().getId()).getParent());
				graph.addCell(vedge, vedge.getEdgeParent());
				graph.getModel().endUpdate();
			}
		}
	}
	
	/**
	 * 
	 */
	protected void genActivity(BpmnGraph graph, MActivity mactivity, List<MSequenceEdge> sseqedges, 
		Map<String, Tuple3<Integer, mxICell, mxICell>> elements, mxICell vparent, int depth)
	{
		Tuple3<Integer, mxICell, mxICell> tup = elements.get(mactivity.getId());
		if(tup!=null)
		{
			if(depth>tup.getFirstEntity().intValue())
			{
				elements.put(mactivity.getId(), new Tuple3<Integer,  mxICell, mxICell>(Integer.valueOf(depth), tup.getSecondEntity(), vparent));
			}
		}
		else
		{
			if(mactivity.getOutgoingSequenceEdges() != null)
				sseqedges.addAll(mactivity.getOutgoingSequenceEdges());
			
			VActivity vactivity = mactivity instanceof MSubProcess? mactivity.hasProperty("file") || mactivity.hasProperty("filename")? 
				new VExternalSubProcess(graph): new VSubProcess(graph): new VActivity(graph);
			vactivity.setBpmnElement(mactivity);
			elements.put(mactivity.getId(), new Tuple3<Integer,  mxICell, mxICell>(Integer.valueOf(depth), vactivity, vparent));
		}
		
		if(mactivity instanceof MSubProcess)
		{
			List<MActivity> activities = ((MSubProcess) mactivity).getActivities();
			if(activities != null && activities.size() > 0)
			{
				for(MActivity activity : activities)
				{
					genActivity(graph, activity, sseqedges, elements, elements.get(mactivity.getId()).getSecondEntity(), depth+1);
				}
			}
		}
		
		List<MActivity> evthandlers = mactivity.getEventHandlers();
		if(evthandlers != null && evthandlers.size() > 0)
		{
			for(MActivity evthandler : evthandlers)
			{
				if(evthandler.getOutgoingSequenceEdges() != null && sseqedges != null)
				{
					sseqedges.addAll(evthandler.getOutgoingSequenceEdges());
				}
				
				genActivity(graph, evthandler, sseqedges, elements, elements.get(mactivity.getId()).getSecondEntity(), depth+1);
			}
		}
	}
	
//	protected VActivity generateActivity(BpmnGraph graph, MActivity mactivity, mxICell parent, Map<String, mxICell> elements, List<MSequenceEdge> sseqedges)
//	{
//		VActivity vactivity = null;
//		if(mactivity instanceof MSubProcess)
//		{
//			if (mactivity.hasProperty("file") ||
//				mactivity.hasProperty("filename"))
//			{
//				vactivity = new VExternalSubProcess(graph);
//				
//				graph.getModel().beginUpdate();
//				graph.addCell(vactivity, parent);
//				graph.getModel().endUpdate();
//				
//				vactivity.setBpmnElement(mactivity);
//			}
//			else
//			{
//				vactivity = new VSubProcess(graph);
//				
//				graph.getModel().beginUpdate();
//				graph.addCell(vactivity, parent);
//				graph.getModel().endUpdate();
//				
//				vactivity.setBpmnElement(mactivity);
//				
//				List<MActivity> activities = ((MSubProcess) mactivity).getActivities();
//				if (activities != null && activities.size() > 0)
//				{
//					for (MActivity activity : activities)
//					{
//						if (activity.getOutgoingSequenceEdges() != null)
//						{
//							sseqedges.addAll(activity.getOutgoingSequenceEdges());
//						}
//						if(elements.get(activity.getId())==null)
//							elements.put(activity.getId(), generateActivity(graph, activity, vactivity, elements, sseqedges));
//					}
//				}
//			}
//		}
//		else
//		{
//			vactivity = new VActivity(graph);
//			
//			graph.getModel().beginUpdate();
//			graph.addCell(vactivity, parent);
//			graph.getModel().endUpdate();
//			
//			vactivity.setBpmnElement(mactivity);
//			
//			if (mactivity.getOutgoingSequenceEdges() != null)
//			{
//				sseqedges.addAll(mactivity.getOutgoingSequenceEdges());
//			}
//		}
//		
//		List<MActivity> evthandlers = mactivity.getEventHandlers();
//		if (evthandlers != null && evthandlers.size() > 0)
//		{
//			for (MActivity evthandler : evthandlers)
//			{
////				if (evthandler.getOutgoingSequenceEdges() != null && sseqedges != null)
////				{
////					sseqedges.addAll(evthandler.getOutgoingSequenceEdges());
////				}
//				if(elements.get(evthandler.getId())==null)
//					elements.put(evthandler.getId(), generateActivity(graph, evthandler, vactivity, elements, sseqedges));
//			}
//		}
//		
//		setActivityGeometry(vactivity);
//		
//		return vactivity;
//	}
	
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
