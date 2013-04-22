package jadex.bpmn.editor.gui.controllers;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VInParameter;
import jadex.bpmn.editor.model.visual.VOutParameter;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.view.mxGraphSelectionModel;

public class KeyboardController extends mxKeyboardHandler
{
	/** The model container. */
	protected ModelContainer modelcontainer;
	
	public KeyboardController(mxGraphComponent graphcomponent, ModelContainer modelcontainer)
	{
		super(graphcomponent);
		this.modelcontainer = modelcontainer;
	}
	
	protected ActionMap createActionMap()
	{
		ActionMap am = new ActionMap();
		am.put("delete", new AbstractAction()
		{
			public void actionPerformed(ActionEvent evt)
			{
				BpmnGraph graph = modelcontainer.getGraph();
				
//				mxGraphSelectionModel sm = graph.getSelectionModel();
//				Object[] selections = sm.getCells();
//				for (Object selection : selections)
//				{
//					if (selection instanceof VInParameter ||
//						selection instanceof VOutParameter)
//					{
//						sm.removeCell(selection);
//					}
//				}
				
				graph.getModel().beginUpdate();
				graph.removeCells();
				graph.getModel().endUpdate();
			}
		});
		return am;
	}
	
	protected InputMap getInputMap(int condition)
	{
		InputMap im = new InputMap();
		im.put(KeyStroke.getKeyStroke("DELETE"), "delete");
		return im;
	}
}
