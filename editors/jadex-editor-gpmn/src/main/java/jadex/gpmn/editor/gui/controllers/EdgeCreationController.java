package jadex.gpmn.editor.gui.controllers;

import java.awt.Point;
import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

import jadex.gpmn.editor.gui.ModelContainer;
import jadex.gpmn.editor.gui.SGuiHelper;
import jadex.gpmn.editor.model.gpmn.IActivationEdge;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.IEdge;
import jadex.gpmn.editor.model.gpmn.IGoal;
import jadex.gpmn.editor.model.gpmn.IPlan;
import jadex.gpmn.editor.model.gpmn.IPlanEdge;
import jadex.gpmn.editor.model.gpmn.ISuppressionEdge;
import jadex.gpmn.editor.model.visual.VEdge;
import jadex.gpmn.editor.model.visual.VElement;
import jadex.gpmn.editor.model.visual.VGoal;
import jadex.gpmn.editor.model.visual.VPlan;
import jadex.gpmn.editor.model.visual.VVirtualActivationEdge;

public class EdgeCreationController implements mxIEventListener
{
	/** The model container. */
	protected ModelContainer modelcontainer;
	
	/** Creates a new edge creation controller. */
	public EdgeCreationController(ModelContainer container)
	{
		this.modelcontainer = container;
	}
	
	public void invoke(Object sender, mxEventObject evt)
	{
		mxCell cell = (mxCell) evt.getProperty("cell");
		mxICell source = cell.getSource();
		mxICell target = cell.getTarget();
		if (!(cell instanceof VEdge) && (source instanceof VGoal) && (target instanceof VPlan))
		{
			IGoal gsource = ((VGoal) source).getGoal();
			IPlan ptarget = ((VPlan) target).getPlan();
			IEdge edge = modelcontainer.getGpmnModel().createEdge(gsource, ptarget, IPlanEdge.class);
			VEdge vedge = new VEdge((VElement) source, (VElement) target, edge);
			modelcontainer.getGraph().getModel().beginUpdate();
			modelcontainer.getGraph().removeCells(new Object[] {cell});
			modelcontainer.getGraph().addCell(vedge);
			modelcontainer.getGraph().getModel().endUpdate();
			SGuiHelper.refreshCellView(modelcontainer.getGraph(), (VGoal) source);
			modelcontainer.setDirty(true);
		}
		else if (!(cell instanceof VEdge) && (source instanceof VPlan) &&
				(((VPlan) source).getPlan() instanceof IActivationPlan) &&
				(target instanceof VGoal))
		{
			IPlan psource = ((VPlan) source).getPlan();
			IGoal gtarget = ((VGoal) target).getGoal();
			IEdge edge = null;
			
			for (IEdge tmpedge : psource.getSourceEdges())
			{
				if (tmpedge.getTarget() == gtarget)
				{
					edge = tmpedge;
					break;
				}
			}
			
			modelcontainer.getGraph().getModel().beginUpdate();
			modelcontainer.getGraph().removeCells(new Object[] {cell});
			if (edge == null)
			{
				edge = modelcontainer.getGpmnModel().createEdge(psource, gtarget, IActivationEdge.class);
				VEdge vedge = new VEdge((VElement) source, (VElement) target, edge);
				modelcontainer.getGraph().addCell(vedge);
			}
			modelcontainer.getGraph().getModel().endUpdate();
			modelcontainer.setDirty(true);
		}
		else if (!(cell instanceof VEdge) && (source instanceof VGoal) && (target instanceof VGoal))
		{
			VGoal vsource = (VGoal) source;
			VGoal vtarget = (VGoal) target;
			IGoal gsource = vsource.getGoal();
			IGoal gtarget = vtarget.getGoal();
			
			if (ModelContainer.SUPPRESSION_EDGE_MODE.equals(modelcontainer.getEditMode()))
			{
				IEdge edge = modelcontainer.getGpmnModel().createEdge(gsource, gtarget, ISuppressionEdge.class);
				VEdge vedge = new VEdge((VElement) source, (VElement) target, edge);
				modelcontainer.getGraph().getModel().beginUpdate();
				modelcontainer.getGraph().removeCells(new Object[] {cell});
				modelcontainer.getGraph().addCell(vedge);
				modelcontainer.getGraph().getModel().endUpdate();
				modelcontainer.setDirty(true);
			}
			else
			{
				VPlan actplan = null;
				Object[] sedges = modelcontainer.getGraph().getEdges(vsource);
				for (int i = 0; i < sedges.length && actplan == null; ++i)
				{
					if ((sedges[i] instanceof VEdge) && 
						((VElement)(((VEdge) sedges[i]).getTarget())).getElement() instanceof IActivationPlan)
					{
						actplan = (VPlan) ((VEdge) sedges[i]).getTarget();
					}
				}
				
				if (actplan != null)
				{
					VEdge vedge = null;
					for (int i = 0; i < actplan.getEdgeCount() && vedge == null; ++i)
					{
						VEdge tmpedge = (VEdge) actplan.getEdgeAt(i);
						if (vtarget.equals(tmpedge.getTarget()))
							vedge = tmpedge;
					}
					
					if (vedge == null)
					{
						IEdge aedge = modelcontainer.getGpmnModel().createEdge(actplan.getNode(), vtarget.getGoal(), IActivationEdge.class);
						vedge = new VEdge(actplan, vtarget, aedge);
						
						VVirtualActivationEdge virtedge = null;
						if (!actplan.isVisible())
						{
							List<VVirtualActivationEdge> group = null;
							int ind = 0;
							while (group == null)
							{
								VEdge curedge = (VEdge) vsource.getEdgeAt(ind++);
								if ((curedge instanceof VVirtualActivationEdge) &&
									((VVirtualActivationEdge) curedge).getPlan().equals(actplan))
								{
									group = ((VVirtualActivationEdge) curedge).getEdgeGroup();
								}
							}
							virtedge = new VVirtualActivationEdge(vsource, vtarget, group, actplan);
							group.add(virtedge);
						}
						
						modelcontainer.getGraph().getModel().beginUpdate();
						if (cell != null)
						{
							modelcontainer.getGraph().removeCells(new Object[] {cell});
						}
						modelcontainer.getGraph().addCell(vedge);
						if (virtedge != null)
						{
							modelcontainer.getGraph().addCell(virtedge);
						}
						modelcontainer.getGraph().getModel().endUpdate();
						modelcontainer.getGraph().refresh();
						modelcontainer.setDirty(true);
					}
					else
					{
						modelcontainer.getGraph().getModel().beginUpdate();
						if (cell != null)
						{
							modelcontainer.getGraph().removeCells(new Object[] {cell});
						}
						modelcontainer.getGraph().getModel().endUpdate();
						modelcontainer.getGraph().refresh();
						modelcontainer.setDirty(true);
					}
				}
				else
				{
					IActivationPlan plan = (IActivationPlan) modelcontainer.getGpmnModel().createNode(IActivationPlan.class);
					Point sp = vsource.getGeometry().getPoint();
					Point tp = vtarget.getGeometry().getPoint();
					double x = (sp.getX() + tp.getX()) * 0.5;
					double y = (sp.getY() + tp.getY()) * 0.5;
					actplan = new VPlan(plan, x, y);
					
					IEdge pedge = modelcontainer.getGpmnModel().createEdge(vsource.getGoal(), plan, IPlanEdge.class);
					VEdge pvedge = new VEdge(vsource, actplan, pedge);
					IEdge aedge = modelcontainer.getGpmnModel().createEdge(actplan.getNode(), vtarget.getGoal(), IActivationEdge.class);
					VEdge avedge = new VEdge(actplan, vtarget, aedge);
					
					modelcontainer.getGraph().getModel().beginUpdate();
					if (cell != null)
					{
						modelcontainer.getGraph().removeCells(new Object[] {cell});
					}
					modelcontainer.getGraph().addCell(actplan);
					modelcontainer.getGraph().addCell(pvedge);
					modelcontainer.getGraph().addCell(avedge);
					modelcontainer.getGraph().getModel().endUpdate();
					modelcontainer.getGraph().refresh();
					modelcontainer.setDirty(true);
				}
			}
			
		}
		else
		{
			modelcontainer.getGraph().getModel().beginUpdate();
			if (cell != null)
			{
				modelcontainer.getGraph().removeCells(new Object[] {cell});
			}
			modelcontainer.getGraph().getModel().endUpdate();
		}
	}
}
