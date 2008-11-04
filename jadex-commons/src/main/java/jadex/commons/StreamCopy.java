package jadex.commons;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *  A runnable to concurrently copy data from one stream to the other.
 */
public class StreamCopy	implements Runnable
{
	//-------- attributes --------

	/** The source stream. */
	protected InputStream	source;

	/** The target stream. */
	protected OutputStream	target;

	//-------- constructors --------

	/**
	 *  Create a stream copy object.
	 */
	public StreamCopy(InputStream source, OutputStream target)
	{
		this.source	= source;
		this.target	= target;
	}

	//-------- Runnable interface --------

	/**
	 *  Copy data from source to target.
	 */
	public void	run()
	{
		try
		{
			int	cnt;
			byte[]	buf	= new byte[4096];
			while((cnt=source.read(buf))!=-1)
			{
				target.write(buf, 0, cnt);
			}
		}
		catch(IOException e)
		{
			StringWriter	sw	= new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			throw new RuntimeException(sw.toString());
		}
	}
}
