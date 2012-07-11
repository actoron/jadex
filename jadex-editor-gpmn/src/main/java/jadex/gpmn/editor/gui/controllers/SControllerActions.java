package jadex.gpmn.editor.gui.controllers;

import jadex.gpmn.editor.gui.BetterFileChooser;
import jadex.gpmn.editor.gui.GpmnGraph;
import jadex.gpmn.editor.gui.GuiConstants;
import jadex.gpmn.editor.gui.IControllerAccess;
import jadex.gpmn.editor.gui.IModelContainer;
import jadex.gpmn.editor.gui.IViewAccess;
import jadex.gpmn.editor.gui.SPropertyPanelFactory;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;
import jadex.gpmn.editor.model.gpmn.IModelCodec;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.xmlgraphics.java2d.GraphicContext;
import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxStylesheet;

/**
 * 
 * Class for providing simple controller functions for simple operations like menu items.
 *
 */
public class SControllerActions
{
	/**
	 *  Sets the style sheet.
	 * 
	 *  @param container The model container.
	 *  @param sheet The style sheet.
	 *  @return The controller.
	 */
	public static final void setStyle(IModelContainer container, mxStylesheet sheet)
	{
		container.getGraph().setStylesheet(sheet);
		container.getGraph().refresh();
	}
	
	/**
	 *  Application exit operation.
	 *  
	 *  @param parent Parent component.
	 *  @param modelcontainer The model container.
	 */
	public static final void exit(Component parent, IModelContainer modelcontainer)
	{
		if (checkUnsaved(parent, modelcontainer))
		{
			System.exit(0);
		}
	}
	
	/**
	 *  New model operation.
	 *  
	 *  @param parent Parent component.
	 *  @param viewaccess View access.
	 *  @param controlleraccess Controller access.
	 *  @param modelcontainer The model container.
	 */
	public static final void newModel(Component parent,
									  IViewAccess viewaccess,
									  IControllerAccess controlleraccess,
									  IModelContainer modelcontainer)
	{
		if (checkUnsaved(parent, modelcontainer))
		{
			mxStylesheet sheet = viewaccess.getSelectedSheet();
			GpmnGraph graph = new GpmnGraph(controlleraccess, sheet);
			IGpmnModel gpmnmodel = modelcontainer.getModelFactory().createModel();
			modelcontainer.setGpmnModel(gpmnmodel);
			modelcontainer.setGraph(graph);
			modelcontainer.getGraphComponent().refresh();
			modelcontainer.setDirty(false);
		}
	}
	
	/**
	 *  Open model operation.
	 *  
	 *  @param parent Parent component.
	 *  @param viewaccess View access.
	 *  @param controlleraccess Controller access.
	 *  @param modelcontainer The model container.
	 */
	public static final void openModel(Component parent,
									  IViewAccess viewaccess,
									  IControllerAccess controlleraccess,
									  IModelContainer modelcontainer)
	{
		BetterFileChooser fc = new BetterFileChooser();
		FileFilter filter = new FileNameExtensionFilter("GPMN intermediate model file", "gpmn");
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);
		int result = fc.showOpenDialog(parent);
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
				GpmnGraph graph = new GpmnGraph(controlleraccess, viewaccess.getSelectedSheet());
				modelcontainer.setGraph(graph);
				
				if (graphmodel == null)
				{
					graphmodel = gpmnmodel.generateGraphModel();
					graph.setModel(graphmodel);
					//applyOrganicLayout(modelcontainer);
					applyTreeLayout(modelcontainer);
				}
				else
				{
					graph.setModel(graphmodel);
				}
				
				modelcontainer.setGpmnModel(gpmnmodel);
				modelcontainer.getGraphComponent().refresh();
				modelcontainer.setFile(file);
				
				viewaccess.setPropertyPanel(SPropertyPanelFactory.createPanel(modelcontainer));
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 *  Save operation.
	 *  
	 *  @param parent Parent component.
	 *  @param modelcontainer The model container.
	 */
	public static final void saveModel(Component parent, IModelContainer modelcontainer)
	{
		File file = modelcontainer.getFile();
		if (file == null)
		{
			saveAsModel(parent, modelcontainer);
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
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 *  Save as operation.
	 *  
	 *  @param parent Parent component.
	 *  @param modelcontainer The model container.
	 */
	public static final void saveAsModel(Component parent, IModelContainer modelcontainer)
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
		
		int result = fc.showSaveDialog(parent);
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
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 *  Export model operation.
	 *  
	 *  @param parent Parent component.
	 *  @param viewaccess View access.
	 *  @param controlleraccess Controller access.
	 *  @param modelcontainer The model container.
	 */
	public static final void exportModel(Component parent, IModelContainer modelcontainer)
	{
		BetterFileChooser fc = new BetterFileChooser();
		FileFilter filter = new FileNameExtensionFilter("EPS file", "eps");
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);
		int result = fc.showSaveDialog(parent);
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
	
	/**
	 *  Applies a circle layout to the model.
	 *  
	 *  @param modelcontainer The model container.
	 */
	public static final void applyCircleLayout(IModelContainer modelcontainer)
	{
		mxCircleLayout layout = new mxCircleLayout(modelcontainer.getGraph());
		layout.execute(modelcontainer.getGraph().getDefaultParent());
	}
	
	/**
	 *  Applies a tree layout to the model.
	 *  
	 *  @param modelcontainer The model container.
	 */
	public static final void applyTreeLayout(IModelContainer modelcontainer)
	{
		mxCompactTreeLayout layout = new mxCompactTreeLayout(modelcontainer.getGraph());
		layout.setEdgeRouting(false);
		layout.setHorizontal(false);
		layout.setNodeDistance(GuiConstants.DEFAULT_GOAL_WIDTH);
		layout.execute(modelcontainer.getGraph().getDefaultParent());
	}
	
	/**
	 *  Applies a circle layout to the model.
	 *  
	 *  @param modelcontainer The model container.
	 */
	public static final void applyOrganicLayout(IModelContainer modelcontainer)
	{
		mxOrganicLayout layout = new mxOrganicLayout(modelcontainer.getGraph());
		layout.execute(modelcontainer.getGraph().getDefaultParent());
	}
	
	/**
	 *  Handles unsaved model deletions.
	 */
	protected static final boolean checkUnsaved(Component parent, IModelContainer modelcontainer)
	{
		boolean ret = true;
		if (modelcontainer.isDirty())
		{
			int result = JOptionPane.showConfirmDialog(parent, "The model contains unsaved changes, proceed anyway?", "Unsaved Changes",JOptionPane.YES_NO_OPTION);
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
