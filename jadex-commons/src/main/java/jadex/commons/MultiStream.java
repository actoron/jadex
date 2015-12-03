package jadex.commons;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import jadex.commons.collection.SCollection;

/**
 *  Dispatch the write calls to a number of
 *  specified output streams.
 */
public class MultiStream extends OutputStream
{
	//-------- attributes --------
	
	/** The output streams. */
	protected OutputStream[] outs;
	
	/** The disabled streams. */
	protected Set disabled;
	
	//-------- constructors --------
	
	/**
	 *  Create a new multi stream.
	 *  @param outs The output streams.
	 */
	public MultiStream(OutputStream[] outs)
	{
		this.outs = outs.clone();
		this.disabled = SCollection.createHashSet();
	}
	
	/**
	 *  Write a byte to the streams.
	 *  @param b The byte.
	 */
	public void write(int b) throws IOException
	{
		for(int i=0; i<outs.length; i++)
		{
			if(!disabled.contains(outs[i]))
				outs[i].write(b);
		}
	}
	
	/**
	 *  Write a byte array to the streams.
	 *  @param b The byte.
	 *  @param off The start offset.
	 *  @param len The length.
	 */
    public void write(byte b[], int off, int len) throws IOException 
    {
    	for(int i=0; i<outs.length; i++)
    	{
    		if(!disabled.contains(outs[i]))
    			outs[i].write(b, off, len);
    	}
    }
    
    /**
     *  Close the streams.
     */
	public void close() throws IOException
	{
		for(int i=0; i<outs.length; i++)
			outs[i].close();
	}

	/**
	 *  Flush the streams.
	 */
	public void flush() throws IOException
	{
		for(int i=0; i<outs.length; i++)
		{
			if(!disabled.contains(outs[i]))
				outs[i].flush();
		}
	}

	/**
	 *  Get the output streams.
	 *  @return The output streams.
	 */
	public OutputStream[] getOutputStreams()
	{
		return outs;
	}

	/**
	 *  Set the output streams.
	 *  @param outs The output streams.
	 */
	public void setOutputStreams(OutputStream[] outs)
	{
		this.outs = outs.clone();
	}
	
	/**
	 *  Set the enabled state of a stream.
	 *  @param out The output stream.
	 *  @param enabled The enabled state.
	 */
	public void setEnabled(OutputStream out, boolean enabled)
	{
		if(enabled)
			disabled.remove(out);
		else
			disabled.add(out);
	}
}