package jadex.gpmn.editor.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxStylesheet;

import jadex.commons.SUtil;
import jadex.gpmn.editor.GpmnEditor;
import jadex.gpmn.editor.gui.stylesheets.GpmnStylesheetGrayscale;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;
import jadex.gpmn.editor.model.gpmn.IModelCodec;

/**
 *  The menu bar of the editor.
 *
 */
public class GpmnMenuBar extends JMenuBar
{
	/** The model container */
	protected ModelContainer modelcontainer;
	
	public GpmnMenuBar(ModelContainer container)
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
		sgrayview.putClientProperty("sheet", new GpmnStylesheetGrayscale());
		sgrayview.setText("Grayscale");
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
				((GpmnToolbar) modelcontainer.getEditingToolbar()).setIconSize(iconsize);
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
		
		JMenu layoutmenu = new JMenu("Layouts");
		viewmenu.add(layoutmenu);
		
		JMenuItem circlelayoutitem = new JMenuItem(new AbstractAction("Circle")
		{
			
			public void actionPerformed(ActionEvent e)
			{
				mxCircleLayout layout = new mxCircleLayout(modelcontainer.getGraph());
				layout.execute(modelcontainer.getGraph().getDefaultParent());
				modelcontainer.setDirty(true);
			}
		});
		layoutmenu.add(circlelayoutitem);
		
		JMenuItem organiclayoutitem = new JMenuItem(new AbstractAction("Organic")
		{
			
			public void actionPerformed(ActionEvent e)
			{
				SGuiHelper.applyOrganicLayout(modelcontainer);
			}
		});
		layoutmenu.add(organiclayoutitem);
		
		JMenuItem treelayoutitem = new JMenuItem(new AbstractAction("Tree")
		{
			
			public void actionPerformed(ActionEvent e)
			{
				mxCompactTreeLayout layout = new mxCompactTreeLayout(modelcontainer.getGraph());
				layout.setEdgeRouting(false);
				layout.setHorizontal(false);
				layout.setNodeDistance(GuiConstants.DEFAULT_GOAL_WIDTH);
				layout.execute(modelcontainer.getGraph().getDefaultParent());
				modelcontainer.setDirty(true);
			}
		});
		layoutmenu.add(treelayoutitem);
		
		JMenu helpmenu = new JMenu("Help");
		add(helpmenu);
		
		JMenuItem aboutitem = new JMenuItem(new AbstractAction("About " + GpmnEditor.APP_NAME)
		{
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(getParent(),
					    GpmnEditor.APP_NAME + " Build " + GpmnEditor.BUILD,
					    GpmnEditor.APP_NAME,
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
				if (modelcontainer.checkUnsaved(modelcontainer.getGraphComponent()))
				{
					mxStylesheet sheet = modelcontainer.getGraph().getStylesheet();
					GpmnGraph graph = new GpmnGraph(modelcontainer, sheet);
					IGpmnModel gpmnmodel = modelcontainer.getModelFactory().createModel();
					modelcontainer.setGpmnModel(gpmnmodel);
					modelcontainer.setGraph(graph);
					modelcontainer.getGraphComponent().refresh();
					modelcontainer.setDirty(false);
				}
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
				FileFilter filter = new FileNameExtensionFilter("GPMN intermediate model file", "gpmn");
				fc.addChoosableFileFilter(filter);
				fc.setFileFilter(filter);
				int result = fc.showOpenDialog(modelcontainer.getGraphComponent());
				if (JFileChooser.APPROVE_OPTION == result)
				{
					try
					{
						IGpmnModel gpmnmodel = modelcontainer.getModelFactory().createModel();
						File file = fc.getSelectedFile();
						if (!file.getName().endsWith(".gpmn"))
						{
							file = new File(file.getAbsolutePath() + ".gpmn");
						}
						mxIGraphModel graphmodel = gpmnmodel.getModelCodec(IModelCodec.CODEC_TYPE_GPMN).readModel(file);
						
						// Funny, we need a new graph or we get quirky graphics (stuck selection marker)... Bug?
						GpmnGraph graph = new GpmnGraph(modelcontainer, modelcontainer.getGraph().getStylesheet());
						modelcontainer.setGraph(graph);
						
						if (graphmodel == null)
						{
							graphmodel = gpmnmodel.generateGraphModel();
							graph.setModel(graphmodel);
							//applyOrganicLayout(modelcontainer);
							SGuiHelper.applyOrganicLayout(modelcontainer);
						}
						else
						{
							graph.setModel(graphmodel);
						}
						
						modelcontainer.setGpmnModel(gpmnmodel);
						modelcontainer.getGraphComponent().refresh();
						modelcontainer.setFile(file);
						
						modelcontainer.setPropertyPanel(SPropertyPanelFactory.createPanel(modelcontainer));
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
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
				File file = modelcontainer.getFile();
				if (file == null)
				{
					saveWithDialog();
				}
				else
				{
					try
					{
						modelcontainer.getGpmnModel().getModelCodec(IModelCodec.CODEC_TYPE_GPMN).writeModel(file, modelcontainer.getGraph());
						modelcontainer.setDirty(false);
						modelcontainer.setFile(file);
					}
					catch (IOException e1)
					{
						displayIOError(e1);
					}
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
						
						File tmpfile = File.createTempFile("export", ".eps");
						//modelcontainer.getGraphComponent().paint(g)
						FileOutputStream fos = new FileOutputStream(tmpfile);
						g2d.setupDocument(fos, w, h);
						g2d.setClip(0, 0, w, h);
						g2d.translate(-x, -y);
			        	modelcontainer.getGraphComponent().getGraphControl().paint(g2d);
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
		FileFilter filter = new FileNameExtensionFilter("GPMN intermediate model file (*.gpmn)", "gpmn");
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);
		filter = new FileNameExtensionFilter("Jadex BDI agent model file (*.agent.xml)", "agent.xml");
		fc.addChoosableFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(false);
		
		if (modelcontainer.getFile() != null)
		{
			fc.setSelectedFile(modelcontainer.getFile());
		}
		
		int result = fc.showSaveDialog(modelcontainer.getGraphComponent());
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
					
					// FIXME: Hack until BDI models can be loaded.
					modelcontainer.setFile(file);
				}
				else if (".agent.xml".equals(ext))
				{
					codec = modelcontainer.getGpmnModel().getModelCodec(IModelCodec.CODEC_TYPE_BDI);
				}
				codec.writeModel(file, modelcontainer.getGraph());
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
		JOptionPane.showMessageDialog(getParent(),
			    e.getMessage(),
			    GpmnEditor.APP_NAME,
			    JOptionPane.ERROR_MESSAGE);
		Logger.getLogger(GpmnEditor.APP_NAME).log(Level.SEVERE, "Failed to save file: " + e.getMessage());
		//e1.printStackTrace();
	}
}
