package jadex.gpmn.editor.gui.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

import jadex.gpmn.editor.gui.ModelContainer;
import jadex.gpmn.editor.gui.SGuiHelper;
import jadex.gpmn.editor.model.gpmn.IActivationEdge;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.IPlanEdge;
import jadex.gpmn.editor.model.visual.VEdge;
import jadex.gpmn.editor.model.visual.VElement;
import jadex.gpmn.editor.model.visual.VGoal;
import jadex.gpmn.editor.model.visual.VPlan;
import jadex.gpmn.editor.model.visual.VVirtualActivationEdge;

public class EdgeReconnectController implements mxIEventListener
{
	/** The model container. */
	protected ModelContainer modelcontainer;
	
	/** Creates a new edge reconnect controller. */
	public EdgeReconnectController(ModelContainer container)
	{
		this.modelcontainer = container;
	}
	
	public void invoke(Object sender, mxEventObject evt)
	{
		if (evt.getProperty("edge") instanceof VVirtualActivationEdge)
		{
			reconnectVirtualActivationEdge(evt);
			modelcontainer.setDirty(true);
		}
		else if (evt.getProperty("edge") instanceof VEdge)
		{
			VEdge vedge = (VEdge) evt.getProperty("edge");
			if (Boolean.TRUE.equals(evt.getProperty("source")))
			{
				if (vedge.getEdge() instanceof IPlanEdge && vedge.getEdge().getTarget() instanceof IActivationPlan)
				{
					final VGoal prev = (VGoal) evt.getProperty("previous");
					final VGoal term = (VGoal) evt.getProperty("terminal");
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							SGuiHelper.refreshCellView(modelcontainer.getGraph(), prev);
							SGuiHelper.refreshCellView(modelcontainer.getGraph(), term);
						}
					});
				}
			}
			else
			{
				if (vedge.getEdge() instanceof IPlanEdge)
				{
					VPlan prev = (VPlan) evt.getProperty("previous");
					VPlan term = (VPlan) evt.getProperty("terminal");
					if (prev.getPlan() instanceof IActivationPlan ||
						term.getPlan() instanceof IActivationPlan)
					{
						final VGoal goal = (VGoal) vedge.getSource();
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								SGuiHelper.refreshCellView(modelcontainer.getGraph(), goal);
							}
						});
					}
				}
			}
			modelcontainer.setDirty(true);
		}
	}
	
	/**
	 *  
	 *  Handles reconnection of virtual activation edges (complex operation).
	 *  
	 *  @param evt The event that triggered the reconnect.
	 */
	protected void reconnectVirtualActivationEdge(mxEventObject evt)
	{
		mxGraph graph = modelcontainer.getGraph();
		VVirtualActivationEdge vedge = (VVirtualActivationEdge) evt.getProperty("edge");
		VGoal prev = (VGoal) evt.getProperty("previous");
		VGoal term = (VGoal) evt.getProperty("terminal");
		VPlan aplan = vedge.getPlan();
		
		if (Boolean.TRUE.equals(evt.getProperty("source")))
		{
			// Virtual Edge Source has changed.
			
			VEdge affectedplanedge = null;
			List<VEdge> planedges = new ArrayList<VEdge>();
			List<VEdge> actedges = new ArrayList<VEdge>();
			mxPoint min = new mxPoint(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
			mxPoint max = new mxPoint(0.0, 0.0);
			for (int i = 0; i < aplan.getEdgeCount(); ++i)
			{
				if (aplan.getEdgeAt(i) instanceof VEdge)
				{
					if (((VEdge) aplan.getEdgeAt(i)).getEdge() instanceof IPlanEdge)
					{
						VEdge curedge = (VEdge) aplan.getEdgeAt(i);
						if (curedge.getSource().equals(prev))
						{
							affectedplanedge = curedge;
						}
						planedges.add(curedge);
					}
					else if ((((VEdge) aplan.getEdgeAt(i)).getEdge() instanceof IActivationEdge) &&
							 !((VEdge) aplan.getEdgeAt(i)).getTarget().equals(vedge.getTarget()))
					{
						VEdge curedge = (VEdge) aplan.getEdgeAt(i);
						actedges.add(curedge);
						min.setX(Math.min(min.getX(), curedge.getTarget().getGeometry().getX()));
						min.setY(Math.min(min.getY(), curedge.getTarget().getGeometry().getY()));
						max.setX(Math.max(max.getX(), curedge.getTarget().getGeometry().getX()));
						max.setY(Math.max(max.getY(), curedge.getTarget().getGeometry().getY()));
					}
				} 
			}
			
			// Handle source goal.
			if (((IActivationPlan) aplan.getPlan()).getActivationEdges().size() == 1)
			{
				graph.getModel().beginUpdate();
				graph.removeCells(new Object[] { affectedplanedge });
				affectedplanedge.setSource(term);
				graph.addCell(affectedplanedge);
				graph.getModel().endUpdate();
			}
			else
			{
				// Elements that need to be added in both models.
				List<VElement> newelements = new ArrayList<VElement>();
				// Elements that need to be deleted in both models.
				List<VElement> delelements = new ArrayList<VElement>();
				delelements.add(affectedplanedge);
				
				VGoal tgoal = (VGoal) vedge.getTarget();
				
				// Remove virtual edges.
				modelcontainer.desynchModels();
				graph.getModel().beginUpdate();
				List<VVirtualActivationEdge> group = vedge.getEdgeGroup();
				VVirtualActivationEdge[] grouparray = group.toArray(new VVirtualActivationEdge[group.size()]);
				for (int i = 0; i < grouparray.length; ++i)
				{
					if (grouparray[i].getSource().equals(term) || 
						grouparray[i].getSource().equals(prev))
					{
						//System.out.println("Removing silently: " + grouparray[i]);
						graph.removeCells(new Object[] { grouparray[i] });
						group.remove(grouparray[i]);
					}
				}
				graph.getModel().endUpdate();
				modelcontainer.synchModels();
				
				// Handle previous source of the edge.
				IActivationPlan prevmaplan = (IActivationPlan) aplan.getPlan().getModel().copyNode(aplan.getPlan());
				mxPoint center = SGuiHelper.getCenter(min, max, new mxPoint[] { prev.getGeometry() });
				VPlan prevaplan = new VPlan(prevmaplan, center);
				newelements.add(prevaplan);
				
				group = new ArrayList<VVirtualActivationEdge>();
				for (VEdge actedge : actedges)
				{
					if (!actedge.getTarget().equals(tgoal))
					{
						IActivationEdge newmactedge = (IActivationEdge) prevmaplan.getModel().createEdge(prevmaplan, ((VGoal) actedge.getTarget()).getGoal(), IActivationEdge.class);
						VEdge newactedge = new VEdge(prevaplan, (VGoal) actedge.getTarget(), newmactedge);
						newelements.add(newactedge);
						
						VVirtualActivationEdge virtedge = new VVirtualActivationEdge(prev, (VGoal) actedge.getTarget(), group, prevaplan);
						group.add(virtedge);
						newelements.add(virtedge);
					}
				}
				
				IPlanEdge newmplanedge = (IPlanEdge) prevmaplan.getModel().createEdge(prev.getElement(), prevmaplan, IPlanEdge.class);
				VEdge newplanedge = new VEdge(prev, prevaplan, newmplanedge);
				newelements.add(newplanedge);
				
				// Handle new source of the edge.
				IActivationPlan termmaplan = (IActivationPlan) aplan.getPlan().getModel().copyNode(aplan.getPlan());
				center = SGuiHelper.getCenter(new mxPoint[] { term.getGeometry(), tgoal.getGeometry() });
				VPlan termaplan = new VPlan(termmaplan, center);
				newelements.add(termaplan);
				
				IActivationEdge newmactedge = (IActivationEdge) termmaplan.getModel().createEdge(termmaplan, tgoal.getGoal(), IActivationEdge.class);
				VEdge newactedge = new VEdge(termaplan, tgoal, newmactedge);
				newelements.add(newactedge);
				
				newmplanedge = (IPlanEdge) termmaplan.getModel().createEdge(term.getElement(), termmaplan, IPlanEdge.class);
				newplanedge = new VEdge(term, termaplan, newmplanedge);
				newelements.add(newplanedge);
				
				group = new ArrayList<VVirtualActivationEdge>();
				VVirtualActivationEdge virtedge = new VVirtualActivationEdge(term, tgoal, group, termaplan);
				group.add(virtedge);
				newelements.add(virtedge);
				
				if (aplan.getPlan().getPlanEdges().size() == 1)
				{
					delelements.add(aplan);
				}
				
				//System.out.println("Delete Array: " + Arrays.toString(delelements.toArray()));
				
				modelcontainer.desynchModels();
				graph.getModel().beginUpdate();
				graph.addCells(newelements.toArray());
				graph.removeCells(delelements.toArray());
				graph.getModel().setVisible(prevaplan, false);
				graph.getModel().setVisible(termaplan, false);
				graph.getModel().endUpdate();
				modelcontainer.synchModels();
			}
			
		}
		else
		{
			// Virtual Edge Target has changed.
			
			if (aplan.getPlan().getPlanEdges().size() == 1)
			{
				VEdge actedge = null;
				for (int i = 0; i < aplan.getEdgeCount() && actedge == null; ++i)
				{
					//System.out.println (String.valueOf(prev) + " " + String.valueOf(((VEdge) aplan.getEdgeAt(i)).getTarget()));
					if ((((VEdge) aplan.getEdgeAt(i)).getEdge() instanceof IActivationEdge) &&
						(((VEdge) aplan.getEdgeAt(i)).getTarget().equals(prev)))
					{
						actedge = (VEdge) aplan.getEdgeAt(i);
					}
				}
				
				modelcontainer.desynchModels();
				graph.getModel().beginUpdate();
				graph.removeCells(new Object[] { actedge });
				actedge.setTarget(term);
				graph.addCell(actedge);
				graph.getModel().endUpdate();
				modelcontainer.synchModels();
			}
			else
			{
				// Elements that need to be added in both models.
				List<VElement> newelements = new ArrayList<VElement>();
				
				VGoal sgoal = (VGoal) vedge.getSource();
				
				// Remove virtual edges.
				modelcontainer.desynchModels();
				graph.getModel().beginUpdate();
				
				List<mxGeometry> centerpoints = new ArrayList<mxGeometry>();
				List<VVirtualActivationEdge> group = vedge.getEdgeGroup();
				List<VElement> targets = new ArrayList<VElement>();
				VVirtualActivationEdge[] grouparray = group.toArray(new VVirtualActivationEdge[group.size()]);
				for (int i = 0; i < grouparray.length; ++i)
				{
					if (grouparray[i].getSource().equals(sgoal))
					{
						if (grouparray[i].getTarget().equals(prev) ||
							grouparray[i].getTarget().equals(term))
						{
							centerpoints.add(term.getGeometry());
							targets.add(term);
						}
						else
						{
							centerpoints.add(grouparray[i].getTarget().getGeometry());
							targets.add((VElement) grouparray[i].getTarget());
						}
						
						graph.removeCells(new Object[] { grouparray[i] });
						group.remove(grouparray[i]);
					}
				}
				graph.getModel().endUpdate();
				modelcontainer.synchModels();
				
				centerpoints.add(sgoal.getGeometry());
				
				// Delete old plan edge, disassociating from old activation plan
				for (int i = 0; i < sgoal.getEdgeCount(); ++i)
				{
					if (((VEdge) sgoal.getEdgeAt(i)).getTarget().equals(aplan))
					{
						graph.getModel().beginUpdate();
						graph.removeCells(new Object[] { sgoal.getEdgeAt(i) });
						graph.getModel().endUpdate();
						break;
					}
				}
				
				IActivationPlan newmaplan = (IActivationPlan) aplan.getPlan().getModel().copyNode(aplan.getPlan());
				mxPoint center = SGuiHelper.getCenter((mxGeometry[]) centerpoints.toArray(new mxGeometry[centerpoints.size()]));
				VPlan newaplan = new VPlan(newmaplan, center);
				newelements.add(newaplan);
				
				// Create required edges.
				group = new ArrayList<VVirtualActivationEdge>();
				for (VElement target : targets)
				{
					IActivationEdge newmactedge = (IActivationEdge) newmaplan.getModel().createEdge(newmaplan, target.getElement(), IActivationEdge.class);
					VEdge newactedge = new VEdge(newaplan, target, newmactedge);
					newelements.add(newactedge);
					
					VVirtualActivationEdge virtedge = new VVirtualActivationEdge(sgoal, target, group, newaplan);
					group.add(virtedge);
					newelements.add(virtedge);
				}
				IPlanEdge newmplanedge = (IPlanEdge) newmaplan.getModel().createEdge(sgoal.getGoal(), newmaplan, IPlanEdge.class);
				VEdge newplanedge = new VEdge(sgoal, newaplan, newmplanedge);
				newelements.add(newplanedge);
				
				graph.getModel().beginUpdate();
				graph.addCells(newelements.toArray());
				graph.getModel().setVisible(newaplan, false);
				graph.getModel().endUpdate();
			}
		}
	}
}
