package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.gui.controllers.DeletionController;
import jadex.bpmn.editor.gui.controllers.KeyboardController;
import jadex.bpmn.editor.gui.controllers.MouseController;
import jadex.bpmn.editor.gui.controllers.SelectionController;
import jadex.bpmn.editor.gui.propertypanels.SPropertyPanelFactory;
import jadex.commons.gui.JSplitPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.util.mxEvent;

public class BpmnEditorPanel extends JSplitPanel
{
	/** The pane containing the graph and the property view. */
	//protected JSplitPanel viewpane;
	
	/** The group of styles. */
	//protected ButtonGroup stylegroup;
	
	/** The container of the current model. */
	protected ModelContainer modelcontainer;
	
	/**
	 *  Creates a new editor window.
	 */
	public BpmnEditorPanel(ModelContainer mcontainer)
	{
		
		//JSplitPane statuspane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		
		super(JSplitPane.VERTICAL_SPLIT);
		
		setOneTouchExpandable(true);
		
		modelcontainer = mcontainer;
		
		BpmnGraph graph = modelcontainer.getGraph();
		//BpmnStylesheetColor colorsheet = new BpmnStylesheetColor();
		
		
//		MBpmnModel bpmnmodel = new MBpmnModel();
//		modelcontainer.setBpmnModel(bpmnmodel);
		
		mxGraphComponent graphcomponent = new BpmnGraphComponent(graph);
		graphcomponent.setDragEnabled(false);
		graphcomponent.setPanning(true);
		graphcomponent.setCenterZoom(false);
		graphcomponent.setAutoscrolls(true);
		graphcomponent.setAutoExtend(true);
		graphcomponent.getViewport().setOpaque(false);
		graphcomponent.setBackground(Color.WHITE);
		graphcomponent.setOpaque(true);
		graphcomponent.setTextAntiAlias(true);
		modelcontainer.setGraphComponent(graphcomponent);
		
		MouseController mc = new MouseController(modelcontainer);
		graphcomponent.getGraphControl().addMouseListener(mc);
		graphcomponent.getGraphControl().addMouseWheelListener(mc);
		
		new DeletionController(modelcontainer);
		
		new KeyboardController(graphcomponent, modelcontainer);
		
		new mxRubberband(graphcomponent);
		
		setTopComponent(graphcomponent);
		JPanel propertypanelcontainer = new JPanel(new BorderLayout())
		{
			public Component add(Component comp)
			{
				int loc = getDividerLocation();
				Component ret = super.add(comp);
				setDividerLocation(loc);
				return ret;
			}
			
			public void removeAll()
			{
				int loc = getDividerLocation();
				super.removeAll();
				setDividerLocation(loc);
			}
		};
		setBottomComponent(propertypanelcontainer);
		modelcontainer.setPropertypanelcontainer(propertypanelcontainer);
		modelcontainer.setPropertyPanel(SPropertyPanelFactory.createPanel(null, modelcontainer));
		graph.getSelectionModel().addListener(mxEvent.CHANGE, new SelectionController(modelcontainer));
	}
	
	/**
	 *  Gets the model container.
	 *  
	 *  @return The model container.
	 */
	public ModelContainer getModelContainer()
	{
		return modelcontainer;
	}
	
	/** Bug fix goodness for Swing. */
	@SuppressWarnings("deprecation")
	public void reshape(int x, int y, int w, int h)
	{
		final double divloc = getProportionalDividerLocation();
		super.reshape(x, y, w, h);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				setDividerLocation(divloc);
			}
		});
	}
}
