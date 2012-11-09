package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.gui.controllers.DeletionController;
import jadex.bpmn.editor.gui.controllers.EdgeCreationController;
import jadex.bpmn.editor.gui.controllers.KeyboardController;
import jadex.bpmn.editor.gui.controllers.MouseController;
import jadex.bpmn.editor.gui.controllers.SelectionController;
import jadex.bpmn.editor.gui.propertypanels.SPropertyPanelFactory;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;
import jadex.bpmn.model.MBpmnModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.util.mxEvent;

public class BpmnEditorWindow extends JFrame
{
	/** The pane containing the graph and the property view. */
	protected JSplitPane viewpane;
	
	/** The group of styles. */
	//protected ButtonGroup stylegroup;
	
	/** The container of the current model. */
	protected ModelContainer modelcontainer;
	
	/**
	 *  Creates a new editor window.
	 */
	public BpmnEditorWindow()
	{
		super("Jadex BPMN Editor");
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				getContentPane().setLayout(new BorderLayout());
				
				viewpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
				viewpane.setOneTouchExpandable(true);
				
				addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						System.exit(0);
					}
				});
				
				getContentPane().add(viewpane, BorderLayout.CENTER);
				
				modelcontainer = new ModelContainer();
				
				BpmnStylesheetColor colorsheet = new BpmnStylesheetColor();
				BpmnGraph graph = new BpmnGraph(colorsheet);
				
				MBpmnModel bpmnmodel = new MBpmnModel();
				modelcontainer.setBpmnModel(bpmnmodel);
				
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
				
				BpmnToolbar bpmntoolbar = new BpmnToolbar(GuiConstants.DEFAULT_ICON_SIZE, modelcontainer);
				getContentPane().add(bpmntoolbar, BorderLayout.PAGE_START);
				
				MouseController mc = new MouseController(modelcontainer);
				graphcomponent.getGraphControl().addMouseListener(mc);
				graphcomponent.getGraphControl().addMouseWheelListener(mc);
				
				new DeletionController(modelcontainer);
				
				new KeyboardController(graphcomponent, modelcontainer);
				
				EdgeCreationController edgecreationcontroller = new EdgeCreationController(modelcontainer);
				graphcomponent.getConnectionHandler().addListener(mxEvent.CONNECT,
						edgecreationcontroller);
				/*graph.addListener(mxEvent.MOVE_CELLS, new mxIEventListener()
				{
					public void invoke(Object sender, mxEventObject evt)
					{
						System.out.println(Arrays.toString(evt.getProperties().keySet().toArray()));
						System.out.println(evt.getProperty("target"));
						evt.consume();
					}
				});*/
				
				
				new mxRubberband(graphcomponent);
				
				viewpane.setTopComponent(graphcomponent);
				JPanel propertypanelcontainer = new JPanel(new BorderLayout())
				{
					public Component add(Component comp)
					{
						int loc = viewpane.getDividerLocation();
						Component ret = super.add(comp);
						viewpane.setDividerLocation(loc);
						return ret;
					}
					
					public void removeAll()
					{
						int loc = viewpane.getDividerLocation();
						super.removeAll();
						viewpane.setDividerLocation(loc);
					}
				};
				viewpane.setBottomComponent(propertypanelcontainer);
				modelcontainer.setPropertypanelcontainer(propertypanelcontainer);
				graph.getSelectionModel().addListener(mxEvent.CHANGE, new SelectionController(modelcontainer));
				
				/* Menu */
				JMenuBar menubar = new BpmnMenuBar(modelcontainer);
				setJMenuBar(menubar);
				
				modelcontainer.setPropertyPanel(SPropertyPanelFactory.createPanel(null, modelcontainer));
				
				pack();
				Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
				setSize((int) (sd.width * GuiConstants.GRAPH_PROPERTY_RATIO),
						(int) (sd.height * GuiConstants.GRAPH_PROPERTY_RATIO));
				setLocationRelativeTo(null);
				setVisible(true);
				viewpane.setResizeWeight(GuiConstants.GRAPH_PROPERTY_RATIO);
				viewpane.setDividerLocation(GuiConstants.GRAPH_PROPERTY_RATIO);
			}
		});
	}
}
