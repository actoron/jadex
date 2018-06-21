package jadex.commons;

import java.io.IOException;
import java.io.InputStream;

/**
 *  Protects the underlying input stream from being closed.
 */
public class ProtectedInputStream extends ProxyInputStream
{
	/**
	 *  Create a protected input stream.
	 *  @param in The input stream.
	 */
    public ProtectedInputStream(InputStream in) 
    {
        super(in);
    }

    /**
     *  Close the stream. Replaces the underlying
     *  stream with an always closed version.
     */
    public void close() 
    {
        in = new InputStream()
		{
			public int read() throws IOException
			{
				return -1;
			}
		};
    }
}
