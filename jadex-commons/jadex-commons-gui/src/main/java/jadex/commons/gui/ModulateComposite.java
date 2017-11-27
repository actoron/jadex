package jadex.commons.gui;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 *  Composite for modulating (tinting) images.
 *
 */
public class ModulateComposite implements Composite
{
	/** Modulation color */
	protected Color modcolor;
	
	/**
	 *  Creates a new Composite.
	 *  
	 *  @param modcolor The modulation color.
	 *  @param high Highlight amplification flag.
	 */
	public ModulateComposite(Color modcolor)
	{
		this.modcolor = modcolor;
	}
	
	/**
	 *  Create the context.
	 */
	public CompositeContext createContext(ColorModel srcColorModel,
			ColorModel dstColorModel, RenderingHints hints)
	{
		return new ModulationContext(srcColorModel, dstColorModel);
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
			
			for (int y = 0; y < maxY; ++y)
			{
				for (int x = 0; x < maxX; ++x)
				{
					//System.out.println(dstOut.getDataBuffer().getClass());
					Object inPixel = src.getDataElements(x, y, null);
					byte[] bPixel = (byte[]) inPixel;
					bPixel[0] = (byte) (((bPixel[0] & 0xFF) / 255.0f) * clrPx[0] * 255.0f);
					bPixel[1] = (byte) (((bPixel[1] & 0xFF) / 255.0f) * clrPx[1] * 255.0f);
					bPixel[2] = (byte) (((bPixel[2] & 0xFF) / 255.0f) * clrPx[2] * 255.0f);
					bPixel[3] = (byte) (((bPixel[3] & 0xFF) / 255.0f) * clrPx[3] * 255.0f);
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
