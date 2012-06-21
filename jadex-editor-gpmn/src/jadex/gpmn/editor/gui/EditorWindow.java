package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.gui.controllers.DeletionController;
import jadex.gpmn.editor.gui.controllers.EdgeCreationController;
import jadex.gpmn.editor.gui.controllers.EdgeReconnectController;
import jadex.gpmn.editor.gui.controllers.FoldController;
import jadex.gpmn.editor.gui.controllers.MouseController;
import jadex.gpmn.editor.gui.controllers.SMenuControllerFactory;
import jadex.gpmn.editor.gui.controllers.SelectionController;
import jadex.gpmn.editor.gui.controllers.ValueChangeController;
import jadex.gpmn.editor.gui.propertypanels.BasePropertyPanel;
import jadex.gpmn.editor.gui.stylesheets.GpmnStylesheetColor;
import jadex.gpmn.editor.gui.stylesheets.GpmnStylesheetGrayscale;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;
import jadex.gpmn.editor.model.visual.VNode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.view.mxCellEditor;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxCellState;

public class EditorWindow extends JFrame implements IControllerAccess, IViewAccess
{
	public static String VERSION = "0.9";
	
	/** The model factory. */
	protected IGpmnModelFactory mfactory;
	
	/** The pane containing the graph and the property view. */
	protected JSplitPane viewpane;
	
	/** The model container */
	protected IModelContainer modelcontainer;
	
	/** The group of tools. */
	protected ButtonGroup toolgroup;
	
	/** The select tool. */
	protected JToggleButton selecttool;
	
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
		super("Jadex GPMN Editor " + VERSION);
		
		// TODO: Pick and choose factory.
		mfactory = new GpmnIntermediateModelFactory();
		
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
				
				JToolBar editingtools = new JToolBar();
				toolgroup = new ButtonGroup();
				editingtools.setFloatable(false);
				
				addTools(editingtools);
				
				getContentPane().add(editingtools, BorderLayout.PAGE_START);
				
				getContentPane().add(viewpane, BorderLayout.CENTER);
				
				modelcontainer = new ModelContainer(null, null);
				
				valuechangecontroller = new ValueChangeController(modelcontainer);
				selectioncontroller = new SelectionController(modelcontainer, EditorWindow.this);
				edgereconnectcontroller = new EdgeReconnectController(modelcontainer, EditorWindow.this);
				foldcontroller = new FoldController(modelcontainer, EditorWindow.this);
				GpmnGraph graph = new GpmnGraph(EditorWindow.this, new GpmnStylesheetColor());
				
				IGpmnModel gpmnmodel = mfactory.createModel();
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
				JMenuItem saveasitem = new JMenuItem();
				JMenuItem exititem = new JMenuItem("Exit");
				filemenu.add(newitem);
				filemenu.add(openitem);
				filemenu.add(saveasitem);
				filemenu.addSeparator();
				filemenu.add(exititem);
				menubar.add(filemenu);
				
				
				JMenu viewmenu = new JMenu("View");
				/* Styles */
				final ButtonGroup stylegroup = new ButtonGroup();
				final JRadioButtonMenuItem colorview = new JRadioButtonMenuItem(SMenuControllerFactory.createStyleController(modelcontainer, new GpmnStylesheetColor()));
				colorview.setSelected(true);
				colorview.setText("Color");
				stylegroup.add(colorview);
				viewmenu.add(colorview);
				final JRadioButtonMenuItem grayview = new JRadioButtonMenuItem(SMenuControllerFactory.createStyleController(modelcontainer, new GpmnStylesheetGrayscale()));
				grayview.setText("Grayscale");
				stylegroup.add(grayview);
				viewmenu.add(grayview);
				menubar.add(viewmenu);
				
				newitem.setAction(new AbstractAction("New")
				{
					public void actionPerformed(ActionEvent e)
					{
						// TODO: Pick right sheet.
						GpmnGraph graph = new GpmnGraph(EditorWindow.this, new GpmnStylesheetColor());
						IGpmnModel gpmnmodel = mfactory.createModel();
						stylegroup.setSelected(colorview.getModel(), true);
						modelcontainer.setGpmnModel(gpmnmodel);
						modelcontainer.setGraph(graph);
						modelcontainer.getGraphComponent().refresh();
					}
				});
				
				openitem.setAction(new AbstractAction("Open...")
				{
					public void actionPerformed(ActionEvent e)
					{
						JFileChooser fc = new JFileChooser();
						FileFilter filter = new FileNameExtensionFilter("GPMN intermediate model file", "gpmn");
						fc.addChoosableFileFilter(filter);
						fc.setFileFilter(filter);
						int result = fc.showOpenDialog(EditorWindow.this);
						if (JFileChooser.APPROVE_OPTION == result)
						{
							try
							{
								IGpmnModel gpmnmodel = mfactory.createModel();
								File file = fc.getSelectedFile();
								if (!file.getName().endsWith(".gpmn"))
								{
									file = new File(file.getAbsolutePath() + ".gpmn");
								}
								mxIGraphModel graphmodel = gpmnmodel.getModelCodec().readModel(file);
								
								// Funny, we need a new graph or we get quirky graphics... Bug?
								GpmnGraph graph = new GpmnGraph(EditorWindow.this, new GpmnStylesheetColor());
								graph.setModel(graphmodel);
								stylegroup.setSelected(colorview.getModel(), true);
								modelcontainer.setGpmnModel(gpmnmodel);
								modelcontainer.setGraph(graph);
								modelcontainer.getGraphComponent().refresh();
								setPropertPanel(SPropertyPanelFactory.createPanel(modelcontainer));
							}
							catch (Exception e1)
							{
								e1.printStackTrace();
							}
						}
					}
				});
				
				saveasitem.setAction(new AbstractAction("Save as...")
				{
					public void actionPerformed(ActionEvent e)
					{
						JFileChooser fc = new JFileChooser();
						FileFilter filter = new FileNameExtensionFilter("GPMN intermediate model file", "gpmn");
						fc.addChoosableFileFilter(filter);
						fc.setFileFilter(filter);
						int result = fc.showSaveDialog(EditorWindow.this);
						if (JFileChooser.APPROVE_OPTION == result)
						{
							try
							{
								File file = fc.getSelectedFile();
								if (!file.getName().endsWith(".gpmn"))
								{
									file = new File(file.getAbsolutePath() + ".gpmn");
								}
								modelcontainer.getGpmnModel().getModelCodec().writeModel(file, modelcontainer.getGraph());
							}
							catch (IOException e1)
							{
								e1.printStackTrace();
							}
						}
					}
				});
				
				setJMenuBar(menubar);
				
				setPropertPanel(SPropertyPanelFactory.createPanel(modelcontainer));
				
				pack();
				Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
				setSize((int) (sd.width * 0.7), (int) (sd.height * 0.7));
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
	
	public void setPropertPanel(BasePropertyPanel panel)
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
		// Selections
		selecttool = createTool(IViewAccess.SELECT_MODE, "select_on.png", "select_off.png", "Select");
		toolgroup.add(selecttool);
		editingtools.add(selecttool);
		
		toolgroup.setSelected(selecttool.getModel(), true);
		
		JToggleButton tool = createTool(IViewAccess.CONTROL_POINT_MODE, "cpadd_on.png", "cpadd_off.png", "Add Control Point");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		editingtools.addSeparator();
		
		// Goal Tools
		tool = createTool(IViewAccess.ACHIEVE_GOAL_MODE, "agoal_on.png", "agoal_off.png", "Add Achieve Goal");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		tool = createTool(IViewAccess.PERFORM_GOAL_MODE, "pgoal_on.png", "pgoal_off.png", "Add Perform Goal");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		tool = createTool(IViewAccess.MAINTAIN_GOAL_MODE, "mgoal_on.png", "mgoal_off.png", "Add Maintain Goal");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		tool = createTool(IViewAccess.QUERY_GOAL_MODE, "qgoal_on.png", "qgoal_off.png", "Add Query Goal");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		editingtools.addSeparator();
		
		// Plan Tools
		tool = createTool(IViewAccess.BPMN_PLAN_MODE, "bpmnplan_on.png", "bpmnplan_off.png", "Add BPMN Plan");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		tool = createTool(IViewAccess.ACTIVATION_PLAN_MODE, "actplan_on.png", "actplan_off.png", "Add Activation Plan");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		editingtools.addSeparator();
		
		// Edge Tools
		tool = createTool(IViewAccess.SUPPRESSION_EDGE_MODE, "suppedge_on.png", "suppedge_off.png", "Draw Suppression Edge");
		toolgroup.add(tool);
		editingtools.add(tool);
	}
	
	protected JToggleButton createTool(String mode, String onimage, String offimage, String tooltip)
	{
		JToggleButton tool = new JToggleButton();
		tool.getModel().setActionCommand(mode);
		tool.setContentAreaFilled(false);
		tool.setPressedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/" + onimage)));
		tool.setSelectedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/" + onimage)));
		tool.setIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/" + offimage)));
		tool.setBorder(new EmptyBorder(0, 0, 0, 0));
		tool.setMargin(new Insets(0, 0, 0, 0));
		tool.setToolTipText("Add Suppression Edge");
		return tool;
	}
}
