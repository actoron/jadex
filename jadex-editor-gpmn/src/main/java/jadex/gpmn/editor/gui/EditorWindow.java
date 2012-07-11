package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.gui.controllers.DeletionController;
import jadex.gpmn.editor.gui.controllers.EdgeCreationController;
import jadex.gpmn.editor.gui.controllers.EdgeReconnectController;
import jadex.gpmn.editor.gui.controllers.FoldController;
import jadex.gpmn.editor.gui.controllers.MouseController;
import jadex.gpmn.editor.gui.controllers.SControllerActions;
import jadex.gpmn.editor.gui.controllers.SelectionController;
import jadex.gpmn.editor.gui.controllers.ValueChangeController;
import jadex.gpmn.editor.gui.propertypanels.BasePropertyPanel;
import jadex.gpmn.editor.gui.stylesheets.GpmnStylesheetColor;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;
import jadex.gpmn.editor.model.visual.VNode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.view.mxCellEditor;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxStylesheet;

public class EditorWindow extends JFrame implements IControllerAccess, IViewAccess
{
	public static String VERSION = "0.9";
	
	/** The model factory. */
	//protected IGpmnModelFactory mfactory;
	
	/** The pane containing the graph and the property view. */
	protected JSplitPane viewpane;
	
	/** The model container */
	protected IModelContainer modelcontainer;
	
	/** The group of tools. */
	protected ButtonGroup toolgroup;
	
	/** The select tool. */
	protected JToggleButton selecttool;
	
	/** The group of styles. */
	protected ButtonGroup stylegroup;
	
	/** The value change controller. */
	protected ValueChangeController valuechangecontroller;
	
	/** The selection controller. */
	protected SelectionController selectioncontroller;
	
	/** The edge reconnect controller. */
	protected EdgeReconnectController edgereconnectcontroller;
	
	/** The edge creation controller. */
	protected EdgeCreationController edgecreationcontroller;
	
	/** The fold controller */
	protected FoldController foldcontroller;
	
	/** The deletion controller */
	protected DeletionController deletioncontroller;
	
	public EditorWindow()
	{
		super("Jadex GPMN Editor");// + VERSION);
		
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
						SControllerActions.exit(EditorWindow.this, modelcontainer);
					}
				});
				
				JToolBar editingtools = new JToolBar();
				toolgroup = new ButtonGroup();
				editingtools.setFloatable(false);
				
				addTools(editingtools);
				
				getContentPane().add(editingtools, BorderLayout.PAGE_START);
				
				getContentPane().add(viewpane, BorderLayout.CENTER);
				
				modelcontainer = new ModelContainer(new GpmnIntermediateModelFactory(), null, null);
				
				valuechangecontroller = new ValueChangeController(modelcontainer);
				selectioncontroller = new SelectionController(modelcontainer, EditorWindow.this);
				edgereconnectcontroller = new EdgeReconnectController(modelcontainer, EditorWindow.this);
				foldcontroller = new FoldController(modelcontainer, EditorWindow.this);
				GpmnGraph graph = new GpmnGraph(EditorWindow.this, new GpmnStylesheetColor());
				
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
				
				edgecreationcontroller = new EdgeCreationController(modelcontainer, EditorWindow.this);
				graphcomponent.getConnectionHandler().addListener(mxEvent.CONNECT,
						edgecreationcontroller);
				
				deletioncontroller = new DeletionController(modelcontainer);
				synchModels();
				
				MouseController mc = new MouseController(modelcontainer, EditorWindow.this, EditorWindow.this);
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
				
				/* Menu */
				JMenuBar menubar = new JMenuBar();
				
				JMenu filemenu = new JMenu("File");
				JMenuItem newitem = new JMenuItem("");
				JMenuItem openitem = new JMenuItem();
				JMenuItem saveitem = new JMenuItem();
				JMenuItem saveasitem = new JMenuItem();
				JMenuItem exportitem = new JMenuItem();
				JMenuItem exititem = new JMenuItem();
				filemenu.add(newitem);
				filemenu.add(openitem);
				filemenu.addSeparator();
				filemenu.add(saveitem);
				filemenu.add(saveasitem);
				filemenu.add(exportitem);
				filemenu.addSeparator();
				filemenu.add(exititem);
				menubar.add(filemenu);
				
				
				JMenu viewmenu = new JMenu("View");
				menubar.add(viewmenu);
				
				/* Styles */
				JMenu stylemenu = new JMenu("Styles");
				stylegroup = new ButtonGroup();
				Action styleaction = new AbstractAction()
				{
					public void actionPerformed(ActionEvent e)
					{
						SControllerActions.setStyle(modelcontainer, (mxStylesheet) ((JRadioButtonMenuItem) e.getSource()).getClientProperty("sheet"));
					}
				};
				final JRadioButtonMenuItem colorview = new JRadioButtonMenuItem(styleaction);
				colorview.putClientProperty("sheet", IViewAccess.COLOR_STYLESHEET);
				colorview.setSelected(true);
				colorview.setText("Color");
				stylegroup.add(colorview);
				stylemenu.add(colorview);
				final JRadioButtonMenuItem grayview = new JRadioButtonMenuItem(styleaction);
				grayview.putClientProperty("sheet", IViewAccess.GS_STYLESHEET);
				grayview.setText("Grayscale");
				stylegroup.add(grayview);
				stylemenu.add(grayview);
				viewmenu.add(stylemenu);
				
				JMenu layoutmenu = new JMenu("Layouts");
				viewmenu.add(layoutmenu);
				
				JMenuItem circlelayoutitem = new JMenuItem(new AbstractAction("Circle")
				{
					
					public void actionPerformed(ActionEvent e)
					{
						SControllerActions.applyCircleLayout(modelcontainer);
					}
				});
				layoutmenu.add(circlelayoutitem);
				
				JMenuItem organiclayoutitem = new JMenuItem(new AbstractAction("Organic")
				{
					
					public void actionPerformed(ActionEvent e)
					{
						SControllerActions.applyOrganicLayout(modelcontainer);
					}
				});
				layoutmenu.add(organiclayoutitem);
				
				JMenuItem treelayoutitem = new JMenuItem(new AbstractAction("Tree")
				{
					
					public void actionPerformed(ActionEvent e)
					{
						SControllerActions.applyTreeLayout(modelcontainer);
					}
				});
				layoutmenu.add(treelayoutitem);
				
				newitem.setAction(new AbstractAction("New")
				{
					public void actionPerformed(ActionEvent e)
					{
						EditorWindow ew = EditorWindow.this;
						SControllerActions.newModel(ew, ew, ew, modelcontainer);
					}
				});
				
				openitem.setAction(new AbstractAction("Open...")
				{
					public void actionPerformed(ActionEvent e)
					{
						EditorWindow ew = EditorWindow.this;
						SControllerActions.openModel(ew, ew, ew, modelcontainer);
					}
				});
				
				saveitem.setAction(new AbstractAction("Save")
				{
					public void actionPerformed(ActionEvent e)
					{
						SControllerActions.saveModel(EditorWindow.this, modelcontainer);
					}
				});
				
				saveasitem.setAction(new AbstractAction("Save as...")
				{
					public void actionPerformed(ActionEvent e)
					{
						SControllerActions.saveAsModel(EditorWindow.this, modelcontainer);
					}
				});
				
			    exportitem.setAction(new AbstractAction("Export...")
			    {
			        public void actionPerformed(ActionEvent e)
			        {
			        	SControllerActions.exportModel(EditorWindow.this, modelcontainer);
			        }
			    });
				
				exititem.setAction(new AbstractAction("Exit")
				{
					public void actionPerformed(ActionEvent e)
					{
						SControllerActions.exit(EditorWindow.this, modelcontainer);
					}
				});
				
				setJMenuBar(menubar);
				
				setPropertyPanel(SPropertyPanelFactory.createPanel(modelcontainer));
				
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
	
	/** 
	 * Returns the current edit mode
	 * 
	 * @return the edit mode.
	 */
	public String getEditMode()
	{
		return toolgroup.getSelection().getActionCommand();
	}
	
	/**
	 *  Gets the selected style sheet.
	 */
	public mxStylesheet getSelectedSheet()
	{
		return (mxStylesheet) ((JRadioButtonMenuItem) SGuiHelper.getSelectedButton(stylegroup)).getClientProperty("sheet");
	}
	
	/**
	 *  Returns the tool group.
	 *  
	 *  @return The tool group.
	 */
	public ButtonGroup getToolGroup()
	{
		return toolgroup;
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
	
	public void setPropertyPanel(BasePropertyPanel panel)
	{
		int loc = viewpane.getDividerLocation();
		viewpane.setBottomComponent(panel);
		viewpane.setDividerLocation(loc);
	}
	
	/**
	 *  Disables deletion controller,
	 *  desynchronizing visual and business model.
	 */
	public void desynchModels()
	{
		modelcontainer.getGraph().removeListener(deletioncontroller, mxEvent.REMOVE_CELLS);
	}
	
	/**
	 *  Enables deletion controller,
	 *  synchronizing visual and business model.
	 */
	public void synchModels()
	{
		modelcontainer.getGraph().addListener(mxEvent.REMOVE_CELLS, deletioncontroller);
	}
	
	/**
	 *  Returns the controller for handling value changes.
	 *   
	 *  @return The controller.
	 */
	public ValueChangeController getValueChangeController()
	{
		return valuechangecontroller;
	}
	
	/**
	 *  Returns the controller for handling selections.
	 *   
	 *  @return The controller.
	 */
	public SelectionController getSelectionController()
	{
		return selectioncontroller;
	}
	
	/**
	 *  Returns the controller for handling edge reconnects.
	 *   
	 *  @return The controller.
	 */
	public EdgeReconnectController getEdgeReconnectController()
	{
		return edgereconnectcontroller;
	}
	
	/**
	 *  Returns the controller for handling edge creation.
	 *   
	 *  @return The controller.
	 */
	public EdgeCreationController getEdgeCreationController()
	{
		return edgecreationcontroller;
	}
	
	/**
	 *  Returns the controller for folding.
	 *   
	 *  @return Fold controller.
	 */
	public FoldController getFoldController()
	{
		return foldcontroller;
	}
	
	public static void main(String[] args)
	{
		new EditorWindow();
	}
	
	protected void addTools(JToolBar editingtools)
	{
		ImageProvider imgprovider = new ImageProvider();
		//long time = System.currentTimeMillis();
		// Selections
		//selecttool = SGuiHelper.createTool(IViewAccess.SELECT_MODE, "select", "Select");
		selecttool = SGuiHelper.createTool(imgprovider, IViewAccess.SELECT_MODE, mxUtils.parseColor(GuiConstants.SELECT_COLOR), "selectsym", "Select", false, true);
		toolgroup.add(selecttool);
		editingtools.add(selecttool);
		
		toolgroup.setSelected(selecttool.getModel(), true);
		
		//JToggleButton tool = SGuiHelper.createTool(IViewAccess.CONTROL_POINT_MODE, "cpadd", "Add Control Point");
		JToggleButton tool = SGuiHelper.createTool(imgprovider, IViewAccess.SELECT_MODE, mxUtils.parseColor(GuiConstants.CONTROL_POINT_COLOR), "cpsym", "Add Control Point", false, true);
		toolgroup.add(tool);
		editingtools.add(tool);
		
		editingtools.addSeparator();
		
		// Goal Tools
		tool = SGuiHelper.createTool(imgprovider, IViewAccess.ACHIEVE_GOAL_MODE, mxUtils.parseColor(GuiConstants.ACHIEVE_GOAL_COLOR), "A", "Add Achieve Goal", true, false);
		toolgroup.add(tool);
		editingtools.add(tool);
		
		tool = SGuiHelper.createTool(imgprovider, IViewAccess.PERFORM_GOAL_MODE,  mxUtils.parseColor(GuiConstants.PERFORM_GOAL_COLOR), "P", "Add Perform Goal", true, false);
		toolgroup.add(tool);
		editingtools.add(tool);
		
		tool = SGuiHelper.createTool(imgprovider, IViewAccess.MAINTAIN_GOAL_MODE,  mxUtils.parseColor(GuiConstants.MAINTAIN_GOAL_COLOR), "M", "Add Maintain Goal", true, false);
		toolgroup.add(tool);
		editingtools.add(tool);
		
		tool = SGuiHelper.createTool(imgprovider, IViewAccess.QUERY_GOAL_MODE,  mxUtils.parseColor(GuiConstants.QUERY_GOAL_COLOR), "Q", "Add Query Goal", true, false);
		toolgroup.add(tool);
		editingtools.add(tool);
		
		editingtools.addSeparator();
		
		// Plan Tools
		//tool = SGuiHelper.createTool(IViewAccess.REF_PLAN_MODE, "plan", "Add Plan");
		tool = SGuiHelper.createTool(imgprovider, IViewAccess.REF_PLAN_MODE, mxUtils.parseColor(GuiConstants.REF_PLAN_COLOR), "P", "Add Plan", false, false);
		toolgroup.add(tool);
		editingtools.add(tool);
		
		//tool = SGuiHelper.createTool(IViewAccess.ACTIVATION_PLAN_MODE, "actplan", "Add Activation Plan");
		tool = SGuiHelper.createTool(imgprovider, IViewAccess.ACTIVATION_PLAN_MODE, mxUtils.parseColor(GuiConstants.ACTIVATION_PLAN_COLOR), "A", "Add Activation Plan", false, false);
		toolgroup.add(tool);
		editingtools.add(tool);
		
		editingtools.addSeparator();
		
		// Edge Tools
		tool = SGuiHelper.createTool(imgprovider, IViewAccess.SUPPRESSION_EDGE_MODE, mxUtils.parseColor(GuiConstants.SUPPRESSION_EDGE_COLOR), "suppsym", "Draw Suppression Edge", false, true);
		//tool = SGuiHelper.createTool(IViewAccess.SUPPRESSION_EDGE_MODE, "suppedge", "Draw Suppression Edge");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		//System.out.println(System.currentTimeMillis() - time);
	}
}
