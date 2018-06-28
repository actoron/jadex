package jadex.extension.envsupport.observer.graphics;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/** Abstract Modulation Composite. Subclasses have to implement the getColor() method
 *  which supplies the modulation color.
 */
public abstract class ModulateComposite implements Composite
{
	
	/**
	 * Method supplying the modulation color.
	 * @return modulation color
	 */
	protected abstract Color getColor();
	
	public CompositeContext createContext(ColorModel srcColorModel,
			ColorModel dstColorModel, RenderingHints hints)
	{
		return new ModulateContext(srcColorModel, dstColorModel);
	}
	
	private class ModulateContext implements CompositeContext
	{
		private ColorModel srcColorModel;
		private ColorModel dstColorModel;
		
		public ModulateContext(ColorModel srcColorModel, ColorModel dstColorModel)
		{
			this.srcColorModel = srcColorModel;
			this.dstColorModel = dstColorModel;
		}
		
		public void compose(Raster src, Raster dstIn, WritableRaster dstOut)
		{
			if(getColor()==null)
			{
				System.err.println("color is null");
			}
			
			float[] clrPx = getColor().getComponents(null);
			
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
					float invsa = 1.0f - tmpPx[3];
					
					float[] dstPx = dstColorModel.getNormalizedComponents(dstIn.getDataElements(x, y, null), null, 0);
					tmpPx[0] = (tmpPx[0] * tmpPx[3]) + (dstPx[0] * invsa);
					tmpPx[1] = (tmpPx[1] * tmpPx[3]) + (dstPx[1] * invsa);
					tmpPx[2] = (tmpPx[2] * tmpPx[3]) + (dstPx[2] * invsa);
					tmpPx[3] = 1.0f;
					
					Object outPixel = dstColorModel.getDataElements(tmpPx, 0, null);
					dstOut.setDataElements(x, y, outPixel);
				}
			}
		}
		
		public void dispose()
		{
			srcColorModel = null;
			dstColorModel = null;
		}
	}
}