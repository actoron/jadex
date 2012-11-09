package jadex.bpmn.editor.gui.controllers;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.editor.gui.ModelContainer;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;

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
