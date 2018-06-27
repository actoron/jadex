package jadex.gpmn.editor.gui.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mxgraph.model.mxGraphModel.mxValueChange;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

import jadex.gpmn.editor.gui.ModelContainer;
import jadex.gpmn.editor.gui.SGuiHelper;
import jadex.gpmn.editor.model.gpmn.IActivationEdge;
import jadex.gpmn.editor.model.visual.VEdge;
import jadex.gpmn.editor.model.visual.VPlan;
import jadex.gpmn.editor.model.visual.VVirtualActivationEdge;

public class ValueChangeController implements mxIEventListener
{
	/** The model container. */
	protected ModelContainer modelcontainer;
	
	/** Creates a new edge creation controller. */
	public ValueChangeController(ModelContainer container)
	{
		this.modelcontainer = container;
	}

	public void invoke(Object sender, mxEventObject evt)
	{
		Object chngobj = evt.getProperty("change");
		if (chngobj instanceof mxValueChange)
		{
			mxValueChange vc = (mxValueChange) chngobj;
			if (vc.getCell() instanceof VEdge)
			{
				VEdge vedge = (VEdge) vc.getCell();
				if (vedge.getEdge() instanceof IActivationEdge)
				{
					updateActivationEdgeOrder(vc, vedge);
					modelcontainer.setDirty(true);
				}
				else if (vedge instanceof VVirtualActivationEdge)
				{
					VEdge vaedge = ((VVirtualActivationEdge) vedge).getActivationEdge();
					updateActivationEdgeOrder(vc, vaedge);
					List<VVirtualActivationEdge> group = ((VVirtualActivationEdge) vedge).getEdgeGroup();
					for (VVirtualActivationEdge virtedge : group)
					{
						SGuiHelper.refreshCellView(modelcontainer.getGraph(), virtedge);
					}
					modelcontainer.setDirty(true);
				}
			}
		}
	}
	
	/**
	 *  
	 *  Updates the activation edge order.
	 *  
	 *  @param vc The value change.
	 *  @param vedge The changed activation edge.
	 */
	protected void updateActivationEdgeOrder(mxValueChange vc, VEdge vedge)
	{
		VPlan vaplan = (VPlan) vedge.getSource();
		IActivationEdge aedge = (IActivationEdge) vedge.getEdge();
		if (vaplan != null)
		{
			boolean switched = false;
			
			int order = aedge.getOrder();
			int oldorder = ((Integer) vc.getPrevious()).intValue();
			List<VEdge> edges = new ArrayList<VEdge>();
			for (int i = 0; i < vaplan.getEdgeCount() && !switched; ++i)
			{
				VEdge edge = (VEdge) vaplan.getEdgeAt(i);
				
				if (edge.getEdge() instanceof IActivationEdge)
				{
					edges.add(edge);
					if (edge.getEdge() != aedge &&
					   ((IActivationEdge) edge.getEdge()).getOrder() == order)
					{
						((IActivationEdge) edge.getEdge()).setOrder(oldorder);
						switched = true;
						SGuiHelper.refreshCellView(modelcontainer.getGraph(), edge);
					}
				}
			}
			
			if (!switched)
			{
				Collections.sort(edges, new Comparator<VEdge>()
				{
					public int compare(VEdge o1, VEdge o2)
					{
						return ((IActivationEdge) o1.getEdge()).getOrder() -
								((IActivationEdge) o2.getEdge()).getOrder();
					}
				});
				
				int count = 0;
				for (VEdge edge : edges)
				{
					modelcontainer.getGraph().getModel().beginUpdate();
					((IActivationEdge) edge.getEdge()).setOrder(++count);
					modelcontainer.getGraph().getModel().endUpdate();
					SGuiHelper.refreshCellView(modelcontainer.getGraph(), edge);
				}
			}
		}
	}
	
}
