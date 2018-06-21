package jadex.gpmn.editor.gui.controllers;

import java.util.ArrayList;
import java.util.List;

import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;

import jadex.gpmn.editor.gui.ModelContainer;
import jadex.gpmn.editor.model.gpmn.IActivationEdge;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.IPlanEdge;
import jadex.gpmn.editor.model.visual.VEdge;
import jadex.gpmn.editor.model.visual.VGoal;
import jadex.gpmn.editor.model.visual.VPlan;
import jadex.gpmn.editor.model.visual.VVirtualActivationEdge;

/** 
 *  Controller managing folding actions.
 *
 */
public class FoldController implements mxIEventListener
{
	/** The model container. */
	protected ModelContainer modelcontainer;
	
	/** Creates a new fold controller. */
	public FoldController(ModelContainer container)
	{
		this.modelcontainer = container;
	}
	
	/** 
	 *  Handle fold actions.
	 */
	public void invoke(Object sender, mxEventObject evt)
	{
		Object[] cells = (Object[])evt.getProperty("cells");
		if (Boolean.TRUE.equals(evt.getProperty("collapse")))
		{
			for (int i = 0; i < cells.length; ++i)
			{
				if (cells[i] instanceof VPlan && ((VPlan) cells[i]).getPlan() instanceof IActivationPlan)
				{
					foldActivationPlan((VPlan) cells[i]);
					modelcontainer.setDirty(true);
				}
			}
		}
	}
	
	public void foldActivationPlan(VPlan plan)
	{
		// De-synch models, visual-only operation.
		//controlleraccess.desynchModels();
		
		// Collect sources and targets, delete visual edges.
		List<VGoal> srcgoals = new ArrayList<VGoal>();
		List<VGoal> tgtgoals = new ArrayList<VGoal>();
		//List<VElement> deletiontargets = new ArrayList<VElement>();
		for (int i = 0; i < plan.getEdgeCount(); ++i)
		{
			if (plan.getEdgeAt(i) instanceof VEdge)
			{
				VEdge curedge = (VEdge) plan.getEdgeAt(i);
				if (curedge.getEdge() instanceof IActivationEdge)
				{
					tgtgoals.add((VGoal) curedge.getTarget());
					//deletiontargets.add(curedge);
				}
				else if (curedge.getEdge() instanceof IPlanEdge)
				{
					srcgoals.add((VGoal) curedge.getSource());
					//deletiontargets.add(curedge);
				}
			}
		}
		
		modelcontainer.getGraph().getModel().setVisible(plan, false);
		
		// Insert virtual edges
		List<VVirtualActivationEdge> edgegroup = new ArrayList<VVirtualActivationEdge>();
		for (int i = 0; i < srcgoals.size(); ++i)
		{
			for (int j = 0; j < tgtgoals.size(); ++j)
			{
				VGoal src = srcgoals.get(i);
				VGoal tgt = tgtgoals.get(j);
				
				VVirtualActivationEdge virtedge = new VVirtualActivationEdge(src, tgt, edgegroup, plan);
				edgegroup.add(virtedge);
			}
		}
		
		modelcontainer.getGraph().addCells(edgegroup.toArray());
		
		//controlleraccess.synchModels();
	}
	
	public void unfoldActivationPlan(VVirtualActivationEdge startedge)
	{
		List<VVirtualActivationEdge> group = startedge.getEdgeGroup();
		
		// Remove virtual edges and restore visual cells.
		modelcontainer.desynchModels();
		mxGraph graph = modelcontainer.getGraph();
		graph.getModel().beginUpdate();
		graph.removeCells(group.toArray());
		graph.getModel().setVisible(startedge.getPlan(), true);
		graph.foldCells(false, false, new Object[] { startedge.getPlan() });
		graph.getModel().endUpdate();
		modelcontainer.synchModels();
	}
}
