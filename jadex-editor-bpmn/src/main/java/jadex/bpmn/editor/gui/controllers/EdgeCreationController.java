package jadex.bpmn.editor.gui.controllers;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VEdge;
import jadex.bpmn.editor.model.visual.VSequenceEdge;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

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
		if (!(cell instanceof VEdge) &&
			source instanceof VActivity &&
			target instanceof VActivity &&
			((MActivity) ((VActivity) source).getBpmnElement()).getPool().getId().equals(((MActivity) ((VActivity) target).getBpmnElement()).getPool().getId()))
		{
			MSequenceEdge medge = new MSequenceEdge();
			medge.setId(modelcontainer.getIdGenerator().generateId());
			MActivity msrc = (MActivity) ((VActivity) source).getBpmnElement();
			//MActivity mtgt = (MActivity) ((VActivity) target).getBpmnElement();
			//medge.setSource(msrc);
			//medge.setTarget(mtgt);
			//msrc.addOutgoingSequenceEdge(medge);
			//mtgt.addIncomingSequenceEdge(medge);
			msrc.getPool().addSequenceEdge(medge);
			/*VElement vpool = (VElement) source;
			while (!(vpool instanceof VPool))
			{
				vpool = (VElement) vpool.getParent();
			}
			*/
			VSequenceEdge vedge = new VSequenceEdge(modelcontainer.getGraph(), VSequenceEdge.class.getSimpleName());
			vedge.setBpmnElement(medge);
			vedge.setSource(source);
			vedge.setTarget(target);
			modelcontainer.getGraph().getModel().beginUpdate();
			modelcontainer.getGraph().removeCells(new Object[] {cell});
			modelcontainer.getGraph().addCell(vedge);
			modelcontainer.getGraph().getModel().endUpdate();
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
		}
	}
}
