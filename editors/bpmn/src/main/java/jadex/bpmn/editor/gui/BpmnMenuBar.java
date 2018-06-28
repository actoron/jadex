package jadex.bpmn.editor.gui;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.xmlgraphics.java2d.GraphicContext;
import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;
import org.w3c.dom.Document;

import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxStylesheet;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bpmn.editor.gui.controllers.SCreationController;
import jadex.bpmn.editor.gui.propertypanels.BasePropertyPanel;
import jadex.bpmn.editor.model.visual.BpmnVisualModelWriter;
import jadex.bpmn.editor.model.visual.VEdge;
import jadex.bpmn.editor.model.visual.VElement;
import jadex.bpmn.editor.model.visual.VMessagingEdge;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.io.SBpmnModelWriter;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.future.SwingResultListener;

public class BpmnMenuBar extends JMenuBar
{
	/** The editor window. */
	protected BpmnEditorWindow editorwindow;
	protected Tuple2<BpmnGraph, List<VElement>> copycells;
	/**
	 *  Creates the menu bar.
	 *  
	 *  @param editwindow The editor window.
	 */
	public BpmnMenuBar(BpmnEditorWindow editwindow)
	{
		
		this.editorwindow = editwindow;
		
		JMenu filemenu = new JMenu(BpmnEditor.getString("File"));
		
		filemenu.add(createNewMenuItem());
		filemenu.add(createOpenMenuItem());
		filemenu.addSeparator();
		filemenu.add(createSaveMenuItem());
		filemenu.add(createSaveAsMenuItem());
		filemenu.add(createExportMenuItem());
		filemenu.addSeparator();
		
		JMenuItem optionsitem = new JMenuItem(new AbstractAction(BpmnEditor.getString("Settings..."))
		{
			public void actionPerformed(ActionEvent e)
			{
				final SettingsPanel spanel = new SettingsPanel(editorwindow.getSettings());
				OptionDialog od = new OptionDialog(editorwindow, BpmnEditor.getString("Settings"), true, spanel, new AbstractAction()
				{
					public void actionPerformed(ActionEvent e)
					{
						boolean[] refreshes = spanel.applySettings();
						
						if(refreshes[0])
						{
							editorwindow.getSettings().scanForClasses().addResultListener(new SwingResultListener<Void>(new IResultListener<Void>()
							{
								public void resultAvailable(Void result)
								{
									for (ModelContainer container : editorwindow.getModelContainers())
									{
										container.generateClassLoader();
									}
								}
								
								public void exceptionOccurred(Exception exception)
								{
								}
							}));
						}
						
						for (ModelContainer container : editorwindow.getModelContainers())
						{
							container.getGraphComponent().refresh();
							if (refreshes[1])
							{
								if (container.getGraph().getSelectionCount() == 0 ||
									container.getGraph().getSelectionCount() > 1)
								{
									container.setPropertyPanel(container.getSettings().getPropertyPanelFactory().createPanel(container, null));
//									container.setPropertyPanel(SPropertyPanelFactory.createPanel(null, container));
								}
								
								if (container.getGraph().getSelectionCount() == 1)
								{
									container.setPropertyPanel(container.getSettings().getPropertyPanelFactory().createPanel(container, container.getGraph().getSelectionCell()));
//									container.setPropertyPanel(SPropertyPanelFactory.createPanel(container.getGraph().getSelectionCell(), container));
								}
								container.getPropertypanelcontainer().setVisible(container.getSettings().isJadexExtensions());
								if (container.getSettings().isJadexExtensions())
								{
									((JSplitPane) container.getPropertypanelcontainer().getParent()).setDividerLocation(GuiConstants.GRAPH_PROPERTY_RATIO);
								}
							}
						}
						
					}
				});
				
				od.setSize(600, 400);
				od.setLocationRelativeTo(null);
				od.setVisible(true);
			}
		});
		filemenu.add(optionsitem);
		
		JMenuItem item = new JMenuItem(new AbstractAction(BpmnEditor.getString("Save Settings"))
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					List<File> openfiles = new ArrayList<File>();
					List<ModelContainer> modelcontainers = editorwindow.getModelContainers();
					for (ModelContainer modelcontainer : modelcontainers)
					{
						if (modelcontainer.getFile() != null)
						{
							openfiles.add(modelcontainer.getFile());
						}
					}
					editorwindow.getSettings().setOpenedFiles(openfiles.toArray(new File[openfiles.size()]));
					editorwindow.getSettings().save();
				}
				catch (IOException e1)
				{
					Logger.getLogger(BpmnEditor.APP_NAME).log(Level.SEVERE, e1.toString());
				}
			}
		});
		filemenu.add(item);
		
		JCheckBoxMenuItem saveonexititem = new JCheckBoxMenuItem(new AbstractAction(BpmnEditor.getString("Save Settings on Exit"))
		{
			public void actionPerformed(ActionEvent e)
			{
				JCheckBoxMenuItem saveonexititem = (JCheckBoxMenuItem) e.getSource();
				editorwindow.getSettings().setSaveSettingsOnExit(saveonexititem.getState());
			}
		});
		saveonexititem.setState(editorwindow.getSettings().isSaveSettingsOnExit());
		filemenu.add(saveonexititem);
		
		filemenu.addSeparator();
		filemenu.add(createExitMenuItem());
		add(filemenu);
		
		JMenu editmenu = new JMenu(BpmnEditor.getString("Edit"));
		add(editmenu);
		
		JMenuItem copyitem = new JMenuItem(new AbstractAction(BpmnEditor.getString("Copy"))
		{
			public void actionPerformed(ActionEvent e)
			{
				ModelContainer mc = editorwindow.getSelectedModelContainer();
				BpmnGraph graph = mc.getGraph();
				Object[] cells = graph.getSelectionCells();
				copycells = new Tuple2<BpmnGraph, List<VElement>>(graph, SHelper.copy(graph, mc.getBpmnModel(), cells));
			}
		});
		copyitem.setAccelerator(KeyStroke.getKeyStroke(
								java.awt.event.KeyEvent.VK_C, 
								java.awt.Event.CTRL_MASK));
		editmenu.add(copyitem);
		
		JMenuItem pasteitem = new JMenuItem(new AbstractAction(BpmnEditor.getString("Paste"))
		{
			public void actionPerformed(ActionEvent e)
			{
				if (copycells != null)
				{
					ModelContainer mc = editorwindow.getSelectedModelContainer();
					BpmnGraph graph = mc.getGraph();
					mxICell newparent = null;
					if (graph != copycells.getFirstEntity())
					{
						if (mc.getBpmnModel().getPools() == null || mc.getBpmnModel().getPools().size() == 0)
						{
							newparent = SCreationController.createPool(mc, new Point(50, 50));
						}
						else
						{
							MPool pool = mc.getBpmnModel().getPools().get(0);
							if (pool.getLanes() == null || pool.getLanes().size() == 0)
							{
								newparent = graph.getVisualElementById(pool.getId());
								
							}
							else
							{
								newparent = graph.getVisualElementById(pool.getLanes().get(0).getId());
							}
						}
					}
					graph.getModel().beginUpdate();
					List<VEdge> edges = new ArrayList<VEdge>();
					for (Object obj : copycells.getSecondEntity())
					{
						mxICell cell = (mxICell) obj;
						if (cell instanceof VEdge)
						{
							edges.add((VEdge) cell);
						}
						else
						{
							if (newparent != null)
							{
								cell.setParent(newparent);
								newparent.insert(cell);
								graph.addCell(cell, newparent);
							}
							else
							{
								graph.addCell(cell, cell.getParent());
							}
						}
//						editorwindow.getSelectedModelContainer().getGraph().addCell(cell);
					}
					graph.getModel().endUpdate();
					for (VEdge edge : edges)
					{
						if (edge instanceof VMessagingEdge)
						{
							graph.addCell(edge, graph.getCurrentRoot());
						}
						else
						{
							graph.addCell(edge, edge.getEdgeParent());
						}
						graph.refreshCellView(edge);
					}
					final Object[] ccells = copycells.getSecondEntity().toArray();
					graph.cellsOrdered(ccells, false);
					graph.setSelectionCells(ccells);
					mc.setDirty(true);
					copycells = new Tuple2<BpmnGraph, List<VElement>>(graph, SHelper.copy(editorwindow.getSelectedModelContainer().getGraph(), editorwindow.getSelectedModelContainer().getBpmnModel(), ccells));
					
				}
//				editorwindow.getSelectedModelContainer().getGraph().addCells(copycells);
			}
		});
		pasteitem.setAccelerator(KeyStroke.getKeyStroke(
								 java.awt.event.KeyEvent.VK_V, 
								 java.awt.Event.CTRL_MASK));
		editmenu.add(pasteitem);
		
		JMenuItem deleteitem = new JMenuItem(new AbstractAction(BpmnEditor.getString("Delete"))
		{
			public void actionPerformed(ActionEvent e)
			{
				ModelContainer mc = editorwindow.getSelectedModelContainer();
				BpmnGraph graph = mc.getGraph();
				graph.getModel().beginUpdate();
				graph.removeCells();
				graph.getModel().endUpdate();
			}
		});
		deleteitem.setAccelerator(KeyStroke.getKeyStroke(
								java.awt.event.KeyEvent.VK_D, 
								java.awt.Event.CTRL_MASK));
		editmenu.add(deleteitem);
		
		JMenu viewmenu = new JMenu(BpmnEditor.getString("View"));
		add(viewmenu);
		
		/* Styles */
		JMenu stylemenu = new JMenu(BpmnEditor.getString("Styles"));
		final ButtonGroup stylegroup = new ButtonGroup();
		Action styleaction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				List<ModelContainer> containers = editorwindow.getModelContainers();
				for (ModelContainer modelcontainer : containers)
				{
					if (modelcontainer != null)
					{
						JRadioButtonMenuItem button = (JRadioButtonMenuItem) e.getSource();
						mxStylesheet sheet = (mxStylesheet) button.getClientProperty("sheet");
						modelcontainer.getGraph().setStylesheet(sheet);
						modelcontainer.getGraph().refresh();
						editorwindow.getSettings().setSelectedSheet(button.getText());
					}
				}
			}
		};
		
		for (int i = 0; i < BpmnEditor.STYLE_SHEETS.size(); ++i)
		{
			JRadioButtonMenuItem view = new JRadioButtonMenuItem(styleaction);
			view.putClientProperty("sheet", BpmnEditor.STYLE_SHEETS.get(i).getSecondEntity());
			
			if (BpmnEditor.STYLE_SHEETS.get(i).getFirstEntity().equals(editwindow.getSettings().getSelectedSheet()))
			{
				view.setSelected(true);
			}
			
			view.setText(BpmnEditor.STYLE_SHEETS.get(i).getFirstEntity());
			stylegroup.add(view);
			stylemenu.add(view);
			
		}
		
		viewmenu.add(stylemenu);
		
		editorwindow.addTabListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				ModelContainer modelcontainer = editorwindow.getSelectedModelContainer();
				if (modelcontainer != null)
				{
					mxStylesheet sheet = modelcontainer.getGraph().getStylesheet();
					Enumeration<AbstractButton> buttons = stylegroup.getElements();
					while (buttons.hasMoreElements())
					{
						AbstractButton button = buttons.nextElement();
						mxStylesheet bsheet = (mxStylesheet) button.getClientProperty("sheet");
						if (bsheet.equals(sheet))
						{
							button.setSelected(true);
							return;
						}
					}
				}
			}
		});
		
		/** Icon sizes */
		JMenu iconmenu = new JMenu(BpmnEditor.getString("Icon Size"));
		ButtonGroup icongroup = new ButtonGroup();
		Action iconaction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				ModelContainer modelcontainer = editorwindow.getSelectedModelContainer();
				if (modelcontainer != null)
				{
					JRadioButtonMenuItem button = (JRadioButtonMenuItem) e.getSource();
					int iconsize = (Integer) button.getClientProperty("size");
					((BpmnToolbar) modelcontainer.getEditingToolbar()).setIconSize(iconsize);
					editorwindow.getSettings().setToolbarIconSize(iconsize);
				}
			}
		};
		
		for (int i = 0; i < GuiConstants.ICON_SIZES.length; ++i)
		{
			JRadioButtonMenuItem isbutton = new JRadioButtonMenuItem(iconaction);
			isbutton.putClientProperty("size", GuiConstants.ICON_SIZES[i]);
			if (GuiConstants.ICON_SIZES[i] == editorwindow.getSettings().getToolbarIconSize())
			{
				isbutton.setSelected(true);
			}
			isbutton.setText("" + GuiConstants.ICON_SIZES[i] + "x" + GuiConstants.ICON_SIZES[i]);
			icongroup.add(isbutton);
			iconmenu.add(isbutton);
		}
		viewmenu.add(iconmenu);
		
		JMenu helpmenu = new JMenu(BpmnEditor.getString("Help"));
		add(helpmenu);
		
		JMenuItem aboutitem = new JMenuItem(new AbstractAction(BpmnEditor.getString("About") + " " + BpmnEditor.APP_NAME)
		{
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(getParent(),
					    BpmnEditor.APP_NAME + " Build " + BpmnEditor.BUILD + "\nModel Build " + getModelBuild(),
					    BpmnEditor.APP_NAME,
					    JOptionPane.INFORMATION_MESSAGE);
			}
		});
		helpmenu.add(aboutitem);
		
		JMenuItem errorlogitem = new JMenuItem(new AbstractAction(BpmnEditor.getString("Debug Log"))
		{
			public void actionPerformed(ActionEvent e)
			{
				JScrollPane sp = new JScrollPane(editorwindow.getStatusArea());
				sp.setPreferredSize(new Dimension(400, 300));
				JOptionPane.showMessageDialog(getParent(), sp, "Debug Log", JOptionPane.PLAIN_MESSAGE);
			}
		});
		helpmenu.add(errorlogitem);
	}
	
	/**
	 *  Creates the "New" menu item.
	 *  
	 *  @return The menu item.
	 */
	protected JMenuItem createNewMenuItem()
	{
		JMenuItem newitem = new JMenuItem(new AbstractAction(BpmnEditor.getString("New"))
		{
			public void actionPerformed(ActionEvent e)
			{
				editorwindow.initializeNewModel(editorwindow.newModelTab(null));
			}
		});
		
		newitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		
		return newitem;
	}
	
	/**
	 *  Creates the "Open" menu item.
	 *  
	 *  @return The menu item.
	 */
	protected JMenuItem createOpenMenuItem()
	{
		JMenuItem openitem = new JMenuItem(new AbstractAction(BpmnEditor.getString("Open..."))
		{
			public void actionPerformed(ActionEvent e)
			{
				File curfile = null;
				ModelContainer curcont = editorwindow.getSelectedModelContainer();
				if (curcont != null)
				{
					curfile = curcont.getFile();
				}
				if (curfile == null)
				{
					curfile = editorwindow.getSettings().getLastFile();
				}
				
				BetterFileChooser fc = new BetterFileChooser(curfile);
				FileFilter filter2 = new FileNameExtensionFilter(BpmnEditor.getString("BPMN model file"), "bpmn2");
				fc.addChoosableFileFilter(filter2);
				FileFilter filter = new FileNameExtensionFilter(BpmnEditor.getString("Legacy BPMN model file"), "bpmn");
				fc.addChoosableFileFilter(filter);
				fc.setFileFilter(filter2);
				int result = fc.showOpenDialog(getParent());
				if (JFileChooser.APPROVE_OPTION == result)
				{
					try
					{
						File file = fc.getSelectedFile();
						
						for (int i = 0; i < editorwindow.getTabPane().getTabCount(); ++i)
						{
							BpmnEditorPanel panel = (BpmnEditorPanel) editorwindow.getTabPane().getComponentAt(i);
							if (file.equals(panel.getModelContainer().getFile()))
							{
								editorwindow.getTabPane().setSelectedIndex(i);
								return;
							}
						}
						
						editorwindow.loadModel(file);
					}
					catch (Exception e1)
					{
						displayIOError(e1);
					}
				}
			}
		});
		
		openitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		
		return openitem;
	}
	
	/**
	 *  Creates the "Save" menu item.
	 *  
	 *  @return The menu item.
	 */
	protected JMenuItem createSaveMenuItem()
	{
		JMenuItem saveitem = new JMenuItem(new AbstractAction(BpmnEditor.getString("Save"))
		{
			public void actionPerformed(ActionEvent e)
			{
				ModelContainer modelcontainer = editorwindow.getSelectedModelContainer();
				if (modelcontainer != null)
				{
					if (modelcontainer.getFile() != null && modelcontainer.getFile().getName().endsWith(".bpmn2"))
					{
						try
						{
							((BasePropertyPanel) modelcontainer.getPropertyPanel()).terminate();
							SBpmnModelWriter.writeModel(modelcontainer.getFile(), modelcontainer.getBpmnModel(), new BpmnVisualModelWriter(modelcontainer.getGraph()));
							editorwindow.getSettings().setLastFile(modelcontainer.getFile());
							modelcontainer.setDirty(false);
						}
						catch (IOException e1)
						{
							displayIOError(e1);
						}
					}
					else
					{
						saveWithDialog();
					}
				}
			}
		});
		
		saveitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		
		return saveitem;
	}
	
	/**
	 *  Creates the "Save as" menu item.
	 *  
	 *  @return The menu item.
	 */
	protected JMenuItem createSaveAsMenuItem()
	{
		JMenuItem saveasitem = new JMenuItem(new AbstractAction(BpmnEditor.getString("Save As..."))
		{
			public void actionPerformed(ActionEvent e)
			{
				saveWithDialog();
			}
		});
		
		return saveasitem;
	}
	
	/**
	 *  Creates the "Export" menu item.
	 *  
	 *  @return The menu item.
	 */
	protected JMenuItem createExportMenuItem()
	{
		JMenuItem exportitem = new JMenuItem(new AbstractAction(BpmnEditor.getString("Export..."))
		{
			public void actionPerformed(ActionEvent e)
			{
				ModelContainer modelcontainer = editorwindow.getSelectedModelContainer();
				if (modelcontainer != null)
				{
					String basefilename = modelcontainer.getFile() != null? modelcontainer.getFile().getPath() : "";
					
					if (basefilename.endsWith(".bpmn2"))
					{
						basefilename = basefilename.substring(0, basefilename.length() - 5) + "svg";
					}
					
					BetterFileChooser fc = new BetterFileChooser(modelcontainer.getFile());
					FileNameExtensionFilter filter = new FileNameExtensionFilter("SVG file", "svg");
					fc.addChoosableFileFilter(filter);
					fc.setFileFilter(filter);
//					filter = new FileNameExtensionFilter("WMF file", "wmf");
//					fc.setFileFilter(filter);
					filter = new FileNameExtensionFilter("EPS file", "eps");
					fc.addChoosableFileFilter(filter);
					fc.setAcceptAllFileFilterUsed(false);
					
					fc.setSelectedFile(new File(basefilename));
					
					int result = fc.showSaveDialog(modelcontainer.getGraphComponent());
					if (JFileChooser.APPROVE_OPTION == result)
					{
						try
						{
							BpmnGraph graph = modelcontainer.getGraph();
							
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
							}
							
							for (int i = 0; i < graph.getModel().getChildCount(graph.getDefaultParent()); ++i)
							{
								mxICell cell = (mxICell) graph.getModel().getChildAt(graph.getDefaultParent(), i);
								mxRectangle geo = graph.getCellBounds(cell);
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
							
							String ext = "eps";
							if (fc.getFileFilter() instanceof FileNameExtensionFilter)
							{
								ext = ((FileNameExtensionFilter) fc.getFileFilter()).getExtensions()[0];
							}
							
							File tmpfile = File.createTempFile("bpmnexport", "." + ext);
							FileOutputStream fos = new FileOutputStream(tmpfile);
							Graphics2D  g2d = null;
							
							if ("svg".equals(ext))
							{
								Document doc = GenericDOMImplementation.getDOMImplementation().createDocument("http://www.w3.org/2000/svg", "svg", null);
								SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(doc);
								ctx.setEmbeddedFontsOn(true);
								g2d = new SVGGraphics2D(ctx, true);
							}
							else
							{
								g2d = new EPSDocumentGraphics2D(true);
								((EPSDocumentGraphics2D) g2d).setGraphicContext(new GraphicContext());
								((EPSDocumentGraphics2D) g2d).setupDocument(fos, w, h);
							}
							
							//modelcontainer.getGraphComponent().paint(g)
							g2d.setClip(0, 0, w, h);
							g2d.translate(-x, -y);
							Object[] selcells = modelcontainer.getGraph().getSelectionModel().getCells();
							modelcontainer.getGraph().getSelectionModel().removeCells(selcells);
				        	
							modelcontainer.getGraphComponent().getGraphControl().paint(g2d);
				        	modelcontainer.getGraph().setSelectionCells(selcells);
				        	
				        	if (g2d instanceof EPSDocumentGraphics2D)
				        	{
				        		((EPSDocumentGraphics2D) g2d).finish();
				        		fos.close();
				        	}
				        	else if (g2d instanceof SVGGraphics2D)
				        	{
				        		OutputStreamWriter writer = new OutputStreamWriter(new GZIPOutputStream(fos));
				        		((SVGGraphics2D) g2d).stream(writer, false, true);
				        		writer.close();
				        	}
				        	
				        	File file = fc.getSelectedFile();
				        	if (!file.getName().endsWith(ext))
							{
								file = new File(file.getPath() + ext);
							}
				        	
				        	SUtil.moveFile(tmpfile, file);
						}
						catch (IOException e1)
						{
							displayIOError(e1);
						}
					}
				}
			}
		});
		
		exportitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));
		
		return exportitem;
	}
	
	/**
	 *  Creates the "Exit" menu item.
	 *  
	 *  @return The menu item.
	 */
	protected JMenuItem createExitMenuItem()
	{
		JMenuItem exititem = new JMenuItem(new AbstractAction(BpmnEditor.getString("Exit"))
		{
			public void actionPerformed(ActionEvent e)
			{
				editorwindow.terminate();
			}
		});
		
		exititem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK));
		
		return exititem;
	}
	
	/**
	 *  Saves a model by asking the user for a file.
	 */
	protected void saveWithDialog()
	{
		ModelContainer modelcontainer = editorwindow.getSelectedModelContainer();
		if (modelcontainer != null)
		{
			((BasePropertyPanel) modelcontainer.getPropertyPanel()).terminate();
			
			BetterFileChooser fc = new BetterFileChooser(modelcontainer.getFile());
			FileFilter filter = new FileNameExtensionFilter("BPMN model file (*.bpmn2)", "bpmn2");
			fc.addChoosableFileFilter(filter);
			fc.setFileFilter(filter);
			fc.setAcceptAllFileFilterUsed(false);
			
			if (modelcontainer.getFile() != null)
			{
				fc.setSelectedFile(modelcontainer.getFile());
			}
			
			int result = fc.showSaveDialog(getParent());
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
					SBpmnModelWriter.writeModel(file, modelcontainer.getBpmnModel(), new BpmnVisualModelWriter(modelcontainer.getGraph()));
					modelcontainer.setDirty(false);
					modelcontainer.setFile(file);
					editorwindow.getSettings().setLastFile(file);
					for (int i = 0; i < editorwindow.getTabPane().getTabCount(); ++i)
					{
						BpmnEditorPanel panel = (BpmnEditorPanel) editorwindow.getTabPane().getComponentAt(i);
						if (panel.getModelContainer() == modelcontainer)
						{
							editorwindow.getTabPane().setToolTipTextAt(i, file.getAbsolutePath());
							break;
						}
					}
				}
				catch (IOException e1)
				{
					displayIOError(e1);
				}
			}
		}
	}
	
	/**
	 *  Displays an error message after a failed IO operation.
	 *  
	 *  @param e The error.
	 */
	protected void displayIOError(Exception e)
	{
		e.printStackTrace();
		JOptionPane.showMessageDialog(getParent(),
			    e.getMessage(),
			    BpmnEditor.APP_NAME,
			    JOptionPane.ERROR_MESSAGE);
		//e1.printStackTrace();
	}
	
	/**
	 *  Returns the model build.
	 *  
	 *  @return The model build.
	 */
	public static int getModelBuild()
	{
		int build = 0;
		try
		{
			Field buildfield = SBpmnModelWriter.class.getField("BUILD");
			if (buildfield != null)
			{
				build = buildfield.getInt(null);
			}
		}
		catch (Exception e)
		{
		}
		
		return build;
	}
}
