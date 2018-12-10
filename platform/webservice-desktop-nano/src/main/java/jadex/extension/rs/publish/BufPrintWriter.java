package jadex.extension.rs.publish;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 *  PrintWriter that writes to a string buffer.
 */
public class BufPrintWriter extends PrintWriter
{
	protected StringBuffer buf;
	
	public BufPrintWriter(StringBuffer buf)
	{
		super(new Writer()
		{
			@Override
			public void write(char[] cbuf, int off, int len) throws IOException
			{
			}
			
			@Override
			public void flush() throws IOException
			{
			}
			
			@Override
			public void close() throws IOException
			{
			}
		});
		
		this.buf = buf;
	}
	
	@Override
	public void write(String s, int off, int len)
	{
		buf.append(s.substring(off, off+len));
	}
	
	@Override
	public void write(char[] buf, int off, int len)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void write(int c)
	{
		buf.append(c);
	}
	
	@Override
	public void println()
	{
		buf.append("\n");
	}
	
	@Override
	public void write(char[] buf)
	{
		this.buf.append(buf);
	}
	
	@Override
	public void write(String s)
	{
		buf.append(s);
	}
	
	@Override
	public void print(char[] s)
	{
		buf.append(s);
	}
	
	@Override
	public void print(Object obj)
	{
		buf.append(obj);
	}
	
	@Override
	public void print(String s)
	{
		buf.append(s);
	}
	
	@Override
	public void println(boolean x)
	{
		buf.append(x);
	}
	
	@Override
	public void println(char[] x)
	{
		buf.append(x);
	}
	
	@Override
	public void println(char x)
	{
		buf.append(x);
	}
	
	@Override
	public void print(boolean b)
	{
		buf.append(b);
	}
	
	@Override
	public void print(long l)
	{
		buf.append(l);
	}
	
	@Override
	public void print(int i)
	{
		buf.append(i);
	}
	
	@Override
	public void print(float f)
	{
		buf.append(f);
	}
	
	@Override
	public void print(double d)
	{
		buf.append(d);
	}
	
	@Override
	public void print(char c)
	{
		buf.append(c);
	}
	
	@Override
	public void println(double x)
	{
		buf.append(x);
	}
	
	@Override
	public void println(float x)
	{
		buf.append(x);
	}
	
	@Override
	public void println(int x)
	{
		buf.append(x);
	}
	
	@Override
	public void println(long x)
	{
		buf.append(x);
	}
	
	@Override
	public void println(Object x)
	{
		buf.append(x);
	}
	
	@Override
	public void println(String x)
	{
		buf.append(x);
	}
	
	@Override
	public void close()
	{
	}
	
	@Override
	public void flush()
	{
	}
	
}
