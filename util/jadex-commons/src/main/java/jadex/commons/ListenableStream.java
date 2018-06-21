package jadex.commons;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *  A stream that can be listened to.
 */
public class ListenableStream extends OutputStream
{
	//-------- attributes --------
	
	/** The super output stream. */
	protected OutputStream out;
	
	/** The event type to be generated. */
	protected String type;
	
	/** The buffered bytes. */
	protected byte[] buffer;
	
	/** The currently used length of the buffer. */
	protected int	len;
	
	/** The listeners. */
	protected List	listeners;
	
	/** The code(s) of the newline character. */
	protected byte[]	newline;
	
	//-------- constructors --------
	
	/**
	 *  Create a new stream.
	 *  @param out The output stream.
	 */
	public ListenableStream(OutputStream out, String type)
	{
		this.out = out;
		this.type	= type;
		this.listeners	= new ArrayList();
		this.newline	= System.getProperty("line.separator").getBytes();
		this.buffer	= new byte[1024];
	}
	
	//-------- OutputStream methods --------
	
	/**
	 *  Write a byte to the stream.
	 *  @param b The byte.
	 */
	public synchronized void write(int b) throws IOException
	{
		out.write(b);
		
		if(len==buffer.length)
		{
			byte[]	tmp	= new byte[buffer.length+1024];
			System.arraycopy(buffer, 0, tmp, 0, len);
			buffer	= tmp;
		}	
		buffer[len++]	= (byte)b;
		
		// Check for newline sequence at end of buffer.
		boolean	event	= len>=newline.length;
		for(int i=0; event && i<newline.length; i++)
		{
			event	= newline[i]==buffer[len-newline.length+i];
		}
		if(event)
		{
			generateEvent();
		}
	}

    /**
     *  Close the streams.
     */
	public synchronized void close() throws IOException
	{
		out.close();
		if(len>0)
			generateEvent();
	}

	/**
	 *  Flush the streams.
	 */
	public synchronized void flush() throws IOException
	{
		out.flush();
		if(len>0)
			generateEvent();
	}
	
	//-------- event handling --------
	
	/**
	 *  Add a line listener.
	 */
	public void	addLineListener(IChangeListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 *  Remove a line listener.
	 */
	public void	removeLineListener(IChangeListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 *  Generate an event for a line
	 *  and flush the buffer.
	 */
	protected void generateEvent()
	{
		String	line	= new String(buffer, 0, len-newline.length);
		for(int i=0; i<listeners.size(); i++)
			((IChangeListener)listeners.get(i)).changeOccurred(new ChangeEvent(null, type, line));
		len	= 0;
		if(buffer.length>1024)
			buffer	= new byte[1024];
	}	
}