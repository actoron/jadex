package jadex.gpmn.editor.gui;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

/** Helper methods for the GUI. */
public class SGuiHelper
{
//	public static final JToggleButton createTool(ImageProvider imgprovider, String mode, Color color, String imagebasename, String tooltip, boolean circular, boolean symbolic)
//	{
//		JToggleButton tool = new JToggleButton();
//		tool.getModel().setActionCommand(mode);
//		tool.setContentAreaFilled(false);
//		
//		ImageIcon onicon = null;
//		if (circular)
//		{
//			onicon = imgprovider.generateCircularImageIcon(imagebasename, color, symbolic, true, true);
//			tool.setIcon(imgprovider.generateCircularImageIcon(imagebasename, color, symbolic, false, false));
//			tool.setRolloverIcon(imgprovider.generateCircularImageIcon(imagebasename, color, symbolic, true, false));
//		}
//		else
//		{
//			onicon = imgprovider.generateRectangularImageIcon(imagebasename, color, symbolic, true, true);
//			tool.setIcon(imgprovider.generateRectangularImageIcon(imagebasename, color, symbolic, false, false));
//			tool.setRolloverIcon(imgprovider.generateRectangularImageIcon(imagebasename, color, symbolic, true, false));
//		}
//		tool.setPressedIcon(onicon);
//		tool.setSelectedIcon(onicon);
//		
//		tool.setBorder(new EmptyBorder(0, 0, 0, 0));
//		tool.setMargin(new Insets(0, 0, 0, 0));
//		tool.setToolTipText(tooltip);
//		return tool;
//	}
	
	/**
	 *  Text extraction from the Java "Document" class is braindead,
	 *  throws non-RuntimeException: Bloat, bloat, bloat.
	 *  
	 *  @param doc The document.
	 *  @return The extracted String.
	 */
	public static final String getText(Document doc)
	{
		String ret = null;
        
		try
        {
            ret = doc.getText(0, doc.getLength());
        }
        catch (BadLocationException e)
        {
        }
		
		if (ret.length() == 0)
		{
			ret = null;
		}
        
        return ret;
	}
	
	/**
	 *  Refreshes the view for a cell.
	 *  
	 *  @param cell The cell.
	 */
	public static final void refreshCellView(mxGraph graph, mxCell cell)
	{
		graph.getView().clear(cell, true, false);
		graph.getView().invalidate(cell);
		Object[] selcells = graph.getSelectionModel().getCells();
		graph.getSelectionModel().removeCells(selcells);
		graph.getView().validate();
		graph.setSelectionCells(selcells);
	}
	
	/**
	 *  Gets the center of the rectangle described by the two points.
	 *  
	 *  @param points Points to be included in the calculation.
	 *  @return The center.
	 */
	public static final mxPoint getCenter(mxPoint[] points)
	{
		return getCenter(new mxPoint(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY), new mxPoint(0.0, 0.0), points);
	}
	
	/**
	 *  Gets the center of the rectangle described by the two points.
	 *  
	 *  @param min Minimum coordinates of the rectangle.
	 *  @param max Maximum coordinates of the rectangle.
	 *  @param addition Additional points to be included in the calculation.
	 *  @return The center.
	 */
	public static final mxPoint getCenter(mxPoint min, mxPoint max, mxPoint[] additional)
	{
		mxPoint tmpmin = new mxPoint(min);
		mxPoint ret = new mxPoint(max);
		if (additional != null)
		{
			for (int i = 0; i < additional.length; ++i)
			{
				tmpmin.setX(Math.min(tmpmin.getX(), additional[i].getX()));
				tmpmin.setY(Math.min(tmpmin.getY(), additional[i].getY()));
				ret.setX(Math.max(ret.getX(), additional[i].getX()));
				ret.setY(Math.max(ret.getY(), additional[i].getY()));
			}
		}
		
		ret = new mxPoint(tmpmin.getX() + (ret.getX() - tmpmin.getX()) * 0.5,
				 		  tmpmin.getY() + (ret.getY() - tmpmin.getY()) * 0.5);
		
		return ret;
	}
	
	/**
	 *  Gets the selected button in a button group.
	 *  
	 *  @param group The button group.
	 *  @return The selected button.
	 */
	public static final AbstractButton getSelectedButton(ButtonGroup group)
	{
		AbstractButton ret = null;
		ButtonModel model = group.getSelection();
		Enumeration<AbstractButton> buttons = group.getElements();
		while (ret == null && buttons.hasMoreElements())
		{
			AbstractButton b = buttons.nextElement();
			if (b.getModel().equals(model))
			{
				ret = b;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Applies a circle layout to the model.
	 *  
	 *  @param modelcontainer The model container.
	 */
	public static final void applyOrganicLayout(ModelContainer modelcontainer)
	{
		mxOrganicLayout layout = new mxOrganicLayout(modelcontainer.getGraph());
		Object root = ((mxCell) modelcontainer.getGraph().getModel().getRoot()).getChildAt(0);
		int count = modelcontainer.getGraph().getChildVertices(root).length;
		int len = count * GuiConstants.DEFAULT_GOAL_WIDTH;
		modelcontainer.getGraph().getView().setGraphBounds(new mxRectangle(0.0, 0.0, len, len));
		layout.setAverageNodeArea(0.0);
		layout.setOptimizeNodeDistribution(true);
		layout.setNodeDistributionCostFactor(10000000.0);
		layout.setEdgeCrossingCostFactor(60000.0);
		layout.execute(modelcontainer.getGraph().getDefaultParent());
		modelcontainer.setDirty(true);
	}
	
	/**
	 *  Composite for modulation.
	 *
	 */
	protected static class ModulateComposite implements Composite
	{
		/** Modulation color */
		protected Color modcolor;
		
		/** Highlight amplification*/
		protected boolean high;
		
		/**
		 *  Creates a new Composite.
		 *  
		 *  @param modcolor The modulation color.
		 *  @param high Highlight amplification flag.
		 */
		public ModulateComposite(Color modcolor, boolean high)
		{
			this.modcolor = modcolor;
			this.high = high;
		}
		
		/**
		 *  Create the context.
		 */
		public CompositeContext createContext(ColorModel srcColorModel,
				ColorModel dstColorModel, RenderingHints hints)
		{
			return new ModulationContextAccel(srcColorModel, dstColorModel);
		}
		
		/**
		 *  Regular "correct" modulation context.
		 *
		 */
		protected class ModulationContext implements CompositeContext
		{
			/** Source color model. */
			protected ColorModel srcColorModel;
			
			/** Destination color model. */
			protected ColorModel dstColorModel;
			
			/**
			 *  Creates the context.
			 *  
			 *  @param srcColorModel Source color model.
			 *  @param dstColorModel Destination color model.
			 */
			public ModulationContext(ColorModel srcColorModel, ColorModel dstColorModel)
			{
				this.srcColorModel = srcColorModel;
				this.dstColorModel = dstColorModel;
			}
			
			/**
			 *  Composes the image.
			 */
			public void compose(Raster src, Raster dstIn, WritableRaster dstOut)
			{
				float[] clrPx = modcolor.getComponents(null);
				
				int maxX = Math.min(src.getWidth(), dstIn.getWidth());
				int maxY = Math.min(src.getHeight(), dstIn.getHeight());
				float[] tmpPx = new float[4];
				for (int y = 0; y < maxY; ++y)
				{
					for (int x = 0; x < maxX; ++x)
					{
						Object inPixel = src.getDataElements(x, y, null);
						tmpPx = srcColorModel.getNormalizedComponents(inPixel, tmpPx, 0);
						
						tmpPx[0] *= clrPx[0];
						tmpPx[1] *= clrPx[1];
						tmpPx[2] *= clrPx[2];
						tmpPx[3] *= clrPx[3];
						
						if (high)
						{
							for (int i = 0; i < 4; ++i)
							{
								tmpPx[i] *= GuiConstants.HIGHLIGHT_AMP;
								tmpPx[i] = Math.min(tmpPx[i], 1.0f);
							}
						}
						
						Object outPixel = dstColorModel.getDataElements(tmpPx, 0, null);
						dstOut.setDataElements(x, y, outPixel);
					}
				}
			}
			
			/**
			 *  Disposes context.
			 */
			public void dispose()
			{
				srcColorModel = null;
				dstColorModel = null;
			}
		}
		
		/**
		 *  Accelerated modulation context, based on some assumptions
		 *  (4 color components, each 8 bits, etc.).
		 *
		 */
		protected class ModulationContextAccel implements CompositeContext
		{
			/** Source color model. */
			protected ColorModel srcColorModel;
			
			/** Destination color model. */
			protected ColorModel dstColorModel;
			
			/**
			 *  Creates the context.
			 *  
			 *  @param srcColorModel Source color model.
			 *  @param dstColorModel Destination color model.
			 */
			public ModulationContextAccel(ColorModel srcColorModel, ColorModel dstColorModel)
			{
				this.srcColorModel = srcColorModel;
				this.dstColorModel = dstColorModel;
			}
			
			/**
			 *  Composes the image.
			 */
			public void compose(Raster src, Raster dstIn, WritableRaster dstOut)
			{
				float[] clrPx = modcolor.getComponents(null);
				
				int maxX = Math.min(src.getWidth(), dstIn.getWidth());
				int maxY = Math.min(src.getHeight(), dstIn.getHeight());
				
				float amp = 1.0f;
				if (high)
				{
					amp = GuiConstants.HIGHLIGHT_AMP;
				}
				
				for (int y = 0; y < maxY; ++y)
				{
					for (int x = 0; x < maxX; ++x)
					{
						//System.out.println(dstOut.getDataBuffer().getClass());
						Object inPixel = src.getDataElements(x, y, null);
						byte[] bPixel = (byte[]) inPixel;
						bPixel[0] = (byte) (Math.min(((bPixel[0] & 0xFF) / 255.0f) * clrPx[0] * amp, 1.0f) * 255.0f);
						bPixel[1] = (byte) (Math.min(((bPixel[1] & 0xFF) / 255.0f) * clrPx[1] * amp, 1.0f) * 255.0f);
						bPixel[2] = (byte) (Math.min(((bPixel[2] & 0xFF) / 255.0f) * clrPx[2] * amp, 1.0f) * 255.0f);
						bPixel[3] = (byte) (Math.min(((bPixel[3] & 0xFF) / 255.0f) * clrPx[3] * amp, 1.0f) * 255.0f);
						dstOut.setDataElements(x, y, bPixel);
					}
				}
			}
			
			/**
			 *  Disposes context.
			 */
			public void dispose()
			{
				srcColorModel = null;
				dstColorModel = null;
			}
		}
	}
}
