package jadex.commons.transformation.traverser;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

import jadex.commons.SReflect;
//import sun.awt.image.ImageRepresentation;
//import sun.awt.image.ToolkitImage;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 * 
 */
public class ImageProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		return object instanceof Image;
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
	{
		Object ret = object;
		if(SCloner.isCloneContext(context))
		{
			byte[] data = imageToStandardBytes((Image) object, "image/png");
			Class<?> clazz = SReflect.getClass(type);
			ret = imageFromBytes(data, clazz);
		}
		return ret;
	}
	
	/** Hack disabling image caching on Mac OS which seems broken in Java. */
	static
	{
//		if (System.getProperty("os.name").startsWith("Mac "))
//		{
		ImageIO.setUseCache(false);
//		}
	}
	
	/**
	 * 
	 */
	public static Image imageFromBytes(byte[] data, Class<?> clazz)
	{
		Image ret;
		String classname = SReflect.getClassName(clazz);
		if(classname.indexOf("Toolkit")!=-1)
		{
			Toolkit t = Toolkit.getDefaultToolkit();
			ret = t.createImage(data);
		}
		else
		{
			try
			{
				ret = ImageIO.read(new ByteArrayInputStream(data));
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		return ret;
	}
	
	/**
	 *  Convert image to bytes.
	 *  
	 *  Method is a direct copy from protected method
	 *  sun.awt.datatransfer.DataTransferer
	 */
	public static byte[] imageToStandardBytes(Image image, String mimeType)
	{
		IOException originalIOE = null;
		Iterator writerIterator = ImageIO.getImageWritersByMIMEType(mimeType);
		if(!writerIterator.hasNext())
		{
			throw new RuntimeException(new IOException("No registered service provider can encode "
				+ " an image to " + mimeType));
		}
		if(image instanceof RenderedImage)
		{
			// Try to encode the original image.
			try
			{
				return imageToStandardBytesImpl((RenderedImage)image, mimeType);
			}
			catch(IOException ioe)
			{
				originalIOE = ioe;
			}
		}
		// Retry with a BufferedImage.
		int width = 0;
		int height = 0;
		boolean tki = false;
		
		Class<?> tst = image.getClass();
		try
		{
			while(!tst.equals(Object.class) && !tki)
			{
				if(image.getClass().getName().equals("sun.awt.image.ToolkitImage"))
				{
					Method m = image.getClass().getMethod("getImageRep", new Class<?>[0]);
					Object ir = m.invoke(image, new Object[0]);
					m = ir.getClass().getMethod("reconstruct", new Class<?>[]{int.class});
					m.invoke(ir, new Object[]{"ImageObserver.ALLBITS"});
					m = ir.getClass().getMethod("getWidth", new Class<?>[0]);
					width = (Integer)m.invoke(ir, new Object[0]);
					m = ir.getClass().getMethod("getHeight", new Class<?>[0]);
					height = (Integer)m.invoke(ir, new Object[0]);
					tki = true;
				}
				tst = tst.getSuperclass();
			}
		}
		catch(Exception e)
		{
		}
		
		if(!tki)
		{
			width = image.getWidth(null);
			height = image.getHeight(null);
		}
		
//		if(image instanceof ToolkitImage)
//		{
//			ImageRepresentation ir = ((ToolkitImage)image).getImageRep();
//			ir.reconstruct(ImageObserver.ALLBITS);
//			width = ir.getWidth();
//			height = ir.getHeight();
//		}
//		else
//		{
//			width = image.getWidth(null);
//			height = image.getHeight(null);
//		}
		ColorModel model = ColorModel.getRGBdefault();
		WritableRaster raster = model.createCompatibleWritableRaster(width, height);
		BufferedImage bufferedImage = new BufferedImage(model, raster,
			model.isAlphaPremultiplied(), null);
		Graphics g = bufferedImage.getGraphics();
		try
		{
			g.drawImage(image, 0, 0, width, height, null);
		}
		finally
		{
			g.dispose();
		}
		try
		{
			return imageToStandardBytesImpl(bufferedImage, mimeType);
		}
		catch(IOException ioe)
		{
			if(originalIOE != null)
			{
				throw new RuntimeException(originalIOE);
			}
			else
			{
				throw new RuntimeException(ioe);
			}
		}
	}

	/**
	 *  Convert image to bytes.
	 *  
	 *  Method is a direct copy from protected method
	 *  sun.awt.datatransfer.DataTransferer
	 */
	protected static byte[] imageToStandardBytesImpl(RenderedImage renderedImage, String mimeType) throws IOException
	{
		Iterator writerIterator = ImageIO.getImageWritersByMIMEType(mimeType);
		ImageTypeSpecifier typeSpecifier = new ImageTypeSpecifier(renderedImage);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOException ioe = null;
		while(writerIterator.hasNext())
		{
			ImageWriter imageWriter = (ImageWriter)writerIterator.next();
			ImageWriterSpi writerSpi = imageWriter.getOriginatingProvider();
			if(!writerSpi.canEncodeImage(typeSpecifier))
			{
				continue;
			}
			try
			{
				ImageOutputStream imageOutputStream = ImageIO
						.createImageOutputStream(baos);
				try
				{
					imageWriter.setOutput(imageOutputStream);
					imageWriter.write(renderedImage);
					imageOutputStream.flush();
				}
				finally
				{
					imageOutputStream.close();
				}
			}
			catch(IOException e)
			{
				imageWriter.dispose();
				baos.reset();
				ioe = e;
				continue;
			}
			imageWriter.dispose();
			baos.close();
			return baos.toByteArray();
		}
		baos.close();
		if(ioe == null)
		{
			ioe = new IOException("Registered service providers failed to encode "
				+ renderedImage + " to " + mimeType);
		}
		throw ioe;
	}
}
