package jadex.tools.jcc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

/**
 *  A stream that prints in a styled document (of a text pane).
 */
public class StyledDocumentOutputStream extends PrintStream
{
	//-------- attributes --------
	
	/** The document. */
	protected StyledDocument doc;
	
	/** The style. */
	protected Style style;
	
	//-------- constructors --------

	/**
	 *  Create a new 
	 */
	public StyledDocumentOutputStream(StyledDocument doc, Style style)
	{
		super(new ByteArrayOutputStream()); // needs a stream
		this.doc = doc;
		this.style = style;
	}
	
	//-------- methods --------
	
	/**
	 *  Write a byte to the document.
	 *  @param b The byte.
	 */
    public void write(int b)
    {
    	try
		{
    		synchronized(doc)
    		{
    			doc.insertString(doc.getLength(), ""+(char)b, style);
    		}
    	}
		catch(BadLocationException e)
		{
			//e.printStackTrace();
		}
    }
	
    /**
	 *  Write a byte array to the document.
	 *  @param b The byte array.
	 */
	public void write(byte b[]) throws IOException 
	{
		String tmp = new String(b);
		try
		{
			synchronized(doc)
			{
				doc.insertString(doc.getLength(), tmp, style);
			}
		}
		catch(BadLocationException e)
		{
			//e.printStackTrace();
		}
	}

	/**
	 *  Write a byte to the document.
	 *  @param b The byte array.
	 *  @param off The offset.
	 *  @param len The length.
	 */
	public void write(byte b[], int off, int len)
	{
		String tmp = new String(b , off , len);
		try
		{
			synchronized(doc)
			{
				doc.insertString(doc.getLength(), tmp, style);
			}
		}
		catch(BadLocationException e)
		{
			//e.printStackTrace();
		}
	}
}
