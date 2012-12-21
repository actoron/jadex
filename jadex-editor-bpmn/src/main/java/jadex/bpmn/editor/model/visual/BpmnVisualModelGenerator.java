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
		
		for (MPool mpool : pools)
		{
			VPool vpool = new VPool(graph);
			vpool.setGeometry(new mxGeometry(0, 0, BpmnStylesheetColor.DEFAULT_POOL_WIDTH, BpmnStylesheetColor.DEFAULT_POOL_HEIGHT));
			graph.getModel().beginUpdate();
			graph.addCell(vpool);
			graph.getModel().endUpdate();
			vpool.setBpmnElement(mpool);
			
			Map<String, mxICell> elements = new HashMap<String, mxICell>();
			
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
					elements.put(mactivity.getId(), generateActivity(graph, mactivity, vpool));
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
							elements.put(mactivity.getId(), generateActivity(graph, mactivity, vlane));
						}
					}
				}
			}
			
			List<MSequenceEdge> seqedges = mpool.getSequenceEdges();
			if (seqedges != null && seqedges.size() > 0)
			{
				for (MSequenceEdge medge : seqedges)
				{
					VSequenceEdge vedge = new VSequenceEdge(graph, VSequenceEdge.class.getSimpleName());
					vedge.setSource(elements.get(medge.getSource().getId()));
					vedge.setTarget(elements.get(medge.getTarget().getId()));
					vedge.setBpmnElement(medge);
					graph.getModel().beginUpdate();
					graph.addCell(vedge, elements.get(medge.getSource().getId()).getParent());
					graph.getModel().endUpdate();
				}
			}
			
		}
	}
	
	protected VActivity generateActivity(BpmnGraph graph, MActivity mactivity, mxICell parent)
	{
		VActivity vactivity = null;
		if (mactivity instanceof MSubProcess)
		{
			if (mactivity.hasPropertyValue("file"))
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
				
				Map<String, mxICell> elements = new HashMap<String, mxICell>();
				
				List<MActivity> evthandlers = mactivity.getEventHandlers();
				if (evthandlers != null && evthandlers.size() > 0)
				{
					for (MActivity evthandler : evthandlers)
					{
						elements.put(evthandler.getId(), generateActivity(graph, evthandler, vactivity));
					}
				}
				
				List<MActivity> activities = ((MSubProcess) mactivity).getActivities();
				if (activities != null && activities.size() > 0)
				{
					for (MActivity activity : activities)
					{
						elements.put(activity.getId(), generateActivity(graph, activity, vactivity));
					}
				}
				
				List<MSequenceEdge> seqedges = ((MSubProcess) mactivity).getSequenceEdges();
				if (seqedges != null && seqedges.size() > 0)
				{
					for (MSequenceEdge medge : seqedges)
					{
						VSequenceEdge vedge = new VSequenceEdge(graph, VSequenceEdge.class.getSimpleName());
						vedge.setSource(elements.get(medge.getSource().getId()));
						vedge.setTarget(elements.get(medge.getTarget().getId()));
						vedge.setBpmnElement(medge);
						graph.getModel().beginUpdate();
						graph.addCell(vedge, elements.get(medge.getSource().getId()).getParent());
						graph.getModel().endUpdate();
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
			
			List<MActivity> evthandlers = mactivity.getEventHandlers();
			if (evthandlers != null && evthandlers.size() > 0)
			{
				for (MActivity evthandler : evthandlers)
				{
					generateActivity(graph, evthandler, vactivity);
				}
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
