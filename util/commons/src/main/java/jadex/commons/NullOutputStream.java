/**
 * 
 */
package jadex.commons;

import java.io.IOException;
import java.io.OutputStream;

/**
 *  An output stream that writes to dev null.
 */
public class NullOutputStream extends OutputStream
{
	/**
	 *  Write to dev null.
	 */
	public void write(int b) throws IOException
	{
	}
	
	/**
	 *  Write to dev null.
	 */
	public void write(byte[] b, int off, int len) throws IOException
	{
	}
	
	/**
	 *  Write to dev null.
	 */
	public void write(byte[] b) throws IOException
	{
	}
}
