package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.gui.controllers.DeletionController;
import jadex.gpmn.editor.gui.controllers.EdgeCreationController;
import jadex.gpmn.editor.gui.controllers.EdgeReconnectController;
import jadex.gpmn.editor.gui.controllers.FoldController;
import jadex.gpmn.editor.gui.controllers.MouseController;
import jadex.gpmn.editor.gui.controllers.SMenuControllerFactory;
import jadex.gpmn.editor.gui.controllers.ValueChangeController;
import jadex.gpmn.editor.gui.propertypanels.BasePropertyPanel;
import jadex.gpmn.editor.gui.stylesheets.GpmnStylesheetColor;
import jadex.gpmn.editor.gui.stylesheets.GpmnStylesheetGrayscale;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;
import jadex.gpmn.editor.model.visual.SequentialMarker;
import jadex.gpmn.editor.model.visual.VGoal;
import jadex.gpmn.editor.model.visual.VNode;
import jadex.gpmn.editor.model.visual.VPlan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

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

import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.view.mxCellEditor;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxStylesheet;

public class EditorWindow extends JFrame implements IControllerAccess
{
	public static String VERSION = "1.0";
	
	/** The model factory. */
	protected IGpmnModelFactory mfactory;
	
	/** The model. */
	//protected IGpmnModel gpmnmodel;
	
	/** The pane containing the graph and the property view. */
	protected JSplitPane viewpane;
	
	/** The graph component. */
	//protected mxGraphComponent graphcomponent;
	
	/** The current graph. */
	//protected GpmnGraph graph;
	
	/** The model container */
	protected IModelContainer modelcontainer;
	
	/** The group of tools. */
	protected ButtonGroup toolgroup;
	
	/** The select tool. */
	protected JToggleButton selecttool;
	
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
				
				GpmnGraph graph = createGraph();
				IGpmnModel gpmnmodel = mfactory.createModel();
				
				//Graph<VNode, VEdge> g = new DirectedSparseMultigraph<VNode, VEdge>();
				//g.addVertex(new VNode());
				//VNode node = new VNode(new Goal());
				//g.addVertex(new VNode());
				//graph.setCellStyle(vgoal.getStyle(), new Object[]{vgoal});
				mxGraphComponent graphcomponent = new GpmnGraphComponent(graph);
				graphcomponent.setDragEnabled(false);
				graphcomponent.setPanning(true);
				graphcomponent.setCenterZoom(false);
				graphcomponent.setAutoscrolls(true);
				graphcomponent.setAutoExtend(true);
				graphcomponent.getViewport().setOpaque(false);
				graphcomponent.setBackground(Color.WHITE);
				graphcomponent.setOpaque(true);
				graphcomponent.setTextAntiAlias(true);
				//new mxEdgeHandler(graphcomponent, null).;
				graphcomponent.setCellEditor(new mxCellEditor(graphcomponent)
				{
					protected boolean useLabelBounds(mxCellState state)
					{
						// Hack to edit label at label position.
						return super.useLabelBounds(state) || state.getCell() instanceof VNode;
					}
				});
				
				modelcontainer = new ModelContainer(graphcomponent, gpmnmodel);
				
				graphcomponent.getConnectionHandler().addListener(mxEvent.CONNECT,
						new EdgeCreationController(modelcontainer, EditorWindow.this));
				
				deletioncontroller = new DeletionController(modelcontainer);
				synchModels();
				
				MouseController mc = new MouseController(modelcontainer, EditorWindow.this);
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
				
				//JScrollPane gcsp = new JScrollPane(graphcomponent);
				//graphpane.setTopComponent(gcsp);
				viewpane.setTopComponent(graphcomponent);
				
				setPropertPanel(SPropertyPanelFactory.EMPTY_PANEL);
				
				/* Menu */
				JMenuBar menubar = new JMenuBar();
				
				JMenu filemenu = new JMenu("File");
				JMenuItem newitem = new JMenuItem("New");
				JMenuItem openitem = new JMenuItem();
				JMenuItem saveasitem = new JMenuItem();
				JMenuItem exititem = new JMenuItem("Exit");
				filemenu.add(newitem);
				filemenu.add(openitem);
				filemenu.add(saveasitem);
				filemenu.addSeparator();
				filemenu.add(exititem);
				menubar.add(filemenu);
				
				openitem.setAction(new AbstractAction("Open...")
				{
					public void actionPerformed(ActionEvent e)
					{
						JFileChooser fc = new JFileChooser();
						int result = fc.showOpenDialog(EditorWindow.this);
						if (JFileChooser.APPROVE_OPTION == result)
						{
							try
							{
								GpmnGraph graph = createGraph();
								IGpmnModel gpmnmodel = mfactory.createModel();
								gpmnmodel.getModelCodec().readModel(fc.getSelectedFile(), graph);
								modelcontainer.setGpmnModel(gpmnmodel);
								modelcontainer.setGraph(graph);
								modelcontainer.getGraphComponent().refresh();
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
						int result = fc.showSaveDialog(EditorWindow.this);
						if (JFileChooser.APPROVE_OPTION == result)
						{
							try
							{
								modelcontainer.getGpmnModel().getModelCodec().writeModel(fc.getSelectedFile(), modelcontainer.getGraph());
							}
							catch (IOException e1)
							{
								e1.printStackTrace();
							}
						}
					}
				});
				
				JMenu viewmenu = new JMenu("View");
				
				/* Styles */
				ButtonGroup stylegroup = new ButtonGroup();
				JRadioButtonMenuItem colorview = new JRadioButtonMenuItem(SMenuControllerFactory.createStyleController(modelcontainer, new GpmnStylesheetColor()));
				colorview.setSelected(true);
				colorview.setText("Color");
				stylegroup.add(colorview);
				viewmenu.add(colorview);
				JRadioButtonMenuItem grayview = new JRadioButtonMenuItem(SMenuControllerFactory.createStyleController(modelcontainer, new GpmnStylesheetGrayscale()));
				grayview.setText("Grayscale");
				stylegroup.add(grayview);
				viewmenu.add(grayview);
				menubar.add(viewmenu);
				
				setJMenuBar(menubar);
				
				
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
	 *  Sets the select tool as the current tool.
	 *  
	 */
	public void setSelectTool()
	{
		toolgroup.setSelected(selecttool.getModel(), true);
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
		selecttool = new JToggleButton();
		selecttool.getModel().setActionCommand(IModelContainer.SELECT_MODE);
		selecttool.setContentAreaFilled(false);
		selecttool.setPressedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/select_on.png")));
		selecttool.setSelectedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/select_on.png")));
		selecttool.setIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/select_off.png")));
		selecttool.setBorder(new EmptyBorder(0, 0, 0, 0));
		selecttool.setMargin(new Insets(0, 0, 0, 0));
		selecttool.setToolTipText("Select");
		toolgroup.add(selecttool);
		editingtools.add(selecttool);
		
		toolgroup.setSelected(selecttool.getModel(), true);
		
		JToggleButton tool = new JToggleButton();
		tool.getModel().setActionCommand(IModelContainer.CONTROL_POINT_MODE);
		tool.setContentAreaFilled(false);
		tool.setPressedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/cpadd_on.png")));
		tool.setSelectedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/cpadd_on.png")));
		tool.setIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/cpadd_off.png")));
		tool.setBorder(new EmptyBorder(0, 0, 0, 0));
		tool.setMargin(new Insets(0, 0, 0, 0));
		tool.setToolTipText("Add Control Point");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		editingtools.addSeparator();
		
		// Goal Tools
		tool = new JToggleButton();
		tool.getModel().setActionCommand(IModelContainer.ACHIEVE_GOAL_MODE);
		tool.setContentAreaFilled(false);
		tool.setPressedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/agoal_on.png")));
		tool.setSelectedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/agoal_on.png")));
		tool.setIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/agoal_off.png")));
		tool.setBorder(new EmptyBorder(0, 0, 0, 0));
		tool.setMargin(new Insets(0, 0, 0, 0));
		tool.setToolTipText("Add Achieve Goal");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		tool = new JToggleButton();
		tool.getModel().setActionCommand(IModelContainer.PERFORM_GOAL_MODE);
		tool.setContentAreaFilled(false);
		tool.setPressedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/pgoal_on.png")));
		tool.setSelectedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/pgoal_on.png")));
		tool.setIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/pgoal_off.png")));
		tool.setBorder(new EmptyBorder(0, 0, 0, 0));
		tool.setMargin(new Insets(0, 0, 0, 0));
		tool.setToolTipText("Add Perform Goal");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		tool = new JToggleButton();
		tool.getModel().setActionCommand(IModelContainer.MAINTAIN_GOAL_MODE);
		tool.setContentAreaFilled(false);
		tool.setPressedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/mgoal_on.png")));
		tool.setSelectedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/mgoal_on.png")));
		tool.setIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/mgoal_off.png")));
		tool.setBorder(new EmptyBorder(0, 0, 0, 0));
		tool.setMargin(new Insets(0, 0, 0, 0));
		tool.setToolTipText("Add Maintain Goal");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		tool = new JToggleButton();
		tool.getModel().setActionCommand(IModelContainer.QUERY_GOAL_MODE);
		tool.setContentAreaFilled(false);
		tool.setPressedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/qgoal_on.png")));
		tool.setSelectedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/qgoal_on.png")));
		tool.setIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/qgoal_off.png")));
		tool.setBorder(new EmptyBorder(0, 0, 0, 0));
		tool.setMargin(new Insets(0, 0, 0, 0));
		tool.setToolTipText("Add Query Goal");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		editingtools.addSeparator();
		
		// Plan Tools
		tool = new JToggleButton();
		tool.getModel().setActionCommand(IModelContainer.BPMN_PLAN_MODE);
		tool.setContentAreaFilled(false);
		tool.setPressedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/bpmnplan_on.png")));
		tool.setSelectedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/bpmnplan_on.png")));
		tool.setIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/bpmnplan_off.png")));
		tool.setBorder(new EmptyBorder(0, 0, 0, 0));
		tool.setMargin(new Insets(0, 0, 0, 0));
		tool.setToolTipText("Add BPMN Plan");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		tool = new JToggleButton();
		tool.getModel().setActionCommand(IModelContainer.ACTIVATION_PLAN_MODE);
		tool.setContentAreaFilled(false);
		tool.setPressedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/actplan_on.png")));
		tool.setSelectedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/actplan_on.png")));
		tool.setIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/actplan_off.png")));
		tool.setBorder(new EmptyBorder(0, 0, 0, 0));
		tool.setMargin(new Insets(0, 0, 0, 0));
		tool.setToolTipText("Add Activation Plan");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		editingtools.addSeparator();
		
		// Edge Tools
		tool = new JToggleButton();
		tool.getModel().setActionCommand(IModelContainer.SUPPRESSION_EDGE_MODE);
		tool.setContentAreaFilled(false);
		tool.setPressedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/suppedge_on.png")));
		tool.setSelectedIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/suppedge_on.png")));
		tool.setIcon(new ImageIcon(getClass().getResource("/" + getClass().getPackage().getName().replaceAll("\\.", "/") + "/images/suppedge_off.png")));
		tool.setBorder(new EmptyBorder(0, 0, 0, 0));
		tool.setMargin(new Insets(0, 0, 0, 0));
		tool.setToolTipText("Add Suppression Edge");
		toolgroup.add(tool);
		editingtools.add(tool);
	}
	
	protected GpmnGraph createGraph()
	{
		final GpmnGraph graph = new GpmnGraph();
		
		graph.getModel().addListener(mxEvent.EXECUTE, new ValueChangeController(modelcontainer));
		
		graph.getSelectionModel().addListener(mxEvent.CHANGE, new mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				//TODO: JGraphX Bug! added and removed are switched.
				String removed = "added";
				String added = "removed";
				
				if (evt.getProperty(added) != null)
				{
					boolean nodes = false;
					List addedcells = (List) evt.getProperty(added);
					for (int i = 0; i < addedcells.size(); ++i)
					{
						if (!nodes && addedcells.get(i) instanceof VNode)
						{
							nodes = true;
						}
						else if ((addedcells.get(i) instanceof VGoal.VGoalType) ||
								 (addedcells.get(i) instanceof VPlan.VPlanType) ||
								 (addedcells.get(i) instanceof SequentialMarker))
						{
							mxICell marker = (mxICell) addedcells.get(i);
							mxICell parent = marker.getParent();
							graph.removeSelectionCell(marker);
							graph.addSelectionCell(parent);
						}
					}
					
					if (nodes && !IModelContainer.SELECT_MODE.equals(toolgroup.getSelection().getActionCommand()))
					{
						setSelectTool();
					}
				}
				else if (evt.getProperty(removed) != null && graph.getSelectionCount() == 0)
				{
					//TODO: Add context panel.
					setPropertPanel(SPropertyPanelFactory.EMPTY_PANEL);
				}
				
				if (graph.getSelectionCount() == 1)
				{
					setPropertPanel(SPropertyPanelFactory.createPanel(graph));
				}
			}
		});
		
		graph.addListener(mxEvent.CONNECT_CELL, new EdgeReconnectController(modelcontainer, EditorWindow.this));
		
		foldcontroller = new FoldController(modelcontainer, EditorWindow.this);
		graph.addListener(mxEvent.CELLS_FOLDED, foldcontroller);
		
		mxStylesheet defaultsheet = new GpmnStylesheetColor();
		graph.setStylesheet(defaultsheet);
		
		return graph;
	}
	
	protected void setPropertPanel(BasePropertyPanel panel)
	{
		int loc = viewpane.getDividerLocation();
		viewpane.setBottomComponent(panel);
		viewpane.setDividerLocation(loc);
	}
}
