package jadex.commons;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *  The proxy input stream is similar to filter input stream but
 *  overrides read(byte[]) to redirect it to the same input stream method.
 */
public abstract class ProxyInputStream extends FilterInputStream
{
	/**
	 *  Constructs a new ProxyInputStream.
	 *  @param proxy The input stream to which calls are delegated.
	 */
	public ProxyInputStream(InputStream proxy)
	{
		super(proxy);
	}

	/**
	 *  Invokes the delegate's <code>read(byte[])</code> method.
	 *  @param data The buffer to read the bytes into.
	 *  @return The number of bytes read or -1 if the end of stream.
	 *  @throws IOException if an I/O error occurs.
	 */
	public int read(byte[] data) throws IOException
	{
		return in.read(data);
	}
}