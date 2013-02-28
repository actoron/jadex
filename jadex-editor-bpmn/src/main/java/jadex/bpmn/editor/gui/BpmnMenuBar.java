package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bpmn.editor.gui.controllers.DeletionController;
import jadex.bpmn.editor.gui.controllers.SelectionController;
import jadex.bpmn.editor.gui.propertypanels.SPropertyPanelFactory;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetComplexGrayscale;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetSimpleGrayscale;
import jadex.bpmn.editor.model.legacy.BpmnXMLReader;
import jadex.bpmn.editor.model.visual.BpmnVisualModelGenerator;
import jadex.bpmn.editor.model.visual.BpmnVisualModelReader;
import jadex.bpmn.editor.model.visual.BpmnVisualModelWriter;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.io.SBpmnModelReader;
import jadex.bpmn.model.io.SBpmnModelWriter;
import jadex.bridge.ResourceIdentifier;
import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.xmlgraphics.java2d.GraphicContext;
import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;

import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxStylesheet;

public class BpmnMenuBar extends JMenuBar
{
	/** The model container */
	protected ModelContainer modelcontainer;
	
	public BpmnMenuBar(ModelContainer container)
	{
		this.modelcontainer = container;
		
		JMenu filemenu = new JMenu("File");
		
		filemenu.add(createNewMenuItem());
		filemenu.add(createOpenMenuItem());
		filemenu.addSeparator();
		filemenu.add(createSaveMenuItem());
		filemenu.add(createSaveAsMenuItem());
		filemenu.add(createExportMenuItem());
		filemenu.addSeparator();
		filemenu.add(createExitMenuItem());
		add(filemenu);
		
		
		JMenu viewmenu = new JMenu("View");
		add(viewmenu);
		
		/* Styles */
		JMenu stylemenu = new JMenu("Styles");
		ButtonGroup stylegroup = new ButtonGroup();
		Action styleaction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				JRadioButtonMenuItem button = (JRadioButtonMenuItem) e.getSource();
				mxStylesheet sheet = (mxStylesheet) button.getClientProperty("sheet");
				modelcontainer.getGraph().setStylesheet(sheet);
				modelcontainer.getGraph().refresh();
			}
		};
		JRadioButtonMenuItem colorview = new JRadioButtonMenuItem(styleaction);
		
		// Assumes the default style sheet is color and set in the graph.
		colorview.putClientProperty("sheet", modelcontainer.getGraph().getStylesheet());
		
		colorview.setSelected(true);
		colorview.setText("Color");
		stylegroup.add(colorview);
		stylemenu.add(colorview);
		
		JRadioButtonMenuItem sgrayview = new JRadioButtonMenuItem(styleaction);
		sgrayview.putClientProperty("sheet", new BpmnStylesheetSimpleGrayscale());
		sgrayview.setText("Simple Grayscale");
		stylegroup.add(sgrayview);
		stylemenu.add(sgrayview);
		
		sgrayview = new JRadioButtonMenuItem(styleaction);
		sgrayview.putClientProperty("sheet", new BpmnStylesheetComplexGrayscale());
		sgrayview.setText("Complex Grayscale");
		stylegroup.add(sgrayview);
		stylemenu.add(sgrayview);
		
		viewmenu.add(stylemenu);
		
		/** Icon sizes */
		JMenu iconmenu = new JMenu("Icon Size");
		ButtonGroup icongroup = new ButtonGroup();
		Action iconaction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				JRadioButtonMenuItem button = (JRadioButtonMenuItem) e.getSource();
				int iconsize = (Integer) button.getClientProperty("size");
				((BpmnToolbar) modelcontainer.getEditingToolbar()).setIconSize(iconsize);
			}
		};
		
		for (int i = 0; i < GuiConstants.ICON_SIZES.length; ++i)
		{
			JRadioButtonMenuItem isbutton = new JRadioButtonMenuItem(iconaction);
			isbutton.putClientProperty("size", GuiConstants.ICON_SIZES[i]);
			if (GuiConstants.ICON_SIZES[i] == GuiConstants.DEFAULT_ICON_SIZE)
			{
				isbutton.setSelected(true);
			}
			isbutton.setText("" + GuiConstants.ICON_SIZES[i] + "x" + GuiConstants.ICON_SIZES[i]);
			icongroup.add(isbutton);
			iconmenu.add(isbutton);
		}
		viewmenu.add(iconmenu);
		
		JMenu helpmenu = new JMenu("Help");
		add(helpmenu);
		
		JMenuItem aboutitem = new JMenuItem(new AbstractAction("About " + BpmnEditor.APP_NAME)
		{
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(getParent(),
					    BpmnEditor.APP_NAME + " Build " + BpmnEditor.BUILD,
					    BpmnEditor.APP_NAME,
					    JOptionPane.INFORMATION_MESSAGE);
			}
		});
		helpmenu.add(aboutitem);
	}
	
	/**
	 *  Creates the "New" menu item.
	 *  
	 *  @return The menu item.
	 */
	protected JMenuItem createNewMenuItem()
	{
		JMenuItem newitem = new JMenuItem(new AbstractAction("New")
		{
			public void actionPerformed(ActionEvent e)
			{
				BpmnGraph graph = new BpmnGraph(modelcontainer, modelcontainer.getGraph().getStylesheet());
				MBpmnModel model = new MBpmnModel();
				initializeNewModel(modelcontainer, graph, model);
			}
		});
		
		return newitem;
	}
	
	/**
	 *  Creates the "Open" menu item.
	 *  
	 *  @return The menu item.
	 */
	protected JMenuItem createOpenMenuItem()
	{
		JMenuItem openitem = new JMenuItem(new AbstractAction("Open...")
		{
			public void actionPerformed(ActionEvent e)
			{
				BetterFileChooser fc = new BetterFileChooser();
				FileFilter filter = new FileNameExtensionFilter("BPMN model file", "bpmn2");
				fc.addChoosableFileFilter(filter);
				fc.setFileFilter(filter);
				filter = new FileNameExtensionFilter("Legacy BPMN model file", "bpmn");
				//fc.addChoosableFileFilter(filter);
				int result = fc.showOpenDialog(getParent());
				if (JFileChooser.APPROVE_OPTION == result)
				{
					try
					{
						File file = fc.getSelectedFile();
						if (!file.getName().endsWith(".bpmn2") &&
							!file.getName().endsWith(".bpmn"))
						{
							File tmpfile = new File(file.getAbsolutePath() + ".bpmn2");
							if (tmpfile.exists())
							{
								file = tmpfile;
							}
							else
							{
								file = new File(file.getAbsolutePath() + ".bpmn");
							}
						}
						
						BpmnGraph graph = new BpmnGraph(modelcontainer, modelcontainer.getGraph().getStylesheet());
						
						MBpmnModel mmodel = null;
						if (file.getName().endsWith("bpmn2"))
						{
							BpmnVisualModelReader vreader = new BpmnVisualModelReader(graph);
							mmodel = SBpmnModelReader.readModel(file, vreader);
						}
						else
						{
							ResourceInfo rinfo = new ResourceInfo(file.getAbsolutePath(), new FileInputStream(file), file.lastModified());
							mmodel = BpmnXMLReader.read(rinfo, BpmnMenuBar.class.getClassLoader(), new ResourceIdentifier(), null);
							(new BpmnVisualModelGenerator(mmodel)).generateModel(graph);
						}
						
						initializeNewModel(modelcontainer, graph, mmodel);
						modelcontainer.setFile(file);
					}
					catch (Exception e1)
					{
						displayIOError(e1);
					}
				}
			}
		});
		
		return openitem;
	}
	
	/**
	 *  Creates the "Save" menu item.
	 *  
	 *  @return The menu item.
	 */
	protected JMenuItem createSaveMenuItem()
	{
		JMenuItem saveitem = new JMenuItem(new AbstractAction("Save")
		{
			public void actionPerformed(ActionEvent e)
			{
				if (modelcontainer.getFile() != null)
				{
					try
					{
						SBpmnModelWriter.writeModel(modelcontainer.getFile(), modelcontainer.getBpmnModel(), new BpmnVisualModelWriter(modelcontainer.getGraph()));
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
		});
		
		return saveitem;
	}
	
	/**
	 *  Creates the "Save as" menu item.
	 *  
	 *  @return The menu item.
	 */
	protected JMenuItem createSaveAsMenuItem()
	{
		JMenuItem saveasitem = new JMenuItem(new AbstractAction("Save As...")
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
		JMenuItem exportitem = new JMenuItem(new AbstractAction("Export...")
		{
			public void actionPerformed(ActionEvent e)
			{
				BetterFileChooser fc = new BetterFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("EPS file", "eps");
				fc.addChoosableFileFilter(filter);
				fc.setFileFilter(filter);
				int result = fc.showSaveDialog(modelcontainer.getGraphComponent());
				if (JFileChooser.APPROVE_OPTION == result)
				{
					try
					{
						EPSDocumentGraphics2D g2d = new EPSDocumentGraphics2D(false);
						g2d.setGraphicContext(new GraphicContext());
						
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
						Object[] selcells = modelcontainer.getGraph().getSelectionModel().getCells();
						modelcontainer.getGraph().getSelectionModel().removeCells(selcells);
			        	//modelcontainer.getGraphComponent().paint(g2d);
			        	//modelcontainer.getGraphComponent().setSize(w, h);
			        	//modelcontainer.getGraphComponent().paintComponents(g2d);
						modelcontainer.getGraphComponent().getGraphControl().paint(g2d);
			        	modelcontainer.getGraph().setSelectionCells(selcells);
			        	g2d.finish();
			        	fos.close();
			        	
			        	File file = fc.getSelectedFile();
			        	String ext = "." + filter.getExtensions()[0];
			        	if (!file.getName().endsWith(ext))
						{
							file = new File(file.getAbsolutePath() + ext);
						}
			        	
			        	SUtil.moveFile(tmpfile, file);
					}
					catch (IOException e1)
					{
						displayIOError(e1);
					}
				}
			}
		});
		
		return exportitem;
	}
	
	/**
	 *  Creates the "Exit" menu item.
	 *  
	 *  @return The menu item.
	 */
	protected JMenuItem createExitMenuItem()
	{
		JMenuItem exititem = new JMenuItem(new AbstractAction("Exit")
		{
			public void actionPerformed(ActionEvent e)
			{
				if (modelcontainer.checkUnsaved(getParent()))
				{
					System.exit(0);
				}
			}
		});
		
		return exititem;
	}
	
	/**
	 *  Saves a model by asking the user for a file.
	 */
	protected void saveWithDialog()
	{
		BetterFileChooser fc = new BetterFileChooser();
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
			}
			catch (IOException e1)
			{
				displayIOError(e1);
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
	 *  Initializes a new model with the required controllers.
	 *  
	 *  @param graph New graph.
	 *  @param model New BPMN model.
	 */
	protected static void initializeNewModel(ModelContainer modelcontainer, BpmnGraph graph, MBpmnModel model)
	{
		graph.getSelectionModel().addListener(mxEvent.CHANGE, new SelectionController(modelcontainer));
		
		modelcontainer.setBpmnModel(model);
		modelcontainer.setGraph(graph);
		modelcontainer.setDirty(false);
		
		
		modelcontainer.getGraphComponent().refresh();
		modelcontainer.setPropertyPanel(SPropertyPanelFactory.createPanel(null, modelcontainer));
		
		new DeletionController(modelcontainer);
	}
}
