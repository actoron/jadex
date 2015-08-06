package jadex.xml.writer;

import jadex.xml.SXML;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * 
 */
public abstract class AWriter
{
	
	// -------- static part --------

	/**
	 * Constant for indicating if public fields should be written. The field has
	 * to be declared as public and its value will be used to determine if
	 * fields should be included.
	 */
	public static final String XML_INCLUDE_FIELDS = "XML_INCLUDE_FIELDS";

	/**
	 * Write the properties to an xml.
	 * 
	 * @param input
	 *            The input stream.
	 * @param classloader
	 *            The classloader.
	 * @param context
	 *            The context.
	 */
	public void write(IObjectWriterHandler handler, Object object, OutputStream out, ClassLoader classloader, final Object context) throws Exception {
		write(handler, object, SXML.DEFAULT_ENCODING, out, classloader, context);
	}

	/**
	 * Write the properties to an xml.
	 * 
	 * @param input
	 *            The input stream.
	 * @param classloader
	 *            The classloader.
	 * @param context
	 *            The context.
	 */
	public abstract void write(IObjectWriterHandler handler, Object object, String encoding, OutputStream out, ClassLoader classloader, final Object context)
			throws Exception;

	/**
	 * Convert to a string.
	 */
	public static String objectToXML(AWriter writer, Object val, ClassLoader classloader, IObjectWriterHandler handler)
	{
		try
		{
			return new String(objectToByteArray(writer, val, classloader, handler), "UTF-8");
		} 
		catch (UnsupportedEncodingException e)
		{
			System.err.println("Warning: no UTF-8 available");
			return new String(objectToByteArray(writer, val, classloader, handler));
		}
	}

	/**
	 * Convert to a string.
	 */
	public static String objectToXML(AWriter writer, Object val, ClassLoader classloader, Object context, IObjectWriterHandler handler)
	{
		try
		{
			return new String(objectToByteArray(writer, val, classloader, context, handler), "UTF-8");
		} 
		catch (UnsupportedEncodingException e)
		{
			System.err.println("Warning: no UTF-8 available");
			return new String(objectToByteArray(writer, val, classloader, context, handler));
		}
	}

	/**
	 * Convert to a byte array.
	 */
	public static byte[] objectToByteArray(AWriter writer, Object val, ClassLoader classloader, IObjectWriterHandler handler)
	{
		return objectToByteArray(writer, val, classloader, null, handler);
	}

	/**
	 * Convert to a byte array.
	 */
	public static byte[] objectToByteArray(AWriter writer, Object val, ClassLoader classloader, Object context, IObjectWriterHandler handler)
	{
		return objectToByteArray(writer, val, SXML.DEFAULT_ENCODING, classloader, context, handler);
	}
	
	/**
	 * Convert to a byte array.
	 */
	public static byte[] objectToByteArray(AWriter writer, Object val, String encoding, ClassLoader classloader, Object context, IObjectWriterHandler handler)
	{
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			writer.write(handler, val, encoding, bos, classloader, context);
			byte[] ret = bos.toByteArray();
			bos.close();
			return ret;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			// System.out.println("Exception writing: "+val);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Write to output stream.
	 */
	public static void objectToOutputStream(AWriter writer, Object val, OutputStream os, ClassLoader classloader, Object context, IObjectWriterHandler handler)
	{
		try
		{
			writer.write(handler, val, os, classloader, context);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			// System.out.println("Exception writing: "+val);
			throw new RuntimeException(e);
		}
	}

}