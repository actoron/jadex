package jadex.gpmn.editor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.view.mxCellEditor;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxStylesheet;

import jadex.commons.gui.JSplitPanel;
import jadex.gpmn.editor.gui.controllers.EdgeCreationController;
import jadex.gpmn.editor.gui.controllers.MouseController;
import jadex.gpmn.editor.gui.stylesheets.GpmnStylesheetColor;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;
import jadex.gpmn.editor.model.visual.VNode;

public class GpmnEditorWindow extends JFrame
{
	/** The model factory. */
	//protected IGpmnModelFactory mfactory;
	
	/** The pane containing the graph and the property view. */
	protected JSplitPane viewpane;
	
	/** The model container */
	protected ModelContainer modelcontainer;
	
	/** The select tool. */
	protected JToggleButton selecttool;
	
	/** The group of styles. */
	protected ButtonGroup stylegroup;
	
	public GpmnEditorWindow()
	{
		super("Jadex GPMN Editor");
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				getContentPane().setLayout(new BorderLayout());
				
				setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				
//				viewpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
				viewpane = new JSplitPanel(JSplitPane.VERTICAL_SPLIT);
				viewpane.setOneTouchExpandable(true);
				
				addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						if (modelcontainer.checkUnsaved(GpmnEditorWindow.this))
						{
							System.exit(0);
						}
					}
				});
				
				
//				toolgroup = new ButtonGroup();
//				editingtools.setFloatable(false);
				
//				addTools(editingtools);
				
				
				
				getContentPane().add(viewpane, BorderLayout.CENTER);
				
				modelcontainer = new ModelContainer(new GpmnIntermediateModelFactory(), null, null);
				
				GpmnToolbar editingtools = new GpmnToolbar(32, modelcontainer);
				getContentPane().add(editingtools, BorderLayout.PAGE_START);
				
//				foldcontroller = new FoldController(modelcontainer, EditorWindow.this);
				GpmnGraph graph = new GpmnGraph(modelcontainer, new GpmnStylesheetColor());
				
				IGpmnModel gpmnmodel = modelcontainer.getModelFactory().createModel();
				modelcontainer.setGpmnModel(gpmnmodel);
				
				mxGraphComponent graphcomponent = new GpmnGraphComponent(graph);
				modelcontainer.setGraphComponent(graphcomponent);
				graphcomponent.setDragEnabled(false);
				graphcomponent.setPanning(true);
				graphcomponent.setCenterZoom(false);
				graphcomponent.setAutoscrolls(true);
				graphcomponent.setAutoExtend(true);
				graphcomponent.getViewport().setOpaque(false);
				graphcomponent.setBackground(Color.WHITE);
				graphcomponent.setOpaque(true);
				graphcomponent.setTextAntiAlias(true);
				graphcomponent.setCellEditor(new mxCellEditor(graphcomponent)
				{
					protected boolean useLabelBounds(mxCellState state)
					{
						// Hack to edit label at label position.
						return super.useLabelBounds(state) || state.getCell() instanceof VNode;
					}
				});
				
				EdgeCreationController edgecreationcontroller = new EdgeCreationController(modelcontainer);
				graphcomponent.getConnectionHandler().addListener(mxEvent.CONNECT,
						edgecreationcontroller);
				
//				deletioncontroller = new DeletionController(modelcontainer);
				modelcontainer.synchModels();
				
				MouseController mc = new MouseController(modelcontainer);
				graphcomponent.getGraphControl().addMouseListener(mc);
				graphcomponent.getGraphControl().addMouseWheelListener(mc);
				
				new mxKeyboardHandler(graphcomponent)
				{
					protected ActionMap createActionMap()
					{
						ActionMap am = new ActionMap();
						am.put("delete", new AbstractAction()
						{
							public void actionPerformed(ActionEvent evt)
							{
								GpmnGraph graph = modelcontainer.getGraph();
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
				};
				
				new mxRubberband(graphcomponent);
				
				viewpane.setTopComponent(graphcomponent);
				
				setJMenuBar(new GpmnMenuBar(modelcontainer));
				
//				setPropertyPanel(SPropertyPanelFactory.createPanel(modelcontainer));
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
				modelcontainer.setPropertyPanelContainer(propertypanelcontainer);
				modelcontainer.setPropertyPanel(SPropertyPanelFactory.createPanel(modelcontainer));
				
				pack();
				Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
				setSize((int) (sd.width * GuiConstants.GRAPH_PROPERTY_RATIO),
						(int) (sd.height * GuiConstants.GRAPH_PROPERTY_RATIO));
				setLocationRelativeTo(null);
				setVisible(true);
				//viewpane.setResizeWeight(GuiConstants.GRAPH_PROPERTY_RATIO);
				viewpane.setDividerLocation(GuiConstants.GRAPH_PROPERTY_RATIO);
				
				// Buggy Swing Bugness
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						getContentPane().doLayout();
						viewpane.repaint();
						viewpane.setDividerLocation(GuiConstants.GRAPH_PROPERTY_RATIO);
						viewpane.repaint();
					}
				});
			}
		});
	}
	
	/**
	 *  Gets the selected style sheet.
	 */
	public mxStylesheet getSelectedSheet()
	{
		return (mxStylesheet) ((JRadioButtonMenuItem) SGuiHelper.getSelectedButton(stylegroup)).getClientProperty("sheet");
	}
	
	/**
	 *  Returns the select tool.
	 *  
	 *  @return The select tool.
	 */
	public JToggleButton getSelectTool()
	{
		return selecttool;
	}
}
