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
import jadex.gpmn.editor.model.gpmn.IModelCodec;
import jadex.gpmn.editor.model.visual.VNode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.xmlgraphics.java2d.GraphicContext;
import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.view.mxCellEditor;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxRectangle;
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
		super("Jadex GPMN Editor");// + VERSION);
		
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
						if (checkUnsaved())
						{
							System.exit(0);
						}
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
						if (checkUnsaved())
						{
							// TODO: Pick right sheet.
							GpmnGraph graph = new GpmnGraph(EditorWindow.this, new GpmnStylesheetColor());
							IGpmnModel gpmnmodel = mfactory.createModel();
							stylegroup.setSelected(colorview.getModel(), true);
							modelcontainer.setGpmnModel(gpmnmodel);
							modelcontainer.setGraph(graph);
							modelcontainer.getGraphComponent().refresh();
							modelcontainer.setDirty(false);
						}
					}
				});
				
				openitem.setAction(new AbstractAction("Open...")
				{
					public void actionPerformed(ActionEvent e)
					{
						BetterFileChooser fc = new BetterFileChooser();
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
								mxIGraphModel graphmodel = gpmnmodel.getModelCodec(IModelCodec.CODEC_TYPE_GPMN).readModel(file);
								
								// Funny, we need a new graph or we get quirky graphics (stuck selection marker)... Bug?
								GpmnGraph graph = new GpmnGraph(EditorWindow.this, new GpmnStylesheetColor());
								
								if (graphmodel == null)
								{
									graphmodel = gpmnmodel.generateGraphModel();
									graph.setModel(graphmodel);
									mxCompactTreeLayout layout = new mxCompactTreeLayout(graph);
									layout.setEdgeRouting(false);
									layout.setHorizontal(false);
									layout.setNodeDistance(GuiConstants.DEFAULT_GOAL_WIDTH);
									layout.execute(graph.getDefaultParent());
								}
								else
								{
									graph.setModel(graphmodel);
								}
								
								stylegroup.setSelected(colorview.getModel(), true);
								modelcontainer.setGpmnModel(gpmnmodel);
								modelcontainer.setGraph(graph);
								modelcontainer.getGraphComponent().refresh();
								setPropertPanel(SPropertyPanelFactory.createPanel(modelcontainer));
								
								modelcontainer.setFile(file);
							}
							catch (Exception e1)
							{
								e1.printStackTrace();
							}
						}
					}
				});
				
				saveitem.setAction(new AbstractAction("Save")
				{
					public void actionPerformed(ActionEvent e)
					{
						File file = modelcontainer.getFile();
						if (file == null)
						{
							BetterFileChooser fc = new BetterFileChooser();
							FileFilter filter = new FileNameExtensionFilter("GPMN intermediate model file", "gpmn");
							fc.addChoosableFileFilter(filter);
							fc.setFileFilter(filter);
							int result = fc.showSaveDialog(EditorWindow.this);
							if (JFileChooser.APPROVE_OPTION == result)
							{
								file = fc.getSelectedFile();
								if (!file.getName().endsWith(".gpmn"))
								{
									file = new File(file.getAbsolutePath() + ".gpmn");
								}
							}
						}
						
						try
						{
							modelcontainer.getGpmnModel().getModelCodec(IModelCodec.CODEC_TYPE_GPMN).writeModel(file, modelcontainer.getGraph());
							modelcontainer.setDirty(false);
							modelcontainer.setFile(file);
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
						}
					}
				});
				
				saveasitem.setAction(new AbstractAction("Save as...")
				{
					public void actionPerformed(ActionEvent e)
					{
						BetterFileChooser fc = new BetterFileChooser();
						FileFilter filter = new FileNameExtensionFilter("GPMN intermediate model file (*.gpmn)", "gpmn");
						fc.addChoosableFileFilter(filter);
						fc.setFileFilter(filter);
						filter = new FileNameExtensionFilter("Jadex BDI agent model file (*.agent.xml)", "agent.xml");
						fc.addChoosableFileFilter(filter);
						fc.setAcceptAllFileFilterUsed(false);
						int result = fc.showSaveDialog(EditorWindow.this);
						if (JFileChooser.APPROVE_OPTION == result)
						{
							try
							{
								FileNameExtensionFilter ef = (FileNameExtensionFilter) fc.getFileFilter();
								String ext = "." + ef.getExtensions()[0];
								File file = fc.getSelectedFile();
								if (!file.getName().endsWith(ext))
								{
									file = new File(file.getAbsolutePath() + ext);
								}
								IModelCodec codec = null;
								if (".gpmn".equals(ext))
								{
									codec = modelcontainer.getGpmnModel().getModelCodec(IModelCodec.CODEC_TYPE_GPMN);
								}
								else if (".agent.xml".equals(ext))
								{
									codec = modelcontainer.getGpmnModel().getModelCodec(IModelCodec.CODEC_TYPE_BDI);
								}
								codec.writeModel(file, modelcontainer.getGraph());
								modelcontainer.setDirty(false);
								modelcontainer.setFile(file);
							}
							catch (IOException e1)
							{
								e1.printStackTrace();
							}
						}
					}
				});
				
			    exportitem.setAction(new AbstractAction("Export...")
			    {
			        public void actionPerformed(ActionEvent e)
			        {

						BetterFileChooser fc = new BetterFileChooser();
						FileFilter filter = new FileNameExtensionFilter("EPS file", "eps");
						fc.addChoosableFileFilter(filter);
						fc.setFileFilter(filter);
						int result = fc.showSaveDialog(EditorWindow.this);
						if (JFileChooser.APPROVE_OPTION == result)
						{
							try
							{
								EPSDocumentGraphics2D g2d = new EPSDocumentGraphics2D(false);
								g2d.setGraphicContext(new GraphicContext());
								
								GpmnGraph graph = modelcontainer.getGraph();
								
								int x = Integer.MAX_VALUE;
								int y = Integer.MAX_VALUE;
								int w = 0;
								int h = 0;
								
								for (int i = 0; i < graph.getModel().getChildCount(graph.getDefaultParent()); ++i)
								{
									mxICell cell = (mxICell) graph.getModel().getChildAt(graph.getDefaultParent(), i);
									mxRectangle geo = graph.getCellBounds(cell);
									//mxGeometry geo = cell.getGeometry();
									if (geo.getX() < x)
									{
										x = (int) geo.getX();
									}
									if (geo.getY() < y)
									{
										y = (int) geo.getY();
									}
									if (geo.getX() + geo.getWidth() - x > w)
									{
										w = (int) Math.ceil(geo.getX() + geo.getWidth() - x);
									}
									if (geo.getY() + geo.getHeight() - y > h)
									{
										h = (int) Math.ceil(geo.getY() + geo.getHeight() - y);
									}
								}
								
								// Avoid cutting off shadows.
								w += 4;
								h += 4;
								
								File tmpfile = File.createTempFile("export", ".eps");
								//modelcontainer.getGraphComponent().paint(g)
								FileOutputStream fos = new FileOutputStream(tmpfile);
								g2d.setupDocument(fos, w, h);
								g2d.setClip(0, 0, w, h);
								g2d.translate(-x, -y);
								System.out.println(g2d.getClipBounds());
					        	modelcontainer.getGraphComponent().paint(g2d);
					        	g2d.finish();
					        	fos.close();
					        	tmpfile.renameTo(fc.getSelectedFile());
							}
							catch (IOException e1)
							{
								e1.printStackTrace();
							}
						}
			        }
			    });
				
				exititem.setAction(new AbstractAction("Exit")
				{
					public void actionPerformed(ActionEvent e)
					{
						if (checkUnsaved())
						{
							System.exit(0);
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
		selecttool = SGuiHelper.createTool(IViewAccess.SELECT_MODE, "select", "Select");
		toolgroup.add(selecttool);
		editingtools.add(selecttool);
		
		toolgroup.setSelected(selecttool.getModel(), true);
		
		JToggleButton tool = SGuiHelper.createTool(IViewAccess.CONTROL_POINT_MODE, "cpadd", "Add Control Point");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		editingtools.addSeparator();
		
		// Goal Tools
		tool = SGuiHelper.createTool(IViewAccess.ACHIEVE_GOAL_MODE, "agoal", "Add Achieve Goal");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		tool = SGuiHelper.createTool(IViewAccess.PERFORM_GOAL_MODE, "pgoal", "Add Perform Goal");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		tool = SGuiHelper.createTool(IViewAccess.MAINTAIN_GOAL_MODE, "mgoal", "Add Maintain Goal");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		tool = SGuiHelper.createTool(IViewAccess.QUERY_GOAL_MODE, "qgoal", "Add Query Goal");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		editingtools.addSeparator();
		
		// Plan Tools
		tool = SGuiHelper.createTool(IViewAccess.REF_PLAN_MODE, "plan", "Add Plan");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		tool = SGuiHelper.createTool(IViewAccess.ACTIVATION_PLAN_MODE, "actplan", "Add Activation Plan");
		toolgroup.add(tool);
		editingtools.add(tool);
		
		editingtools.addSeparator();
		
		// Edge Tools
		tool = SGuiHelper.createTool(IViewAccess.SUPPRESSION_EDGE_MODE, "suppedge", "Draw Suppression Edge");
		toolgroup.add(tool);
		editingtools.add(tool);
	}
	
	/**
	 *  Handles unsaved model deletions.
	 */
	protected boolean checkUnsaved()
	{
		boolean ret = true;
		if (modelcontainer.isDirty())
		{
			int result = JOptionPane.showConfirmDialog(EditorWindow.this, "The model contains unsaved changes, proceed anyway?", "Unsaved Changes",JOptionPane.YES_NO_OPTION);
	        switch(result)
	        {
	            case JOptionPane.NO_OPTION:
	                ret = false;
	            case JOptionPane.CLOSED_OPTION:
	                ret = false;
	            case JOptionPane.YES_OPTION:
	            default:
	        }
		}
		return ret;
	}
}
