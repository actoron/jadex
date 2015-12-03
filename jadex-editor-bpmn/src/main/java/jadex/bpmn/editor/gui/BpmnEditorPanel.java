package jadex.bpmn.editor.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.mxgraph.util.mxEvent;

import jadex.bpmn.editor.gui.controllers.SelectionController;
import jadex.commons.gui.JSplitPanel;

/**
 * 
 */
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
		setResizeWeight(1.0);
		
		modelcontainer = mcontainer;
		
		BpmnGraph graph = modelcontainer.getGraph();
		//BpmnStylesheetColor colorsheet = new BpmnStylesheetColor();
		
		
//		MBpmnModel bpmnmodel = new MBpmnModel();
//		modelcontainer.setBpmnModel(bpmnmodel);
		
		BpmnGraphComponent graphcomponent = new BpmnGraphComponent(graph);
//		graphcomponent.setDragEnabled(false);
//		graphcomponent.setPanning(true);
//		graphcomponent.setCenterZoom(false);
//		graphcomponent.setAutoscrolls(true);
//		graphcomponent.setAutoExtend(true);
//		graphcomponent.getViewport().setOpaque(false);
//		graphcomponent.setBackground(Color.WHITE);
//		graphcomponent.setOpaque(true);
//		graphcomponent.setTextAntiAlias(true);
//		modelcontainer.setGraphComponent(graphcomponent);
//		
//		MouseController mc = new MouseController(modelcontainer);
//		graphcomponent.getGraphControl().addMouseListener(mc);
//		graphcomponent.getGraphControl().addMouseWheelListener(mc);
//		
//		new DeletionController(modelcontainer);
//		
//		new KeyboardController(graphcomponent, modelcontainer);
//		
//		new mxRubberband(graphcomponent);
		
		graphcomponent.init(modelcontainer);
		
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
//		modelcontainer.setPropertyPanel(SPropertyPanelFactory.createPanel(null, modelcontainer));
		modelcontainer.setPropertyPanel(modelcontainer.getSettings().getPropertyPanelFactory().createPanel(modelcontainer, null));
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
}
