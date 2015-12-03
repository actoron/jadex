package jadex.bpmn.editor.gui.controllers;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.editor.gui.ModelContainer;

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
//				Object[] cells = graph.getSelectionCells();
//				graph.removeCells(cells);
//				modelcontainer.getGraphComponent().refresh();
//				System.out.println(graph.getSelectionCells().length);
				graph.removeCells();
//				
//				for (Object cell : cells)
//				{
//					graph.getModel().remove(cell);
//				}
				
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
